package algorithm.repository;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;

public abstract class RepositoryImpl<T, R, U> implements GraphRepository<T, R, U> {

	@Context
	public GraphDatabaseService database;
	@Context
	public Log log;

}
