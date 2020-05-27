package pt.ist.meic.phylodb.phylogeny.locus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pt.ist.meic.phylodb.ServiceTestsContext;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class LocusServiceTests extends ServiceTestsContext {

	private static final int LIMIT = 2;
	private static final Locus[] STATE = new Locus[]{LOCUS1, LOCUS2};

	private static Stream<Arguments> getLoci_params() {
		List<Locus> expected1 = new ArrayList<Locus>() {{
			add(STATE[0]);
		}};
		List<Locus> expected2 = new ArrayList<Locus>() {{
			add(STATE[0]);
			add(STATE[1]);
		}};
		return Stream.of(Arguments.of(0, Collections.emptyList()),
				Arguments.of(0, expected1),
				Arguments.of(0, expected2),
				Arguments.of(-1, null));
	}

	private static Stream<Arguments> getLocus_params() {
		return Stream.of(Arguments.of(LOCUS1.getPrimaryKey(), 1, LOCUS1),
				Arguments.of(LOCUS1.getPrimaryKey(), 1, null));
	}

	private static Stream<Arguments> saveLocus_params() {
		return Stream.of(Arguments.of(STATE[0], true, true),
				Arguments.of(STATE[1], true, false),
				Arguments.of(STATE[1], false, false),
				Arguments.of(null, false, false));
	}

	private static Stream<Arguments> deleteLocus_params() {
		return Stream.of(Arguments.of(STATE[0].getPrimaryKey(), true),
				Arguments.of(STATE[0].getPrimaryKey(), false));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@ParameterizedTest
	@MethodSource("getLoci_params")
	public void getLoci(int page, List<Locus> expected) {
		Mockito.when(locusRepository.findAllEntities(anyInt(), anyInt(), any())).thenReturn(Optional.ofNullable(expected));
		Optional<List<Locus>> result = locusService.getLoci(TAXON1.getPrimaryKey(), page, LIMIT);
		if (expected == null && !result.isPresent()) {
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
		Optional<Locus> result = locusService.getLocus(key.getTaxonId(), key.getId(), version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("saveLocus_params")
	public void saveLocus(Locus locus, boolean exists, boolean expected) {
		Mockito.when(taxonRepository.exists(any())).thenReturn(exists);
		Mockito.when(locusRepository.save(any())).thenReturn(expected);
		boolean result = locusService.saveLocus(locus);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("deleteLocus_params")
	public void deleteLocus(Locus.PrimaryKey key, boolean expected) {
		Mockito.when(locusRepository.remove(any())).thenReturn(expected);
		boolean result = locusService.deleteLocus(key.getTaxonId(), key.getId());
		assertEquals(expected, result);
	}

}
