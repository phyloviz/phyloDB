package pt.ist.meic.phylodb.phylogeny.allele;

import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.ServiceTestsContext;
import pt.ist.meic.phylodb.formatters.FastaFormatterTests;
import pt.ist.meic.phylodb.formatters.FormatterTests;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class AlleleServiceTests extends ServiceTestsContext {

	private static final int LIMIT = 2;
	private static final String TAXONID = TAXON1.getPrimaryKey(), LOCUSID = LOCUS1.getPrimaryKey().getId();
	private static final Allele[] STATE = new Allele[]{ALLELE11, ALLELE12P};

	private static Stream<Arguments> getAlleles_params() {
		List<Allele> expected1 = new ArrayList<Allele>() {{
			add(STATE[0]);
		}};
		List<Allele> expected2 = new ArrayList<Allele>() {{
			add(STATE[0]);
			add(STATE[1]);
		}};
		return Stream.of(Arguments.of(0, Collections.emptyList()),
				Arguments.of(0, expected1),
				Arguments.of(0, expected2),
				Arguments.of(-1, null));
	}

	private static Stream<Arguments> getAllele_params() {
		return Stream.of(Arguments.of(ALLELE11.getPrimaryKey(), 1, ALLELE11),
				Arguments.of(ALLELE11.getPrimaryKey(), 1, null));
	}

	private static Stream<Arguments> saveAllele_params() {
		return Stream.of(Arguments.of(STATE[0], true, true),
				Arguments.of(STATE[1], true, false),
				Arguments.of(STATE[1], false, false),
				Arguments.of(null, false, false));
	}

	private static Stream<Arguments> deleteAllele_params() {
		return Stream.of(Arguments.of(STATE[0].getPrimaryKey(), true),
				Arguments.of(STATE[0].getPrimaryKey(), false));
	}

	private static Stream<Arguments> saveAllOnConflictSkip_params() throws IOException {
		Locus.PrimaryKey key = new Locus.PrimaryKey(TAXONID, LOCUSID);
		MultipartFile file1 = FormatterTests.createFile("fasta", "f-2-a.txt");
		MultipartFile file2 = FormatterTests.createFile("fasta", "f-3-d.txt");
		List<Allele> alleles1 = Arrays.asList(FastaFormatterTests.alleles(TAXONID, LOCUSID, null, new String[]{"TCGAGGAACCGCTCGAGAGGTGATCCTGTCG", "TCGAGGAACCGCTCGAGAGGTGATCCTGTCG"}));
		List<Pair<Allele, Boolean>> existsNone = alleles1.stream().map(a -> new Pair<>(a, false)).collect(Collectors.toList());
		List<Pair<Allele, Boolean>> existsNone2 = Collections.singletonList(new Pair<>(new Allele(TAXONID, LOCUSID, "2", "TCGAGGAACCGCTCGAGAGGTGATCCTGTCG", null), false));
		List<Pair<Allele, Boolean>> existsAll1 = alleles1.stream().map(a -> new Pair<>(a, true)).collect(Collectors.toList());
		List<Pair<Allele, Boolean>> existsSome = new ArrayList<>();
		existsSome.add(new Pair<>(alleles1.get(0), true));
		existsSome.add(new Pair<>(alleles1.get(1), false));
		return Stream.of(Arguments.of(key, file1, true, existsNone, true, new Pair<>(new Integer[0], new String[0])),
				Arguments.of(key, file1, true, existsAll1, false, null),
				Arguments.of(key, file1, true, existsSome, true, new Pair<>(new Integer[0], new String[] {"1"})),
				Arguments.of(key, file1, true, existsSome, false, null),
				Arguments.of(key, file1, false, existsSome, true, null),
				Arguments.of(key, file2, true, existsNone2, true, new Pair<>(new Integer[] {2, 3, 4, 5, 6, 7, 13, 14, 15, 16, 17}, new String[0])));
	}

	private static Stream<Arguments> saveAllOnConflictUpdate_params() throws IOException {
		Locus.PrimaryKey key = new Locus.PrimaryKey(TAXONID, LOCUSID);
		MultipartFile file1 = FormatterTests.createFile("fasta", "f-2-a.txt");
		MultipartFile file2 = FormatterTests.createFile("fasta", "f-3-d.txt");
		List<Allele> alleles1 = Arrays.asList(FastaFormatterTests.alleles(TAXONID, LOCUSID, null, new String[]{"TCGAGGAACCGCTCGAGAGGTGATCCTGTCG", "TCGAGGAACCGCTCGAGAGGTGATCCTGTCG"}));
		List<Pair<Allele, Boolean>> existsNone = alleles1.stream().map(a -> new Pair<>(a, false)).collect(Collectors.toList());
		List<Pair<Allele, Boolean>> existsAll1 = alleles1.stream().map(a -> new Pair<>(a, true)).collect(Collectors.toList());
		List<Pair<Allele, Boolean>> existsNone2 = Collections.singletonList(new Pair<>(new Allele(TAXONID, LOCUSID, "2", "TCGAGGAACCGCTCGAGAGGTGATCCTGTCG", null), false));
		List<Pair<Allele, Boolean>> existsSome = new ArrayList<>();
		existsSome.add(new Pair<>(alleles1.get(0), true));
		existsSome.add(new Pair<>(alleles1.get(1), false));
		return Stream.of(Arguments.of(key, file1, true, existsNone, true, new Pair<>(new Integer[0], new String[0])),
				Arguments.of(key, file1, true, existsAll1, true, new Pair<>(new Integer[0], new String[0])),
				Arguments.of(key, file1, true, existsSome, true, new Pair<>(new Integer[0], new String[0])),
				Arguments.of(key, file1, true, existsSome, false, null),
				Arguments.of(key, file1, false, existsSome, true, null),
				Arguments.of(key, file2, true, existsNone2, true, new Pair<>(new Integer[] {2, 3, 4, 5, 6, 7, 13, 14, 15, 16, 17}, new String[0])));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@ParameterizedTest
	@MethodSource("getAlleles_params")
	public void getAlleles(int page, List<Allele> expected) {
		Mockito.when(alleleRepository.findAllEntities(anyInt(), anyInt(), any())).thenReturn(Optional.ofNullable(expected));
		Optional<List<Allele>> result = alleleService.getAlleles(TAXONID, LOCUSID, null, page, LIMIT);
		if (expected == null && !result.isPresent()) {
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
		Optional<Allele> result = alleleService.getAllele(key.getTaxonId(), key.getLocusId(), key.getId(), key.getProjectId(), version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("saveAllele_params")
	public void saveAllele(Allele allele, boolean exists, boolean expected) {
		Mockito.when(locusRepository.exists(any())).thenReturn(exists);
		Mockito.when(alleleRepository.save(any())).thenReturn(expected);
		boolean result = alleleService.saveAllele(allele);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("deleteAllele_params")
	public void deleteAllele(Allele.PrimaryKey key, boolean expected) {
		Mockito.when(alleleRepository.remove(any())).thenReturn(expected);
		boolean result = alleleService.deleteAllele(key.getTaxonId(), key.getLocusId(), key.getId(), key.getProjectId());
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("saveAllOnConflictSkip_params")
	public void saveAllelesOnConflictSkip(Locus.PrimaryKey key, MultipartFile file, boolean locusExists, List<Pair<Allele, Boolean>> canSaves, boolean expected, Pair<Integer[], String[]> invalids) throws IOException {
		Mockito.when(locusRepository.exists(any())).thenReturn(locusExists);
		for (Pair<Allele, Boolean> canSave : canSaves)
			Mockito.when(alleleRepository.exists(canSave.getKey().getPrimaryKey())).thenReturn(canSave.getValue());
		List<Allele> alleles = canSaves.stream().filter(p -> !p.getValue()).map(Pair::getKey).collect(Collectors.toList());
		Mockito.when(alleleRepository.saveAll(alleles)).thenReturn(expected);
		Optional<Pair<Integer[], String[]>> result = alleleService.saveAllelesOnConflictSkip(key.getTaxonId(), key.getId(), null, file);
		if(invalids != null) {
			assertTrue(result.isPresent());
			assertArrayEquals(invalids.getKey(), result.get().getKey());
			assertArrayEquals(invalids.getValue(), result.get().getValue());
		} else
			assertFalse(result.isPresent());
	}

	@ParameterizedTest
	@MethodSource("saveAllOnConflictUpdate_params")
	public void saveAlleles(Locus.PrimaryKey key, MultipartFile file, boolean locusExists, List<Pair<Allele, Boolean>> canSaves, boolean expected, Pair<Integer[], String[]> invalids) throws IOException {
		Mockito.when(locusRepository.exists(any())).thenReturn(locusExists);
		for (Pair<Allele, Boolean> canSave : canSaves)
			Mockito.when(alleleRepository.exists(canSave.getKey().getPrimaryKey())).thenReturn(canSave.getValue());
		List<Allele> alleles = canSaves.stream().map(Pair::getKey).collect(Collectors.toList());
		Mockito.when(alleleRepository.saveAll(alleles)).thenReturn(expected);
		Optional<Pair<Integer[], String[]>> result = alleleService.saveAllelesOnConflictUpdate(key.getTaxonId(), key.getId(), null, file);
		if(invalids != null) {
			assertTrue(result.isPresent());
			assertArrayEquals(invalids.getKey(), result.get().getKey());
			assertArrayEquals(invalids.getValue(), result.get().getValue());
		} else
			assertFalse(result.isPresent());
	}

}
