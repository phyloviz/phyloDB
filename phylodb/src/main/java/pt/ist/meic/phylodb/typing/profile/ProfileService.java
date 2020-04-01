package pt.ist.meic.phylodb.typing.profile;

import javafx.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.error.exception.FileFormatException;
import pt.ist.meic.phylodb.formatters.dataset.FileDataset;
import pt.ist.meic.phylodb.formatters.dataset.SchemedFileDataset;
import pt.ist.meic.phylodb.formatters.dataset.profile.ProfilesFormatter;
import pt.ist.meic.phylodb.phylogeny.locus.LocusRepository;
import pt.ist.meic.phylodb.typing.dataset.DatasetRepository;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.SchemaRepository;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.db.Status;
import pt.ist.meic.phylodb.utils.service.Reference;
import pt.ist.meic.phylodb.utils.service.SchemaValidator;

import java.util.*;

import static pt.ist.meic.phylodb.utils.db.Status.UNCHANGED;
import static pt.ist.meic.phylodb.utils.db.Status.UPDATED;

@Service
public class ProfileService {

	private DatasetRepository datasetRepository;
	private ProfileRepository profileRepository;
	private LocusRepository locusRepository;
	private SchemaRepository schemaRepository;

	private Map<String, SchemaValidator<String, String, FileDataset<Profile>>> schemaExistanceValidator = new HashMap<>();

	public ProfileService(DatasetRepository datasetRepository, ProfileRepository profileRepository, LocusRepository locusRepository, SchemaRepository schemaRepository) {
		this.datasetRepository = datasetRepository;
		this.profileRepository = profileRepository;
		this.locusRepository = locusRepository;
		this.schemaRepository = schemaRepository;
		this.schemaExistanceValidator.put(Schema.SNP, (t, s, f) -> schemaRepository.exists(new Schema.PrimaryKey(t, s)));
		this.schemaExistanceValidator.put(Schema.MLVA, (t, s, f) -> schemaRepository.find(t, ((SchemedFileDataset) f).getLociIds()) != null);
		this.schemaExistanceValidator.put(Schema.MLST, (t, s, f) -> schemaRepository.find(t, ((SchemedFileDataset) f).getLociIds()) != null);
	}

	@Transactional(readOnly = true)
	public Optional<Pair<Schema, List<Profile>>> getProfiles(UUID datasetId, Map<String, String> filters, int page, int limit) {
		List<Profile> profiles = profileRepository.findAll(page, limit, datasetId, filters);
		Schema schema = schemaRepository.find(datasetId);
		return Optional.of(new Pair<>(schema, profiles));
	}

	@Transactional(readOnly = true)
	public Optional<Profile> getProfile(UUID datasetId, String profileId, int version) {
		return Optional.ofNullable(profileRepository.find(new Profile.PrimaryKey(datasetId, profileId), version));
	}

	@Transactional
	public Status saveProfile(Profile profile) {
		if (!datasetRepository.exists(profile.getDatasetId())) return UNCHANGED;
		Schema schema = schemaRepository.find(profile.getDatasetId());
		String[] lociIds = schema.getLociIds().stream()
				.map(Reference::getId)
				.toArray(String[]::new);
		if (locusRepository.anyMissing(schema.getTaxonId(), lociIds) ||
				lociIds.length != profile.getAllelesIds().size())
			return UNCHANGED;
		return profileRepository.save(profile);
	}

	@Transactional
	public Status deleteProfile(UUID datasetId, String profileId) {
		if (!profileRepository.exists(new Profile.PrimaryKey(datasetId, profileId)))
			return UNCHANGED;
		return profileRepository.remove(new Profile.PrimaryKey(datasetId, profileId));
	}

	@Transactional
	public Status saveProfilesOnConflictSkip(UUID datasetId, String method, String taxonId, String schemaId, MultipartFile file) throws FileFormatException {
		FileDataset<Profile> dataset = getProfileDataset(method, taxonId, schemaId, file);
		if(!datasetRepository.exists(datasetId) || dataset == null)
			return UNCHANGED;
		profileRepository.saveAllOnConflictSkip(datasetId, dataset.getEntities());
		return UPDATED;
	}

	@Transactional
	public Status saveProfilesOnConflictUpdate(UUID datasetId, String method, String taxonId, String schemaId, MultipartFile file) throws FileFormatException {
		FileDataset<Profile> dataset = getProfileDataset(method, taxonId, schemaId, file);
		if(!datasetRepository.exists(datasetId) || dataset == null)
			return UNCHANGED;
		profileRepository.saveAllOnConflictUpdate(datasetId, dataset.getEntities());
		return UPDATED;
	}

	private FileDataset<Profile> getProfileDataset(String method, String taxonId, String schemaId, MultipartFile file) throws FileFormatException {
		if (!Schema.METHODS.contains(method))
			return null;
		FileDataset<Profile> dataset = ProfilesFormatter.get(method).read(file);
		if(!schemaExistanceValidator.get(method).test(taxonId, schemaId, dataset))
			return null;
		return dataset;
	}

}
