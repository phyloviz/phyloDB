package pt.ist.meic.phylodb.security.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import pt.ist.meic.phylodb.Test;
import pt.ist.meic.phylodb.security.authentication.user.UserRepository;
import pt.ist.meic.phylodb.security.authentication.user.UserService;
import pt.ist.meic.phylodb.security.authentication.user.model.User;
import pt.ist.meic.phylodb.security.authorization.Role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class UserServiceTests extends Test {

	private static final int LIMIT = 2;
	private static final User first = new User("1one", "one", 1, false, Role.USER);
	private static final User second = new User("2two", "two", 1, false, Role.USER);
	private static final User[] state = new User[]{first, second};
	@MockBean
	private UserRepository repository;
	@InjectMocks
	private UserService service;

	private static Stream<Arguments> getUsers_params() {
		List<User> expected1 = new ArrayList<User>() {{
			add(state[0]);
		}};
		List<User> expected2 = new ArrayList<User>() {{
			add(state[0]);
			add(state[1]);
		}};
		return Stream.of(Arguments.of(0, Collections.emptyList()),
				Arguments.of(0, expected1),
				Arguments.of(0, expected2),
				Arguments.of(-1, null));
	}

	private static Stream<Arguments> getUser_params() {
		return Stream.of(Arguments.of(first.getPrimaryKey(), 1, first),
				Arguments.of(first.getPrimaryKey(), 1, null));
	}

	private static Stream<Arguments> updateUser_params() {
		return Stream.of(Arguments.of(first, true, 1),
				Arguments.of(second, false, 0),
				Arguments.of(null, false, 0));
	}

	private static Stream<Arguments> createUser_params() {
		return Stream.of(Arguments.of(first, true, 0),
				Arguments.of(second, false, 1),
				Arguments.of(null, false, 0));
	}

	private static Stream<Arguments> deleteUser_params() {
		return Stream.of(Arguments.of(state[0].getPrimaryKey(), true),
				Arguments.of(state[0].getPrimaryKey(), false));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@ParameterizedTest
	@MethodSource("getUsers_params")
	public void getUsers(int page, List<User> expected) {
		Mockito.when(repository.findAll(anyInt(), anyInt())).thenReturn(Optional.ofNullable(expected));
		Optional<List<User>> result = service.getUsers(page, LIMIT);
		if (expected == null && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertNotNull(expected);
		assertTrue(result.isPresent());
		List<User> users = result.get();
		assertEquals(expected.size(), users.size());
		assertEquals(expected, users);
	}

	@ParameterizedTest
	@MethodSource("getUser_params")
	public void getUser(User.PrimaryKey key, long version, User expected) {
		Mockito.when(repository.find(any(), anyLong())).thenReturn(Optional.ofNullable(expected));
		Optional<User> result = service.getUser(key.getId(), key.getProvider(), version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("updateUser_params")
	public void updateUser(User user, boolean exists, int times) {
		Mockito.when(repository.exists(any())).thenReturn(exists);
		service.updateUser(user);
		Mockito.verify(repository, Mockito.times(times)).save(user);
	}

	@ParameterizedTest
	@MethodSource("createUser_params")
	public void createUser(User user, boolean exists, int times) {
		Mockito.when(repository.exists(any())).thenReturn(exists);
		service.createUser(user);
		Mockito.verify(repository, Mockito.times(times)).save(user);
	}

	@ParameterizedTest
	@MethodSource("deleteUser_params")
	public void deleteUser(User.PrimaryKey key, boolean expected) {
		Mockito.when(repository.remove(any())).thenReturn(expected);
		boolean result = service.deleteUser(key.getId(), key.getProvider());
		assertEquals(expected, result);
	}

}
