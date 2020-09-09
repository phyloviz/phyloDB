package pt.ist.meic.phylodb.security.project;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.security.user.UserRepository;
import pt.ist.meic.phylodb.security.user.model.User;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class that contains operations to manage projects
 * <p>
 * The service responsibility is to guarantee that the database state is not compromised and verify all business rules.
 */
@Service
public class ProjectService extends pt.ist.meic.phylodb.utils.service.Service  {

	private ProjectRepository projectRepository;
	private UserRepository userRepository;

	public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
		this.projectRepository = projectRepository;
		this.userRepository = userRepository;
	}

	/**
	 * Operation to retrieve the information of the requested projects that the user has access to
	 *
	 * @param userId the user that is related to the projects to be retrieved
	 * @param page   number of the page to retrieve
	 * @param limit  number of projects to retrieve by page
	 * @return an {@link Optional} with a {@link List} of {@link VersionedEntity<String>}, which is the resumed information of each project
	 */
	@Transactional(readOnly = true)
	public Optional<List<VersionedEntity<String>>> getProjects(User.PrimaryKey userId, int page, int limit) {
		return projectRepository.findAllEntities(page, limit, userId);
	}

	/**
	 * Operation to retrieve the requested project
	 *
	 * @param id      identifier of the {@link Project project}
	 * @param version version of the project
	 * @return an {@link Optional} of {@link Project}, which is the requested project
	 */
	@Transactional(readOnly = true)
	public Optional<Project> getProject(String id, long version) {
		return projectRepository.find(id, version);
	}

	/**
	 * Operation to save a project
	 * <p>
	 * It will save the given project, and associate the user that to it. If all users exist then the project is saved.
	 *
	 * @param project {@link Project project} to be saved
	 * @param user    {@link User.PrimaryKey user id} to associate to the project
	 * @return {@code true} if the project was saved
	 */
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

	/**
	 * Operation to deprecate a project
	 *
	 * @param id identifier of the {@link Project project}
	 * @return {@code true} if the project was deprecated
	 */
	@Transactional
	public boolean deleteProject(String id) {
		return projectRepository.remove(id);
	}

}
