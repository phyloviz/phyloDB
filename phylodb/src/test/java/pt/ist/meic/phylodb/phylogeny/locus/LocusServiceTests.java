package pt.ist.meic.phylodb.phylogeny.locus;

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
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.phylogeny.taxon.TaxonRepository;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.utils.MockResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class LocusServiceTests extends Test {

	@MockBean
	private TaxonRepository taxonRepository;

	@MockBean
	private LocusRepository locusRepository;

	@InjectMocks
	private LocusService service;

	private static final int LIMIT = 2;
	private static final Taxon taxon = new Taxon("t", 1, false, null);
	private static final Locus first = new Locus(taxon.getPrimaryKey(), "1one", 1, false, "description");
	private static final Locus second = new Locus(taxon.getPrimaryKey(), "2two", 1, false, null);
	private static final Locus[] state = new Locus[]{first, second};

	private static Stream<Arguments> getLoci_params() {
		List<Locus> expected1 = new ArrayList<Locus>() {{ add(state[0]); }};
		List<Locus> expected2 = new ArrayList<Locus>() {{ add(state[0]); add(state[1]); }};
		return Stream.of(Arguments.of(0, Collections.emptyList()),
				Arguments.of(0, expected1),
				Arguments.of(0, expected2),
				Arguments.of(-1, null));
	}

	private static Stream<Arguments> getLocus_params() {
		return Stream.of(Arguments.of(first.getPrimaryKey(), 1, first),
				Arguments.of(first.getPrimaryKey(), 1, null));
	}

	private static Stream<Arguments> saveLocus_params() {
		return Stream.of(Arguments.of(state[0], true, new MockResult().queryStatistics()),
				Arguments.of(state[1], true, null),
				Arguments.of(state[1], false, null),
				Arguments.of(null, false, null));
	}

	private static Stream<Arguments> deleteLocus_params() {
		return Stream.of(Arguments.of(state[0].getPrimaryKey(), true),
				Arguments.of(state[0].getPrimaryKey(), false));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@ParameterizedTest
	@MethodSource("getLoci_params")
	public void getLoci(int page, List<Locus> expected) {
		Mockito.when(locusRepository.findAll(anyInt(), anyInt(), any())).thenReturn(Optional.ofNullable(expected));
		Optional<List<Locus>> result = service.getLoci(taxon.getPrimaryKey(), page, LIMIT);
		if(expected == null && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertNotNull(expected);
		assertTrue(result.isPresent());
		List<Locus> users = result.get();
		assertEquals(expected.size(), users.size());
		assertEquals(expected, users);
	}

	@ParameterizedTest
	@MethodSource("getLocus_params")
	public void getLocus(Locus.PrimaryKey key, long version, Locus expected) {
		Mockito.when(locusRepository.find(any(), anyLong())).thenReturn(Optional.ofNullable(expected));
		Optional<Locus> result = service.getLocus(key.getTaxonId(), key.getId(), version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("saveLocus_params")
	public void saveLocus(Locus locus, boolean exists, QueryStatistics expected) {
		Mockito.when(taxonRepository.exists(any())).thenReturn(exists);
		Mockito.when(locusRepository.save(any())).thenReturn(Optional.ofNullable(expected));
		boolean result = service.saveLocus(locus);
		assertEquals(expected != null, result);
	}

	@ParameterizedTest
	@MethodSource("deleteLocus_params")
	public void deleteLocus(Locus.PrimaryKey key, boolean expected) {
		Mockito.when(locusRepository.remove(any())).thenReturn(expected);
		boolean result = service.deleteLocus(key.getTaxonId(), key.getId());
		assertEquals(expected, result);
	}

}
