package pt.ist.meic.phylodb.security.authorization.project.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.List;
import java.util.stream.Collectors;

public class GetProjectsOutputModel implements OutputModel {

	private final List<ProjectOutputModel.Resumed> entities;

	public GetProjectsOutputModel(List<Project> entities) {
		this.entities = entities.stream()
				.map(ProjectOutputModel.Resumed::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<ProjectOutputModel.Resumed>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(entities);
	}

}
