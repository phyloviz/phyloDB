package pt.ist.meic.phylodb.analysis.inference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.ServiceTestsContext;
import pt.ist.meic.phylodb.analysis.inference.model.Inference;
import pt.ist.meic.phylodb.analysis.inference.model.InferenceAlgorithm;
import pt.ist.meic.phylodb.formatters.FormatterTests;
import pt.ist.meic.phylodb.io.formatters.analysis.TreeFormatter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

public class InferenceServiceTests extends ServiceTestsContext {

	private static final int LIMIT = 2;
	private static final Inference[] STATE = new Inference[]{INFERENCE1, INFERENCE2};

	private static Stream<Arguments> getInferences_params() {
		List<Inference> expected1 = new ArrayList<Inference>() {{
			add(STATE[0]);
		}};
		List<Inference> expected2 = new ArrayList<Inference>() {{
			add(STATE[0]);
			add(STATE[1]);
		}};
		return Stream.of(Arguments.of(0, Collections.emptyList()),
				Arguments.of(0, expected1),
				Arguments.of(0, expected2),
				Arguments.of(-1, null));
	}

	private static Stream<Arguments> getInference_params() {
		return Stream.of(Arguments.of(INFERENCE1.getPrimaryKey(), INFERENCE1),
				Arguments.of(INFERENCE1.getPrimaryKey(), null));
	}

	private static Stream<Arguments> saveInference_params() throws IOException {
		String algorithmOk = InferenceAlgorithm.GOEBURST.getName(), algorithmBad = "teste",
			formatOk = TreeFormatter.NEWICK, formatBad = "teste2";
		MultipartFile fileOk = FormatterTests.createFile("newick", "nwk-3-e.txt"),
				fileBad = FormatterTests.createFile("newick", "nwk-0.txt");
		return Stream.of(Arguments.of(algorithmOk, formatOk, fileOk, true, false, true),
				Arguments.of(algorithmOk, formatOk, fileOk, true, true, false),
				Arguments.of(algorithmOk, formatOk, fileOk, false, false, false),
				Arguments.of(algorithmOk, formatOk, fileBad, true, false, false),
				Arguments.of(algorithmOk, formatBad, fileOk, true, false, false),
				Arguments.of(algorithmBad, formatOk, fileOk, true, false, false));
	}

	private static Stream<Arguments> deleteInference_params() {
		return Stream.of(Arguments.of(STATE[0].getPrimaryKey(), true),
				Arguments.of(STATE[0].getPrimaryKey(), false));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@ParameterizedTest
	@MethodSource("getInferences_params")
	public void getInferences(int page, List<Inference> expected) {
		Mockito.when(inferenceRepository.findAll(anyInt(), anyInt(), any(), any())).thenReturn(Optional.ofNullable(expected));
		Optional<List<Inference>> result = inferenceService.getInferences(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), page, LIMIT);
		if (expected == null && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertNotNull(expected);
		assertTrue(result.isPresent());
		List<Inference> schemas = result.get();
		assertEquals(expected.size(), schemas.size());
		assertEquals(expected, schemas);
	}

	@ParameterizedTest
	@MethodSource("getInference_params")
	public void getInference(Inference.PrimaryKey key, Inference expected) {
		Mockito.when(inferenceRepository.find(any())).thenReturn(Optional.ofNullable(expected));
		Optional<Inference> result = inferenceService.getInference(key.getProjectId(), key.getDatasetId(), key.getId());
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("saveInference_params")
	public void saveInference(String algorithm, String format, MultipartFile file, boolean dataset, boolean profiles, boolean expected) throws IOException {
		Mockito.when(datasetRepository.exists(any())).thenReturn(dataset);
		Mockito.when(profileRepository.anyMissing(any())).thenReturn(profiles);
		Mockito.when(inferenceRepository.save(any())).thenReturn(expected);
		Optional<String> result = inferenceService.saveInference(UUID.randomUUID().toString(), UUID.randomUUID().toString(), algorithm, format, file);
		assertEquals(expected, result.isPresent());
	}

	@ParameterizedTest
	@MethodSource("deleteInference_params")
	public void deleteInference(Inference.PrimaryKey key, boolean expected) {
		Mockito.when(inferenceRepository.remove(any())).thenReturn(expected);
		boolean result = inferenceService.deleteInference(key.getProjectId(), key.getDatasetId(), key.getId());
		assertEquals(expected, result);
	}

}
