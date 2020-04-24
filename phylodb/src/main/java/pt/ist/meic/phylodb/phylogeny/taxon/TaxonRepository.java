package pt.ist.meic.phylodb.phylogeny.taxon;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.db.Query;

import java.util.Map;

@Repository
public class TaxonRepository extends EntityRepository<Taxon, String> {

	public TaxonRepository(Session session) {
		super(session);
	}

	@Override
	protected Result getAll(int page, int limit, Object... filters) {
		String statement = "MATCH (t:Taxon)-[r:CONTAINS_DETAILS]->(td:TaxonDetails)\n" +
				"WHERE t.deprecated = false AND NOT EXISTS(r.to)\n" +
				"RETURN t.id as id, t.deprecated as deprecated, r.version as version,\n" +
				"t.name as name, td.description as description\n" +
				"ORDER BY t.id SKIP $ LIMIT $";
		return query(new Query(statement, page, limit));
	}

	@Override
	protected Result get(String key, long version) {
		String where = version == CURRENT_VERSION_VALUE ? "NOT EXISTS(r.to)" : "r.version = $";
		String statement = "MATCH (t:Taxon {id: $})-[r:CONTAINS_DETAILS]->(td:TaxonDetails)\n" +
				"WHERE " + where + "\n" +
				"RETURN t.id as id, t.deprecated as deprecated, r.version as version,\n" +
				"td.description as description";
		return query(new Query(statement, key, version));
	}

	@Override
	protected Taxon parse(Map<String, Object> row) {
		return new Taxon((String) row.get("id"),
				(long) row.get("version"),
				(boolean) row.get("deprecated"),
				(String) row.get("description"));
	}

	@Override
	protected boolean isPresent(String key) {
		String statement = "OPTIONAL MATCH (t:Taxon {id: $})\n" +
				"RETURN COALESCE(t.deprecated = false, false)";
		return query(Boolean.class, new Query(statement, key));
	}

	@Override
	protected Result store(Taxon taxon) {
		String statement = "MERGE (t:Taxon {id: $}) SET t.deprecated = false WITH t\n" +
				"OPTIONAL MATCH (t)-[r:CONTAINS_DETAILS]->(td:TaxonDetails)\n" +
				"WHERE NOT EXISTS(r.to) SET r.to = datetime()\n" +
				"WITH t, COALESCE(MAX(r.version), 0) + 1 as v\n" +
				"CREATE (t)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(td:TaxonDetails {description: $})";
		return execute(new Query(statement, taxon.getPrimaryKey(), taxon.getDescription()));
	}

	@Override
	protected void delete(String key) {
		String statement = "MATCH (t:Taxon {id: $}) SET t.deprecated = true WITH t\n" +
				"MATCH (t)-[:CONTAINS]->(l:Locus) SET l.deprecated = true WITH l\n" +
				"MATCH (l)-[:CONTAINS]->(a:Allele) SET a.deprecated = true";
		execute(new Query(statement, key));
	}

}
