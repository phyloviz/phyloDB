package algorithm;

import algorithm.inference.InferenceProcedures;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.*;
import org.neo4j.harness.junit.Neo4jRule;
import org.neo4j.test.extension.Inject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.neo4j.driver.v1.Values.parameters;

public class GoeBURSTTest {

	private static final String PROJECT_ID = "project", DATASET_ID = "dataset";
	private static final String PROJECT_PARAM = "project", DATASET_PARAM = "dataset", INFERENCE_PARAM = "inference", LVS_PARAM = "lvs";
	private static final String goeburst = String.format("CALL algorithms.inference.goeburst({%s}, {%s}, {%s}, {%s})", PROJECT_PARAM, DATASET_PARAM, INFERENCE_PARAM, LVS_PARAM);
	private static final String getInferenceStatement = String.format("MATCH (p:Project {id: {%s}})\n" +
			"MATCH (p)-[:CONTAINS]->(d:Dataset {id:{%s}})\n" +
			"MATCH (d)-[:CONTAINS]->(p1:Profile)-[ds:DISTANCES {id: {%s}}]->(p2:Profile)\n" +
			"RETURN p1 as from, ds as edge, p2 as to", PROJECT_PARAM, DATASET_PARAM, INFERENCE_PARAM);


	@Rule
	public Neo4jRule neo4j = new Neo4jRule().withProcedure(InferenceProcedures.class );

	@Test
	public void test() throws IOException {
		// In a try-block, to make sure we close the driver and session after the test
		try(Driver driver = GraphDatabase.driver(neo4j.boltURI(), Config.build().withEncryptionLevel(Config.EncryptionLevel.NONE).toConfig());
			Session session = driver.session())
		{
			String statement = readFile("","ctx-2p-1a.cypher");
			session.run(statement);
			session.run(goeburst, parameters(PROJECT_PARAM, PROJECT_ID, DATASET_PARAM, DATASET_ID, INFERENCE_PARAM, "teste", LVS_PARAM, 3));
			List<Record> result =  session.run(getInferenceStatement, parameters(PROJECT_PARAM, PROJECT_ID, DATASET_PARAM, DATASET_ID, INFERENCE_PARAM, "teste"))
					.list();
			assertEquals(1, result.size());
		}
	}

	private static String readFile(String path, String filename) throws IOException {
		path = Paths.get("src", "test", "java", "resources").toFile().getAbsolutePath() + "/" + path;
		return Files.lines(Paths.get(path + "/" + filename)).collect(Collectors.joining("\n"));
	}

}
