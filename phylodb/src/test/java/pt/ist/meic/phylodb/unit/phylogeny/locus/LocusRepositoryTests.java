package pt.ist.meic.phylodb.unit.phylogeny.locus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.neo4j.ogm.model.Result;
import pt.ist.meic.phylodb.unit.RepositoryTestsContext;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.utils.db.VersionedRepository;
import pt.ist.meic.phylodb.utils.db.Query;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

public class LocusRepositoryTests extends RepositoryTestsContext {

	private static final int LIMIT = 2;
	private static final Locus[] STATE = new Locus[]{LOCUS1, LOCUS2};

	private static Stream<Arguments> findAll_params() {
		String id1 = "3test", id3 = "5test";
		String taxonKey = TAXON1.getPrimaryKey();
		Locus firstE = new Locus(taxonKey, id1, 1, false, "description"),
				firstChangedE = new Locus(taxonKey, id1, 2, false, "description2"),
				secondE = new Locus(taxonKey, "4test", 1, false, null),
				thirdE = new Locus(taxonKey, id3, 1, false, "description3"),
				thirdChangedE = new Locus(taxonKey, id3, 2, false, null),
				fourthE = new Locus(taxonKey, "6test", 1, false, null);
		VersionedEntity<Locus.PrimaryKey> first = new VersionedEntity<>(new Locus.PrimaryKey(taxonKey, id1), 1, false),
				firstChanged = new VersionedEntity<>(new Locus.PrimaryKey(taxonKey, id1), 2, false),
				second = new VersionedEntity<>(new Locus.PrimaryKey(taxonKey, "4test"), 1, false),
				third = new VersionedEntity<>(new Locus.PrimaryKey(taxonKey, id3), 1, false),
				thirdChanged = new VersionedEntity<>(new Locus.PrimaryKey(taxonKey, id3), 2, false),
				fourth = new VersionedEntity<>(new Locus.PrimaryKey(taxonKey, "6test"), 1, false),
				state0 = new VersionedEntity<>(STATE[0].getPrimaryKey(), STATE[0].getVersion(), STATE[0].isDeprecated()),
				state1 = new VersionedEntity<>(STATE[1].getPrimaryKey(), STATE[1].getVersion(), STATE[1].isDeprecated());
		return Stream.of(Arguments.of(0, new Locus[0], Collections.emptyList()),
				Arguments.of(0, new Locus[]{STATE[0]}, Collections.singletonList(state0)),
				Arguments.of(0, new Locus[]{firstE, firstChangedE}, Collections.singletonList(firstChanged)),
				Arguments.of(0, new Locus[]{STATE[0], STATE[1], firstE}, Arrays.asList(state0, state1)),
				Arguments.of(0, new Locus[]{STATE[0], STATE[1], firstE, firstChangedE}, Arrays.asList(state0, state1)),
				Arguments.of(1, new Locus[0], Collections.emptyList()),
				Arguments.of(1, new Locus[]{STATE[0]}, Collections.emptyList()),
				Arguments.of(1, new Locus[]{firstE, firstChangedE}, Collections.emptyList()),
				Arguments.of(1, new Locus[]{STATE[0], STATE[1], firstE}, Collections.singletonList(first)),
				Arguments.of(1, new Locus[]{STATE[0], STATE[1], firstE, firstChangedE}, Collections.singletonList(firstChanged)),
				Arguments.of(1, new Locus[]{STATE[0], STATE[1], firstE, secondE}, Arrays.asList(first, second)),
				Arguments.of(1, new Locus[]{STATE[0], STATE[1], firstE, firstChangedE, secondE}, Arrays.asList(firstChanged, second)),
				Arguments.of(1, new Locus[]{STATE[0], STATE[1], firstE, firstChangedE}, Collections.singletonList(firstChanged)),
				Arguments.of(2, new Locus[0], Collections.emptyList()),
				Arguments.of(2, new Locus[]{STATE[0]}, Collections.emptyList()),
				Arguments.of(2, new Locus[]{firstE, firstChangedE}, Collections.emptyList()),
				Arguments.of(2, new Locus[]{STATE[0], STATE[1], firstE, secondE, thirdE}, Collections.singletonList(third)),
				Arguments.of(2, new Locus[]{STATE[0], STATE[1], firstE, secondE, thirdE, thirdChangedE}, Collections.singletonList(thirdChanged)),
				Arguments.of(2, new Locus[]{STATE[0], STATE[1], firstE, secondE, thirdE, fourthE}, Arrays.asList(third, fourth)),
				Arguments.of(2, new Locus[]{STATE[0], STATE[1], firstE, secondE, thirdE, thirdChangedE, fourthE}, Arrays.asList(thirdChanged, fourth)),
				Arguments.of(-1, new Locus[0], Collections.emptyList()));
	}

