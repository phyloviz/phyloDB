package pt.ist.meic.phylodb.security.authentication.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.security.authentication.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

	private UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional(readOnly = true)
	public Optional<List<User>> getUsers(int page, int limit) {
		return userRepository.findAll(page, limit);
	}

	@Transactional(readOnly = true)
	public Optional<User> getUser(String user, String provider, int version) {
		return userRepository.find(new User.PrimaryKey(user, provider), version);
	}

	@Transactional
	public boolean updateUser(User user) {
		if (!userRepository.exists(user.getPrimaryKey()))
			return false;
		return userRepository.save(user);
	}

	@Transactional
	public void createUser(User user) {
		if (userRepository.exists(user.getPrimaryKey()))
			return;
		userRepository.save(user);
	}

	@Transactional
	public boolean deleteUser(String user, String provider) {
		return userRepository.remove(new User.PrimaryKey(user, provider));
	}

}
