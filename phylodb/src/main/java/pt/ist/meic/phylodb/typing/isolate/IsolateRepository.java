package pt.ist.meic.phylodb.typing.isolate;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.typing.isolate.model.Ancillary;
import pt.ist.meic.phylodb.typing.isolate.model.Isolate;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.db.BatchRepository;
import pt.ist.meic.phylodb.utils.db.Query;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Repository
public class IsolateRepository extends BatchRepository<Isolate, Isolate.PrimaryKey> {

	public IsolateRepository(Session session) {
		super(session);
	}

	@Override
	protected Result getAll(int page, int limit, Object... filters) {
		if (filters == null || filters.length != 2)
			return null;
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})-[:CONTAINS]->(i:Isolate)-[r:CONTAINS_DETAILS]->(id:IsolateDetails)\n" +
				"WHERE pj.deprecated = false AND d.deprecated = false AND i.deprecated = false AND NOT EXISTS(r.to)\n" +
				"OPTIONAL MATCH (id)-[h:HAS]->(p:Profile)\n" +
				"OPTIONAL MATCH (id)-[:HAS]->(a:Ancillary)\n" +
				"WITH pj, d, r, i, id, h, p, a\n" +
				"ORDER BY pj.id, d.id, i.id, a.key\n" +
				"WITH pj, d, r, i, id, h, p, collect(DISTINCT {key: a.key, value: a.value}) as ancillary\n" +
				"RETURN pj.id as projectId, d.id as datasetId, i.id as id, i.deprecated as deprecated, r.version as version, " +
				"p.id as profileId, p.deprecated as profileDeprecated, h.version as profileVersion, " +
				"id.description as description, ancillary as ancillaries\n" +
				"SKIP $ LIMIT $";
		return query(new Query(statement, filters[0], filters[1], page, limit));
	}

	@Override
	protected Result get(Isolate.PrimaryKey key, long version) {
		String where = version == CURRENT_VERSION_VALUE ? "NOT EXISTS(r.to)" : "r.version = $";
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})-[:CONTAINS]->(i:Isolate {id: $})-[r:CONTAINS_DETAILS]->(id:IsolateDetails)\n" +
				"WHERE " + where + "\n" +
				"OPTIONAL MATCH (id)-[h:HAS]->(p:Profile)\n" +
				"OPTIONAL MATCH (id)-[:HAS]->(a:Ancillary)\n" +
				"WITH pj, d, r, i, id, h, p, a\n" +
				"ORDER BY pj.id, d.id, i.id, a.key\n" +
				"WITH pj, d, r, i, id, h, p, collect(DISTINCT {key: a.key, value: a.value}) as ancillary\n" +
				"RETURN pj.id as projectId, d.id as datasetId, i.id as id, i.deprecated as deprecated, r.version as version, " +
				"p.id as profileId, p.deprecated as profileDeprecated, h.version as profileVersion, " +
				"id.description as description, ancillary as ancillaries";
		return query(new Query(statement, key.getProjectId(), key.getDatasetId(), key.getId(), version));
	}

	@Override
	protected Isolate parse(Map<String, Object> row) {
		UUID projectId = UUID.fromString(row.get("projectId").toString()), datasetId = UUID.fromString(row.get("datasetId").toString());
		Entity<Profile.PrimaryKey> profile = null;
		if (row.get("profileId") != null)
			profile = new Entity<>(new Profile.PrimaryKey(projectId, datasetId, (String) row.get("profileId")), (long) row.get("profileVersion"), (boolean) row.get("profileDeprecated"));
		Ancillary[] ancillaries = Arrays.stream((Map<String, Object>[]) row.get("ancillaries"))
				.filter(a -> a.get("key") != null)
				.map(a -> new Ancillary((String) a.get("key"), (String) a.get("value")))
				.toArray(Ancillary[]::new);
		return new Isolate(projectId,
				datasetId,
				(String) row.get("id"),
				(long) row.get("version"),
				(boolean) row.get("deprecated"),
				(String) row.get("description"),
				ancillaries,
				profile);
	}

	@Override
	protected boolean isPresent(Isolate.PrimaryKey key) {
		String statement = "OPTIONAL MATCH (p:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})-[:CONTAINS]->(i:Isolate {id: $})\n" +
				"RETURN COALESCE(i.deprecated = false, false)";
		return query(Boolean.class, new Query(statement, key.getProjectId(), key.getDatasetId(), key.getId()));
	}

	@Override
	protected Result store(Isolate isolate) {
		Isolate.PrimaryKey key = isolate.getPrimaryKey();
		Query query = new Query("MATCH (p:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $}) WHERE p.deprecated = false AND d.deprecated = false\n", key.getProjectId(), key.getDatasetId());
		composeStore(query, isolate);
		return execute(query);
	}

	@Override
	protected void delete(Isolate.PrimaryKey key) {
		String statement = "MATCH (p:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})-[:CONTAINS]->(i:Isolate {id: $})\n" +
				"WHERE p.deprecated = false AND d.deprecated = false AND i.deprecated = false\n" +
				"SET i.deprecated = true";
		execute(new Query(statement, key.getProjectId(), key.getDatasetId(), key.getId()));
	}

	@Override
	protected Query init(String... params) {
		String statement = "MATCH (p:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})\n" +
				"WHERE p.deprecated = false AND d.deprecated = false\n" +
				"WITH d\n";
		return new Query(statement, params[0], params[1]);
	}

	@Override
	protected void batch(Query query, Isolate isolate) {
		composeStore(query, isolate);
		query.appendQuery("WITH d\n");
	}

	@Override
	protected void arrange(Query query, String... params) {
		query.subQuery(query.length() - "WITH d\n".length());
	}

	private void composeStore(Query query, Isolate isolate) {
		String statement = "MERGE (d)-[:CONTAINS]->(i:Isolate {id: $}) SET i.deprecated = false WITH d, i\n" +
				"OPTIONAL MATCH (i)-[r:CONTAINS_DETAILS]->(id:IsolateDetails)\n" +
				"WHERE NOT EXISTS(r.to) SET r.to = datetime()\n" +
				"WITH d, i, COALESCE(MAX(r.version), 0) + 1 as v\n" +
				"CREATE (i)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(id:IsolateDetails {description: $})\n";
		query.appendQuery(statement).addParameter(isolate.getPrimaryKey().getId(), isolate.getDescription());
		composeProfile(query, isolate);
		composeAncillary(query, isolate);
	}

	private void composeProfile(Query query, Isolate isolate) {
		if (isolate.getProfile() == null) return;
		String statement = "WITH d, id\n" +
				"MATCH (d)-[:CONTAINS]->(p:Profile {id: $})-[r:CONTAINS_DETAILS]->(:ProfileDetails)\n" +
				"WHERE p.deprecated = false AND NOT EXISTS(r.to)\n" +
				"CREATE (id)-[:HAS {version: r.version}]->(p)\n";
		query.appendQuery(statement).addParameter(isolate.getProfile().getPrimaryKey().getId());
	}

	private void composeAncillary(Query query, Isolate isolate) {
		query.appendQuery("WITH d, id\n");
		Ancillary[] ancillaries = isolate.getAncillaries();
		for (Ancillary ancillary : ancillaries) {
			query.appendQuery("MERGE (a:Ancillary {key: $, value: $}) WITH d, id, a\n")
					.appendQuery("CREATE (id)-[:HAS]->(a) WITH d, id\n")
					.addParameter(ancillary.getKey(), ancillary.getValue());
		}
		query.subQuery(query.length() - "WITH d, id\n".length());
	}

}
