package pt.ist.meic.phylodb.utils.service;

public abstract class EntityService<E, K> extends Service {

	protected abstract boolean save(E entity);

	protected abstract boolean remove(K key);

}
