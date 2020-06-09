package pt.ist.meic.phylodb.analysis.visualization.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.Objects;

public class VisualizationOutputModel implements OutputModel {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String project_id;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String dataset_id;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String analysis_id;
	protected String id;
	protected boolean deprecated;

	public VisualizationOutputModel() {
	}

	public VisualizationOutputModel(Visualization visualization) {
		this.project_id = visualization.getPrimaryKey().getProjectId();
		this.dataset_id = visualization.getPrimaryKey().getDatasetId();
		this.analysis_id = visualization.getPrimaryKey().getInferenceId();
		this.id = visualization.getPrimaryKey().getId();
		this.deprecated = visualization.isDeprecated();
	}

	public VisualizationOutputModel(Entity<Visualization.PrimaryKey> visualization) {
		this.project_id = visualization.getPrimaryKey().getProjectId();
		this.dataset_id = visualization.getPrimaryKey().getDatasetId();
		this.analysis_id = visualization.getPrimaryKey().getInferenceId();
		this.id = visualization.getPrimaryKey().getId();
		this.deprecated = visualization.isDeprecated();
	}

	public String getProject_id() {
		return project_id;
	}

	public String getDataset_id() {
		return dataset_id;
	}

	public String getAnalysis_id() {
		return analysis_id;
	}

	public String getId() {
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

	@JsonIgnoreProperties({"project_id", "dataset_id", "analysis_id", "deprecated"})
	public static class Resumed extends VisualizationOutputModel {

		public Resumed() {
		}

		public Resumed(Entity<Visualization.PrimaryKey> visualization) {
			super(visualization);
		}

	}

}
