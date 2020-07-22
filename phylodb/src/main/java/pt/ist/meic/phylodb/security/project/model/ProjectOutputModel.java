package pt.ist.meic.phylodb.security.project.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.Objects;

/**
 * A ProjectOutputModel is an output model for a project
 * <p>
 * A ProjectOutputModel contains the {@link #id} field which identify the project, and the {@link #version}, and {@link #deprecated}
 * fields which are the version of the project, and the existence status respectively.
 */
public class ProjectOutputModel implements OutputModel {

	protected String id;
	protected long version;
	protected boolean deprecated;

	public ProjectOutputModel() {
	}

	public ProjectOutputModel(Project project) {
		this.id = project.getPrimaryKey();
		this.version = project.getVersion();
		this.deprecated = project.isDeprecated();
	}

	public ProjectOutputModel(VersionedEntity<String> project) {
		this.id = project.getPrimaryKey();
		this.version = project.getVersion();
		this.deprecated = project.isDeprecated();
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

	/**
	 * A ProjectOutputModel.Resumed is the resumed information of a project output model
	 * <p>
	 * A ProjectOutputModel.Resumed is constituted by the {@link #id} field which is the id of the project,
	 * and by the {@link #version} field which is the version of the project.
	 */
	@JsonIgnoreProperties({"deprecated"})
	public static class Resumed extends ProjectOutputModel {

		public Resumed() {
		}

		public Resumed(VersionedEntity<String> project) {
			super(project);
		}

	}

}
