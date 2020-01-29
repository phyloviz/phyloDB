package pt.ist.meic.phylodb.typing.schema.repository;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class SchemaRepositoryImpl implements SchemaCypherRepository{

	private Session session;

	public SchemaRepositoryImpl(Session session) {
		this.session = session;
	}

}
