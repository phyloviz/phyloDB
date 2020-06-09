package algorithm.inference;

import algorithm.Service;
import algorithm.inference.implementation.GoeBURST;
import algorithm.inference.model.Inference;
import algorithm.inference.model.Matrix;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.logging.Log;

public class InferenceService extends Service {

	public InferenceService(GraphDatabaseService database, Log log) {
		super(database, log);
	}

	public void goeBURST(String project, String dataset, String analysis, long lvs) {
		InferenceRepository repository = new InferenceRepository(database);
		GoeBURST algorithm = new GoeBURST();
		try (Transaction tx = database.beginTx()) {
			Matrix matrix = repository.read(project, dataset);
			algorithm.init(project, dataset, analysis, lvs);
			Inference inference = algorithm.compute(matrix);
			repository.write(inference);
			tx.success();
		}
	}

}
