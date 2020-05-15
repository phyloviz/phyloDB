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
	public Optional<List<Visualization>> getVisualizations(UUID projectId, UUID datasetId, UUID analysisId, int page, int limit) {
		return visualizationRepository.findAll(page, limit, projectId, datasetId, analysisId);
	}

	@Transactional(readOnly = true)
	public Optional<Visualization> getVisualization(UUID projectId, UUID datasetId, UUID analysisId, UUID visualizationId) {
		return visualizationRepository.find(new Visualization.PrimaryKey(projectId, datasetId, analysisId, visualizationId));
	}

	@Transactional
	public boolean deleteVisualization(UUID projectId, UUID datasetId, UUID analysisId, UUID visualizationId) {
		return visualizationRepository.remove(new Visualization.PrimaryKey(projectId, datasetId, analysisId, visualizationId));
	}

}
