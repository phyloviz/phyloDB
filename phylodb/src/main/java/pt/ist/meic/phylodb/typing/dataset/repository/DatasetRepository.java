package pt.ist.meic.phylodb.typing.dataset.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Component;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;

@Component
public interface DatasetRepository extends Neo4jRepository<Dataset, Long>, DatasetCypherRepository {

}
