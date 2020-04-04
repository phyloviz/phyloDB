package pt.ist.meic.phylodb.authentication;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Order(2)
public class AuthorizationInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
		if (handler.getClass() != HandlerMethod.class)
			return true;

		return false;

	}
/*
	private boolean handleProblem(HttpServletResponse res, HttpStatus status, String problem) {
		res.setStatus(status.value());
		res.setHeader(WWW_AUTHENTICATE_HEADER, String.format("%s error=%s", AUTHENTICATION_SCHEME, problem));
		return false;
	}
	*/

}
