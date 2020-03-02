package pt.ist.meic.phylodb.utils.db;

import org.neo4j.ogm.session.Session;

import java.util.List;

import static pt.ist.meic.phylodb.utils.db.Status.*;

public abstract class EntityRepository<E, K> extends Repository {

	protected EntityRepository(Session session) {
		super(session);
	}

	protected abstract List<E> getAll(int page, int limit, Object... filters);

	protected abstract E get(K key);

	protected abstract boolean exists(E entity);

	protected abstract void create(E entity);

	protected abstract void update(E entity);

	protected abstract void delete(K key);

	public List<E> findAll(int page, int limit, Object... filters) {
		if (page < 0 || limit <= 0)
			return null;
		return getAll(page * limit, limit, filters);
	}

	public E find(K key) {
		if (key == null) return null;
		return get(key);
	}

	public Status save(E entity) {
		if (entity == null) return UNCHANGED;
		if (exists(entity)) {
			update(entity);
			return UPDATED;
		}
		create(entity);
		return CREATED;
	}

	public Status remove(K key) {
		if (key == null) return UNCHANGED;
		delete(key);
		return DELETED;
	}

}
