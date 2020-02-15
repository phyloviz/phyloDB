package pt.ist.meic.phylodb.authentication.user;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.authentication.user.model.UserInputModel;

@RestController
@RequestMapping("/users")
public class UserController {

	private UserService service;

	public UserController(UserService service) {
		this.service = service;
	}

	@GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getUsers() {
		return null;
	}

	@GetMapping(path = "/{user}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getUser(
			@PathVariable("project") String user
	) {
		return null;
	}

	@PutMapping(path = "/{user}")
	public ResponseEntity putUser(
			@PathVariable("user") String userId,
			@RequestBody UserInputModel user
	) {
		return null;
	}

	@DeleteMapping(path = "/{user}")
	public ResponseEntity deleteUser(
			@PathVariable("user") String user
	) {
		return null;
	}

}
