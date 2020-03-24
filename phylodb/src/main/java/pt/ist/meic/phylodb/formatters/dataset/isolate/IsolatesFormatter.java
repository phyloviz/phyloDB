package pt.ist.meic.phylodb.formatters.dataset.isolate;

import pt.ist.meic.phylodb.formatters.Formatter;
import pt.ist.meic.phylodb.formatters.dataset.FileDataset;
import pt.ist.meic.phylodb.typing.isolate.model.Isolate;

import java.util.stream.Stream;

public class IsolatesFormatter implements Formatter<Isolate> {

	@Override
	public FileDataset<Isolate> parse(Stream<String> data) {
		return null;
	}

	@Override
	public String format(FileDataset<Isolate> data) {
		return null;
	}

}
