package pt.ist.meic.phylodb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

/**
 * Application entry point
 */
@SpringBootApplication
@EnableNeo4jRepositories
public class PhylodbApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhylodbApplication.class, args);
	}

}
