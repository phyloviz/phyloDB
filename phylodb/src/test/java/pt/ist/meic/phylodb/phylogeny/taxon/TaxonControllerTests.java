package pt.ist.meic.phylodb.phylogeny.taxon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.phylogeny.taxon.model.GetTaxonOutputModel;
import pt.ist.meic.phylodb.phylogeny.taxon.model.GetTaxonsOutputModel;
import pt.ist.meic.phylodb.phylogeny.taxon.model.TaxonInputModel;
import pt.ist.meic.phylodb.utils.MockHttp;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaxonControllerTests extends TaxonTests {

	@Autowired
	private MockHttp http;

	private static Stream<Arguments> getTaxons_validKeyParameters() {
		return Stream.of(Arguments.of("0"), Arguments.of(""), null);
	}

	private static Stream<Arguments> saveTaxon_invalidParameters() {
		return Stream.of(Arguments.of("teste", new TaxonInputModel("id", null)),
				Arguments.of("teste", null));
	}

	@ParameterizedTest
	@MethodSource("getTaxons_validKeyParameters")
	public void getTaxons_validPageAndAbsentTaxonsInDB_ok(String page) throws Exception {
		String uri = "/taxons";
		if (page != null)
			uri = String.format(uri + "?page=%s", page);

		MockHttpServletResponse response = http.get(uri);

		assertEquals(HttpStatus.OK.value(), response.getStatus());
		GetTaxonsOutputModel result = http.parseResult(GetTaxonsOutputModel.class, response);
		assertEquals(0, result.getTaxons().size());
	}

	@ParameterizedTest
	@MethodSource("getTaxons_validKeyParameters")
	public void getTaxons_validPageAndExistingTaxonsInDB_ok(String page) throws Exception {
		String uri = "/taxons";
		if (page != null)
			uri = String.format(uri + "?page=%s", page);
		arrange(IDS[0], IDS[1]);

		MockHttpServletResponse response = http.get(uri);

		assertEquals(HttpStatus.OK.value(), response.getStatus());
		GetTaxonsOutputModel result = http.parseResult(GetTaxonsOutputModel.class, response);
		assertEquals(2, result.getTaxons().size());
		assertEquals(IDS[0], result.getTaxons().get(0).getId());
		assertEquals(IDS[1], result.getTaxons().get(1).getId());
	}

	@ParameterizedTest
	@ValueSource(strings = {"-1", "-500"})
	public void getTaxons_invalidPage_badRequest(String page) throws Exception {
		String uri = "/taxons";
		if (page != null)
			uri = String.format(uri + "?page=%s", page);

		MockHttpServletResponse response = http.get(uri);

		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		ErrorOutputModel result = http.parseResult(ErrorOutputModel.class, response);
		assertEquals(Problem.BAD_REQUEST, result.getMessage());
	}

	@Test
	public void getTaxon_existingTaxonInDB_ok() throws Exception {
		String uri = "/taxons/" + IDS[0];
		arrange(IDS[0]);

		MockHttpServletResponse response = http.get(uri);

		assertEquals(HttpStatus.OK.value(), response.getStatus());
		GetTaxonOutputModel result = http.parseResult(GetTaxonOutputModel.class, response);
		assertEquals(IDS[0], result.getId());
	}

	@Test
	public void getTaxon_absentTaxonInDB_notFound() throws Exception {
		String uri = "/taxons/" + IDS[0];

		MockHttpServletResponse response = http.get(uri);

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
		ErrorOutputModel result = http.parseResult(ErrorOutputModel.class, response);
		assertEquals(Problem.NOT_FOUND, result.getMessage());
	}

	@Test
	public void saveTaxon_validKeyAndAbsentTaxonInDB_noContent() throws Exception {
		String uri = "/taxons/" + IDS[0];

		MockHttpServletResponse response = http.put(uri, new TaxonInputModel(IDS[0], null));

		assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
	}

	@Test
	public void saveTaxon_validKeyAndExistingTaxonInDB_noContent() throws Exception {
		String uri = "/taxons/" + IDS[0];
		arrange(IDS[0]);

		MockHttpServletResponse response = http.put(uri, new TaxonInputModel(IDS[0], null));

		assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
	}

	@ParameterizedTest
	@MethodSource("saveTaxon_invalidParameters")
	public void saveTaxon_invalidParameters_badRequest(String id, TaxonInputModel inputModel) throws Exception {
		String uri = "/taxons/" + id;

		MockHttpServletResponse response = http.put(uri, inputModel);

		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		ErrorOutputModel result = http.parseResult(ErrorOutputModel.class, response);
		assertEquals(Problem.BAD_REQUEST, result.getMessage());
	}

	@Test
	public void deleteTaxon_validKeyAndExistingTaxonInDB_noContent() throws Exception {
		String uri = "/taxons/" + IDS[0];
		arrange(IDS[0]);

		MockHttpServletResponse response = http.delete(uri);

		assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
	}

	@Test
	public void deleteTaxon_validKeyAndAbsentTaxonInDB_unauthorized() throws Exception {
		String uri = "/taxons/" + IDS[0];

		MockHttpServletResponse response = http.delete(uri);

		assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
		ErrorOutputModel result = http.parseResult(ErrorOutputModel.class, response);
		assertEquals(Problem.UNAUTHORIZED, result.getMessage());
	}

	@Test
	public void deleteTaxon_validKeyAndExistingTaxonWithRelationshipsInDB_unauthorized() throws Exception {
		String uri = "/taxons/" + IDS[0];
		arrangeWithRelationships(IDS[0]);

		MockHttpServletResponse response = http.delete(uri);

		assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
		ErrorOutputModel result = http.parseResult(ErrorOutputModel.class, response);
		assertEquals(Problem.UNAUTHORIZED, result.getMessage());
	}

}
