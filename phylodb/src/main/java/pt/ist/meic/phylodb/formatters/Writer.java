package pt.ist.meic.phylodb.formatters;

import java.io.IOException;
import java.io.OutputStream;

public interface Writer<T> {

	String format(T data);

	default void write(OutputStream file, T data) throws IOException {
		file.write(format(data).getBytes());
	}

}
