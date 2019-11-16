package pt.ist.meic.phylodb.file.graph;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.function.BiFunction;

public class NewickFileReader<R> extends GraphFileReader<R> {

	public NewickFileReader(BiFunction<Vertice, List<Edge>, R> parse) {
		super.parse = parse;
	}

	@Override
	protected List<R> read(MultipartFile file, BiFunction<Vertice, List<Edge>, R> parse) {
		return null;
	}
}
