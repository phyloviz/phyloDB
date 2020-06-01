package pt.ist.meic.phylodb.phylogeny.taxon;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.neo4j.ogm.model.Result;
import pt.ist.meic.phylodb.RepositoryTestsContext;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.utils.db.Query;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

public class TaxonRepositoryTests extends RepositoryTestsContext {

	private static final int LIMIT = 2;
	private static final Taxon[] STATE = new Taxon[]{TAXON1, TAXON2};

	private static Stream<Arguments> findAll_params() {
		String id1 = "3test", id3 = "5test";
		Taxon firstE = new Taxon(id1, 1, false, "teste"),
				firstChangedE = new Taxon(id1, 2, false, "teste1"),
				secondE = new Taxon("4test", 1, false, "teste"),
				thirdE = new Taxon(id3, 1, false, "teste2"),
				thirdChangedE = new Taxon(id3, 2, false, "teste"),
				fourthE = new Taxon("6test", 1, false, "teste3");
		VersionedEntity<String> first = new VersionedEntity<>(id1, 1, false),
				firstChanged = new VersionedEntity<>(id1, 2, false),
				second = new VersionedEntity<>("4test", 1, false),
				third = new VersionedEntity<>(id3, 1, false),
				thirdChanged = new VersionedEntity<>(id3, 2, false),
				fourth = new VersionedEntity<>("6test", 1, false),
				state0 = new VersionedEntity<>(STATE[0].getPrimaryKey(), STATE[0].getVersion(), STATE[0].isDeprecated()),
				state1 = new VersionedEntity<>(STATE[1].getPrimaryKey(), STATE[1].getVersion(), STATE[1].isDeprecated());
		return Stream.of(Arguments.of(0, new Taxon[0], Collections.emptyList()),
				Arguments.of(0, new Taxon[]{STATE[0]}, Collections.singletonList(state0)),
				Arguments.of(0, new Taxon[]{firstE, firstChangedE}, Collections.singletonList(firstChanged)),
				Arguments.of(0, new Taxon[]{STATE[0], STATE[1], firstE}, Arrays.asList(state0, state1)),
				Arguments.of(0, new Taxon[]{STATE[0], STATE[1], firstE, firstChangedE},  Arrays.asList(state0, state1)),
				Arguments.of(1, new Taxon[0], Collections.emptyList()),
				Arguments.of(1, new Taxon[]{STATE[0]}, Collections.emptyList()),
				Arguments.of(1, new Taxon[]{firstE, firstChangedE},Collections.emptyList()),
				Arguments.of(1, new Taxon[]{STATE[0], STATE[1], firstE}, Collections.singletonList(first)),
				Arguments.of(1, new Taxon[]{STATE[0], STATE[1], firstE, firstChangedE}, Collections.singletonList(firstChanged)),
				Arguments.of(1, new Taxon[]{STATE[0], STATE[1], firstE, secondE}, Arrays.asList(first, second)),
				Arguments.of(1, new Taxon[]{STATE[0], STATE[1], firstE, firstChangedE, secondE}, Arrays.asList(firstChanged, second)),
				Arguments.of(1, new Taxon[]{STATE[0], STATE[1], firstE, firstChangedE}, Collections.singletonList(firstChanged)),
				Arguments.of(2, new Taxon[0], Collections.emptyList()),
				Arguments.of(2, new Taxon[]{STATE[0]}, Collections.emptyList()),
				Arguments.of(2, new Taxon[]{firstE, firstChangedE}, Collections.emptyList()),
				Arguments.of(2, new Taxon[]{STATE[0], STATE[1], firstE, secondE, thirdE}, Collections.singletonList(third)),
				Arguments.of(2, new Taxon[]{STATE[0], STATE[1], firstE, secondE, thirdE, thirdChangedE}, Collections.singletonList(thirdChanged)),
				Arguments.of(2, new Taxon[]{STATE[0], STATE[1], firstE, secondE, thirdE, fourthE}, Arrays.asList(third, fourth)),
				Arguments.of(2, new Taxon[]{STATE[0], STATE[1], firstE, secondE, thirdE, thirdChangedE, fourthE}, Arrays.asList(thirdChanged, fourth)),
				Arguments.of(-1, new Taxon[0], Collections.emptyList()));
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
		return Stream.of(Arguments.of(first, new Taxon[0], new Taxon[]{STATE[0], STATE[1], first}, true, 2, 1),
				Arguments.of(second, new Taxon[]{first}, new Taxon[]{STATE[0], STATE[1], first, second}, true, 1, 1),
				Arguments.of(null, new Taxon[0], STATE, false, 0, 0));
	}

