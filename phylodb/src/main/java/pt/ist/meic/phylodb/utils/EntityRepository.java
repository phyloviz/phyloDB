package pt.ist.meic.phylodb.utils;

import org.neo4j.ogm.session.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class EntityRepository<E, K> extends Repository {

	protected EntityRepository(Session session) {
		super(session);
	}

	protected abstract List<E> getAll(Map<String, Object> params, Object... filters);

	protected abstract E get(K key);

	protected abstract boolean exists(E entity);

	protected abstract void create(E entity);

	protected abstract void update(E entity);

	protected abstract void delete(K key);

	public List<E> findAll(int page, Object... filters) {
		int limit = 2; //TODO
		if (page < 0 || limit <= 0)
			return null;
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("page", page * limit);
			put("limit", limit);
		}};
		return getAll(params, filters);
	}

	public E find(K key) {
		if (key == null) return null;
		return get(key);
	}

	public void save(E entity) {
		if (entity == null) return;
		if (exists(entity)) {
			update(entity);
			return;
		}
		create(entity);
	}

	public void remove(K key) {
		if (key == null) return;
		delete(key);
	}

}
