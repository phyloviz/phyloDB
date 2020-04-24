package pt.ist.meic.phylodb.typing.schema;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.db.Query;
import pt.ist.meic.phylodb.utils.service.Entity;
import pt.ist.meic.phylodb.utils.service.Reference;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SchemaRepository extends EntityRepository<Schema, Schema.PrimaryKey> {

	public SchemaRepository(Session session) {
		super(session);
	}

	@Override
	protected Result getAll(int page, int limit, Object... filters) {
		if (filters == null || filters.length == 0)
			return null;
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus)<-[h:HAS]-(sd:SchemaDetails)<-[r:CONTAINS_DETAILS]-(s:Schema)\n" +
				"WHERE s.deprecated = false AND NOT EXISTS(r.to) WITH t, s, sd, l\n" +
				"ORDER BY h.part\n" +
				"RETURN t.id as taxonId, s.id as id, s.deprecated as deprecated, r.version as version, " +
				"collect([l.id, l.deprecated, h.version]) as lociIds, " +
				"s.type as type, sd.description as description\n" +
				"SKIP $ LIMIT $";
		return query(new Query(statement, filters[0], page, limit));
	}

	@Override
	protected Result get(Schema.PrimaryKey key, long version) {
		String where = version == CURRENT_VERSION_VALUE ? "NOT EXISTS(r.to)" : "r.version = $";
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus)<-[h:HAS]-(sd:SchemaDetails)<-[r:CONTAINS_DETAILS]-(s:Schema {id: $})\n" +
				"WHERE " + where + " WITH t, s, sd, l\n" +
				"ORDER BY h.part\n" +
				"RETURN t.id as taxonId, s.id as id, s.deprecated as deprecated, r.version as version,\n" +
				"s.type as type, sd.description as description, collect([l.id, l.deprecated, h.version]) as lociIds";
		return query(new Query(statement, key.getTaxonId(), key.getId()));
	}

	@Override
	protected Schema parse(Map<String, Object> row) {
		List<Reference<String>> lociIds = Arrays.stream((Object[][]) row.get("lociIds"))
				.map(a -> new Reference<>((String) a[0], (long) a[1], (boolean) a[2]))
				.collect(Collectors.toList());
		return new Schema((String) row.get("taxonId"),
				(String) row.get("id"),
				(long) row.get("version"),
				(boolean) row.get("deprecated"),
				Method.valueOf((String) row.get("type")),
				(String) row.get("description"),
				lociIds);
	}

	@Override
	protected boolean isPresent(Schema.PrimaryKey key) {
		String statement = "OPTIONAL MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus)<-[h:HAS]-(sd:SchemaDetails)<-[r:CONTAINS_DETAILS]-(s:Schema {id: $})\n" +
				"RETURN COALESCE(s.deprecated = false, false)";
		return query(Boolean.class, new Query(statement, key.getTaxonId(), key.getId()));
	}

	@Override
	protected Result store(Schema schema) {
		if (isPresent(schema.getPrimaryKey()))
			return put(schema);
		return post(schema);
	}

	@Override
	protected void delete(Schema.PrimaryKey key) {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus)<-[h:HAS]-(sd:SchemaDetails)<-[r:CONTAINS_DETAILS]-(s:Schema {id: $})\n" +
				"SET s.deprecated = true\n";
		execute(new Query(statement, key.getTaxonId(), key.getId()));
	}

	public Optional<Schema> find(Schema schema) {
		String taxonId = schema.getPrimaryKey().getTaxonId();
		String[] lociIds = schema.getLociIds().stream()
				.map(Entity::getPrimaryKey)
				.toArray(String[]::new);
		String where = schema.getVersion() == CURRENT_VERSION_VALUE ? "NOT EXISTS(r.to)" : "r.version = $";
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus)<-[h:HAS]-(sd:SchemaDetails)<-[r:CONTAINS_DETAILS]-(s:Schema)\n" +
				"WHERE s.deprecated = false AND  " + where + "\n" +
				"WITH t, s, sd\n";
		Query query = new Query(statement, taxonId);
		for (int i = 0; i < lociIds.length; i++) {
			query.appendQuery("MATCH (sd)-[:HAS {part: %s}]->(l%s:Locus {id: $}) WITH t, s, sd\n", i, i);
			query.addParameter(lociIds[i]);
		}
		query.appendQuery("MATCH (sd)-[h:has]->(l:Locus)\n" +
				"ORDER BY h.part\n" +
				"RETURN t.id as taxonId, s.id as id, s.deprecated as deprecated, r.version as version,\n" +
				"s.type as type, sd.description as description, collect(l.id) as lociId");
		List<Object> params = new ArrayList<>();
		params.add(taxonId);
		params.addAll(Arrays.asList(lociIds));
		Result result = query(new Query(statement, params));
		if (!result.iterator().hasNext())
			return Optional.empty();
		return Optional.of(parse(result.iterator().next()));
	}

	public Optional<Schema> find(Dataset.PrimaryKey key) {
		String statement = "MATCH (p:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})-[r1:CONTAINS_DETAILS]->(dd:DatasetDetails)-[h:HAS]->(s:Schema)-[r2:CONTAINS_DETAILS]->(sd:SchemaDetails)\n" +
				"WHERE NOT EXISTS(r1.to) AND r2.version = h.version\n" +
				"MATCH (sd)-[:HAS]->(l:Locus)<-[:CONTAINS]-(t:Taxon)  WITH t, s, sd, l\n" +
				"ORDER BY h.part\n" +
				"WITH s, sd, t, collect(l) as loci\n" +
				"RETURN t.id as taxonId, s.id as id, s.deprecated as deprecated, r2.version as version,\n" +
				"s.type as type, sd.description as description, collect(l.id) as lociId";
		Result result = query(new Query(statement, key.getProjectId(), key.getId()));
		if (!result.iterator().hasNext())
			return Optional.empty();
		return Optional.of(parse(result.iterator().next()));
	}

	private Result post(Schema schema) {
		String statement = "CREATE (s:Schema {id: $, type: $, deprecated: false})-[:CONTAINS_DETAILS {from: datetime(), version 1}]->(sd:SchemaDetails {description: $}) WITH sd\n " +
				"MATCH (t:Taxon {id: $}) WHERE t.deprecated = false\n";
		Query query = new Query(statement, schema.getPrimaryKey(), schema.getType(), schema.getDescription(), schema.getPrimaryKey().getTaxonId());
		composeLoci(schema, query);
		return execute(query);
	}

	private Result put(Schema schema) {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus)<-[h:HAS]-(sd:SchemaDetails)<-[r:CONTAINS_DETAILS]-(s:Schema {id: $})\n" +
				"WHERE NOT EXISTS(r.to)\n" +
				"SET s.deprecated = false, r.to = datetime() WITH a, r.version + 1 as v\n" +
				"CREATE (s)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(:SchemaDetails {description: $})\n" +
				"WITH t\n";
		Query query = new Query(statement, schema.getPrimaryKey().getTaxonId(), schema.getPrimaryKey(), schema.getDescription());
		composeLoci(schema, query);
		return execute(query);
	}

	private void composeLoci(Schema schema, Query query) {
		String[] ids = schema.getLociIds().stream()
				.map(Reference::getPrimaryKey)
				.toArray(String[]::new);
		for (int i = 0; i < ids.length; i++) {
			query.appendQuery("MATCH (t)-[:CONTAINS]->(l%s:Locus {id: $})-[r:CONTAINS_DETAILS]->(:LocusDetails)\n" +
					"WHERE l%s.deprecated = false AND NOT EXISTS(r.to)\n" +
					"CREATE (sd)-[:HAS {part: %s, version: r.version}]->(l%s) WITH sd, t\n", i, i, i + 1, i)
					.addParameter(ids[i]);
		}
		query.subQuery(query.length() - "WITH sd, t\n".length());
	}

}
