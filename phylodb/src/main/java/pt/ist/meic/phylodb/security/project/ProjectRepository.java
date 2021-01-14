package pt.ist.meic.phylodb.security.project;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.security.authorization.Visibility;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.security.user.model.User;
import pt.ist.meic.phylodb.utils.db.Query;
import pt.ist.meic.phylodb.utils.db.VersionedRepository;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.Arrays;
import java.util.Map;

/**
 * Class that contains the implementation of the {@link VersionedRepository} for projects
 */
@Repository
public class ProjectRepository extends VersionedRepository<Project, String> {

	public ProjectRepository(Session session) {
		super(session);
	}

	@Override
	protected Result getAllEntities(int page, int limit, Object... filters) {
		if (filters == null || filters.length != 1)
			return null;
		User.PrimaryKey id = (User.PrimaryKey) filters[0];
		String statement = "MATCH (p:Project)-[r:CONTAINS_DETAILS]->(pd:ProjectDetails)\n" +
				"WHERE p.deprecated = false AND NOT EXISTS(r.to)\n" +
				"OPTIONAL MATCH (pd)-[:HAS]->(u:User)\n" +
				"WITH p, r, pd, collect(DISTINCT {id: u.id, provider: u.provider}) as users\n" +
				"WHERE {id: $, provider: $} IN users OR pd.type = \"public\"\n" +
				"RETURN p.id as id, p.deprecated as deprecated, r.version as version\n" +
				"ORDER BY size(p.id), p.id SKIP $ LIMIT $";
		return query(new Query(statement, id.getId(), id.getProvider(), page, limit));
	}

	@Override
	protected Result get(String key, long version) {
		String where = version == CURRENT_VERSION_VALUE ? "NOT EXISTS(r.to)" : "r.version = $";
		String statement = "MATCH (p:Project {id: $})-[r:CONTAINS_DETAILS]->(pd:ProjectDetails)\n" +
				"WHERE " + where + "\n" +
				"OPTIONAL MATCH (pd)-[:HAS]->(u:User)\n" +
				"WITH p, r, pd, collect(DISTINCT {id: u.id, provider: u.provider}) as users\n" +
				"RETURN p.id as id, p.deprecated as deprecated, r.version as version,\n" +
				"pd.name as name, pd.type as type, pd.description as description, [user in users WHERE user.id is not null] as users";
		return query(new Query(statement, key, version));
	}

	@Override
	protected VersionedEntity<String> parseVersionedEntity(Map<String, Object> row) {
		return new VersionedEntity<>((String) row.get("id"),
				(long) row.get("version"),
				(boolean) row.get("deprecated"));
	}

	@Override
	protected Project parse(Map<String, Object> row) {
		User.PrimaryKey[] userIds = ((Object []) row.get("users")).length > 0 ? Arrays.stream((Map<String, String>[]) row.get("users"))
				.map(a -> new User.PrimaryKey(a.get("id"), a.get("provider")))
				.toArray(User.PrimaryKey[]::new) : new User.PrimaryKey[0];
		return new Project((String) row.get("id"),
				(long) row.get("version"),
				(boolean) row.get("deprecated"),
				(String) row.get("name"),
				Visibility.valueOf(((String) row.get("type")).toUpperCase()),
				(String) row.get("description"),
				userIds);
	}

	@Override
	protected boolean isPresent(String key) {
		String statement = "OPTIONAL MATCH (p:Project {id: $})\n" +
				"RETURN COALESCE(p.deprecated = false, false)";
		return query(Boolean.class, new Query(statement, key));
	}

	@Override
	protected void store(Project project) {
		String statement = "MERGE (p:Project {id: $}) SET p.deprecated = false WITH p\n" +
				"OPTIONAL MATCH (p)-[r:CONTAINS_DETAILS]->(pd:ProjectDetails)\n" +
				"WHERE NOT EXISTS(r.to) SET r.to = datetime()\n" +
				"WITH p, COALESCE(r.version, 0) + 1 as v\n" +
				"CREATE (p)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(pd:ProjectDetails {name: $, type: $, description: $})\n" +
				"WITH pd\n" +
				"UNWIND $ as param\n" +
				"MATCH (u:User {id: param.id, provider: param.provider}) WHERE u.deprecated = false CREATE (pd)-[:HAS]->(u)";
		Query query = new Query(statement, project.getPrimaryKey(), project.getName(), project.getVisibility().getName(), project.getDescription(),
				Arrays.stream(project.getUsers()).map(u -> new Object() {
					public final String id = u.getId();
					public final String provider = u.getProvider();
				})
		);
		execute(query);
	}

	@Override
	protected void delete(String key) {
		String statement = "MATCH (p:Project {id: $}) SET p.deprecated = true WITH p\n" +
				"MATCH (p)-[:CONTAINS]->(a:Allele) SET a.deprecated = true WITH p\n" +
				"MATCH (p)-[:CONTAINS]->(d:Dataset) SET d.deprecated = true WITH d\n" +
				"MATCH (p)-[:CONTAINS]->(pf:Profile) SET pf.deprecated = true WITH d\n" +
				"MATCH (p)-[:CONTAINS]->(i:Isolate) SET i.deprecated = true WITH d\n" +
				"MATCH (d)-[:CONTAINS]->(p1:Profile)-[di:DISTANCES]->(p2:Profile)\n" +
				"SET di.deprecated = true\n" +
				"WITH d, di.id as analysis, collect(di) as ignored\n" +
				"MATCH (d)-[:CONTAINS]->(p:Profile)-[h:HAS {inferenceId: analysis}]->(c:Coordinate)\n" +
				"WHERE h.deprecated = false\n" +
				"SET h.deprecated = true";
		execute(new Query(statement, key));
	}

}
