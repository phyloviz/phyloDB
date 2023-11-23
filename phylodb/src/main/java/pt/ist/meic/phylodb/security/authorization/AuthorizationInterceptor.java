package pt.ist.meic.phylodb.security.authorization;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import org.springframework.core.annotation.AnnotatedMethod;



import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.security.SecurityInterceptor;
import pt.ist.meic.phylodb.security.project.ProjectService;
import pt.ist.meic.phylodb.security.project.model.Project;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION_VALUE;

/**
 * AuthorizationInterceptor is the implementation of {@link SecurityInterceptor} that validates if the user performing the
 * request has access to the respective project.
 * <p>
 * A user is authorized when:
 * - Its an admin
 * - The project and belongs to the user
 * - The project is public and doesn't belong to the user, but the activity is management and its a read operation
 * - The project is public and doesn't belong to the user, but the activity is algorithms
 * - The resource doesn't belong to a project
 */
@Component
@Order(2)
public class AuthorizationInterceptor extends SecurityInterceptor {

	public static final String PROJECT = "project";

	private final ProjectService projectService;

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
			if (annotation.role() == Role.USER) {
				Optional<Project> optional = projectService.getProject(projectId.toString(), CURRENT_VERSION_VALUE);
				if (optional.isPresent()) {
					Project project = optional.get();
					boolean included = Arrays.stream(project.getUsers())
							.anyMatch(u -> u.getId().equals(userId) && u.getProvider().equals(provider));
					if (annotation.activity() == Activity.MANAGEMENT && ((operation == Operation.WRITE && included) ||
							(operation == Operation.READ && (included || project.getVisibility() == Visibility.PUBLIC))))
						return true;
					else if (annotation.activity() == Activity.ALGORITHMS && (included || project.getVisibility() == Visibility.PUBLIC))
						return true;
				}
			}
		} else if (!annotation.required())
			return true;
		res.setStatus(Problem.UNAUTHORIZED.getStatus().value());
		return false;
	}

}
