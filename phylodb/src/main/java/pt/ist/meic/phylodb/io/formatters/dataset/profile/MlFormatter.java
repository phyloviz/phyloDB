package pt.ist.meic.phylodb.io.formatters.dataset.profile;

import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class MlFormatter extends ProfilesFormatter {

	@Override
	protected boolean parse(String line, Consumer<Profile> add) {
		String[] columns = line.split("\\t");
		if (!Arrays.stream(columns).allMatch(c -> c.matches("^\\d+$")) || columns.length != loci)
			return false;
		add.accept(new Profile(datasetId, columns[0], null, Arrays.copyOfRange(columns, 1, columns.length)));
		return true;
	}

	@Override
	public String format(List<Profile> data, Object... params) {
		StringBuilder raw = new StringBuilder("ST\\t");
		String[] lociIds = ((Schema) params[0]).getLociIds().stream().map(Entity::getPrimaryKey).toArray(String[]::new);
		raw.append(String.join("\\t", lociIds)).append("\n");
		for (Profile profile : data)
			raw.append(profile.getPrimaryKey().getId()).append("\\t")
					.append(String.join("\\t", profile.getAllelesids()))
					.append("\n");
		return raw.toString();
	}

}
