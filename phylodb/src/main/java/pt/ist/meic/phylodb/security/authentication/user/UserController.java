package pt.ist.meic.phylodb.security.authentication.user;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.MultipleOutputModel;
import pt.ist.meic.phylodb.security.authentication.user.model.User;
import pt.ist.meic.phylodb.security.authentication.user.model.UserInputModel;
import pt.ist.meic.phylodb.security.authentication.user.model.UserOutputModel;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Permission;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.utils.controller.Controller;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION;

@RestController
@RequestMapping("/users")
public class UserController extends Controller<User> {

	private UserService service;

	public UserController(UserService service) {
		this.service = service;
	}

	@GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getUsers(@RequestParam(value = "page", defaultValue = "0") int page) {
		String type = MediaType.APPLICATION_JSON_VALUE;
		return getAll(type, l -> service.getUsers(page, l), MultipleOutputModel::new, null);
	}

	@GetMapping(path = "/{user}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getUser(
			@PathVariable("user") String userId,
			@RequestParam(value = "provider") String provider,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) int version
	) {
		String type = MediaType.APPLICATION_JSON_VALUE;
		return get(() -> service.getUser(userId, provider, version), UserOutputModel::new, () -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	@Authorized(role = Role.ADMIN, permission = Permission.WRITE)
	@PutMapping(path = "/{user}")
	public ResponseEntity<?> putUser(
			@PathVariable("user") String userId,
			@RequestBody UserInputModel input
	) {
		return put(() -> input.toDomainEntity(userId), service::updateUser);
	}

	@Authorized(role = Role.ADMIN, permission = Permission.WRITE)
	@DeleteMapping(path = "/{user}")
	public ResponseEntity<?> deleteUser(
			@PathVariable("user") String userId,
			@RequestParam(value = "provider") String provider
	) {
		return status(() -> service.deleteUser(userId, provider));
	}

}
