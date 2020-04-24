package pt.ist.meic.phylodb.security.authorization.project;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.security.authentication.user.model.User;
import pt.ist.meic.phylodb.security.authorization.project.model.Project;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.db.Query;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Repository
public class ProjectRepository extends EntityRepository<Project, UUID> {

	public ProjectRepository(Session session) {
		super(session);
	}

	@Override
	protected Result getAll(int page, int limit, Object... filters) {
		if (filters == null || filters.length != 1)
			return null;
		User.PrimaryKey id = (User.PrimaryKey) filters[0];
		String statement = "MATCH (p:Project)-[r:CONTAINS_DETAILS]->(pd:ProjectDetails)-[:HAS]->(u:User)\n" +
				"WHERE p.deprecated = false AND NOT EXISTS(r.to)\n" +
				"WITH p, r, pd, collect(DISTINCT {id: u.id, provider: u.provider}) as users\n" +
				"WHERE {id: $, provider: $} IN users OR pd.type = \"public\"\n" +
				"RETURN p.id as id, p.deprecated as deprecated, r.version as version,\n" +
				"pd.name as name, pd.type as type, pd.description as description, users as users\n" +
				"ORDER BY p.id SKIP $ LIMIT $";
		return query(new Query(statement, id.getId(), id.getProvider(), page, limit));
	}

	@Override
	protected Result get(UUID key, long version) {
		String where = version == CURRENT_VERSION_VALUE ? "NOT EXISTS(r.to)" : "r.version = $";
		String statement = "MATCH (p:Project {id: $})-[r:CONTAINS_DETAILS]->(pd:ProjectDetails)-[:HAS]->(u:User)\n" +
				"WHERE " + where + "\n" +
				"WITH p, r, pd, collect(DISTINCT {id: u.id, provider: u.provider}) as users\n" +
				"RETURN p.id as id, p.deprecated as deprecated, r.version as version,\n" +
				"pd.name as name, pd.type as type, pd.description as description, users as users";
		return query(new Query(statement, key, version));
	}

	@Override
	protected Project parse(Map<String, Object> row) {
		User.PrimaryKey[] userIds = Arrays.stream((Map<String, String>[]) row.get("users"))
				.map(a -> new User.PrimaryKey(a.get("id"), a.get("provider")))
				.toArray(User.PrimaryKey[]::new);
		return new Project(UUID.fromString((String) row.get("id")),
				(long) row.get("version"),
				(boolean) row.get("deprecated"),
				(String) row.get("name"),
				(String) row.get("type"),
				(String) row.get("description"),
				userIds);
	}

	@Override
	protected boolean isPresent(UUID key) {
		String statement = "OPTIONAL MATCH (p:Project {id: $})\n" +
				"RETURN COALESCE(p.deprecated = false, false)";
		return query(Boolean.class, new Query(statement, key));
	}

	@Override
	protected Result store(Project project) {
		String statement = "MERGE (p:Project {id: $}) SET p.deprecated = false WITH p\n" +
				"OPTIONAL MATCH (p)-[r:CONTAINS_DETAILS]->(pd:ProjectDetails)\n" +
				"WHERE NOT EXISTS(r.to) SET r.to = datetime()\n" +
				"WITH p, COALESCE(r.version, 0) + 1 as v\n" +
				"CREATE (p)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(pd:ProjectDetails {name: $, type: $, description: $})\n" +
				"WITH pd\n";
		Query query = new Query(statement, project.getPrimaryKey(), project.getName(), project.getType(), project.getDescription());
		composeUsers(query, project.getUsers());
		return execute(query);
	}

	@Override
	protected void delete(UUID key) {
		String statement = "MATCH (p:Project {id: $}) SET p.deprecated = true WITH p\n" +
				"MATCH (p)-[:CONTAINS]->(a:Allele) SET a.deprecated = true WITH p\n" +
				"MATCH (p)-[:CONTAINS]->(d:Dataset) SET d.deprecated = true WITH d\n" +
				"MATCH (p)-[:CONTAINS]->(pf:Profile) SET pf.deprecated = true WITH d\n" +
				"MATCH (p)-[:CONTAINS]->(i:Isolate) SET i.deprecated = true";
		execute(new Query(statement, key));
	}

	private void composeUsers(Query query, User.PrimaryKey[] users) {
		for (User.PrimaryKey user : users) {
			query.appendQuery("MATCH (u:User {id: $, provider: $}) WHERE u.deprecated = false CREATE (pd)-[:HAS]->(u) WITH pd\n");
			query.addParameter(user.getId(), user.getProvider());
		}
		query.subQuery(query.length() - "WITH pd\n".length());
	}

}
