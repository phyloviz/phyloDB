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

public class IsolatesFormatter extends Formatter<Isolate> {

	private List<String> headers;
	private int id;
	private int st;

	@Override
	protected boolean init(Iterator<String> it, Object... params) {
		headers = Arrays.asList(it.next().split("\\t"));
		if ((id = headers.indexOf("id")) == -1)
			return false;
		st = IntStream.range(0, headers.size())
				.filter(i -> headers.get(i).startsWith("ST"))
				.findFirst()
				.orElse(-1);
		return true;
	}

	@Override
	protected boolean parse(String line, Consumer<Isolate> add) {
		String[] columns = line.split("\\t");
		String id = columns[this.id];
		String profile = st != -1 ? columns[st] : null;
		if (!id.matches("^\\d+$"))
			return false;
		Ancillary[] ancillaries = IntStream.range(0, columns.length)
				.filter(i -> !columns[i].equals(" ") && i != this.id && i != st)
				.mapToObj(i -> new Ancillary(headers.get(i), columns[i]))
				.toArray(Ancillary[]::new);
		add.accept(new Isolate(id, null, ancillaries, profile));
		return true;
	}

	@Override
	public String format(List<Isolate> isolates, Object... params) {
		List<String> headers = isolates.stream()
				.flatMap(i -> Arrays.stream(i.getAncillaries()).map(Ancillary::getKey))
				.distinct()
				.collect(Collectors.toList());
		StringBuilder raw = new StringBuilder("id\t").append(Strings.join(headers, '\t')).append("ST\n");
		for (Isolate isolate : isolates) {
			raw.append(isolate.getPrimaryKey().getId()).append('\t');
			Map<String, String> ancillaries = Arrays.stream(isolate.getAncillaries())
					.collect(Collectors.toMap(Ancillary::getKey, Ancillary::getValue));
			for (String header : headers)
				raw.append(ancillaries.getOrDefault(header, " ")).append('\t');
			raw.append(isolate.getProfile()).append('\n');
		}
		return raw.toString();
	}

}
