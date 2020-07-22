package pt.ist.meic.phylodb.utils.controller;

import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.BatchOutputModel;
import pt.ist.meic.phylodb.io.output.CreatedOutputModel;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Base class of a controller that contains auxiliar methods
 */
public abstract class Controller {

	@Value("${application.limits.pagination.json}")
	protected String jsonLimit;

	@Value("${application.limits.pagination.file}")
	protected String fileLimit;

	protected <R> ResponseEntity<?> getAllJson(Function<Integer, Optional<R>> getter, Function<R, OutputModel> json) {
		int limit = Integer.parseInt(jsonLimit);
		return getAll(limit, getter, json);
	}

	protected <R> ResponseEntity<?> getAllFile(Function<Integer, Optional<R>> getter, Function<R, OutputModel> file) {
		int limit = Integer.parseInt(fileLimit);
		return getAll(limit, getter, file);
	}

	protected <R> ResponseEntity<?> get(Supplier<Optional<R>> input, Function<R, OutputModel> map, Supplier<OutputModel> error) {
		return execute(input, map, error);
	}

	protected <R> ResponseEntity<?> put(Supplier<Optional<R>> input, Function<R, Boolean> map) {
		return execute(input, o -> output(map.apply(o)), () -> new ErrorOutputModel(Problem.BAD_REQUEST));
	}

	protected <R> ResponseEntity<?> post(Supplier<Optional<R>> input, Function<R, Boolean> map, Function<R, String> id) {
		return execute(input, o -> output(map.apply(o), id.apply(o)), () -> new ErrorOutputModel(Problem.BAD_REQUEST));
	}

	protected ResponseEntity<?> fileStatus(Getter<Optional<Pair<Integer[], String[]>>> input) throws IOException {
		Optional<Pair<Integer[], String[]>> invalids = input.get();
		return (invalids.map(this::output).orElseGet(() -> output(null))).toResponseEntity();
	}

	protected ResponseEntity<?> status(Supplier<Boolean> input) {
		return output(input.get()).toResponseEntity();
	}

	private <R> ResponseEntity<?> getAll(int limit, Function<Integer, Optional<R>> getter, Function<R, OutputModel> map) {
		return getter.apply(limit)
				.map(map)
				.orElse(new ErrorOutputModel(Problem.BAD_REQUEST))
				.toResponseEntity();
	}

	private <R> ResponseEntity<?> execute(Supplier<Optional<R>> input, Function<R, OutputModel> map, Supplier<OutputModel> error) {
		return input.get()
				.map(map)
				.orElse(error.get())
				.toResponseEntity();
	}

	private OutputModel output(Pair<Integer[], String[]> result) {
		return result == null ? new ErrorOutputModel(Problem.UNAUTHORIZED) : new BatchOutputModel(result.getKey(), result.getValue());
	}

	private OutputModel output(boolean result) {
		return !result ? new ErrorOutputModel(Problem.UNAUTHORIZED) : new NoContentOutputModel();
	}

	private OutputModel output(boolean result, String id) {
		return !result ? new ErrorOutputModel(Problem.UNAUTHORIZED) : new CreatedOutputModel(id);
	}

	protected interface Getter<T> {

		T get() throws IOException;

	}

}