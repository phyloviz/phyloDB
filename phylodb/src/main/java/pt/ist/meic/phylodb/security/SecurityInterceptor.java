package pt.ist.meic.phylodb.security;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class SecurityInterceptor implements HandlerInterceptor {

	protected static final String ID = "id", ROLE = "role";

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
		if (handler.getClass() != HandlerMethod.class)
			return true;
		return handle(req, res, handler);
	}

	public abstract boolean handle(HttpServletRequest req, HttpServletResponse res, Object handler);

}
