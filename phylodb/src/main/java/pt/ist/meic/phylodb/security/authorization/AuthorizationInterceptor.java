package pt.ist.meic.phylodb.security.authorization;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.security.SecurityInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Order(2)
public class AuthorizationInterceptor extends SecurityInterceptor {

	private static final String PROJECT = "projectId";

	@Override
	public boolean handle(HttpServletRequest req, HttpServletResponse res, Object handler) {
		HandlerMethod hm = (HandlerMethod) handler;
		Authorized methodAnnotation = hm.getMethodAnnotation(Authorized.class);
		if (methodAnnotation == null)
			return true;
		Role methodRole = methodAnnotation.value();
		Role userRole = Role.valueOf(req.getAttribute(ROLE).toString());
		String userId = req.getAttribute(ID).toString();
		String projectId = req.getParameter(PROJECT);
		if (userRole.equals(Role.ADMIN) || (methodRole.equals(Role.USER) && projectService.hasAccess(userId, projectId)))
			return true;
		res.setStatus(Problem.UNAUTHORIZED.getStatus().value());
		return false;
	}

}
