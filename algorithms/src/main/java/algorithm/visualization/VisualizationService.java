package algorithm.visualization;

import algorithm.utils.Service;
import algorithm.visualization.implementation.Radial;
import algorithm.visualization.model.Tree;
import algorithm.visualization.model.Visualization;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.logging.Log;

/**
 * Class that contains operations to execute visualization algorithms
 * <p>
 * The service responsibility is to obtain the data from the db, execute the algorithm, and store the result in the db
 */
public class VisualizationService extends Service {

	public VisualizationService(GraphDatabaseService database, Log log) {
		super(database, log);
	}

	/**
	 * Executes the radial algorithm, with the data resulting from the inference identified in the parameters, and stores the result
	 *
	 * @param project   project id
	 * @param dataset   dataset id
	 * @param inference inference id
	 * @param id        visualization id
	 */
	public void radial(String project, String dataset, String inference, String id) {
		VisualizationRepository repository = new VisualizationRepository(database);
		Radial algorithm = new Radial();
		algorithm.init(project, dataset, inference, id);
		Tree tree;
		try (Transaction tx1 = database.beginTx()) {
			tree = repository.read(tx1, project, dataset, inference);
			tx1.commit();
		}
		Visualization visualization = algorithm.compute(tree);
		try (Transaction tx2 = database.beginTx()) {
			repository.write(tx2, visualization);
			tx2.commit();
		}
	}

}
