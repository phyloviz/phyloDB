package pt.ist.meic.phylodb.security.project.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A GetProjectsOutputModel is the output model representation of a set of {@link Project projects}
 * <p>
 * A GetProjectsOutputModel is constituted by the {@link #projects} field that contains the resumed information of each project.
 * Each resumed information is represented by an {@link ProjectOutputModel.Resumed} object.
 */
public class GetProjectsOutputModel implements OutputModel {

	private final List<ProjectOutputModel.Resumed> projects;

	public GetProjectsOutputModel(List<VersionedEntity<String>> entities) {
		this.projects = entities.stream()
				.map(ProjectOutputModel.Resumed::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<ProjectOutputModel.Resumed>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(projects);
	}

}
