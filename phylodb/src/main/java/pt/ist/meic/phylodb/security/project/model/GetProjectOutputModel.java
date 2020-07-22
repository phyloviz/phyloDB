package pt.ist.meic.phylodb.security.project.model;

import pt.ist.meic.phylodb.security.user.model.User;

import java.util.Arrays;
import java.util.Objects;

/**
 * A GetProjectOutputModel is the output model representation of a {@link Project}
 * <p>
 * A GetProjectOutputModel is constituted by the {@link #id} field to identify the project, the {@link #deprecated}, and {@link #version} fields which
 * indicates if the project is deprecated, and what version it has. It also contains the {@link #name}, that is the project name, the {@link #visibility}
 * which is the project visibility, either private or public, the {@link #description}, that is the project description and
 * the {@link #users}, which are the users that have access to the project.
 */
public class GetProjectOutputModel extends ProjectOutputModel {

	private String name;
	private String visibility;
	private String description;
	private UserKeyOutputModel[] users;

	public GetProjectOutputModel() {
	}

	public GetProjectOutputModel(Project project) {
		super(project);
		this.name = project.getName();
		this.visibility = project.getVisibility().getName();
		this.description = project.getDescription();
		this.users = Arrays.stream(project.getUsers()).map(UserKeyOutputModel::new).toArray(UserKeyOutputModel[]::new);
	}

	public String getName() {
		return name;
	}

	public String getVisibility() {
		return visibility;
	}

	public String getDescription() {
		return description;
	}

	public UserKeyOutputModel[] getUsers() {
		return users;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		GetProjectOutputModel that = (GetProjectOutputModel) o;
		return super.equals(that) &&
				Objects.equals(name, that.name) &&
				Objects.equals(visibility, that.visibility) &&
				Objects.equals(description, that.description) &&
				Arrays.equals(users, that.users);
	}

	/**
	 * A UserKeyOutputModel is the output model representation of a {@link User.PrimaryKey}
	 * <p>
	 * A UserKeyOutputModel is constituted by the {@link #email}, {@link #provider} to specify which user it represents.
	 */
	private static class UserKeyOutputModel {

		private String email;
		private String provider;

		public UserKeyOutputModel() {
		}

		public UserKeyOutputModel(User.PrimaryKey key) {
			this.email = key.getId();
			this.provider = key.getProvider();
		}

		public String getEmail() {
			return email;
		}

		public String getProvider() {
			return provider;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			UserKeyOutputModel that = (UserKeyOutputModel) o;
			return Objects.equals(email, that.email) &&
					Objects.equals(provider, that.provider);
		}

	}

}
