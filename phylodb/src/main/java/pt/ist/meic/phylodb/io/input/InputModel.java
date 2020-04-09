package pt.ist.meic.phylodb.io.input;

import java.util.Optional;

public interface InputModel<T> {

	Optional<T> toDomainEntity(String... params);

}
