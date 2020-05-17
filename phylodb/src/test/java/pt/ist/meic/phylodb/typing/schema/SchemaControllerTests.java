package pt.ist.meic.phylodb.typing.schema;

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
import pt.ist.meic.phylodb.ControllerTestsContext;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.typing.schema.model.GetSchemaOutputModel;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.typing.schema.model.SchemaInputModel;
import pt.ist.meic.phylodb.typing.schema.model.SchemaOutputModel;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class SchemaControllerTests extends ControllerTestsContext {

	private static final String TAXONID = TAXON1.getPrimaryKey();

	private static Stream<Arguments> getSchemas_params() {
		String uri = "/taxons/%s/schemas";
		List<Schema> loci = new ArrayList<Schema>() {{
			add(SCHEMA1);
			add(SCHEMA2);
		}};
		MockHttpServletRequestBuilder req1 = get(String.format(uri, TAXONID)).param("page", "0"),
				req2 = get(uri), req3 = get(String.format(uri, TAXONID)).param("page", "-10");
		List<SchemaOutputModel> result = loci.stream()
				.map(SchemaOutputModel::new)
				.collect(Collectors.toList());
		return Stream.of(Arguments.of(req1, loci, HttpStatus.OK, result, null),
				Arguments.of(req1, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req2, loci, HttpStatus.OK, result, null),
				Arguments.of(req2, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req3, null, HttpStatus.BAD_REQUEST, null, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> getSchema_params() {
		String uri = "/taxons/%s/schemas/%s";
		Schema.PrimaryKey key = SCHEMA1.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = get(String.format(uri, TAXONID, key.getId())).param("version", "1"),
				req2 = get(String.format(uri, TAXONID, key.getId()));
		return Stream.of(Arguments.of(req1, SCHEMA1, HttpStatus.OK, new GetSchemaOutputModel(SCHEMA1)),
				Arguments.of(req1, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())),
				Arguments.of(req2, SCHEMA1, HttpStatus.OK, new GetSchemaOutputModel(SCHEMA1)),
				Arguments.of(req2, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())));
	}

	private static Stream<Arguments> saveSchema_params() {
		String uri = "/taxons/%s/schemas/%s";
		Schema.PrimaryKey key = SCHEMA1.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = put(String.format(uri, TAXONID, key.getId()));
		SchemaInputModel input1 = new SchemaInputModel(TAXONID, key.getId(), Method.MLST.getName(), null, new String[]{LOCUS1.getPrimaryKey().getId(), LOCUS2.getPrimaryKey().getId()}),
				input2 = new SchemaInputModel("different", "description", Method.MLST.getName(), null, new String[]{LOCUS1.getPrimaryKey().getId(), LOCUS2.getPrimaryKey().getId()});
		return Stream.of(Arguments.of(req1, input1, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req1, input1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req1, input2, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req1, null, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> deleteSchema_params() {
		String uri = "/taxons/%s/schemas/%s";
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
	@MethodSource("getSchemas_params")
	public void getSchemas(MockHttpServletRequestBuilder req, List<Schema> schemas, HttpStatus expectedStatus, List<SchemaOutputModel> expectedResult, ErrorOutputModel expectedError) throws Exception {
		Mockito.when(schemaService.getSchemas(anyString(), anyInt(), anyInt())).thenReturn(Optional.ofNullable(schemas));
		MockHttpServletResponse result = http.executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful()) {
			List<Map<String, Object>> parsed = http.parseResult(List.class, result);
			assertEquals(expectedResult.size(), parsed.size());
			if (expectedResult.size() > 0) {
				for (int i = 0; i < expectedResult.size(); i++) {
					Map<String, Object> p = parsed.get(i);
					assertEquals(expectedResult.get(i).getId(), p.get("id"));
					assertEquals(expectedResult.get(i).getVersion(), Long.parseLong(p.get("version").toString()));
					assertEquals(expectedResult.get(i).isDeprecated(), p.get("deprecated"));
				}
			}
		} else
			assertEquals(expectedError, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("getSchema_params")
	public void getSchema(MockHttpServletRequestBuilder req, Schema schema, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(schemaService.getSchema(anyString(), anyString(), anyLong())).thenReturn(Optional.ofNullable(schema));
		MockHttpServletResponse result = http.executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful())
			assertEquals(expectedResult, http.parseResult(GetSchemaOutputModel.class, result));
		else
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("saveSchema_params")
	public void saveSchema(MockHttpServletRequestBuilder req, SchemaInputModel input, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(schemaService.saveSchema(any())).thenReturn(ret);
		MockHttpServletResponse result = http.executeRequest(req, input);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("deleteSchema_params")
	public void deleteSchema(MockHttpServletRequestBuilder req, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(schemaService.deleteSchema(anyString(), anyString())).thenReturn(ret);
		MockHttpServletResponse result = http.executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

}
