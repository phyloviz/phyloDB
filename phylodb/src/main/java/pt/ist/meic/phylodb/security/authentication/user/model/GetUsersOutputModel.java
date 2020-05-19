package pt.ist.meic.phylodb.security.authentication.user.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.List;
import java.util.stream.Collectors;

public class GetUsersOutputModel implements OutputModel {

	private final List<UserOutputModel.Resumed> entities;

	public GetUsersOutputModel(List<User> entities) {
		this.entities = entities.stream()
				.map(UserOutputModel.Resumed::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<UserOutputModel.Resumed>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(entities);
	}

}
