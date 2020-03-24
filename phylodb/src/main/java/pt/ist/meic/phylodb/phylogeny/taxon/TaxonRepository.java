package pt.ist.meic.phylodb.phylogeny.taxon;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.db.Query;

import java.util.List;

@Repository
public class TaxonRepository extends EntityRepository<Taxon, String> {

	public TaxonRepository(Session session) {
		super(session);
	}

	@Override
	protected List<Taxon> getAll(int page, int limit, Object... filters) {
		String statement = "MATCH (t:Taxon)\n" +
				"WHERE NOT EXISTS(t.to)\n" +
				"RETURN t SKIP $ LIMIT $";
		return queryAll(Taxon.class, new Query(statement, page, limit));
	}

	@Override
	protected Taxon get(String key) {
		String statement = "MATCH (t:Taxon {id: $})\n" +
				"WHERE NOT EXISTS(t.to)\n" +
				"RETURN t";
		return query(Taxon.class, new Query(statement, key));
	}

	@Override
	protected boolean exists(Taxon taxon) {
		return get(taxon.getId()) != null;
	}

	@Override
	protected void create(Taxon taxon) {
		String statement = "CREATE (t:Taxon {id: $, description: $, from: datetime()})";
		execute(new Query(statement, taxon.getId(), taxon.getDescription()));
	}

	@Override
	protected void update(Taxon taxon) {
		String statement = "MATCH (t:Taxon {id: $}) WHERE NOT EXISTS(t.to)\n" +
				"WITH t\n" +
				"CALL apoc.refactor.cloneNodes([t], true) YIELD input, output\n" +
				"SET output.description = $, output.from = datetime(), t.to = datetime()";
		execute(new Query(statement, taxon.getId(), taxon.getDescription()));
	}

	@Override
	protected void delete(String key) {
		String statement = "MATCH (t:Taxon {id: $}) WHERE NOT EXISTS(t.to) SET t.to = datetime() WITH t\n" +
				"MATCH (t)-[:CONTAINS]->(l:Locus) WHERE NOT EXISTS(l.to) SET l.to = datetime() WITH l\n" +
				"MATCH (l)-[:CONTAINS]->(a:Allele) WHERE NOT EXISTS(a.to) SET a.to = datetime()";
		execute(new Query(statement, key));
	}

}
