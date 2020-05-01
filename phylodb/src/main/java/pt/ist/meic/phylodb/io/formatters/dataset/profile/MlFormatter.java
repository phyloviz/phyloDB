package pt.ist.meic.phylodb.io.formatters.dataset.profile;

import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.model.Schema;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class MlFormatter extends ProfilesFormatter {

	@Override
	protected boolean parse(String line, boolean last, Consumer<Profile> add) {
		String[] columns = line.split("\\t");
		if (Arrays.asList(columns).contains("ST") || columns.length != loci + 1)
			return false;
		add.accept(new Profile(projectId, datasetId, columns[0], null, Arrays.copyOfRange(columns, 1, columns.length)));
		return true;
	}

	@Override
	public String format(List<Profile> data, Object... params) {
		StringBuilder raw = new StringBuilder("ST\t");
		String[] lociIds = ((Schema) params[0]).getLociIds().toArray(new String[0]);
		raw.append(String.join("\t", lociIds)).append("\n");
		for (Profile profile : data)
			raw.append(profile.getPrimaryKey().getId()).append("\t")
					.append(String.join("\t", profile.getAllelesIds()))
					.append("\n");
		return raw.substring(0, raw.length() - "\n".length());
	}

}