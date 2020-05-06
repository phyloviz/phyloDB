package pt.ist.meic.phylodb.typing.schema;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import pt.ist.meic.phylodb.Test;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.security.authentication.AuthenticationInterceptor;
import pt.ist.meic.phylodb.security.authorization.AuthorizationInterceptor;
import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.typing.schema.model.GetSchemaOutputModel;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.typing.schema.model.SchemaInputModel;
import pt.ist.meic.phylodb.typing.schema.model.SchemaOutputModel;
import pt.ist.meic.phylodb.utils.MockHttp;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class SchemaControllerTests extends Test {

	private static final String taxonId = "t";
	private static final Locus locus1 = new Locus(taxonId, "1", 1, false, "description");
	private static final Locus locus2 = new Locus(taxonId, "2", 1, false, null);
	private static final Schema schema1 = new Schema(taxonId, "1one", 1, false, Method.MLST, null,
			Arrays.asList(new Entity<>(locus1.getPrimaryKey(), locus1.getVersion(), locus1.isDeprecated()), new Entity<>(locus2.getPrimaryKey(), locus2.getVersion(), locus2.isDeprecated())));
	private static final Schema schema2 = new Schema(taxonId, "2two", 1, false, Method.MLST, null,
			Arrays.asList(new Entity<>(locus2.getPrimaryKey(), locus2.getVersion(), locus2.isDeprecated()), new Entity<>(locus1.getPrimaryKey(), locus1.getVersion(), locus1.isDeprecated())));
	@InjectMocks
	private SchemaController controller;
	@MockBean
	private SchemaService service;
	@MockBean
	private AuthenticationInterceptor authenticationInterceptor;
	@MockBean
	private AuthorizationInterceptor authorizationInterceptor;
	@Autowired
	private MockHttp http;

	private static Stream<Arguments> getSchemas_params() {
		String uri = "/taxons/%s/schemas";
		List<Schema> loci = new ArrayList<Schema>() {{
			add(schema1);
			add(schema2);
		}};
		MockHttpServletRequestBuilder req1 = get(String.format(uri, taxonId)).param("page", "0"),
				req2 = get(uri), req3 = get(String.format(uri, taxonId)).param("page", "-10");
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
		Schema.PrimaryKey key = schema1.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = get(String.format(uri, taxonId, key.getId())).param("version", "1"),
				req2 = get(String.format(uri, taxonId, key.getId()));
		return Stream.of(Arguments.of(req1, schema1, HttpStatus.OK, new GetSchemaOutputModel(schema1)),
				Arguments.of(req1, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())),
				Arguments.of(req2, schema1, HttpStatus.OK, new GetSchemaOutputModel(schema1)),
				Arguments.of(req2, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())));
	}

	private static Stream<Arguments> saveSchema_params() {
		String uri = "/taxons/%s/schemas/%s";
		Schema.PrimaryKey key = schema1.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = put(String.format(uri, taxonId, key.getId()));
		SchemaInputModel input1 = new SchemaInputModel(taxonId, key.getId(), Method.MLST.getName(), null, new String[]{locus1.getPrimaryKey().getId(), locus2.getPrimaryKey().getId()}),
				input2 = new SchemaInputModel("different", "description", Method.MLST.getName(), null, new String[]{locus1.getPrimaryKey().getId(), locus2.getPrimaryKey().getId()});
		return Stream.of(Arguments.of(req1, input1, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req1, input1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req1, input2, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req1, null, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> deleteSchema_params() {
		String uri = "/taxons/%s/schemas/%s";
		Locus locus = new Locus(taxonId, "id", "description");
		Locus.PrimaryKey key = locus.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = delete(String.format(uri, taxonId, key.getId()));
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
		Mockito.when(service.getSchemas(anyString(), anyInt(), anyInt())).thenReturn(Optional.ofNullable(schemas));
		MockHttpServletResponse result = http.executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful()) {
			List<Map<String, Object>> parsed = http.parseResult(List.class, result);
			assertEquals(expectedResult.size(), parsed.size());
			if (expectedResult.size() > 0) {
				for (int i = 0; i < expectedResult.size(); i++) {
					Map<String, Object> p = parsed.get(i);
					assertEquals(expectedResult.get(i).getTaxon_id(), p.get("taxon_id"));
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
		Mockito.when(service.getSchema(anyString(), anyString(), anyLong())).thenReturn(Optional.ofNullable(schema));
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
		Mockito.when(service.saveSchema(any())).thenReturn(ret);
		MockHttpServletResponse result = http.executeRequest(req, input);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("deleteSchema_params")
	public void deleteSchema(MockHttpServletRequestBuilder req, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(service.deleteSchema(anyString(), anyString())).thenReturn(ret);
		MockHttpServletResponse result = http.executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

}
