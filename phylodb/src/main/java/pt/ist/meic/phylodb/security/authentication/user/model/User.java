package pt.ist.meic.phylodb.security.authentication.user.model;

import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.utils.service.Entity;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION_VALUE;

public class User extends Entity<User.PrimaryKey> {

	private final Role role;

	public User(String email, String provider, int version, boolean deprecated, Role role) {
		super(new PrimaryKey(email, provider), version, deprecated);
		this.role = role;
	}

	public User(String email, String provider, Role role) {
		this(email, provider, CURRENT_VERSION_VALUE, false, role);
	}

	public Role getRole() {
		return role;
	}

	public static class PrimaryKey {

		private final String id;
		private final String provider;

		public PrimaryKey(String email, String provider) {
			this.id = email;
			this.provider = provider;
		}

		public String getId() {
			return id;
		}

		public String getProvider() {
			return provider;
		}

	}

}
