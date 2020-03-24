package pt.ist.meic.phylodb.phylogeny.allele;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.db.Query;

import java.util.List;
import java.util.function.BiFunction;

@Repository
public class AlleleRepository extends EntityRepository<Allele, Allele.PrimaryKey> {

	public AlleleRepository(Session session) {
		super(session);
	}

	@Override
	protected List<Allele> getAll(int page, int limit, Object... filters) {
		if (filters == null || filters.length == 0)
			return null;
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})-[:CONTAINS]->(a:Allele)\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to) AND NOT EXISTS(a.to)\n" +
				"RETURN a SKIP $ LIMIT $";
		return queryAll(Allele.class, new Query(statement, filters[0], filters[1], page, limit));
	}

	@Override
	protected Allele get(Allele.PrimaryKey key) {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})-[:CONTAINS]->(a:Allele {id: $})\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to) AND NOT EXISTS(a.to)\n" +
				"RETURN a";
		return query(Allele.class, new Query(statement, key.getTaxonId(), key.getLocusId(), key.getId()));
	}

	@Override
	protected boolean exists(Allele allele) {
		return get(allele.getPrimaryKey()) != null;
	}

	@Override
	protected void create(Allele allele) {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to)\n" +
				"CREATE (l)-[:CONTAINS]->(a:Allele {id: $, sequence: $, from: datetime()})";
		execute(new Query(statement, allele.getTaxonId(), allele.getLocusId(), allele.getId(), allele.getSequence()));
	}

	@Override
	protected void update(Allele allele) {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})-[:CONTAINS]->(a:Allele {id: $})\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to) AND NOT EXISTS(a.to)\n" +
				"WITH a\n" +
				"CALL apoc.refactor.cloneNodes([a], true) YIELD input, output\n" +
				"SET output.sequence = $, output.from = datetime(), a.to = datetime() WITH output\n" +
				"MATCH (p:Profile)-[r:HAS]->(output) REMOVE r";
		execute(new Query(statement, allele.getTaxonId(),  allele.getLocusId(), allele.getId(), allele.getSequence()));
	}

	@Override
	protected void delete(Allele.PrimaryKey key) {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})-[:CONTAINS]->(a:Allele {id: $})\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to) AND NOT EXISTS(a.to)\n" +
				"SET a.to = datetime()";
		execute(new Query(statement, key.getTaxonId(), key.getLocusId(), key.getId()));
	}

	public void saveAllOnConflictUpdate(String taxon, String locus, List<Allele> alleles) {
		saveAll(taxon, locus, alleles.size(), (q, i) -> {
			if(find(new Allele.PrimaryKey(taxon, locus, alleles.get(i).getId())) != null) {
				q.appendQuery("MATCH (l)-[:CONTAINS]->(a%s:Allele {id: $})\n", i)
						.appendQuery("WHERE NOT EXISTS(a%s.to)\n", i)
						.appendQuery("WITH l, a%s\n", i)
						.appendQuery("CALL apoc.refactor.cloneNodes([a%s], true) YIELD input, output\n", i)
						.appendQuery("SET a%s.sequence = $, a%s.from = datetime(), output.to = datetime()\n", i, i);
			}
			else {
				q.appendQuery("CREATE (l)-[:CONTAINS]->(:Allele {id: $, sequence: $, from: datetime()})\n");
			}
			q.addParameter(alleles.get(i).getId(), alleles.get(i).getSequence());
			q.appendQuery("WITH l\n");
			return 1;
		});
	}

	public void saveAllOnConflictSkip(String taxon, String locus, List<Allele> alleles) {
		saveAll(taxon, locus, alleles.size(), (q, i) -> {
			if(find(new Allele.PrimaryKey(taxon, locus, alleles.get(i).getId())) != null) {
				LOG.info("The allele " + alleles.get(i).getId() + " with sequence " + alleles.get(i).getSequence() + " could not be created since it already exists");
				return 0;
			} else {
				q.appendQuery("CREATE (l)-[:CONTAINS]->(:Allele {id: $, sequence: $, from: datetime()})\n");
				q.addParameter(alleles.get(i).getId(), alleles.get(i).getSequence());
				q.appendQuery("WITH l\n");
				return 1;
			}
		});
	}

	private void saveAll(String taxon, String locus, int size, BiFunction<Query, Integer, Integer> compose) {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to)\n" +
				"WITH l\n";
		Query query = new Query(statement, taxon, locus);
		int toExecute = 0;
		for (int i = 0; i < size; i++)
			toExecute += compose.apply(query, i);
		if(toExecute != 0) {
			query.subQuery(query.length() - "WITH l\n".length());
			execute(query);
		}
	}

	public boolean existsAll(String taxonId, String[] lociIds, String[] allelesIds) {
		String statement = "MATCH (t:Taxon {id: $})\n" +
				"WHERE NOT EXISTS(t.to)\n" +
				"WITH t, collect(null) as alleles\n";
		Query query = new Query(statement, taxonId);
		for (int i = 0; i < lociIds.length; i++) {
			query.appendQuery("OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: $})-[:CONTAINS]->(a:Allele {id: $}) WITH t, alleles + collect(a) as alleles\n");
			query.addParameter(lociIds[i], allelesIds[i]);
		}
		query.appendQuery("RETURN size(alleles)");
		return query(Integer.class, query) == allelesIds.length;
	}

}
