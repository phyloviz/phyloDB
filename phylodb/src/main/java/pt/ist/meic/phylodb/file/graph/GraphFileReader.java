package pt.ist.meic.phylodb.file.graph;

import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.file.FileReader;

import java.util.List;
import java.util.function.BiFunction;

public abstract class GraphFileReader<R> implements FileReader<List<R>> {

	protected BiFunction<Vertice, List<Edge>, R> parse;

	@Override
	public List<R> read(MultipartFile file) {
		return read(file, parse);
	}

	protected abstract List<R> read(MultipartFile file, BiFunction<Vertice, List<Edge>, R> parse);
}
