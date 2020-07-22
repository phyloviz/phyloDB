package pt.ist.meic.phylodb.phylogeny.allele;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.utils.db.BatchRepository;
import pt.ist.meic.phylodb.utils.db.Query;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.*;
import java.util.stream.StreamSupport;

/**
 * Class that contains the implementation of the {@link BatchRepository} for alleles
 */
@Repository
public class AlleleRepository extends BatchRepository<Allele, Allele.PrimaryKey> {

	public AlleleRepository(Session session) {
		super(session);
	}

	@Override
	protected Result getAllEntities(int page, int limit, Object... filters) {
		if (filters == null || filters.length != 3)
			return null;
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})-[:CONTAINS]->(a:Allele)-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)\n" +
				"WHERE t.deprecated = false AND l.deprecated = false AND a.deprecated = false AND NOT EXISTS(r.to)";
		Object[] params = new Object[]{filters[0], filters[1], page, limit};
		if (filters[2] != null) {
			params = new Object[]{filters[0], filters[1], filters[2], page, limit};
			statement += "\nMATCH (a)<-[:CONTAINS]-(p:Project {id: $})\n" +
					"WHERE p.deprecated = false\n" +
					"RETURN t.id as taxonId, l.id as locusId, a.id as id, a.deprecated as deprecated, r.version as version, p.id as project\n";
		} else {
			statement += " AND NOT (a)<-[:CONTAINS]-(:Project)\n" +
					"\nRETURN t.id as taxonId, l.id as locusId, a.id as id, a.deprecated as deprecated, r.version as version\n";
		}
		statement += "ORDER BY t.id, l.id, size(a.id), a.id SKIP $ LIMIT $";
		return query(new Query(statement, params));
	}

	@Override
	protected Result getAll(int page, int limit, Object... filters) {
		if (filters == null || filters.length != 3)
			return null;
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})-[:CONTAINS]->(a:Allele)-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)\n" +
				"WHERE t.deprecated = false AND l.deprecated = false AND a.deprecated = false AND NOT EXISTS(r.to)";
		Object[] params = new Object[]{filters[0], filters[1], page, limit};
		if (filters[2] != null) {
			params = new Object[]{filters[0], filters[1], filters[2], page, limit};
			statement += "\nMATCH (a)<-[:CONTAINS]-(p:Project {id: $})\n" +
					"WHERE p.deprecated = false\n" +
					"RETURN t.id as taxonId, l.id as locusId, a.id as id, a.deprecated as deprecated, r.version as version, ad.sequence as sequence, p.id as project\n";
		} else {
			statement += " AND NOT (a)<-[:CONTAINS]-(:Project)\n" +
					"\nRETURN t.id as taxonId, l.id as locusId, a.id as id, a.deprecated as deprecated, r.version as version, ad.sequence as sequence\n";
		}
		statement += "ORDER BY t.id, l.id, size(a.id), a.id SKIP $ LIMIT $";
		return query(new Query(statement, params));
	}

	@Override
	protected Result get(Allele.PrimaryKey key, long version) {
		String where = version == CURRENT_VERSION_VALUE ? "NOT EXISTS(r.to)" : "r.version = $";
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})-[:CONTAINS]->(a:Allele {id: $})-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)\n" +
				"WHERE " + where;
		if (key.getProjectId() != null) {
			statement += "\nMATCH (a)<-[:CONTAINS]-(p:Project {id: $}) WHERE p.deprecated = false\n" +
					"RETURN t.id as taxonId, l.id as locusId, a.id as id, a.deprecated as deprecated, r.version as version, ad.sequence as sequence, p.id as project\n";
		} else {
			statement += " AND NOT (a)<-[:CONTAINS]-(:Project)\n" +
					"RETURN t.id as taxonId, l.id as locusId, a.id as id, a.deprecated as deprecated, r.version as version, ad.sequence as sequence\n";
		}
		return query(new Query(statement, key.getTaxonId(), key.getLocusId(), key.getId(), version, key.getProjectId()));
	}

	@Override
	protected VersionedEntity<Allele.PrimaryKey> parseVersionedEntity(Map<String, Object> row) {
		return new VersionedEntity<>(new Allele.PrimaryKey((String) row.get("taxonId"), (String) row.get("locusId"), (String) row.get("id"), (String) row.get("project")),
				(long) row.get("version"),
				(boolean) row.get("deprecated"));
	}

	@Override
	protected Allele parse(Map<String, Object> row) {
		return new Allele((String) row.get("taxonId"),
				(String) row.get("locusId"),
				(String) row.get("id"),
				(long) row.get("version"),
				(boolean) row.get("deprecated"),
				(String) row.get("sequence"),
				(String) row.get("project"));
	}

	@Override
	protected boolean isPresent(Allele.PrimaryKey key) {
		String statement = "OPTIONAL MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})-[:CONTAINS]->(a:Allele {id: $})\n";
		statement += key.getProjectId() != null ?
				"OPTIONAL MATCH (a)<-[:CONTAINS]-(p:Project {id: $}) WHERE p.deprecated = false\n" :
				"WHERE NOT (a)<-[:CONTAINS]-(:Project)\n";
		statement += "RETURN COALESCE(a.deprecated = false, false)";
		return query(Boolean.class, new Query(statement, key.getTaxonId(), key.getLocusId(), key.getId(), key.getProjectId()));
	}

	@Override
	protected void store(Allele allele) {
		String statement = String.format("WITH $ as param\n%s", getInsertStatement());
		execute(new Query(statement, getInsertParam(allele)));
	}

	@Override
	protected void delete(Allele.PrimaryKey key) {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})-[:CONTAINS]->(a:Allele {id: $})\n" +
				"WHERE t.deprecated = false AND l.deprecated = false AND a.deprecated = false ";
		statement += key.getProjectId() != null ? "\nMATCH (a)<-[:CONTAINS]-(p:Project {id: $}) WHERE p.deprecated = false\n" :
				"AND NOT (a)<-[:CONTAINS]-(:Project)\n";
		statement += "SET a.deprecated = true\n";
		execute(new Query(statement, key.getTaxonId(), key.getLocusId(), key.getId(), key.getProjectId()));
	}

	@Override
	protected Query batch(Query query, List<Allele> alleles) {
		query.addParameter((Object) alleles.stream().map(this::getInsertParam).toArray());
		return query.appendQuery(getInsertStatement());
	}

	/**
	 * Verifies if any of the alleles represented by the primary keys received in the params doesn't exist
	 *
	 * @param references alleles {@link VersionedEntity<Allele.PrimaryKey> primary keys}
	 * @return {@code true} if any of alleles represented by the keys don't exist
	 */
	public boolean anyMissing(List<VersionedEntity<Allele.PrimaryKey>> references) {
		Optional<VersionedEntity<Allele.PrimaryKey>> optional = references.stream().filter(Objects::nonNull).findFirst();
		if (!optional.isPresent())
			return true;
		String taxon = optional.get().getPrimaryKey().getTaxonId();
		String statement = "MATCH (t:Taxon {id: $}) UNWIND $ as param\n" +
				"CALL apoc.when(param.project IS NOT NULL,\n" +
				"\"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: param.locus})-[:CONTAINS]->(a:Allele {id: param.allele})\n" +
				"WHERE (a)<-[:CONTAINS]-(:Project {id: param.project})\n" +
				"RETURN a.id as result\",\n" +
				"\"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: param.locus})-[:CONTAINS]->(a:Allele {id: param.allele})\n" +
				"WHERE NOT (a)<-[:CONTAINS]-(:Project)\n" +
				"RETURN a.id as result\",\n" +
				"{t: t, param: param}) YIELD value RETURN value.result as present";
		Result result = query(new Query(statement, taxon, references
				.stream()
				.filter(Objects::nonNull)
				.map(r -> new Object() {
					public final String locus = r.getPrimaryKey().getLocusId();
					public final String allele = r.getPrimaryKey().getId();
					public final String project = r.getPrimaryKey().getProjectId();
				})
				.toArray())
		);
		Iterator<Map<String, Object>> it = result.iterator();
		if (!it.hasNext())
			return true;
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(it, Spliterator.ORDERED), false)
				.anyMatch(r -> r.get("present") == null);
	}

	private Object getInsertParam(Allele allele) {
		return new Object() {
			public final String taxonId = allele.getPrimaryKey().getTaxonId();
			public final String locusId = allele.getPrimaryKey().getLocusId();
			public final String projectId = allele.getPrimaryKey().getProjectId();
			public final String id = allele.getPrimaryKey().getId();
			public final String sequence = allele.getSequence();
		};
	}

	private String getInsertStatement() {
		return "MATCH (t:Taxon {id: param.taxonId})-[:CONTAINS]->(l:Locus {id: param.locusId})\n" +
				"WHERE t.deprecated = false AND l.deprecated = false\n" +
				"CALL apoc.do.when(param.projectId IS NOT NULL,\n" +
				"    \"MATCH (p:Project {id: pid}) WHERE p.deprecated = false\n" +
				"    MERGE (l)-[:CONTAINS]->(a:Allele {id: aid})<-[:CONTAINS]-(p) RETURN a as allele\",\n" +
				"    \"OPTIONAL MATCH (l)-[:CONTAINS]->(a:Allele {id: aid}) WHERE NOT (a)<-[:CONTAINS]-(:Project)\n" +
				"    CALL apoc.do.when(a IS NOT NULL, 'RETURN a as allele', 'CREATE (l)-[:CONTAINS]->(n:Allele {id: aid}) RETURN n as allele', {l: l, a: a, aid: aid})\n" +
				"    YIELD value\n" +
				"    RETURN value.allele as allele\"\n" +
				", {l: l, pid: param.projectId, aid: param.id}) YIELD value as result\n" +
				"WITH result.allele as a, param.sequence as sequence\n" +
				"SET a.deprecated = false\n" +
				"WITH a, sequence\n" +
				"OPTIONAL MATCH (a)-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)\n" +
				"WHERE NOT EXISTS(r.to) SET r.to = datetime()\n" +
				"WITH a, sequence, COALESCE(MAX(r.version), 0) + 1 as v\n" +
				"CREATE (a)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(ad:AlleleDetails {sequence: sequence})";
	}

}
