package pt.ist.meic.phylodb.analysis.inference;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.analysis.inference.model.Inference;
import pt.ist.meic.phylodb.analysis.inference.model.Edge;
import pt.ist.meic.phylodb.analysis.inference.model.InferenceAlgorithm;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.db.AlgorithmsRepository;
import pt.ist.meic.phylodb.utils.db.Query;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class InferenceRepository extends AlgorithmsRepository<Inference, Inference.PrimaryKey> {

	protected InferenceRepository(Session session) {
		super(session);
	}

	@Override
	protected Result getAll(int page, int limit, Object... filters) {
		if (filters == null || filters.length == 0)
			return null;
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(ds:Dataset {id: $})\n" +
				"MATCH (ds)-[:CONTAINS]->(p1:Profile)-[d:DISTANCES]->(p2:Profile)\n" +
				"WHERE d.deprecated = false\n" +
				"WITH pj, ds, p1, p2, d\n" +
				"ORDER BY d.distance\n" +
				"RETURN pj.id as projectId, ds.id as datasetId, d.id as id, d.deprecated as deprecated, d.algorithm as algorithm,\n" +
				"collect(DISTINCT {from: p1.id, fromVersion: d.fromVersion, fromDeprecated: p1.deprecated, distance: d.distance,\n" +
				"to: p2.id, toVersion: d.toVersion, toDeprecated: p2.deprecated}) as edges\n" +
				"ORDER BY pj.id, d.id, size(d.id), d.id SKIP $ LIMIT $";
		return query(new Query(statement, filters[0], filters[1], page, limit));
	}

	@Override
	protected Result get(Inference.PrimaryKey key) {
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(ds:Dataset {id: $})\n" +
				"MATCH (ds)-[:CONTAINS]->(p1:Profile)-[d:DISTANCES {id: $}]->(p2:Profile)\n" +
				"WITH pj, ds, p1, p2, d\n" +
				"ORDER BY d.distance\n" +
				"RETURN pj.id as projectId, ds.id as datasetId, d.id as id, d.deprecated as deprecated, d.algorithm as algorithm,\n" +
				"collect(DISTINCT {from: p1.id, fromVersion: d.fromVersion, fromDeprecated: p1.deprecated, distance: d.distance,\n" +
				"to: p2.id, toVersion: d.toVersion, toDeprecated: p2.deprecated}) as edges\n";
		return query(new Query(statement, key.getProjectId(), key.getDatasetId(), key.getId()));
	}

	@Override
	protected Inference parse(Map<String, Object> row) {
		List<Edge> list = new ArrayList<>();
		String projectId = row.get("projectId").toString();
		String datasetId = row.get("datasetId").toString();
		for (Map<String, Object> edge: (Map<String, Object>[]) row.get("edges")) {
			Entity<Profile.PrimaryKey> from = new Entity<>(new Profile.PrimaryKey(projectId, datasetId, (String) edge.get("from")), (long) edge.get("fromVersion"), (boolean) edge.get("fromDeprecated"));
			Entity<Profile.PrimaryKey> to = new Entity<>(new Profile.PrimaryKey(projectId, datasetId, (String) edge.get("to")), (long) edge.get("toVersion"), (boolean) edge.get("toDeprecated"));
			list.add(new Edge(from, to, Math.toIntExact((long) edge.get("distance"))));
		}
		return new Inference(projectId,
				datasetId,
				row.get("id").toString(),
				(boolean) row.get("deprecated"), InferenceAlgorithm.valueOf(row.get("algorithm").toString().toUpperCase()),
				list
		);
	}

	@Override
	protected boolean isPresent(Inference.PrimaryKey key) {
		String statement = "OPTIONAL MATCH (pj:Project {id: $})-[:CONTAINS]->(ds:Dataset {id: $})\n" +
				"OPTIONAL MATCH (ds)-[:CONTAINS]->(p1:Profile)-[d:DISTANCES {id: $}]->(p2:Profile)\n" +
				"WITH d.deprecated as deprecated, collect(d) as ignored\n" +
				"RETURN COALESCE(deprecated = false, false)";
		return query(Boolean.class, new Query(statement, key.getProjectId(), key.getDatasetId(), key.getId()));
	}

	@Override
	protected void store(Inference analysis) {
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})\n" +
				"WHERE d.deprecated = false\n" +
				"WITH d, $ as treeId, $ as algorithm\n" +
				"UNWIND $ as edge\n" +
				"MATCH (d)-[:CONTAINS]->(p1:Profile {id: edge.from})-[r1:CONTAINS_DETAILS]->(:ProfileDetails)\n" +
				"WHERE NOT EXISTS(r1.to)\n" +
				"MATCH (d)-[:CONTAINS]->(p2:Profile {id: edge.to})-[r2:CONTAINS_DETAILS]->(:ProfileDetails)\n" +
				"WHERE NOT EXISTS(r2.to)\n" +
				"CREATE (p1)-[:DISTANCES {id: treeId, deprecated: false, algorithm: algorithm, fromVersion: r1.version, toVersion: r2.version, distance: edge.distance}]->(p2)";
		Inference.PrimaryKey key = analysis.getPrimaryKey();
		Query query = new Query(statement, key.getProjectId(), key.getDatasetId(), key.getId(), analysis.getAlgorithm().getName(),
				analysis.getEdges().stream()
						.map(e -> new Object() {
							public final String from = e.getFrom().getPrimaryKey().getId();
							public final String to = e.getTo().getPrimaryKey().getId();
							public final int distance = e.getWeight();
						})
		);
		execute(query);
	}

	@Override
	protected void delete(Inference.PrimaryKey key) {
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(ds:Dataset {id: $})\n" +
				"MATCH (ds)-[:CONTAINS]->(p1:Profile)-[d:DISTANCES {id: $}]->(p2:Profile)\n" +
				"SET d.deprecated = true\n" +
				"WITH ds, d.id as analysis, collect(d) as ignored\n" +
				"MATCH (ds)-[:CONTAINS]->(p:Profile)-[:HAS]->(c:Coordinate {analysisId: analysis})\n" +
				"WHERE c.deprecate = false\n" +
				"SET c.deprecated = true";
		execute(new Query(statement, key.getProjectId(), key.getDatasetId(), key.getId()));
	}

}
