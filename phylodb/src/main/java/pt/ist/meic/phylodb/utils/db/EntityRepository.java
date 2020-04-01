package pt.ist.meic.phylodb.utils.db;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static pt.ist.meic.phylodb.utils.db.Status.*;

public abstract class EntityRepository<E, K> extends Repository {

	public static final int CURRENT_VERSION_VALUE = -1;
	public static final String CURRENT_VERSION = "" + CURRENT_VERSION_VALUE;

	protected EntityRepository(Session session) {
		super(session);
	}

	protected abstract Result getAll(int page, int limit, Object... filters);

	protected abstract Result get(K key, int version);

	protected abstract boolean isPresent(K key);

	protected abstract void store(E entity);

	protected abstract void delete(K key);

	protected abstract E parse(Map<String, Object> row);

	public List<E> findAll(int page, int limit, Object... filters) {
		if(page < 0 || limit < 0) return null;
		Result result = getAll(page * limit, limit, filters);
		if(result == null) return null;
		return StreamSupport.stream(result.spliterator(), false)
				.map(this::parse)
				.collect(Collectors.toList());
	}

	public E find(K key, int version) {
		if(key == null) return null;
		Result result = get(key, version);
		if(result == null) return null;
		Iterator<Map<String, Object>> it = result.iterator();
		return !it.hasNext() ? null : parse(it.next());
	}

	public boolean exists(K key) {
		return key != null && isPresent(key);
	}

	public Status save(E entity) {
		if (entity == null)
			return UNCHANGED;
		store(entity);
		return UPDATED;
	}

	public Status remove(K key) {
		if (!isPresent(key))
			return UNCHANGED;
		delete(key);
		return DELETED;
	}

}
