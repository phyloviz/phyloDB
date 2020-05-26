package algorithm.inference;

import algorithm.repository.type.*;
import algorithm.repository.Repository;
import algorithm.inference.model.Edge;
import algorithm.inference.model.Inference;
import algorithm.inference.model.Matrix;
import org.neo4j.graphdb.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InferenceRepository extends Repository<Inference, Matrix> {

	public InferenceRepository(GraphDatabaseService database) {
		super(database);
	}

	@Override
	public Matrix read(String... params) {
		String projectId = params[0], datasetId = params[1];
		Node project = node(Project.LABEL, projectId);
		Node dataset = related(project, Relation.CONTAINS, Direction.OUTGOING, Dataset.LABEL, datasetId);
		List<Node> profiles = related(dataset, Relation.CONTAINS, Direction.OUTGOING, Profile.LABEL)
				.collect(Collectors.toList());
		Node detail = detail(profiles.get(0));
		int loci = Math.toIntExact((long) relationships(detail, Relation.HAS, Direction.OUTGOING)
				.findFirst()
				.orElseThrow(() -> new RuntimeException("loci - profiles size:" + profiles.size()))
				.getProperty(Allele.TOTAL));
		int lines = profiles.size();
		String[] ids = new String[lines];
		int[] isolates = new int[lines];
		String[][] alleles = new String[lines][loci];
		IntStream.range(0, lines)
				.peek(i -> ids[i] = (String) profiles.get(i).getProperty(Profile.ID))
				.peek(i -> isolates[i] = Math.toIntExact(relationships(profiles.get(i), Relation.HAS, Direction.INCOMING).count()))
				.forEach(i -> relationships(detail(profiles.get(i)), Relation.HAS, Direction.OUTGOING)
						.map(r -> new Object[] {Math.toIntExact((long)  r.getProperty(Allele.PART)), r.getEndNode().getProperty(Allele.ID)})
						.forEach(p -> alleles[i][((int)p[0]) - 1] = (String) p[1]));
		return new Matrix(ids, isolates, alleles);
	}

	@Override
	public void write(Inference inference) {
		String inferenceId = inference.getId();
		String algorithm = inference.getAlgorithm();
		List<Edge> edges = direct(inference.getEdges());
		String[] ids = inference.getProfileIds();
		Node project = node(Project.LABEL, inference.getProjectId());
		Node dataset = related(project, Relation.CONTAINS, Direction.OUTGOING, Dataset.LABEL, inference.getDatasetId());
		for (Edge edge : edges) {
			Node from = related(dataset, Relation.CONTAINS, Direction.OUTGOING, Profile.LABEL, ids[edge.from()]);
			int fromVersion = version(from);
			Node to = related(dataset, Relation.CONTAINS, Direction.OUTGOING, Profile.LABEL, ids[edge.to()]);
			int toVersion = version(to);
			Map<String, Object> properties = new HashMap<>();
			properties.put(Distance.ID, inferenceId);
			properties.put(Distance.ALGORITHM, algorithm);
			properties.put(Distance.DISTANCE, edge.distance());
			properties.put(Distance.FROM_VERSION, fromVersion);
			properties.put(Distance.TO_VERSION, toVersion);
			properties.put(Distance.DEPRECATED, false);
			createRelationship(from, to, Relation.DISTANCES, properties);
		}

	}

	private List<Edge> direct(List<Edge> edges) {
		List<Edge> directed = new ArrayList<>();
		Integer[] roots = edges.stream()
				.map(Edge::from)
				.filter(i -> edges.stream().noneMatch(edge -> edge.to() == i))
				.distinct()
				.toArray(Integer[]::new);
		List<Edge> toDirect = new ArrayList<>(edges);
		for (Integer root : roots)
			directed.addAll(directEdges(toDirect, root));
		return directed;
	}

	private List<Edge> directEdges(List<Edge> tree, int root) {
		List<Edge> edges = tree.stream().filter(e -> e.from() == root || e.to() == root).collect(Collectors.toList());
		tree.removeAll(edges);
		List<Edge> directed = new ArrayList<>();
		for (Edge edge : edges) {
			if (edge.to() == root)
				edge = new Edge(edge.to(), edge.from(), edge.distance());
			directed.add(edge);
			directed.addAll(directEdges(tree, edge.to()));
		}
		return directed;
	}

}