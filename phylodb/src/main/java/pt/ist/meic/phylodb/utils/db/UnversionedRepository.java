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

public abstract class UnversionedRepository<E, K> extends EntityRepository<E, K> {

	protected UnversionedRepository(Session session) {
		super(session);
	}

	protected abstract Entity<K> parseEntity(Map<String, Object> row);

	protected abstract Result get(K key);

	public Optional<List<Entity<K>>> findAllEntities(int page, int limit, Object... filters) {
		if (page < 0 || limit < 0) return Optional.empty();
		Result result = getAllEntities(page * limit, limit, filters);
		if (result == null) return Optional.empty();
		return Optional.of(StreamSupport.stream(result.spliterator(), false)
				.map(this::parseEntity)
				.collect(Collectors.toList()));
	}

	public Optional<E> find(K key) {
		if (key == null) return Optional.empty();
		Result result = get(key);
		if (result == null) return Optional.empty();
		Iterator<Map<String, Object>> it = result.iterator();
		return !it.hasNext() ? Optional.empty() : Optional.of(parse(it.next()));
	}

}
