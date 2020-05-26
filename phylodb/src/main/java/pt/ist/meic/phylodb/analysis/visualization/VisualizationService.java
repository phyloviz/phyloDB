package pt.ist.meic.phylodb.analysis.visualization;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.analysis.visualization.model.Visualization;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VisualizationService {

	private VisualizationRepository visualizationRepository;

	public VisualizationService(VisualizationRepository visualizationRepository) {
		this.visualizationRepository = visualizationRepository;
	}

	@Transactional(readOnly = true)
	public Optional<List<Visualization>> getVisualizations(String projectId, String datasetId, String analysisId, int page, int limit) {
		return visualizationRepository.findAll(page, limit, projectId, datasetId, analysisId);
	}

	@Transactional(readOnly = true)
	public Optional<Visualization> getVisualization(String projectId, String datasetId, String analysisId, String visualizationId) {
		return visualizationRepository.find(new Visualization.PrimaryKey(projectId, datasetId, analysisId, visualizationId));
	}

	@Transactional
	public boolean deleteVisualization(String projectId, String datasetId, String analysisId, String visualizationId) {
		return visualizationRepository.remove(new Visualization.PrimaryKey(projectId, datasetId, analysisId, visualizationId));
	}

}
