package pt.ist.meic.phylodb.io.formatters;

import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.utils.service.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * The Formatter class is an abstract base class of the any formatter
 * <p>
 * The Formatter class contains abstract methods to be overridden by the classes that extend from it, and the a base implementation of the parsing of a file.
 *
 * @param <T> domain object type that can be parsed or formatted
 */
public abstract class Formatter<T> {

	/**
	 * Initializes the formatter with state needed to perform the parsing, or perform some logic before the parsing
	 *
	 * @param it     {@link Iterator<String>} that allows to iterate over the rows of the file
	 * @param params any objects that can be used to initialize the formatter state
	 * @return {@code true} if it was possible to initialize the state of the formatter or some logic before the parsing
	 */
	protected abstract boolean init(Iterator<String> it, Object... params);

	/**
	 * Parses the given line of the file
	 * <p>
	 * Parses the given line of the file, if it is possible to parse the line then the respective parsed entity is added to the list of entities parsed,
	 * by calling the param add with the entity, and it is return true. If the line is invalid then the parameter add is not called, thus the list of entities doesn't
	 * change, and it is returned false.
	 *
	 * @param line the line to be parsed
	 * @param last boolean which specifies if it is the last line
	 * @param add  consumer that adds to a list of entities the passed entity
	 * @return {@code true} if it was possible to parse the line
	 */
	protected abstract boolean parse(String line, boolean last, Consumer<T> add);

	/**
	 * Formats the given list of entities
	 * <p>
	 * Any Formatter shall implement this method to format into a String the given entities in their respective format.
	 * The parameter params is used to initialize the state for the formatting
	 *
	 * @param entities list of entities to be formatted
	 * @param params   any objects that can be used to initialize the operation
	 * @return a formatted string of all entities received
	 */
	public abstract String format(List<T> entities, Object... params);

	/**
	 * Parses the file and returns the list of entities parsed, and a list with the number of the lines invalid
	 * <p>
	 * Parses the file by iterating over every line, and calling the {@link #init(Iterator, Object...)} and {@link #parse(String, boolean, Consumer)} methods.
	 * Every entity that is parsed is added to a list, and every line that is invalid is also added to a list. In the end those lists are returned.
	 *
	 * @param file   file to be parsed
	 * @param params any objects that can be used to initialize the operation
	 * @return the list of entities parsed and the list of line numbers that couldn't be parsed
	 * @throws IOException if there is an error parsing the file
	 */
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
				List<T> parsed = new ArrayList<>();
				count++;
				if (!parse(it.next(), !it.hasNext(), parsed::add))
					errors.add(count);
				else
					entities.addAll(parsed);
			}
			return new Pair<>(entities, errors);
		}
	}

}
