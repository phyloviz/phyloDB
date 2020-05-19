package pt.ist.meic.phylodb.security.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import pt.ist.meic.phylodb.ControllerTestsContext;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.security.authentication.user.model.GetUserOutputModel;
import pt.ist.meic.phylodb.security.authentication.user.model.User;
import pt.ist.meic.phylodb.security.authentication.user.model.UserInputModel;
import pt.ist.meic.phylodb.security.authentication.user.model.UserOutputModel;
import pt.ist.meic.phylodb.security.authorization.Role;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class UserControllerTests extends ControllerTestsContext {

	private static Stream<Arguments> getUsers_params() {
		String uri = "/users";
		List<User> users = new ArrayList<User>() {{
			add(new User("id", "provider", Role.USER));
		}};
		MockHttpServletRequestBuilder req1 = get(uri).param("page", "0"),
				req2 = get(uri), req3 = get(uri).param("page", "-10");
		List<UserOutputModel> result = users.stream()
				.map(UserOutputModel::new)
				.collect(Collectors.toList());
		return Stream.of(Arguments.of(req1, users, HttpStatus.OK, result, null),
				Arguments.of(req1, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req2, users, HttpStatus.OK, result, null),
				Arguments.of(req2, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req3, null, HttpStatus.BAD_REQUEST, null, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> getUser_params() {
		String uri = "/users/%s";
		User user = new User("id", "provider", Role.USER);
		User.PrimaryKey key = user.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = get(String.format(uri, key.getId())).param("provider", key.getProvider()).param("version", "1"),
				req2 = get(String.format(uri, key.getId())).param("provider", key.getProvider()),
				req3 = get(String.format(uri, key.getId()));
		return Stream.of(Arguments.of(req1, user, HttpStatus.OK, new GetUserOutputModel(user)),
				Arguments.of(req1, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())),
				Arguments.of(req2, user, HttpStatus.OK, new GetUserOutputModel(user)),
				Arguments.of(req2, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())),
				Arguments.of(req3, null, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> updateUser_params() {
		String uri = "/users/%s";
		User user = new User("id", "provider", Role.USER);
		User.PrimaryKey key = user.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = put(String.format(uri, key.getId())).param("provider", key.getProvider()),
				req2 = put(String.format(uri, key.getId()));
		UserInputModel input1 = new UserInputModel(key.getId(), key.getProvider(), user.getRole().getName()),
				input2 = new UserInputModel("different", key.getProvider(), "random");
		return Stream.of(Arguments.of(req1, input1, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req1, input1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req1, input2, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req1, null, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req2, input1, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> deleteUser_params() {
		String uri = "/users/%s";
		User user = new User("id", "provider", Role.USER);
		User.PrimaryKey key = user.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = delete(String.format(uri, key.getId())).param("provider", key.getProvider()),
				req2 = delete(String.format(uri, key.getId()));
		return Stream.of(Arguments.of(req1, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req2, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(authenticationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
		Mockito.when(authorizationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
	}

	@ParameterizedTest
	@MethodSource("getUsers_params")
	public void getUsers(MockHttpServletRequestBuilder req, List<User> users, HttpStatus expectedStatus, List<UserOutputModel> expectedResult, ErrorOutputModel expectedError) throws Exception {
		Mockito.when(userService.getUsers(anyInt(), anyInt())).thenReturn(Optional.ofNullable(users));
		MockHttpServletResponse result = http.executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful()) {
			List<Map<String, Object>> parsed = http.parseResult(List.class, result);
			assertEquals(expectedResult.size(), parsed.size());
			if (expectedResult.size() > 0) {
				for (int i = 0; i < expectedResult.size(); i++) {
					Map<String, Object> p = parsed.get(i);
					assertEquals(expectedResult.get(i).getEmail(), p.get("email"));
					assertEquals(expectedResult.get(i).getProvider(), p.get("provider"));
					assertEquals(expectedResult.get(i).getVersion(), Long.parseLong(p.get("version").toString()));
				}
			}
		} else
			assertEquals(expectedError, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("getUser_params")
	public void getUser(MockHttpServletRequestBuilder req, User user, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(userService.getUser(anyString(), anyString(), anyLong())).thenReturn(Optional.ofNullable(user));
		MockHttpServletResponse result = http.executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful())
			assertEquals(expectedResult, http.parseResult(GetUserOutputModel.class, result));
		else
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("updateUser_params")
	public void updateUser(MockHttpServletRequestBuilder req, UserInputModel input, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		if (input != null)
			Mockito.when(userService.updateUser(any())).thenReturn(ret);
		MockHttpServletResponse result = http.executeRequest(req, input);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("deleteUser_params")
	public void deleteUser(MockHttpServletRequestBuilder req, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(userService.deleteUser(any(), any())).thenReturn(ret);
		MockHttpServletResponse result = http.executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

}
