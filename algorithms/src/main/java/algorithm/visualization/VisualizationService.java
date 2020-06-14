package algorithm.visualization;

import algorithm.Service;
import algorithm.visualization.implementation.Radial;
import algorithm.visualization.model.Tree;
import algorithm.visualization.model.Vertex;
import algorithm.visualization.model.Visualization;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.logging.Log;

public class VisualizationService extends Service {

	public VisualizationService(GraphDatabaseService database, Log log) {
		super(database, log);
	}

	public void radial(String project, String dataset, String inference, String id) {
		VisualizationRepository repository = new VisualizationRepository(database);
		Radial algorithm = new Radial();
		try (Transaction tx = database.beginTx()) {
			Tree tree = repository.read(project, dataset, inference);
			algorithm.init(project, dataset, inference, id);
			Visualization visualization = algorithm.compute(tree);
			repository.write(visualization);
			tx.success();
		}
	}
}
