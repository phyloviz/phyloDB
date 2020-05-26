package pt.ist.meic.phylodb.typing.profile;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.db.BatchRepository;
import pt.ist.meic.phylodb.utils.db.Query;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Repository
public class ProfileRepository extends BatchRepository<Profile, Profile.PrimaryKey> {

	public ProfileRepository(Session session) {
		super(session);
	}

	@Override
	protected Result getAll(int page, int limit, Object... filters) {
		if (filters == null || filters.length == 0)
			return null;
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})-[:CONTAINS]->(p:Profile)-[r:CONTAINS_DETAILS]->(pd:ProfileDetails)\n" +
				"MATCH (pd)-[h:HAS]->(a:Allele)<-[:CONTAINS]-(l:Locus)<-[:CONTAINS]-(t:Taxon)\n" +
				"WHERE p.deprecated = false AND NOT EXISTS(r.to)\n" +
				"OPTIONAL MATCH (a)<-[:CONTAINS]-(pj2:Project)\n" +
				"RETURN pj.id as projectId, d.id as datasetId, p.id as id, r.version as version, p.deprecated as deprecated,\n" +
				"pd.aka as aka, collect(DISTINCT {project: pj2.id, taxon: t.id, locus: l.id, id: a.id, version: h.version, deprecated: a.deprecated, part:h.part, total: h.total}) as alleles\n" +
				"ORDER BY pj.id, d.id, size(p.id), p.id SKIP $ LIMIT $";
		return query(new Query(statement, filters[0], filters[1], page, limit));
	}

	@Override
	protected Result get(Profile.PrimaryKey key, long version) {
		String where = version == CURRENT_VERSION_VALUE ? "NOT EXISTS(r.to)" : "r.version = $";
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})-[:CONTAINS]->(p:Profile {id: $})\n" +
				"MATCH (p)-[r:CONTAINS_DETAILS]->(pd:ProfileDetails)-[h:HAS]->(a:Allele)<-[:CONTAINS]-(l:Locus)<-[:CONTAINS]-(t:Taxon)\n" +
				"WHERE " + where + "\n" +
				"OPTIONAL MATCH (a)<-[:CONTAINS]-(pj2:Project)\n" +
				"RETURN pj.id as projectId, d.id as datasetId, p.id as id, r.version as version, p.deprecated as deprecated,\n" +
				"pd.aka as aka, collect(DISTINCT {project: pj2.id, taxon: t.id, locus: l.id, id: a.id, version: h.version, deprecated: a.deprecated, part:h.part, total: h.total}) as alleles";
		return query(new Query(statement, key.getProjectId(), key.getDatasetId(), key.getId(), version));
	}

	@Override
	protected Profile parse(Map<String, Object> row) {
		Map<String, Object>[] alleles = (Map<String, Object>[]) row.get("alleles");
		int size = Math.toIntExact((long) alleles[0].get("total"));
		List<Entity<Allele.PrimaryKey>> allelesReferences = new ArrayList<>(size);
		for (int i = 0; i < size; i++)
			allelesReferences.add(null);
		for (Map<String, Object> a : alleles) {
			int position = Math.toIntExact((long) a.get("part"));
			Object projectId = a.get("project");
			Allele.PrimaryKey key = new Allele.PrimaryKey((String) a.get("taxon"), (String) a.get("locus"), (String) a.get("id"), (String) projectId);
			Entity<Allele.PrimaryKey> reference = new Entity<>(key, (long) a.get("version"), (boolean) a.get("deprecated"));
			allelesReferences.set(position - 1, reference);
		}
		return new Profile((String) row.get("projectId"),
				(String) row.get("datasetId"),
				(String) row.get("id"),
				(long) row.get("version"),
				(boolean) row.get("deprecated"),
				(String) row.get("aka"),
				allelesReferences
		);
	}

	@Override
	protected boolean isPresent(Profile.PrimaryKey key) {
		String statement = "OPTIONAL MATCH (pj:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})-[:CONTAINS]->(p:Profile {id: $})\n" +
				"RETURN COALESCE(p.deprecated = false, false)";
		return query(Boolean.class, new Query(statement, key.getProjectId(), key.getDatasetId(), key.getId()));
	}

	@Override
	protected void store(Profile profile) {
		String statement = String.format("WITH $ as param\n%s", getInsertStatement());
		execute(new Query(statement, getInsertParam(profile)));
	}

	@Override
	protected void delete(Profile.PrimaryKey key) {
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})-[:CONTAINS]->(p:Profile {id: $})\n" +
				"SET p.deprecated = true";
		execute(new Query(statement, key.getProjectId(), key.getDatasetId(), key.getId()));
	}

	@Override
	protected Query init(Query query, List<Profile> profiles) {
		query.addParameter((Object) profiles.stream().map(this::getInsertParam).toArray());
		return query;
	}

	@Override
	protected Query batch(Query query) {
		return query.appendQuery(getInsertStatement());
	}


	public boolean anyMissing(List<Entity<Profile.PrimaryKey>> references) {
		Optional<Entity<Profile.PrimaryKey>> optional = references.stream().filter(Objects::nonNull).findFirst();
		if (!optional.isPresent())
			return true;
		String project = optional.get().getPrimaryKey().getProjectId();
		String dataset = optional.get().getPrimaryKey().getDatasetId();
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})\n" +
				"UNWIND $ as param\n" +
				"OPTIONAL MATCH (d)-[:CONTAINS]->(p:Profile {id: param})\n" +
				"RETURN p.id as present";
		Object[] ids = references.stream()
				.filter(Objects::nonNull)
				.map(r -> r.getPrimaryKey().getId())
				.toArray();
		Result result = query(new Query(statement, project, dataset, ids));
		Iterator<Map<String, Object>> it = result.iterator();
		if(!it.hasNext())
			return true;
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(it, Spliterator.ORDERED), false)
				.anyMatch(r -> r.get("present") == null);
	}

	private String getInsertStatement() {
		return "MATCH (pj:Project {id: param.projectId})-[:CONTAINS]->(d:Dataset {id: param.datasetId})\n" +
				"WHERE d.deprecated = false\n" +
				"MERGE (d)-[:CONTAINS]->(p:Profile {id: param.id}) SET p.deprecated = false WITH param, pj, d, p\n" +
				"OPTIONAL MATCH (p)-[r:CONTAINS_DETAILS]->(pd:ProfileDetails)\n" +
				"WHERE NOT EXISTS(r.to) SET r.to = datetime()\n" +
				"WITH param, pj, d, p, COALESCE(r.version, 0) + 1 as v\n" +
				"CREATE (p)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(pd:ProfileDetails {aka: param.aka})\n" +
				"WITH param, pj, d, pd\n" +
				"MATCH (d)-[r1:CONTAINS_DETAILS]->(dd:DatasetDetails)-[h:HAS]->(s:Schema)-[r2:CONTAINS_DETAILS]->(sd:SchemaDetails)\n" +
				"WHERE NOT EXISTS(r1.to) AND r2.version = h.version\n" +
				"UNWIND param.alleles as n\n" +
				"MATCH (sd)-[:HAS {part: n.part}]->(l:Locus)\n" +
				"CALL apoc.do.when(param.project = TRUE,\n" +
				"    \"MATCH (l)-[:CONTAINS]->(a:Allele {id: n.id})-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)\n" +
				"    WHERE NOT EXISTS(r.to) AND (a)<-[:CONTAINS]-(pj)\n" +
				"    CREATE (pd)-[:HAS {version: r.version, part: n.part, total: n.total}]->(a)" +
				"    RETURN TRUE\",\n" +
				"    \"MATCH (l)-[:CONTAINS]->(a:Allele {id: n.id})-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)\n" +
				"    WHERE NOT EXISTS(r.to) AND NOT (a)<-[:CONTAINS]-(:Project)\n" +
				"    CREATE (pd)-[:HAS {version: r.version, part: n.part, total: n.total}]->(a)\n" +
				"    RETURN TRUE\"\n" +
				", {l: l, pd: pd, n: n, pj: pj}) YIELD value\n" +
				"RETURN 0";
	}

	private Object getInsertParam(Profile profile) {
		Profile.PrimaryKey key = profile.getPrimaryKey();
		List<Entity<Allele.PrimaryKey>> references = profile.getAllelesReferences();
		boolean priv = references.stream().anyMatch(a -> a != null && a.getPrimaryKey().getProjectId() != null);
		return new Object(){
			public final String projectId = key.getProjectId();
			public final String datasetId = key.getDatasetId();
			public final String id = key.getId();
			public final String aka = profile.getAka();
			public final boolean project = priv;
			public final Object[] alleles = IntStream.range(0, references.size())
					.mapToObj(i -> references.get(i) != null ? new Object(){
						public final String id = references.get(i).getPrimaryKey().getId();
						public final int part = i + 1;
						public final int total = references.size();
					} : null)
					.toArray();
		};
	}

}
