package pt.ist.meic.phylodb.security.authentication;

import org.springframework.http.HttpHeaders;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.security.SecurityInterceptor;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.security.user.UserService;
import pt.ist.meic.phylodb.security.user.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION_VALUE;

/**
 * AuthenticationInterceptor is the base class for authentication interceptors
 */
public abstract class AuthenticationInterceptor extends SecurityInterceptor {

	private static final String AUTHENTICATION_SCHEME = "Bearer";

	private final UserService userService;
	private final String provider;

	public AuthenticationInterceptor(UserService userService, String provider) {
		this.userService = userService;
		this.provider = provider;
	}

	/**
	 * Retrieves the token information for a given access token
	 *
	 * @param accessToken a String representing the access token
	 * @return the token information access token
	 * @throws IOException if its not possible to retrieve the token information
	 */
	protected abstract TokenInfo introspect(String accessToken) throws IOException;

	/**
	 * @return {@code true} if this authentication interceptor is the last interceptor in the chain, otherwise {@code false}
	 */
	protected abstract boolean isLastHandler();

	@Override
	public boolean handle(HttpServletRequest req, HttpServletResponse res, Object handler) {
		String auth = req.getHeader(HttpHeaders.AUTHORIZATION);
		if (!provider.equals(req.getParameter(PROVIDER)))
			return !isLastHandler() || handleProblem(res, Problem.INVALID_REQUEST);
		String[] authorization;
		if (auth == null || !(authorization = auth.split(" "))[0].equals(AUTHENTICATION_SCHEME))
			return handleProblem(res, Problem.INVALID_REQUEST);
		try {
			TokenInfo info = introspect(authorization[1]);
			if (!info.isValid())
				return handleProblem(res, Problem.INVALID_TOKEN);
			String id = info.getId();
			userService.createUser(new User(id, provider, Role.USER));
			Optional<User> optionalUser = userService.getUser(id, provider, CURRENT_VERSION_VALUE);
			req.setAttribute(ID, id);
			req.setAttribute(PROVIDER, provider);
			req.setAttribute(ROLE, optionalUser.get().getRole());
			return true;
		} catch (IOException ignored) {
			return handleProblem(res, Problem.INVALID_TOKEN);
		}
	}

	private boolean handleProblem(HttpServletResponse res, Problem problem) {
		res.setStatus(problem.getStatus().value());
		res.setHeader(HttpHeaders.WWW_AUTHENTICATE, String.format("%s error=%s", AUTHENTICATION_SCHEME, problem));
		return false;
	}

}
