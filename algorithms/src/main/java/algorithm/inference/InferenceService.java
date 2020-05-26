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
			long startTime = System.nanoTime();
			Matrix matrix = repository.read(project, dataset);
			logTime(startTime, "Read");
			algorithm.init(project, dataset, analysis, lvs);
			startTime = System.currentTimeMillis();
			Inference inference = algorithm.compute(matrix);
			logTime(startTime, "Compute");
			startTime = System.currentTimeMillis();
			repository.write(inference);
			logTime(startTime, "Write");
			tx.success();
		}
	}

	private void logTime(long startTime, String s) {
		long stopTime = System.nanoTime();
		long time = stopTime - startTime;
		long minutes = time / 1000000000 / 60;
		long seconds = time / 1000000000 % 60;
		long millis = (time % 1000000000) / 1000000;
		log.info(s + ": " + minutes + " m " + seconds + " s " + millis + " ms");
	}

}
