package pt.ist.meic.phylodb.analysis.visualization.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.Objects;

/**
 * Base class of a visualization output model, which extends {@link OutputModel}
 * <p>
 * An VisualizationOutputModel contains the base information of a visualization, and is constituted by the {@link #project_id}, {@link #dataset_id}, {@link #inference_id}, and {@link #id}
 * fields to identify the visualization. It also contains the {@link #deprecated} field which indicates if the inference is deprecated.
 */
public class VisualizationOutputModel implements OutputModel {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String project_id;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String dataset_id;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String inference_id;
	protected String id;
	protected boolean deprecated;

	public VisualizationOutputModel() {
	}

	public VisualizationOutputModel(Visualization visualization) {
		this.project_id = visualization.getPrimaryKey().getProjectId();
		this.dataset_id = visualization.getPrimaryKey().getDatasetId();
		this.inference_id = visualization.getPrimaryKey().getInferenceId();
		this.id = visualization.getPrimaryKey().getId();
		this.deprecated = visualization.isDeprecated();
	}

	public VisualizationOutputModel(Entity<Visualization.PrimaryKey> visualization) {
		this.project_id = visualization.getPrimaryKey().getProjectId();
		this.dataset_id = visualization.getPrimaryKey().getDatasetId();
		this.inference_id = visualization.getPrimaryKey().getInferenceId();
		this.id = visualization.getPrimaryKey().getId();
		this.deprecated = visualization.isDeprecated();
	}

	public String getProject_id() {
		return project_id;
	}

	public String getDataset_id() {
		return dataset_id;
	}

	public String getInference_id() {
		return inference_id;
	}

	public String getId() {
		return id;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VisualizationOutputModel that = (VisualizationOutputModel) o;
		return deprecated == that.deprecated &&
				Objects.equals(project_id, that.project_id) &&
				Objects.equals(dataset_id, that.dataset_id) &&
				Objects.equals(inference_id, that.inference_id) &&
				Objects.equals(id, that.id);
	}

	@Override
	public ResponseEntity<VisualizationOutputModel> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(this);
	}

	/**
	 * An VisualizationOutputModel.Resumed is the resumed information of a visualization output model
	 * <p>
	 * A VisualizationOutputModel.Resumed is constituted by the {@link #id} field which is the id of the inference.
	 */
	@JsonIgnoreProperties({"project_id", "dataset_id", "analysis_id", "deprecated"})
	public static class Resumed extends VisualizationOutputModel {

		public Resumed() {
		}

		public Resumed(Entity<Visualization.PrimaryKey> visualization) {
			super(visualization);
		}

	}

}
