package pt.ist.meic.phylodb.io.formatters.dataset.isolate;

import org.apache.logging.log4j.util.Strings;
import pt.ist.meic.phylodb.io.formatters.Formatter;
import pt.ist.meic.phylodb.typing.isolate.model.Ancillary;
import pt.ist.meic.phylodb.typing.isolate.model.Isolate;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * IsolatesFormatter is the implementation of the formatter to parse and format isolates
 */
public class IsolatesFormatter extends Formatter<Isolate> {

	private List<String> headers;
	private int id;
	private int st;
	private String projectId;
	private String datasetId;
	private String missing;

	@Override
	protected boolean init(Iterator<String> it, Object... params) {
		this.projectId = (String) params[0];
		this.datasetId = (String) params[1];
		this.id = (int) params[2];
		this.missing = (String) params[3];
		headers = Arrays.asList(it.next().split("\\t"));
		if (id < 0 || id >= headers.size())
			return false;
		st = IntStream.range(0, headers.size())
				.filter(i -> headers.get(i).startsWith("ST"))
				.findFirst()
				.orElse(-1);
		return true;
	}

	@Override
	protected boolean parse(String line, boolean last, Consumer<Isolate> add) {
		String[] columns = line.split("\\t", -1);
		if (columns.length != headers.size())
			return false;
		String id = columns[this.id];
		String profile = st == -1 || columns[st].matches(String.format("[\\s%s]*", missing)) || columns[st].isEmpty() ? null : columns[st];
		Ancillary[] ancillaries = IntStream.range(0, columns.length)
				.filter(i -> !columns[i].matches(String.format("[\\s%s]*", missing)) && !columns[i].isEmpty() && i != this.id && i != st)
				.mapToObj(i -> new Ancillary(headers.get(i), columns[i]))
				.toArray(Ancillary[]::new);
		add.accept(new Isolate(projectId, datasetId, id, null, ancillaries, profile));
		return true;
	}

	@Override
	public String format(List<Isolate> isolates, Object... params) {
		List<String> headers = isolates.stream()
				.flatMap(i -> Arrays.stream(i.getAncillaries()).map(Ancillary::getKey))
				.distinct()
				.collect(Collectors.toList());
		String headersString = Strings.join(headers, '\t');
		StringBuilder formatted = new StringBuilder("id");
		if (!headersString.equals(""))
			formatted.append("\t").append(headersString);
		boolean st = isolates.stream().anyMatch(i -> i.getProfile() != null);
		if (st)
			formatted.append("\t").append("ST");
		formatted.append("\n");
		for (Isolate isolate : isolates) {
			formatted.append(isolate.getPrimaryKey().getId()).append('\t');
			Map<String, String> ancillaries = Arrays.stream(isolate.getAncillaries())
					.collect(Collectors.toMap(Ancillary::getKey, Ancillary::getValue));
			for (String header : headers)
				formatted.append(ancillaries.getOrDefault(header, "")).append('\t');
			if (isolate.getProfile() != null)
				formatted.append(isolate.getProfile().getPrimaryKey().getId());
			else if (!st)
				formatted.delete(formatted.length() - "\t".length(), formatted.length());
			formatted.append('\n');
		}
		return formatted.substring(0, formatted.length() - "\n".length());
	}

}
