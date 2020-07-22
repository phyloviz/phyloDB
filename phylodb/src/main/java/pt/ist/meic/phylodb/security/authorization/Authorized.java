package pt.ist.meic.phylodb.security.authorization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Authorized annotation represents that, the handler operations needs the user to have access to the project
 * <p>
 * An user is authorized depending on the type of activity, his role, and the type of authorization. There might be handlers
 * that don't always need the user to be authorized.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorized {

	Activity activity() default Activity.MANAGEMENT;

	Role role();

	Operation operation();

	boolean required() default true;

}
