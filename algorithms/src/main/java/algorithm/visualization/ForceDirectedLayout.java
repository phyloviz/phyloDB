package algorithm.visualization;

import algorithm.Algorithm;
import org.neo4j.graphdb.Node;
import org.neo4j.procedure.Procedure;

import java.util.stream.Stream;

public class ForceDirectedLayout extends Algorithm {

	@Procedure(value = "algorithms.visualization.force-directed-layout")
	public Stream<ForceDirectedLayoutResult> forceDirectedLayout() {
		return null;
	}

	public class ForceDirectedLayoutResult {
		public final Node newNodeObject;

		public ForceDirectedLayoutResult(Node node) {
			this.newNodeObject = node;
		}
	}
}