	private static Stream<Arguments> remove_params() {
		String id = "3three";
		Taxon first = new Taxon(id, 1, false, null),
				second = new Taxon(id, 1, true, null);
		return Stream.of(Arguments.of(id, new Taxon[0], STATE, false),
				Arguments.of(id, new Taxon[]{first}, new Taxon[]{STATE[0], STATE[1], second}, true),
				Arguments.of(null, new Taxon[0], STATE, false));
	}

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

	private boolean hasDescendents(String key) {
		String statement = "MATCH (t:Taxon {id: $})\n" +
				"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus) WHERE l.deprecated = false WITH l\n" +
				"OPTIONAL MATCH (l)-[:CONTAINS]->(a:Allele) WHERE a.deprecated = false WITH l, a\n" +
				"RETURN (COUNT(l) + COUNT(a)) <> 0";
		return query(Boolean.class, new Query(statement, key));
	}

	@ParameterizedTest
	@MethodSource("findAll_params")
	public void findAll(int page, Taxon[] state, List<VersionedEntity<String>> expected) {
		store(state);
		Optional<List<VersionedEntity<String>>> result = taxonRepository.findAllEntities(page, LIMIT);
		if (expected.size() == 0 && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertTrue(result.isPresent());
		List<VersionedEntity<String>> taxons = result.get();
		assertEquals(expected.size(), taxons.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).getPrimaryKey(), taxons.get(i).getPrimaryKey());
			assertEquals(expected.get(i).getVersion(), taxons.get(i).getVersion());
			assertEquals(expected.get(i).isDeprecated(), taxons.get(i).isDeprecated());
		}
	}

	@ParameterizedTest
	@MethodSource("find_params")
	public void find(String key, long version, Taxon[] state, Taxon expected) {
		store(TaxonRepositoryTests.STATE);
		store(state);
		Optional<Taxon> result = taxonRepository.find(key, version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("exists_params")
	public void exists(String key, Taxon[] state, boolean expected) {
		store(TaxonRepositoryTests.STATE);
		store(state);
		boolean result = taxonRepository.exists(key);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("save_params")
	public void save(Taxon user, Taxon[] state, Taxon[] expectedState, boolean executed, int nodesCreated, int relationshipsCreated) {
		store(TaxonRepositoryTests.STATE);
		store(state);
		int nodes = countNodes();
		int relationships = countRelationships();
		boolean result = taxonRepository.save(user);
		if (executed) {
			assertTrue(result);
			assertEquals(nodes + nodesCreated, countNodes());
			assertEquals(relationships + relationshipsCreated, countRelationships());
		} else
			assertFalse(result);
		Taxon[] stateResult = findAll();
		assertArrayEquals(expectedState, stateResult);
	}

	@ParameterizedTest
	@MethodSource("remove_params")
	public void remove(String key, Taxon[] state, Taxon[] expectedState, boolean expectedResult) {
		store(TaxonRepositoryTests.STATE);
		store(state);
		boolean result = taxonRepository.remove(key);
		Taxon[] stateResult = findAll();
		assertEquals(expectedResult, result);
		assertFalse(hasDescendents(key));
		assertArrayEquals(expectedState, stateResult);
	}

}
