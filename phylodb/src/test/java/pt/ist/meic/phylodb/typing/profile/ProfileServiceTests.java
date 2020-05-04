package pt.ist.meic.phylodb.typing.profile;

import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.neo4j.ogm.model.QueryStatistics;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.Test;
import pt.ist.meic.phylodb.formatters.FormatterTests;
import pt.ist.meic.phylodb.formatters.ProfilesFormatterTests;
import pt.ist.meic.phylodb.phylogeny.allele.AlleleRepository;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.typing.dataset.DatasetRepository;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.SchemaRepository;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.MockResult;
import pt.ist.meic.phylodb.utils.db.BatchRepository;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class ProfileServiceTests extends Test {

	@MockBean
	private DatasetRepository datasetRepository;
	@MockBean
	private ProfileRepository profileRepository;
	@MockBean
	private AlleleRepository alleleRepository;
	@MockBean
	private SchemaRepository schemaRepository;

	@InjectMocks
	private ProfileService service;

	private static final int LIMIT = 2;
	private static final String taxonId = "t", locusId1 = "l", locusId2 = "2";
	private static final UUID projectId = UUID.randomUUID(), datasetId = UUID.randomUUID();
	private static final Allele first = new Allele(taxonId, locusId1, "1", 1, false, "description", null);
	private static final Allele second = new Allele(taxonId, locusId2, "2", 1, false, null, null);
	private static final Allele third = new Allele(taxonId, locusId1, "3", 1, false, "description", projectId);
	private static final Allele fourth = new Allele(taxonId, locusId2, "4", 1, false, null, projectId);
	private static final Profile profile1 = new Profile(projectId, datasetId, "1", 1, false, null,
			Arrays.asList(new Entity<>(first.getPrimaryKey(), first.getVersion(), first.isDeprecated()), new Entity<>(second.getPrimaryKey(), second.getVersion(), second.isDeprecated())));
	private static final Profile profile2 = new Profile(projectId, datasetId, "2", 1, false, null,
			Arrays.asList(new Entity<>(third.getPrimaryKey(), third.getVersion(), third.isDeprecated()), new Entity<>(fourth.getPrimaryKey(), fourth.getVersion(), fourth.isDeprecated())));
	private static final Profile[] state = new Profile[]{profile1, profile2};

	private static Stream<Arguments> getProfiles_params() {
		List<Profile> expected1 = new ArrayList<Profile>() {{ add(state[0]); }};
		List<Profile> expected2 = new ArrayList<Profile>() {{ add(state[0]); add(state[1]); }};
		return Stream.of(Arguments.of(0, Collections.emptyList()),
				Arguments.of(0, expected1),
				Arguments.of(0, expected2),
				Arguments.of(-1, null));
	}

	private static Stream<Arguments> getProfile_params() {
		return Stream.of(Arguments.of(state[0].getPrimaryKey(), 1, profile1),
				Arguments.of(state[0].getPrimaryKey(), 1, null));
	}

	private static Stream<Arguments> saveProfile_params() {
		Profile profile1 = new Profile(projectId, datasetId, state[0].getPrimaryKey().getId(), 1, false, null,
				Arrays.asList(new Entity<>(new Allele.PrimaryKey(null, null, first.getPrimaryKey().getId()), first.getVersion(), first.isDeprecated()), new Entity<>(new Allele.PrimaryKey(null, null, second.getPrimaryKey().getId()), second.getVersion(), second.isDeprecated())));
		Profile profile2 = new Profile(projectId, datasetId, "new", 1, false, null, Arrays.asList(new Entity<>(new Allele.PrimaryKey(null, null, third.getPrimaryKey().getId()), third.getVersion(), third.isDeprecated()), null));
		Profile profile3 = new Profile(projectId, datasetId, "new", 1, false, null, Collections.singletonList(new Entity<>(third.getPrimaryKey(), third.getVersion(), third.isDeprecated())));
		Profile profile4 = new Profile(projectId, datasetId, "new", 1, false, null, Arrays.asList(null, null));
		return Stream.of(Arguments.of(profile1, true, true, false, new MockResult().queryStatistics()),
				Arguments.of(profile2, false, true, false, new MockResult().queryStatistics()),
				Arguments.of(profile2, false, true, true, null),
				Arguments.of(profile3, false, true, false, null),
				Arguments.of(profile4, true, true,  false, null),
				Arguments.of(profile1, true, false, false, null),
				Arguments.of(null, false, true, false, null));
	}

	private static Stream<Arguments> deleteProfile_params() {
		return Stream.of(Arguments.of(state[0].getPrimaryKey(), true),
				Arguments.of(state[0].getPrimaryKey(), false));
	}

	private static Stream<Arguments> saveAll_params() throws IOException {
		String[] headers = new String[]{"uvrA", "gyrB", "ftsY", "tuf", "gap"};
		Schema schema = new Schema("taxon", "id", Method.MLST, "description", headers);
		Dataset dataset = new Dataset(projectId, datasetId, null, schema.getPrimaryKey().getTaxonId(), schema.getPrimaryKey().getId());
		String[][] alleles1 = {{"1", "1", "1", "1", "1"}};
		String[][] alleles2 = {{"1", "1", "1", "1", " "}, {" ", "1", " ", "3", "3"}};
		MultipartFile fileNone = FormatterTests.createFile("ml", "ml-d-0.txt"), file1 = FormatterTests.createFile("ml", "ml-h-d-1.txt"),
				fileN = FormatterTests.createFile("ml", "ml-h-d-2-m.txt");
		List<Profile> profiles1 = Arrays.asList(ProfilesFormatterTests.profiles(projectId,datasetId, schema, alleles1, false));
		List<Profile> profilesN = Arrays.asList(ProfilesFormatterTests.profiles(projectId,datasetId, schema, alleles2, false));
		boolean[] existsAll1 = new boolean[] {true}, notExists1 = new boolean[] {false},
				notExistsN = new boolean[] {false, false}, existsAllN = new boolean[] {true, true},
				existsSomeN = new boolean[] {true, false};
		boolean[] missing1 = new boolean[] {true}, missingN = new boolean[] {true, false},
				notMissing1 = new boolean[] {false}, notMissingN = new boolean[] {false, false};
		return Stream.of(Arguments.of(BatchRepository.SKIP, fileNone, dataset, schema, Collections.emptyList(), new boolean[0], new boolean[0], null),
				Arguments.of(BatchRepository.SKIP, file1, dataset, schema, profiles1, notExists1, notMissing1, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.SKIP, fileN, dataset, schema, profilesN, notExistsN, notMissingN, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.SKIP, file1, dataset, schema, profiles1, notExists1, missing1, null),
				Arguments.of(BatchRepository.SKIP, fileN, dataset, schema, profilesN, notExistsN, missingN, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.SKIP, file1, dataset, schema, profiles1, existsAll1, notMissing1, null),
				Arguments.of(BatchRepository.SKIP, fileN, dataset, schema, profilesN, existsAllN, notMissingN, null),
				Arguments.of(BatchRepository.SKIP, file1, dataset, schema, profiles1, existsAll1, missing1, null),
				Arguments.of(BatchRepository.SKIP, fileN, dataset, schema, profilesN, existsAllN, missingN, null),
				Arguments.of(BatchRepository.SKIP, fileN, dataset, schema, profilesN, existsSomeN, notMissingN, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.SKIP, fileN, dataset, schema, profilesN, existsSomeN, missingN, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.UPDATE, file1, dataset, schema, profiles1, notExists1, notMissing1, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.UPDATE, fileN, dataset, schema, profilesN, notExistsN, notMissingN, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.UPDATE, file1, dataset, schema, profiles1, notExists1, missing1, null),
				Arguments.of(BatchRepository.UPDATE, fileN, dataset, schema, profilesN, notExistsN, missingN, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.UPDATE, file1, dataset, schema, profiles1, existsAll1, notMissing1, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.UPDATE, fileN, dataset, schema, profilesN, existsAllN, notMissingN, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.UPDATE, file1, dataset, schema, profiles1, existsAll1, missing1, null),
				Arguments.of(BatchRepository.UPDATE, fileN, dataset, schema, profilesN, existsAllN, missingN, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.UPDATE, fileN, dataset, schema, profilesN, existsSomeN, notMissingN, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.UPDATE, fileN, dataset, schema, profilesN, existsSomeN, missingN, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.SKIP, fileN, dataset,  schema, profilesN, existsSomeN, missingN, null));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@ParameterizedTest
	@MethodSource("getProfiles_params")
	public void getProfiles(int page, List<Profile> expected) {
		Schema schema = new Schema(taxonId, "schema", Method.MLVA, null, new String[]{locusId1, locusId2});
		Mockito.when(profileRepository.findAll(anyInt(), anyInt(), any())).thenReturn(Optional.ofNullable(expected));
		Mockito.when(schemaRepository.find(any())).thenReturn(Optional.of(schema));
		Optional<Pair<Schema, List<Profile>>> result = service.getProfiles(projectId, datasetId, page, LIMIT);
		if(expected == null && !result.isPresent()) {
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
		Optional<Profile> result = service.getProfile(key.getProjectId(), key.getDatasetId(), key.getId(), version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("saveProfile_params")
	public void saveProfile(Profile profile, boolean authorized, boolean dataset, boolean anyMissing, QueryStatistics expected) {
		Schema schema = new Schema(taxonId, "schema", Method.MLVA, null, new String[]{locusId1, locusId2});
		Mockito.when(datasetRepository.exists(any())).thenReturn(dataset);
		Mockito.when(schemaRepository.find(any())).thenReturn(Optional.of(schema));
		Mockito.when(alleleRepository.anyMissing(any())).thenReturn(anyMissing);
		if(profile != null)
			Mockito.when(profileRepository.save(profile.updateReferences(schema, "", authorized))).thenReturn(Optional.ofNullable(expected));
		boolean result = service.saveProfile(profile, authorized);
		assertEquals(expected != null, result);
	}

	@ParameterizedTest
	@MethodSource("deleteProfile_params")
	public void deleteProfile(Profile.PrimaryKey key, boolean expected) {
		Mockito.when(profileRepository.remove(any())).thenReturn(expected);
		boolean result = service.deleteProfile(key.getProjectId(), key.getDatasetId(), key.getId());
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("saveAll_params")
	public void saveAllelesOnConflictSkip(String flag, MultipartFile file, Dataset dataset, Schema schema,  List<Profile> profiles, boolean[] exists, boolean[] missing, QueryStatistics expected) throws IOException {
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
			if(flag.equals(BatchRepository.SKIP) && !exists[i] && !missing[i])
				toSave.add(updated.get(i));
			else if(flag.equals(BatchRepository.UPDATE) && !missing[i])
				toSave.add(updated.get(i));
		});
		Mockito.when(profileRepository.saveAll(toSave, projectId.toString(), datasetId.toString())).thenReturn(Optional.ofNullable(expected));
		boolean result;
		if (flag.equals(BatchRepository.SKIP))
			result = service.saveProfilesOnConflictSkip(projectId, datasetId, false, file);
		else
			result = service.saveProfilesOnConflictUpdate(projectId, datasetId, false, file);
		assertEquals(expected != null, result);
	}
}
