package pt.ist.meic.phylodb.security.user.model;

import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.Objects;

import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION_VALUE;

/**
 * A User is a domain entity that represent the several users of the platform
 * <p>
 * A User is constituted by the {@link #id} field to identify the user, the {@link #deprecated} field which indicates if the user is deprecated, and
 * the {@link #version} field that is the version of the user. It is also constituted by the {@link #role}, that is the {@link Role role} of the user,
 */
public class User extends VersionedEntity<User.PrimaryKey> {

	private final Role role;

	public User(String email, String provider, long version, boolean deprecated, Role role) {
		super(new PrimaryKey(email, provider), version, deprecated);
		this.role = role;
	}

	public User(String email, String provider, Role role) {
		this(email, provider, CURRENT_VERSION_VALUE, false, role);
	}

	public Role getRole() {
		return role;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return super.equals(user) &&
				Objects.equals(role, user.role);
	}

	/**
	 * A User.PrimaryKey is the identification of a user
	 * <p>
	 * A User.PrimaryKey is constituted by the {@link #id}, {@link #provider} fields which identify the user.
	 */
	public static class PrimaryKey {

		private String id;
		private String provider;

		public PrimaryKey() {
		}

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

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			PrimaryKey that = (PrimaryKey) o;
			return Objects.equals(id, that.id) &&
					Objects.equals(provider, that.provider);
		}

	}

}
