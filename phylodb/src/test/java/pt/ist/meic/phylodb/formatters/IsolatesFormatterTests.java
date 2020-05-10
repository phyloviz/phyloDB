package pt.ist.meic.phylodb.formatters;

import javafx.util.Pair;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pt.ist.meic.phylodb.io.formatters.dataset.isolate.IsolatesFormatter;
import pt.ist.meic.phylodb.typing.isolate.model.Ancillary;
import pt.ist.meic.phylodb.typing.isolate.model.Isolate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IsolatesFormatterTests extends FormatterTests {

	private static final String[] ANCILLARY = {"isolate", "country", "continent"};
	private static final UUID projectId = UUID.randomUUID(), datasetId = UUID.randomUUID();
	private static final String missing = "";

	public static List<Isolate> isolates(UUID projectId, UUID datasetId, String[][] ancilarry, String[] profiles) {
		List<Isolate> isolates = new ArrayList<>();
		for (int i = 0; i < ancilarry.length; i++) {
			if (ancilarry[i] == null)
				continue;
			int[] aux = new int[]{i};
			Ancillary[] a = IntStream.range(0, ancilarry[i].length)
					.filter(d -> !ancilarry[aux[0]][d].isEmpty())
					.mapToObj(d -> new Ancillary(ANCILLARY[d], ancilarry[aux[0]][d]))
					.toArray(Ancillary[]::new);
			String profile = null;
			if(profiles != null && profiles[i] != null && !profiles[i].matches(String.format("[\\s%s]*", missing)))
				profile = profiles[i];
			isolates.add(new Isolate(projectId, datasetId, String.valueOf(i + 1), null, a, profile));
		}
		return isolates;
	}

	private static List<Isolate> isolates(String[] profiles) {
		return IntStream.range(0, profiles.length)
				.filter(i -> profiles[i] != null)
				.mapToObj(i -> new Isolate(projectId, datasetId, String.valueOf(i + 1), null, new Ancillary[0], profiles[i]))
				.collect(Collectors.toList());
	}

	private static Stream<Arguments> emptyListParams() {
		return Stream.of(Arguments.of("i-0.txt", -1, new Integer[0]),
				Arguments.of("i-ia-0.txt", 0, new Integer[0]),
				Arguments.of("i-iap-0.txt", 0, new Integer[0]),
				Arguments.of("i-ip-0.txt", 0, new Integer[0]),
				Arguments.of("i-ap-1.txt", 10, new Integer[0]),
				Arguments.of("i-i-0.txt", 0, new Integer[0]));
	}

	private static Stream<Arguments> nonemptyListParams() {
		String[][] ancillary = {{"AU13161", "USA", "North America"}, {"LMG 1231T", "Unknown", "Europe"}, {"LMG 1860", "France", "Europe"}};
		String[][] ancillary1 = {{"AU13161", "USA", "North America"}, {"LMG 1231T", "Unknown", "Europe"}};
		String[][] ancillary2 = {{"AU13161", "USA", "North America"}, null, {"LMG 1860", "France", "Europe"}};
		String[][] ancillary3 = {{"AU13161", "USA", "North America"}, {"LMG 1231T", "Unknown", ""}, {"LMG 1860", "France", "Europe"}};
		String[][] ancillary4 = {{"AU13161", "USA", "North America"}, {"LMG 1231T", "Unknown", "Europe"}};
		List<Isolate> isolates1 = new ArrayList<>(), isolates2 = new ArrayList<>();
		isolates1.add(new Isolate(UUID.randomUUID(), UUID.randomUUID(), "1", null, new Ancillary[0], null));
		isolates2.add(new Isolate(UUID.randomUUID(), UUID.randomUUID(), "1", null, new Ancillary[0], null));
		isolates2.add(new Isolate(UUID.randomUUID(), UUID.randomUUID(), "2", null, new Ancillary[0], null));
		Pair<List<Isolate>, Integer[]> expected1 = new Pair<>(isolates(projectId, datasetId, ancillary, new String[]{"", "", "103"}), new Integer[0]);
		Pair<List<Isolate>, Integer[]> expected2 = new Pair<>(isolates(projectId, datasetId, ancillary1, new String[]{"102", "103"}), new Integer[0]);
		Pair<List<Isolate>, Integer[]> expected3 = new Pair<>(isolates(projectId, datasetId, ancillary2, new String[]{"102", "", "103"}), new Integer[] {2});
		Pair<List<Isolate>, Integer[]> expected4 = new Pair<>(isolates(projectId, datasetId, ancillary3, new String[]{"102", "102", "103"}), new Integer[0]);
		Pair<List<Isolate>, Integer[]> expected5 = new Pair<>(isolates(projectId, datasetId, Arrays.copyOfRange(ancillary1, 0, 1), new String[]{"102"}), new Integer[0]);
		Pair<List<Isolate>, Integer[]> expected6 = new Pair<>(isolates(projectId, datasetId, ancillary4, null), new Integer[0]);
		Pair<List<Isolate>, Integer[]> expected7 = new Pair<>(isolates(projectId, datasetId, Arrays.copyOfRange(ancillary4, 0, 1), null), new Integer[0]);
		Pair<List<Isolate>, Integer[]> expected8 = new Pair<>(isolates(new String[]{"102", "102"}), new Integer[0]);
		Pair<List<Isolate>, Integer[]> expected9 = new Pair<>(isolates(new String[]{"102"}), new Integer[0]);
		Pair<List<Isolate>, Integer[]> expected10 = new Pair<>(isolates1, new Integer[0]);
		Pair<List<Isolate>, Integer[]> expected11 = new Pair<>(isolates2, new Integer[0]);
		return Stream.of(Arguments.of("i-iap-3-ve.txt", 0, expected1),
				Arguments.of("i-iap-2-ve-p.txt", 0, expected2),
				Arguments.of("i-iap-3-vd-p.txt", 0, expected3),
				Arguments.of("i-iap-3-me-p.txt", 0, expected4),
				Arguments.of("i-iap-1-ve-p.txt", 0, expected5),
				Arguments.of("i-ia-2-ve.txt", 0, expected6),
				Arguments.of("i-ia-1-ve.txt", 0, expected7),
				Arguments.of("i-ip-2-ve-p.txt", 0, expected8),
				Arguments.of("i-ip-1-ve-p.txt", 0, expected9),
				Arguments.of("i-i-1.txt", 0, expected10),
				Arguments.of("i-i-2.txt", 0, expected11));
	}

	@ParameterizedTest
	@MethodSource("emptyListParams")
	public void parse_emptyList(String filename, int id, Integer[] errors) throws IOException {
		IsolatesFormatter formatter = new IsolatesFormatter();
		Pair<List<Isolate>, List<Integer>> result = formatter.parse(createFile("isolates", filename), UUID.randomUUID(), UUID.randomUUID(), id, missing);
		assertEquals(0, result.getKey().size());
		assertArrayEquals(errors, result.getValue().toArray());
	}

	@ParameterizedTest
	@MethodSource("nonemptyListParams")
	public void parse_nonemptyList(String filename, int id, Pair<List<Isolate>, Integer[]> expected) throws IOException {
		IsolatesFormatter formatter = new IsolatesFormatter();
		Pair<List<Isolate>, List<Integer>> result = formatter.parse(createFile("isolates", filename), projectId, datasetId, id, missing);
		List<Isolate> isolates = result.getKey();
		assertEquals(expected.getKey().size(), isolates.size());
		assertArrayEquals(expected.getValue(), result.getValue().toArray());
		for (int i = 0; i < isolates.size(); i++) {
			assertEquals(expected.getKey().get(i).getPrimaryKey().getId(), isolates.get(i).getPrimaryKey().getId());
			assertEquals(expected.getKey().get(i).getProfile(), isolates.get(i).getProfile());
			assertEquals(expected.getKey().get(i).getAncillaries().length, isolates.get(i).getAncillaries().length);
			for (int j = 0; j < isolates.get(i).getAncillaries().length; j++) {
				assertEquals(expected.getKey().get(i).getAncillaries()[j].getKey(), isolates.get(i).getAncillaries()[j].getKey());
				assertEquals(expected.getKey().get(i).getAncillaries()[j].getValue(), isolates.get(i).getAncillaries()[j].getValue());
			}
		}
	}

	@Test
	public void format_fileWithAllHeadersAndIsolates() throws IOException {
		String[][] ancillary = {{"AU13161", "USA", "North America"}, {"LMG 1231T", "Unknown", "Europe"}};
		List<Isolate> isolates = isolates(projectId, datasetId, ancillary, new String[]{"102", "103"});
		IsolatesFormatter formatter = new IsolatesFormatter();
		String expected = readFile("isolates", "i-iap-2-ve-p.txt");
		String formatted = formatter.format(isolates);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithAllHeadersAndIsolatesWithMissingAncillary() throws IOException {
		String[][] ancillary = {{"AU13161", "USA", "North America"}, {"LMG 1231T", "Unknown", ""}};
		List<Isolate> isolates = new ArrayList<>(isolates(projectId, datasetId, ancillary, new String[]{"102", "102"}));
		isolates.add(new Isolate(projectId, datasetId, "3", null, new Ancillary[]{new Ancillary(ANCILLARY[0], "LMG 1860"),
				new Ancillary(ANCILLARY[1], "France"), new Ancillary(ANCILLARY[2], "Europe")}, "103"));
		IsolatesFormatter formatter = new IsolatesFormatter();
		String expected = readFile("isolates", "i-iap-3-me-p.txt");
		String formatted = formatter.format(isolates);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithAllHeadersAndIsolatesWithMissingProfiles() throws IOException {
		String[][] ancillary = {{"AU13161", "USA", "North America"}, {"LMG 1231T", "Unknown", "Europe"}, {"LMG 1860", "France", "Europe"}};
		List<Isolate> isolates = new ArrayList<>(isolates(projectId, datasetId, ancillary, new String[]{null, null, "103"}));
		IsolatesFormatter formatter = new IsolatesFormatter();
		String expected = readFile("isolates", "i-iap-3-ve.txt");
		String formatted = formatter.format(isolates);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithAllHeadersAndIsolate() throws IOException {
		String[][] ancillary = {{"AU13161", "USA", "North America"}};
		List<Isolate> isolates = new ArrayList<>(isolates(projectId, datasetId, ancillary, new String[]{"102"}));
		IsolatesFormatter formatter = new IsolatesFormatter();
		String expected = readFile("isolates", "i-iap-1-ve-p.txt");
		String formatted = formatter.format(isolates);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithIdAndAncillaryHeadersAndIsolates() throws IOException {
		String[][] ancillary = {{"AU13161", "USA", "North America"}, {"LMG 1231T", "Unknown", "Europe"}};
		List<Isolate> isolates = isolates(projectId, datasetId, ancillary, null);
		IsolatesFormatter formatter = new IsolatesFormatter();
		String expected = readFile("isolates", "i-ia-2-ve.txt");
		String formatted = formatter.format(isolates);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithIdAndAncillaryHeadersAndIsolate() throws IOException {
		String[][] ancillary = {{"AU13161", "USA", "North America"}};
		List<Isolate> isolates = isolates(projectId, datasetId, ancillary, null);
		IsolatesFormatter formatter = new IsolatesFormatter();
		String expected = readFile("isolates", "i-ia-1-ve.txt");
		String formatted = formatter.format(isolates);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithIdAndProfileHeadersAndIsolates() throws IOException {
		List<Isolate> isolates = isolates(new String[]{"102", "102"});
		IsolatesFormatter formatter = new IsolatesFormatter();
		String expected = readFile("isolates", "i-ip-2-ve-p.txt");
		String formatted = formatter.format(isolates);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithIdAndProfileHeadersAndIsolate() throws IOException {
		List<Isolate> isolates = isolates(new String[]{"102"});
		IsolatesFormatter formatter = new IsolatesFormatter();
		String expected = readFile("isolates", "i-ip-1-ve-p.txt");
		String formatted = formatter.format(isolates);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithIdHeaderAndIsolates() throws IOException {
		List<Isolate> isolates = new ArrayList<>();
		isolates.add(new Isolate(projectId, datasetId, "1", null, new Ancillary[0], null));
		isolates.add(new Isolate(projectId, datasetId, "2", null, new Ancillary[0], null));
		IsolatesFormatter formatter = new IsolatesFormatter();
		String expected = readFile("isolates", "i-i-2.txt");
		String formatted = formatter.format(isolates);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_fileWithIdHeaderAndIsolate() throws IOException {
		List<Isolate> isolates = new ArrayList<>();
		isolates.add(new Isolate(projectId, datasetId, "1", null, new Ancillary[0], null));
		IsolatesFormatter formatter = new IsolatesFormatter();
		String expected = readFile("isolates", "i-i-1.txt");
		String formatted = formatter.format(isolates);
		assertEquals(expected, formatted);
	}

	@Test
	public void format_emptyFile() throws IOException {
		IsolatesFormatter formatter = new IsolatesFormatter();
		String expected = readFile("isolates", "i-i-0.txt");
		String formatted = formatter.format(Lists.emptyList());
		assertEquals(expected, formatted);
	}

}
