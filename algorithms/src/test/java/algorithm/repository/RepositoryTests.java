package algorithm.repository;

import org.neo4j.graphdb.GraphDatabaseService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class RepositoryTests {

	protected GraphDatabaseService database;

	protected void arrange(String path, String filename) throws IOException {
		database.execute(readFile(path, filename));
	}

	protected static String readFile(String path, String filename) throws IOException {
		path = Paths.get("src", "test", "java", "resources").toFile().getAbsolutePath() + "/" + path;
		return Files.lines(Paths.get(path + "/" + filename)).collect(Collectors.joining("\n"));
	}

	protected long getRelationshipsCount() {
		return database.getAllRelationships().stream().count();
	}

	protected long getNodesCount() {
		return database.getAllNodes().stream().count();
	}
}
