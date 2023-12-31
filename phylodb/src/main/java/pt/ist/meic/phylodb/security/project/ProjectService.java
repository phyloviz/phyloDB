package pt.ist.meic.phylodb.security.project;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.security.user.UserRepository;
import pt.ist.meic.phylodb.security.user.model.User;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;
import pt.ist.meic.phylodb.utils.service.VersionedEntityService;

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
public class ProjectService extends VersionedEntityService<Project, String> {

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
	public Optional<List<VersionedEntity<String>>> getProjects(String userId, String provider, int page, int limit) {
		return getAllEntities(page, limit, new User.PrimaryKey(userId, provider));
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
		return get(id, version);
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
		return save(project);
	}

	/**
	 * Operation to deprecate a project
	 *
	 * @param id identifier of the {@link Project project}
	 * @return {@code true} if the project was deprecated
	 */
	@Transactional
	public boolean deleteProject(String id) {
		return remove(id);
	}

	@Override
	protected Optional<List<VersionedEntity<String>>> getAllEntities(int page, int limit, Object... params) {
		return projectRepository.findAllEntities(page, limit, params[0]);
	}

	@Override
	protected Optional<Project> get(String key, long version) {
		return projectRepository.find(key, version);
	}

	@Override
	protected boolean save(Project entity) {
		return projectRepository.save(entity);
	}

	@Override
	protected boolean remove(String key) {
		return projectRepository.remove(key);
	}

}
