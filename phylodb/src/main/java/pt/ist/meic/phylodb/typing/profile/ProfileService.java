package pt.ist.meic.phylodb.typing.profile;

import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.io.formatters.dataset.profile.ProfilesFormatter;
import pt.ist.meic.phylodb.phylogeny.allele.AlleleRepository;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.typing.dataset.DatasetRepository;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.SchemaRepository;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

@Service
public class ProfileService {

	@Value("${application.missing}")
	private String missing;
	@Value("${application.repository.batch}")
	private String batch;

	private DatasetRepository datasetRepository;
	private ProfileRepository profileRepository;
	private AlleleRepository alleleRepository;
	private SchemaRepository schemaRepository;

	public ProfileService(DatasetRepository datasetRepository, ProfileRepository profileRepository, AlleleRepository alleleRepository, SchemaRepository schemaRepository) {
		this.datasetRepository = datasetRepository;
		this.profileRepository = profileRepository;
		this.alleleRepository = alleleRepository;
		this.schemaRepository = schemaRepository;
	}

	@Transactional(readOnly = true)
	public Optional<Pair<Schema, List<Profile>>> getProfiles(UUID projectId, UUID datasetId, int page, int limit) {
		return profileRepository.findAll(page, limit, projectId, datasetId)
				.flatMap(p -> schemaRepository.find(new Dataset.PrimaryKey(projectId, datasetId)).map(s -> new Pair<>(s, p)));
	}

	@Transactional(readOnly = true)
	public Optional<Profile> getProfile(UUID projectId, UUID datasetId, String profileId, Long version) {
		return profileRepository.find(new Profile.PrimaryKey(projectId, datasetId, profileId), version);
	}

	@Transactional
	public boolean saveProfile(Profile profile, boolean authorized) {
		if (profile == null)
			return false;
		Dataset.PrimaryKey datasetKey = new Dataset.PrimaryKey(profile.getPrimaryKey().getProjectId(), profile.getPrimaryKey().getDatasetId());
		Optional<Schema> optional = schemaRepository.find(datasetKey);
		if (!datasetRepository.exists(datasetKey) || !optional.isPresent())
			return false;
		Schema schema = optional.get();
		List<Entity<Locus.PrimaryKey>> loci = schema.getLociReferences();
		List<Entity<Allele.PrimaryKey>> alleles = profile.getAllelesReferences();
		if (loci.size() != alleles.size())
			return false;
		profile = profile.updateReferences(schema, missing, authorized);
		return verifyAlleles(profile.getAllelesReferences()) && profileRepository.save(profile);
	}

	@Transactional
	public boolean deleteProfile(UUID projectId, UUID datasetId, String profileId) {
		return profileRepository.remove(new Profile.PrimaryKey(projectId, datasetId, profileId));
	}

	@Transactional
	public Optional<Pair<Integer[], String[]>> saveProfilesOnConflictSkip(UUID projectId, UUID datasetId, boolean authorized, MultipartFile file) throws IOException {
		return saveAll(projectId, datasetId, authorized, false, file);
	}

	@Transactional
	public Optional<Pair<Integer[], String[]>> saveProfilesOnConflictUpdate(UUID projectId, UUID datasetId, boolean authorized, MultipartFile file) throws IOException {
		return saveAll(projectId, datasetId, authorized, true, file);
	}

	private Optional<Pair<Integer[], String[]>> saveAll(UUID projectId, UUID datasetId, boolean authorized, boolean conflict, MultipartFile file) throws IOException {
		Optional<Schema> optional = datasetRepository.find(new Dataset.PrimaryKey(projectId, datasetId), EntityRepository.CURRENT_VERSION_VALUE)
				.flatMap(d -> schemaRepository.find(d.getSchema().getPrimaryKey(), d.getSchema().getVersion()));
		if (!optional.isPresent())
			return Optional.empty();
		Schema schema = optional.get();
		Predicate<Profile> canSave = conflict ? p -> true : p -> !profileRepository.exists(p.getPrimaryKey());
		Pair<List<Profile>, List<Integer>> parsed = ProfilesFormatter.get(schema.getType().getName()).parse(file, projectId, datasetId, schema, missing, authorized);
		List<String> invalids = new ArrayList<>();
		List<Profile> profiles = parsed.getKey(), toSave = new ArrayList<>();
		for (Profile profile : profiles) {
			if(canSave.test(profile) && verifyAlleles(profile.getAllelesReferences())) {
				toSave.add(profile);
				continue;
			}
			invalids.add(profile.getPrimaryKey().getId());
		}
		return profileRepository.saveAll(toSave, Integer.parseInt(batch), projectId.toString(), datasetId.toString()) ?
				Optional.of(new Pair<>(parsed.getValue().toArray(new Integer[0]), invalids.toArray(new String[0]))) :
				Optional.empty();
	}

	private boolean verifyAlleles(List<Entity<Allele.PrimaryKey>> references) {
		return !alleleRepository.anyMissing(references) && !references.stream().allMatch(Objects::isNull);
	}

}
