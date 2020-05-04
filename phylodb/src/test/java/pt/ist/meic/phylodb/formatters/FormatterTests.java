package pt.ist.meic.phylodb.formatters;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FormatterTests {

	public static MultipartFile createFile(String path, String filename) throws IOException {
		path = Paths.get("src", "test", "java", "resources", "formatters").toFile().getAbsolutePath() + "/" + path;
		byte[] bytes = Files.readAllBytes(Paths.get(path + "/" + filename));
		return new MockMultipartFile(filename, filename, "text/plain", bytes);
	}

	protected static String readFile(String path, String filename) throws IOException {
		path = Paths.get("src", "test", "java", "resources", "formatters").toFile().getAbsolutePath() + "/" + path;
		return Files.lines(Paths.get(path + "/" + filename)).collect(Collectors.joining("\n"));
	}

}
