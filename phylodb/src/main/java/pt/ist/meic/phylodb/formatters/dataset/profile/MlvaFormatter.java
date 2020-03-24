package pt.ist.meic.phylodb.formatters.dataset.profile;

import pt.ist.meic.phylodb.formatters.dataset.FileDataset;
import pt.ist.meic.phylodb.typing.profile.model.Profile;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MlvaFormatter extends ProfilesFormatter {

	public MlvaFormatter() {
	}

	@Override
	public FileDataset<Profile> parse(Stream<String> data) {
		return new FileDataset<>(data.map(line -> line.split("\\t"))
				.map(line -> new Profile(null, line[0], null, Arrays.copyOfRange(line, 1, line.length)))
				.collect(Collectors.toList()));
	}

	@Override
	public String format(FileDataset<Profile> data) {
		return data.getEntities().stream()
				.map(p -> "" + p.getId() + "\\t" + String.join("\\t", p.getAllelesIds()))
				.reduce("", (a, c) -> a + c + "\n");
	}

}
