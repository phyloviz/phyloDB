package pt.ist.meic.phylodb.utils.db;

import org.neo4j.ogm.session.Session;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.List;

public abstract class BatchRepository<T extends Entity<K>, K> extends EntityRepository<T, K> {

	protected BatchRepository(Session session) {
		super(session);
	}

	protected abstract Query init(Query query, List<T> entities);

	protected abstract Query batch(Query query);

	public boolean saveAll(List<T> entities) {
		if (entities.size() == 0)
			return false;
		execute(batch(init(new Query("UNWIND $ as param\n"), entities)));
		return true;
	}

}
