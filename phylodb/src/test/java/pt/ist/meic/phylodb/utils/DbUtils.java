package pt.ist.meic.phylodb.utils;

import org.neo4j.ogm.session.Session;

import java.util.Collections;

public class DbUtils {

	public static void clearContext(Session session) {
		clearProfiles(session);
		session.query("call apoc.periodic.iterate('MATCH (n) return n', 'DETACH DELETE n', {batchSize:1000})", Collections.emptyMap());
		session.clear();
	}

	public static void clearProfiles(Session session) {
		session.query("call apoc.periodic.iterate('MATCH (n:Profile) return n', 'DETACH DELETE n', {batchSize:1000})", Collections.emptyMap());
		session.query("call apoc.periodic.iterate('MATCH (n:ProfileDetails) return n', 'DETACH DELETE n', {batchSize:1000})", Collections.emptyMap());
		session.query("call apoc.periodic.iterate('MATCH (n:Coordinate) return n', 'DETACH DELETE n', {batchSize:1000})", Collections.emptyMap());
		session.clear();
	}

	public static void goeBURST(Session session, String projectId, String datasetId, String inferenceId) {
		session.query(String.format("call algorithms.inference.goeburst('%s', '%s', 3, '%s')", projectId, datasetId, inferenceId), Collections.emptyMap());
		session.clear();
	}

	public static void radial(Session session, String projectId, String datasetId, String inferenceId, String visualizationId) {
		session.query(String.format("call algorithms.visualization.radial('%s', '%s', '%s', '%s')", projectId, datasetId, inferenceId, visualizationId), Collections.emptyMap());
		session.clear();
	}

}
