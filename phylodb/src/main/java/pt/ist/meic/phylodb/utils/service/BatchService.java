package pt.ist.meic.phylodb.utils.service;

import java.util.List;
import java.util.Optional;

public abstract class BatchService<E, K> extends VersionedEntityService<E, K> {

	protected abstract Optional<List<E>> getAll(int page, int limit, Object... params);

	protected abstract boolean saveAll(List<E> entities);

}
