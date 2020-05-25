package algorithm;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.logging.Log;

public abstract class Service {

	public GraphDatabaseService database;
	public Log log;

	public Service(GraphDatabaseService database, Log log) {
		this.database = database;
		this.log = log;
	}

}
