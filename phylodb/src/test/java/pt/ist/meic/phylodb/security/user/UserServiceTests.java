package pt.ist.meic.phylodb.security.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pt.ist.meic.phylodb.ServiceTestsContext;
import pt.ist.meic.phylodb.security.user.model.User;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class UserServiceTests extends ServiceTestsContext {

	private static final int LIMIT = 2;
	private static final User[] STATE = new User[]{USER1, USER2};

	private static Stream<Arguments> getUsers_params() {
		VersionedEntity<User.PrimaryKey> state0 = new VersionedEntity<>(STATE[0].getPrimaryKey(), STATE[0].getVersion(), STATE[0].isDeprecated()),
				state1 = new VersionedEntity<>(STATE[1].getPrimaryKey(), STATE[1].getVersion(), STATE[1].isDeprecated());
		List<VersionedEntity<User.PrimaryKey>> expected1 = new ArrayList<VersionedEntity<User.PrimaryKey>>() {{
			add(state0);
		}};
		List<VersionedEntity<User.PrimaryKey>> expected2 = new ArrayList<VersionedEntity<User.PrimaryKey>>() {{
			add(state0);
			add(state1);
		}};
		return Stream.of(Arguments.of(0, Collections.emptyList()),
				Arguments.of(0, expected1),
				Arguments.of(0, expected2),
				Arguments.of(-1, null));
	}

	private static Stream<Arguments> getUser_params() {
		return Stream.of(Arguments.of(USER1.getPrimaryKey(), 1, USER1),
				Arguments.of(USER1.getPrimaryKey(), 1, null));
	}

	private static Stream<Arguments> updateUser_params() {
		return Stream.of(Arguments.of(USER1, true, 1),
				Arguments.of(USER2, false, 0),
				Arguments.of(null, false, 0));
	}

	private static Stream<Arguments> createUser_params() {
		return Stream.of(Arguments.of(USER1, true, 0),
				Arguments.of(USER2, false, 1),
				Arguments.of(null, false, 0));
	}

	private static Stream<Arguments> deleteUser_params() {
		return Stream.of(Arguments.of(STATE[0].getPrimaryKey(), true),
				Arguments.of(STATE[0].getPrimaryKey(), false));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@ParameterizedTest
	@MethodSource("getUsers_params")
	public void getUsers(int page, List<VersionedEntity<User.PrimaryKey>> expected) {
		Mockito.when(userRepository.findAllEntities(anyInt(), anyInt())).thenReturn(Optional.ofNullable(expected));
		Optional<List<VersionedEntity<User.PrimaryKey>>> result = userService.getUsers(page, LIMIT);
		if (expected == null && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertNotNull(expected);
		assertTrue(result.isPresent());
		List<VersionedEntity<User.PrimaryKey>> users = result.get();
		assertEquals(expected.size(), users.size());
		assertEquals(expected, users);
	}

	@ParameterizedTest
	@MethodSource("getUser_params")
	public void getUser(User.PrimaryKey key, long version, User expected) {
		Mockito.when(userRepository.find(any(), anyLong())).thenReturn(Optional.ofNullable(expected));
		Optional<User> result = userService.getUser(key.getId(), key.getProvider(), version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("updateUser_params")
	public void updateUser(User user, boolean exists, int times) {
		Mockito.when(userRepository.exists(any())).thenReturn(exists);
		userService.updateUser(user);
		Mockito.verify(userRepository, Mockito.times(times)).save(user);
	}

	@ParameterizedTest
	@MethodSource("createUser_params")
	public void createUser(User user, boolean exists, int times) {
		Mockito.when(userRepository.exists(any())).thenReturn(exists);
		userService.createUser(user);
		Mockito.verify(userRepository, Mockito.times(times)).save(user);
	}

	@ParameterizedTest
	@MethodSource("deleteUser_params")
	public void deleteUser(User.PrimaryKey key, boolean expected) {
		Mockito.when(userRepository.remove(any())).thenReturn(expected);
		boolean result = userService.deleteUser(key.getId(), key.getProvider());
		assertEquals(expected, result);
	}

}
