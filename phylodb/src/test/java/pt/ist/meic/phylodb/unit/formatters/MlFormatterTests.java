package pt.ist.meic.phylodb.unit.formatters;

import javafx.util.Pair;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pt.ist.meic.phylodb.io.formatters.dataset.profile.MlFormatter;
import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.model.Schema;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static pt.ist.meic.phylodb.utils.FileUtils.createFile;
import static pt.ist.meic.phylodb.utils.FileUtils.readFile;

public class MlFormatterTests extends ProfilesFormatterTests {

	private static String[] headers = new String[]{"uvrA", "gyrB", "ftsY", "tuf", "gap"};

	private static Stream<Arguments> emptyListParams() {
		String project = UUID.randomUUID().toString(), dataset = UUID.randomUUID().toString();
		Schema fileSchema = new Schema("taxon", "id", Method.MLST, "description", headers);
		Schema otherSchema = new Schema("taxon", "id", Method.MLST, "description", Arrays.copyOfRange(headers, 0, 2));
		return Stream.of(Arguments.of(project, dataset, otherSchema, "ml-h-d-2.txt", new Integer[] {1, 2, 3}),
				Arguments.of(project, dataset, fileSchema, "ml-h-d-0.txt", new Integer[] {1}),
				Arguments.of(project, dataset, fileSchema, "ml-d-0.txt", new Integer[0]));
	}

	private static Stream<Arguments> nonemptyListParams() {
		String project = UUID.randomUUID().toString(), dataset = UUID.randomUUID().toString();
		Schema schema = new Schema("taxon", "id", Method.MLST, "description", headers);
		String[][] alleles = {{"1", "1", "1", "1", "1"}};
		String[][] alleles1 = {{"1", "1", "1", "1", "1"}, {"4", "1", "1", "3", "3"}};
		String[][] alleles2 = {{"1", "1", "1", "1", "1"}, {"4", "1", "1", "3", "3"}, {"3", "1", "1", "4", "4"}};
		String[][] alleles3 = {{"1", null, "1", "1", "1"}, {"4", "1", "1", "3", "3"}, {"3", "1", "b", "4", "4"}, {"4", "1", "1", "2", "3"}};
		String[][] alleles4 = {{"1", "1", "1", "1", null}};
		Pair<Profile[], Integer[]> expected1 = new Pair<>(profiles(project, dataset, schema, alleles, true), new Integer[] {1});
		Pair<Profile[], Integer[]> expected2 = new Pair<>(profiles(project, dataset, schema, alleles, true), new Integer[0]);
		Pair<Profile[], Integer[]> expected3 = new Pair<>(profiles(project, dataset, schema, alleles1, false), new Integer[] {1});
		Pair<Profile[], Integer[]> expected4 = new Pair<>(profiles(project, dataset, schema, alleles2, true), new Integer[0]);
		Pair<Profile[], Integer[]> expected5 = new Pair<>(profiles(project, dataset, schema, alleles3, false), new Integer[] {1});
		Pair<Profile[], Integer[]> expected6 = new Pair<>(profiles(project, dataset, schema, alleles4, false), new Integer[] {1, 3});
		return Stream.of(Arguments.of(project, dataset, schema, "ml-h-d-1.txt", expected1, true),
				Arguments.of(project, dataset, schema, "ml-d-1.txt", expected2, true),
				Arguments.of(project, dataset, schema, "ml-h-d-2.txt", expected3, false),
				Arguments.of(project, dataset, schema, "ml-d-3.txt", expected4, true),
				Arguments.of(project, dataset, schema, "ml-h-a-4.txt", expected5, false),
				Arguments.of(project, dataset, schema, "ml-h-d-2-d.txt", expected6, false));
	}

	@ParameterizedTest
	@MethodSource("emptyListParams")
	public void parse_emptyList(String project, String dataset, Schema schema, String filename, Integer[] errors) throws IOException {
		MlFormatter formatter = new MlFormatter();
		Pair<List<Profile>, List<Integer>> profiles = formatter.parse(createFile("formatters/ml", filename), project, dataset, schema, " ", false);
		assertEquals(0, profiles.getKey().size());
		assertArrayEquals(errors, profiles.getValue().toArray());
	}

	@ParameterizedTest
	@MethodSource("nonemptyListParams")
	public void parse_nonemptyList(String project, String dataset, Schema schema, String filename, Pair<Profile[], Integer[]> expected, boolean authorized) throws IOException {
		MlFormatter formatter = new MlFormatter();
		Pair<List<Profile>, List<Integer>> result = formatter.parse(createFile("formatters/ml", filename), project, dataset, schema, " ", authorized);
		List<Profile> profiles = result.getKey();
		assertEquals(expected.getKey().length, profiles.size());
		assertArrayEquals(expected.getValue(), result.getValue().toArray());
		for (int i = 0; i < profiles.size(); i++) {
			assertEquals(expected.getKey()[i].getPrimaryKey().getId(), profiles.get(i).getPrimaryKey().getId());
			assertEquals(expected.getKey()[i].getAllelesReferences(), profiles.get(i).getAllelesReferences());
		}
	}

	@Test
	public void format_fileWithHeaders() throws IOException {
		Schema schema = new Schema("taxon", "id", Method.MLST, "description", headers);
		MlFormatter formatter = new MlFormatter();
		String expected = readFile("formatters/ml", "ml-h-d-0.txt");
		String formatted = formatter.format(Lists.emptyList(), schema);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithHeadersAndProfile() throws IOException {
		Schema schema = new Schema("taxon", "id", Method.MLST, "description", headers);
		MlFormatter formatter = new MlFormatter();
		String[][] alleles = {{"1", "1", "1", "1", "1"}};
		String expected = readFile("formatters/ml", "ml-h-d-1.txt");
		String formatted = formatter.format(Arrays.asList(profiles(UUID.randomUUID().toString(), UUID.randomUUID().toString(), schema, alleles, false)), schema);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithHeadersAndProfiles() throws IOException {
		Schema schema = new Schema("taxon", "id", Method.MLST, "description", headers);
		MlFormatter formatter = new MlFormatter();
		String[][] alleles = {{"1", "1", "1", "1", "1"}, {"4", "1", "1", "3", "3"}};
		String expected = readFile("formatters/ml", "ml-h-d-2.txt");
		String formatted = formatter.format(Arrays.asList(profiles(UUID.randomUUID().toString(), UUID.randomUUID().toString(), schema, alleles, false)), schema);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithHeadersAndProfilesWithMissingAlleles() throws IOException {
		Schema schema = new Schema("taxon", "id", Method.MLST, "description", headers);
		MlFormatter formatter = new MlFormatter();
		String[][] alleles = {{"1", "1", "1", "1", null}, {null, "1", null, "3", "3"}};
		String expected = readFile("formatters/ml", "ml-h-d-2-m.txt");
		String formatted = formatter.format(Arrays.asList(profiles(UUID.randomUUID().toString(), UUID.randomUUID().toString(), schema, alleles, false)), schema);
		assertEquals(expected, formatted);
	}

}
