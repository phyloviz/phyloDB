package algorithm.inference;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Mode;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

public class InferenceProcedures {

	@Context
	public GraphDatabaseService database;
	@Context
	public Log log;

	@Procedure(value = "algorithms.inference.goeburst", mode = Mode.WRITE)
	public void goeBURST(@Name("project") String project, @Name("dataset") String dataset, @Name("lvs") long lvs, @Name("inference") String analysis) {
		InferenceService service = new InferenceService(database, log);
		service.goeBURST(project, dataset, analysis, lvs);
	}

}
