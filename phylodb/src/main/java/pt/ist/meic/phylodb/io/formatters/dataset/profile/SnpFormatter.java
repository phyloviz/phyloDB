package pt.ist.meic.phylodb.io.formatters.dataset.profile;

import pt.ist.meic.phylodb.typing.profile.model.Profile;

import java.util.List;
import java.util.function.Consumer;

public class SnpFormatter extends ProfilesFormatter {

	public SnpFormatter() {
	}

	@Override
	protected boolean parse(String line, boolean last, Consumer<Profile> add) {
		String[] columns = line.split("\\t", 2);
		if (columns.length != 2 || columns[1].length() != loci)
			return false;
		add.accept(new Profile(projectId, datasetId, columns[0], null, columns[1].split("")));
		return true;
	}

	@Override
	public String format(List<Profile> data, Object... params) {
		String formatted = data.stream()
				.map(p -> p.getPrimaryKey().getId() + "\t" + String.join("", p.getAllelesIds()))
				.reduce("", (a, c) -> a + c + "\n");
		return formatted.length() > 0 ? formatted.substring(0, formatted.length() - "\n".length()) : "";
	}

}
