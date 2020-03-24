package pt.ist.meic.phylodb.typing.profile;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.db.Query;

import java.util.*;
import java.util.function.BiFunction;

@Repository
public class ProfileRepository extends EntityRepository<Profile, Profile.PrimaryKey> {

	public ProfileRepository(Session session) {
		super(session);
	}

	@Override
	protected List<Profile> getAll(int page, int limit, Object... filters) {
		if (filters == null || filters.length == 0)
			return null;
		String statement = "MATCH (d:Dataset {id: $})-[:CONTAINS]->(p:Profile)\n" +
				"WHERE NOT EXISTS(d.to) AND NOT EXISTS(p.to)\n" +
				"MATCH (d)-[:HAS]->(s:Schema)-[h:HAS]->(l:Locus)-[:CONTAINS]->(a)<-[:HAS]-(p)\n" +
				"WHERE NOT EXISTS(s.to) AND NOT EXISTS(l.to) AND NOT EXISTS(a.to)\n" +
				"WITH p, a\n" +
				"ORDER BY h.part\n" +
				"RETURN p.id as id, p.aka as aka, collect(a) as alleles SKIP $ LIMIT $";
		Result r = query(new Query(statement, filters[0], page, limit));
		List<Profile> profiles = new ArrayList<>();
		while (r.iterator().hasNext()) {
			Map<String, Object> row = r.iterator().next();
			profiles.add(new Profile((String) filters[0], (String)row.get("id"), (String) row.get("aka"), (String[]) row.get("alleles")));
		}
		return profiles;
	}

	@Override
	protected Profile get(Profile.PrimaryKey key) {
		String statement = "MATCH (d:Dataset {id: $})-[:CONTAINS]->(p:Profile {id: $})\n" +
				"WHERE NOT EXISTS(d.to) AND NOT EXISTS(p.to)\n" +
				"MATCH (d)-[:HAS]->(s:Schema)-[h:HAS]->(l:Locus)-[:CONTAINS]->(a)<-[:HAS]-(p)\n" +
				"WHERE NOT EXISTS(s.to) AND NOT EXISTS(l.to) AND NOT EXISTS(a.to)\n" +
				"WITH p, a\n" +
				"ORDER BY h.part\n" +
				"RETURN p.id as id, p.aka as aka, collect(a) as alleles";
		Result r = query(new Query(statement, key.getDatasetId(), key.getId()));
		if (!r.iterator().hasNext())
			return null;
		Map<String, Object> row = r.iterator().next();
		return new Profile(key.getDatasetId().toString(), (String)row.get("id"), (String) row.get("aka"), (String[]) row.get("alleles"));

	}

	@Override
	protected boolean exists(Profile profile) {
		String statement = "MATCH (d:Dataset {id: $})-[:CONTAINS]->(p:Profile {id: $})\n" +
				"WHERE NOT EXISTS(d.to) AND NOT EXISTS(p.to)\n" +
				"RETURN p";
		return query(Profile.class, new Query(statement, profile.getDatasetId(), profile.getId())) != null;
	}

	@Override
	protected void create(Profile profile) {
		Query query = new Query("MATCH (d:Dataset {id: $}) WHERE NOT EXISTS(d.to)\n", profile.getDatasetId());
		composeCreate(query, profile);
		execute(query);
	}

	@Override
	protected void update(Profile profile) {
		Query query = new Query("MATCH (d:Dataset {id: $}) WHERE NOT EXISTS(d.to)\n", profile.getDatasetId());
		composeUpdate(query, profile);
		execute(query);
	}

	@Override
	protected void delete(Profile.PrimaryKey key) {
		String statement = "MATCH (d:Dataset {id: $})-[:CONTAINS]->(p:Profile {id: $})\n" +
				"WHERE NOT EXISTS(d.to) AND NOT EXISTS(p.to)\n" +
				"SET p.to = datetime()";
		execute(new Query(statement, key.getDatasetId(), key.getId()));
	}

	public void saveAllOnConflictSkip(UUID datasetId, List<Profile> entities) {
		saveAll(datasetId, entities, (q, p) -> {
			if (exists(p)) {
				LOG.info(String.format("The profile %s could not be created with the alleles %s since it already existed", p.getId(), Arrays.toString(p.getAllelesIds())));
				return 0;
			} else {
				composeCreate(q, p);
				q.appendQuery("WITH d\n");
				return 1;
			}
		});
	}

	public void saveAllOnConflictUpdate(UUID datasetId, List<Profile> entities) {
		saveAll(datasetId, entities, (q, p) -> {
			if (exists(p)) {
				composeUpdate(q, p);
			} else {
				composeCreate(q, p);
				q.appendQuery("WITH d\n");
			}
			q.appendQuery("WITH d\n");
			return 1;
		});
	}

	private void composeCreate(Query query, Profile profile) {
		String statement = "CREATE (d)-[:CONTAINS]->(p:Profile {id: $, aka: $, from: datetime()}) WITH d, p\n" +
				"MATCH (d)-[:HAS]->(s:Schema) WHERE NOT EXISTS(s.to)\n" +
				"WITH d, s, p\n";
		query.appendQuery(statement).addParameter(profile.getId(), profile.getAka());
		composeAlleles(query, profile);
	}

	private void composeUpdate(Query query, Profile profile) {
		String statement = "MATCH (d)-[:CONTAINS]->(p:Profile {id: $})\n" +
				"CALL apoc.refactor.cloneNodes([p], false) YIELD input, output\n" +
				"SET output.aka = $, output.from = datetime(), p.to = datetime() WITH d, output as p\n" +
				"CREATE (d)-[:CONTAINS]->(p) WITH d, p\n" +
				"MATCH (d)-[:HAS]->(s:Schema) WHERE NOT EXISTS(s.to)\n" +
				"WITH d, s, p\n";
		query.appendQuery(statement).addParameter(profile.getId(), profile.getAka());
		composeAlleles(query, profile);
	}

	private void composeAlleles(Query query, Profile profile) {
		String[] allelesIds = profile.getAllelesIds();
		for (int i = 0; i < profile.getAllelesIds().length; i++) {
			query.appendQuery("MATCH (s)-[:HAS {part: %s}]->(l:Locus) WHERE NOT EXISTS(l.to)\n", i)
					.appendQuery("MERGE (l)-[:CONTAINS]->(a:Allele {id: $}) WHERE NOT EXISTS(a.to) ON CREATE SET a.from = datetime() WITH d, s, p, a\n")
					.appendQuery("CREATE (p)-[:HAS]->(a) WITH d, s, p\n")
					.addParameter(allelesIds[i]);
		}
		query.subQuery(query.length() - "WITH d, s, p\n".length());
	}

	private void saveAll(UUID datasetId, List<Profile> entities, BiFunction<Query, Profile, Integer> compose) {
		String statement = "MATCH (d:Dataset {id: $}))\n" +
				"WHERE NOT EXISTS(d.to)\n" +
				"WITH d\n";
		Query query = new Query(statement, datasetId);
		int execute = 0;
		for (Profile profile : entities)
			execute += compose.apply(query, profile);
		if(execute == 0)
			return;
		query.subQuery(query.length() - "WITH d\n".length());
		execute(query);
	}

}
