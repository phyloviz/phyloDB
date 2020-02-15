package algorithm.visualization;

import algorithm.inference.model.Analysis;
import algorithm.inference.model.AnalysisKey;
import algorithm.procedure.Procedure;
import algorithm.visualization.implementation.ForceDirectedLayout;
import algorithm.visualization.model.Visualization;
import org.neo4j.procedure.Mode;
import org.neo4j.procedure.Name;

public class VisualizationProcedures implements Procedure<VisualizationAlgorithm, AnalysisKey> {

	@Override
	public void execute(VisualizationAlgorithm algorithm, AnalysisKey param) {
		VisualizationRepository repository =  new VisualizationRepository();
		Analysis analysis = repository.findInput(param);
		Visualization visualization = algorithm.compute(analysis);
		repository.createOutput(visualization);
	}

	@org.neo4j.procedure.Procedure(value = "algorithms.visualization.force-directed-layout", mode = Mode.WRITE)
	public void forceDirectedLayout(@Name("analysis") String dataset, @Name("analysis") String analysis) {
		execute(new ForceDirectedLayout(), new AnalysisKey(dataset, analysis));
	}

}
