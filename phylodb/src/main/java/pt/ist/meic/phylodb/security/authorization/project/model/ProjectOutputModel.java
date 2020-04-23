package pt.ist.meic.phylodb.security.authorization.project.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.Objects;
import java.util.UUID;

public class ProjectOutputModel implements OutputModel {

	protected UUID id;
	protected long version;
	protected boolean deprecated;

	public ProjectOutputModel() {
	}

	public ProjectOutputModel(Project project) {
		this.id = project.getPrimaryKey();
		this.version = project.getVersion();
		this.deprecated = project.isDeprecated();
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
	public ResponseEntity<ProjectOutputModel> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProjectOutputModel that = (ProjectOutputModel) o;
		return Objects.equals(id, that.getId()) &&
				version == that.version &&
				deprecated == that.deprecated;
	}

}
