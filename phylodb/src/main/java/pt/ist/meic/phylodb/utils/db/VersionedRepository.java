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

public abstract class VersionedRepository<E, K> extends EntityRepository<E, K> {

	public static final long CURRENT_VERSION_VALUE = -1;
	public static final String CURRENT_VERSION = "" + CURRENT_VERSION_VALUE;

	protected VersionedRepository(Session session) {
		super(session);
	}

	protected abstract VersionedEntity<K> parseVersionedEntity(Map<String, Object> row);

	protected abstract Result get(K key, long version);

	public Optional<List<VersionedEntity<K>>> findAllEntities(int page, int limit, Object... filters) {
		if (page < 0 || limit < 0) return Optional.empty();
		Result result = getAllEntities(page * limit, limit, filters);
		if (result == null) return Optional.empty();
		return Optional.of(StreamSupport.stream(result.spliterator(), false)
				.map(this::parseVersionedEntity)
				.collect(Collectors.toList()));
	}

	public Optional<E> find(K key, long version) {
		if (key == null) return Optional.empty();
		Result result = get(key, version);
		if (result == null) return Optional.empty();
		Iterator<Map<String, Object>> it = result.iterator();
		return !it.hasNext() ? Optional.empty() : Optional.of(parse(it.next()));
	}

}
