package pt.ist.meic.phylodb.typing.profile;

import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.ServiceTestsContext;
import pt.ist.meic.phylodb.formatters.FormatterTests;
import pt.ist.meic.phylodb.formatters.ProfilesFormatterTests;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class ProfileServiceTests extends ServiceTestsContext {

	private static final int LIMIT = 2;
	private static final String TAXONID = TAXON1.getPrimaryKey(), locusId1 = LOCUS1.getPrimaryKey().getId(), locusId2 = LOCUS2.getPrimaryKey().getId();
	private static final String PROJECTID = PROJECT1.getPrimaryKey(), datasetId = DATASET1.getPrimaryKey().getId();
	private static final Allele ALLELE21P = new Allele(TAXONID, locusId2, "1", 1, false, null, PROJECTID);
	private static final Profile PROFILE3 = new Profile(PROJECTID, datasetId, "1", 1, false, null,
			Arrays.asList(new VersionedEntity<>(ALLELE11.getPrimaryKey(), ALLELE11.getVersion(), ALLELE11.isDeprecated()), new VersionedEntity<>(ALLELE21.getPrimaryKey(), ALLELE21.getVersion(), ALLELE21.isDeprecated())));
	private static final Profile PROFILE4 = new Profile(PROJECTID, datasetId, "2", 1, false, null,
			Arrays.asList(new VersionedEntity<>(ALLELE11P.getPrimaryKey(), ALLELE11P.getVersion(), ALLELE11P.isDeprecated()), new VersionedEntity<>(ALLELE21P.getPrimaryKey(), ALLELE21P.getVersion(), ALLELE21P.isDeprecated())));
	private static final Profile[] STATE = new Profile[]{PROFILE3, PROFILE4};

	private static Stream<Arguments> getProfilesEntities_params() {
		VersionedEntity<Profile.PrimaryKey> state0 = new VersionedEntity<>(STATE[0].getPrimaryKey(), STATE[0].getVersion(), STATE[0].isDeprecated()),
				state1 = new VersionedEntity<>(STATE[1].getPrimaryKey(), STATE[1].getVersion(), STATE[1].isDeprecated());
		List<VersionedEntity<Profile.PrimaryKey>> expected1 = new ArrayList<VersionedEntity<Profile.PrimaryKey>>() {{
			add(state0);
		}};
		List<VersionedEntity<Profile.PrimaryKey>> expected2 = new ArrayList<VersionedEntity<Profile.PrimaryKey>>() {{
			add(state0);
			add(state1);
		}};
		return Stream.of(Arguments.of(0, Collections.emptyList()),
				Arguments.of(0, expected1),
				Arguments.of(0, expected2),
				Arguments.of(-1, null));
	}

	private static Stream<Arguments> getProfiles_params() {
		List<Profile> expected1 = new ArrayList<Profile>() {{
			add(STATE[0]);
		}};
		List<Profile> expected2 = new ArrayList<Profile>() {{
			add(STATE[0]);
			add(STATE[1]);
		}};
		return Stream.of(Arguments.of(0, Collections.emptyList()),
				Arguments.of(0, expected1),
				Arguments.of(0, expected2),
				Arguments.of(-1, null));
	}

	private static Stream<Arguments> getProfile_params() {
		return Stream.of(Arguments.of(STATE[0].getPrimaryKey(), 1, PROFILE3),
				Arguments.of(STATE[0].getPrimaryKey(), 1, null));
	}

	private static Stream<Arguments> saveProfile_params() {
		Profile profile1 = new Profile(PROJECTID, datasetId, STATE[0].getPrimaryKey().getId(), 1, false, null,
				Arrays.asList(new VersionedEntity<>(new Allele.PrimaryKey(null, null, ALLELE11.getPrimaryKey().getId()), ALLELE11.getVersion(), ALLELE11.isDeprecated()), new VersionedEntity<>(new Allele.PrimaryKey(null, null, ALLELE21.getPrimaryKey().getId()), ALLELE21.getVersion(), ALLELE21.isDeprecated())));
		Profile profile2 = new Profile(PROJECTID, datasetId, "new", 1, false, null, Arrays.asList(new VersionedEntity<>(new Allele.PrimaryKey(null, null, ALLELE11P.getPrimaryKey().getId()), ALLELE11P.getVersion(), ALLELE11P.isDeprecated()), null));
		Profile profile3 = new Profile(PROJECTID, datasetId, "new", 1, false, null, Collections.singletonList(new VersionedEntity<>(ALLELE11P.getPrimaryKey(), ALLELE11P.getVersion(), ALLELE11P.isDeprecated())));
		Profile profile4 = new Profile(PROJECTID, datasetId, "new", 1, false, null, Arrays.asList(null, null));
		return Stream.of(Arguments.of(profile1, true, true, false, true),
				Arguments.of(profile2, false, true, false, true),
				Arguments.of(profile2, false, true, true, false),
				Arguments.of(profile3, false, true, false, false),
				Arguments.of(profile4, true, true, false, false),
				Arguments.of(profile1, true, false, false, false),
				Arguments.of(null, false, true, false, false));
	}

	private static Stream<Arguments> deleteProfile_params() {
		return Stream.of(Arguments.of(STATE[0].getPrimaryKey(), true),
				Arguments.of(STATE[0].getPrimaryKey(), false));
	}

	private static Stream<Arguments> saveAll_params() throws IOException {
		String[] headers = new String[]{"uvrA", "gyrB", "ftsY", "tuf", "gap"};
		Schema schema = new Schema("taxon", "id", Method.MLST, "description", headers);
		Dataset dataset = new Dataset(PROJECTID, datasetId, null, schema.getPrimaryKey().getTaxonId(), schema.getPrimaryKey().getId());
		String[][] alleles1 = {{"1", "1", "1", "1", "1"}};
		String[][] alleles2 = {{"1", "1", "1", "1", " "}, {" ", "1", " ", "3", "3"}};
		String[][] alleles3 = {{"1", "1", "1", "1", " "}};
		MultipartFile fileNone = FormatterTests.createFile("ml", "ml-d-0.txt"), file1 = FormatterTests.createFile("ml", "ml-h-d-1.txt"),
				fileN = FormatterTests.createFile("ml", "ml-h-d-2-m.txt"), fileNBad = FormatterTests.createFile("ml", "ml-h-d-2-d.txt");
		List<Profile> profiles1 = Arrays.asList(ProfilesFormatterTests.profiles(PROJECTID, datasetId, schema, alleles1, false));
		List<Profile> profilesN = Arrays.asList(ProfilesFormatterTests.profiles(PROJECTID, datasetId, schema, alleles2, false));
		List<Profile> profilesNBad = Arrays.asList(ProfilesFormatterTests.profiles(PROJECTID, datasetId, schema, alleles3, false));
		boolean[] existsAll1 = new boolean[]{true}, notExists1 = new boolean[]{false},
				notExistsN = new boolean[]{false, false}, existsAllN = new boolean[]{true, true},
				existsSomeN = new boolean[]{true, false};
		boolean[] missing1 = new boolean[]{true}, missingN = new boolean[]{true, false},
				notMissing1 = new boolean[]{false}, notMissingN = new boolean[]{false, false};
		return Stream.of(Arguments.of(false, fileNone, dataset, schema, Collections.emptyList(), new boolean[0], new boolean[0], false, null),
				Arguments.of(false, file1, dataset, schema, profiles1, notExists1, notMissing1, true, new Pair<>(new Integer[] {1}, new String[0])),
				Arguments.of(false, fileN, dataset, schema, profilesN, notExistsN, notMissingN, true, new Pair<>(new Integer[] {1}, new String[0])),
				Arguments.of(false, file1, dataset, schema, profiles1, notExists1, missing1, false, null),
				Arguments.of(false, fileN, dataset, schema, profilesN, notExistsN, missingN, true, new Pair<>(new Integer[] {1}, new String[] {"1"})),
				Arguments.of(false, file1, dataset, schema, profiles1, existsAll1, notMissing1, false, null),
				Arguments.of(false, fileN, dataset, schema, profilesN, existsAllN, notMissingN, false, null),
				Arguments.of(false, file1, dataset, schema, profiles1, existsAll1, missing1, false, null),
				Arguments.of(false, fileN, dataset, schema, profilesN, existsAllN, missingN, false, null),
				Arguments.of(false, fileN, dataset, schema, profilesN, existsSomeN, notMissingN, true, new Pair<>(new Integer[] {1}, new String[] {"1"})),
				Arguments.of(false, fileN, dataset, schema, profilesN, existsSomeN, missingN, true, new Pair<>(new Integer[] {1}, new String[] {"1"})),
				Arguments.of(false, fileNBad, dataset, schema, profilesNBad, notExists1, notMissing1, true, new Pair<>(new Integer[] {1, 3}, new String[0])),
				Arguments.of(true, file1, dataset, schema, profiles1, notExists1, notMissing1, true, new Pair<>(new Integer[] {1}, new String[0])),
				Arguments.of(true, fileN, dataset, schema, profilesN, notExistsN, notMissingN, true, new Pair<>(new Integer[] {1}, new String[0])),
				Arguments.of(true, file1, dataset, schema, profiles1, notExists1, missing1, false, null),
				Arguments.of(true, fileN, dataset, schema, profilesN, notExistsN, missingN, true, new Pair<>(new Integer[] {1}, new String[] {"1"})),
				Arguments.of(true, file1, dataset, schema, profiles1, existsAll1, notMissing1, true, new Pair<>(new Integer[] {1}, new String[0])),
				Arguments.of(true, fileN, dataset, schema, profilesN, existsAllN, notMissingN, true, new Pair<>(new Integer[] {1}, new String[0])),
				Arguments.of(true, file1, dataset, schema, profiles1, existsAll1, missing1, false, null),
				Arguments.of(true, fileN, dataset, schema, profilesN, existsAllN, missingN, true, new Pair<>(new Integer[] {1}, new String[] {"1"})),
				Arguments.of(true, fileN, dataset, schema, profilesN, existsSomeN, notMissingN, true, new Pair<>(new Integer[] {1}, new String[0])),
				Arguments.of(true, fileN, dataset, schema, profilesN, existsSomeN, missingN, true, new Pair<>(new Integer[] {1}, new String[] {"1"})),
				Arguments.of(true, fileNBad, dataset, schema, profilesNBad, notExists1, notMissing1, true, new Pair<>(new Integer[] {1, 3}, new String[0])),
				Arguments.of(false, fileN, dataset, schema, profilesN, existsSomeN, missingN, false, null));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@ParameterizedTest
	@MethodSource("getProfilesEntities_params")
	public void getProfilesEntities(int page, List<VersionedEntity<Profile.PrimaryKey>> expected) {
		Schema schema = new Schema(TAXONID, "schema", Method.MLVA, null, new String[]{locusId1, locusId2});
		Mockito.when(profileRepository.findAllEntities(anyInt(), anyInt(), any(), any())).thenReturn(Optional.ofNullable(expected));
		Mockito.when(schemaRepository.find(any())).thenReturn(Optional.of(schema));
		Optional<List<VersionedEntity<Profile.PrimaryKey>>> result = profileService.getProfilesEntities(PROJECTID, datasetId, page, LIMIT);
		if (expected == null && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertNotNull(expected);
		assertTrue(result.isPresent());
		List<VersionedEntity<Profile.PrimaryKey>> profiles = result.get();
		assertEquals(expected, profiles);
	}

	@ParameterizedTest
	@MethodSource("getProfiles_params")
	public void getProfiles(int page, List<Profile> expected) {
		Schema schema = new Schema(TAXONID, "schema", Method.MLVA, null, new String[]{locusId1, locusId2});
		Mockito.when(profileRepository.findAll(anyInt(), anyInt(), any(), any())).thenReturn(Optional.ofNullable(expected));
		Mockito.when(schemaRepository.find(any())).thenReturn(Optional.of(schema));
		Optional<Pair<Schema, List<Profile>>> result = profileService.getProfiles(PROJECTID, datasetId, page, LIMIT);
		if (expected == null && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertNotNull(expected);
		assertTrue(result.isPresent());
		List<Profile> profiles = result.get().getValue();
		assertEquals(expected.size(), profiles.size());
		assertEquals(schema, result.get().getKey());
		assertEquals(expected, profiles);
	}

	@ParameterizedTest
	@MethodSource("getProfile_params")
	public void getProfile(Profile.PrimaryKey key, long version, Profile expected) {
		Mockito.when(profileRepository.find(any(), anyLong())).thenReturn(Optional.ofNullable(expected));
		Optional<Profile> result = profileService.getProfile(key.getProjectId(), key.getDatasetId(), key.getId(), version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("saveProfile_params")
	public void saveProfile(Profile profile, boolean authorized, boolean dataset, boolean anyMissing, boolean expected) {
		Schema schema = new Schema(TAXONID, "schema", Method.MLVA, null, new String[]{locusId1, locusId2});
		Mockito.when(datasetRepository.exists(any())).thenReturn(dataset);
		Mockito.when(schemaRepository.find(any())).thenReturn(Optional.of(schema));
		Mockito.when(alleleRepository.anyMissing(any())).thenReturn(anyMissing);
		if (profile != null)
			Mockito.when(profileRepository.save(profile.updateReferences(schema, "", authorized))).thenReturn(expected);
		boolean result = profileService.saveProfile(profile, authorized);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("deleteProfile_params")
	public void deleteProfile(Profile.PrimaryKey key, boolean expected) {
		Mockito.when(profileRepository.remove(any())).thenReturn(expected);
		boolean result = profileService.deleteProfile(key.getProjectId(), key.getDatasetId(), key.getId());
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("saveAll_params")
	public void saveProfiles(boolean flag, MultipartFile file, Dataset dataset, Schema schema, List<Profile> profiles, boolean[] exists, boolean[] missing, boolean expected, Pair<Integer[], String[]> invalids) throws IOException {
		Mockito.when(datasetRepository.find(any(), anyLong())).thenReturn(Optional.ofNullable(dataset));
		Mockito.when(schemaRepository.find(any(), anyLong())).thenReturn(Optional.ofNullable(schema));
		List<Profile> updated = profiles.stream().map(p -> p.getAllelesReferences().stream().noneMatch(a -> a.getPrimaryKey().getId().matches(" ")) ? p :
				new Profile(p.getPrimaryKey().getProjectId(), p.getPrimaryKey().getDatasetId(), p.getPrimaryKey().getId(), p.getVersion(), p.isDeprecated(), p.getAka(), p.getAllelesReferences()
						.stream()
						.map(a -> a.getPrimaryKey().getId().matches(" ") ? null : a)
						.collect(Collectors.toList()))
		).collect(Collectors.toList());
		List<Profile> toSave = new ArrayList<>();
		IntStream.range(0, profiles.size()).forEach(i -> {
			Mockito.when(profileRepository.exists(updated.get(i).getPrimaryKey())).thenReturn(exists[i]);
			Mockito.when(alleleRepository.anyMissing(updated.get(i).getAllelesReferences())).thenReturn(missing[i]);
			if (!flag && !exists[i] && !missing[i])
				toSave.add(updated.get(i));
			else if (flag && !missing[i])
				toSave.add(updated.get(i));
		});
		Mockito.when(profileRepository.saveAll(toSave)).thenReturn(expected);
		Optional<Pair<Integer[], String[]>> result;
		if (!flag)
			result = profileService.saveProfilesOnConflictSkip(PROJECTID, datasetId, false, file);
		else
			result = profileService.saveProfilesOnConflictUpdate(PROJECTID, datasetId, false, file);
		if(invalids != null) {
			assertTrue(result.isPresent());
			assertArrayEquals(invalids.getKey(), result.get().getKey());
			assertArrayEquals(invalids.getValue(), result.get().getValue());
		} else
			assertFalse(result.isPresent());
	}

}
