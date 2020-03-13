package pt.ist.meic.phylodb.typing.schema;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.db.Query;

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
		String statement = "MATCH (t:Taxon {id: $0})-[:CONTAINS]->(l:Locus)<-[:HAS]-(s:Schema)\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to) AND NOT EXISTS(s.to)\n" +
				"WITH s, collect(l) as loci\n" +
				"RETURN s SKIP $1 LIMIT $2";
		return queryAll(Schema.class, new Query(statement, filters[0], page, limit));
	}

	@Override
	protected Schema get(Schema.PrimaryKey key) {
		String statement = "MATCH (t:Taxon {id: $0})-[:CONTAINS]->(l:Locus)<-[h:HAS]-(s:Schema {id: $1})\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to) AND NOT EXISTS(s.to) WITH t, s, l\n" +
				"ORDER BY h.part\n" +
				"RETURN t.id as taxonId, s.id as id, s.description as description, collect(l.id) as lociId";
		Result r = query(new Query(statement, key.getTaxonId(), key.getId()));
		if (!r.iterator().hasNext())
			return null;
		Map<String, Object> row = r.iterator().next();
		return new Schema((String) row.get("taxonId"), (String) row.get("id"), (String) row.get("description"), (String[]) row.get("lociId"));
	}

	@Override
	protected boolean exists(Schema schema) {
		return get(schema.getPrimaryKey()) != null;
	}

	@Override
	protected void create(Schema schema) {
		String statement = "CREATE (s:Schema {id: $0, description: $1, from: datetime()}) WITH s\n " +
				"MATCH (t:Taxon {id: $2}) WHERE NOT EXISTS(t.to)\n";
		Query query = new Query(statement, schema.getId(), schema.getDescription(), schema.getTaxonId());
		String[] ids = schema.getLociIds();
		for (int i = 0, p = 3; i < ids.length; i++, p++) {
			query.appendQuery("MATCH (t)-[:CONTAINS]->(l%s:Locus {id: $%s}) WHERE NOT EXISTS(l%s.to)\n", i, p, i);
			query.appendQuery("CREATE (s)-[:HAS {part: %s}]->(l%s) WITH s, t\n", i + 1, i);
			query.addParameter(ids[i]);
		}
		query.subQuery(query.length() - "WITH s, t\n".length());
		execute(query);
	}

	@Override
	protected void update(Schema schema) {
		String statement = "MATCH (s:Schema {id: $0})-[:HAS]->(l:Locus)<-[:CONTAINS]-(t:Taxon {id: $1})\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to) AND NOT EXISTS(s.to)\n" +
				"WITH s, collect(l) as loci\n" +
				"CALL apoc.refactor.cloneNodes([s], true) YIELD input, output\n" +
				"SET s.description = $2, s.from = datetime(), output.to = datetime()";
		execute(new Query(statement, schema.getId(), schema.getTaxonId(), schema.getDescription()));
	}

	@Override
	protected void delete(Schema.PrimaryKey key) {
		String statement = "MATCH (s:Schema {id: $0})-[:HAS]->(l:Locus)<-[:CONTAINS]-(t:Taxon {id: $1})\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to) AND NOT EXISTS(s.to)\n" +
				"WITH s, collect(l) as loci\n" +
				"SET s.to = datetime()";
		execute(new Query(statement, key.getId(), key.getTaxonId()));
	}

}
