package pt.ist.meic.phylodb.typing.profile;

import javafx.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.io.formatters.dataset.profile.ProfilesFormatter;
import pt.ist.meic.phylodb.phylogeny.locus.LocusRepository;
import pt.ist.meic.phylodb.typing.dataset.DatasetRepository;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.SchemaRepository;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.db.BatchRepository;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.service.Reference;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {

	private DatasetRepository datasetRepository;
	private ProfileRepository profileRepository;
	private LocusRepository locusRepository;
	private SchemaRepository schemaRepository;

	public ProfileService(DatasetRepository datasetRepository, ProfileRepository profileRepository, LocusRepository locusRepository, SchemaRepository schemaRepository) {
		this.datasetRepository = datasetRepository;
		this.profileRepository = profileRepository;
		this.locusRepository = locusRepository;
		this.schemaRepository = schemaRepository;
	}

	@Transactional(readOnly = true)
	public Optional<Pair<Schema, List<Profile>>> getProfiles(UUID datasetId, Map<String, String> filters, int page, int limit) {
		return profileRepository.findAll(page, limit, datasetId, filters)
				.flatMap(p -> schemaRepository.find(datasetId).map(s -> new Pair<>(s, p)));
	}

	@Transactional(readOnly = true)
	public Optional<Profile> getProfile(UUID datasetId, String profileId, int version) {
		return profileRepository.find(new Profile.PrimaryKey(datasetId, profileId), version);
	}

	@Transactional
	public boolean saveProfile(Profile profile) {
		Optional<Schema> optional = schemaRepository.find(profile.getDatasetId());
		if (!datasetRepository.exists(profile.getDatasetId()) || !optional.isPresent())
			return false;
		Schema schema = optional.get();
		String[] lociIds = schema.getLociIds().stream()
				.map(Reference::getPrimaryKey)
				.toArray(String[]::new);
		if (locusRepository.anyMissing(schema.getPrimaryKey().getTaxonId(), lociIds) || lociIds.length != profile.getAllelesReferences().size())
			return false;
		return profileRepository.save(profile);
	}

	@Transactional
	public boolean deleteProfile(UUID datasetId, String profileId) {
		return profileRepository.remove(new Profile.PrimaryKey(datasetId, profileId));
	}

	@Transactional
	public boolean saveProfilesOnConflictSkip(UUID datasetId, MultipartFile file) throws IOException {
		return saveAll(datasetId, BatchRepository.SKIP, file);
	}

	@Transactional
	public boolean saveProfilesOnConflictUpdate(UUID datasetId, MultipartFile file) throws IOException {
		return saveAll(datasetId, BatchRepository.UPDATE, file);
	}

	private boolean saveAll(UUID datasetId, String conflict, MultipartFile file) throws IOException {
		Optional<Schema> optional = datasetRepository.find(datasetId, EntityRepository.CURRENT_VERSION_VALUE)
				.flatMap(d -> schemaRepository.find(d.getSchema().getPrimaryKey(), d.getSchema().getVersion()));
		if (!optional.isPresent())
			return false;
		Schema schema = optional.get();
		List<Profile> profiles = ProfilesFormatter.get(schema.getType())
				.parse(file, datasetId, schema);
		return profileRepository.saveAll(profiles, conflict, datasetId.toString());
	}

}
