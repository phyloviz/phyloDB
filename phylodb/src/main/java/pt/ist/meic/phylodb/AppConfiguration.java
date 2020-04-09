package pt.ist.meic.phylodb;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pt.ist.meic.phylodb.security.authentication.AuthenticationInterceptor;
import pt.ist.meic.phylodb.security.authorization.AuthorizationInterceptor;

@Configuration
public class AppConfiguration implements WebMvcConfigurer {

	private final AuthenticationInterceptor authenticationInterceptor;
	private final AuthorizationInterceptor authorizationInterceptor;

	public AppConfiguration(AuthenticationInterceptor authenticationInterceptor, AuthorizationInterceptor authorizationInterceptor) {
		this.authenticationInterceptor = authenticationInterceptor;
		this.authorizationInterceptor = authorizationInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authenticationInterceptor);
		registry.addInterceptor(authorizationInterceptor);
	}

}
