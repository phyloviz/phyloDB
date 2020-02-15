package algorithm;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;

public interface Algorithm<T, R> {

	R compute(T param);
}
