package pt.ist.meic.phylodb.analysis.visualization;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.analysis.visualization.model.Coordinate;
import pt.ist.meic.phylodb.analysis.visualization.model.Visualization;
import pt.ist.meic.phylodb.analysis.visualization.model.VisualizationAlgorithm;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.db.Query;
import pt.ist.meic.phylodb.utils.db.UnversionedRepository;
import pt.ist.meic.phylodb.utils.service.Entity;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class that contains the implementation of the {@link UnversionedRepository} for visualizations
 */
@Repository
public class VisualizationRepository extends UnversionedRepository<Visualization, Visualization.PrimaryKey> {

	protected VisualizationRepository(Session session) {
		super(session);
	}

	@Override
	protected Result getAllEntities(int page, int limit, Object... filters) {
		if (filters == null || filters.length == 0)
			return null;
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(ds:Dataset {id: $})\n" +
				"MATCH (ds)-[:CONTAINS]->(p:Profile)-[h:HAS {inferenceId: $}]->(c:Coordinate)\n" +
				"WHERE h.deprecated = false\n" +
				"WITH pj, ds, h.inferenceId as inferenceId, h.id as id, h.deprecated as deprecated, collect(DISTINCT {profileId: p.id, component: c.component, x: c.x, y: c.y}) as coordinates\n" +
				"RETURN pj.id as projectId, ds.id as datasetId, inferenceId as inferenceId, id as id, deprecated as deprecated\n" +
				"ORDER BY pj.id, ds.id, inferenceId, size(id), id SKIP $ LIMIT $";
		return query(new Query(statement, filters[0], filters[1], filters[2], page, limit));
	}

	@Override
	protected Result get(Visualization.PrimaryKey key) {
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(ds:Dataset {id: $})\n" +
				"MATCH (ds)-[:CONTAINS]->(p:Profile)-[h:HAS {inferenceId: $, id: $}]->(c:Coordinate)\n" +
				"WHERE h.deprecated = false\n" +
				"WITH pj, ds, h.inferenceId as inferenceId, p, h, c\n" +
				"ORDER BY pj.id, ds.id, inferenceId, size(h.id), h.id, h.component, p.id, c.x, c.y\n" +
				"RETURN pj.id as projectId, ds.id as datasetId, inferenceId as inferenceId, h.id as id, h.deprecated as deprecated, h.algorithm as algorithm,\n" +
				"collect(DISTINCT {profileId: p.id, component: h.component, x: c.x, y: c.y}) as coordinates";
		return query(new Query(statement, key.getProjectId(), key.getDatasetId(), key.getInferenceId(), key.getId()));
	}

	@Override
	protected Entity<Visualization.PrimaryKey> parseEntity(Map<String, Object> row) {
		return new Entity<>(new Visualization.PrimaryKey((String) row.get("projectId"), (String) row.get("datasetId"), (String) row.get("inferenceId"), (String) row.get("id")),
				(boolean) row.get("deprecated"));
	}

	@Override
	protected Visualization parse(Map<String, Object> row) {
		List<Coordinate> list = new ArrayList<>();
		String projectId = row.get("projectId").toString();
		String datasetId = row.get("datasetId").toString();
		for (Map<String, Object> coordinates : (Map<String, Object>[]) row.get("coordinates")) {
			Profile.PrimaryKey profile = new Profile.PrimaryKey(projectId, datasetId, (String) coordinates.get("profileId"));
			list.add(new Coordinate(profile, (long) coordinates.get("component"), (double) coordinates.get("x"), (double) coordinates.get("y")));
		}
		return new Visualization(projectId,
				datasetId,
				row.get("inferenceId").toString(),
				row.get("id").toString(),
				(boolean) row.get("deprecated"),
				VisualizationAlgorithm.valueOf(row.get("algorithm").toString().toUpperCase()),
				list
		);
	}

	@Override
	protected boolean isPresent(Visualization.PrimaryKey key) {
		String statement = "OPTIONAL MATCH (pj:Project {id: $})-[:CONTAINS]->(ds:Dataset {id: $})\n" +
				"OPTIONAL MATCH (ds)-[:CONTAINS]->(p:Profile)-[h:HAS {inferenceId: $, id: $}]->(c:Coordinate)\n" +
				"WITH h.deprecated as deprecated, collect(c) as ignored\n" +
				"RETURN COALESCE(deprecated = false, false)";
		return query(Boolean.class, new Query(statement, key.getProjectId(), key.getDatasetId(), key.getInferenceId(), key.getId()));
	}

	@Override
	protected void store(Visualization entity) {
		throw new NotImplementedException();
	}

	@Override
	protected void delete(Visualization.PrimaryKey key) {
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(ds:Dataset {id: $})\n" +
				"MATCH (ds)-[:CONTAINS]->(p:Profile)-[h:HAS {inferenceId: $, id: $}]->(c:Coordinate)\n" +
				"WHERE h.deprecated = false\n" +
				"SET h.deprecated = true";
		execute(new Query(statement, key.getProjectId(), key.getDatasetId(), key.getInferenceId(), key.getId()));
	}

}
