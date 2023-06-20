package pt.ist.meic.phylodb.unit.phylogeny.allele;

import pt.ist.meic.phylodb.utils.service.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import pt.ist.meic.phylodb.unit.ControllerTestsContext;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.formatters.dataset.allele.FastaFormatter;
import pt.ist.meic.phylodb.io.output.BatchOutputModel;
import pt.ist.meic.phylodb.io.output.FileOutputModel;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.allele.model.AlleleInputModel;
import pt.ist.meic.phylodb.phylogeny.allele.model.AlleleOutputModel;
import pt.ist.meic.phylodb.phylogeny.allele.model.GetAlleleOutputModel;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class AlleleControllerTests extends ControllerTestsContext {

	private static final String TAXONID = TAXON1.getPrimaryKey(), LOCUSID = LOCUS1.getPrimaryKey().getId();
	private static final String PROJECTID = PROJECT1.getPrimaryKey();

	private static Stream<Arguments> getAllelesList_params() {
		String uri = "/taxa/%s/loci/%s/alleles";
		List<VersionedEntity<Allele.PrimaryKey>> alleles1 = new ArrayList<VersionedEntity<Allele.PrimaryKey>>() {{
			add(new VersionedEntity<>(new Allele.PrimaryKey(TAXONID, LOCUSID, "id", null), 1, false));
		}};
		List<Allele> alleles2 = new ArrayList<Allele>() {{
			add(new Allele(TAXONID, LOCUSID, "id", null, null));
			add(new Allele(TAXONID, LOCUSID, "id2", null, null));
		}};
		MockHttpServletRequestBuilder req1 = get(String.format(uri, TAXONID, LOCUSID)).param("page", "0"),
				req2 = get(String.format(uri, TAXONID, LOCUSID)).param("page", "0").param("project", PROJECTID),
				req3 = get(uri), req4 = get(String.format(uri, TAXONID, LOCUSID)).param("page", "-10");
		List<AlleleOutputModel> result1 = alleles1.stream()
				.map(AlleleOutputModel::new)
				.collect(Collectors.toList());
		List<AlleleOutputModel> result2 = alleles2.stream()
				.map(AlleleOutputModel::new)
				.collect(Collectors.toList());
		return Stream.of(Arguments.of(req1, alleles1, MediaType.APPLICATION_JSON, HttpStatus.OK, result1, null),
				Arguments.of(req2, alleles2, MediaType.APPLICATION_JSON, HttpStatus.OK, result2, null),
				Arguments.of(req1, Collections.emptyList(), MediaType.APPLICATION_JSON, HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req3, alleles1, MediaType.APPLICATION_JSON, HttpStatus.OK, result1, null),
				Arguments.of(req2, alleles2, MediaType.APPLICATION_JSON, HttpStatus.OK, result2, null),
				Arguments.of(req3, Collections.emptyList(), MediaType.APPLICATION_JSON, HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req4, null, MediaType.APPLICATION_JSON, HttpStatus.BAD_REQUEST, null, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req1, alleles1, MediaType.IMAGE_PNG, HttpStatus.NOT_ACCEPTABLE, result1, new ErrorOutputModel(Problem.NOT_ACCEPTABLE.getMessage())));
	}

	private static Stream<Arguments> getAllelesFile_params() {
		String uri = "/taxa/%s/loci/%s/alleles/files";
		List<Allele> alleles1 = new ArrayList<Allele>() {{
			add(new Allele(TAXONID, LOCUSID, "id", null, null));
		}};
		List<Allele> alleles2 = new ArrayList<Allele>() {{
			add(new Allele(TAXONID, LOCUSID, "id", null, null));
			add(new Allele(TAXONID, LOCUSID, "id2", null, null));
		}};
		MockHttpServletRequestBuilder req1 = get(String.format(uri, TAXONID, LOCUSID)).param("page", "0");
		FileOutputModel result3 = new FileOutputModel(new FastaFormatter().format(Collections.emptyList(), 60));
		FileOutputModel result4 = new FileOutputModel(new FastaFormatter().format(alleles1, 60));
		FileOutputModel result5 = new FileOutputModel(new FastaFormatter().format(alleles2, 60));
		return Stream.of(Arguments.of(req1, Collections.emptyList(), HttpStatus.OK, result3, null),
				Arguments.of(req1, alleles1, HttpStatus.OK, result4, null),
				Arguments.of(req1, alleles2, HttpStatus.OK, result5, null));
	}

	private static Stream<Arguments> getAllele_params() {
		String uri = "/taxa/%s/loci/%s/alleles/%s";
		Allele allele1 = new Allele(TAXONID, LOCUSID, "id1", "description", null);
		Allele allele2 = new Allele(TAXONID, LOCUSID, "id2", "description", PROJECTID);
		Allele.PrimaryKey key1 = allele1.getPrimaryKey();
		Allele.PrimaryKey key2 = allele2.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = get(String.format(uri, TAXONID, LOCUSID, key1.getId())).param("version", "1"),
				req2 = get(String.format(uri, TAXONID, LOCUSID, key2.getId())).param("version", "1").param("project", PROJECTID),
				req3 = get(String.format(uri, TAXONID, LOCUSID, key1.getId())),
				req4 = get(String.format(uri, TAXONID, LOCUSID, key2.getId())).param("project", PROJECTID);
		return Stream.of(Arguments.of(req1, allele1, HttpStatus.OK, new GetAlleleOutputModel(allele1)),
				Arguments.of(req2, allele2, HttpStatus.OK, new GetAlleleOutputModel(allele2)),
				Arguments.of(req1, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())),
				Arguments.of(req2, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())),
				Arguments.of(req3, allele1, HttpStatus.OK, new GetAlleleOutputModel(allele1)),
				Arguments.of(req4, allele2, HttpStatus.OK, new GetAlleleOutputModel(allele2)),
				Arguments.of(req3, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())),
				Arguments.of(req3, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())));
	}

	private static Stream<Arguments> saveAllele_params() {
		String uri = "/taxa/%s/loci/%s/alleles/%s";
		Allele allele = new Allele(TAXONID, LOCUSID, "id", "description", null);
		Allele.PrimaryKey key = allele.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = put(String.format(uri, TAXONID, LOCUSID, key.getId())),
				req2 = put(String.format(uri, TAXONID, LOCUSID, key.getId())).param("project", PROJECTID);
		AlleleInputModel input1 = new AlleleInputModel(key.getId(), "description"),
				input2 = new AlleleInputModel("different", "description");
		return Stream.of(Arguments.of(req1, input1, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req1, input1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req1, input2, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req2, input1, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req2, input1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req2, input2, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req1, null, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> putAlleles_params() {
		String uri = "/taxa/%s/loci/%s/alleles/files";
		MockMultipartFile file = new MockMultipartFile("file", "", "text/plain", "b".getBytes());
		MockMultipartHttpServletRequestBuilder req1 = multipart(String.format(uri, TAXONID, LOCUSID)).file(file),
				req2 = multipart(String.format(uri, TAXONID, LOCUSID)).file(file),
				req3 = multipart(String.format(uri, TAXONID, LOCUSID));
		req1.with(r -> {
			r.setMethod(HttpMethod.PUT.name());
			return r;
		});
		req2.with(r -> {
			r.setMethod(HttpMethod.PUT.name());
			return r;
		});
		req3.with(r -> {
			r.setMethod(HttpMethod.PUT.name());
			return r;
		});
		req2.param("project", PROJECTID);
		MockHttpServletRequestBuilder req4 = put(String.format(uri, TAXONID, LOCUSID));
		Integer[] invalidLines = {1, 2, 3};
		String[] invalidIds = {"4, 5"};
		return Stream.of(Arguments.of(req1, new Pair<>(invalidLines, invalidIds), HttpStatus.OK, new BatchOutputModel(invalidLines, invalidIds)),
				Arguments.of(req1, new Pair<>(new Integer[0], new String[0]), HttpStatus.OK, new BatchOutputModel(new Integer[0], new String[0])),
				Arguments.of(req1, null, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req2, new Pair<>(invalidLines, invalidIds), HttpStatus.OK, new BatchOutputModel(invalidLines, invalidIds)),
				Arguments.of(req2, null, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req3, null, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req4, null, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> postAlleles_params() {
		String uri = "/taxa/%s/loci/%s/alleles/files";
		MockMultipartFile file = new MockMultipartFile("file", "", "text/plain", "bytes".getBytes());
		MockHttpServletRequestBuilder req1 = multipart(String.format(uri, TAXONID, LOCUSID)).file(file),
				req2 = multipart(String.format(uri, TAXONID, LOCUSID)).file(file).param("project", PROJECTID),
				req3 = multipart(String.format(uri, TAXONID, LOCUSID)),
				req4 = post(String.format(uri, TAXONID, LOCUSID));
		Integer[] invalidLines = {1, 2, 3};
		String[] invalidIds = {"4, 5"};
		return Stream.of(Arguments.of(req1, new Pair<>(invalidLines, invalidIds), HttpStatus.OK, new BatchOutputModel(invalidLines, invalidIds)),
				Arguments.of(req1, new Pair<>(new Integer[0], new String[0]), HttpStatus.OK, new BatchOutputModel(new Integer[0], new String[0])),
				Arguments.of(req1, null, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req2, new Pair<>(invalidLines, invalidIds), HttpStatus.OK, new BatchOutputModel(invalidLines, invalidIds)),
				Arguments.of(req2, null, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req3, null, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req4, null, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> deleteAllele_params() {
		String uri = "/taxa/%s/loci/%s/alleles/%s";
		Allele allele = new Allele(TAXONID, LOCUSID, "id", "description", null);
		Allele.PrimaryKey key = allele.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = delete(String.format(uri, TAXONID, LOCUSID, key.getId()));
		return Stream.of(Arguments.of(req1, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(authenticationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
		Mockito.when(authorizationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
	}

	@ParameterizedTest
	@MethodSource("getAllelesList_params")
	public void getAllelesList(MockHttpServletRequestBuilder req, List<VersionedEntity<Allele.PrimaryKey>> alleles, MediaType mediatype, HttpStatus expectedStatus, List<AlleleOutputModel> expectedResult, ErrorOutputModel expectedError) throws Exception {
		Mockito.when(alleleService.getAllelesEntities(anyString(), anyString(), any(), anyInt(), anyInt())).thenReturn(Optional.ofNullable(alleles));
		MockHttpServletResponse result = executeRequest(req, mediatype);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful()) {
			List<Map<String, Object>> parsed = parseResult(List.class, result);
			assertEquals(expectedResult.size(), parsed.size());
			if (expectedResult.size() > 0) {
				for (int i = 0; i < expectedResult.size(); i++) {
					Map<String, Object> p = parsed.get(i);
					assertEquals(expectedResult.get(i).getId(), p.get("id"));
					assertEquals(expectedResult.get(i).getVersion(), Long.parseLong(p.get("version").toString()));
				}
			}
		} else
			assertEquals(expectedError, parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("getAllelesFile_params")
	public void getAllelesString(MockHttpServletRequestBuilder req, List<Allele> alleles, HttpStatus expectedStatus, FileOutputModel expectedResult, ErrorOutputModel expectedError) throws Exception {
		Mockito.when(alleleService.getAlleles(anyString(), anyString(), any(), anyInt(), anyInt())).thenReturn(Optional.ofNullable(alleles));
		MockHttpServletResponse result = executeRequest(req, MediaType.TEXT_PLAIN);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful()) {
			assertEquals(expectedResult.toResponseEntity().getBody(), result.getContentAsString());
		} else
			assertEquals(expectedError, parseResult(ErrorOutputModel.class, result));
	}
	@ParameterizedTest
	@MethodSource("getAllele_params")
	public void getAllele(MockHttpServletRequestBuilder req, Allele allele, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(alleleService.getAllele(anyString(), anyString(), anyString(), any(), anyLong())).thenReturn(Optional.ofNullable(allele));
		MockHttpServletResponse result = executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful())
			assertEquals(expectedResult, parseResult(GetAlleleOutputModel.class, result));
		else
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("saveAllele_params")
	public void putAllele(MockHttpServletRequestBuilder req, AlleleInputModel input, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		if (input != null)
			Mockito.when(alleleService.saveAllele(any())).thenReturn(ret);
		MockHttpServletResponse result = executeRequest(req, input);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("putAlleles_params")
	public void putAlleles(MockHttpServletRequestBuilder req, Pair<Integer[], String[]> invalids, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(alleleService.saveAllelesOnConflictUpdate(anyString(), anyString(), any(), any())).thenReturn(Optional.ofNullable(invalids));
		MockHttpServletResponse result = executeFileRequest(req);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
		else
			assertEquals(expectedResult, parseResult(BatchOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("postAlleles_params")
	public void postAlleles(MockHttpServletRequestBuilder req, Pair<Integer[], String[]> invalids, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(alleleService.saveAllelesOnConflictSkip(anyString(), anyString(), any(), any())).thenReturn(Optional.ofNullable(invalids));
		MockHttpServletResponse result = executeFileRequest(req);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
		else
			assertEquals(expectedResult, parseResult(BatchOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("deleteAllele_params")
	public void deleteAllele(MockHttpServletRequestBuilder req, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(alleleService.deleteAllele(anyString(), anyString(), anyString(), any())).thenReturn(ret);
		MockHttpServletResponse result = executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
	}

}
