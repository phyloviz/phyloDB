package pt.ist.meic.phylodb.security.user;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.security.user.model.GetUserOutputModel;
import pt.ist.meic.phylodb.security.user.model.GetUsersOutputModel;
import pt.ist.meic.phylodb.security.user.model.UserInputModel;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Operation;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.utils.controller.Controller;

import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION;

@RestController
@RequestMapping("/users")
public class UserController extends Controller {

	private UserService service;

	public UserController(UserService service) {
		this.service = service;
	}

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getUsers(@RequestParam(value = "page", defaultValue = "0") int page) {
		return getAllJson(l -> service.getUsers(page, l), GetUsersOutputModel::new);
	}

	@GetMapping(path = "/{user}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getUser(
			@PathVariable("user") String userId,
			@RequestParam(value = "provider") String provider,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) Long version
	) {
		return get(() -> service.getUser(userId, provider, version), GetUserOutputModel::new, () -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	@Authorized(role = Role.ADMIN, operation = Operation.WRITE)
	@PutMapping(path = "/{user}")
	public ResponseEntity<?> putUser(
			@PathVariable("user") String userId,
			@RequestParam(value = "provider") String provider,
			@RequestBody UserInputModel input
	) {
		return put(() -> input.toDomainEntity(userId, provider), service::updateUser);
	}

	@Authorized(role = Role.ADMIN, operation = Operation.WRITE)
	@DeleteMapping(path = "/{user}")
	public ResponseEntity<?> deleteUser(
			@PathVariable("user") String userId,
			@RequestParam(value = "provider") String provider
	) {
		return status(() -> service.deleteUser(userId, provider));
	}

}
