package pt.ist.meic.phylodb.phylogeny.allele;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.neo4j.ogm.model.QueryStatistics;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import pt.ist.meic.phylodb.Test;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.locus.LocusRepository;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.utils.MockResult;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class AlleleServiceTests extends Test {

	@MockBean
	private LocusRepository locusRepository;
	@MockBean
	private AlleleRepository alleleRepository;

	@InjectMocks
	private AlleleService service;

	private static final int LIMIT = 2;
	private static final String taxonId = "t", locusId = "l";
	private static final Allele first = new Allele(taxonId, locusId, "1one", 1, false, "description", null);
	private static final Allele second = new Allele(taxonId, locusId, "2two", 1, false, null, UUID.randomUUID());
	private static final Allele[] state = new Allele[]{first, second};

	private static Stream<Arguments> getAlleles_params() {
		List<Allele> expected1 = new ArrayList<Allele>() {{ add(state[0]); }};
		List<Allele> expected2 = new ArrayList<Allele>() {{ add(state[0]); add(state[1]); }};
		return Stream.of(Arguments.of(0, Collections.emptyList()),
				Arguments.of(0, expected1),
				Arguments.of(0, expected2),
				Arguments.of(-1, null));
	}

	private static Stream<Arguments> getAllele_params() {
		return Stream.of(Arguments.of(first.getPrimaryKey(), 1, first),
				Arguments.of(first.getPrimaryKey(), 1, null));
	}

	private static Stream<Arguments> saveAllele_params() {
		return Stream.of(Arguments.of(state[0], true, new MockResult().queryStatistics()),
				Arguments.of(state[1], true, null),
				Arguments.of(state[1], false, null),
				Arguments.of(null, false, null));
	}

	private static Stream<Arguments> deleteAllele_params() {
		return Stream.of(Arguments.of(state[0].getPrimaryKey(), true),
				Arguments.of(state[0].getPrimaryKey(), false));
	}

	private static Stream<Arguments> saveAll_params() {
		Locus.PrimaryKey key = new Locus.PrimaryKey(taxonId, locusId);
		return Stream.of(Arguments.of(key, true, new MockResult().queryStatistics()),
				Arguments.of(key, true, null),
				Arguments.of(key, false, null));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@ParameterizedTest
	@MethodSource("getAlleles_params")
	public void getAlleles(int page, List<Allele> expected) {
		Mockito.when(alleleRepository.findAll(anyInt(), anyInt(), any())).thenReturn(Optional.ofNullable(expected));
		Optional<List<Allele>> result = service.getAlleles("", "", null, page, LIMIT);
		if(expected == null && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertNotNull(expected);
		assertTrue(result.isPresent());
		List<Allele> users = result.get();
		assertEquals(expected.size(), users.size());
		assertEquals(expected, users);
	}

	@ParameterizedTest
	@MethodSource("getAllele_params")
	public void getAllele(Allele.PrimaryKey key, long version, Allele expected) {
		Mockito.when(alleleRepository.find(any(), anyLong())).thenReturn(Optional.ofNullable(expected));
		Optional<Allele> result = service.getAllele(key.getTaxonId(), key.getLocusId(), key.getId(), key.getProject(), version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("saveAllele_params")
	public void saveAllele(Allele allele, boolean exists, QueryStatistics expected) {
		Mockito.when(locusRepository.exists(any())).thenReturn(exists);
		Mockito.when(alleleRepository.save(any())).thenReturn(Optional.ofNullable(expected));
		boolean result = service.saveAllele(allele);
		assertEquals(expected != null, result);
	}

	@ParameterizedTest
	@MethodSource("deleteAllele_params")
	public void deleteAllele(Allele.PrimaryKey key, boolean expected) {
		Mockito.when(alleleRepository.remove(any())).thenReturn(expected);
		boolean result = service.deleteAllele(key.getTaxonId(), key.getLocusId(), key.getId(), key.getProject());
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("saveAll_params")
	public void saveAllelesOnConflictSkip(Locus.PrimaryKey key, boolean exists, QueryStatistics expected) throws IOException {
		Mockito.when(locusRepository.exists(any())).thenReturn(exists);
		Mockito.when(alleleRepository.saveAll(any(), any(), any(), any(), any())).thenReturn(Optional.ofNullable(expected));
		MockMultipartFile file = new MockMultipartFile("t", "t", "text/plain", new byte[0]);
		boolean result = service.saveAllelesOnConflictSkip(key.getTaxonId(), key.getId(), null, file);
		assertEquals(expected != null, result);
	}

	@ParameterizedTest
	@MethodSource("saveAll_params")
	public void saveAllelesOnConflictUpdate(Locus.PrimaryKey key, boolean exists, QueryStatistics expected) throws IOException {
		Mockito.when(locusRepository.exists(any())).thenReturn(exists);
		Mockito.when(alleleRepository.saveAll(any(), any(), any(), any(), any())).thenReturn(Optional.ofNullable(expected));
		MockMultipartFile file = new MockMultipartFile("t", "t", "text/plain", new byte[0]);
		boolean result = service.saveAllelesOnConflictUpdate(key.getTaxonId(), key.getId(), null, file);
		assertEquals(expected != null, result);

	}

}
