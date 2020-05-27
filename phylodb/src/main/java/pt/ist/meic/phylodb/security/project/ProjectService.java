package pt.ist.meic.phylodb.security.project;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.security.user.UserRepository;
import pt.ist.meic.phylodb.security.user.model.User;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {

	private ProjectRepository projectRepository;
	private UserRepository userRepository;

	public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
		this.projectRepository = projectRepository;
		this.userRepository = userRepository;
	}

	@Transactional(readOnly = true)
	public Optional<List<VersionedEntity<String>>> getProjects(User.PrimaryKey userId, int page, int limit) {
		return projectRepository.findAllEntities(page, limit, userId);
	}

	@Transactional(readOnly = true)
	public Optional<Project> getProject(String id, long version) {
		return projectRepository.find(id, version);
	}

	@Transactional
	public boolean saveProject(Project project, User.PrimaryKey user) {
		if (project == null || user == null)
			return false;
		List<User.PrimaryKey> users = Arrays.stream(project.getUsers())
				.filter(u -> !u.equals(user))
				.distinct()
				.collect(Collectors.toList());
		users.add(user);
		if (userRepository.anyMissing(users.toArray(new User.PrimaryKey[0])))
			return false;
		return projectRepository.save(project);
	}

	@Transactional
	public boolean deleteProject(String id) {
		return projectRepository.remove(id);
	}

}
