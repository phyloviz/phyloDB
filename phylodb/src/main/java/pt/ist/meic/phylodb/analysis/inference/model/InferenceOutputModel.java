package pt.ist.meic.phylodb.analysis.inference.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.Objects;

/**
 * Base class of an inference output model, which extends {@link OutputModel}
 * <p>
 * An InferenceOutputModel contains the base information of an inference, and is constituted by the {@link #project_id}, {@link #dataset_id}, and {@link #id}
 * fields to identify the inference. It also contains the {@link #deprecated} field which indicates if the inference is deprecated.
 */
public class InferenceOutputModel implements OutputModel {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String project_id;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String dataset_id;
	protected String id;
	protected boolean deprecated;

	public InferenceOutputModel() {
	}

	public InferenceOutputModel(Inference analysis) {
		this.project_id = analysis.getPrimaryKey().getProjectId();
		this.dataset_id = analysis.getPrimaryKey().getDatasetId();
		this.id = analysis.getPrimaryKey().getId();
		this.deprecated = analysis.isDeprecated();
	}

	public InferenceOutputModel(Entity<Inference.PrimaryKey> analysis) {
		this.project_id = analysis.getPrimaryKey().getProjectId();
		this.dataset_id = analysis.getPrimaryKey().getDatasetId();
		this.id = analysis.getPrimaryKey().getId();
		this.deprecated = analysis.isDeprecated();
	}

	public String getProject_id() {
		return project_id;
	}

	public String getDataset_id() {
		return dataset_id;
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
		InferenceOutputModel that = (InferenceOutputModel) o;
		return deprecated == that.deprecated &&
				Objects.equals(project_id, that.project_id) &&
				Objects.equals(dataset_id, that.dataset_id) &&
				Objects.equals(id, that.id);
	}

	@Override
	public ResponseEntity<InferenceOutputModel> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(this);
	}

	/**
	 * An InferenceOutputModel.Resumed is the resumed information of an inference output model
	 * <p>
	 * An InferenceOutputModel.Resumed is constituted by the {@link #id} field which is the id of the inference.
	 */
	@JsonIgnoreProperties({"project_id", "dataset_id", "deprecated"})
	public static class Resumed extends InferenceOutputModel {

		public Resumed() {
		}

		public Resumed(Entity<Inference.PrimaryKey> inference) {
			super(inference);
		}

	}

}
