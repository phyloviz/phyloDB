package pt.ist.meic.phylodb.utils;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class FileUtils {

	public static MultipartFile createFile(String path, String filename) throws IOException {
		path = new File("src/test/java/resources/" + path).getAbsolutePath();
		byte[] bytes = Files.readAllBytes(Paths.get(path + "/" + filename));
		return new MockMultipartFile(filename, filename, "text/plain", bytes);
	}

	public static String readFile(String path, String filename) throws IOException {
		path = new File("src/test/java/resources/" + path).getAbsolutePath();
		return Files.lines(Paths.get(path + "/" + filename)).collect(Collectors.joining("\n"));
	}
}
