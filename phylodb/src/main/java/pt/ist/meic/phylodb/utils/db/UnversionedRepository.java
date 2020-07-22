package pt.ist.meic.phylodb.utils.db;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * An UnversionedRepository is an {@link EntityRepository} that contains operations to manage entities that aren't versioned
 *
 * @param <E> type of entity
 * @param <K> type of entity id
 */
public abstract class UnversionedRepository<E, K> extends EntityRepository<E, K> {

	protected UnversionedRepository(Session session) {
		super(session);
	}

	/**
	 * Parses a row from a {@link Result} into an {@link Entity<K> entity}
	 *
	 * @param row row from a {@link Result}
	 * @return an entity identified by {@link K}
	 */
	protected abstract Entity<K> parseEntity(Map<String, Object> row);

	/**
	 * Retrieves a {@link Result} containing the entity identified by {@link K key}
	 *
	 * @param key entity id
	 * @return {@link Result} containing the entity identified by {@link K key}
	 */
	protected abstract Result get(K key);

	/**
	 * Retrieves a page of {@link Entity<K>} as a result
	 *
	 * @param page    number of the page to retrieve
	 * @param limit   number of {@link Entity<K>} to retrieve by page
	 * @param filters used to filter the query results
	 * @return page of {@link Entity<K>}
	 */
	public Optional<List<Entity<K>>> findAllEntities(int page, int limit, Object... filters) {
		if (page < 0 || limit < 0) return Optional.empty();
		Result result = getAllEntities(page * limit, limit, filters);
		if (result == null) return Optional.empty();
		return Optional.of(StreamSupport.stream(result.spliterator(), false)
				.map(this::parseEntity)
				.collect(Collectors.toList()));
	}

	/**
	 * Retrieves an {@link E entity} identified by {@link K key}
	 *
	 * @param key entity id
	 * @return {@link Optional<E>} containing the entity identified by {@link K key}
	 */
	public Optional<E> find(K key) {
		if (key == null) return Optional.empty();
		Result result = get(key);
		if (result == null) return Optional.empty();
		Iterator<Map<String, Object>> it = result.iterator();
		return !it.hasNext() ? Optional.empty() : Optional.of(parse(it.next()));
	}

}
