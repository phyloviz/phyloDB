package pt.ist.meic.phylodb.io.formatters.dataset.profile;

import pt.ist.meic.phylodb.typing.profile.model.Profile;

import java.util.List;
import java.util.function.Consumer;

public class SnpFormatter extends ProfilesFormatter {

	public SnpFormatter() {
	}

	@Override
	protected boolean parse(String line, Consumer<Profile> add) {
		String[] columns = line.split("\\t", 2);
		if (columns.length != 2 || !columns[0].matches("^\\d+$") || !columns[1].matches("^[01]+$") || columns[1].length() != loci)
			return false;
		add.accept(new Profile(projectId, datasetId, columns[0], null, columns[1].split("")));
		return true;
	}

	@Override
	public String format(List<Profile> data, Object... params) {
		return data.stream()
				.map(p -> p.getPrimaryKey() + "\\t" + String.join("\\t", p.getAllelesids()))
				.reduce("", (a, c) -> a + c + "\n");
	}

}
