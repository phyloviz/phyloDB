package pt.ist.meic.phylodb.phylogeny.taxon;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import pt.ist.meic.phylodb.RepositoryTests;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.utils.db.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

public class TaxonRepositoryTests extends RepositoryTests {

	@Autowired
	private TaxonRepository repository;

	private static final int LIMIT = 2;
	private static final Taxon first = new Taxon("1one", 1, false, "description");
	private static final Taxon second = new Taxon("2two", 1, false, null);
	private static final Taxon[] state = new Taxon[]{first, second};

	private void store(Taxon[] taxons) {
		for (Taxon taxon : taxons) {
			String statement = "MERGE (t:Taxon {id: $}) SET t.deprecated = $ WITH t\n" +
					"OPTIONAL MATCH (t)-[r:CONTAINS_DETAILS]->(td:TaxonDetails)\n" +
					"WHERE NOT EXISTS(r.to) SET r.to = datetime()\n" +
					"WITH t, COALESCE(MAX(r.version), 0) + 1 as v\n" +
					"CREATE (t)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(td:TaxonDetails {description: $}) WITH t\n" +
					"CREATE (p)-[:CONTAINS]->(l:Locus {deprecated: false})-[:CONTAINS]->(:Allele {deprecated: false})";
			execute(new Query(statement, taxon.getPrimaryKey(), taxon.isDeprecated(), taxon.getDescription()));
		}
	}

	private Taxon parse(Map<String, Object> row) {
		return new Taxon((String) row.get("id"),
				(long) row.get("version"),
				(boolean) row.get("deprecated"),
				(String) row.get("description"));
	}

	private Taxon[] findAll() {
		String statement = "MATCH (t:Taxon)-[r:CONTAINS_DETAILS]->(td:TaxonDetails)\n" +
				"RETURN t.id as id, t.deprecated as deprecated, r.version as version,\n" +
				"t.name as name, td.description as description\n" +
				"ORDER BY id, version";
		Result result = query(new Query(statement));
		if (result == null) return new Taxon[0];
		return StreamSupport.stream(result.spliterator(), false)
				.map(this::parse)
				.toArray(Taxon[]::new);
	}


	private boolean hasDescendents(String key){
		String statement = "MATCH (t:Taxon {id: $})\n" +
				"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus) WHERE l.deprecated = false WITH l\n" +
				"OPTIONAL MATCH (l)-[:CONTAINS]->(a:Allele) WHERE a.deprecated = false WITH l, a\n" +
				"RETURN (COUNT(l) + COUNT(a)) <> 0";
		return query(Boolean.class, new Query(statement, key));
	}

	private static Stream<Arguments> findAll_params() {
		String id1 = "3test", id3 = "5test";
		Taxon first = new Taxon(id1, 1, false, "description"),
				firstChanged = new Taxon(id1, 2, false, "description2"),
				second = new Taxon("4test", 1, false, null),
				third = new Taxon(id3 , 1, false, "description3"),
				thirdChanged = new Taxon(id3, 2, false, null),
				fourth = new Taxon("6test", 1, false, null);
		return Stream.of(Arguments.of(0, new Taxon[0], new Taxon[0]),
				Arguments.of(0, new Taxon[]{state[0]}, new Taxon[]{state[0]}),
				Arguments.of(0, new Taxon[]{first, firstChanged}, new Taxon[]{firstChanged}),
				Arguments.of(0, new Taxon[]{state[0], state[1], first}, state),
				Arguments.of(0, new Taxon[]{state[0], state[1], first, firstChanged}, state),
				Arguments.of(1, new Taxon[0], new Taxon[0]),
				Arguments.of(1, new Taxon[]{state[0]}, new Taxon[0]),
				Arguments.of(1, new Taxon[]{first, firstChanged}, new Taxon[0]),
				Arguments.of(1, new Taxon[]{state[0], state[1], first}, new Taxon[]{first}),
				Arguments.of(1, new Taxon[]{state[0], state[1], first, firstChanged}, new Taxon[]{firstChanged}),
				Arguments.of(1, new Taxon[]{state[0], state[1], first, second}, new Taxon[]{first, second}),
				Arguments.of(1, new Taxon[]{state[0], state[1], first, firstChanged, second}, new Taxon[]{firstChanged, second}),
				Arguments.of(1, new Taxon[]{state[0], state[1], first, firstChanged}, new Taxon[]{firstChanged}),
				Arguments.of(2, new Taxon[0], new Taxon[0]),
				Arguments.of(2, new Taxon[]{state[0]}, new Taxon[0]),
				Arguments.of(2, new Taxon[]{first, firstChanged}, new Taxon[0]),
				Arguments.of(2, new Taxon[]{state[0], state[1], first, second, third}, new Taxon[]{third}),
				Arguments.of(2, new Taxon[]{state[0], state[1], first, second, third, thirdChanged}, new Taxon[]{thirdChanged}),
				Arguments.of(2, new Taxon[]{state[0], state[1], first, second, third, fourth}, new Taxon[]{third, fourth}),
				Arguments.of(2, new Taxon[]{state[0], state[1], first, second, third, thirdChanged, fourth}, new Taxon[]{thirdChanged, fourth}),
				Arguments.of(-1, new Taxon[0], new Taxon[0]));
	}

