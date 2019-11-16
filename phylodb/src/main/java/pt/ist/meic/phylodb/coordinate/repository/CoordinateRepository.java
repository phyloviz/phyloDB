package pt.ist.meic.phylodb.coordinate.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import pt.ist.meic.phylodb.coordinate.model.Coordinate;

public interface CoordinateRepository extends Neo4jRepository<Coordinate, Long>, CoordinateCypherRepository {
}
