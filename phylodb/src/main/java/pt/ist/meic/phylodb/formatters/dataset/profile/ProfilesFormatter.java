package pt.ist.meic.phylodb.formatters.dataset.profile;

import pt.ist.meic.phylodb.formatters.Formatter;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.model.Schema;

import java.util.HashMap;

public abstract class ProfilesFormatter implements Formatter<Profile> {

	public static ProfilesFormatter get(String format) {
		return new HashMap<String, ProfilesFormatter>() {{
			put(Schema.MLST, new MlstFormatter());
			put(Schema.MLVA, new MlvaFormatter());
			put(Schema.SNP, new SnpFormatter());
		}}.get(format);
	}

}
