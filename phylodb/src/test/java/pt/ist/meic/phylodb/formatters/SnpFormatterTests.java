package pt.ist.meic.phylodb.formatters;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pt.ist.meic.phylodb.io.formatters.dataset.profile.SnpFormatter;
import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.model.Schema;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SnpFormatterTests extends ProfilesFormatterTests {

	private static String[] headers = new String[]{"uvrA", "gyrB", "ftsY", "tuf", "gap"};

	private static Stream<Arguments> emptyListParams() {
		UUID project = UUID.randomUUID(), dataset = UUID.randomUUID();
		Schema fileSchema = new Schema("taxon", "id", Method.MLST, "description", headers);
		Schema otherSchema = new Schema("taxon", "id", Method.MLST, "description", Arrays.copyOfRange(headers, 0, 2));
		return Stream.of(Arguments.of(project, dataset, otherSchema, "snp-d-2.txt"),
				Arguments.of(project, dataset, fileSchema, "snp-d-0.txt"));
	}

	private static Stream<Arguments> nonemptyListParams() {
		UUID project = UUID.randomUUID(), dataset = UUID.randomUUID();
		Schema fileSchema = new Schema("taxon", "id", Method.MLST, "description", headers);
		String[][] alleles = {{"1", "0", "0", "1", "0"}};
		String[][] alleles1 = {{"1", "0", "0", "1", "0"}, {"0", "1", "1", "0", "1"}};
		String[][] alleles2 = {{}, {"0", "1", "1", "0", "1"}, {}};
		return Stream.of(Arguments.of(project, dataset, fileSchema, "snp-d-1.txt", profiles(project, dataset, alleles)),
				Arguments.of(project, dataset, fileSchema, "snp-d-2.txt", profiles(project, dataset, alleles1)),
				Arguments.of(project, dataset, fileSchema, "snp-a-3.txt", profiles(project, dataset, alleles2)));
	}

	@ParameterizedTest
	@MethodSource("emptyListParams")
	public void parse_emptyList(UUID project, UUID dataset, Schema schema, String filename) throws IOException {
		SnpFormatter formatter = new SnpFormatter();
		List<Profile> profiles = formatter.parse(createFile("snp", filename), project, dataset, schema);
		assertEquals(0, profiles.size());
	}

	@ParameterizedTest
	@MethodSource("nonemptyListParams")
	public void parse_nonemptyList(UUID project, UUID dataset, Schema schema, String filename, Profile[] expected) throws IOException {
		SnpFormatter formatter = new SnpFormatter();
		List<Profile> profiles = formatter.parse(createFile("snp", filename), project, dataset, schema);
		assertEquals(expected.length, profiles.size());
		for (int i = 0; i < profiles.size(); i++) {
			assertEquals(expected[i].getPrimaryKey().getId(), profiles.get(i).getPrimaryKey().getId());
			assertEquals(expected[i].getAllelesIds(), profiles.get(i).getAllelesIds());
		}
	}

	@Test
	public void format_empty() throws IOException {
		Schema fileSchema = new Schema("taxon", "id", Method.MLST, "description", headers);
		SnpFormatter formatter = new SnpFormatter();
		String expected = readFile("snp", "snp-d-0.txt");
		String formatted = formatter.format(Lists.emptyList(), fileSchema);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithOneProfile() throws IOException {
		Schema fileSchema = new Schema("taxon", "id", Method.MLST, "description", headers);
		SnpFormatter formatter = new SnpFormatter();
		String[][] alleles = {{"1", "0", "0", "1", "0"}};
		String expected = readFile("snp", "snp-d-1.txt");
		String formatted = formatter.format(Arrays.asList(profiles(UUID.randomUUID(), UUID.randomUUID(), alleles)), fileSchema);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithProfiles() throws IOException {
		Schema fileSchema = new Schema("taxon", "id", Method.MLST, "description", headers);
		SnpFormatter formatter = new SnpFormatter();
		String[][] alleles = {{"1", "0", "0", "1", "0"}, {"0", "1", "1", "0", "1"}};
		String expected = readFile("snp", "snp-d-2.txt");
		String formatted = formatter.format(Arrays.asList(profiles(UUID.randomUUID(), UUID.randomUUID(), alleles)), fileSchema);
		assertEquals(expected, formatted);
	}

}
