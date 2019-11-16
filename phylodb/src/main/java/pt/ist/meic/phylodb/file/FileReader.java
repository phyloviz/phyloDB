package pt.ist.meic.phylodb.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileReader<R> {

	R read(MultipartFile file);
}
