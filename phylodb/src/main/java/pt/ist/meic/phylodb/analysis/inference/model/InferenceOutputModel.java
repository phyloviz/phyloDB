package pt.ist.meic.phylodb.analysis.inference.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.Objects;

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
	public ResponseEntity<InferenceOutputModel> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(this);
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

	@JsonIgnoreProperties({"project_id", "dataset_id", "deprecated"})
	public static class Resumed extends InferenceOutputModel {

		public Resumed() {
		}

		public Resumed(Inference inference) {
			super(inference);
		}

	}

}
