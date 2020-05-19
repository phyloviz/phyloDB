package pt.ist.meic.phylodb.security.authentication.user.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.Objects;

public class UserOutputModel implements OutputModel {

	protected String email;
	protected String provider;
	protected long version;
	protected boolean deprecated;

	public UserOutputModel() {
	}

	public UserOutputModel(User user) {
		this.email = user.getPrimaryKey().getId();
		this.provider = user.getPrimaryKey().getProvider();
		this.version = user.getVersion();
		this.deprecated = user.isDeprecated();
	}

	public String getEmail() {
		return email;
	}

	public String getProvider() {
		return provider;
	}

	public long getVersion() {
		return version;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	@Override
	public ResponseEntity<UserOutputModel> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserOutputModel that = (UserOutputModel) o;
		return version == that.version &&
				deprecated == that.deprecated &&
				Objects.equals(email, that.email) &&
				Objects.equals(provider, that.provider);
	}

	@JsonIgnoreProperties({"deprecated"})
	public static class Resumed extends UserOutputModel {

		public Resumed() {
		}

		public Resumed(User user) {
			super(user);
		}

	}

}
