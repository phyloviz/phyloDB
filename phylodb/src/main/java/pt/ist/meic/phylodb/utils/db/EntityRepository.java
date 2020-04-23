package pt.ist.meic.phylodb.utils.db;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class EntityRepository<E, K> extends Repository {

	public static final long CURRENT_VERSION_VALUE = -1;
	public static final String CURRENT_VERSION = "" + CURRENT_VERSION_VALUE;

	protected EntityRepository(Session session) {
		super(session);
	}

	protected abstract Result getAll(int page, int limit, Object... filters);

	protected abstract Result get(K key, Long version);

	protected abstract boolean isPresent(K key);

	protected abstract void store(E entity);

	protected abstract void delete(K key);

	protected abstract E parse(Map<String, Object> row);

	public Optional<List<E>> findAll(int page, int limit, Object... filters) {
		if (page < 0 || limit < 0) return Optional.empty();
		Result result = getAll(page * limit, limit, filters);
		if (result == null) return Optional.empty();
		return Optional.of(StreamSupport.stream(result.spliterator(), false)
				.map(this::parse)
				.collect(Collectors.toList()));
	}

	public Optional<E> find(K key, Long version) {
		if (key == null) return Optional.empty();
		Result result = get(key, version);
		if (result == null) return Optional.empty();
		Iterator<Map<String, Object>> it = result.iterator();
		return !it.hasNext() ? Optional.empty() : Optional.of(parse(it.next()));
	}

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
