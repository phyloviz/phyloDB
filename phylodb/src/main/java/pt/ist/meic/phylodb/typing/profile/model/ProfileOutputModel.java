package pt.ist.meic.phylodb.typing.profile.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.Reference;

import java.util.Objects;
import java.util.UUID;

public class ProfileOutputModel implements OutputModel {

	protected UUID project_id;
	protected UUID dataset_id;
	protected String id;
	protected long version;
	protected boolean deprecated;

	public ProfileOutputModel() {
	}

	public ProfileOutputModel(Profile profile) {
		this.project_id = profile.getPrimaryKey().getProjectId();
		this.dataset_id = profile.getPrimaryKey().getDatasetId();
		this.id = profile.getPrimaryKey().getId();
		this.version = profile.getVersion();
		this.deprecated = profile.isDeprecated();
	}

	public ProfileOutputModel(UUID projectId, UUID datasetId, Reference<String> profile) {
		this.project_id = projectId;
		this.dataset_id = datasetId;
		this.id = profile.getPrimaryKey();
		this.version = profile.getVersion();
		this.deprecated = profile.isDeprecated();
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
	public ResponseEntity<ProfileOutputModel> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProfileOutputModel that = (ProfileOutputModel) o;
		return version == that.version &&
				deprecated == that.deprecated &&
				Objects.equals(project_id, that.project_id) &&
				Objects.equals(dataset_id, that.dataset_id) &&
				Objects.equals(id, that.id);
	}

}
