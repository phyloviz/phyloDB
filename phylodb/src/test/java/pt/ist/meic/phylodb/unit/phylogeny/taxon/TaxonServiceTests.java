package pt.ist.meic.phylodb.unit.phylogeny.taxon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pt.ist.meic.phylodb.unit.ServiceTestsContext;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

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

	private static Stream<Arguments> getTaxa_params() {
		VersionedEntity<String> state0 = new VersionedEntity<>(STATE[0].getPrimaryKey(), STATE[0].getVersion(), STATE[0].isDeprecated()),
				state1 = new VersionedEntity<>(STATE[1].getPrimaryKey(), STATE[1].getVersion(), STATE[1].isDeprecated());
		List<VersionedEntity<String>> expected1 = new ArrayList<VersionedEntity<String>>() {{
			add(state0);
		}};
		List<VersionedEntity<String>> expected2 = new ArrayList<VersionedEntity<String>>() {{
			add(state0);
			add(state1);
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
	@MethodSource("getTaxa_params")
	public void getTaxa(int page, List<VersionedEntity<String>> expected) {
		Mockito.when(taxonRepository.findAllEntities(anyInt(), anyInt())).thenReturn(Optional.ofNullable(expected));
		Optional<List<VersionedEntity<String>>> result = taxonService.getTaxa(page, LIMIT);
		if (expected == null && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertNotNull(expected);
		assertTrue(result.isPresent());
		List<VersionedEntity<String>> taxa = result.get();
		assertEquals(expected.size(), taxa.size());
		assertEquals(expected, taxa);
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
