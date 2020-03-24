package pt.ist.meic.phylodb.input;

import java.util.Optional;

public interface Input<T> {

	Optional<T> toDomainEntity(String... params);
}
