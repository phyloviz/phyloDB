package pt.ist.meic.phylodb.security.user;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.security.user.model.User;
import pt.ist.meic.phylodb.utils.db.Query;
import pt.ist.meic.phylodb.utils.db.VersionedRepository;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class that contains the implementation of the {@link VersionedRepository} for users
 */
@Repository
public class UserRepository extends VersionedRepository<User, User.PrimaryKey> {

	public UserRepository(Session session) {
		super(session);
	}

	@Override
	protected Result getAllEntities(int page, int limit, Object... filters) {
		String statement = "MATCH (u:User)-[r:CONTAINS_DETAILS]->(ud:UserDetails)\n" +
				"WHERE u.deprecated = false AND r.to IS NULL\n" +
				"RETURN u.id as id, u.provider as provider, u.deprecated as deprecated, r.version as version\n" +
				"ORDER BY size(u.id), u.id, size(u.provider), u.provider SKIP $ LIMIT $";
		return query(new Query(statement, page, limit));
	}

	@Override
	protected Result get(User.PrimaryKey key, long version) {
		String where = version == CURRENT_VERSION_VALUE ? "r.to IS NULL" : "r.version = $";
		String statement = "MATCH (u:User {id: $, provider: $})-[r:CONTAINS_DETAILS]->(ud:UserDetails)\n" +
				"WHERE " + where + "\n" +
				"RETURN u.id as id, u.provider as provider, u.deprecated as deprecated, r.version as version,\n" +
				"ud.role as role";
		return query(new Query(statement, key.getId(), key.getProvider(), version));
	}

	@Override
	protected VersionedEntity<User.PrimaryKey> parseVersionedEntity(Map<String, Object> row) {
		return new VersionedEntity<>(new User.PrimaryKey((String) row.get("id"), (String) row.get("provider")),
				(long) row.get("version"),
				(boolean) row.get("deprecated"));
	}

	@Override
	protected User parse(Map<String, Object> row) {
		return new User((String) row.get("id"),
				(String) row.get("provider"),
				(Long) row.get("version"),
				(boolean) row.get("deprecated"),
				Role.valueOf(((String) row.get("role")).toUpperCase()));
	}

	@Override
	protected boolean isPresent(User.PrimaryKey key) {
		String statement = "OPTIONAL MATCH (u:User {id: $, provider: $})\n" +
				"RETURN COALESCE(u.deprecated = false, false)";
		return query(Boolean.class, new Query(statement, key.getId(), key.getProvider()));
	}

	@Override
	protected void store(User user) {
		String statement = "MERGE (u:User {id: $, provider: $}) SET u.deprecated = false WITH u\n" +
				"OPTIONAL MATCH (u)-[r:CONTAINS_DETAILS]->(ud:UserDetails)\n" +
				"WHERE r.to IS NULL SET r.to = datetime()\n" +
				"WITH u, COALESCE(MAX(r.version), 0) + 1 as v\n" +
				"CREATE (u)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(ud:UserDetails {role: $})";
		execute(new Query(statement, user.getPrimaryKey().getId(), user.getPrimaryKey().getProvider(), user.getRole().getName()));
	}

	@Override
	protected void delete(User.PrimaryKey key) {
		String statement = "MATCH (u:User {id: $, provider: $}) SET u.deprecated = true\n";
		execute(new Query(statement, key.getId(), key.getProvider()));
	}

	/**
	 * Verifies if any of the users represented by the primary keys received in the params doesn't exist
	 *
	 * @param keys users {@link User.PrimaryKey primary keys}
	 * @return {@code true} if any of the users represented by the keys doesn't exist
	 */
	public boolean anyMissing(User.PrimaryKey[] keys) {
		String parameterized = Arrays.stream(keys).map((i) -> "{id: $, provider: $}").collect(Collectors.joining(","));
		String statement = String.format("MATCH (u:User)\n" +
				"WHERE u.deprecated = false AND {id: u.id, provider: u.provider} IN [%s]\n" +
				"RETURN COUNT(u.id)", parameterized);
		List<String> params = new ArrayList<>();
		for (User.PrimaryKey key : keys) {
			params.add(key.getId());
			params.add(key.getProvider());
		}
		return query(Integer.class, new Query(statement, params.toArray())) != keys.length;
	}

}
