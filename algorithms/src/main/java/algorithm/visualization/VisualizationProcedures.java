package algorithm.visualization;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Mode;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

public class VisualizationProcedures {

	@Context
	public GraphDatabaseService database;
	@Context
	public Log log;

	@Procedure(value = "algorithms.visualization.radial", mode = Mode.WRITE)
	public void goeBURST(@Name("project") String project, @Name("dataset") String dataset, @Name("inference") String inference, @Name("visualization") String visualization) {
		VisualizationService service = new VisualizationService(database, log);
		service.radial(project, dataset, inference, visualization);
	}

}
