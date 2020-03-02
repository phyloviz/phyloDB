package pt.ist.meic.phylodb.phylogeny.allele.model.output;

import org.springframework.http.MediaType;
import pt.ist.meic.phylodb.mediatype.Output;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public interface GetAllelesOutputModel<T> extends Output<T> {

	static Function<List<Allele>, GetAllelesOutputModel<?>> get(String type) {
		return new HashMap<String, Function<List<Allele>, GetAllelesOutputModel<?>>>() {{
			put(MediaType.APPLICATION_OCTET_STREAM_VALUE, GetAllelesFileOutputModel::new);
			put(MediaType.APPLICATION_JSON_VALUE, GetAllelesJsonOutputModel::new);
		}}.get(type);
	}

}
