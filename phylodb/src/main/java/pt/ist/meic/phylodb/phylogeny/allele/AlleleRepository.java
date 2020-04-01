package pt.ist.meic.phylodb.phylogeny.allele;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.db.Query;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Repository
public class AlleleRepository extends EntityRepository<Allele, Allele.PrimaryKey> {

	public AlleleRepository(Session session) {
		super(session);
	}

	@Override
	protected Result getAll(int page, int limit, Object... filters) {
		if (filters == null || filters.length != 2)
			return null;
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})-[:CONTAINS]->(a:Allele)-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)\n" +
				"WHERE t.deprecated = false AND l.deprecated = false AND a.deprecated = false AND NOT EXISTS(r.to)\n" +
				"RETURN t.id as taxonId, l.id as locusId, a.id as id, a.deprecated as deprecated, r.version as version,\n" +
				"ad.sequence as sequence\n" +
				"SKIP $ LIMIT $";
		return query(new Query(statement, filters[0], filters[1], page, limit));
	}

	@Override
	protected Result get(Allele.PrimaryKey key, int version) {
		String where = version == CURRENT_VERSION_VALUE ? "NOT EXISTS(r.to)" : "r.version = $";
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})-[:CONTAINS]->(a:Allele {id: $})-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)\n" +
				"WHERE " + where + "\n" +
				"RETURN t.id as taxonId, l.id as locusId, a.id as id, a.deprecated as deprecated, r.version as version,\n" +
				"ad.sequence as sequence";
		return query(new Query(statement, key.getTaxonId(), key.getLocusId(), key.getId(), version));
	}

	@Override
	protected Allele parse(Map<String, Object> row) {
		return new Allele((String) row.get("taxonId"),
				(String) row.get("locusId"),
				(String) row.get("id"),
				(int) row.get("version"),
				(boolean) row.get("deprecated"),
				(String) row.get("sequence"));
	}

	@Override
	protected boolean isPresent(Allele.PrimaryKey key) {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})-[:CONTAINS]->(a:Allele {id: $})\n" +
				"WHERE t.deprecated = false AND l.deprecated = false\n" +
				"RETURN a.deprecated = false";
		return query(Boolean.class, new Query(statement, key.getTaxonId(), key.getLocusId(), key.getId()));
	}

	@Override
	protected void store(Allele allele) {
		Query query = new Query("MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})\n" +
				"WHERE t.deprecated = false AND l.deprecated = false\n", allele.getTaxonId(),  allele.getLocusId(), allele.getId());
		composeStore(query, allele);
		execute(query);
	}

	@Override
	protected void delete(Allele.PrimaryKey key) {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})-[:CONTAINS]->(a:Allele {id: $})\n" +
				"SET a.deprecated = true\n";
		execute(new Query(statement, key.getTaxonId(), key.getLocusId(), key.getId()));
	}

	public void saveAllOnConflictSkip(String taxon, String locus, List<Allele> alleles) {
		saveAll(taxon, locus, alleles, (q, a) -> {
			if(isPresent(a.getPrimaryKey())) {
				LOG.info("The allele " + a.getId() + " with sequence " + a.getSequence() + " could not be created since it already exists");
				return 0;
			} else {
				composeStore(q, new Allele(taxon, locus, a.getId(), a.getSequence()));
				q.appendQuery("WITH l\n");
				return 1;
			}
		});
	}

	public void saveAllOnConflictUpdate(String taxon, String locus, List<Allele> alleles) {
		saveAll(taxon, locus, alleles, (q, a) -> {
			composeStore(q, new Allele(taxon, locus, a.getId(), a.getSequence()));
			q.appendQuery("WITH l\n");
			return 1;
		});
	}

	private void composeStore(Query query, Allele allele) {
		String statement = "MERGE (l)-[:CONTAINS]->(a:Allele {id: $}) SET a.deprecated = false WITH l, a\n" +
				"OPTIONAL MATCH (a)-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)\n" +
				"WHERE NOT EXISTS(r.to) SET r.to = datetime()\n" +
				"WITH l, a, COALESCE(MAX(r.version), 0) + 1 as v\n" +
				"CREATE (a)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(ad:AlleleDetails {sequence: $}) ";
		query.appendQuery(statement).addParameter(allele.getId(), allele.getSequence());
	}

	private void saveAll(String taxon, String locus, List<Allele> alleles, BiFunction<Query, Allele, Integer> compose) {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})\n" +
				"WHERE t.deprecated = false AND l.deprecated = false\n" +
				"WITH l\n";
		Query query = new Query(statement, taxon, locus);
		int toExecute = 0;
		for (Allele allele : alleles)
			toExecute += compose.apply(query, allele);
		if(toExecute != 0) {
			query.subQuery(query.length() - "WITH l\n".length());
			execute(query);
		}
	}

}
