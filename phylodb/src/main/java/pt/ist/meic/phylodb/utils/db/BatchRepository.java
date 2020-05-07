package pt.ist.meic.phylodb.utils.db;

import org.neo4j.ogm.session.Session;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.List;

public abstract class BatchRepository<T extends Entity<K>, K> extends EntityRepository<T, K> {

	protected BatchRepository(Session session) {
		super(session);
	}

	protected abstract Query init(String... params);

	protected abstract void batch(Query query, T entity);

	protected abstract void arrange(Query query, String... params);

	public boolean saveAll(List<T> entities, int execute, String... params) {
		if (params.length == 0 || entities.size() == 0 || execute < 1)
			return false;
		int i = 0;
		Query query = init(params);
		while(i != entities.size()) {
			batch(query, entities.get(i));
			if(++i % execute == 0) {
				arrange(query, params);
				execute(query).queryStatistics();
				query = init(params);
			}
		}
		arrange(query, params);
		execute(query);
		return true;
	}

}
