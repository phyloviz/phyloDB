package pt.ist.meic.phylodb.typing.isolate;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.typing.isolate.model.Ancillary;
import pt.ist.meic.phylodb.typing.isolate.model.Isolate;
import pt.ist.meic.phylodb.typing.profile.ProfileRepository;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.db.BatchRepository;
import pt.ist.meic.phylodb.utils.db.Query;
import pt.ist.meic.phylodb.utils.service.Reference;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Repository
public class IsolateRepository extends BatchRepository<Isolate, Isolate.PrimaryKey> {

	private ProfileRepository profileRepository;

	public IsolateRepository(Session session, ProfileRepository profileRepository) {
		super(session);
		this.profileRepository = profileRepository;
	}

	@Override
	protected Result getAll(int page, int limit, Object... filters) {
		if (filters == null || filters.length != 2)
			return null;
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})-[:CONTAINS]->(i:Isolate)-[r:CONTAINS_DETAILS]->(id:IsolateDetails)\n" +
				"WHERE pj.deprecated = false, d.deprecated = false AND i.deprecated = false AND NOT EXISTS(r.to)\n" +
				"OPTIONAL MATCH (p:Profile)<-[h:HAS]-(id)-[:HAS]->(a:Ancillary) WITH pj, d, r, i, id, h, p, collect([a.key, a.value]) as ancillary\n" +
				"RETURN pj.id as projectId, d.id as datasetId, i.id as id, i.deprecated, r.version as version, " +
				"p.id as profileId, p.deprecated as profileDeprecated, h.version as profileVersion, " +
				"id.description as description.id as id, ancillary as ancillaries\n" +
				"SKIP $ LIMIT $";
		return query(new Query(statement, filters[0], filters[1], page, limit));
	}

	@Override
	protected Result get(Isolate.PrimaryKey key, long version) {
		String where = version == CURRENT_VERSION_VALUE ? "NOT EXISTS(r.to)" : "r.version = $";
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})-[:CONTAINS]->(i:Isolate {id: $})-[r:CONTAINS_DETAILS]->(id:IsolateDetails)\n" +
				"WHERE " + where + "\n" +
				"OPTIONAL MATCH (p:Profile)<-[:HAS]-(id)-[:HAS]->(a:Ancillary) WITH pj, d, r, i, id, p, collect([a.key, a.value]) as ancillary\n" +
				"RETURN pj.id as projectId, d.id as datasetId, i.id as id, i.deprecated, r.version as version, " +
				"p.id as profileId, p.deprecated as profileDeprecated, h.version as profileVersion, " +
				"d.description as description.id as id, ancillary as ancillaries";
		return query(new Query(statement, key.getProjectId(), key.getDatasetId(), key.getId(), version));
	}

	@Override
	protected Isolate parse(Map<String, Object> row) {
		Reference<String> profile = null;
		if(row.get("profileId") != null )
			profile = new Reference<>((String) row.get("profileId"), (long) row.get("profileVersion"), (boolean) row.get("profileDeprecated"));
		Ancillary[] ancillaries = Arrays.stream((Object[][])row.get("ancillaries"))
				.map(a -> new Ancillary((String)a[0], (String)a[1]))
				.toArray(Ancillary[]::new);
		return new Isolate(UUID.fromString(row.get("projectId").toString()),
				UUID.fromString(row.get("datasetId").toString()),
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
		Query query = new Query("MATCH (p:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $}) d.deprecated = false\n", key.getProjectId(), key.getDatasetId());
		composeStore(query, isolate);
		return execute(query);
	}

	@Override
	protected void delete(Isolate.PrimaryKey key) {
		String statement = "MATCH (p:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})-[:CONTAINS]->(i:Isolate {id: $})\n" +
				"WHERE p.deprecated = false AND d.deprecated = false AND i.deprecated = false AND NOT EXISTS(r.to)\n" +
				"SET i.deprecated = true";
		execute(new Query(statement, key.getProjectId(), key.getDatasetId(), key.getId()));
	}

	@Override
	protected Query init(String... params) {
		String statement = "MATCH (p:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})\n" +
				"WHERE d.deprecated = false\n" +
				"WITH d\n";
		return new Query(statement, params[0], params[1]);
	}

	@Override
	protected void batch(Query query, Isolate isolate) {
		Isolate.PrimaryKey key = isolate.getPrimaryKey();
		if(isolate.getProfile() != null && profileRepository.exists(new Profile.PrimaryKey(key.getProjectId(), key.getDatasetId(), isolate.getProfile().getPrimaryKey()))) {
			LOG.info(String.format("The %s was not created due to absent profile", isolate.toString()));
			return;
		}
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
		query.appendQuery(statement).addParameter(isolate.getPrimaryKey(), isolate.getDescription());
		composeProfile(query, isolate);
		composeAncillary(query, isolate);
	}

	private void composeProfile(Query query, Isolate isolate) {
		if (isolate.getProfile() == null) return;
		String statement = "WITH d, id\n" +
				"MATCH (p:Profile {id: $}-[r:CONTAINS_DETAILS]->(pd:ProfileDetails))\n" +
				"WHERE p.deprecated = false AND NOT EXISTS(r.to)\n" +
				"WITH d, p, id, r\n" +
				"CREATE (id)-[:HAS {version: r.version}]->(p)\n";
		query.appendQuery(statement).addParameter(isolate.getProfile());
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
