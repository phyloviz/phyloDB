package pt.ist.meic.phylodb.security.project.model;

import pt.ist.meic.phylodb.security.user.model.User;

import java.util.Arrays;
import java.util.Objects;

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
