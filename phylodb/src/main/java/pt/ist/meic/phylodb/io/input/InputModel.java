package pt.ist.meic.phylodb.io.input;

import java.util.Optional;

/**
 * The InputModel interface specifies that each input model must implement a method to transform itself in a domain object
 *
 * @param <T> any domain object type
 */
public interface InputModel<T> {

	/**
	 * Creates an domain object
	 *
	 * @param params other parameters received in the request that can be used to create the domain object
	 * @return an {@link Optional} of a domain object if it is possible to create it, otherwise an empty optional
	 */
	Optional<T> toDomainEntity(String... params);

}