	private static Stream<Arguments> find_params() {
		Locus.PrimaryKey key = new Locus.PrimaryKey(TAXON1.getPrimaryKey(), "test");
		Locus first = new Locus(key.getTaxonId(), key.getId(), 1, false, null),
				second = new Locus(key.getTaxonId(), key.getId(), 2, false, "description");
		return Stream.of(Arguments.of(key, 1, new Locus[0], null),
				Arguments.of(key, 1, new Locus[]{first}, first),
				Arguments.of(key, 2, new Locus[]{first, second}, second),
				Arguments.of(key, -3, new Locus[0], null),
				Arguments.of(key, 10, new Locus[]{first}, null),
				Arguments.of(key, -10, new Locus[]{first, second}, null),
				Arguments.of(null, 1, new Locus[0], null));
	}

	private static Stream<Arguments> exists_params() {
		Locus.PrimaryKey key = new Locus.PrimaryKey(TAXON1.getPrimaryKey(), "test");
		Locus first = new Locus(key.getTaxonId(), key.getId(), 1, false, null),
				second = new Locus(key.getTaxonId(), key.getId(), 1, true, "description");
		return Stream.of(Arguments.of(key, new Locus[0], false),
				Arguments.of(key, new Locus[]{first}, true),
				Arguments.of(key, new Locus[]{second}, false),
				Arguments.of(null, new Locus[0], false));
	}

	private static Stream<Arguments> save_params() {
		Locus.PrimaryKey key = new Locus.PrimaryKey(TAXON1.getPrimaryKey(), "3three");
		Locus first = new Locus(key.getTaxonId(), key.getId(), 1, false, null),
				second = new Locus(key.getTaxonId(), key.getId(), 2, false, "description");
		return Stream.of(Arguments.of(first, new Locus[0], new Locus[]{STATE[0], STATE[1], first}, true, 2, 2),
				Arguments.of(second, new Locus[]{first}, new Locus[]{STATE[0], STATE[1], first, second}, true, 1, 1),
				Arguments.of(null, new Locus[0], STATE, false, 0, 0));
	}

	private static Stream<Arguments> remove_params() {
		Locus.PrimaryKey key = new Locus.PrimaryKey(TAXON1.getPrimaryKey(), "3three");
		Locus first = new Locus(key.getTaxonId(), key.getId(), 1, false, null),
				second = new Locus(key.getTaxonId(), key.getId(), 1, true, null);
		return Stream.of(Arguments.of(key, new Locus[0], STATE, false),
				Arguments.of(key, new Locus[]{first}, new Locus[]{STATE[0], STATE[1], second}, true),
				Arguments.of(null, new Locus[0], STATE, false));
	}

	private static Stream<Arguments> anyMissing_params() {
		List<VersionedEntity<Locus.PrimaryKey>> references1 = new ArrayList<>(), references2 = new ArrayList<>(), references3 = new ArrayList<>(), references4 = new ArrayList<>();
		VersionedEntity<Locus.PrimaryKey> reference1 = new VersionedEntity<>(new Locus.PrimaryKey(TAXON1.getPrimaryKey(), STATE[0].getPrimaryKey().getId()), VersionedRepository.CURRENT_VERSION_VALUE, false),
				reference2 = new VersionedEntity<>(new Locus.PrimaryKey(TAXON1.getPrimaryKey(), STATE[1].getPrimaryKey().getId()), VersionedRepository.CURRENT_VERSION_VALUE, false),
				notReference = new VersionedEntity<>(new Locus.PrimaryKey("not", "not"), VersionedRepository.CURRENT_VERSION_VALUE, false);
		references1.add(reference1);
		references2.add(reference1);
		references2.add(reference2);
		references3.add(reference1);
		references3.add(notReference);
		references4.add(notReference);
		return Stream.of(Arguments.of(references1, false),
				Arguments.of(references2, false),
				Arguments.of(references3, true),
				Arguments.of(references4, true));
	}

