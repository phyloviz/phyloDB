package algorithm.visualization;

import algorithm.inference.model.Analysis;
import algorithm.inference.model.AnalysisKey;
import algorithm.repository.RepositoryImpl;
import algorithm.visualization.model.Visualization;

public class VisualizationRepository extends RepositoryImpl<Visualization, Analysis, AnalysisKey> {


	@Override
	public Analysis findInput(AnalysisKey param) {
		return null;
	}

	@Override
	public void createOutput(Visualization param) {

	}
}
