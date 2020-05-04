package pt.ist.meic.phylodb.phylogeny.locus;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.db.Query;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class LocusRepository extends EntityRepository<Locus, Locus.PrimaryKey> {

	public LocusRepository(Session session) {
		super(session);
	}

	@Override
	protected Result getAll(int page, int limit, Object... filters) {
		if (filters == null || filters.length == 0)
			return null;
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus)-[r:CONTAINS_DETAILS]->(ld:LocusDetails)\n" +
				"WHERE t.deprecated = false AND l.deprecated = false AND NOT EXISTS(r.to)\n" +
				"RETURN t.id as taxonId, l.id as id, l.deprecated as deprecated, r.version as version,\n" +
				"l.name as name, ld.description as description\n" +
				"ORDER BY t.id, l.id SKIP $ LIMIT $";
		return query(new Query(statement, filters[0], page, limit));
	}

	@Override
	protected Result get(Locus.PrimaryKey key, long version) {
		String where = version == CURRENT_VERSION_VALUE ? "NOT EXISTS(r.to)" : "r.version = $";
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})-[r:CONTAINS_DETAILS]->(ld:LocusDetails)\n" +
				"WHERE " + where + "\n" +
				"RETURN t.id as taxonId, l.id as id, l.deprecated as deprecated, r.version as version,\n" +
				"l.name as name, ld.description as description";
		return query(new Query(statement, key.getTaxonId(), key.getId(), version));
	}

	@Override
	protected Locus parse(Map<String, Object> row) {
		return new Locus((String) row.get("taxonId"),
				(String) row.get("id"),
				(long) row.get("version"),
				(boolean) row.get("deprecated"),
				(String) row.get("description"));
	}

	@Override
	protected boolean isPresent(Locus.PrimaryKey key) {
		String statement = "OPTIONAL MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})\n" +
				"RETURN COALESCE(l.deprecated = false, false)";
		return query(Boolean.class, new Query(statement, key.getTaxonId(), key.getId()));
	}

	@Override
	protected Result store(Locus locus) {
		String statement = "MATCH (t:Taxon {id: $})\n" +
				"WHERE t.deprecated = false\n" +
				"MERGE (t)-[:CONTAINS]->(l:Locus {id: $}) SET l.deprecated = false WITH l\n" +
				"OPTIONAL MATCH (l)-[r:CONTAINS_DETAILS]->(ld:LocusDetails)\n" +
				"WHERE NOT EXISTS(r.to) SET r.to = datetime()\n" +
				"WITH l, COALESCE(MAX(r.version), 0) + 1 as v\n" +
				"CREATE (l)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(ld:LocusDetails {description: $})";
		return execute(new Query(statement, locus.getPrimaryKey().getTaxonId(), locus.getPrimaryKey().getId(), locus.getDescription()));
	}

	@Override
	protected void delete(Locus.PrimaryKey key) {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $}) SET l.deprecated = true WITH l\n" +
				"MATCH (l)-[:CONTAINS]->(a:Allele) SET a.deprecated = true\n";
		execute(new Query(statement, key.getTaxonId(), key.getId()));
	}

	public boolean anyMissing(List<Entity<Locus.PrimaryKey>> references) {
		String parameterized = references.stream().map((i) -> "$").collect(Collectors.joining(","));
		String statement = String.format("MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus)\n" +
				"WHERE t.deprecated = false AND l.deprecated = false AND l.id IN [%s]\n" +
				"RETURN COUNT(l.id)", parameterized);
		List<String> params = new ArrayList<>();
		params.add(references.get(0).getPrimaryKey().getTaxonId());
		params.addAll(references.stream().map(r -> r.getPrimaryKey().getId()).collect(Collectors.toList()));
		return query(Integer.class, new Query(statement, params.toArray())) != references.size();
	}

}
