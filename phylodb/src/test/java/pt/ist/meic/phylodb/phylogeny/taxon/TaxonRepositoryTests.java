package pt.ist.meic.phylodb.phylogeny.taxon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaxonRepositoryTests extends TaxonTests{

	@Autowired
	private TaxonRepository repository;

	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2})
	public void findAll_absentTaxonsAndPageZeroToN_emptyList(int page) {
		List<Taxon> actual = repository.findAll(page);
		assertEquals(0, actual.size());
	}

	@ParameterizedTest
	@ValueSource(ints = {1, 2})
	public void findAll_singleTaxonAndPageOneToN_emptyList(int page) {
		arrange(IDS[0]);
		List<Taxon> actual = repository.findAll(page);
		assertEquals(0, actual.size());
	}

	@Test
	public void findAll_singleTaxonAndPageZero_singletonList() {
		arrange(IDS[0]);
		List<Taxon> actual = repository.findAll(0);
		assertEquals(1, actual.size());
		assertEquals(IDS[0], actual.get(0).get_id());
	}

	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2})
	public void findAll_manyTaxonsAndPageZeroToN_nSizedList(int page) {
		arrange(IDS);
		int limit = 2;
		List<Taxon> actual = repository.findAll(page);

		for (int i = 0; i <  actual.size(); i++)
			assertEquals(IDS[i + page * limit], actual.get(i).get_id());
	}

	@ParameterizedTest
	@ValueSource(ints = {-1, -2, -10})
	public void findAll_negativePage_null(int page) {
		List<Taxon> actual = repository.findAll(page);
		assertNull(actual);
	}

	private static Stream<Arguments> validKeyParameters() {
		return Stream.of(Arguments.of(IDS[0]), Arguments.of(IDS[1]));
	}

	@ParameterizedTest
	@MethodSource("validKeyParameters")
	public void find_validKeyAndExistingTaxon_taxon(String key) {
		arrange(key);

		Taxon actual = repository.find(key);

		assertNotNull(actual);
		assertEquals(key, actual.get_id());
	}

	private static Stream<Arguments> validAndNullKeyParameters() {
		return Stream.of(Arguments.of(IDS[0]), null);
	}

	@ParameterizedTest
	@MethodSource("validAndNullKeyParameters")
	public void find_validKeyAndAbsentTaxon_null(String key) {
		Taxon actual = repository.find(key);
		assertNull(actual);
	}

	@ParameterizedTest
	@MethodSource("validKeyParameters")
	public void save_validTaxonAbsentInDB_created(String id) {
		repository.save(new Taxon(id, null));

		Taxon actual = repository.find(id);
		assertNotNull(actual);
	}

	@ParameterizedTest
	@MethodSource("validKeyParameters")
	public void save_validTaxonExistingInDB_updated(String id) {
		arrange(id);
		String description = "test description";
		repository.save(new Taxon(id, description));

		Taxon actual = repository.find(id);
		assertNotNull(actual);
		assertEquals(description, actual.getDescription());
	}

	@Test
	public void save_nullTaxon_unmodified() {
		List<Taxon> before = repository.findAll(0);
		repository.save(null);
		List<Taxon> after = repository.findAll(0);
		assertEquals(before.size(), after.size());
	}

	@ParameterizedTest
	@MethodSource("validKeyParameters")
	public void remove_validKeyAndTaxonExistingInDB_deleted(String id) {
		arrange(id);
		Taxon before = repository.find(id);

		repository.remove(id);

		Taxon after = repository.find(id);
		assertNotNull(before);
		assertNull(after);
	}

	@ParameterizedTest
	@MethodSource("validAndNullKeyParameters")
	public void remove_validOrNullKeyAndTaxonAbsentInDB_unmodified(String key) {
		List<Taxon> before = repository.findAll(0);
		repository.remove(key);
		List<Taxon> after = repository.findAll(0);
		assertEquals(before.size(), after.size());
	}
}
