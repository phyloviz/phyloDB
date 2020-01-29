package pt.ist.meic.phylodb.analysis.visualization.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import pt.ist.meic.phylodb.analysis.visualization.model.Coordinate;

public interface CoordinateRepository extends Neo4jRepository<Coordinate, Long>, CoordinateCypherRepository {
}
