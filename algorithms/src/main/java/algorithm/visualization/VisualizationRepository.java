package algorithm.visualization;

import algorithm.repository.Repository;
import algorithm.repository.type.*;
import algorithm.visualization.model.Tree;
import algorithm.visualization.model.Vertex;
import algorithm.visualization.model.Visualization;
import org.neo4j.graphdb.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VisualizationRepository extends Repository<Visualization, Tree> {

	public VisualizationRepository(GraphDatabaseService database) {
		super(database);
	}

	@Override
	public Tree read(String... params) {
		String projectId = params[0], datasetId = params[1], inferenceId = params[2];
		Node project = node(Project.LABEL, projectId);
		Node dataset = related(project, Relation.CONTAINS, Direction.OUTGOING, Dataset.LABEL, datasetId);
		List<Relationship> distances = related(dataset, Relation.CONTAINS, Direction.OUTGOING, Profile.LABEL)
				.flatMap(n -> relationships(n, Relation.DISTANCES, Direction.OUTGOING)
					.filter(r -> r.getProperty(Distance.ID).equals(inferenceId)))
				.collect(Collectors.toList());
		Vertex[] roots = distances.stream()
				.map(Relationship::getStartNode)
				.filter(r -> distances.stream().noneMatch(r2 -> r2.getEndNode().equals(r)))
				.distinct()
				.map(n ->  tree(n, 0, inferenceId))
				.toArray(Vertex[]::new);
		return new Tree(roots);
	}

	@Override
	public void write(Visualization visualization) {
		String inferenceId = visualization.getInferenceId();
		String id = visualization.getId();
		String algorithm = visualization.getAlgorithm();
		algorithm.visualization.model.Coordinate[] coordinates = visualization.getCoordinates();
		Node project = node(Project.LABEL, visualization.getProjectId());
		Node dataset = related(project, Relation.CONTAINS, Direction.OUTGOING, Dataset.LABEL, visualization.getDatasetId());
		for (algorithm.visualization.model.Coordinate coordinate : coordinates) {
			Node profile = related(dataset, Relation.CONTAINS, Direction.OUTGOING, Profile.LABEL, coordinate.getProfileId());
			Node c = database.createNode(Label.label(Coordinate.LABEL));
			c.setProperty(Coordinate.X, coordinate.getX());
			c.setProperty(Coordinate.Y, coordinate.getY());
			Map<String, Object> properties = new HashMap<>();
			properties.put(Has.INFERENCE_ID, inferenceId);
			properties.put(Has.ID, id);
			properties.put(Has.COMPONENT, coordinate.getComponent());
			properties.put(Has.ALGORITHM, algorithm);
			properties.put(Has.DEPRECATED, false);
			createRelationship(profile, c, Relation.HAS, properties);
		}
	}

	private Vertex tree(Node current, long distance, String inferenceId) {
		Vertex[] children = distances(current, Relation.DISTANCES, Direction.OUTGOING, inferenceId)
				.map(r -> tree(r.getEndNode(), (long) r.getProperty(Distance.DISTANCE), inferenceId))
				.toArray(Vertex[]::new);
		return new Vertex((String) current.getProperty(Profile.ID), distance, children);
	}

	private Stream<Relationship> distances(Node node, Relation relationship, Direction direction, String id) {
		return relationships(node, relationship, direction).filter(r -> r.getProperty(Distance.ID).equals(id));
	}
}
