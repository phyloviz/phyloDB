package pt.ist.meic.phylodb.typing.profile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.io.formatters.dataset.profile.ProfilesFormatter;
import pt.ist.meic.phylodb.phylogeny.allele.AlleleRepository;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.typing.dataset.DatasetRepository;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.SchemaRepository;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.db.VersionedRepository;
import pt.ist.meic.phylodb.utils.service.BatchService;
import pt.ist.meic.phylodb.utils.service.Pair;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Class that contains operations to manage profiles
 * <p>
 * The service responsibility is to guarantee that the database state is not compromised and verify all business rules.
 */
@Service
public class ProfileService extends BatchService<Profile, Profile.PrimaryKey> {

	@Value("${application.missing}")
	private String missing;

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

	/**
	 * Operation to retrieve the resumed information of the requested profiles
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @param page      number of the page to retrieve
	 * @param limit     number of profiles to retrieve by page
	 * @return an {@link Optional} with a {@link List} of {@link VersionedEntity<Profile.PrimaryKey>}, which is the resumed information of each profile
	 */
	@Transactional(readOnly = true)
	public Optional<List<VersionedEntity<Profile.PrimaryKey>>> getProfilesEntities(String projectId, String datasetId, int page, int limit) {
		return getAllEntities(page, limit, projectId, datasetId);
	}

	/**
	 * Operation to retrieve the information of the requested profiles
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @param page      number of the page to retrieve
	 * @param limit     number of profiles to retrieve by page
	 * @return an {@link Optional} with a {@link List<Profile>} which is the information of each profile
	 */
	@Transactional(readOnly = true)
	public Optional<Pair<Schema, List<Profile>>> getProfiles(String projectId, String datasetId, int page, int limit) {
		return getAll(page, limit, projectId, datasetId)
				.flatMap(p -> schemaRepository.find(new Dataset.PrimaryKey(projectId, datasetId)).map(s -> new Pair<>(s, p)));
	}

	/**
	 * Operation to retrieve the requested profile
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @param profileId identifier of the {@link Profile profile}
	 * @param version   version of the profile
	 * @return an {@link Optional} of {@link Profile}, which is the requested profile
	 */
	@Transactional(readOnly = true)
	public Optional<Profile> getProfile(String projectId, String datasetId, String profileId, long version) {
		return get(new Profile.PrimaryKey(projectId, datasetId, profileId), version);
	}

	/**
	 * Operation to save a profile
	 *
	 * @param profile    profile to be saved
	 * @param authorized boolean which indicates if the alleles used are private or public
	 * @return {@code true} if the profile was saved
	 */
	@Transactional
	public boolean saveProfile(Profile profile, boolean authorized) {
		if (profile == null)
			return false;
		Dataset.PrimaryKey datasetKey = new Dataset.PrimaryKey(profile.getPrimaryKey().getProjectId(), profile.getPrimaryKey().getDatasetId());
		Optional<Schema> optional = schemaRepository.find(datasetKey);
		if (!datasetRepository.exists(datasetKey) || !optional.isPresent())
			return false;
		Schema schema = optional.get();
		List<VersionedEntity<Locus.PrimaryKey>> loci = schema.getLociReferences();
		List<VersionedEntity<Allele.PrimaryKey>> alleles = profile.getAllelesReferences();
		if (loci.size() != alleles.size())
			return false;
		profile = profile.updateReferences(schema, missing, authorized);
		return verifyAlleles(profile.getAllelesReferences()) && save(profile);
	}

	/**
	 * Operation to deprecate a profile
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @param profileId identifier of the {@link Profile profile}
	 * @return {@code true} if the profile was deprecated
	 */
	@Transactional
	public boolean deleteProfile(String projectId, String datasetId, String profileId) {
		return remove(new Profile.PrimaryKey(projectId, datasetId, profileId));
	}

