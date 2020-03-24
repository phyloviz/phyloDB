package pt.ist.meic.phylodb.formatters.dataset.profile;

import pt.ist.meic.phylodb.formatters.dataset.FileDataset;
import pt.ist.meic.phylodb.typing.profile.model.Profile;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SnpFormatter extends ProfilesFormatter {

	public SnpFormatter() {
	}

	@Override
	public FileDataset<Profile> parse(Stream<String> data) {
		return new FileDataset<>(data.map(line -> line.split("\\t", 2))
				.map(values -> new Profile(null, values[0], null, values[1].split("")))
				.collect(Collectors.toList()));
	}

	@Override
	public String format(FileDataset<Profile> data) {
		return data.getEntities().stream()
				.map(p -> p.getId() + "\\t" + String.join("\\t", p.getAllelesIds()))
				.reduce("", (a, c) -> a + c + "\n");
	}

}
