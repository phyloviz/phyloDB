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
		if (columns.length != 2 || columns[1].length() != schema.getLociReferences().size())
			return false;
		String[] alleles = columns[1].split("");
		Profile profile = new Profile(projectId, datasetId, columns[0], null, alleles);
		add.accept(profile.updateReferences(schema, missing, authorized));
		return true;
	}

	@Override
	public String format(List<Profile> data, Object... params) {
		String formatted = data.stream()
				.map(p -> p.getPrimaryKey().getId() + "\t" + String.join("", formatAlleles(p.getAllelesReferences())))
				.reduce("", (a, c) -> a + c + "\n");
		return formatted.length() > 0 ? formatted.substring(0, formatted.length() - "\n".length()) : "";
	}

}
