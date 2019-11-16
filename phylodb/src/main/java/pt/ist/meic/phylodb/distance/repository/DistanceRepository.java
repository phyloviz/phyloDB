package pt.ist.meic.phylodb.distance.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import pt.ist.meic.phylodb.distance.model.Distance;

public interface DistanceRepository extends Neo4jRepository<Distance, Long>, DistanceCypherRepository {
}
