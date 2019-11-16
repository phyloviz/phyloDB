package pt.ist.meic.phylodb.ancillary.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import pt.ist.meic.phylodb.ancillary.model.Ancillary;

public interface AncillaryRepository extends Neo4jRepository<Ancillary, Long>, AncillaryCypherRepository {
}
