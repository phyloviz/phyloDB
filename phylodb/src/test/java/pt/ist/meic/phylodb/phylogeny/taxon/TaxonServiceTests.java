package pt.ist.meic.phylodb.phylogeny.taxon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pt.ist.meic.phylodb.ServiceTestsContext;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class TaxonServiceTests extends ServiceTestsContext {

	private static final int LIMIT = 2;
	private static final Taxon[] STATE = new Taxon[]{TAXON1, TAXON2};

	private static Stream<Arguments> getTaxons_params() {
		List<Taxon> expected1 = new ArrayList<Taxon>() {{
			add(STATE[0]);
		}};
		List<Taxon> expected2 = new ArrayList<Taxon>() {{
			add(STATE[0]);
			add(STATE[1]);
		}};
		return Stream.of(Arguments.of(0, Collections.emptyList()),
				Arguments.of(0, expected1),
				Arguments.of(0, expected2),
				Arguments.of(-1, null));
	}

	private static Stream<Arguments> getTaxon_params() {
		return Stream.of(Arguments.of(TAXON1.getPrimaryKey(), 1, TAXON1),
				Arguments.of(TAXON1.getPrimaryKey(), 1, null));
	}

	private static Stream<Arguments> saveTaxon_params() {
		return Stream.of(Arguments.of(STATE[0], true),
				Arguments.of(STATE[1], false),
				Arguments.of(null, false));
	}

	private static Stream<Arguments> deleteTaxon_params() {
		return Stream.of(Arguments.of(STATE[0].getPrimaryKey(), true),
				Arguments.of(STATE[0].getPrimaryKey(), false));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@ParameterizedTest
	@MethodSource("getTaxons_params")
	public void getTaxons(int page, List<Taxon> expected) {
		Mockito.when(taxonRepository.findAllEntities(anyInt(), anyInt())).thenReturn(Optional.ofNullable(expected));
		Optional<List<Taxon>> result = taxonService.getTaxons(page, LIMIT);
		if (expected == null && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertNotNull(expected);
		assertTrue(result.isPresent());
		List<Taxon> users = result.get();
		assertEquals(expected.size(), users.size());
		assertEquals(expected, users);
	}

	@ParameterizedTest
	@MethodSource("getTaxon_params")
	public void getTaxon(String key, long version, Taxon expected) {
		Mockito.when(taxonRepository.find(any(), anyLong())).thenReturn(Optional.ofNullable(expected));
		Optional<Taxon> result = taxonService.getTaxon(key, version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("saveTaxon_params")
	public void saveTaxon(Taxon taxon, boolean expected) {
		Mockito.when(taxonRepository.save(any())).thenReturn(expected);
		boolean result = taxonService.saveTaxon(taxon);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("deleteTaxon_params")
	public void deleteTaxon(String key, boolean expected) {
		Mockito.when(taxonRepository.remove(any())).thenReturn(expected);
		boolean result = taxonService.deleteTaxon(key);
		assertEquals(expected, result);
	}

}
