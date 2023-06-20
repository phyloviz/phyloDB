package pt.ist.meic.phylodb.unit.phylogeny.taxon;

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
import pt.ist.meic.phylodb.phylogeny.taxon.model.GetTaxonOutputModel;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.phylogeny.taxon.model.TaxonInputModel;
import pt.ist.meic.phylodb.phylogeny.taxon.model.TaxonOutputModel;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class TaxonControllerTests extends ControllerTestsContext {

	private static Stream<Arguments> getTaxa_params() {
		String uri = "/taxa";
		List<VersionedEntity<String>> taxa = new ArrayList<VersionedEntity<String>>() {{
			add(new VersionedEntity<>("id", 1, false));
		}};
		MockHttpServletRequestBuilder req1 = get(uri).param("page", "0"),
				req2 = get(uri), req3 = get(uri).param("page", "-10");
		List<TaxonOutputModel> result = taxa.stream()
				.map(TaxonOutputModel::new)
				.collect(Collectors.toList());
		return Stream.of(Arguments.of(req1, taxa, HttpStatus.OK, result, null),
				Arguments.of(req1, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req2, taxa, HttpStatus.OK, result, null),
				Arguments.of(req2, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req3, null, HttpStatus.BAD_REQUEST, null, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> getTaxon_params() {
		String uri = "/taxa/%s";
		Taxon taxon = new Taxon("id", "description");
		String key = taxon.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = get(String.format(uri, key)).param("version", "1"),
				req2 = get(String.format(uri, key));
		return Stream.of(Arguments.of(req1, taxon, HttpStatus.OK, new GetTaxonOutputModel(taxon)),
				Arguments.of(req1, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())),
				Arguments.of(req2, taxon, HttpStatus.OK, new GetTaxonOutputModel(taxon)),
				Arguments.of(req2, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())));
	}

	private static Stream<Arguments> saveTaxon_params() {
		String uri = "/taxa/%s";
		Taxon taxon = new Taxon("id", "description");
		String key = taxon.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = put(String.format(uri, key));
		TaxonInputModel input1 = new TaxonInputModel(key, "description"),
				input2 = new TaxonInputModel("different", "description");
		return Stream.of(Arguments.of(req1, input1, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req1, input1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req1, input2, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req1, null, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> deleteTaxon_params() {
		String uri = "/taxa/%s";
		Taxon taxon = new Taxon("id", "description");
		String key = taxon.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = delete(String.format(uri, key));
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
	@MethodSource("getTaxa_params")
	public void getTaxa(MockHttpServletRequestBuilder req, List<VersionedEntity<String>> taxa, HttpStatus expectedStatus, List<TaxonOutputModel> expectedResult, ErrorOutputModel expectedError) throws Exception {
		Mockito.when(taxonService.getTaxa(anyInt(), anyInt())).thenReturn(Optional.ofNullable(taxa));
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
	@MethodSource("getTaxon_params")
	public void getTaxon(MockHttpServletRequestBuilder req, Taxon taxon, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(taxonService.getTaxon(anyString(), anyLong())).thenReturn(Optional.ofNullable(taxon));
		MockHttpServletResponse result = executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful())
			assertEquals(expectedResult, parseResult(GetTaxonOutputModel.class, result));
		else
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("saveTaxon_params")
	public void saveTaxon(MockHttpServletRequestBuilder req, TaxonInputModel input, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		if (input != null)
			Mockito.when(taxonService.saveTaxon(any())).thenReturn(ret);
		MockHttpServletResponse result = executeRequest(req, input);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("deleteTaxon_params")
	public void deleteTaxon(MockHttpServletRequestBuilder req, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(taxonService.deleteTaxon(any())).thenReturn(ret);
		MockHttpServletResponse result = executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
	}

}
