package pt.ist.meic.phylodb;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pt.ist.meic.phylodb.security.authentication.google.GoogleInterceptor;
import pt.ist.meic.phylodb.security.authentication.phyloviz.PHYLOViZInterceptor;
import pt.ist.meic.phylodb.security.authorization.AuthorizationInterceptor;

/**
 * Configuration of the application
 */
@Configuration
public class AppConfiguration implements WebMvcConfigurer {

	private final GoogleInterceptor googleInterceptor;
	private final PHYLOViZInterceptor phylovizInterceptor;
	private final AuthorizationInterceptor authorizationInterceptor;

	public AppConfiguration(GoogleInterceptor googleInterceptor, PHYLOViZInterceptor phylovizInterceptor, AuthorizationInterceptor authorizationInterceptor) {
		this.googleInterceptor = googleInterceptor;
		this.phylovizInterceptor = phylovizInterceptor;
		this.authorizationInterceptor = authorizationInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(googleInterceptor);
		registry.addInterceptor(phylovizInterceptor);
		registry.addInterceptor(authorizationInterceptor);
	}

}
