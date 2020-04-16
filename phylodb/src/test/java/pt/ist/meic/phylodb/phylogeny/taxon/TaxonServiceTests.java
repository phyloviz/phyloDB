package pt.ist.meic.phylodb.phylogeny.taxon;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaxonServiceTests extends TaxonTests {
/*
	@Autowired
	private TaxonService service;

	private static Stream<Arguments> validKeyParameters() {
		return Stream.of(Arguments.of(IDS[0]), Arguments.of(IDS[1]));
	}

	private static Stream<Arguments> validAndNullKeyParameters() {
		return Stream.of(Arguments.of(IDS[0]), null);
	}

	private static Stream<Arguments> save_invalidParameters() {
		return Stream.of(Arguments.of(IDS[0], new Taxon(IDS[1], null)),
				Arguments.of(null, new Taxon(IDS[1], null)),
				Arguments.of(IDS[1], null));
	}

	private static Stream<Arguments> remove_invalidParameters() {
		return Stream.of(Arguments.of(IDS[0]), null);
	}

	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2})
	public void findAll_absentTaxonsAndPageZeroToN_emptyList(int page) {
		Optional<List<Taxon>> actual = service.getTaxons(page);
		assertTrue(actual.isPresent());
		assertEquals(0, actual.get().size());
	}

	@ParameterizedTest
	@ValueSource(ints = {1, 2})
	public void findAll_singleTaxonAndPageOneToN_emptyList(int page) {
		arrange(IDS[0]);
		Optional<List<Taxon>> actual = service.getTaxons(page);
		assertTrue(actual.isPresent());
		assertEquals(0, actual.get().size());
	}

	@Test
	public void findAll_singleTaxonAndPageZero_singletonList() {
		arrange(IDS[0]);
		Optional<List<Taxon>> actual = service.getTaxons(0);
		assertTrue(actual.isPresent());
		assertEquals(1, actual.get().size());
		assertEquals(IDS[0], actual.get().get(0).getPrimaryKey());
	}

	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2})
	public void findAll_manyTaxonsAndPageZeroToN_nSizedList(int page) {
		arrange(IDS);
		int limit = 2;
		Optional<List<Taxon>> actual = service.getTaxons(page);

		assertTrue(actual.isPresent());
		for (int i = 0; i < actual.get().size(); i++)
			assertEquals(IDS[i + page * limit], actual.get().get(i).getPrimaryKey());
	}

	@ParameterizedTest
	@ValueSource(ints = {-1, -2, -10})
	public void findAll_negativePage_null(int page) {
		Optional<List<Taxon>> actual = service.getTaxons(page);
		assertFalse(actual.isPresent());
	}

	@ParameterizedTest
	@MethodSource("validKeyParameters")
	public void find_validKeyAndExistingTaxon_taxon(String key) {
		arrange(key);

		Optional<Taxon> actual = service.getTaxon(key);

		assertTrue(actual.isPresent());
		assertEquals(key, actual.get().getPrimaryKey());
	}

	@ParameterizedTest
	@MethodSource("validAndNullKeyParameters")
	public void find_nullOrValidKeyAndAbsentTaxon_null(String key) {
		Optional<Taxon> actual = service.getTaxon(key);
		assertFalse(actual.isPresent());
	}

	@Test
	public void save_validKeyAndTaxonAbsentInDB_created() {
		Taxon taxon = new Taxon(IDS[0], null);

		boolean result = service.saveTaxon(IDS[0], taxon);

		assertTrue(result);
		Optional<Taxon> actual = service.getTaxon(IDS[0]);
		assertTrue(actual.isPresent());
	}

	@Test
	public void save_validKeyAndTaxonExistingInDB_updated() {
		arrange(IDS[0]);
		String description = "test description";

		boolean result = service.saveTaxon(IDS[0], new Taxon(IDS[0], description));

		assertTrue(result);
		Optional<Taxon> actual = service.getTaxon(IDS[0]);
		assertTrue(actual.isPresent());
		assertEquals(description, actual.get().getDescription());
	}

	@ParameterizedTest
	@MethodSource("save_invalidParameters")
	public void save_invalidParameters_unmodified(String key, Taxon taxon) {
		Optional<List<Taxon>> before = service.getTaxons(0);

		boolean result = service.saveTaxon(key, taxon);

		assertFalse(result);
		Optional<List<Taxon>> after = service.getTaxons(0);
		assertTrue(before.isPresent());
		assertTrue(after.isPresent());
		assertEquals(before.get().size(), after.get().size());
	}

	@ParameterizedTest
	@MethodSource("validKeyParameters")
	public void remove_validKeyAndTaxonExistingInDB_deleted(String id) {
		arrange(id);
		Optional<Taxon> before = service.getTaxon(id);

		boolean result = service.deleteTaxon(id);

		assertTrue(result);
		Optional<Taxon> after = service.getTaxon(id);
		assertTrue(before.isPresent());
		assertFalse(after.isPresent());
	}

	@ParameterizedTest
	@MethodSource("remove_invalidParameters")
	public void remove_nullOrValidKeyAndTaxonAbsentInDB_unmodified(String key) {
		Optional<List<Taxon>> before = service.getTaxons(0);

		boolean result = service.deleteTaxon(key);

		assertFalse(result);
		Optional<List<Taxon>> after = service.getTaxons(0);
		assertTrue(before.isPresent());
		assertTrue(after.isPresent());
		assertEquals(before.get().size(), after.get().size());
	}

	@Test
	public void remove_validKeyAndTaxonWithRelationshipsExistingInDB_unmodified() {
		arrangeWithRelationships(IDS[0]);
		Optional<List<Taxon>> before = service.getTaxons(0);

		boolean result = service.deleteTaxon(IDS[0]);

		assertFalse(result);
		Optional<List<Taxon>> after = service.getTaxons(0);
		assertTrue(before.isPresent());
		assertTrue(after.isPresent());
		assertEquals(before.get().size(), after.get().size());
	}
*/
}
