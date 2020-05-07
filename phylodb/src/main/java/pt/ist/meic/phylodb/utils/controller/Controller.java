package pt.ist.meic.phylodb.utils.controller;

import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.BatchOutputModel;
import pt.ist.meic.phylodb.io.output.CreatedOutputModel;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Controller<T extends Entity<?>> {

	@Value("${application.limits.pagination.json}")
	protected String jsonLimit;

	@Value("${application.limits.pagination.file}")
	protected String fileLimit;

	protected <R> ResponseEntity<?> getAll(String type, Function<Integer, Optional<R>> getter, Function<R, OutputModel> json, Function<R, OutputModel> file) {
		int limit = Integer.parseInt(jsonLimit);
		Function<R, OutputModel> result = json;
		if (type.equals(MediaType.TEXT_PLAIN_VALUE)) {
			limit = Integer.parseInt(fileLimit);
			result = file;
		}
		return getter.apply(limit)
				.map(result)
				.orElse(new ErrorOutputModel(Problem.BAD_REQUEST))
				.toResponseEntity();
	}

	protected <R> ResponseEntity<?> get(Supplier<Optional<R>> input, Function<R, OutputModel> map, Supplier<OutputModel> error) {
		return execute(input, map, error);
	}

	protected ResponseEntity<?> put(Supplier<Optional<T>> input, Function<T, Boolean> map) {
		return execute(input, o -> output(map.apply(o)), () -> new ErrorOutputModel(Problem.BAD_REQUEST));
	}

	protected ResponseEntity<?> post(Supplier<Optional<T>> input, Function<T, Boolean> map, Function<T, UUID> id) {
		return execute(input, o -> output(map.apply(o), id.apply(o)), () -> new ErrorOutputModel(Problem.BAD_REQUEST));
	}

	public ResponseEntity<?> fileStatus(Getter<Optional<Pair<Integer[], String[]>>> input) throws IOException {
		Optional<Pair<Integer[], String[]>> invalids = input.get();
		return (invalids.map(this::output).orElseGet(() -> output(null))).toResponseEntity();
	}

	public ResponseEntity<?> status(Supplier<Boolean> input) {
		return output(input.get()).toResponseEntity();
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

	private OutputModel output(boolean result, UUID id) {
		return !result ? new ErrorOutputModel(Problem.UNAUTHORIZED) : new CreatedOutputModel(id);
	}

	protected interface Getter<T> {

		T get() throws IOException;

	}

}