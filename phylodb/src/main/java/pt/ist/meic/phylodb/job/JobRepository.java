package pt.ist.meic.phylodb.job;

import org.apache.logging.log4j.util.Strings;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.job.model.Job;
import pt.ist.meic.phylodb.utils.db.Query;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Repository
public class JobRepository extends pt.ist.meic.phylodb.utils.db.Repository<Job, Job.PrimaryKey> {

	private static final String FULLY_QUALIFIED = "algorithms.%s";
	private static final int UUID_LENGTH = UUID.randomUUID().toString().length();

	protected JobRepository(Session session) {
		super(session);
	}

	@Override
	protected Result getAll(int page, int limit, Object... filters) {
		String statement = "CALL apoc.periodic.list() YIELD name, done, cancelled\n" +
				"WHERE name STARTS WITH $\n" +
				"RETURN name as name, done as completed, cancelled as cancelled\n" +
				"ORDER BY size(name), name SKIP $ LIMIT $";
		return query(new Query(statement, filters[0], page, limit));
	}

	@Override
	protected Job parse(Map<String, Object> row) {
		String name = (String) row.get("name");
		UUID project = UUID.fromString(name.substring(0, UUID_LENGTH));
		UUID id = UUID.fromString(name.substring(UUID_LENGTH + 1));
		return new Job(project,
				id,
				(boolean) row.get("completed"),
				(boolean) row.get("cancelled")
		);
	}

	@Override
	protected boolean isPresent(Job.PrimaryKey key) {
		String statement = "CALL apoc.periodic.list() YIELD name, done, cancelled\n" +
				"WHERE name = $\n" +
				"RETURN COALESCE(name is not null, false)";
		return query(Boolean.class, new Query(statement, name(key.getProjectId(), key.getId())));
	}

	@Override
	protected void store(Job job) {
		Job.PrimaryKey key = job.getPrimaryKey();
		String jobName = name(key.getProjectId(), key.getId());
		String params = Strings.join(Arrays.asList(job.getParams()), ',') + "," + job.getAnalysisId();
		String statement = "CALL apoc.periodic.submit(\"$\", \"CALL $.($)\")";
		execute(new Query(statement, jobName, String.format(FULLY_QUALIFIED, job.getAlgorithm()), params));
	}

	@Override
	protected void delete(Job.PrimaryKey key) {
		execute(new Query("CALL apoc.periodic.cancel($)", name(key.getProjectId(), key.getId())));
	}

	private String name(UUID projectId, UUID jobId) {
		return projectId.toString() + "-" + jobId.toString();
	}

}
