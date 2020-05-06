package pt.ist.meic.phylodb.phylogeny.allele;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.utils.db.BatchRepository;
import pt.ist.meic.phylodb.utils.db.Query;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.*;

@Repository
public class AlleleRepository extends BatchRepository<Allele, Allele.PrimaryKey> {

	public AlleleRepository(Session session) {
		super(session);
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
		statement += "ORDER BY t.id, l.id, a.id SKIP $ LIMIT $";
		return query(new Query(statement, params));
	}

	@Override
	protected Result get(Allele.PrimaryKey key, long version) {
		String where = version == CURRENT_VERSION_VALUE ? "NOT EXISTS(r.to)" : "r.version = $";
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})-[:CONTAINS]->(a:Allele {id: $})-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)\n" +
				"WHERE " + where;
		if (key.getProject() != null) {
			statement += "\nMATCH (a)<-[:CONTAINS]-(p:Project {id: $}) WHERE p.deprecated = false\n" +
					"RETURN t.id as taxonId, l.id as locusId, a.id as id, a.deprecated as deprecated, r.version as version, ad.sequence as sequence, p.id as project\n";
		} else {
			statement += " AND NOT (a)<-[:CONTAINS]-(:Project)\n" +
					"RETURN t.id as taxonId, l.id as locusId, a.id as id, a.deprecated as deprecated, r.version as version, ad.sequence as sequence\n";
		}
		return query(new Query(statement, key.getTaxonId(), key.getLocusId(), key.getId(), version, key.getProject()));
	}

	@Override
	protected Allele parse(Map<String, Object> row) {
		UUID project = row.get("project") != null ? UUID.fromString((String) row.get("project")) : null;
		return new Allele((String) row.get("taxonId"),
				(String) row.get("locusId"),
				(String) row.get("id"),
				(long) row.get("version"),
				(boolean) row.get("deprecated"),
				(String) row.get("sequence"),
				project);
	}

	@Override
	protected boolean isPresent(Allele.PrimaryKey key) {
		String statement = "OPTIONAL MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})-[:CONTAINS]->(a:Allele {id: $})\n";
		statement += key.getProject() != null ?
				"OPTIONAL MATCH (a)<-[:CONTAINS]-(p:Project {id: $}) WHERE p.deprecated = false\n" :
				"WHERE NOT (a)<-[:CONTAINS]-(:Project)\n";
		statement += "RETURN COALESCE(a.deprecated = false, false)";
		return query(Boolean.class, new Query(statement, key.getTaxonId(), key.getLocusId(), key.getId(), key.getProject()));
	}

	@Override
	protected Result store(Allele allele) {
		Object[] params = new Object[]{allele.getTaxonId(), allele.getLocusId()};
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})\n" +
				"WHERE t.deprecated = false AND l.deprecated = false\n";
		if (allele.getPrimaryKey().getProject() != null) {
			params = new Object[]{allele.getTaxonId(), allele.getLocusId(), allele.getPrimaryKey().getProject()};
			statement += "MATCH(p:Project {id: $}) WHERE p.deprecated = false WITH p, l\n";
		}
		Query query = new Query(statement, params);
		composeStore(query, allele);
		return execute(query);
	}

	@Override
	protected void delete(Allele.PrimaryKey key) {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})-[:CONTAINS]->(a:Allele {id: $})\n" +
				"WHERE t.deprecated = false AND l.deprecated = false AND a.deprecated = false ";
		statement += key.getProject() != null ? "\nMATCH (a)<-[:CONTAINS]-(p:Project {id: $}) WHERE p.deprecated = false\n" :
				"AND NOT (a)<-[:CONTAINS]-(:Project)\n";
		statement += "SET a.deprecated = true\n";
		execute(new Query(statement, key.getTaxonId(), key.getLocusId(), key.getId(), key.getProject()));
	}

	@Override
	protected Query init(String... params) {
		String project = "WITH l";
		List<String> parameters = new ArrayList<String>() {{
			add(params[0]);
			add(params[1]);
		}};
		if (params[2] != null) {
			project = "MATCH (p:Project {id: $}) WHERE p.deprecated = false WITH p, l";
			parameters.add(params[2]);
		}
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})\n" +
				"WHERE t.deprecated = false AND l.deprecated = false\n" + project + "\n";
		return new Query(statement, parameters.toArray());
	}

	@Override
	protected void batch(Query query, Allele allele) {
		composeStore(query, allele);
		query.appendQuery(allele.getPrimaryKey().getProject() == null ? "WITH l\n" : "WITH p, l\n");
	}

	@Override
	protected void arrange(Query query, String... params) {
		query.subQuery(query.length() - (params[2] == null ? "WITH l\n".length() : "WITH p, l\n".length()));
	}

	private void composeStore(Query query, Allele allele) {
		String with = "l, a", project = "";
		if (allele.getPrimaryKey().getProject() != null) {
			with = "p, l, a";
			project = "<-[:CONTAINS]-(p)";
		}
		String statement = "MERGE (l)-[:CONTAINS]->(a:Allele {id: $})" + project + " SET a.deprecated = false WITH " + with + "\n" +
				"OPTIONAL MATCH (a)-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)\n" +
				"WHERE NOT EXISTS(r.to) SET r.to = datetime()\n" +
				"WITH " + with + ", COALESCE(MAX(r.version), 0) + 1 as v\n" +
				"CREATE (a)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(ad:AlleleDetails {sequence: $}) ";
		query.appendQuery(statement).addParameter(allele.getPrimaryKey().getId(), allele.getSequence());
	}

	public boolean anyMissing(List<Entity<Allele.PrimaryKey>> references) {
		Optional<Entity<Allele.PrimaryKey>> optional = references.stream().filter(Objects::nonNull).findFirst();
		if (!optional.isPresent())
			return true;
		String taxon = optional.get().getPrimaryKey().getTaxonId();
		Query query = new Query("MATCH (t:Taxon {id: $}) WITH t, 0 as c\n", taxon);
		for (Entity<Allele.PrimaryKey> reference : references) {
			if (reference == null)
				continue;
			String where = "NOT (a)<-[:CONTAINS]-(:Project)";
			List<Object> params = new ArrayList<Object>() {{
				add(reference.getPrimaryKey().getLocusId());
				add(reference.getPrimaryKey().getId());
			}};
			if (reference.getPrimaryKey().getProject() != null) {
				where = "(a)<-[:CONTAINS]-(:Project {id: $})";
				params.add(reference.getPrimaryKey().getProject());
			}
			query.appendQuery("OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: $})-[:CONTAINS]->(a:Allele {id: $})\n" +
					"WHERE " + where + "\n" +
					"WITH t, c + COALESCE(COUNT(a.id), 0) as c\n")
					.addParameter(params.toArray());
		}
		query.appendQuery("RETURN c");
		return query(Integer.class, query) != references.stream().filter(Objects::nonNull).count();
	}

}
