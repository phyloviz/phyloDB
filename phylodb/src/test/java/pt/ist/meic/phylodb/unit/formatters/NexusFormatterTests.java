package pt.ist.meic.phylodb.unit.formatters;

import javafx.util.Pair;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pt.ist.meic.phylodb.analysis.inference.model.Edge;
import pt.ist.meic.phylodb.io.formatters.analysis.NexusFormatter;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static pt.ist.meic.phylodb.utils.FileUtils.createFile;
import static pt.ist.meic.phylodb.utils.FileUtils.readFile;

public class NexusFormatterTests extends FormatterTests {

	private static final String PROJECT_ID = UUID.randomUUID().toString(), DATASET_ID = UUID.randomUUID().toString();

	private static Edge edge(String from, String to, int distance) {
		Profile p1 = new Profile(PROJECT_ID, DATASET_ID, from, null, new String[0]);
		Profile p2 = new Profile(PROJECT_ID, DATASET_ID, to, null, new String[0]);
		return new Edge(new VersionedEntity<>(p1.getPrimaryKey(), p1.getVersion(), p1.isDeprecated()),
				new VersionedEntity<>(p2.getPrimaryKey(), p2.getVersion(), p2.isDeprecated()),
				distance
		);
	}

	private static Stream<Arguments> emptyListParams() {
		return Stream.of(Arguments.of("nx-0.txt", new Integer[0]),
				Arguments.of("nx-b-0.txt", new Integer[]{2, 3}),
				Arguments.of("nx-b-1.txt", new Integer[]{2}));
	}

	private static Stream<Arguments> nonemptyListParams() {
		List<Edge> expected1 = new ArrayList<>();
		expected1.add(edge("2", "1", 1));
		List<Edge> expected2 = new ArrayList<>();
		expected2.add(edge("3", "1", 1));
		expected2.add(edge("3", "2", 2));
		expected2.add(edge("3", "1", 1));
		expected2.add(edge("3", "2", 2));
		List<Edge> expected3 = new ArrayList<>();
		expected3.add(edge("2", "3", 3));
		expected3.add(edge("2", "4", 4));
		expected3.add(edge("1", "2", 2));
		return Stream.of(Arguments.of("nx-2-e.txt", new Pair<>(expected1.toArray(new Edge[0]), new Integer[0])),
				Arguments.of("nx-3-e.txt", new Pair<>(expected2.toArray(new Edge[0]), new Integer[0])),
				Arguments.of("nx-4-t.txt", new Pair<>(expected3.toArray(new Edge[0]), new Integer[0])));
	}

	@ParameterizedTest
	@MethodSource("emptyListParams")
	public void parse_emptyList(String filename, Integer[] errorLines) throws IOException {
		NexusFormatter formatter = new NexusFormatter();
		Pair<List<Edge>, List<Integer>> result = formatter.parse(createFile("formatters/nexus", filename), PROJECT_ID, DATASET_ID);
		assertEquals(0, result.getKey().size());
		assertArrayEquals(errorLines, result.getValue().toArray());
	}

	@ParameterizedTest
	@MethodSource("nonemptyListParams")
	public void parse_nonemptyList(String filename, Pair<Edge[], Integer[]> expected) throws IOException {
		NexusFormatter formatter = new NexusFormatter();
		Pair<List<Edge>, List<Integer>> result = formatter.parse(createFile("formatters/nexus", filename), PROJECT_ID, DATASET_ID);
		List<Edge> edges = result.getKey();
		assertEquals(expected.getKey().length, edges.size());
		assertArrayEquals(expected.getKey(), edges.toArray());
		assertArrayEquals(expected.getValue(), result.getValue().toArray());
	}

	@Test
	public void format_empty() throws IOException {
		NexusFormatter formatter = new NexusFormatter();
		String expected = readFile("formatters/nexus", "nx-0.txt");
		String formatted = formatter.format(Lists.emptyList());
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithOneEdge() throws IOException {
		NexusFormatter formatter = new NexusFormatter();
		List<Edge> edges = new ArrayList<>();
		edges.add(edge("2", "1", 1));
		String expected = readFile("formatters/nexus", "nx-2-e.txt");
		String formatted = formatter.format(edges);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithEdges() throws IOException {
		NexusFormatter formatter = new NexusFormatter();
		List<Edge> edges = new ArrayList<>();
		edges.add(edge("2", "3", 3));
		edges.add(edge("2", "4", 4));
		edges.add(edge("1", "2", 2));
		String expected = readFile("formatters/nexus", "nx-4-t.txt");
		String formatted = formatter.format(edges);
		assertEquals(expected, formatted);
	}
}
