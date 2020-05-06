package pt.ist.meic.phylodb.typing.isolate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.neo4j.ogm.model.QueryStatistics;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.ServiceTestsContext;
import pt.ist.meic.phylodb.formatters.FormatterTests;
import pt.ist.meic.phylodb.formatters.IsolatesFormatterTests;
import pt.ist.meic.phylodb.typing.isolate.model.Ancillary;
import pt.ist.meic.phylodb.typing.isolate.model.Isolate;
import pt.ist.meic.phylodb.utils.MockResult;
import pt.ist.meic.phylodb.utils.db.BatchRepository;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class IsolateServiceTests extends ServiceTestsContext {

	private static final int LIMIT = 2;
	private static final UUID PROJECTID = PROJECT1.getPrimaryKey(), DATASETID = DATASET1.getPrimaryKey().getId();
	private static final Isolate[] STATE = new Isolate[]{ISOLATE1, ISOLATE2};

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
		return Stream.of(Arguments.of(isolate1, true, true, new MockResult().queryStatistics()),
				Arguments.of(isolate2, true, true, new MockResult().queryStatistics()),
				Arguments.of(isolate1, true, false, null),
				Arguments.of(isolate1, false, true, null),
				Arguments.of(null, true, true, null));
	}

	private static Stream<Arguments> deleteIsolate_params() {
		return Stream.of(Arguments.of(STATE[0].getPrimaryKey(), true),
				Arguments.of(STATE[0].getPrimaryKey(), false));
	}

	private static Stream<Arguments> saveAll_params() throws IOException {
		String[][] ancillary1 = {{"AU13161", "USA", "North America"}, {"LMG 1231T", "Unknown", "Europe"}};
		MultipartFile fileNone = FormatterTests.createFile("isolates", "i-iap-0.txt"),
				file1 = FormatterTests.createFile("isolates", "i-iap-1-ve-p.txt"),
				fileN = FormatterTests.createFile("isolates", "i-iap-2-ve-p.txt"),
				fileNWithoutProfile = FormatterTests.createFile("isolates", "i-ia-2-ve.txt");
		List<Isolate> isolates1 = IsolatesFormatterTests.isolates(PROJECTID, DATASETID, Arrays.copyOfRange(ancillary1, 0, 1), new String[]{"102"});
		List<Isolate> isolatesN = IsolatesFormatterTests.isolates(PROJECTID, DATASETID, ancillary1, new String[]{"102", "103"});
		List<Isolate> isolatesNWithoutProfile = IsolatesFormatterTests.isolates(PROJECTID, DATASETID, ancillary1, null);
		boolean[] existsAll1 = new boolean[]{true}, notExists1 = new boolean[]{false},
				notExistsN = new boolean[]{false, false}, existsAllN = new boolean[]{true, true},
				existsSomeN = new boolean[]{true, false};
		boolean[] notExistsProfile = new boolean[]{false}, notExistsProfiles = new boolean[]{false, true},
				existsProfile = new boolean[]{true}, existProfiles = new boolean[]{true, true};
		return Stream.of(Arguments.of(BatchRepository.SKIP, fileNone, true, Collections.emptyList(), new boolean[0], new boolean[0], null),
				Arguments.of(BatchRepository.SKIP, file1, true, isolates1, notExists1, existsProfile, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.SKIP, fileN, true, isolatesN, notExistsN, existProfiles, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.SKIP, file1, true, isolates1, notExists1, notExistsProfile, null),
				Arguments.of(BatchRepository.SKIP, fileN, true, isolatesN, notExistsN, notExistsProfiles, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.SKIP, file1, true, isolates1, existsAll1, existsProfile, null),
				Arguments.of(BatchRepository.SKIP, fileN, true, isolatesN, existsAllN, existProfiles, null),
				Arguments.of(BatchRepository.SKIP, file1, true, isolates1, existsAll1, notExistsProfile, null),
				Arguments.of(BatchRepository.SKIP, fileN, true, isolatesN, existsAllN, notExistsProfiles, null),
				Arguments.of(BatchRepository.SKIP, fileN, true, isolatesN, existsSomeN, existProfiles, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.SKIP, fileN, true, isolatesN, existsSomeN, notExistsProfiles, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.SKIP, fileNWithoutProfile, true, isolatesNWithoutProfile, existsSomeN, notExistsProfiles, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.UPDATE, file1, true, isolates1, notExists1, existsProfile, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.UPDATE, fileN, true, isolatesN, notExistsN, existProfiles, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.UPDATE, file1, true, isolates1, notExists1, notExistsProfile, null),
				Arguments.of(BatchRepository.UPDATE, fileN, true, isolatesN, notExistsN, notExistsProfiles, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.UPDATE, file1, true, isolates1, existsAll1, existsProfile, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.UPDATE, fileN, true, isolatesN, existsAllN, existProfiles, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.UPDATE, file1, true, isolates1, existsAll1, notExistsProfile, null),
				Arguments.of(BatchRepository.UPDATE, fileN, true, isolatesN, existsAllN, notExistsProfiles, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.UPDATE, fileN, true, isolatesN, existsSomeN, existProfiles, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.UPDATE, fileN, true, isolatesN, existsSomeN, notExistsProfiles, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.UPDATE, fileNWithoutProfile, true, isolatesNWithoutProfile, existsSomeN, notExistsProfiles, new MockResult().queryStatistics()),
				Arguments.of(BatchRepository.SKIP, fileN, false, isolatesN, existsSomeN, notExistsProfiles, null));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
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
	public void saveIsolate(Isolate isolate, boolean dataset, boolean profile, QueryStatistics expected) {
		Mockito.when(datasetRepository.exists(any())).thenReturn(dataset);
		Mockito.when(profileRepository.exists(any())).thenReturn(profile);
		if (isolate != null)
			Mockito.when(isolateRepository.save(isolate)).thenReturn(Optional.ofNullable(expected));
		boolean result = isolateService.saveIsolate(isolate);
		assertEquals(expected != null, result);
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
	public void saveIsolates(String flag, MultipartFile file, boolean dataset, List<Isolate> isolates, boolean[] existsIsolates, boolean[] existsProfile, QueryStatistics expected) throws IOException {
		Mockito.when(datasetRepository.exists(any())).thenReturn(dataset);
		List<Isolate> toSave = new ArrayList<>();
		IntStream.range(0, isolates.size()).forEach(i -> {
			Mockito.when(isolateRepository.exists(isolates.get(i).getPrimaryKey())).thenReturn(existsIsolates[i]);
			if(isolates.get(i).getProfile() != null)
				Mockito.when(profileRepository.exists(isolates.get(i).getProfile().getPrimaryKey())).thenReturn(existsProfile[i]);
			if (flag.equals(BatchRepository.SKIP) && !existsIsolates[i] && (isolates.get(i).getProfile() == null || existsProfile[i]))
				toSave.add(isolates.get(i));
			else if (flag.equals(BatchRepository.UPDATE) && (isolates.get(i).getProfile() == null || existsProfile[i]))
				toSave.add(isolates.get(i));
		});
		Mockito.when(isolateRepository.saveAll(toSave, PROJECTID.toString(), DATASETID.toString())).thenReturn(Optional.ofNullable(expected));
		boolean result;
		if (flag.equals(BatchRepository.SKIP))
			result = isolateService.saveIsolatesOnConflictSkip(PROJECTID, DATASETID, 0, file);
		else
			result = isolateService.saveIsolatesOnConflictUpdate(PROJECTID, DATASETID, 0, file);
		assertEquals(expected != null, result);
	}

}
