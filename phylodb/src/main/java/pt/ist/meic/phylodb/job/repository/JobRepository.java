package pt.ist.meic.phylodb.job.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import pt.ist.meic.phylodb.job.model.Job;

public interface JobRepository extends Neo4jRepository<Job, Long>, JobCypherRepository {

}
