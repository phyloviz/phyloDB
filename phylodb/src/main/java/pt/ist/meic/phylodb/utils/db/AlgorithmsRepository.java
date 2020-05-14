package pt.ist.meic.phylodb.utils.db;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public abstract class AlgorithmsRepository<E, K> extends Repository<E, K> {

	protected AlgorithmsRepository(Session session) {
		super(session);
	}

	protected abstract Result get(K key);

	public Optional<E> find(K key) {
		if (key == null) return Optional.empty();
		Result result = get(key);
		if (result == null) return Optional.empty();
		Iterator<Map<String, Object>> it = result.iterator();
		return !it.hasNext() ? Optional.empty() : Optional.of(parse(it.next()));
	}

}
