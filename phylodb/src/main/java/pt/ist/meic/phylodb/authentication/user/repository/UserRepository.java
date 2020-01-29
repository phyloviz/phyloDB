package pt.ist.meic.phylodb.authentication.user.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Component;
import pt.ist.meic.phylodb.authentication.user.model.User;

@Component
public interface UserRepository extends Neo4jRepository<User, Long>, UserCypherRepository {

}
