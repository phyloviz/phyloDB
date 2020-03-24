package pt.ist.meic.phylodb.typing.profile;

import javafx.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.error.exception.FileFormatException;
import pt.ist.meic.phylodb.formatters.dataset.FileDataset;
import pt.ist.meic.phylodb.formatters.dataset.SchemedFileDataset;
import pt.ist.meic.phylodb.formatters.dataset.profile.ProfilesFormatter;
import pt.ist.meic.phylodb.typing.dataset.DatasetRepository;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.SchemaRepository;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.service.SchemaValidator;
import pt.ist.meic.phylodb.utils.service.StatusResult;

import java.util.*;

import static pt.ist.meic.phylodb.utils.db.Status.UNCHANGED;
import static pt.ist.meic.phylodb.utils.db.Status.UPDATED;

@Service
public class ProfileService {

	private DatasetRepository datasetRepository;
	private ProfileRepository profileRepository;

	private Map<String, SchemaValidator<String, String, FileDataset<Profile>>> schemaExistanceValidator = new HashMap<>();
	
	public ProfileService(DatasetRepository datasetRepository, ProfileRepository profileRepository, SchemaRepository schemaRepository) {
		this.datasetRepository = datasetRepository;
		this.profileRepository = profileRepository;
		this.schemaExistanceValidator.put(Schema.SNP, (t, s, f) -> schemaRepository.find(new Schema.PrimaryKey(t, s)) != null);
		this.schemaExistanceValidator.put(Schema.MLVA, (t, s, f) -> schemaRepository.find(t, ((SchemedFileDataset) f).getLociIds()) != null);
		this.schemaExistanceValidator.put(Schema.MLST, (t, s, f) -> schemaRepository.find(t, ((SchemedFileDataset) f).getLociIds()) != null);
	}

	@Transactional(readOnly = true)
	public Optional<Pair<Schema, List<Profile>>> getProfiles(String datasetId, Map<String, String> filters, int page, int limit) {
		List<Profile> profiles = profileRepository.findAll(page, limit, datasetId, filters);
		Schema schema = datasetRepository.getSchema(datasetId);
		return Optional.of(new Pair<>(schema, profiles));
	}

	@Transactional(readOnly = true)
	public Optional<Profile> getProfile(UUID datasetId, String profileId) {
		return Optional.ofNullable(profileRepository.find(new Profile.PrimaryKey(datasetId, profileId)));
	}

	@Transactional
	public StatusResult saveProfile(Profile profile) {
		if (datasetRepository.find(UUID.fromString(profile.getDatasetId())) == null ||
				datasetRepository.getSchema(profile.getDatasetId()).getLociIds().length != profile.getAllelesIds().length)
			return new StatusResult(UNCHANGED);
		return new StatusResult(profileRepository.save(profile));
	}

	@Transactional
	public StatusResult deleteProfile(UUID datasetId, String profileId) {
		if (!getProfile(datasetId, profileId).isPresent())
			return new StatusResult(UNCHANGED);
		return new StatusResult(profileRepository.remove(new Profile.PrimaryKey(datasetId, profileId)));
	}

	@Transactional
	public StatusResult saveProfilesOnConflictSkip(UUID datasetId, String method, String taxonId, String schemaId, MultipartFile file) throws FileFormatException {
		FileDataset<Profile> dataset = getProfileDataset(method, taxonId, schemaId, file);
		if(datasetRepository.find(datasetId) == null || dataset == null)
			return new StatusResult(UNCHANGED);
		profileRepository.saveAllOnConflictSkip(datasetId, dataset.getEntities());
		return new StatusResult(UPDATED);
	}

	@Transactional
	public StatusResult saveProfilesOnConflictUpdate(UUID datasetId, String method, String taxonId, String schemaId, MultipartFile file) throws FileFormatException {
		FileDataset<Profile> dataset = getProfileDataset(method, taxonId, schemaId, file);
		if(datasetRepository.find(datasetId) == null || dataset == null)
			return new StatusResult(UNCHANGED);
		profileRepository.saveAllOnConflictUpdate(datasetId, dataset.getEntities());
		return new StatusResult(UPDATED);
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
