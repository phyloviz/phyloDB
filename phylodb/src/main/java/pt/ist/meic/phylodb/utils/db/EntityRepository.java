package pt.ist.meic.phylodb.utils.db;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;

import java.util.Map;

public abstract class EntityRepository<E, K> extends Repository {

	protected EntityRepository(Session session) {
		super(session);
	}

	protected abstract Result getAllEntities(int page, int limit, Object... filters);

	protected abstract E parse(Map<String, Object> row);

	protected abstract boolean isPresent(K key);

	protected abstract void store(E entity);

	protected abstract void delete(K key);

	public boolean exists(K key) {
		return key != null && isPresent(key);
	}

	public boolean save(E entity) {
		if (entity == null)
			return false;
		store(entity);
		return true;
	}

	public boolean remove(K key) {
		if (!exists(key))
			return false;
		delete(key);
		return true;
	}

}
