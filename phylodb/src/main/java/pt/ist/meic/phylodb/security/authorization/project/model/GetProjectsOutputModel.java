package pt.ist.meic.phylodb.security.authorization.project.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.List;
import java.util.stream.Collectors;

public class GetProjectsOutputModel implements OutputModel {

	private final List<ProjectOutputModel> entities;

	public GetProjectsOutputModel(List<Project> entities) {
		this.entities = entities.stream()
				.map(ProjectOutputModel::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<ProjectOutputModel>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(entities);
	}



}