	private void store(Locus[] loci) {
		for (Locus locus : loci) {
			String statement = "MATCH (t:Taxon {id: $})\n" +
					"WHERE t.deprecated = false\n" +
					"MERGE (t)-[:CONTAINS]->(l:Locus {id: $}) SET l.deprecated = $ WITH l\n" +
					"OPTIONAL MATCH (l)-[r:CONTAINS_DETAILS]->(ld:LocusDetails)\n" +
					"WHERE NOT EXISTS(r.to) SET r.to = datetime()\n" +
					"WITH l, COALESCE(MAX(r.version), 0) + 1 as v\n" +
					"CREATE (l)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(ld:LocusDetails {description: $}) WITH l\n" +
					"CREATE (l)-[:CONTAINS]->(:Allele {deprecated: false})";
			execute(new Query(statement, locus.getPrimaryKey().getTaxonId(), locus.getPrimaryKey().getId(), locus.isDeprecated(), locus.getDescription()));
		}
	}

	private Locus parse(Map<String, Object> row) {
		return new Locus((String) row.get("taxonId"),
				(String) row.get("id"),
				(long) row.get("version"),
				(boolean) row.get("deprecated"),
				(String) row.get("description"));
	}

	private Locus[] findAll() {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus)-[r:CONTAINS_DETAILS]->(ld:LocusDetails)\n" +
				"RETURN t.id as taxonId, l.id as id, l.deprecated as deprecated, r.version as version,\n" +
				"l.name as name, ld.description as description\n" +
				"ORDER BY t.id, l.id, version";
		Result result = query(new Query(statement, TAXON1.getPrimaryKey()));
		if (result == null) return new Locus[0];
		return StreamSupport.stream(result.spliterator(), false)
				.map(this::parse)
				.toArray(Locus[]::new);
	}

	private boolean hasDescendents(Locus.PrimaryKey key) {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $}) WITH l\n" +
				"OPTIONAL MATCH (l)-[:CONTAINS]->(a:Allele) WHERE a.deprecated = false WITH a\n" +
				"RETURN COUNT(a) <> 0";
		return query(Boolean.class, new Query(statement, key.getTaxonId(), key.getId()));
	}

	@BeforeEach
	public void init() {
		taxonRepository.save(TAXON1);
	}

	@ParameterizedTest
	@MethodSource("findAll_params")
	public void findAll(int page, Locus[] state, List<VersionedEntity<Locus.PrimaryKey>> expected) {
		store(state);
		Optional<List<VersionedEntity<Locus.PrimaryKey>>> result = locusRepository.findAllEntities(page, LIMIT, TAXON1.getPrimaryKey());
		if (expected.size() == 0 && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertTrue(result.isPresent());
		List<VersionedEntity<Locus.PrimaryKey>> loci = result.get();
		assertEquals(expected.size(), loci.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).getPrimaryKey(), loci.get(i).getPrimaryKey());
			assertEquals(expected.get(i).getVersion(), loci.get(i).getVersion());
			assertEquals(expected.get(i).isDeprecated(), loci.get(i).isDeprecated());
		}
	}

	@ParameterizedTest
	@MethodSource("find_params")
	public void find(Locus.PrimaryKey key, long version, Locus[] state, Locus expected) {
		store(LocusRepositoryTests.STATE);
		store(state);
		Optional<Locus> result = locusRepository.find(key, version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("exists_params")
	public void exists(Locus.PrimaryKey key, Locus[] state, boolean expected) {
		store(LocusRepositoryTests.STATE);
		store(state);
		boolean result = locusRepository.exists(key);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("save_params")
	public void save(Locus user, Locus[] state, Locus[] expectedState, boolean executed, int nodesCreated, int relationshipsCreated) {
		store(LocusRepositoryTests.STATE);
		store(state);
		int nodes = countNodes();
		int relationships = countRelationships();
		boolean result = locusRepository.save(user);
		if (executed) {
			assertTrue(result);
			assertEquals(nodes + nodesCreated, countNodes());
			assertEquals(relationships + relationshipsCreated, countRelationships());
		} else
			assertFalse(result);
		Locus[] stateResult = findAll();
		assertArrayEquals(expectedState, stateResult);
	}

	@ParameterizedTest
	@MethodSource("remove_params")
	public void remove(Locus.PrimaryKey key, Locus[] state, Locus[] expectedState, boolean expectedResult) {
		store(LocusRepositoryTests.STATE);
		store(state);
		boolean result = locusRepository.remove(key);
		Locus[] stateResult = findAll();
		assertEquals(expectedResult, result);
		if (key != null)
			assertFalse(hasDescendents(key));
		assertArrayEquals(expectedState, stateResult);
	}

	@ParameterizedTest
	@MethodSource("anyMissing_params")
	public void anyMissing(List<VersionedEntity<Locus.PrimaryKey>> references, boolean expected) {
		store(LocusRepositoryTests.STATE);
		boolean result = locusRepository.anyMissing(references);
		assertEquals(expected, result);
	}

}
