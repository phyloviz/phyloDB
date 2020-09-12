package algorithm.utils;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;

/**
 * Class which contains the context to define procedures
 */
public abstract class Procedure {

	@Context
	public GraphDatabaseService database;
	@Context
	public Log log;
}
