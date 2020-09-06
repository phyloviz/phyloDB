package algorithm.repository;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class RepositoryTests {

	protected GraphDatabaseService database;

	protected void arrange(Transaction tx, String path, String filename) throws IOException {
		tx.execute(readFile(path, filename));
	}

	protected static String readFile(String path, String filename) throws IOException {
		path = Paths.get("src", "test", "java", "resources").toFile().getAbsolutePath() + "/" + path;
		return Files.lines(Paths.get(path + "/" + filename)).collect(Collectors.joining("\n"));
	}

	protected long getRelationshipsCount(Transaction tx) {
		return tx.getAllRelationships().stream().count();
	}

	protected long getNodesCount(Transaction tx) {
		return tx.getAllNodes().stream().count();
	}
}
