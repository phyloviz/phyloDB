package pt.ist.meic.phylodb.security.authorization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorized {

	Activity activity() default Activity.MANAGEMENT;

	Role role();

	Operation operation();

	boolean required() default true;

}
