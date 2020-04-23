package pt.ist.meic.phylodb.security.authorization;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.security.SecurityInterceptor;
import pt.ist.meic.phylodb.security.authorization.project.ProjectService;
import pt.ist.meic.phylodb.security.authorization.project.model.Project;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION_VALUE;

@Component
@Order(2)
public class AuthorizationInterceptor extends SecurityInterceptor {

	public static final String PROJECT = "project";

	private ProjectService projectService;

	public AuthorizationInterceptor(ProjectService projectService) {
		this.projectService = projectService;
	}

	@Override
	public boolean handle(HttpServletRequest req, HttpServletResponse res, Object handler) {
		HandlerMethod hm = (HandlerMethod) handler;
		Authorized methodAnnotation = hm.getMethodAnnotation(Authorized.class);
		if (methodAnnotation == null || req.getAttribute(ROLE).equals(Role.ADMIN))
			return true;
		Role methodRole = methodAnnotation.role();
		Permission methodPermission = methodAnnotation.permission();
		String userId = req.getAttribute(ID).toString();
		String provider = req.getAttribute(PROVIDER).toString();
		Optional<String> optional = getProjectId(req);
		if (optional.isPresent()) {
			Optional<Project> optionalProject = optional.flatMap(i -> projectService.getProject(UUID.fromString(i), CURRENT_VERSION_VALUE));
			if(optionalProject.isPresent()) {
				Project project = optionalProject.get();
				boolean included = Arrays.stream(project.getUsers()).anyMatch(u -> u.getId().equals(userId) && u.getProvider().equals(provider));
				if (methodRole.equals(Role.USER) &&
						((methodPermission.equals(Permission.WRITE) && included) ||
								(methodPermission.equals(Permission.READ) && (included || project.getType().equals("public")))))
					return true;
			}
		} else if (!methodAnnotation.required())
			return true;
		res.setStatus(Problem.UNAUTHORIZED.getStatus().value());
		return false;
	}

	private Optional<String> getProjectId(HttpServletRequest req) {
		Object projectId = req.getParameter(PROJECT);
		if (projectId == null) {
			Map<String, Object> pathVariables = (Map<String, Object>) req.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
			projectId = pathVariables.get(PROJECT);
		}
		return projectId != null ? Optional.of(projectId.toString()) : Optional.empty();
	}

}