	private static Stream<Arguments> find_params() {
		String key = "test";
		Taxon first = new Taxon(key, 1, false, null),
				second = new Taxon(key, 2, false, "description");
		return Stream.of(Arguments.of(key, 1, new Taxon[0], null),
				Arguments.of(key, 1, new Taxon[]{first}, first),
				Arguments.of(key, 2, new Taxon[]{first, second}, second),
				Arguments.of(key, -3, new Taxon[0], null),
				Arguments.of(key, 10, new Taxon[]{first}, null),
				Arguments.of(key, -10, new Taxon[]{first, second}, null),
				Arguments.of(null, 1, new Taxon[0], null));
	}

	private static Stream<Arguments> exists_params() {
		String key = "test";
		Taxon first = new Taxon(key, 1, false, null),
				second = new Taxon(key, 1, true, "description");
		return Stream.of(Arguments.of(key, new Taxon[0], false),
				Arguments.of(key, new Taxon[]{first}, true),
				Arguments.of(key, new Taxon[]{second}, false),
				Arguments.of(null, new Taxon[0], false));
	}

	private static Stream<Arguments> save_params() {
		String id = "3three";
		Taxon first = new Taxon(id, 1, false, null),
				second = new Taxon(id, 2, false, "description");
		return Stream.of(Arguments.of(first, new Taxon[0], new Taxon[]{state[0], state[1], first}, true),
				Arguments.of(second, new Taxon[]{first}, new Taxon[]{state[0], state[1], first, second}, true),
				Arguments.of(null, new Taxon[0], state, false));
	}

	private static Stream<Arguments> remove_params() {
		String id = "3three";
		Taxon first = new Taxon(id, 1, false, null),
				second = new Taxon(id, 1, true, null);
		return Stream.of(Arguments.of(id, new Taxon[0], state, false),
				Arguments.of(id, new Taxon[]{first}, new Taxon[]{state[0], state[1], second}, true),
				Arguments.of(null, new Taxon[0], state, false));
	}

	@ParameterizedTest
	@MethodSource("findAll_params")
	public void findAll(int page, Taxon[] state, Taxon[] expected) {
		store(state);
		Optional<List<Taxon>> result = repository.findAll(page, LIMIT);
		if (expected.length == 0 && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertTrue(result.isPresent());
		List<Taxon> users = result.get();
		assertEquals(expected.length,  users.size());
		assertArrayEquals(expected, users.toArray());
	}

	@ParameterizedTest
	@MethodSource("find_params")
	public void find(String key, long version, Taxon[] state, Taxon expected) {
		store(TaxonRepositoryTests.state);
		store(state);
		Optional<Taxon> result = repository.find(key, version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("exists_params")
	public void exists(String key, Taxon[] state, boolean expected) {
		store(TaxonRepositoryTests.state);
		store(state);
		boolean result = repository.exists(key);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("save_params")
	public void save(Taxon user, Taxon[] state, Taxon[] expectedState, boolean expectedResult) {
		store(TaxonRepositoryTests.state);
		store(state);
		boolean result = repository.save(user);
		Taxon[] stateResult = findAll();
		assertEquals(expectedResult, result);
		assertArrayEquals(expectedState, stateResult);
	}

	@ParameterizedTest
	@MethodSource("remove_params")
	public void remove(String key, Taxon[] state, Taxon[] expectedState, boolean expectedResult) {
		store(TaxonRepositoryTests.state);
		store(state);
		boolean result = repository.remove(key);
		Taxon[] stateResult = findAll();
		assertEquals(expectedResult, result);
		assertFalse(hasDescendents(key));
		assertArrayEquals(expectedState, stateResult);
	}

}
