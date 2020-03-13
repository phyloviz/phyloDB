package pt.ist.meic.phylodb.formatters.datasets;

import pt.ist.meic.phylodb.typing.isolate.model.Isolate;

import java.util.stream.Stream;

public class IsolatesFormatter implements DatasetFormatter<Isolate> {

	@Override
	public FileDataset<Isolate> parse(Stream<String> data) {
		return null;
	}

	@Override
	public String format(FileDataset<Isolate> data) {
		return null;
	}

}
