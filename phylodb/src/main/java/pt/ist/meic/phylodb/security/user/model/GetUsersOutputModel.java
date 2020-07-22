package pt.ist.meic.phylodb.security.user.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A GetUsersOutputModel is the output model representation of a set of {@link User users}
 * <p>
 * A GetUsersOutputModel is constituted by the {@link #users} field that contains the resumed information of each user.
 * Each resumed information is represented by an {@link UserOutputModel.Resumed} object.
 */
public class GetUsersOutputModel implements OutputModel {

	private final List<UserOutputModel.Resumed> users;

	public GetUsersOutputModel(List<VersionedEntity<User.PrimaryKey>> entities) {
		this.users = entities.stream()
				.map(UserOutputModel.Resumed::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<UserOutputModel.Resumed>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(users);
	}

}
