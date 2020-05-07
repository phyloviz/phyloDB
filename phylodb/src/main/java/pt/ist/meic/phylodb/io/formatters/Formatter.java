package pt.ist.meic.phylodb.io.formatters;

import javafx.util.Pair;
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

	protected abstract boolean init(Iterator<String> it, Object... params);

	protected abstract boolean parse(String line, boolean last, Consumer<T> add);

	public abstract String format(List<T> entities, Object... params);

	public Pair<List<T>, List<Integer>> parse(MultipartFile file, Object... params) throws IOException {
		try (Stream<String> lines = new BufferedReader(new InputStreamReader(file.getInputStream())).lines()) {
			Iterator<String> it = lines.iterator();
			List<T> entities = new ArrayList<>();
			List<Integer> errors = new ArrayList<>();
			if (!it.hasNext())
				return new Pair<>(entities, errors);
			int count = 0;
			if (!init(it, params))
				return new Pair<>(entities, errors);
			while (it.hasNext()) {
				count++;
				if (!parse(it.next(), !it.hasNext(), entities::add))
					errors.add(count);
			}
			return new Pair<>(entities, errors);
		}
	}

}
