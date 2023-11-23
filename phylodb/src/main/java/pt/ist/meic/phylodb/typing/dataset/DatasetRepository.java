package pt.ist.meic.phylodb.typing.dataset;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.db.Query;
import pt.ist.meic.phylodb.utils.db.VersionedRepository;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.Map;

/**
 * Class that contains the implementation of the {@link VersionedRepository} for datasets
 */
@Repository
@Import({ Session.class })
public class DatasetRepository extends VersionedRepository<Dataset, Dataset.PrimaryKey> {


	@Autowired
	public DatasetRepository(Session session) {
		super(session);
	}

	@Override
	protected Result getAllEntities(int page, int limit, Object... filters) {
		if (filters == null || filters.length != 1)
			return null;
		String statement = "MATCH (p:Project {id: $})-[:CONTAINS]->(d:Dataset)-[r:CONTAINS_DETAILS]->(dd:DatasetDetails)\n" +
				"WHERE p.deprecated = false AND d.deprecated = false AND r.to IS NULL\n" +
				"RETURN p.id as projectId, d.id as datasetId, d.deprecated as deprecated, r.version as version\n" +
				"ORDER BY p.id, size(d.id), d.id SKIP $ LIMIT $";
		return query(new Query(statement, filters[0], page, limit));
	}

	@Override
	protected Result get(Dataset.PrimaryKey id, long version) {
		String where = version == CURRENT_VERSION_VALUE ? "r1.to IS NULL" : "r1.version = $";
		String statement = "MATCH (p:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})-[r1:CONTAINS_DETAILS]->(dd:DatasetDetails)-[h:HAS]->(s:Schema)-[r2:CONTAINS_DETAILS]->(sd:SchemaDetails)\n" +
				"WHERE r2.version = h.version AND " + where + "\n" +
				"MATCH (sd)-[:HAS]->(l:Locus)<-[:CONTAINS]-(t:Taxon)\n" +
				"WITH p, d, r1, dd, h, s, t, collect(l) as loci\n" +
				"RETURN p.id as projectId, d.id as datasetId, d.deprecated as deprecated, r1.version as version, " +
				"dd.description as description, t.id as taxonId, s.id as schemaId, h.version as schemaVersion, s.deprecated as schemaDeprecated";
		return query(new Query(statement, id.getProjectId(), id.getId(), version));
	}

	@Override
	protected VersionedEntity<Dataset.PrimaryKey> parseVersionedEntity(Map<String, Object> row) {
		return new VersionedEntity<>(new Dataset.PrimaryKey((String) row.get("projectId"), (String) row.get("datasetId")),
				(long) row.get("version"),
				(boolean) row.get("deprecated"));
	}

	@Override
	protected Dataset parse(Map<String, Object> row) {
		VersionedEntity<Schema.PrimaryKey> schema = new VersionedEntity<>(new Schema.PrimaryKey((String) row.get("taxonId"),
				(String) row.get("schemaId")),
				(long) row.get("schemaVersion"),
				(boolean) row.get("schemaDeprecated"));
		return new Dataset((String) row.get("projectId"),
				(String) row.get("datasetId"),
				(long) row.get("version"),
				(boolean) row.get("deprecated"),
				(String) row.get("description"),
				schema);
	}

	@Override
	protected boolean isPresent(Dataset.PrimaryKey key) {
		String statement = "OPTIONAL MATCH (p:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})\n" +
				"RETURN COALESCE(d.deprecated = false, false)";
		return query(Boolean.class, new Query(statement, key.getProjectId(), key.getId()));
	}

	@Override
	protected void store(Dataset dataset) {
		String statement = "MATCH (p:Project {id: $})\n" +
				"MERGE (p)-[:CONTAINS]->(d:Dataset {id : $}) SET d.deprecated = false WITH d\n" +
				"OPTIONAL MATCH (d)-[r:CONTAINS_DETAILS]->(dd:DatasetDetails)" +
				"WHERE r.to IS NULL SET r.to = datetime()\n" +
				"WITH d, COALESCE(MAX(r.version), 0) + 1 as v\n" +
				"CREATE (d)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(dd:DatasetDetails {description: $}) WITH dd\n" +
				"MATCH (s:Schema {id: $})-[r:CONTAINS_DETAILS]->(sd:SchemaDetails)-[:HAS]->(l:Locus)<-[:CONTAINS]-(t:Taxon {id: $})\n" +
				"WHERE r.to IS NULL\n" +
				"WITH dd, s, r, collect(l) as loci\n" +
				"CREATE (dd)-[:HAS {version: r.version}]->(s)";
		Schema.PrimaryKey schemaKey = dataset.getSchema().getPrimaryKey();
		Query query = new Query(statement, dataset.getPrimaryKey().getProjectId(), dataset.getPrimaryKey().getId(), dataset.getDescription(), schemaKey.getId(), schemaKey.getTaxonId());
		execute(query);
	}

	@Override
	protected void delete(Dataset.PrimaryKey id) {
		String statement = "MATCH (p:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $}) SET d.deprecated = true WITH d\n" +
				"MATCH (d)-[:CONTAINS]->(p:Profile) SET p.deprecated = true WITH d\n" +
				"MATCH (d)-[:CONTAINS]->(i:Isolate) SET i.deprecated = true WITH d\n" +
				"MATCH (d)-[:CONTAINS]->(p1:Profile)-[di:DISTANCES]->(p2:Profile)\n" +
				"SET di.deprecated = true\n" +
				"WITH d, di.id as analysis, collect(di) as ignored\n" +
				"MATCH (d)-[:CONTAINS]->(p:Profile)-[h:HAS {inferenceId: analysis}]->(c:Coordinate)\n" +
				"WHERE h.deprecated = false\n" +
				"SET h.deprecated = true";
		execute(new Query(statement, id.getProjectId(), id.getId()));
	}

}
