package pt.ist.meic.phylodb.utils.service;

import java.util.List;
import java.util.Optional;

public abstract class UnversionedEntityService<E, K> extends EntityService<E, K> {

	protected abstract Optional<List<Entity<K>>> getAllEntities(int page, int limit, Object... params);

	protected abstract Optional<E> get(K key);

}
