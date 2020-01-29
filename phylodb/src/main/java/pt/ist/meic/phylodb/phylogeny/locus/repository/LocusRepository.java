package pt.ist.meic.phylodb.phylogeny.locus.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;

public interface LocusRepository extends Neo4jRepository<Locus, Long>, LocusCypherRepository {
}
