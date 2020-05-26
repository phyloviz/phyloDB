package pt.ist.meic.phylodb.formatters;

import javafx.util.Pair;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pt.ist.meic.phylodb.io.formatters.dataset.allele.FastaFormatter;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FastaFormatterTests extends FormatterTests {

	public static Allele[] alleles(String taxon, String locus, String project, String[] sequences) {
		return IntStream.range(0, sequences.length)
				.filter(i -> sequences[i] != null)
				.mapToObj(i -> new Allele(taxon, locus, String.valueOf(i + 1), sequences[i], project))
				.toArray(Allele[]::new);
	}

	private static Stream<Arguments> emptyListParams() {
		String taxon = "taxon", locus = "locus";
		String project = UUID.randomUUID().toString();
		return Stream.of(Arguments.of(taxon, locus, project, "f-0-1.txt", new Integer[0]),
				Arguments.of(taxon, locus, project, "f-0-2.txt", new Integer[] {1, 2, 3, 4, 5, 6}));
	}

	private static Stream<Arguments> nonemptyListParams() {
		String taxon = "taxon", locus = "locus";
		String project = UUID.randomUUID().toString();
		String[] alleles = {"TCGAGGAACCGCTCGAGAGGTGATCCTGTCG"};
		String[] alleles2 = {"TCGAGGAACCGCTCGAGAGGTGATCCTGTCG", "TCGAGGAACCGCTCGAGAGGTGATCCTGTCG"};
		String[] alleles3 = {null, "TCGAGGAACCGCTCGAGAGGTGATCCTGTCG"};
		Pair<Allele[], Integer[]> expected1 = new Pair<>(alleles(taxon, locus, project, alleles), new Integer[0]);
		Pair<Allele[], Integer[]> expected2 = new Pair<>(alleles(taxon, locus, project, alleles), new Integer[] {1, 2, 3, 4, 5, 6});
		Pair<Allele[], Integer[]> expected3 = new Pair<>(alleles(taxon, locus, project, alleles2), new Integer[0]);
		Pair<Allele[], Integer[]> expected4 = new Pair<>(alleles(taxon, locus, project, alleles3), new Integer[] {2, 3, 4, 5, 6, 7, 13, 14, 15, 16, 17});
		return Stream.of(Arguments.of(taxon, locus, project, "f-1-1.txt", expected1),
				Arguments.of(taxon, locus, project, "f-1-2.txt", expected2),
				Arguments.of(taxon, locus, project, "f-2-a.txt", expected3),
				Arguments.of(taxon, locus, project, "f-3-d.txt", expected4));
	}

	@ParameterizedTest
	@MethodSource("emptyListParams")
	public void parse_emptyList(String taxon, String locus, String project, String filename, Integer[] errorLines) throws IOException {
		FastaFormatter formatter = new FastaFormatter();
		Pair<List<Allele>, List<Integer>> result = formatter.parse(createFile("fasta", filename), taxon, locus, project);
		assertEquals(0, result.getKey().size());
		assertArrayEquals(errorLines, result.getValue().toArray());
	}

	@ParameterizedTest
	@MethodSource("nonemptyListParams")
	public void parse_nonemptyList(String taxon, String locus, String project, String filename, Pair<Allele[], Integer[]> expected) throws IOException {
		FastaFormatter formatter = new FastaFormatter();
		Pair<List<Allele>, List<Integer>> result = formatter.parse(createFile("fasta", filename), taxon, locus, project);
		List<Allele> alleles = result.getKey();
		assertEquals(expected.getKey().length, alleles.size());
		assertArrayEquals(expected.getValue(), result.getValue().toArray());
		for (int i = 0; i < alleles.size(); i++) {
			assertEquals(expected.getKey()[i].getPrimaryKey().getId(), alleles.get(i).getPrimaryKey().getId());
			assertEquals(expected.getKey()[i].getSequence(), alleles.get(i).getSequence());
		}
	}

	@Test
	public void format_empty() throws IOException {
		FastaFormatter formatter = new FastaFormatter();
		String expected = readFile("fasta", "f-0-1.txt");
		String formatted = formatter.format(Lists.emptyList(), 0);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithOneAllele() throws IOException {
		FastaFormatter formatter = new FastaFormatter();
		String[] alleles = {"TCGAGGAACCGCTCGAGAGGTGATCCTGTCG"};
		String expected = readFile("fasta", "f-1-1.txt");
		String formatted = formatter.format(Arrays.asList(alleles("taxon", "nusA", UUID.randomUUID().toString(), alleles)), 17);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithAlleles() throws IOException {
		FastaFormatter formatter = new FastaFormatter();
		String[] alleles = {"TCGAGGAACCGCTCGAGAGGTGATCCTGTCG", "TCGAGGAACCGCTCGAGAGGTGATCCTGTCG"};
		String expected = readFile("fasta", "f-2-a.txt");
		String formatted = formatter.format(Arrays.asList(alleles("taxon", "nusA", UUID.randomUUID().toString(), alleles)), 17);
		assertEquals(expected, formatted);
	}

}
