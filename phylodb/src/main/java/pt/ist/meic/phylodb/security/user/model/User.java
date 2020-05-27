package pt.ist.meic.phylodb.security.user.model;

import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.Objects;

import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION_VALUE;

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
