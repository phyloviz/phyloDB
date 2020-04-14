package pt.ist.meic.phylodb.typing.profile;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.db.BatchRepository;
import pt.ist.meic.phylodb.utils.db.Query;
import pt.ist.meic.phylodb.utils.service.Reference;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ProfileRepository extends BatchRepository<Profile, Profile.PrimaryKey> {

	public ProfileRepository(Session session) {
		super(session);
	}

	@Override
	protected Result getAll(int page, int limit, Object... filters) {
		if (filters == null || filters.length == 0)
			return null;
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})-[:CONTAINS]->(p:Profile)-[r:CONTAINS_DETAILS]->(pd:ProfileDetails)-[h:HAS]->(a:Allele)\n" +
				"WHERE pj.deprecated = false AND d.deprecated = false AND p.deprecated = false AND NOT EXISTS(r.to)\n" +
				"WITH d, p, h, a\n" +
				"ORDER BY h.part\n" +
				"RETURN pj.id as projectId, d.id as datasetId, p.id as id, r.version as version, p.deprecated as deprecated, " +
				"collect([a.id, a.deprecated, h.version]) as alleleIds, p.aka as aka\n" +
				"SKIP $ LIMIT $";
		return query(new Query(statement, filters[0], filters[1], page, limit));
	}

	@Override
	protected Result get(Profile.PrimaryKey key, int version) {
		String where = version == CURRENT_VERSION_VALUE ? "NOT EXISTS(r.to)" : "r.version = $";
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})-[:CONTAINS]->(p:Profile {id: $})-[r:CONTAINS_DETAILS]->(pd:ProfileDetails)-[h:HAS]->(a:Allele)\n" +
				"WHERE " + where + "\n" +
				"WITH d, p, h, a\n" +
				"ORDER BY h.part\n" +
				"RETURN pj.id as projectId, d.id as datasetId, p.id as id, r.version as version, p.deprecated as deprecated, " +
				"p.aka as aka, collect([a.id, a.deprecated, h.version]) as alleleIds";
		return query(new Query(statement, key.getProjectId(), key.getDatasetId(), key.getId()));

	}

	@Override
	protected Profile parse(Map<String, Object> row) {
		List<Reference<String>> alleleIds = Arrays.stream((Object[][]) row.get("alleleIds"))
				.map(a -> new Reference<>((String) a[0], (int) a[1], (boolean) a[2]))
				.collect(Collectors.toList());
		return new Profile(UUID.fromString(row.get("projectId").toString()),
				UUID.fromString(row.get("datasetId").toString()),
				(String) row.get("id"),
				(int) row.get("version"),
				(boolean) row.get("deprecated"),
				(String) row.get("aka"),
				alleleIds
		);
	}

	@Override
	protected boolean isPresent(Profile.PrimaryKey key) {
		String statement = "OPTIONAL MATCH (pj:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})-[:CONTAINS]->(p:Profile {id: $})\n" +
				"RETURN COALESCE(p.to = false, false)";
		return query(Profile.class, new Query(statement, key.getProjectId(), key.getDatasetId(), key.getId())) != null;
	}

	@Override
	protected void store(Profile profile) {
		Profile.PrimaryKey key = profile.getPrimaryKey();
		Query query = new Query("MATCH (pj:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $}) WHERE d.deprecated = false\n", key.getProjectId(), key.getDatasetId());
		composeStore(query, profile);
		execute(query);
	}

	@Override
	protected void delete(Profile.PrimaryKey key) {
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})-[:CONTAINS]->(p:Profile {id: $})\n" +
				"SET p.deprecated = true";
		execute(new Query(statement, key.getProjectId(), key.getDatasetId(), key.getId()));
	}

	@Override
	protected Query init(String... params) {
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $}))\n" +
				"WHERE d.deprecated = false\n" +
				"WITH pj, d\n";
		return new Query(statement, params[0], params[1]);
	}

	@Override
	protected void batch(Query query, Profile entity) {
		composeStore(query, entity);
		query.appendQuery("WITH d\n");
	}

	@Override
	protected void arrange(Query query, String... params) {
		query.subQuery(query.length() - "WITH d\n".length());
	}

	private void composeStore(Query query, Profile profile) {
		String statement = "MERGE (d)-[:CONTAINS]->(p:Profile {id: $}) SET p.deprecated = false WITH pj, d, p\n" +
				"OPTIONAL MATCH (p)-[r:CONTAINS_DETAILS]->(pd:ProfileDetails)\n" +
				"WHERE NOT EXISTS(r.to) SET r.to = datetime(),\n" +
				"WITH pj, d, p, COALESCE(r.version, 0) + 1 as v\n" +
				"CREATE (p)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(pd:ProfileDetails {aka: $})\n" +
				"WITH pj, d, p\n";
		query.appendQuery(statement).addParameter(profile.getPrimaryKey(), profile.getAka());
		composeAlleles(query, profile);
	}

	private void composeAlleles(Query query, Profile profile) {
		String statement = "MATCH (d)-[r1:CONTAINS_DETAILS]->(dd:DatasetDetails)-[h:HAS]->(s:Schema)-[r2:CONTAINS_DETAILS]->(sd:SchemaDetails)\n" +
				"WHERE NOT EXISTS(r1.to) AND r2.version = h.version\n" +
				"WITH pj, d, p, sd";
		query.appendQuery(statement);
		statement = "MATCH (sd)-[:HAS {part: %s}]->(l:Locus)\n" +
				"OPTIONAL MATCH (l)-[:CONTAINS]->(a:Allele {id: $})\n" +
				"WHERE (a)<-[:CONTAINS]-(pj) OR NOT (a)<-[:CONTAINS]-(:Project)" +
				"FOREACH (_ in CASE WHEN a is null THEN [1] ELSE [] END |\n" +
				"\t CREATE (pj)-[:CONTAINS]->(an:Allele {id: $})-[r:CONTAINS_DETAILS {from: datetime(), version: 1}]->(:AlleleDetails)\n" +
				"\t CREATE (p)-[:HAS {part: %s, version: r.version}]->(an))\n" +
				"FOREACH (_ in CASE WHEN a is not null THEN [1] ELSE [] END |\n" +
				"\t CREATE (p)-[:HAS {part: %s, version: r.version}]->(a))\n" +
				"WITH pj, d, p, sd";
		String[] allelesIds = profile.getAllelesids().toArray(new String[0]);
		for (int i = 0; i < allelesIds.length; i++)
			query.appendQuery(statement, i, i, i)
					.addParameter(allelesIds[i], allelesIds[i]);
		query.subQuery(query.length() - "WITH pj, d, p, sd\n".length());
	}

}
