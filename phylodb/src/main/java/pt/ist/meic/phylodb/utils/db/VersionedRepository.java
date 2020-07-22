package pt.ist.meic.phylodb.utils.db;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * An VersionedRepository is an {@link EntityRepository} that contains operations to manage entities that are versioned
 *
 * @param <E> type of entity
 * @param <K> type of entity id
 */
public abstract class VersionedRepository<E, K> extends EntityRepository<E, K> {

	public static final long CURRENT_VERSION_VALUE = -1;
	public static final String CURRENT_VERSION = "" + CURRENT_VERSION_VALUE;

	protected VersionedRepository(Session session) {
		super(session);
	}

	/**
	 * Parses a row from a {@link Result} into an {@link VersionedEntity <K> entity}
	 *
	 * @param row row from a {@link Result}
	 * @return an entity identified by {@link K}
	 */
	protected abstract VersionedEntity<K> parseVersionedEntity(Map<String, Object> row);

	/**
	 * Retrieves a {@link Result} containing the entity identified by {@link K key} with the version <code>version<code/>
	 *
	 * @param key     entity id
	 * @param version entity version
	 * @return {@link Result} containing the entity identified by {@link K key} with the version <code>version<code/>
	 */
	protected abstract Result get(K key, long version);

	/**
	 * Retrieves a page of {@link VersionedEntity <K>} as a result
	 *
	 * @param page    number of the page to retrieve
	 * @param limit   number of {@link VersionedEntity<K>} to retrieve by page
	 * @param filters used to filter the query results
	 * @return page of {@link VersionedEntity<K>}
	 */
	public Optional<List<VersionedEntity<K>>> findAllEntities(int page, int limit, Object... filters) {
		if (page < 0 || limit < 0) return Optional.empty();
		Result result = getAllEntities(page * limit, limit, filters);
		if (result == null) return Optional.empty();
		return Optional.of(StreamSupport.stream(result.spliterator(), false)
				.map(this::parseVersionedEntity)
				.collect(Collectors.toList()));
	}

	/**
	 * Retrieves an {@link E entity} identified by {@link K key} with the version <code>version<code/>
	 *
	 * @param key     entity id
	 * @param version entity version
	 * @return {@link Optional<E>} containing the entity identified by {@link K key} with the version <code>version<code/>
	 */
	public Optional<E> find(K key, long version) {
		if (key == null) return Optional.empty();
		Result result = get(key, version);
		if (result == null) return Optional.empty();
		Iterator<Map<String, Object>> it = result.iterator();
		return !it.hasNext() ? Optional.empty() : Optional.of(parse(it.next()));
	}

}
