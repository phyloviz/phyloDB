package pt.ist.meic.phylodb.io.formatters.dataset.profile;

import pt.ist.meic.phylodb.io.formatters.Formatter;
import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public abstract class ProfilesFormatter extends Formatter<Profile> {

	protected UUID projectId;
	protected UUID datasetId;
	protected long loci = 0;

	public static ProfilesFormatter get(String format) {
		return new HashMap<String, ProfilesFormatter>() {{
			put(Method.MLST.getName(), new MlFormatter());
			put(Method.MLVA.getName(), new MlFormatter());
			put(Method.SNP.getName(), new SnpFormatter());
		}}.get(format);
	}

	@Override
	protected boolean init(Iterator<String> it, Object... params) {
		this.projectId = (UUID) params[0];
		this.datasetId = (UUID) params[1];
		this.loci = ((Schema) params[1]).getLociIds().stream()
				.map(Entity::getPrimaryKey)
				.count();
		return true;
	}

}
