package pt.ist.meic.phylodb.typing.schema;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.db.Query;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
				"WHERE s.deprecated = false AND NOT EXISTS(r.to) WITH t, s, r, sd, l, h\n" +
				"ORDER BY h.part\n" +
				"WITH t, s, r, sd, collect(DISTINCT {taxon: t.id, id: l.id, deprecated: l.deprecated, version: h.version}) as lociIds\n" +
				"RETURN t.id as taxonId, s.id as id, s.type as type, s.deprecated as deprecated, r.version as version, " +
				"sd.description as description, lociIds\n" +
				"ORDER BY t.id, s.id SKIP $ LIMIT $";
		return query(new Query(statement, filters[0], page, limit));
	}

	@Override
	protected Result get(Schema.PrimaryKey key, long version) {
		String where = version == CURRENT_VERSION_VALUE ? "NOT EXISTS(r.to)" : "r.version = $";
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus)<-[h:HAS]-(sd:SchemaDetails)<-[r:CONTAINS_DETAILS]-(s:Schema {id: $})\n" +
				"WHERE " + where + " WITH t, s, r, sd, l, h\n" +
				"ORDER BY h.part\n" +
				"WITH t, s, r, sd, collect(DISTINCT {id: l.id, deprecated: l.deprecated, version: h.version}) as lociIds\n" +
				"RETURN t.id as taxonId, s.id as id, s.type as type, s.deprecated as deprecated, r.version as version, " +
				"sd.description as description, lociIds";
		return query(new Query(statement, key.getTaxonId(), key.getId(), version));
	}

	@Override
	protected Schema parse(Map<String, Object> row) {
		String taxonId = (String) row.get("taxonId");
		List<Entity<Locus.PrimaryKey>> lociIds = Arrays.stream((Map<String, Object>[]) row.get("lociIds"))
				.map(m -> new Entity<>(new Locus.PrimaryKey(taxonId, (String) m.get("id")), (long) m.get("version"), (boolean) m.get("deprecated")))
				.collect(Collectors.toList());
		return new Schema(taxonId,
				(String) row.get("id"),
				(long) row.get("version"),
				(boolean) row.get("deprecated"),
				Method.valueOf(((String) row.get("type")).toUpperCase()),
				(String) row.get("description"),
				lociIds);
	}

	@Override
	protected boolean isPresent(Schema.PrimaryKey key) {
		String statement = "OPTIONAL MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus)<-[h:HAS]-(sd:SchemaDetails)<-[r:CONTAINS_DETAILS]-(s:Schema {id: $})\n" +
				"WITH s, collect(l) as loci\n" +
				"RETURN COALESCE(s.deprecated = false, false)";
		return query(Boolean.class, new Query(statement, key.getTaxonId(), key.getId()));
	}

	@Override
	protected void store(Schema schema) {
		if (isPresent(schema.getPrimaryKey()))
			put(schema);
		post(schema);
	}

	@Override
	protected void delete(Schema.PrimaryKey key) {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus)<-[h:HAS]-(sd:SchemaDetails)<-[r:CONTAINS_DETAILS]-(s:Schema {id: $})\n" +
				"SET s.deprecated = true\n";
		execute(new Query(statement, key.getTaxonId(), key.getId()));
	}

	public Optional<Schema> find(String taxonId, Method type, String[] lociIds) {
		if (taxonId == null || lociIds == null || lociIds.length == 0)
			return Optional.empty();
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus)<-[:HAS]-(sd:SchemaDetails)<-[r:CONTAINS_DETAILS]-(s:Schema {type: $})\n" +
				"WHERE s.deprecated = false AND NOT EXISTS(r.to)\n" +
				"WITH t, s, r, sd\n";
		Query query = new Query(statement, taxonId, type.getName());
		for (int i = 0; i < lociIds.length; i++) {
			query.appendQuery("MATCH (sd)-[:HAS {part: %s}]->(l%s:Locus {id: $}) WITH t, s, r, sd\n", i + 1, i);
			query.addParameter(lociIds[i]);
		}
		query.appendQuery("MATCH (sd)-[h:HAS]->(l:Locus) WITH t, s, r, sd, h, l\n" +
				"ORDER BY h.part\n" +
				"WITH t, s, r, sd, collect(DISTINCT {id: l.id, deprecated: l.deprecated, version: h.version}) as lociIds\n" +
				"RETURN t.id as taxonId, s.id as id, s.deprecated as deprecated, r.version as version,\n" +
				"s.type as type, sd.description as description, lociIds");
		Result result = query(query);
		if (!result.iterator().hasNext())
			return Optional.empty();
		return Optional.of(parse(result.iterator().next()));
	}

	public Optional<Schema> find(Dataset.PrimaryKey key) {
		if (key == null)
			return Optional.empty();
		String statement = "MATCH (p:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})-[r1:CONTAINS_DETAILS]->(dd:DatasetDetails)-[h1:HAS]->(s:Schema)-[r2:CONTAINS_DETAILS]->(sd:SchemaDetails)\n" +
				"WHERE NOT EXISTS(r1.to) AND r2.version = h1.version\n" +
				"MATCH (sd)-[h2:HAS]->(l:Locus)<-[:CONTAINS]-(t:Taxon)  WITH t, s, r2, sd, h2, l\n" +
				"ORDER BY h2.part\n" +
				"WITH t, s, r2, sd, collect(DISTINCT {id: l.id, deprecated: l.deprecated, version: h2.version}) as lociIds\n" +
				"RETURN t.id as taxonId, s.id as id, s.deprecated as deprecated, r2.version as version,\n" +
				"s.type as type, sd.description as description, lociIds";
		Result result = query(new Query(statement, key.getProjectId(), key.getId()));
		if (!result.iterator().hasNext())
			return Optional.empty();
		return Optional.of(parse(result.iterator().next()));
	}

	private Result post(Schema schema) {
		String statement = "CREATE (s:Schema {id: $, type: $, deprecated: false})-[:CONTAINS_DETAILS {from: datetime(), version: 1}]->(sd:SchemaDetails {description: $}) WITH sd\n " +
				"MATCH (t:Taxon {id: $}) WHERE t.deprecated = false WITH t, sd\n";
		Query query = new Query(statement, schema.getPrimaryKey().getId(), schema.getType().getName(), schema.getDescription(), schema.getPrimaryKey().getTaxonId());
		composeLoci(schema, query);
		return execute(query);
	}

	private Result put(Schema schema) {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus)<-[h:HAS]-(sd:SchemaDetails)<-[r:CONTAINS_DETAILS]-(s:Schema {id: $})\n" +
				"WHERE NOT EXISTS(r.to)\n" +
				"WITH t, s, r, sd, collect(l.id) as loci\n" +
				"SET s.deprecated = false, r.to = datetime() WITH t, s, r.version + 1 as v\n" +
				"CREATE (s)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(sd:SchemaDetails {description: $})\n" +
				"WITH t, sd\n";
		Query query = new Query(statement, schema.getPrimaryKey().getTaxonId(), schema.getPrimaryKey().getId(), schema.getDescription());
		composeLoci(schema, query);
		return execute(query);
	}

	private void composeLoci(Schema schema, Query query) {
		String[] ids = schema.getLociIds().toArray(new String[0]);
		for (int i = 0; i < ids.length; i++) {
			query.appendQuery("MATCH (t)-[:CONTAINS]->(l:Locus {id: $})-[r:CONTAINS_DETAILS]->(:LocusDetails)\n" +
					"WHERE l.deprecated = false AND NOT EXISTS(r.to)\n" +
					"CREATE (sd)-[:HAS {part: %s, version: r.version}]->(l) WITH sd, t\n", i + 1)
					.addParameter(ids[i]);
		}
		query.subQuery(query.length() - "WITH sd, t\n".length());
	}

}
