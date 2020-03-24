package pt.ist.meic.phylodb.typing.profile.model.output;

import org.springframework.http.MediaType;
import pt.ist.meic.phylodb.output.Output;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.model.Schema;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

public interface GetProfilesOutputModel<T> extends Output<T> {

	static BiFunction<Schema, List<Profile>, GetProfilesOutputModel<?>> get(String type) {
		return new HashMap<String, BiFunction<Schema, List<Profile>, GetProfilesOutputModel<?>>>() {{
			put(MediaType.APPLICATION_OCTET_STREAM_VALUE, GetProfilesFileOutputModel::new);
			put(MediaType.APPLICATION_JSON_VALUE, GetProfilesJsonOutputModel::new);
		}}.get(type);
	}

}
