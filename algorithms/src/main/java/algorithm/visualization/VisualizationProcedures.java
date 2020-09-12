package algorithm.visualization;

import org.neo4j.procedure.Mode;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

/**
 * VisualizationProcedures contains the inference algorithms procedures
 */
public class VisualizationProcedures extends algorithm.utils.Procedure {

	/**
	 * Executes the radial algorithm, for a given inference of a dataset within a project. The result is stored using the visualization id
	 *
	 * @param project       project id
	 * @param dataset       dataset id
	 * @param inference     inference id
	 * @param visualization visualization id
	 */
	@Procedure(value = "algorithms.visualization.radial", mode = Mode.WRITE)
	public void goeBURST(@Name("project") String project, @Name("dataset") String dataset, @Name("inference") String inference, @Name("visualization") String visualization) {
		VisualizationService service = new VisualizationService(database, log);
		service.radial(project, dataset, inference, visualization);
	}

}
