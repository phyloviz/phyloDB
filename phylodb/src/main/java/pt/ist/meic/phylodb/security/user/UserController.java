package pt.ist.meic.phylodb.security.user;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Operation;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.security.user.model.GetUserOutputModel;
import pt.ist.meic.phylodb.security.user.model.GetUsersOutputModel;
import pt.ist.meic.phylodb.security.user.model.User;
import pt.ist.meic.phylodb.security.user.model.UserInputModel;
import pt.ist.meic.phylodb.utils.controller.Controller;

import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION;

/**
 * Class that contains the endpoints to manage users
 * <p>
 * The endpoints responsibility is to parse the input, call the respective service, and to format the resulting output.
 */
@RestController
@RequestMapping("/users")
public class UserController extends Controller {

	private UserService service;

	public UserController(UserService service) {
		this.service = service;
	}

	/**
	 * Endpoint to retrieve the specified page of {@link User users}.
	 * <p>
	 * Returns the page with resumed information of each user. It requires the user to
	 * be authenticated.
	 *
	 * @param page number of the page to retrieve
	 * @return a {@link ResponseEntity<GetUsersOutputModel>} representing the specified users page or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getUsers(@RequestParam(value = "page", defaultValue = "0") int page) {
		return getAllJson(l -> service.getUsers(page, l), GetUsersOutputModel::new);
	}

	/**
	 * Endpoint to retrieve the specified {@link User user}.
	 * <p>
	 * Returns all information of the specified user. It requires the user to
	 * be authenticated.
	 *
	 * @param userId   identifier of the {@link User user}
	 * @param provider provider that the user is registered
	 * @param version  version of the {@link User user}
	 * @return a {@link ResponseEntity<GetUserOutputModel>} representing the specified user or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@GetMapping(path = "/{user}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getUser(
			@PathVariable("user") String userId,
			@RequestParam(value = "provider") String provider,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) Long version
	) {
		return get(() -> service.getUser(userId, provider, version), GetUserOutputModel::new, () -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	/**
	 * Endpoint to store the given {@link User user}.
	 * <p>
	 * Saves a user by parsing the input model. It requires the user to be an admin.
	 *
	 * @param userId   identifier of the {@link User user}
	 * @param provider provider that the user is registered
	 * @param input    user input model
	 * @return a {@link ResponseEntity<NoContentOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.ADMIN, operation = Operation.WRITE)
	@PutMapping(path = "/{user}")
	public ResponseEntity<?> putUser(
			@PathVariable("user") String userId,
			@RequestParam(value = "provider") String provider,
			@RequestBody UserInputModel input
	) {
		return put(() -> input.toDomainEntity(userId, provider), service::updateUser);
	}

	/**
	 * Endpoint to deprecate the given {@link User user}.
	 * <p>
	 * Removes the specified user. It requires the user to be an admin.
	 *
	 * @param userId   identifier of the {@link User user}
	 * @param provider provider that the user is registered
	 * @return a {@link ResponseEntity<NoContentOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.ADMIN, operation = Operation.WRITE)
	@DeleteMapping(path = "/{user}")
	public ResponseEntity<?> deleteUser(
			@PathVariable("user") String userId,
			@RequestParam(value = "provider") String provider
	) {
		return status(() -> service.deleteUser(userId, provider));
	}

}
