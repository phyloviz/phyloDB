package pt.ist.meic.phylodb.formatters.datasets;

import pt.ist.meic.phylodb.typing.profile.model.Profile;

import java.util.stream.Stream;

public class MlvaFormatter implements DatasetFormatter<Profile> {

	public MlvaFormatter() {
	}

	@Override
	public Dataset<Profile> parse(Stream<String> data) {
		return null;
	}

	@Override
	public String format(Dataset<Profile> data) {
		return null;
	}

}
