package pt.ist.meic.phylodb.typing.isolate.model.output;

import org.springframework.http.MediaType;
import pt.ist.meic.phylodb.output.Output;
import pt.ist.meic.phylodb.typing.isolate.model.Isolate;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public interface GetIsolatesOutputModel<T> extends Output<T> {

	static Function<List<Isolate>, GetIsolatesOutputModel<?>> get(String type) {
		return new HashMap<String, Function<List<Isolate>, GetIsolatesOutputModel<?>>>() {{
			put(MediaType.APPLICATION_OCTET_STREAM_VALUE, GetIsolatesFileOutputModel::new);
			put(MediaType.APPLICATION_JSON_VALUE, GetIsolatesJsonOutputModel::new);
		}}.get(type);
	}

}
