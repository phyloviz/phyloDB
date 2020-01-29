package pt.ist.meic.phylodb.typing.ancillary.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import pt.ist.meic.phylodb.typing.ancillary.model.Ancillary;

public interface AncillaryRepository extends Neo4jRepository<Ancillary, Long>, AncillaryCypherRepository {
}
