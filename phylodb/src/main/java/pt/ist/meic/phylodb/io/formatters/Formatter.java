package pt.ist.meic.phylodb.io.formatters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class Formatter<T> {

	protected static final Logger LOG = LoggerFactory.getLogger(Formatter.class);

	protected abstract boolean init(Iterator<String> it, Object... params);

	protected abstract boolean parse(String line, boolean last, Consumer<T> add);

	public abstract String format(List<T> entities, Object... params);

	public List<T> parse(MultipartFile file, Object... params) throws IOException {
		try (Stream<String> lines = new BufferedReader(new InputStreamReader(file.getInputStream())).lines()) {
			Iterator<String> it = lines.iterator();
			List<T> entities = new ArrayList<>();
			StringBuilder errors = new StringBuilder();
			if (!it.hasNext())
				return entities;
			int count = 0;
			if (!init(it, params))
				return entities;
			while (it.hasNext()) {
				count++;
				if (!parse(it.next(), !it.hasNext(), entities::add))
					errors.append(count).append(',');
			}
			if (errors.length() > 0)
				LOG.info(String.format("The following lines of file %s were not valid: %s", file.getName(), errors.substring(0, errors.length() - 1)));
			return entities;
		}
	}

}
