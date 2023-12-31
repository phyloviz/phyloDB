package pt.ist.meic.phylodb.security.authentication.google;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import pt.ist.meic.phylodb.security.authentication.AuthenticationInterceptor;
import pt.ist.meic.phylodb.security.authentication.TokenInfo;
import pt.ist.meic.phylodb.security.user.UserService;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * GoogleInterceptor is the implementation of {@link AuthenticationInterceptor} that uses the google identity provider
 * to perform instropect the access tokens
 */
@Component
@Order(1)
public class GoogleInterceptor extends AuthenticationInterceptor {

	private static final String INTROSPECTION = "https://oauth2.googleapis.com/tokeninfo?access_token=%s", PROVIDER = "google";

	public GoogleInterceptor(UserService userService) {
		super(userService, PROVIDER);
	}

	protected TokenInfo introspect(String accessToken) throws IOException {
		HttpURLConnection connection;
		URL url = new URL(String.format(INTROSPECTION, accessToken));
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod(HttpMethod.GET.name());
		return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.readValue(connection.getInputStream(), GoogleToken.class);
	}

	@Override
	protected boolean isLastHandler() {
		return false;
	}

}
