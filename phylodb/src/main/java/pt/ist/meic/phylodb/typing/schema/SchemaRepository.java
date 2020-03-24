package pt.ist.meic.phylodb.typing.schema;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.db.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository
public class SchemaRepository extends EntityRepository<Schema, Schema.PrimaryKey> {

	public SchemaRepository(Session session) {
		super(session);
	}

	@Override
	protected List<Schema> getAll(int page, int limit, Object... filters) {
		if (filters == null || filters.length == 0)
			return null;
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus)<-[h:HAS]-(s:Schema {id: $})\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to) AND NOT EXISTS(s.to) WITH t, s, l\n" +
				"ORDER BY h.part\n" +
				"RETURN t.id as taxonId, s.id as id, s.type as type, s.description as description, collect(l.id) as lociId SKIP $ LIMIT $";
		Result r = query(new Query(statement, filters[0], page, limit));
		List<Schema> schemas = new ArrayList<>();
		while (r.iterator().hasNext()) {
			Map<String, Object> row = r.iterator().next();
			schemas.add(new Schema((String)row.get("taxonId"), (String) row.get("id"), (String) row.get("type"), (String) row.get("description"), (String[]) row.get("lociId")));
		}
		return schemas;
	}

	@Override
	protected Schema get(Schema.PrimaryKey key) {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus)<-[h:HAS]-(s:Schema {id: $})\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to) AND NOT EXISTS(s.to) WITH t, s, l\n" +
				"ORDER BY h.part\n" +
				"RETURN t.id as taxonId, s.id as id, s.type as type, s.description as description, collect(l.id) as lociId";
		Result r = query(new Query(statement, key.getTaxonId(), key.getId()));
		if (!r.iterator().hasNext())
			return null;
		Map<String, Object> row = r.iterator().next();
		return new Schema((String) row.get("taxonId"), (String) row.get("id"), (String) row.get("type"), (String) row.get("description"), (String[]) row.get("lociId"));
	}

	@Override
	protected boolean exists(Schema schema) {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus)<-[:HAS]-(s:Schema {id: $})\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to) AND NOT EXISTS(s.to)\n" +
				"WITH s, collect(l) as loci\n" +
				"RETURN s";
		return query(Schema.class, new Query(statement, schema.getTaxonId(), schema.getId())) != null;
	}

	@Override
	protected void create(Schema schema) {
		String statement = "CREATE (s:Schema {id: $, description: $, type: $ from: datetime()}) WITH s\n " +
				"MATCH (t:Taxon {id: $}) WHERE NOT EXISTS(t.to)\n";
		Query query = new Query(statement, schema.getId(), schema.getType(), schema.getDescription(), schema.getTaxonId());
		composeLoci(schema, query);
		execute(query);
	}

	@Override
	protected void update(Schema schema) {
		String statement = "MATCH (s:Schema {id: $})-[:HAS]->(l:Locus)<-[:CONTAINS]-(t:Taxon {id: $})\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to) AND NOT EXISTS(s.to)\n" +
				"WITH s, collect(l) as loci\n" +
				"CALL apoc.refactor.cloneNodes([s], false) YIELD input, output\n" +
				"SET output.description = $, output.from = datetime(), s.to = datetime()\n" +
				"WITH s, t\n";
		Query query = new Query(statement, schema.getId(), schema.getTaxonId(), schema.getDescription());
		composeLoci(schema, query);
		execute(query);
}

	@Override
	protected void delete(Schema.PrimaryKey key) {
		String statement = "MATCH (s:Schema {id: $})-[:HAS]->(l:Locus)<-[:CONTAINS]-(t:Taxon {id: $})\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to) AND NOT EXISTS(s.to)\n" +
				"WITH s, collect(l) as loci\n" +
				"SET s.to = datetime()";
		execute(new Query(statement, key.getId(), key.getTaxonId()));
	}

	public Schema find(String taxonId, String[] lociIds) {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus)<-[:HAS]-(s:Schema)\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to) AND NOT EXISTS(s.to)\n" +
				"WITH s, t\n";
		Query query = new Query(statement, taxonId);
		for (int i = 0; i < lociIds.length; i++) {
			query.appendQuery("MATCH (s)-[:HAS {part: %s}]->(l%s:Locus {id: $})<-[:CONTAINS]-(t) WHERE NOT EXISTS(l%s.to) WITH s, t\n", i, i);
			query.addParameter(lociIds[i]);
		}
		query.subQuery(query.length() - "WITH s, t\n".length());
		query.appendQuery("\nRETURN s");
		List<String> params = new ArrayList<>();
		params.add(taxonId);
		params.addAll(Arrays.asList(lociIds));
		return query(Schema.class, new Query(statement, params));
	}

	private void composeLoci(Schema schema, Query query) {
		String[] ids = schema.getLociIds();
		for (int i = 0; i < ids.length; i++) {
			query.appendQuery("MATCH (t)-[:CONTAINS]->(l%s:Locus {id: $}) WHERE NOT EXISTS(l%s.to)\n", i, i);
			query.appendQuery("CREATE (s)-[:HAS {part: %s}]->(l%s) WITH s, t\n", i + 1, i);
			query.addParameter(ids[i]);
		}
		query.subQuery(query.length() - "WITH s, t\n".length());
	}

}
