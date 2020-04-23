package pt.ist.meic.phylodb.security.authorization.project.model;

import pt.ist.meic.phylodb.security.authentication.user.model.User;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION_VALUE;

public class Project extends Entity<UUID> {

	private final String name;
	private final String type;
	private final String description;
	private final User.PrimaryKey[] users;

	public Project(UUID id, long version, boolean deprecated, String name, String type, String description, User.PrimaryKey[] users) {
		super(id, version, deprecated);
		this.name = name;
		this.type = type;
		this.description = description;
		this.users = users;
	}

	public Project(UUID id, String name, String type, String description, User.PrimaryKey[] users) {
		this(id, CURRENT_VERSION_VALUE, false, name, type, description, users);
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public User.PrimaryKey[] getUsers() {
		return users;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Project project = (Project) o;
		return Objects.equals(id, project.getPrimaryKey()) &&
				Objects.equals(version, project.getVersion()) &&
				Objects.equals(deprecated, project.isDeprecated()) &&
				Objects.equals(name, project.name) &&
				Objects.equals(type, project.type) &&
				Objects.equals(description, project.description) &&
				Arrays.equals(users, project.users);
	}

}
