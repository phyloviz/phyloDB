package pt.ist.meic.phylodb.formatters.dataset.profile;

import pt.ist.meic.phylodb.formatters.dataset.FileDataset;
import pt.ist.meic.phylodb.formatters.dataset.SchemedFileDataset;
import pt.ist.meic.phylodb.typing.profile.model.Profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class MlstFormatter extends ProfilesFormatter {

	public MlstFormatter() {
	}

	@Override
	public FileDataset<Profile> parse(Stream<String> data) {
		Iterator<String[]> iterator = data.map(line -> line.split("\\t")).iterator();
		String[] headers = iterator.next();
		List<Profile> profiles = new ArrayList<>();
		while (iterator.hasNext()) {
			String[] line = iterator.next();
			profiles.add(new Profile(null, line[0], null, Arrays.copyOfRange(line, 1, line.length)));
		}
		return new SchemedFileDataset(Arrays.copyOfRange(headers, 1, headers.length), profiles);
	}

	@Override
	public String format(FileDataset<Profile> data) {
		SchemedFileDataset dataset = (SchemedFileDataset) data;
		StringBuilder raw = new StringBuilder("ST\\t");
		raw.append(String.join("\\t", dataset.getLociIds())).append("\n");
		for (Profile profile: dataset.getEntities())
			raw.append(profile.getId()).append("\\t").append(String.join("\\t", profile.getAllelesIds()))
					.append("\n");
		return raw.toString();
	}

}
