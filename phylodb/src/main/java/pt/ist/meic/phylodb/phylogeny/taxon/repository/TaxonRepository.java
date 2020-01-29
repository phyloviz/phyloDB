package pt.ist.meic.phylodb.phylogeny.taxon.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Component;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;

@Component
public interface TaxonRepository extends Neo4jRepository<Taxon, Long>, TaxonCypherRepository {

}
