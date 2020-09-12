package algorithm.utils;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.logging.Log;

/**
 * Class which contains the common fields of a service
 */
public abstract class Service {

	public GraphDatabaseService database;
	public Log log;

	public Service(GraphDatabaseService database, Log log) {
		this.database = database;
		this.log = log;
	}

}
