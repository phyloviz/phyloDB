package pt.ist.meic.phylodb.unit.typing.isolate;

import pt.ist.meic.phylodb.utils.service.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.typing.isolate.model.Ancillary;
import pt.ist.meic.phylodb.typing.isolate.model.Isolate;
import pt.ist.meic.phylodb.unit.ServiceTestsContext;
import pt.ist.meic.phylodb.unit.formatters.IsolatesFormatterTests;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static pt.ist.meic.phylodb.utils.FileUtils.createFile;

public class IsolateServiceTests extends ServiceTestsContext {

	private static final int LIMIT = 2;
	private static final String PROJECTID = PROJECT1.getPrimaryKey(), DATASETID = DATASET1.getPrimaryKey().getId();
	private static final Isolate[] STATE = new Isolate[]{ISOLATE1, ISOLATE2};

	private static Stream<Arguments> getIsolatesEntities_params() {
		VersionedEntity<Isolate.PrimaryKey> state0 = new VersionedEntity<>(STATE[0].getPrimaryKey(), STATE[0].getVersion(), STATE[0].isDeprecated()),
				state1 = new VersionedEntity<>(STATE[1].getPrimaryKey(), STATE[1].getVersion(), STATE[1].isDeprecated());
		List<VersionedEntity<Isolate.PrimaryKey>> expected1 = new ArrayList<VersionedEntity<Isolate.PrimaryKey>>() {{
			add(state0);
		}};
		List<VersionedEntity<Isolate.PrimaryKey>> expected2 = new ArrayList<VersionedEntity<Isolate.PrimaryKey>>() {{
			add(state0);
			add(state1);
		}};
		return Stream.of(Arguments.of(0, Collections.emptyList()),
				Arguments.of(0, expected1),
				Arguments.of(0, expected2),
				Arguments.of(-1, null));
	}

	private static Stream<Arguments> getIsolates_params() {
		List<Isolate> expected1 = new ArrayList<Isolate>() {{
			add(STATE[0]);
		}};
		List<Isolate> expected2 = new ArrayList<Isolate>() {{
			add(STATE[0]);
			add(STATE[1]);
		}};
		return Stream.of(Arguments.of(0, Collections.emptyList()),
				Arguments.of(0, expected1),
				Arguments.of(0, expected2),
				Arguments.of(-1, null));
	}

	private static Stream<Arguments> getIsolate_params() {
		return Stream.of(Arguments.of(STATE[0].getPrimaryKey(), 1, ISOLATE1),
				Arguments.of(STATE[0].getPrimaryKey(), 1, null));
	}

	private static Stream<Arguments> saveIsolate_params() {
		Isolate isolate1 = isolate(STATE[0].getPrimaryKey().getId(), 1, false, null, new Ancillary[0], PROFILE1),
				isolate2 = isolate(STATE[0].getPrimaryKey().getId(), 1, false, null, new Ancillary[0], null);
		return Stream.of(Arguments.of(isolate1, true, true, true),
				Arguments.of(isolate2, true, true, true),
				Arguments.of(isolate1, true, false, false),
				Arguments.of(isolate1, false, true, false),
				Arguments.of(null, true, true, false));
	}

	private static Stream<Arguments> deleteIsolate_params() {
		return Stream.of(Arguments.of(STATE[0].getPrimaryKey(), true),
				Arguments.of(STATE[0].getPrimaryKey(), false));
	}

