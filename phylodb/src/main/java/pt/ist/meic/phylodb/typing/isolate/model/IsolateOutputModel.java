package pt.ist.meic.phylodb.typing.isolate.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.Objects;
import java.util.UUID;

public class IsolateOutputModel implements OutputModel {

	protected UUID project_id;
	protected UUID dataset_id;
	protected String id;
	protected long version;
	protected boolean deprecated;

	public IsolateOutputModel() {
	}

	public IsolateOutputModel(Isolate isolate) {
		this.project_id = isolate.getPrimaryKey().getProjectId();
		this.dataset_id = isolate.getPrimaryKey().getDatasetId();
		this.id = isolate.getPrimaryKey().getId();
		this.version = isolate.getVersion();
		this.deprecated = isolate.isDeprecated();
	}

	public UUID getProject_id() {
		return project_id;
	}

	public UUID getDataset_id() {
		return dataset_id;
	}

	public String getId() {
		return id;
	}

	public long getVersion() {
		return version;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	@Override
	public ResponseEntity<IsolateOutputModel> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		IsolateOutputModel that = (IsolateOutputModel) o;
		return version == that.version &&
				deprecated == that.deprecated &&
				Objects.equals(project_id, that.project_id) &&
				Objects.equals(dataset_id, that.dataset_id) &&
				Objects.equals(id, that.id);
	}

}
