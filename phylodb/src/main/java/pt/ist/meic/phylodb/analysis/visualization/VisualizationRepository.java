package pt.ist.meic.phylodb.analysis.visualization;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.analysis.visualization.model.Coordinate;
import pt.ist.meic.phylodb.analysis.visualization.model.Visualization;
import pt.ist.meic.phylodb.analysis.visualization.model.VisualizationAlgorithm;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.db.AlgorithmsRepository;
import pt.ist.meic.phylodb.utils.db.Query;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class VisualizationRepository extends AlgorithmsRepository<Visualization, Visualization.PrimaryKey> {

	protected VisualizationRepository(Session session) {
		super(session);
	}

	@Override
	protected Result getAll(int page, int limit, Object... filters) {
		if (filters == null || filters.length == 0)
			return null;
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(ds:Dataset {id: $})\n" +
				"MATCH (ds)-[:CONTAINS]->(p1:Profile)-[d:DISTANCES {id: $}]->(p2:Profile)\n" +
				"WITH pj, ds, d.id as analysis, collect(d) as ignored\n" +
				"MATCH (ds)-[:CONTAINS]->(p:Profile)-[:HAS]->(c:Coordinate {analysisId: analysis})\n" +
				"WHERE c.deprecate = false\n" +
				"RETURN pj.id as projectId, ds.id as datasetId, analysis as analysisId, c.id as id, c.deprecated as deprecated, c.algorithm as algorithm,\n" +
				"collect(DISTINCT {profileId: p.id, x: c.x, y: c.y}) as coordinates\n" +
				"ORDER BY pj.id, d.id, analysis, size(c.id), c.id SKIP $ LIMIT $";
		return query(new Query(statement, filters[0], filters[1], filters[2], page, limit));
	}

	@Override
	protected Result get(Visualization.PrimaryKey key) {
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(ds:Dataset {id: $})\n" +
				"MATCH (ds)-[:CONTAINS]->(p1:Profile)-[d:DISTANCES {id: $}]->(p2:Profile)\n" +
				"WITH pj, ds, d.id as analysis, collect(d) as ignored\n" +
				"MATCH (ds)-[:CONTAINS]->(p:Profile)-[:HAS]->(c:Coordinate {analysisId: analysis, id: $})\n" +
				"WHERE c.deprecate = false\n" +
				"RETURN pj.id as projectId, ds.id as datasetId, analysis as analysisId, c.id as id, c.deprecated as deprecated, c.algorithm as algorithm,\n" +
				"collect(DISTINCT {profileId: p.id, x: c.x, y: c.y}) as coordinates";
		return query(new Query(statement, key.getProjectId(), key.getDatasetId(), key.getAnalysisId(), key.getId()));
	}

	@Override
	protected Visualization parse(Map<String, Object> row) {
		List<Coordinate> list = new ArrayList<>();
		UUID projectId = UUID.fromString(row.get("projectId").toString());
		UUID datasetId = UUID.fromString(row.get("datasetId").toString());
		for (Map<String, Object> coordinates: (Map<String, Object>[]) row.get("coordinates")) {
			Profile.PrimaryKey profile = new Profile.PrimaryKey(projectId, datasetId, (String) coordinates.get("profileId"));
			list.add(new Coordinate(profile, (int) coordinates.get("x"), (int) coordinates.get("y")));
		}
		return new Visualization(projectId,
				datasetId,
				UUID.fromString(row.get("analysisId").toString()),
				UUID.fromString(row.get("id").toString()),
				(boolean) row.get("deprecated"),
				VisualizationAlgorithm.valueOf(row.get("algorithm").toString()),
				list
		);
	}

	@Override
	protected boolean isPresent(Visualization.PrimaryKey key) {
		String statement = "OPTIONAL MATCH (pj:Project {id: $})-[:CONTAINS]->(ds:Dataset {id: $})\n" +
				"OPTIONAL MATCH (ds)-[:CONTAINS]->(p1:Profile)-[d:DISTANCES {id: $}]->(p2:Profile)\n" +
				"WITH pj, ds, d.id as analysis, collect(d) as ignored\n" +
				"OPTIONAL MATCH (ds)-[:CONTAINS]->(p:Profile)-[:HAS]->(c:Coordinate {analysisId: analysis, id: $})\n" +
				"WITH c.deprecated as deprecated, collect(c) as ignored\n" +
				"RETURN COALESCE(deprecated = false, false)";
		return query(Boolean.class, new Query(statement, key.getProjectId(), key.getDatasetId(), key.getAnalysisId(), key.getId()));
	}

	@Override
	protected void store(Visualization entity) {
		throw new NotImplementedException();
	}

	@Override
	protected void delete(Visualization.PrimaryKey key) {
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(ds:Dataset {id: $})\n" +
				"MATCH (ds)-[:CONTAINS]->(p1:Profile)-[d:DISTANCES {id: $}]->(p2:Profile)\n" +
				"WITH pj, ds, d.id as analysis, collect(d) as ignored\n" +
				"MATCH (ds)-[:CONTAINS]->(p:Profile)-[:HAS]->(c:Coordinate {analysisId: analysis, id: $})\n" +
				"WHERE c.deprecate = false\n" +
				"SET c.deprecated = true";
		execute(new Query(statement, key.getProjectId(), key.getDatasetId(), key.getAnalysisId(), key.getId()));
	}

}
