package algorithm;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;

public class Algorithm {

	@Context
	public GraphDatabaseService database;
	@Context
	public Log log;
}
