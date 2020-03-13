package pt.ist.meic.phylodb.formatters.datasets;

import pt.ist.meic.phylodb.typing.profile.model.Profile;

import java.util.stream.Stream;

public class MlstFormatter implements DatasetFormatter<Profile> {


	public MlstFormatter() {
	}

	@Override
	public FileDataset<Profile> parse(Stream<String> data) {
		return null;
	}

	@Override
	public String format(FileDataset<Profile> data) {
		return null;
	}

}
