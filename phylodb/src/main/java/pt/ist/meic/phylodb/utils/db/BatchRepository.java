package pt.ist.meic.phylodb.utils.db;

import org.neo4j.ogm.session.Session;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.List;
import java.util.function.Function;

public abstract class BatchRepository<T extends Entity<K>, K> extends EntityRepository<T, K> {

	public static final String SKIP = "skip", UPDATE = "update";
	private static final String LOG_MESSAGE = "%s could not be stored.";

	protected BatchRepository(Session session) {
		super(session);
	}

	protected abstract Query init(String... params);

	protected abstract void batch(Query query, T entity);

	protected abstract void arrange(Query query, String... params);

	public boolean saveAll(List<T> entities, String flag, String... params) {
		if (params.length == 0)
			return false;
		Query query = init(params);
		Function<T, Integer> handle = flag.equals(UPDATE) ?
				(e) -> saveAllOnConflictUpdate(query, e) :
				(e) -> saveAllOnConflictSkip(query, e);
		int toExecute = 0;
		for (T e : entities) {
			toExecute += handle.apply(e);
		}
		if (toExecute == 0)
			return true;
		arrange(query, params);
		execute(query);
		return true;
	}

	private int saveAllOnConflictSkip(Query query, T entity) {
		if (exists(entity.getPrimaryKey())) {
			LOG.info(String.format(LOG_MESSAGE, entity.toString()));
			return 0;
		}
		batch(query, entity);
		return 1;
	}

	private int saveAllOnConflictUpdate(Query query, T entity) {
		batch(query, entity);
		return 1;
	}

}
