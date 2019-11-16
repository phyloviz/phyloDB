package algorithm.processing;

import algorithm.Algorithm;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Result;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GoeBURST extends Algorithm {

	private static final String PROFILE = "Profile", FOLLOWS_SCHEMA = "FOLLOWS_SCHEMA";

	@Procedure(value = "algorithms.processing.goeBURST"/*, mode = Mode.WRITE*/)
	public Stream<GoeBURSTResult> GoeBURST(@Name("taxon") String taxon, @Name("schema") String schema) {
		return database.findNodes(Label.label(taxon)).stream()
				.filter(n -> n.hasLabel(Label.label(PROFILE)) && hasNeighbourWithProperty(n, RelationshipType.withName(FOLLOWS_SCHEMA), schema))
				.map(GoeBURSTResult::new);

	}

	@Procedure(value = "algorithms.processing.goeBURST2"/*, mode = Mode.WRITE*/)
	public Stream<GoeBURSTResult> GoeBURST2(@Name("taxon") String taxon, @Name("schema") String schema) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("taxon", taxon);
		parameters.put("schema", schema);
		try (Result r = database.execute("MATCH (n:Profile)-[:FOLLOWS_SCHEMA]->(s:Schema {name: $schema}) WHERE $taxon IN LABELS(n) RETURN n;", parameters)) {
			 return r.stream()
					 .map(re -> new GoeBURSTResult((Node) re.get("n")))
					 .collect(Collectors.toList())
					 .stream();
		}
	}

	public class GoeBURSTResult {
		public final Node newNodeObject;

		public GoeBURSTResult(Node node) {
			this.newNodeObject = node;
		}
	}

	private boolean hasNeighbourWithProperty(Node node, RelationshipType relationship, String property) {
		return StreamSupport.stream(node.getRelationships(relationship).spliterator(), false)
				.anyMatch(r -> r.getEndNode().getProperty("name").equals(property));
	}

}
