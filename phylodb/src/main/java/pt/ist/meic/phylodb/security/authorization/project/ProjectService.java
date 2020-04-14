package pt.ist.meic.phylodb.security.authorization.project;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.security.authentication.user.model.User;
import pt.ist.meic.phylodb.security.authorization.project.model.Project;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectService {

	private ProjectRepository projectRepository;

	public ProjectService(ProjectRepository projectRepository) {
		this.projectRepository = projectRepository;
	}

	@Transactional(readOnly = true)
	public Optional<List<Project>> getProjects(User.PrimaryKey userId, int page, int limit) {
		return projectRepository.findAll(page, limit, userId);
	}

	@Transactional(readOnly = true)
	public Optional<Project> getProject(UUID id, int version) {
		return projectRepository.find(id, version);
	}

	@Transactional
	public boolean saveProject(Project project, User.PrimaryKey user) {
		List<User.PrimaryKey> users = Arrays.stream(project.getUsers())
				.filter(u -> u.equals(user))
				.distinct()
				.collect(Collectors.toList());
		users.add(user);
		return projectRepository.save(project);
	}

	@Transactional
	public boolean deleteProject(UUID id) {
		return projectRepository.remove(id);
	}

	@Transactional(readOnly = true)
	public boolean containsUser(String projectId, User.PrimaryKey userId) {
		return projectRepository.containsUser(projectId, userId);
	}

}
