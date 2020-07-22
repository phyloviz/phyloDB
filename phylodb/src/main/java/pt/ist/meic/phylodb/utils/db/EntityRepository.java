package pt.ist.meic.phylodb.utils.db;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.Map;

/**
 * A EntityRepository is a repository that contains CRUD operations to manage domain objects that can be identified by an id
 *
 * @param <E> type of entity
 * @param <K> type of entity id
 */
public abstract class EntityRepository<E, K> extends Repository {

	protected EntityRepository(Session session) {
		super(session);
	}

	/**
	 * Retrieves a page of {@link Entity} as a result
	 *
	 * @param page    number of the page to retrieve
	 * @param limit   number of {@link Entity} to retrieve by page
	 * @param filters used to filter the query results
	 * @return a page of {@link Entity} as a {@link Result}
	 */
	protected abstract Result getAllEntities(int page, int limit, Object... filters);

	/**
	 * Parses a row from a {@link Result}
	 *
	 * @param row row resulting from a {@link Result}
	 * @return a {@link E} object
	 */
	protected abstract E parse(Map<String, Object> row);

	/**
	 * Verifies if an entity with identified by the {@link K key} exists
	 *
	 * @param key entity id
	 * @return {@code true} if it exists
	 */
	protected abstract boolean isPresent(K key);

	/**
	 * Saves the entity
	 *
	 * @param entity entity to be saved
	 */
	protected abstract void store(E entity);

	/**
	 * Deprecates the entity identified by the {@link K key}
	 *
	 * @param key entity id
	 */
	protected abstract void delete(K key);

	/**
	 * Verifies if an entity identified by the {@link K key} exists
	 *
	 * @param key entity id
	 * @return {@code true} if it exists
	 */
	public boolean exists(K key) {
		return key != null && isPresent(key);
	}

	/**
	 * Saves the entity
	 *
	 * @param entity entity to be saved
	 */
	public boolean save(E entity) {
		if (entity == null)
			return false;
		store(entity);
		return true;
	}

	/**
	 * Deprecates the entity identified by the {@link K key}
	 *
	 * @param key entity id
	 */
	public boolean remove(K key) {
		if (!exists(key))
			return false;
		delete(key);
		return true;
	}

}
