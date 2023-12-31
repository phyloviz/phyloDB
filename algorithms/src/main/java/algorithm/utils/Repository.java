package algorithm.utils;

import algorithm.utils.type.Relation;
import org.neo4j.graphdb.*;

import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Class which contains the operations to iterate with the database
 *
 * @param <T> domain object to be written
 * @param <R> domain object to be read
 */
public abstract class Repository<T, R> {

	protected GraphDatabaseService database;

	public Repository(GraphDatabaseService database) {
		this.database = database;
	}

	/**
	 * Reads the domain object from the database
	 *
	 * @param params identifiers
	 * @return domain object
	 * @throws Exception if couldn't retrieve the domain object
	 */
	public abstract R read(Transaction tx, String... params) throws Exception;

	/**
	 * Stores the domain object in the database
	 *
	 * @param param domain object
	 */
	public abstract void write(Transaction tx, T param);

	protected void createRelationship(Node from, Node to, Relation type, Map<String, Object> params) {
		Relationship relationship = from.createRelationshipTo(to, RelationshipType.withName(type.name()));
		params.forEach(relationship::setProperty);
	}

	protected Node node(String label, String id, Transaction tx) {
		return tx.findNode(Label.label(label), "id", id);
	}

	protected Node related(Node node, Relation relationship, Direction direction, String label, String id) {
		return relationships(node, relationship, direction)
				.map(Relationship::getEndNode)
				.filter(n -> n.hasLabel(Label.label(label)) && n.getProperty("id").equals(id))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("related " + node.getProperty("id") + " " + relationship.name() + " " + label + " " + id));
	}

	protected Stream<Node> related(Node node, Relation relationship, Direction direction, String label) {
		return relationships(node, relationship, direction)
				.map(Relationship::getEndNode)
				.filter(n -> n.hasLabel(Label.label(label)));
	}

	protected Stream<Relationship> relationships(Node node, Relation relationship, Direction direction) {

		ResourceIterable<Relationship> relationships = node.getRelationships(direction, RelationshipType.withName(relationship.name()));

		return StreamSupport.stream(relationships.spliterator(), false);
	}

	protected int version(Node node) {
		return relationships(node, Relation.CONTAINS_DETAILS, Direction.OUTGOING)
				.filter(r -> r.getProperty("to", null) == null)
				.findFirst()
				.map(r -> Math.toIntExact((long) r.getProperty("version")))
				.orElseThrow(() -> new RuntimeException("version " + node.getProperty("id") + " " + node.getLabels().iterator().next().name()));
	}

	protected Node detail(Node node) {
		return relationships(node, Relation.CONTAINS_DETAILS, Direction.OUTGOING)
				.filter(r -> r.getProperty("to", null) == null)
				.findFirst()
				.map(Relationship::getEndNode)
				.orElseThrow(() -> new RuntimeException("detail " + node.getProperty("id") + " " + node.getLabels().iterator().next().name()));
	}

}
