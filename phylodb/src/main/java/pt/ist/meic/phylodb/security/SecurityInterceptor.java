package pt.ist.meic.phylodb.security;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SecurityInterceptor is an abstract base class for security concerns, namely authentication and authorization
 */
public abstract class SecurityInterceptor implements HandlerInterceptor {

	public static final String PROVIDER = "provider", ID = "id", ROLE = "role";

	/**
	 * Allows the request to continue after validating it through the {@link #handle(HttpServletRequest, HttpServletResponse, Object)}
	 *
	 * @param req     current HTTP request
	 * @param res     current HTTP response
	 * @param handler chosen handler to execute, for type and/or instance evaluation
	 * @return {@code true} if the execution chain should proceed with the next interceptor or the handler itself, otherwise {@code false}.
	 */
	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
		if (handler.getClass() != HandlerMethod.class)
			return true;
		//return handle(req, res, handler);
		return true;
	}

	/**
	 * Validates if the request is valid under the implemented restrictions
	 *
	 * @param req     current HTTP request
	 * @param res     current HTTP response
	 * @param handler chosen handler to execute, for type and/or instance evaluation
	 * @return {@code true} if the execution chain should proceed with the next interceptor or the handler itself, otherwise {@code false}.
	 */
	public abstract boolean handle(HttpServletRequest req, HttpServletResponse res, Object handler);

}
