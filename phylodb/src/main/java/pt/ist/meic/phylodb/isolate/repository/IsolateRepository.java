package pt.ist.meic.phylodb.isolate.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import pt.ist.meic.phylodb.isolate.model.Isolate;

public interface IsolateRepository extends Neo4jRepository<Isolate, Long>, IsolateCypherRepository {
}
