package pt.ist.meic.phylodb.typing.dataset.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.Objects;
import java.util.UUID;

public class DatasetOutputModel implements OutputModel {

	protected UUID project_id;
	protected UUID id;
	protected long version;
	protected boolean deprecated;

	public DatasetOutputModel() {
	}

	public DatasetOutputModel(Dataset dataset) {
		this.project_id = dataset.getPrimaryKey().getProjectId();
		this.id = dataset.getPrimaryKey().getId();
		this.version = dataset.getVersion();
		this.deprecated = dataset.isDeprecated();
	}

	public UUID getProject_id() {
		return project_id;
	}

	public UUID getId() {
		return id;
	}

	public long getVersion() {
		return version;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	@Override
	public ResponseEntity<DatasetOutputModel> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DatasetOutputModel that = (DatasetOutputModel) o;
		return version == that.version &&
				deprecated == that.deprecated &&
				Objects.equals(project_id, that.project_id) &&
				Objects.equals(id, that.id);
	}

}
