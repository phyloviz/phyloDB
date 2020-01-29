package pt.ist.meic.phylodb.phylogeny.allele.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;

public interface AlleleRepository extends Neo4jRepository<Allele, Long>, AlleleCypherRepository {
}
