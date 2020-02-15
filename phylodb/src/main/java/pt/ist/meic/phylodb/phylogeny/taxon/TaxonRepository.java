package pt.ist.meic.phylodb.phylogeny.taxon;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
public class TaxonRepository {

	private Session session;

	public TaxonRepository(Session session) {
		this.session = session;
	}

	public List<Taxon> findAll(int page) {
		int limit = 2;
		if(page < 0 || limit <= 0)
			return null;
		Map<String, Object> params = new HashMap<>();
		params.put("page", page * limit);
		params.put("limit", limit);
		String query = "MATCH (t:Taxon) RETURN t ORDER BY t._id SKIP $page LIMIT $limit;";
		return StreamSupport.stream(session.query(Taxon.class, query, params).spliterator(), false)
				.collect(Collectors.toList());
	}

	public Taxon find(String key) {
		if(key == null)
			return null;
		Map<String, Object> params = new HashMap<>();
		params.put("key", key);
		String query = "MATCH (t:Taxon {_id: $key}) RETURN t;";
		return session.queryForObject(Taxon.class, query, params);
	}

	public void save(Taxon taxon) {
		if(taxon == null)
			return;
		Map<String, Object> params = new HashMap<>();
		params.put("key", taxon.get_id());
		params.put("description", taxon.getDescription());
		String query = "MERGE (t:Taxon {_id: $key}) SET t.description = $description;";
		session.query(query, params);
		session.clear();
	}

	public void remove(String key) {
		if(key == null)
			return;
		Map<String, Object> params = new HashMap<>();
		params.put("key", key);
		String query = "MATCH (t:Taxon {_id: $key}) DELETE t;";
		session.query(query, params);
		session.clear();
	}
}
