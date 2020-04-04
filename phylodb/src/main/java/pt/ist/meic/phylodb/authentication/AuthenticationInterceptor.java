package pt.ist.meic.phylodb.authentication;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import pt.ist.meic.phylodb.output.mediatype.Problem;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
@Order(1)
public class AuthenticationInterceptor implements HandlerInterceptor {

	private static final String USERINFO_ENDPOINT = "https://openidconnect.googleapis.com/v1/userinfo",
			AUTHORIZATION_HEADER = "Authorization", WWW_AUTHENTICATE_HEADER = "WWW-Authenticate", AUTHENTICATION_SCHEME = "Bearer";

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
		if (handler.getClass() != HandlerMethod.class)
			return true;
		String auth = req.getHeader(AUTHORIZATION_HEADER);
		if (auth != null) {
			String[] authorization = auth.split(" ");
			if (authorization[0].equals(AUTHENTICATION_SCHEME)) {
				try {
					UserInfo info = getUserInfo(authorization[1]);
					if (info != null) {
						req.setAttribute("user", info.getEmail());
						return true;
					}
				} catch (IOException ignored) {
					return handleProblem(res, HttpStatus.UNAUTHORIZED, Problem.INVALID_TOKEN);
				}
			}
		}
		return handleProblem(res, HttpStatus.BAD_REQUEST, Problem.INVALID_REQUEST);
	}

	private static UserInfo getUserInfo(String accessToken) throws IOException {
		HttpURLConnection connection;
		URL url = new URL(String.format("%s?access_token=%s", USERINFO_ENDPOINT, accessToken));
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.readValue(connection.getInputStream(), UserInfo.class);
	}

	private boolean handleProblem(HttpServletResponse res, HttpStatus status, String problem) {
		res.setStatus(status.value());
		res.setHeader(WWW_AUTHENTICATE_HEADER, String.format("%s error=%s", AUTHENTICATION_SCHEME, problem));
		return false;
	}

	private static class UserInfo {

		private String picture;
		private String email;
		private boolean verified_email;

		public String getPicture() {
			return picture;
		}

		public String getEmail() {
			return email;
		}

		public boolean isVerified_email() {
			return verified_email;
		}

		public UserInfo() {
		}

	}
}
