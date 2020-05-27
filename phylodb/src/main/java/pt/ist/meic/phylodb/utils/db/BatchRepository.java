package pt.ist.meic.phylodb.utils.db;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class BatchRepository<E extends VersionedEntity<K>, K> extends VersionedRepository<E, K> {

	protected BatchRepository(Session session) {
		super(session);
	}

	protected abstract Result getAll(int page, int limit, Object... filters);

	protected abstract Query batch(Query query, List<E> entities);

	public Optional<List<E>> findAll(int page, int limit, Object... filters) {
		if (page < 0 || limit < 0) return Optional.empty();
		Result result = getAll(page * limit, limit, filters);
		if (result == null) return Optional.empty();
		return Optional.of(StreamSupport.stream(result.spliterator(), false)
				.map(this::parse)
				.collect(Collectors.toList()));
	}

	public boolean saveAll(List<E> entities) {
		if (entities.size() == 0)
			return false;
		long start = System.currentTimeMillis();
		execute(batch(new Query("UNWIND $ as param\n"), entities));
		System.out.println("Took " + (System.currentTimeMillis() - start) + " milliseconds to insert");
		return true;
	}

}
