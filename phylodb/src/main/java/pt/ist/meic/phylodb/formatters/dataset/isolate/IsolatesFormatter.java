package pt.ist.meic.phylodb.formatters.dataset.isolate;

import org.apache.logging.log4j.util.Strings;
import pt.ist.meic.phylodb.formatters.Formatter;
import pt.ist.meic.phylodb.formatters.dataset.FileDataset;
import pt.ist.meic.phylodb.typing.isolate.model.Ancillary;
import pt.ist.meic.phylodb.typing.isolate.model.Isolate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class IsolatesFormatter implements Formatter<Isolate> {

	@Override
	public FileDataset<Isolate> parse(Stream<String> data) {
		Iterator<String[]> iterator = data.map(line -> line.split("\\t")).iterator();
		String[] headers = iterator.next();
		int profileIdx= IntStream.range(2, headers.length)
				.filter(i -> headers[i].startsWith("ST"))
				.findFirst().getAsInt();
		List<Isolate> isolates = new ArrayList<>();
		while (iterator.hasNext()) {
			String[] line = iterator.next();
			Ancillary[] ancillaries = new Ancillary[profileIdx - 2];
			for (int i = 2; i < profileIdx; i++) {
				if(!line[i].equals(" "))
					ancillaries[i - 2] = new Ancillary(headers[i], line[i]);
			}
			isolates.add(new Isolate(line[1], null, ancillaries, line[profileIdx]));
		}
		return new FileDataset<>(isolates);
	}

	@Override
	public String format(FileDataset<Isolate> data) {
		List<Isolate> isolates = data.getEntities();
		List<String> headers = isolates.stream()
				.flatMap(i -> Arrays.stream(i.getAncillaries()).map(Ancillary::getKey))
				.distinct()
				.collect(Collectors.toList());
		StringBuilder raw = new StringBuilder("id\tisolate\t").append(Strings.join(headers, '\t')).append("ST\n");
		for (int i = 0; i < isolates.size(); i++) {
			Isolate isolate = isolates.get(i);
			raw.append(i).append('\t').append(isolate.getId()).append('\t');
			Map<String, String> ancillaries = Arrays.stream(isolate.getAncillaries())
					.collect(Collectors.toMap(Ancillary::getKey, Ancillary::getValue));
			for (String header : headers)
				raw.append(ancillaries.getOrDefault(header, " ")).append('\t');
			raw.append(isolate.getProfile()).append('\n');
		}
		return raw.toString();
	}

}
