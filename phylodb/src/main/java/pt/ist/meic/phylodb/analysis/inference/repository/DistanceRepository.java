package pt.ist.meic.phylodb.analysis.inference.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import pt.ist.meic.phylodb.analysis.inference.model.Distance;

public interface DistanceRepository extends Neo4jRepository<Distance, Long>, DistanceCypherRepository {
}
