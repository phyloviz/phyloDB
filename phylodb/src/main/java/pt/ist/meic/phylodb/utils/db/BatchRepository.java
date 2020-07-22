package pt.ist.meic.phylodb.utils.db;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * A BatchRepository is an {@link VersionedRepository} that contains batch operations, namely {@link #findAll(int, int, Object...)} and {@link #save(Object)}
 *
 * @param <E> type of entity
 * @param <K> type of entity id
 */
public abstract class BatchRepository<E extends VersionedEntity<K>, K> extends VersionedRepository<E, K> {

	protected BatchRepository(Session session) {
		super(session);
	}

	/**
	 * Retrieves a page of {@link E} as a result
	 *
	 * @param page    number of the page to retrieve
	 * @param limit   number of {@link E} to retrieve by page
	 * @param filters used to filter the query results
	 * @return a page of {@link E} as a {@link Result}
	 */
	protected abstract Result getAll(int page, int limit, Object... filters);

	/**
	 * Saves a list of {@link E}
	 *
	 * @param query    {@link Query} initialized to operate through a list of {@link E}
	 * @param entities list of {@link E}
	 * @return {@link Query} to be executed
	 */
	protected abstract Query batch(Query query, List<E> entities);

	/**
	 * Retrieves a page of {@link E}
	 *
	 * @param page    number of the page to retrieve
	 * @param limit   number of {@link E} to retrieve by page
	 * @param filters used to filter the query results
	 * @return an {@link Optional} with a {@link List} of {@link E}
	 */
	public Optional<List<E>> findAll(int page, int limit, Object... filters) {
		if (page < 0 || limit < 0) return Optional.empty();
		Result result = getAll(page * limit, limit, filters);
		if (result == null) return Optional.empty();
		return Optional.of(StreamSupport.stream(result.spliterator(), false)
				.map(this::parse)
				.collect(Collectors.toList()));
	}

	/**
	 * Saves a list of {@link E}
	 *
	 * @param entities list of {@link E}
	 * @return {@code true} if it was possible to save
	 */
	public boolean saveAll(List<E> entities) {
		if (entities.size() == 0)
			return false;
		execute(batch(new Query("UNWIND $ as param\n"), entities));
		return true;
	}

}
