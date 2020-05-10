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
import java.util.List;
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
				"ORDER BY pj.id, d.id, size(i.id), i.id, a.key\n" +
				"WITH pj, d, r, i, id, h, p, collect(DISTINCT {key: a.key, value: a.value}) as ancillary\n" +
				"RETURN pj.id as projectId, d.id as datasetId, i.id as id, i.deprecated as deprecated, r.version as version, " +
				"p.id as profileId, p.deprecated as profileDeprecated, h.version as profileVersion, " +
				"id.description as description, ancillary as ancillaries\n" +
				"ORDER BY pj.id, d.id, size(i.id), i.id SKIP $ LIMIT $";
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
	protected void store(Isolate isolate) {
		String statement = String.format("WITH $ AS param\n%s", getInsertStatement());
		execute(new Query(statement, getInsertParam(isolate)));
	}

	@Override
	protected void delete(Isolate.PrimaryKey key) {
		String statement = "MATCH (p:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})-[:CONTAINS]->(i:Isolate {id: $})\n" +
				"WHERE p.deprecated = false AND d.deprecated = false AND i.deprecated = false\n" +
				"SET i.deprecated = true";
		execute(new Query(statement, key.getProjectId(), key.getDatasetId(), key.getId()));
	}

	@Override
	protected Query init(Query query, List<Isolate> profiles) {
		query.addParameter((Object) profiles.stream().map(this::getInsertParam).toArray());
		return query;
	}

	@Override
	protected Query batch(Query query) {
		return query.appendQuery(getInsertStatement());
	}

	private String getInsertStatement() {
		return "MATCH (p:Project {id: param.projectId})-[:CONTAINS]->(d:Dataset {id: param.datasetId})\n" +
				"WHERE p.deprecated = false AND d.deprecated = false\n" +
				"MERGE (d)-[:CONTAINS]->(i:Isolate {id: param.id}) SET i.deprecated = false WITH param, d, i\n" +
				"OPTIONAL MATCH (i)-[r:CONTAINS_DETAILS]->(id:IsolateDetails)\n" +
				"WHERE NOT EXISTS(r.to) SET r.to = datetime()\n" +
				"WITH param, d, i, COALESCE(MAX(r.version), 0) + 1 as v\n" +
				"CREATE (i)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(id:IsolateDetails {description: param.description})\n" +
				"WITH param, d, id\n" +
				"CALL apoc.do.when(param.profile IS NOT NULL,\n" +
				"    \"MATCH (d2)-[:CONTAINS]->(p:Profile {id: pid})-[r:CONTAINS_DETAILS]->(:ProfileDetails)\n" +
				"    WHERE p.deprecated = false AND NOT EXISTS(r.to)\n" +
				"    CREATE (id2)-[:HAS {version: r.version}]->(p)\n" +
				"    RETURN true\",\n" +
				"    \"RETURN true\"\n" +
				", {d2: d, id2: id, pid: param.profile}) YIELD value as ignored\n" +
				"WITH param, d, id\n" +
				"UNWIND param.ancillary as an\n" +
				"MERGE (a:Ancillary {key: an.key, value: an.value})\n" +
				"CREATE (id)-[:HAS]->(a)";
	}

	private Object getInsertParam(Isolate isolate) {
		Isolate.PrimaryKey key = isolate.getPrimaryKey();
		String profileId = isolate.getProfile() != null ? isolate.getProfile().getPrimaryKey().getId() : null;
		return new Object() {
			public final UUID projectId = key.getProjectId();
			public final UUID datasetId = key.getDatasetId();
			public final String id = key.getId();
			public final String description = isolate.getDescription();
			public final String profile = profileId;
			public final Object[] ancillary = isolate.getAncillaries();
		};
	}

}
