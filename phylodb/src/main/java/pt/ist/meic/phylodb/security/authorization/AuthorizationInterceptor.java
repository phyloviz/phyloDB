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
		Authorized annotation = ((HandlerMethod) handler).getMethodAnnotation(Authorized.class);
		if (annotation == null || req.getAttribute(ROLE).equals(Role.ADMIN))
			return true;
		Operation operation = annotation.operation();
		String userId = req.getAttribute(ID).toString();
		String provider = req.getAttribute(PROVIDER).toString();
		Map<String, Object> pathVariables = (Map<String, Object>) req.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		Object projectId = pathVariables.getOrDefault(PROJECT, req.getParameter(PROJECT));
		if (projectId != null) {
			if(annotation.role() == Role.USER) {
				Optional<Project> optional = projectService.getProject(UUID.fromString(projectId.toString()), CURRENT_VERSION_VALUE);
				if (optional.isPresent()) {
					Project project = optional.get();
					boolean included = Arrays.stream(project.getUsers())
							.anyMatch(u -> u.getId().equals(userId) && u.getProvider().equals(provider));
					if(annotation.activity() == Activity.MANAGEMENT && ((operation == Operation.WRITE && included) ||
							(operation == Operation.READ && (included || project.getType().equals("public")))))
						return true;
					else if (included || project.getType().equals("public"))
						return true;
				}
			}
		} else if (!annotation.required())
			return true;
		res.setStatus(Problem.UNAUTHORIZED.getStatus().value());
		return false;
	}

}
