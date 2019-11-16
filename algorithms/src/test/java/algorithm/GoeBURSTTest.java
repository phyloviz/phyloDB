package algorithm;

import algorithm.processing.GoeBURST;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.*;
import org.neo4j.harness.junit.Neo4jRule;

import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.neo4j.driver.v1.Values.parameters;


public class GoeBURSTTest {

	@Rule
	public Neo4jRule neo4j = new Neo4jRule().withProcedure(GoeBURST.class );

	@Test
	public void test() {
		// In a try-block, to make sure we close the driver and session after the test
		try(Driver driver = GraphDatabase.driver(neo4j.boltURI(), Config.build().withEncryptionLevel(Config.EncryptionLevel.NONE).toConfig());
			Session session = driver.session())
		{
			session.run("CREATE  (p1:Profile:Taxon1 {identifier: 1}),\n" +
					"\t(p2:Profile:Taxon1 {identifier: 2}),\n" +
					"        (i1:Isolate:Taxon1 {name: 'isolate1'}),\n" +
					"        (i2:Isolate:Taxon1 {name: 'isolate2'}),\n" +
					"        (ac1:Ancillary {key: 'key1', value: 'value1'}),\n" +
					"        (ac2:Ancillary {key: 'key2', value: 'value2'}),\n" +
					"        (c1:Coordinate {x: 'x1', y: 'y1'}),\n" +
					"        (c2:Coordinate {x: 'x2', y: 'y2'}),\n" +
					"        (s:Schema {name: 'schema1', tag: 'tag1', description: 'description1'}),\n" +
					"        (l1:Locus {name: 'locus1'}),\n" +
					"        (l2:Locus {name: 'locus2'}),\n" +
					"        (al1:Allele {identifier: '1', sequence:'sequence1'}),\n" +
					"        (al2:Allele {identifier: '2', sequence:'sequence2'}),\n" +
					"        (al3:Allele {identifier: '3', sequence:'sequence3'}),\n" +
					"        (al4:Allele {identifier: '4', sequence:'sequence4'}),\n" +
					" \t(i1)-[:HAS_ANCILLARY]->(ac1),\n" +
					"        (i2)-[:HAS_ANCILLARY]->(ac1),\n" +
					"        (i2)-[:HAS_ANCILLARY]->(ac2),\n" +
					"        (p1)-[:FROM_ISOLATE]->(i1),\n" +
					"        (p2)-[:FROM_ISOLATE]->(i2),\n" +
					"        (p1)-[:IS_POSITIONED]->(c1),\n" +
					"        (p2)-[:IS_POSITIONED]->(c2),\n" +
					"        (p1)-[:DISTANCES]->(p2),\n" +
					"        (p1)-[:FOLLOWS_SCHEMA]->(s),\n" +
					"        (p2)-[:FOLLOWS_SCHEMA]->(s),\n" +
					"        (p1)-[:HAS_ALLELE]->(al1),\n" +
					"        (p1)-[:HAS_ALLELE]->(al3),\n" +
					"        (p2)-[:HAS_ALLELE]->(al2),\n" +
					"        (p2)-[:HAS_ALLELE]->(al4),\n" +
					"        (s)-[:USES_LOCUS]->(l1),\n" +
					"        (s)-[:USES_LOCUS]->(l2),\n" +
					"        (l1)-[:CONTAINS_ALLELE]->(al1),\n" +
					"        (l1)-[:CONTAINS_ALLELE]->(al2),\n" +
					"        (l2)-[:CONTAINS_ALLELE]->(al3),\n" +
					"        (l2)-[:CONTAINS_ALLELE]->(al4);");
			List<Record> results = session.run( "CALL algorithms.processing.goeBURST2({taxon}, {schema})\n" +
					"YIELD newNodeObject \n" +
					"RETURN newNodeObject", parameters("taxon", "Taxon1", "schema", "schema1"))
					.stream()
					.collect(Collectors.toList());
			assertTrue(true);
		}
	}
}
