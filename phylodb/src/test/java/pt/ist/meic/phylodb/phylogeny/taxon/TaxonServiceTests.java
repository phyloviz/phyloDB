package pt.ist.meic.phylodb.phylogeny.taxon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.neo4j.ogm.model.QueryStatistics;
import org.springframework.boot.test.mock.mockito.MockBean;
import pt.ist.meic.phylodb.Test;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.utils.MockResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class TaxonServiceTests extends Test {

	private static final int LIMIT = 2;
	private static final Taxon first = new Taxon("1one", 1, false, "description");
	private static final Taxon second = new Taxon("2two", 1, false, null);
	private static final Taxon[] state = new Taxon[]{first, second};
	@MockBean
	private TaxonRepository repository;
	@InjectMocks
	private TaxonService service;

	private static Stream<Arguments> getTaxons_params() {
		List<Taxon> expected1 = new ArrayList<Taxon>() {{
			add(state[0]);
		}};
		List<Taxon> expected2 = new ArrayList<Taxon>() {{
			add(state[0]);
			add(state[1]);
		}};
		return Stream.of(Arguments.of(0, Collections.emptyList()),
				Arguments.of(0, expected1),
				Arguments.of(0, expected2),
				Arguments.of(-1, null));
	}

	private static Stream<Arguments> getTaxon_params() {
		return Stream.of(Arguments.of(first.getPrimaryKey(), 1, first),
				Arguments.of(first.getPrimaryKey(), 1, null));
	}

	private static Stream<Arguments> saveTaxon_params() {
		return Stream.of(Arguments.of(state[0], new MockResult().queryStatistics()),
				Arguments.of(state[1], null),
				Arguments.of(null, null));
	}

	private static Stream<Arguments> deleteTaxon_params() {
		return Stream.of(Arguments.of(state[0].getPrimaryKey(), true),
				Arguments.of(state[0].getPrimaryKey(), false));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@ParameterizedTest
	@MethodSource("getTaxons_params")
	public void getTaxons(int page, List<Taxon> expected) {
		Mockito.when(repository.findAll(anyInt(), anyInt())).thenReturn(Optional.ofNullable(expected));
		Optional<List<Taxon>> result = service.getTaxons(page, LIMIT);
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
		Mockito.when(repository.find(any(), anyLong())).thenReturn(Optional.ofNullable(expected));
		Optional<Taxon> result = service.getTaxon(key, version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("saveTaxon_params")
	public void saveTaxon(Taxon taxon, QueryStatistics expected) {
		Mockito.when(repository.save(any())).thenReturn(Optional.ofNullable(expected));
		boolean result = service.saveTaxon(taxon);
		assertEquals(expected != null, result);
	}

	@ParameterizedTest
	@MethodSource("deleteTaxon_params")
	public void deleteTaxon(String key, boolean expected) {
		Mockito.when(repository.remove(any())).thenReturn(expected);
		boolean result = service.deleteTaxon(key);
		assertEquals(expected, result);
	}

}