	private static Stream<Arguments> saveAll_params() throws IOException {
		String[][] ancillary1 = {{"AU13161", "USA", "North America"}, {"LMG 1231T", "Unknown", "Europe"}};
		MultipartFile fileNone = createFile("formatters/isolates", "i-iap-0.txt"),
				file1 = createFile("formatters/isolates", "i-iap-1-ve-p.txt"),
				fileN = createFile("formatters/isolates", "i-iap-2-ve-p.txt"),
				fileNWithoutProfile = createFile("formatters/isolates", "i-ia-2-ve.txt");
		List<Isolate> isolates1 = IsolatesFormatterTests.isolates(PROJECTID, DATASETID, Arrays.copyOfRange(ancillary1, 0, 1), new String[]{"102"});
		List<Isolate> isolatesN = IsolatesFormatterTests.isolates(PROJECTID, DATASETID, ancillary1, new String[]{"102", "103"});
		List<Isolate> isolatesNWithoutProfile = IsolatesFormatterTests.isolates(PROJECTID, DATASETID, ancillary1, null);
		boolean[] existsAll1 = new boolean[]{true}, notExists1 = new boolean[]{false},
				notExistsN = new boolean[]{false, false}, existsAllN = new boolean[]{true, true},
				existsSomeN = new boolean[]{true, false};
		boolean[] notExistsProfile = new boolean[]{false}, notExistsProfiles = new boolean[]{false, true},
				existsProfile = new boolean[]{true}, existProfiles = new boolean[]{true, true};
		return Stream.of(Arguments.of(false, fileNone, true, Collections.emptyList(), new boolean[0], new boolean[0], false, null),
				Arguments.of(false, file1, true, isolates1, notExists1, existsProfile, true, new Pair<>(new Integer[0], new String[0])),
				Arguments.of(false, fileN, true, isolatesN, notExistsN, existProfiles, true, new Pair<>(new Integer[0], new String[0])),
				Arguments.of(false, file1, true, isolates1, notExists1, notExistsProfile, false, null),
				Arguments.of(false, fileN, true, isolatesN, notExistsN, notExistsProfiles, true, new Pair<>(new Integer[0], new String[] {"1"})),
				Arguments.of(false, file1, true, isolates1, existsAll1, existsProfile, false, null),
				Arguments.of(false, fileN, true, isolatesN, existsAllN, existProfiles, false, null),
				Arguments.of(false, file1, true, isolates1, existsAll1, notExistsProfile, false, null),
				Arguments.of(false, fileN, true, isolatesN, existsAllN, notExistsProfiles, false, null),
				Arguments.of(false, fileN, true, isolatesN, existsSomeN, existProfiles, true, new Pair<>(new Integer[0], new String[] {"1"})),
				Arguments.of(false, fileN, true, isolatesN, existsSomeN, notExistsProfiles, false, null),
				Arguments.of(false, fileNWithoutProfile, true, isolatesNWithoutProfile, existsSomeN, notExistsProfiles, true, new Pair<>(new Integer[0], new String[] {"1"})),
				Arguments.of(true, file1, true, isolates1, notExists1, existsProfile, true, new Pair<>(new Integer[0], new String[0])),
				Arguments.of(true, fileN, true, isolatesN, notExistsN, existProfiles, true, new Pair<>(new Integer[0], new String[0])),
				Arguments.of(true, file1, true, isolates1, notExists1, notExistsProfile, false, null),
				Arguments.of(true, fileN, true, isolatesN, notExistsN, notExistsProfiles, true, new Pair<>(new Integer[0], new String[] {"1"})),
				Arguments.of(true, file1, true, isolates1, existsAll1, existsProfile, true, new Pair<>(new Integer[0], new String[0])),
				Arguments.of(true, fileN, true, isolatesN, existsAllN, existProfiles, true, new Pair<>(new Integer[0], new String[0])),
				Arguments.of(true, file1, true, isolates1, existsAll1, notExistsProfile, false, null),
				Arguments.of(true, fileN, true, isolatesN, existsAllN, notExistsProfiles, true, new Pair<>(new Integer[0], new String[] {"1"})),
				Arguments.of(true, fileN, true, isolatesN, existsSomeN, existProfiles, true, new Pair<>(new Integer[0], new String[0])),
				Arguments.of(true, fileN, true, isolatesN, existsSomeN, notExistsProfiles, true, new Pair<>(new Integer[0], new String[] {"1"})),
				Arguments.of(true, fileNWithoutProfile, true, isolatesNWithoutProfile, existsSomeN, notExistsProfiles, true,new Pair<>(new Integer[0], new String[0])),
				Arguments.of(false, fileN, false, isolatesN, existsSomeN, notExistsProfiles, false, null));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@ParameterizedTest
	@MethodSource("getIsolatesEntities_params")
	public void getIsolatesEntities(int page, List<VersionedEntity<Isolate.PrimaryKey>> expected) {
		Mockito.when(isolateRepository.findAllEntities(anyInt(), anyInt(), any(), any())).thenReturn(Optional.ofNullable(expected));
		Optional<List<VersionedEntity<Isolate.PrimaryKey>>> result = isolateService.getIsolatesEntities(PROJECTID, DATASETID, page, LIMIT);
		if (expected == null && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertNotNull(expected);
		assertTrue(result.isPresent());
		List<VersionedEntity<Isolate.PrimaryKey>> isolates = result.get();
		assertEquals(expected.size(), isolates.size());
		assertEquals(expected, isolates);
	}

	@ParameterizedTest
	@MethodSource("getIsolates_params")
	public void getProfiles(int page, List<Isolate> expected) {
		Mockito.when(isolateRepository.findAll(anyInt(), anyInt(), any(), any())).thenReturn(Optional.ofNullable(expected));
		Optional<List<Isolate>> result = isolateService.getIsolates(PROJECTID, DATASETID, page, LIMIT);
		if (expected == null && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertNotNull(expected);
		assertTrue(result.isPresent());
		List<Isolate> isolates = result.get();
		assertEquals(expected.size(), isolates.size());
		assertEquals(expected, isolates);
	}

	@ParameterizedTest
	@MethodSource("getIsolate_params")
	public void getIsolate(Isolate.PrimaryKey key, long version, Isolate expected) {
		Mockito.when(isolateRepository.find(any(), anyLong())).thenReturn(Optional.ofNullable(expected));
		Optional<Isolate> result = isolateService.getIsolate(key.getProjectId(), key.getDatasetId(), key.getId(), version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("saveIsolate_params")
	public void saveIsolate(Isolate isolate, boolean dataset, boolean profile, boolean expected) {
		Mockito.when(datasetRepository.exists(any())).thenReturn(dataset);
		Mockito.when(profileRepository.exists(any())).thenReturn(profile);
		if (isolate != null)
			Mockito.when(isolateRepository.save(isolate)).thenReturn(expected);
		boolean result = isolateService.saveIsolate(isolate);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("deleteIsolate_params")
	public void deleteIsolate(Isolate.PrimaryKey key, boolean expected) {
		Mockito.when(isolateRepository.remove(any())).thenReturn(expected);
		boolean result = isolateService.deleteIsolate(key.getProjectId(), key.getDatasetId(), key.getId());
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("saveAll_params")
	public void saveIsolates(boolean update, MultipartFile file, boolean dataset, List<Isolate> isolates, boolean[] existsIsolates, boolean[] existsProfile, boolean expected, Pair<Integer[], String[]> invalids) throws IOException {
		Mockito.when(datasetRepository.exists(any())).thenReturn(dataset);
		List<Isolate> toSave = new ArrayList<>();
		IntStream.range(0, isolates.size()).forEach(i -> {
			Mockito.when(isolateRepository.exists(isolates.get(i).getPrimaryKey())).thenReturn(existsIsolates[i]);
			if(isolates.get(i).getProfile() != null)
				Mockito.when(profileRepository.exists(isolates.get(i).getProfile().getPrimaryKey())).thenReturn(existsProfile[i]);
			if (!update && !existsIsolates[i] && (isolates.get(i).getProfile() == null || existsProfile[i]))
				toSave.add(isolates.get(i));
			else if (update && (isolates.get(i).getProfile() == null || existsProfile[i]))
				toSave.add(isolates.get(i));
		});
		Mockito.when(isolateRepository.saveAll(toSave)).thenReturn(expected);
		Optional<Pair<Integer[], String[]>> result;
		if (!update)
			result = isolateService.saveIsolatesOnConflictSkip(PROJECTID, DATASETID, 0, file);
		else
			result = isolateService.saveIsolatesOnConflictUpdate(PROJECTID, DATASETID, 0, file);
		if(invalids != null) {
			assertTrue(result.isPresent());
			assertArrayEquals(invalids.getKey(), result.get().getKey());
			assertArrayEquals(invalids.getValue(), result.get().getValue());
		} else
			assertFalse(result.isPresent());
	}

}
