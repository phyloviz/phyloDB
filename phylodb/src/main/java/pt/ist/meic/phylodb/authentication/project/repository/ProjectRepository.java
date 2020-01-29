package pt.ist.meic.phylodb.authentication.project.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Component;
import pt.ist.meic.phylodb.authentication.project.model.Project;

@Component
public interface ProjectRepository extends Neo4jRepository<Project, Long>, ProjectCypherRepository {

}
