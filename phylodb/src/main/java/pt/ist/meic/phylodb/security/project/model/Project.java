package pt.ist.meic.phylodb.security.project.model;

import pt.ist.meic.phylodb.security.user.model.User;
import pt.ist.meic.phylodb.security.authorization.Visibility;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.Arrays;
import java.util.Objects;

import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION_VALUE;

public class Project extends VersionedEntity<String> {

	private final String name;
	private final Visibility visibility;
	private final String description;
	private final User.PrimaryKey[] users;

	public Project(String id, long version, boolean deprecated, String name, Visibility visibility, String description, User.PrimaryKey[] users) {
		super(id, version, deprecated);
		this.name = name;
		this.visibility = visibility;
		this.description = description;
		this.users = users;
	}

	public Project(String id, String name, Visibility visibility, String description, User.PrimaryKey[] users) {
		this(id, CURRENT_VERSION_VALUE, false, name, visibility, description, users);
	}

	public String getName() {
		return name;
	}

	public Visibility getVisibility() {
		return visibility;
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
				Objects.equals(visibility, project.visibility) &&
				Objects.equals(description, project.description) &&
				Arrays.equals(users, project.users);
	}

}
