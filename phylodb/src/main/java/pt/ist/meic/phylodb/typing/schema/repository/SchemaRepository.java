package pt.ist.meic.phylodb.typing.schema.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import pt.ist.meic.phylodb.typing.schema.model.Schema;

public interface SchemaRepository  extends Neo4jRepository<Schema, Long>, SchemaCypherRepository {

}
