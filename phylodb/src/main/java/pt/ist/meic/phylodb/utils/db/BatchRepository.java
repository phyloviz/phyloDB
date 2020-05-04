package pt.ist.meic.phylodb.utils.db;

import org.neo4j.ogm.model.QueryStatistics;
import org.neo4j.ogm.session.Session;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.List;
import java.util.Optional;

public abstract class BatchRepository<T extends Entity<K>, K> extends EntityRepository<T, K> {

	public static final String SKIP = "skip", UPDATE = "update";

	protected BatchRepository(Session session) {
		super(session);
	}

	protected abstract Query init(String... params);

	protected abstract void batch(Query query, T entity);

	protected abstract void arrange(Query query, String... params);

	public Optional<QueryStatistics> saveAll(List<T> entities, String... params) {
		if (params.length == 0 || entities.size() == 0)
			return Optional.empty();
		Query query = init(params);
		for (T e : entities)
			batch(query, e);
		arrange(query, params);
		return Optional.of(execute(query).queryStatistics());
	}

}
