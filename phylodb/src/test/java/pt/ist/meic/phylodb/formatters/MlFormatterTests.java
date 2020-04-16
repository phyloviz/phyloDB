package pt.ist.meic.phylodb.formatters;

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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MlFormatterTests extends ProfilesFormatterTests {

	private static String[] headers = new String[]{"uvrA", "gyrB", "ftsY", "tuf", "gap"};

	private static Stream<Arguments> emptyListParams() {
		UUID project = UUID.randomUUID(), dataset = UUID.randomUUID();
		Schema fileSchema = new Schema("taxon", "id", Method.MLST, "description", headers);
		Schema otherSchema = new Schema("taxon", "id", Method.MLST, "description", Arrays.copyOfRange(headers, 0, 2));
		return Stream.of(Arguments.of(project, dataset, otherSchema, "ml-h-d-2.txt"),
				Arguments.of(project, dataset, fileSchema, "ml-h-d-0.txt"),
				Arguments.of(project, dataset, fileSchema, "ml-d-0.txt"));
	}

	private static Stream<Arguments> nonemptyListParams() {
		UUID project = UUID.randomUUID(), dataset = UUID.randomUUID();
		Schema fileSchema = new Schema("taxon", "id", Method.MLST, "description", headers);
		String[][] alleles = {{"1", "1", "1", "1", "1"}};
		String[][] alleles1 = {{"1", "1", "1", "1", "1"}, {"4", "1", "1", "3", "3"}};
		String[][] alleles2 = {{"1", "1", "1", "1", "1"}, {"4", "1", "1", "3", "3"}, {"3", "1", "1", "4", "4"}};
		String[][] alleles3 = {{}, {"4", "1", "1", "3", "3"}, {}, {"4", "1", "1", "2", "3"}};
		return Stream.of(Arguments.of(project, dataset, fileSchema, "ml-h-d-1.txt", profiles(project, dataset, alleles)),
				Arguments.of(project, dataset, fileSchema, "ml-d-1.txt", profiles(project, dataset, alleles)),
				Arguments.of(project, dataset, fileSchema, "ml-h-d-2.txt", profiles(project, dataset, alleles1)),
				Arguments.of(project, dataset, fileSchema, "ml-d-3.txt", profiles(project, dataset, alleles2)),
				Arguments.of(project, dataset, fileSchema, "ml-h-a-4.txt", profiles(project, dataset, alleles3)));
	}

	@ParameterizedTest
	@MethodSource("emptyListParams")
	public void parse_emptyList(UUID project, UUID dataset, Schema schema, String filename) throws IOException {
		MlFormatter formatter = new MlFormatter();
		List<Profile> profiles = formatter.parse(createFile("ml", filename), project, dataset, schema);
		assertEquals(0, profiles.size());
	}

	@ParameterizedTest
	@MethodSource("nonemptyListParams")
	public void parse_nonemptyList(UUID project, UUID dataset, Schema schema, String filename, Profile[] expected) throws IOException {
		MlFormatter formatter = new MlFormatter();
		List<Profile> profiles = formatter.parse(createFile("ml", filename), project, dataset, schema);
		assertEquals(expected.length, profiles.size());
		for (int i = 0; i < profiles.size(); i++) {
			assertEquals(expected[i].getPrimaryKey().getId(), profiles.get(i).getPrimaryKey().getId());
			assertEquals(expected[i].getAllelesIds(), profiles.get(i).getAllelesIds());
		}
	}

	@Test
	public void format_fileWithHeaders() throws IOException {
		Schema fileSchema = new Schema("taxon", "id", Method.MLST, "description", headers);
		MlFormatter formatter = new MlFormatter();
		String expected = readFile("ml", "ml-h-d-0.txt");
		String formatted = formatter.format(Lists.emptyList(), fileSchema);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithHeadersAndProfile() throws IOException {
		Schema fileSchema = new Schema("taxon", "id", Method.MLST, "description", headers);
		MlFormatter formatter = new MlFormatter();
		String[][] alleles = {{"1", "1", "1", "1", "1"}};
		String expected = readFile("ml", "ml-h-d-1.txt");
		String formatted = formatter.format(Arrays.asList(profiles(UUID.randomUUID(), UUID.randomUUID(), alleles)), fileSchema);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithHeadersAndProfiles() throws IOException {
		Schema fileSchema = new Schema("taxon", "id", Method.MLST, "description", headers);
		MlFormatter formatter = new MlFormatter();
		String[][] alleles = {{"1", "1", "1", "1", "1"}, {"4", "1", "1", "3", "3"}};
		String expected = readFile("ml", "ml-h-d-2.txt");
		String formatted = formatter.format(Arrays.asList(profiles(UUID.randomUUID(), UUID.randomUUID(), alleles)), fileSchema);
		assertEquals(expected, formatted);
	}

}
