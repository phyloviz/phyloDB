package pt.ist.meic.phylodb.formatters;

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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FastaFormatterTests extends FormatterTests {

	public static Allele[] alleles(String taxon, String locus, UUID project, String[] sequences) {
		return IntStream.range(0, sequences.length)
				.filter(i -> sequences[i] != null)
				.mapToObj(i -> new Allele(taxon, locus, "nusA_" + (i + 1), sequences[i], project))
				.toArray(Allele[]::new);
	}

	private static Stream<Arguments> emptyListParams() {
		String taxon = "taxon", locus = "locus";
		UUID project = UUID.randomUUID();
		return Stream.of(Arguments.of(taxon, locus, project, "f-0-1.txt"),
				Arguments.of(taxon, locus, project, "f-0-2.txt"));
	}

	private static Stream<Arguments> nonemptyListParams() {
		String taxon = "taxon", locus = "locus";
		UUID project = UUID.randomUUID();
		String[] alleles = {"TCGAGGAACCGCTCGAGAGGTGATCCTGTCG"};
		String[] alleles2 = {"TCGAGGAACCGCTCGAGAGGTGATCCTGTCG", "TCGAGGAACCGCTCGAGAGGTGATCCTGTCG"};
		String[] alleles3 = {null, "TCGAGGAACCGCTCGAGAGGTGATCCTGTCG"};
		return Stream.of(Arguments.of(taxon, locus, project, "f-1-1.txt", alleles(taxon, locus, project, alleles)),
				Arguments.of(taxon, locus, project, "f-1-2.txt", alleles(taxon, locus, project, alleles)),
				Arguments.of(taxon, locus, project, "f-2-a.txt", alleles(taxon, locus, project, alleles2)),
				Arguments.of(taxon, locus, project, "f-3-d.txt", alleles(taxon, locus, project, alleles3)));
	}

	@ParameterizedTest
	@MethodSource("emptyListParams")
	public void parse_emptyList(String taxon, String locus, UUID project, String filename) throws IOException {
		FastaFormatter formatter = new FastaFormatter();
		List<Allele> alleles = formatter.parse(createFile("fasta", filename), taxon, locus, project);
		assertEquals(0, alleles.size());
	}

	@ParameterizedTest
	@MethodSource("nonemptyListParams")
	public void parse_nonemptyList(String taxon, String locus, UUID project, String filename, Allele[] expected) throws IOException {
		FastaFormatter formatter = new FastaFormatter();
		List<Allele> alleles = formatter.parse(createFile("fasta", filename), taxon, locus, project);
		assertEquals(expected.length, alleles.size());
		for (int i = 0; i < alleles.size(); i++) {
			assertEquals(expected[i].getPrimaryKey().getId(), alleles.get(i).getPrimaryKey().getId());
			assertEquals(expected[i].getSequence(), alleles.get(i).getSequence());
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
		String formatted = formatter.format(Arrays.asList(alleles("taxon", "locus", UUID.randomUUID(), alleles)), 17);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithAlleles() throws IOException {
		FastaFormatter formatter = new FastaFormatter();
		String[] alleles = {"TCGAGGAACCGCTCGAGAGGTGATCCTGTCG", "TCGAGGAACCGCTCGAGAGGTGATCCTGTCG"};
		String expected = readFile("fasta", "f-2-a.txt");
		String formatted = formatter.format(Arrays.asList(alleles("taxon", "locus", UUID.randomUUID(), alleles)), 17);
		assertEquals(expected, formatted);
	}

}
