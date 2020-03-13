package pt.ist.meic.phylodb.typing.dataset;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.db.Query;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class DatasetRepository extends EntityRepository<Dataset, UUID> {

	public DatasetRepository(Session session) {
		super(session);
	}

	@Override
	protected List<Dataset> getAll(int page, int limit, Object... filters) {
		String statement = "MATCH (d:Dataset)\n" +
				"WHERE NOT EXISTS(d.to)\n" +
				"RETURN d SKIP $0 LIMIT $1";
		return queryAll(Dataset.class, new Query(statement, page, limit));
	}

	@Override
	protected Dataset get(UUID id) {
		String statement = "MATCH (d:Dataset {id: $0})-[:HAS]->(s:Schema)-[:HAS]->(l:Locus)<-[:CONTAINS]-(t:Taxon)\n" +
				"WHERE NOT EXISTS(d.to) AND NOT EXISTS(s.to) AND NOT EXISTS(l.to) AND NOT EXISTS(t.to)\n" +
				"WITH d, s, t, collect(l) as loci\n" +
				"RETURN d.id as datasetId, d.description as description, t.id as taxonId, s.id as schemaId";
		Result r = query(new Query(statement, id));
		if (!r.iterator().hasNext())
			return null;
		Map<String, Object> row = r.iterator().next();
		return new Dataset(UUID.fromString((String) row.get("datasetId")), (String) row.get("description"), (String) row.get("taxonId"), (String) row.get("schemaId"));
	}

	@Override
	protected boolean exists(Dataset dataset) {
		return get(dataset.getId()) != null;
	}

	@Override
	protected void create(Dataset dataset) {
		String statement = "CREATE (d:Dataset {id: $0, description: $1, from: datetime()}) WITH d\n" +
				"MATCH (s:Schema {id: $2})-[:HAS]->(l:Locus)<-[:CONTAINS]-(t:Taxon {id: $3})\n" +
				"WHERE NOT EXISTS(s.to) AND NOT EXISTS(l.to) AND NOT EXISTS(t.to)\n" +
				"WITH d, s, t, collect(l) as loci\n" +
				"CREATE (d)-[:HAS]->(s)";
		Query query = new Query(statement, dataset.getId(), dataset.getDescription(), dataset.getSchemaId(), dataset.getTaxonId());
		execute(query);
	}

	@Override
	protected void update(Dataset dataset) {
		String statement = "MATCH (d:Dataset {id: $0})\n" +
				"WHERE NOT EXISTS(d.to)\n" +
				"CALL apoc.refactor.cloneNodes([d], true) YIELD input, output\n" +
				"SET d.description = $1, d.from = datetime(), output.to = datetime()";
		execute(new Query(statement, dataset.getId(), dataset.getDescription()));
	}

	@Override
	protected void delete(UUID id) {
		String statement = "MATCH (d:Dataset {id: $0})\n" +
				"WHERE NOT EXISTS(d.to)\n" +
				"SET d.to = datetime()";
		execute(new Query(statement, id));
	}

}