	/**
	 * Operation to save several profiles if they don't exist
	 *
	 * @param projectId  identifier of the {@link Project project}
	 * @param datasetId  identifier of the {@link Dataset dataset}
	 * @param authorized boolean which indicates if the alleles used are private or public
	 * @param file       file with the profiles
	 * @return an {@link Optional} of {@link Pair} where the key is the list of line numbers that couldn't be parsed, and the value is list of profiles ids parsed that are not valid
	 * @throws IOException if there is an error parsing the file
	 */
	@Transactional
	public Optional<Pair<Integer[], String[]>> saveProfilesOnConflictSkip(String projectId, String datasetId, boolean authorized, MultipartFile file) throws IOException {
		return saveAll(projectId, datasetId, authorized, false, file);
	}

	/**
	 * Operation to save several profiles
	 *
	 * @param projectId  identifier of the {@link Project project}
	 * @param datasetId  identifier of the {@link Dataset dataset}
	 * @param authorized boolean which indicates if the alleles used are private or public
	 * @param file       file with the profiles
	 * @return an {@link Optional} of {@link Pair} where the key is the list of line numbers that couldn't be parsed, and the value is list of profiles ids parsed that are not valid
	 * @throws IOException if there is an error parsing the file
	 */
	@Transactional
	public Optional<Pair<Integer[], String[]>> saveProfilesOnConflictUpdate(String projectId, String datasetId, boolean authorized, MultipartFile file) throws IOException {
		return saveAll(projectId, datasetId, authorized, true, file);
	}

	private Optional<Pair<Integer[], String[]>> saveAll(String projectId, String datasetId, boolean authorized, boolean conflict, MultipartFile file) throws IOException {
		Optional<Schema> optional = datasetRepository.find(new Dataset.PrimaryKey(projectId, datasetId), VersionedRepository.CURRENT_VERSION_VALUE)
				.flatMap(d -> schemaRepository.find(d.getSchema().getPrimaryKey(), d.getSchema().getVersion()));
		if (!optional.isPresent())
			return Optional.empty();
		Schema schema = optional.get();
		Predicate<Profile> canSave = conflict ? p -> true : p -> !profileRepository.exists(p.getPrimaryKey());
		Pair<List<Profile>, List<Integer>> parsed = ProfilesFormatter.get(schema.getType().getName()).parse(file, projectId, datasetId, schema, missing, authorized);
		List<String> invalids = new ArrayList<>();
		List<Profile> profiles = parsed.getKey(), toSave = new ArrayList<>();
		for (Profile profile : profiles) {
			if (canSave.test(profile) && verifyAlleles(profile.getAllelesReferences())) {
				toSave.add(profile);
				continue;
			}
			invalids.add(profile.getPrimaryKey().getId());
		}
		return saveAll(toSave) ?
				Optional.of(new Pair<>(parsed.getValue().toArray(new Integer[0]), invalids.toArray(new String[0]))) :
				Optional.empty();
	}

	private boolean verifyAlleles(List<VersionedEntity<Allele.PrimaryKey>> references) {
		return !alleleRepository.anyMissing(references) && !references.stream().allMatch(Objects::isNull);
	}

	@Override
	protected Optional<List<Profile>> getAll(int page, int limit, Object... params) {
		return profileRepository.findAll(page, limit, params[0], params[1]);
	}

	@Override
	protected boolean saveAll(List<Profile> entities) {
		return profileRepository.saveAll(entities);
	}

	@Override
	protected Optional<List<VersionedEntity<Profile.PrimaryKey>>> getAllEntities(int page, int limit, Object... params) {
		return profileRepository.findAllEntities(page, limit, params[0], params[1]);
	}

	@Override
	protected Optional<Profile> get(Profile.PrimaryKey key, long version) {
		return profileRepository.find(key, version);
	}

	@Override
	protected boolean save(Profile entity) {
		return profileRepository.save(entity);
	}

	@Override
	protected boolean remove(Profile.PrimaryKey key) {
		return profileRepository.remove(key);
	}

}
