package pt.ist.meic.phylodb.formatters;

import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.error.exception.FileFormatException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Stream;

public interface Reader<T> {

	T parse(Stream<String> data);

	default T read(MultipartFile file) throws FileFormatException {
		try(Stream<String> lines =  new BufferedReader(new InputStreamReader(file.getInputStream())).lines()) {
			return parse(lines);
		} catch (Exception e) {
			throw new FileFormatException(e.getMessage());
		}
	}

}
