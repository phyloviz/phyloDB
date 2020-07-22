package pt.ist.meic.phylodb.security.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.security.user.model.User;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.List;
import java.util.Optional;

/**
 * Class that contains operations to manage users
 * <p>
 * The service responsibility is to guarantee that the database state is not compromised and verify all business rules.
 */
@Service
public class UserService {

	private UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * Operation to retrieve the information of the requested users
	 *
	 * @param page  number of the page to retrieve
	 * @param limit number of users to retrieve by page
	 * @return an {@link Optional} with a {@link List} of {@link VersionedEntity<User.PrimaryKey>}, which is the resumed information of each user
	 */
	@Transactional(readOnly = true)
	public Optional<List<VersionedEntity<User.PrimaryKey>>> getUsers(int page, int limit) {
		return userRepository.findAllEntities(page, limit);
	}

	/**
	 * Operation to retrieve the requested user
	 *
	 * @param user     identifier of the {@link User user}
	 * @param provider provider that the user is registered
	 * @param version  version of the user
	 * @return an {@link Optional} of {@link User}, which is the requested user
	 */
	@Transactional(readOnly = true)
	public Optional<User> getUser(String user, String provider, long version) {
		return userRepository.find(new User.PrimaryKey(user, provider), version);
	}

	/**
	 * Operation to update a user
	 *
	 * @param user user to be saved
	 * @return {@code true} if the user was updated
	 */
	@Transactional
	public boolean updateUser(User user) {
		if (user == null || !userRepository.exists(user.getPrimaryKey()))
			return false;
		return userRepository.save(user);
	}

	/**
	 * Operation to create a user
	 *
	 * @param user user to be saved
	 */
	@Transactional
	public void createUser(User user) {
		if (user == null || userRepository.exists(user.getPrimaryKey()))
			return;
		userRepository.save(user);
	}

	/**
	 * Operation to deprecate a user
	 *
	 * @param user     identifier of the {@link User user}
	 * @param provider provider that the user is registered
	 * @return {@code true} if the user was deprecated
	 */
	@Transactional
	public boolean deleteUser(String user, String provider) {
		return userRepository.remove(new User.PrimaryKey(user, provider));
	}

}
