package pt.ist.meic.phylodb.unit.formatters;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pt.ist.meic.phylodb.io.formatters.dataset.profile.SnpFormatter;
import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.service.Pair;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static pt.ist.meic.phylodb.utils.FileUtils.createFile;
import static pt.ist.meic.phylodb.utils.FileUtils.readFile;

public class SnpFormatterTests extends ProfilesFormatterTests {

	private static String[] headers = new String[]{"uvrA", "gyrB", "ftsY", "tuf", "gap"};

	private static Stream<Arguments> emptyListParams() {
		String project = UUID.randomUUID().toString(), dataset = UUID.randomUUID().toString();
		Schema fileSchema = new Schema("taxon", "id", Method.MLST, "description", headers);
		Schema otherSchema = new Schema("taxon", "id", Method.MLST, "description", Arrays.copyOfRange(headers, 0, 2));
		return Stream.of(Arguments.of(project, dataset, otherSchema, "snp-d-2.txt", new Integer[] {1, 2}),
				Arguments.of(project, dataset, fileSchema, "snp-d-0.txt", new Integer[0]));
	}

	private static Stream<Arguments> nonemptyListParams() {
		String project = UUID.randomUUID().toString(), dataset = UUID.randomUUID().toString();
		Schema schema = new Schema("taxon", "id", Method.MLST, "description", headers);
		String[][] alleles = {{"1", "0", "0", "1", "0"}};
		String[][] alleles1 = {{"1", "0", "0", "1", "0"}, {"0", "1", "1", "0", "1"}};
		String[][] alleles2 = {{"a", "0", "0", "1", "0"}, {"0", "1", "1", "0", "1"}, {"1", "3", "0", "1", "0"}};
		String[][] alleles3 = {{"1", "0", "0", "1", null}, {null, "1", null, "0", "1"}};
		Pair<Profile[], Integer[]> expected1 = new Pair<>(profiles(project, dataset, schema, alleles, true), new Integer[0]);
		Pair<Profile[], Integer[]> expected2 = new Pair<>(profiles(project, dataset, schema, alleles1, false), new Integer[0]);
		Pair<Profile[], Integer[]> expected3 = new Pair<>(profiles(project, dataset, schema, alleles2, true), new Integer[0]);
		Pair<Profile[], Integer[]> expected4 = new Pair<>(profiles(project, dataset, schema, alleles3, false), new Integer[0]);
		Pair<Profile[], Integer[]> expected5 = new Pair<>(profiles(project, dataset, schema, alleles3, false), new Integer[] {3});
		return Stream.of(Arguments.of(project, dataset, schema, "snp-d-1.txt", expected1, true),
				Arguments.of(project, dataset, schema, "snp-d-2.txt", expected2, false),
				Arguments.of(project, dataset, schema, "snp-a-3.txt", expected3, true),
				Arguments.of(project, dataset, schema, "snp-d-2-m.txt", expected4, false),
				Arguments.of(project, dataset, schema, "snp-d-2-m-d.txt", expected5, false));
	}

	@ParameterizedTest
	@MethodSource("emptyListParams")
	public void parse_emptyList(String project, String dataset, Schema schema, String filename, Integer[] errors) throws IOException {
		SnpFormatter formatter = new SnpFormatter();
		Pair<List<Profile>, List<Integer>> profiles = formatter.parse(createFile("formatters/snp", filename), project, dataset, schema, " ", false);
		assertEquals(0, profiles.getKey().size());
		assertArrayEquals(errors, profiles.getValue().toArray());
	}

	@ParameterizedTest
	@MethodSource("nonemptyListParams")
	public void parse_nonemptyList(String project, String dataset, Schema schema, String filename, Pair<Profile[], Integer[]> expected, boolean authorized) throws IOException {
		SnpFormatter formatter = new SnpFormatter();
		Pair<List<Profile>, List<Integer>> result = formatter.parse(createFile("formatters/snp", filename), project, dataset, schema, " ", authorized);
		List<Profile> profiles = result.getKey();
		assertEquals(expected.getKey().length, profiles.size());
		assertArrayEquals(expected.getValue(), result.getValue().toArray());
		for (int i = 0; i < profiles.size(); i++) {
			assertEquals(expected.getKey()[i].getPrimaryKey().getId(), profiles.get(i).getPrimaryKey().getId());
			assertEquals(expected.getKey()[i].getAllelesReferences(), profiles.get(i).getAllelesReferences());
		}
	}

	@Test
	public void format_empty() throws IOException {
		Schema fileSchema = new Schema("taxon", "id", Method.MLST, "description", headers);
		SnpFormatter formatter = new SnpFormatter();
		String expected = readFile("formatters/snp", "snp-d-0.txt");
		String formatted = formatter.format(Lists.emptyList(), fileSchema);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithOneProfile() throws IOException {
		Schema schema = new Schema("taxon", "id", Method.MLST, "description", headers);
		SnpFormatter formatter = new SnpFormatter();
		String[][] alleles = {{"1", "0", "0", "1", "0"}};
		String expected = readFile("formatters/snp", "snp-d-1.txt");
		String formatted = formatter.format(Arrays.asList(profiles(UUID.randomUUID().toString(), UUID.randomUUID().toString(), schema, alleles, false)), schema);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithProfiles() throws IOException {
		Schema schema = new Schema("taxon", "id", Method.MLST, "description", headers);
		SnpFormatter formatter = new SnpFormatter();
		String[][] alleles = {{"1", "0", "0", "1", "0"}, {"0", "1", "1", "0", "1"}};
		String expected = readFile("formatters/snp", "snp-d-2.txt");
		String formatted = formatter.format(Arrays.asList(profiles(UUID.randomUUID().toString(), UUID.randomUUID().toString(), schema, alleles, false)), schema);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithProfilesAndAllelesMissing() throws IOException {
		Schema schema = new Schema("taxon", "id", Method.MLST, "description", headers);
		SnpFormatter formatter = new SnpFormatter();
		String[][] alleles = {{"1", "0", "0", "1", null}, {null, "1", null, "0", "1"}};
		String expected = readFile("formatters/snp", "snp-d-2-m.txt");
		String formatted = formatter.format(Arrays.asList(profiles(UUID.randomUUID().toString(), UUID.randomUUID().toString(), schema, alleles, false)), schema);
		assertEquals(expected, formatted);
	}

}
