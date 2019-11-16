package pt.ist.meic.phylodb.file;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FastaFileReader<R> implements FileReader<List<R>> {

	@Override
	public List<R> read(MultipartFile file) {
		return null;
	}
}