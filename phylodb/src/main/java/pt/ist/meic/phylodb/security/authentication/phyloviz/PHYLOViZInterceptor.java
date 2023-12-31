package pt.ist.meic.phylodb.security.authentication.phyloviz;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import pt.ist.meic.phylodb.security.authentication.AuthenticationInterceptor;
import pt.ist.meic.phylodb.security.authentication.TokenInfo;
import pt.ist.meic.phylodb.security.user.UserService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * PHYLOViZInterceptor is the implementation of {@link AuthenticationInterceptor} that uses the google identity provider
 * to perform instropect the access tokens
 */
@Component
@Order(1)
public class PHYLOViZInterceptor extends AuthenticationInterceptor {

	private static final String INTROSPECTION = "https://auth.phyloviz.net/realms/phyloviz-web-platform/protocol/openid-connect/token/introspect", PROVIDER = "phyloviz";
	private static final String PARAMETERS = "client_id=phylodb-client&client_secret=%s&token=%s";

	@Value("${phyloviz.client_secret}")
	private String clientSecret;

	public PHYLOViZInterceptor(UserService userService) {
		super(userService, PROVIDER);
	}

	protected TokenInfo introspect(String accessToken) throws IOException {
		HttpURLConnection connection;
		URL url = new URL(INTROSPECTION);
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod(HttpMethod.POST.name());
		connection.setDoOutput(true);
		OutputStream os = connection.getOutputStream();
		os.write(String.format(PARAMETERS, clientSecret, accessToken).getBytes());
		os.flush();
		os.close();
		return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.readValue(connection.getInputStream(), PHYLOViZToken.class);
	}

	@Override
	protected boolean isLastHandler() {
		return true;
	}
}
