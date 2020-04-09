package pt.ist.meic.phylodb.utils.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.CreatedOutputModel;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@RestController
public abstract class Controller<T extends Entity<?>> {

	@Value("${jsonLimit}")
	protected String jsonLimit;

	@Value("${fileLimit}")
	protected String fileLimit;

	protected <R> ResponseEntity<?> getAll(String type, Function<Integer, Optional<R>> getter, Function<R, OutputModel> json, Function<R, OutputModel> file) {
		int limit = Integer.parseInt(fileLimit);
		Function<R, OutputModel> result = file;
		if (type.equals(MediaType.APPLICATION_JSON_VALUE)) {
			limit = Integer.parseInt(jsonLimit);
			result = json;
		}
		return getter.apply(limit)
				.map(result)
				.orElse(new ErrorOutputModel(Problem.BAD_REQUEST))
				.toResponseEntity();
	}

	protected ResponseEntity<?> get(Supplier<Optional<T>> input, Function<T, OutputModel> map, Supplier<OutputModel> error) {
		return execute(input, map, error);
	}

	protected ResponseEntity<?> put(Supplier<Optional<T>> input, Function<T, Boolean> map) {
		return execute(input, o -> output(map.apply(o)), () -> new ErrorOutputModel(Problem.BAD_REQUEST));
	}

	protected ResponseEntity<?> post(Supplier<Optional<T>> input, Function<T, Boolean> map) {
		return execute(input, o -> output(map.apply(o), (UUID) o.getPrimaryKey()), () -> new ErrorOutputModel(Problem.BAD_REQUEST));
	}

	public ResponseEntity<?> status(Getter<Boolean> input) throws IOException {
		return output(input.get()).toResponseEntity();
	}

	private <R> ResponseEntity<?> execute(Supplier<Optional<R>> input, Function<R, OutputModel> map, Supplier<OutputModel> error) {
		return input.get()
				.map(map)
				.orElse(error.get())
				.toResponseEntity();
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