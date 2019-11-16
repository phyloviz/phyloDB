package pt.ist.meic.phylodb.profile.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Component;
import pt.ist.meic.phylodb.profile.model.Profile;

@Component
public interface ProfileRepository extends Neo4jRepository<Profile, Long>, ProfileCypherRepository {

}
