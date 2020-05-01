package pt.ist.meic.phylodb.typing.profile;

import javafx.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.io.formatters.dataset.profile.ProfilesFormatter;
import pt.ist.meic.phylodb.typing.dataset.DatasetRepository;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.SchemaRepository;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.db.BatchRepository;
import pt.ist.meic.phylodb.utils.db.EntityRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {

	private DatasetRepository datasetRepository;
	private ProfileRepository profileRepository;
	private SchemaRepository schemaRepository;

	public ProfileService(DatasetRepository datasetRepository, ProfileRepository profileRepository, SchemaRepository schemaRepository) {
		this.datasetRepository = datasetRepository;
		this.profileRepository = profileRepository;
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
	public boolean saveProfile(Profile profile) {
		Dataset.PrimaryKey datasetKey = new Dataset.PrimaryKey(profile.getPrimaryKey().getProjectId(), profile.getPrimaryKey().getDatasetId());
		Optional<Schema> optional = schemaRepository.find(datasetKey);
		if (!datasetRepository.exists(datasetKey) || !optional.isPresent() || optional.get().getLociIds().size() != profile.getAllelesReferences().size())
			return false;
		return profileRepository.save(profile).isPresent();
	}

	@Transactional
	public boolean deleteProfile(UUID projectId, UUID datasetId, String profileId) {
		return profileRepository.remove(new Profile.PrimaryKey(projectId, datasetId, profileId));
	}

	@Transactional
	public boolean saveProfilesOnConflictSkip(UUID projectId, UUID datasetId, MultipartFile file) throws IOException {
		return saveAll(projectId, datasetId, BatchRepository.SKIP, file);
	}

	@Transactional
	public boolean saveProfilesOnConflictUpdate(UUID projectId, UUID datasetId, MultipartFile file) throws IOException {
		return saveAll(projectId, datasetId, BatchRepository.UPDATE, file);
	}

	private boolean saveAll(UUID projectId, UUID datasetId, String conflict, MultipartFile file) throws IOException {
		Optional<Schema> optional = datasetRepository.find(new Dataset.PrimaryKey(projectId, datasetId), EntityRepository.CURRENT_VERSION_VALUE)
				.flatMap(d -> schemaRepository.find(d.getSchema().getPrimaryKey(), d.getSchema().getVersion()));
		if (!optional.isPresent())
			return false;
		Schema schema = optional.get();
		List<Profile> profiles = ProfilesFormatter.get(schema.getType().getName())
				.parse(file, projectId, datasetId, schema);
		return profileRepository.saveAll(profiles, conflict, datasetId.toString()).isPresent();
	}

}
