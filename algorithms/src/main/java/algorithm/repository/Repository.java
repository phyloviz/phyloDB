package algorithm.repository;

import algorithm.repository.type.Relation;
import org.neo4j.graphdb.*;

import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class Repository<T, R> {

	public GraphDatabaseService database;

	public Repository(GraphDatabaseService database) {
		this.database = database;
	}

	public abstract R read(String... params) throws Exception;
	public abstract void write(T param);

	protected void createRelationship(Node from, Node to, Relation type, Map<String, Object> params) {
		Relationship relationship = from.createRelationshipTo(to, RelationshipType.withName(type.name()));
		params.forEach(relationship::setProperty);
	}

	protected Node node(String label, String id) {
		return database.findNode(Label.label(label), "id", id);
	}

	protected Node related(Node node, Relation relationship, Direction direction, String label, String id) {
		return relationships(node, relationship, direction)
				.map(Relationship::getEndNode)
				.filter(n -> n.hasLabel(Label.label(label)) && n.getProperty("id").equals(id))
				.findFirst()
				.orElseThrow(RuntimeException::new);
	}

	protected Stream<Node> related(Node node, Relation relationship, Direction direction, String label) {
		return relationships(node, relationship, direction)
				.map(Relationship::getEndNode)
				.filter(n -> n.hasLabel(Label.label(label)));
	}

	protected Stream<Relationship> relationships(Node node, Relation relationship, Direction direction) {
		Iterable<Relationship> relationships = node.getRelationships(RelationshipType.withName(relationship.name()), direction);
		return StreamSupport.stream(relationships.spliterator(), false);
	}

	protected int version(Node node) {
		return relationships(node, Relation.CONTAINS_DETAILS, Direction.OUTGOING)
				.filter(r -> r.getProperty("to", null) == null)
				.findFirst()
				.map(r -> Math.toIntExact((long) r.getProperty("version")))
				.orElseThrow(RuntimeException::new);
	}

}
