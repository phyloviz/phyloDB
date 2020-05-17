package pt.ist.meic.phylodb.analysis.visualization.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.Objects;
import java.util.UUID;

public class VisualizationOutputModel implements OutputModel {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected UUID project_id;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected UUID dataset_id;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected UUID analysis_id;
	protected UUID id;
	protected boolean deprecated;

	public VisualizationOutputModel() {
	}

	public VisualizationOutputModel(Visualization visualization) {
		this.project_id = visualization.getPrimaryKey().getProjectId();
		this.dataset_id = visualization.getPrimaryKey().getDatasetId();
		this.analysis_id = visualization.getPrimaryKey().getAnalysisId();
		this.id = visualization.getPrimaryKey().getId();
		this.deprecated = visualization.isDeprecated();
	}

	public UUID getProject_id() {
		return project_id;
	}

	public UUID getDataset_id() {
		return dataset_id;
	}

	public UUID getAnalysis_id() {
		return analysis_id;
	}

	public UUID getId() {
		return id;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	@Override
	public ResponseEntity<VisualizationOutputModel> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VisualizationOutputModel that = (VisualizationOutputModel) o;
		return deprecated == that.deprecated &&
				Objects.equals(project_id, that.project_id) &&
				Objects.equals(dataset_id, that.dataset_id) &&
				Objects.equals(analysis_id, that.analysis_id) &&
				Objects.equals(id, that.id);
	}

	@JsonIgnoreProperties({"project_id, dataset_id, analysis_id"})
	public static class Resumed extends VisualizationOutputModel {

		public Resumed() {
		}

		public Resumed(Visualization visualization) {
			super(visualization);
		}

	}

}
