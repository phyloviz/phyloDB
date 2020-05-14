package pt.ist.meic.phylodb.analysis.inference.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.Objects;
import java.util.UUID;

public class AnalysisOutputModel implements OutputModel {

	protected UUID project_id;
	protected UUID dataset_id;
	protected UUID id;
	protected boolean deprecated;

	public AnalysisOutputModel() {
	}

	public AnalysisOutputModel(Analysis analysis) {
		this.project_id = analysis.getPrimaryKey().getProjectId();
		this.dataset_id = analysis.getPrimaryKey().getDatasetId();
		this.id = analysis.getPrimaryKey().getId();
		this.deprecated = analysis.isDeprecated();
	}

	public UUID getProject_id() {
		return project_id;
	}

	public UUID getDataset_id() {
		return dataset_id;
	}

	public UUID getId() {
		return id;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	@Override
	public ResponseEntity<AnalysisOutputModel> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AnalysisOutputModel that = (AnalysisOutputModel) o;
		return deprecated == that.deprecated &&
				Objects.equals(project_id, that.project_id) &&
				Objects.equals(dataset_id, that.dataset_id) &&
				Objects.equals(id, that.id);
	}

}
