package pt.ist.meic.phylodb.unit.phylogeny.locus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import pt.ist.meic.phylodb.unit.ControllerTestsContext;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.phylogeny.locus.model.GetLocusOutputModel;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.phylogeny.locus.model.LocusInputModel;
import pt.ist.meic.phylodb.phylogeny.locus.model.LocusOutputModel;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class LocusControllerTests extends ControllerTestsContext {

	private static final String TAXONID = TAXON1.getPrimaryKey();

	private static Stream<Arguments> getLoci_params() {
		String uri = "/taxa/%s/loci";
		List<VersionedEntity<Locus.PrimaryKey>> loci = new ArrayList<VersionedEntity<Locus.PrimaryKey>>() {{
			add(new VersionedEntity<>(new Locus.PrimaryKey(TAXONID, "id"), 1, false));
		}};
		MockHttpServletRequestBuilder req1 = get(String.format(uri, TAXONID)).param("page", "0"),
				req2 = get(uri), req3 = get(String.format(uri, TAXONID)).param("page", "-10");
		List<LocusOutputModel> result = loci.stream()
				.map(LocusOutputModel::new)
				.collect(Collectors.toList());
		return Stream.of(Arguments.of(req1, loci, HttpStatus.OK, result, null),
				Arguments.of(req1, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req2, loci, HttpStatus.OK, result, null),
				Arguments.of(req2, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req3, null, HttpStatus.BAD_REQUEST, null, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> getLocus_params() {
		String uri = "/taxa/%s/loci/%s";
		Locus locus = new Locus(TAXONID, "id", "description");
		Locus.PrimaryKey key = locus.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = get(String.format(uri, TAXONID, key.getId())).param("version", "1"),
				req2 = get(String.format(uri, TAXONID, key.getId()));
		return Stream.of(Arguments.of(req1, locus, HttpStatus.OK, new GetLocusOutputModel(locus)),
				Arguments.of(req1, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())),
				Arguments.of(req2, locus, HttpStatus.OK, new GetLocusOutputModel(locus)),
				Arguments.of(req2, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())));
	}

	private static Stream<Arguments> saveLocus_params() {
		String uri = "/taxa/%s/loci/%s";
		Locus locus = new Locus(TAXONID, "id", "description");
		Locus.PrimaryKey key = locus.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = put(String.format(uri, TAXONID, key.getId()));
		LocusInputModel input1 = new LocusInputModel(key.getId(), "description"),
				input2 = new LocusInputModel("different", "description");
		return Stream.of(Arguments.of(req1, input1, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req1, input1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req1, input2, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req1, null, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> deleteLocus_params() {
		String uri = "/taxa/%s/loci/%s";
		Locus locus = new Locus(TAXONID, "id", "description");
		Locus.PrimaryKey key = locus.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = delete(String.format(uri, TAXONID, key.getId()));
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
	@MethodSource("getLoci_params")
	public void getLoci(MockHttpServletRequestBuilder req, List<VersionedEntity<Locus.PrimaryKey>> loci, HttpStatus expectedStatus, List<LocusOutputModel> expectedResult, ErrorOutputModel expectedError) throws Exception {
		Mockito.when(locusService.getLoci(anyString(), anyInt(), anyInt())).thenReturn(Optional.ofNullable(loci));
		MockHttpServletResponse result = executeRequest(req, MediaType.APPLICATION_JSON);
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
	@MethodSource("getLocus_params")
	public void getLocus(MockHttpServletRequestBuilder req, Locus locus, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(locusService.getLocus(anyString(), anyString(), anyLong())).thenReturn(Optional.ofNullable(locus));
		MockHttpServletResponse result = executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful())
			assertEquals(expectedResult, parseResult(GetLocusOutputModel.class, result));
		else
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("saveLocus_params")
	public void saveLocus(MockHttpServletRequestBuilder req, LocusInputModel input, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		if (input != null)
			Mockito.when(locusService.saveLocus(any())).thenReturn(ret);
		MockHttpServletResponse result = executeRequest(req, input);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("deleteLocus_params")
	public void deleteLocus(MockHttpServletRequestBuilder req, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(locusService.deleteLocus(anyString(), anyString())).thenReturn(ret);
		MockHttpServletResponse result = executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
	}

}
