package algorithm.visualization;

import algorithm.Service;
import algorithm.visualization.implementation.Radial;
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
			//long startTime = System.nanoTime();
			Vertex tree = repository.read(project, dataset, inference);
			//logTime(startTime, "Read");
			algorithm.init(project, dataset, inference, id);
			//startTime = System.currentTimeMillis();
			Visualization visualization = algorithm.compute(tree);
			//logTime(startTime, "Compute");
			//startTime = System.currentTimeMillis();
			repository.write(visualization);
			//logTime(startTime, "Write");
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
