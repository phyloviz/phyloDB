package pt.ist.meic.phylodb.phylogeny.allele;

import org.apache.logging.log4j.util.TriConsumer;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.db.Query;

import java.util.List;

@Repository
public class AlleleRepository extends EntityRepository<Allele, Allele.PrimaryKey> {

	public AlleleRepository(Session session) {
		super(session);
	}

	@Override
	protected List<Allele> getAll(int page, int limit, Object... filters) {
		if (filters == null || filters.length == 0)
			return null;
		String statement = "MATCH (t:Taxon {id: $0})-[:CONTAINS]->(l:Locus {id: $1})-[:CONTAINS]->(a:Allele)\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to) AND NOT EXISTS(a.to)\n" +
				"RETURN a SKIP $2 LIMIT $3";
		return queryAll(Allele.class, new Query(statement, filters[0], filters[1], page, limit));
	}

	@Override
	protected Allele get(Allele.PrimaryKey key) {
		String statement = "MATCH (t:Taxon {id: $0})-[:CONTAINS]->(l:Locus {id: $1})-[:CONTAINS]->(a:Allele {id: $2})\n" +
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
		String statement = "MATCH (t:Taxon {id: $0})-[:CONTAINS]->(l:Locus {id: $1})\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to)\n" +
				"CREATE (l)-[:CONTAINS]->(a:Allele {id: $2, sequence: $3, from: datetime()})";
		execute(new Query(statement, allele.getTaxonId(), allele.getLocusId(), allele.getId(), allele.getSequence()));
	}

	@Override
	protected void update(Allele allele) {
		String statement = "MATCH (t:Taxon {id: $0})-[:CONTAINS]->(l:Locus {id: $1})-[:CONTAINS]->(a:Allele {id: $2})\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to) AND NOT EXISTS(a.to)\n" +
				"WITH a\n" +
				"CALL apoc.refactor.cloneNodes([a], true) YIELD input, output\n" +
				"SET a.sequence = $3, a.from = datetime(), output.to = datetime()";
		execute(new Query(statement, allele.getTaxonId(),  allele.getLocusId(), allele.getId(), allele.getSequence()));
	}

	@Override
	protected void delete(Allele.PrimaryKey key) {
		String statement = "MATCH (t:Taxon {id: $0})-[:CONTAINS]->(l:Locus {id: $1})-[:CONTAINS]->(a:Allele {id: $2})\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to) AND NOT EXISTS(a.to)\n" +
				"SET a.to = datetime()";
		execute(new Query(statement, key.getTaxonId(), key.getLocusId(), key.getId()));
	}

	public void saveAllOnConflictUpdate(String taxon, String locus, List<Allele> alleles) {
		saveAll(taxon, locus, alleles.size(), (q, i, p) -> {
			if(find(new Allele.PrimaryKey(taxon, locus, alleles.get(i).getId())) != null) {
				q.appendQuery("MATCH (l)-[:CONTAINS]->(a%s:Allele {id: $%s})\n", i, p++)
						.appendQuery("WHERE NOT EXISTS(a%s.to)\n", i)
						.appendQuery("WITH l, a%s\n", i)
						.appendQuery("CALL apoc.refactor.cloneNodes([a%s], true) YIELD input, output\n", i)
						.appendQuery("SET a%s.sequence = $%s, a%s.from = datetime(), output.to = datetime()\n", i, p, i);
			}
			else {
				q.appendQuery("CREATE (l)-[:CONTAINS]->(:Allele {id: $%s, sequence: $%s, from: datetime()})\n", p++, p);
			}
			q.addParameter(alleles.get(i).getId(), alleles.get(i).getSequence());
			q.appendQuery("WITH l\n");
		});
	}

	public void saveAllOnConflictSkip(String taxon, String locus, List<Allele> alleles) {
		saveAll(taxon, locus, alleles.size(), (q, i, p)-> {
			if(find(new Allele.PrimaryKey(taxon, locus, alleles.get(i).getId())) != null) {
				LOG.info("The allele " + alleles.get(i).getId() + " with sequence " + alleles.get(i).getSequence() + " could not be created since it already exists");
			} else {
				q.appendQuery("CREATE (l)-[:CONTAINS]->(:Allele {id: $%s, sequence: $%s, from: datetime()})\n", p++, p);
				q.addParameter(alleles.get(i).getId(), alleles.get(i).getSequence());
				q.appendQuery("WITH l\n");
			}
		});
	}

	private void saveAll(String taxon, String locus, int size, TriConsumer<Query, Integer, Integer> compose) {
		String statement = "MATCH (t:Taxon {id: $0})-[:CONTAINS]->(l:Locus {id: $1})\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to)\n" +
				"WITH l\n";
		Query query = new Query(statement, taxon, locus);
		for (int i = 0, p = 2; i < size; i++, p += 2)
			compose.accept(query, i, p);
		query.subQuery(query.length() - "WITH l\n".length());
		execute(query);
	}

}
