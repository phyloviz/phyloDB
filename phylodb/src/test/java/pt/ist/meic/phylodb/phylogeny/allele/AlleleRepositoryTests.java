package pt.ist.meic.phylodb.phylogeny.allele;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.neo4j.ogm.model.QueryStatistics;
import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import pt.ist.meic.phylodb.RepositoryTests;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.locus.LocusRepository;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.phylogeny.taxon.TaxonRepository;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.security.authentication.user.model.User;
import pt.ist.meic.phylodb.security.authorization.project.ProjectRepository;
import pt.ist.meic.phylodb.security.authorization.project.model.Project;
import pt.ist.meic.phylodb.utils.db.BatchRepository;
import pt.ist.meic.phylodb.utils.db.Query;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

public class AlleleRepositoryTests extends RepositoryTests {

	@Autowired
	private TaxonRepository taxonRepository;
	@Autowired
	private LocusRepository locusRepository;
	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private AlleleRepository alleleRepository;

	private static final int LIMIT = 2;
	private static final UUID projectId = UUID.randomUUID();
	private static final Taxon taxon = new Taxon("t", 1, false,null);
	private static final Locus locus = new Locus(taxon.getPrimaryKey(), "1one", 1, false, "description");
	private static final Project project = new Project(projectId, 1, false,"name", "private", "", new User.PrimaryKey[0]);
	private static final Allele first = new Allele(taxon.getPrimaryKey(), locus.getPrimaryKey().getId(), "1", 1, false, "description", null);
	private static final Allele second = new Allele(taxon.getPrimaryKey(), locus.getPrimaryKey().getId(), "2", 1, false,null, null);
	private static final Allele firstProject = new Allele(taxon.getPrimaryKey(), locus.getPrimaryKey().getId(), "1", 1, false,null, projectId);
	private static final Allele secondProject = new Allele(taxon.getPrimaryKey(), locus.getPrimaryKey().getId(), "2", 1, false,null, projectId);
	private static final Allele[] state = new Allele[]{first, second, firstProject, secondProject};

	private void store(Allele[] alleles) {
		for (Allele allele : alleles) {
			Object[] params = new Object[]{allele.getTaxonId(), allele.getLocusId()};
			String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})\n" +
					"WHERE t.deprecated = false AND l.deprecated = false\n";
			String project = "";
			if (allele.getPrimaryKey().getProject() != null) {
				params = new Object[]{allele.getTaxonId(), allele.getLocusId(), allele.getPrimaryKey().getProject()};
				statement += "MATCH(p:Project {id: $}) WHERE p.deprecated = false WITH t, l, p\n";
				project = "<-[:CONTAINS]-(p)";
			}
			Query query = new Query(statement, params);
			String statement2 = "MERGE (l)-[:CONTAINS]->(a:Allele {id: $})" + project + " SET a.deprecated = $ WITH l, a\n" +
					"OPTIONAL MATCH (a)-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)\n" +
					"WHERE NOT EXISTS(r.to) SET r.to = datetime()\n" +
					"WITH l, a, COALESCE(MAX(r.version), 0) + 1 as v\n" +
					"CREATE (a)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(ad:AlleleDetails {sequence: $}) ";
			query.appendQuery(statement2).addParameter(allele.getPrimaryKey().getId(), allele.isDeprecated(), allele.getSequence());
			execute(query);
		}
	}

	private Allele parse(Map<String, Object> row) {
		UUID project = row.get("project") != null ? UUID.fromString((String) row.get("project")) : null;
		return new Allele((String) row.get("taxonId"),
				(String) row.get("locusId"),
				(String) row.get("id"),
				(long) row.get("version"),
				(boolean) row.get("deprecated"),
				(String) row.get("sequence"),
				project);
	}

	private Allele[] findAll(UUID projectId) {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})-[:CONTAINS]->(a:Allele)-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)\n";
		List<Object> params = new ArrayList<>();
		params.add(taxon.getPrimaryKey());
		params.add(locus.getPrimaryKey().getId());
		if(projectId != null) {
			params.add(projectId);
			statement += "\nMATCH (a)<-[:CONTAINS]-(p:Project {id: $}) WHERE p.deprecated = false\n" +
					"RETURN t.id as taxonId, l.id as locusId, a.id as id, a.deprecated as deprecated, r.version as version, ad.sequence as sequence, p.id as project\n";
		} else {
			statement += "WHERE NOT (a)<-[:CONTAINS]-(:Project)\n" +
					"\nRETURN t.id as taxonId, l.id as locusId, a.id as id, a.deprecated as deprecated, r.version as version, ad.sequence as sequence\n";
		}
		statement += "ORDER BY t.id, l.id, a.id, version";
		Result result = query(new Query(statement, params.toArray()));
		if (result == null) return new Allele[0];
		return StreamSupport.stream(result.spliterator(), false)
				.map(this::parse)
				.toArray(Allele[]::new);
	}

	private static Stream<Arguments> findAllNoProject_params() {
		String id1 = "3test", id3 = "5test";
		String taxonKey = taxon.getPrimaryKey();
		String locusKey = locus.getPrimaryKey().getId();
		Allele first = new Allele(taxonKey, locusKey, id1, 1, false, "description", null),
				firstChanged = new Allele(taxonKey, locusKey, id1, 2, false, "description2", null),
				second = new Allele(taxonKey, locusKey, "4test", 1, false, null, null),
				third = new Allele(taxonKey, locusKey, id3 , 1, false, "description3", null),
				thirdChanged = new Allele(taxonKey, locusKey, id3, 2, false, null, null),
				fourth = new Allele(taxonKey, locusKey, "6test", 1, false, null, null);
		return Stream.of(Arguments.of(0, new Allele[0], new Allele[0]),
				Arguments.of(0, new Allele[]{state[0]}, new Allele[]{state[0]}),
				Arguments.of(0, new Allele[]{first, firstChanged}, new Allele[]{firstChanged}),
				Arguments.of(0, new Allele[]{state[0], state[1], first}, new Allele[]{state[0], state[1]}),
				Arguments.of(0, new Allele[]{state[0], state[1], first, firstChanged}, new Allele[]{state[0], state[1]}),
				Arguments.of(1, new Allele[0], new Allele[0]),
				Arguments.of(1, new Allele[]{state[0]}, new Allele[0]),
				Arguments.of(1, new Allele[]{first, firstChanged}, new Allele[0]),
				Arguments.of(1, new Allele[]{state[0], state[1], first}, new Allele[]{first}),
				Arguments.of(1, new Allele[]{state[0], state[1], first, firstChanged}, new Allele[]{firstChanged}),
				Arguments.of(1, new Allele[]{state[0], state[1], first, second}, new Allele[]{first, second}),
				Arguments.of(1, new Allele[]{state[0], state[1], first, firstChanged, second}, new Allele[]{firstChanged, second}),
				Arguments.of(1, new Allele[]{state[0], state[1], first, firstChanged}, new Allele[]{firstChanged}),
				Arguments.of(2, new Allele[0], new Allele[0]),
				Arguments.of(2, new Allele[]{state[0]}, new Allele[0]),
				Arguments.of(2, new Allele[]{first, firstChanged}, new Allele[0]),
				Arguments.of(2, new Allele[]{state[0], state[1], first, second, third}, new Allele[]{third}),
				Arguments.of(2, new Allele[]{state[0], state[1], first, second, third, thirdChanged}, new Allele[]{thirdChanged}),
				Arguments.of(2, new Allele[]{state[0], state[1], first, second, third, fourth}, new Allele[]{third, fourth}),
				Arguments.of(2, new Allele[]{state[0], state[1], first, second, third, thirdChanged, fourth}, new Allele[]{thirdChanged, fourth}),
				Arguments.of(-1, new Allele[0], new Allele[0]));
	}

	private static Stream<Arguments> findAllProject_params() {
		String id1 = "3test", id3 = "5test";
		String taxonKey = taxon.getPrimaryKey();
		String locusKey = locus.getPrimaryKey().getId();
		Allele first = new Allele(taxonKey, locusKey, id1, 1, false, "description", projectId),
				firstChanged = new Allele(taxonKey, locusKey, id1, 2, false, "description2", projectId),
				second = new Allele(taxonKey, locusKey, "4test", 1, false, null, projectId),
				third = new Allele(taxonKey, locusKey, id3 , 1, false, "description3", projectId),
				thirdChanged = new Allele(taxonKey, locusKey, id3, 2, false, null, projectId),
				fourth = new Allele(taxonKey, locusKey, "6test", 1, false, null, projectId);
		return Stream.of(Arguments.of(0, new Allele[0], new Allele[0]),
				Arguments.of(0, new Allele[]{state[2]}, new Allele[]{state[2]}),
				Arguments.of(0, new Allele[]{first, firstChanged}, new Allele[]{firstChanged}),
				Arguments.of(0, new Allele[]{state[2], state[3], first}, new Allele[]{state[2], state[3]}),
				Arguments.of(0, new Allele[]{state[2], state[3], first, firstChanged}, new Allele[]{state[2], state[3]}),
				Arguments.of(1, new Allele[0], new Allele[0]),
				Arguments.of(1, new Allele[]{state[2]}, new Allele[0]),
				Arguments.of(1, new Allele[]{first, firstChanged}, new Allele[0]),
				Arguments.of(1, new Allele[]{state[2], state[3], first}, new Allele[]{first}),
				Arguments.of(1, new Allele[]{state[2], state[3], first, firstChanged}, new Allele[]{firstChanged}),
				Arguments.of(1, new Allele[]{state[2], state[3], first, second}, new Allele[]{first, second}),
				Arguments.of(1, new Allele[]{state[2], state[3], first, firstChanged, second}, new Allele[]{firstChanged, second}),
				Arguments.of(1, new Allele[]{state[2], state[3], first, firstChanged}, new Allele[]{firstChanged}),
				Arguments.of(2, new Allele[0], new Allele[0]),
				Arguments.of(2, new Allele[]{state[2]}, new Allele[0]),
				Arguments.of(2, new Allele[]{first, firstChanged}, new Allele[0]),
				Arguments.of(2, new Allele[]{state[2], state[3], first, second, third}, new Allele[]{third}),
				Arguments.of(2, new Allele[]{state[2], state[3], first, second, third, thirdChanged}, new Allele[]{thirdChanged}),
				Arguments.of(2, new Allele[]{state[2], state[3], first, second, third, fourth}, new Allele[]{third, fourth}),
				Arguments.of(2, new Allele[]{state[2], state[3], first, second, third, thirdChanged, fourth}, new Allele[]{thirdChanged, fourth}),
				Arguments.of(-1, new Allele[0], new Allele[0]));
	}

	private static Stream<Arguments> find_params() {
		Allele.PrimaryKey key = new Allele.PrimaryKey(taxon.getPrimaryKey(), locus.getPrimaryKey().getId(), "test"),
				keyP = new Allele.PrimaryKey(taxon.getPrimaryKey(), locus.getPrimaryKey().getId(), "test", projectId);
		Allele first = new Allele(key.getTaxonId(), key.getLocusId(), key.getId(), 1, false, null, key.getProject()),
				second = new Allele(key.getTaxonId(), key.getLocusId(), key.getId(), 2, false, null, key.getProject()),
				firstP = new Allele(keyP.getTaxonId(), keyP.getLocusId(), keyP.getId(), 1, false, null, keyP.getProject()),
				secondP = new Allele(keyP.getTaxonId(), keyP.getLocusId(), keyP.getId(), 2, false, null, keyP.getProject());
		return Stream.of(Arguments.of(key, 1, new Allele[0], null),
				Arguments.of(key, 1, new Allele[]{first}, first),
				Arguments.of(key, 2, new Allele[]{first, second}, second),
				Arguments.of(key, -3, new Allele[0], null),
				Arguments.of(key, 10, new Allele[]{first}, null),
				Arguments.of(key, -10, new Allele[]{first, second}, null),
				Arguments.of(keyP, 1, new Allele[0], null),
				Arguments.of(keyP, 1, new Allele[]{firstP}, firstP),
				Arguments.of(keyP, 2, new Allele[]{firstP, secondP}, secondP),
				Arguments.of(keyP, -3, new Allele[0], null),
				Arguments.of(keyP, 10, new Allele[]{firstP}, null),
				Arguments.of(keyP, -10, new Allele[]{firstP, secondP}, null),
				Arguments.of(null, 1, new Allele[0], null));
	}

	private static Stream<Arguments> exists_params() {
		Allele.PrimaryKey key = new Allele.PrimaryKey(taxon.getPrimaryKey(), locus.getPrimaryKey().getId(), "test"),
				keyP = new Allele.PrimaryKey(taxon.getPrimaryKey(), locus.getPrimaryKey().getId(), "test", projectId);
		Allele first = new Allele(key.getTaxonId(), key.getLocusId(), key.getId(), 1, false, null, key.getProject()),
				second = new Allele(key.getTaxonId(), key.getLocusId(), key.getId(), 1, true, null, key.getProject()),
				firstP = new Allele(keyP.getTaxonId(), keyP.getLocusId(), keyP.getId(), 1, false, null, keyP.getProject()),
				secondP = new Allele(keyP.getTaxonId(), keyP.getLocusId(), keyP.getId(), 1, true, null, keyP.getProject());
		return Stream.of(Arguments.of(key, new Allele[0], false),
				Arguments.of(key, new Allele[]{first}, true),
				Arguments.of(key, new Allele[]{second}, false),
				Arguments.of(keyP, new Allele[0], false),
				Arguments.of(keyP, new Allele[]{firstP}, true),
				Arguments.of(keyP, new Allele[]{secondP}, false),
				Arguments.of(null, new Allele[0], false));
	}

	private static Stream<Arguments> save_params() {
		Allele.PrimaryKey key = new Allele.PrimaryKey(taxon.getPrimaryKey(), locus.getPrimaryKey().getId(), "3"),
				keyP = new Allele.PrimaryKey(taxon.getPrimaryKey(), locus.getPrimaryKey().getId(), "3", projectId);
		Allele first = new Allele(key.getTaxonId(), key.getLocusId(), key.getId(), 1, false, null, key.getProject()),
				second = new Allele(key.getTaxonId(), key.getLocusId(), key.getId(), 2, false, null, key.getProject()),
				firstP = new Allele(keyP.getTaxonId(), keyP.getLocusId(), keyP.getId(), 1, false, null, keyP.getProject()),
				secondP = new Allele(keyP.getTaxonId(), keyP.getLocusId(), keyP.getId(), 2, false, null, keyP.getProject());
		return Stream.of(Arguments.of(first, new Allele[0], null, new Allele[]{state[0], state[1], first}, true, 2, 2),
				Arguments.of(second, new Allele[]{first}, null,  new Allele[]{state[0], state[1], first, second}, true, 1, 1),
				Arguments.of(firstP, new Allele[0], projectId, new Allele[]{state[2], state[3], firstP}, true, 2, 3),
				Arguments.of(secondP, new Allele[]{firstP}, projectId, new Allele[]{state[2], state[3], firstP, secondP}, true, 1, 1),
				Arguments.of(null, new Allele[0], null,  new Allele[]{state[0], state[1]}, false, 0, 0),
				Arguments.of(null, new Allele[0], projectId, new Allele[]{state[2], state[3]}, false, 0, 0));
	}

	private static Stream<Arguments> remove_params() {
		Allele.PrimaryKey key = new Allele.PrimaryKey(taxon.getPrimaryKey(), locus.getPrimaryKey().getId(), "3"),
				keyP = new Allele.PrimaryKey(taxon.getPrimaryKey(), locus.getPrimaryKey().getId(), "3", projectId);
		Allele first = new Allele(key.getTaxonId(), key.getLocusId(), key.getId(), 1, false, null, key.getProject()),
				second = new Allele(key.getTaxonId(), key.getLocusId(), key.getId(), 1, true, null, key.getProject()),
				firstP = new Allele(keyP.getTaxonId(), keyP.getLocusId(), keyP.getId(), 1, false, null, keyP.getProject()),
				secondP = new Allele(keyP.getTaxonId(), keyP.getLocusId(), keyP.getId(), 1, true, null, keyP.getProject());
		return Stream.of(Arguments.of(key, new Allele[0], null, new Allele[]{state[0], state[1]}, false),
				Arguments.of(key, new Allele[]{first}, null, new Allele[]{state[0], state[1], second}, true),
				Arguments.of(null, new Allele[0], null, new Allele[]{state[0], state[1]}, false),
				Arguments.of(keyP, new Allele[0], projectId, new Allele[]{state[2], state[3]}, false),
				Arguments.of(keyP, new Allele[]{firstP}, projectId, new Allele[]{state[2], state[3], secondP}, true),
				Arguments.of(null, new Allele[0], projectId, new Allele[]{state[2], state[3]}, false));
	}

	private static Stream<Arguments> saveAll_params() {
		Allele.PrimaryKey firstKey = first.getPrimaryKey(), firstPkey = firstProject.getPrimaryKey();
		Allele firstConflict = new Allele(firstKey.getTaxonId(), firstKey.getLocusId(), firstKey.getId(), 2, false, "teste", firstKey.getProject()),
				firstPConflict = new Allele(firstPkey.getTaxonId(), firstPkey.getLocusId(), firstPkey.getId(), 2, false, "sequencep", firstPkey.getProject());
		return Stream.of(Arguments.of(Collections.emptyList(), new Allele[]{state[0], state[1]}, null, BatchRepository.SKIP, new Allele[]{state[0], state[1]}, false, 0, 0),
				Arguments.of(Collections.singletonList(state[0]), new Allele[]{state[1]}, null, BatchRepository.SKIP, new Allele[]{state[0], state[1]}, true, 2, 2),
				Arguments.of(Collections.singletonList(firstConflict), new Allele[]{state[0]}, null, BatchRepository.SKIP, new Allele[]{state[0]}, false, 0, 0),
				Arguments.of(Collections.singletonList(state[2]), new Allele[]{state[3]}, projectId, BatchRepository.SKIP, new Allele[]{state[2], state[3]}, true, 2, 3),
				Arguments.of(Collections.singletonList(firstPConflict), new Allele[]{state[2], state[3]}, projectId, BatchRepository.SKIP, new Allele[]{state[2], state[3]}, false, 0, 0),
				Arguments.of(Collections.singletonList(state[0]), new Allele[]{state[1]}, null, BatchRepository.UPDATE,  new Allele[]{state[0], state[1]}, true, 2, 2),
				Arguments.of(Collections.singletonList(firstConflict), new Allele[]{state[0]}, null, BatchRepository.UPDATE, new Allele[]{state[0], firstConflict}, true, 1, 1),
				Arguments.of(Collections.singletonList(state[2]), new Allele[]{state[3]}, projectId, BatchRepository.UPDATE, new Allele[]{state[2], state[3]}, true, 2, 3),
				Arguments.of(Collections.singletonList(firstPConflict), new Allele[]{state[2], state[3]}, projectId, BatchRepository.UPDATE, new Allele[]{state[2], firstPConflict, state[3]}, true, 1, 1),
				Arguments.of(Arrays.asList(state[0], state[1]), new Allele[0], null, BatchRepository.SKIP,  new Allele[]{state[0], state[1]}, true, 4, 4),
				Arguments.of(Arrays.asList(firstConflict, state[1]), new Allele[]{state[0]}, null, BatchRepository.SKIP,  new Allele[]{state[0], state[1]}, true, 2, 2),
				Arguments.of(Arrays.asList(state[2], state[3]), new Allele[0], projectId, BatchRepository.SKIP, new Allele[]{state[2], state[3]}, true, 4, 6),
				Arguments.of(Arrays.asList(firstPConflict, state[3]), new Allele[]{state[2]}, projectId, BatchRepository.SKIP, new Allele[]{state[2], state[3]}, true, 2, 3),
				Arguments.of(Arrays.asList(state[0], state[1]), new Allele[0], null, BatchRepository.UPDATE,  new Allele[]{state[0], state[1]}, true, 4, 4),
				Arguments.of(Arrays.asList(firstConflict, state[1]), new Allele[]{state[0]}, null, BatchRepository.UPDATE, new Allele[]{state[0], firstConflict, state[1]}, true, 3, 3),
				Arguments.of(Arrays.asList(state[2], state[3]), new Allele[0], projectId, BatchRepository.UPDATE, new Allele[]{state[2], state[3]}, true, 4, 6),
				Arguments.of(Arrays.asList(firstPConflict, state[3]), new Allele[]{state[2]}, projectId, BatchRepository.UPDATE,  new Allele[]{state[2], firstPConflict, state[3]}, true, 3, 4));
	}

	@BeforeEach
	public void init() {
		taxonRepository.save(taxon);
		locusRepository.save(locus);
		projectRepository.save(project);
	}

	@ParameterizedTest
	@MethodSource("findAllNoProject_params")
	public void findAllNoProject(int page, Allele[] state, Allele[] expected) {
		store(state);
		Optional<List<Allele>> result = alleleRepository.findAll(page, LIMIT, taxon.getPrimaryKey(), locus.getPrimaryKey().getId(), null);
		if (expected.length == 0 && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertTrue(result.isPresent());
		List<Allele> alleles = result.get();
		assertEquals(expected.length,  alleles.size());
		assertArrayEquals(expected, alleles.toArray());
	}

	@ParameterizedTest
	@MethodSource("findAllProject_params")
	public void findAllProject(int page, Allele[] state, Allele[] expected) {
		store(state);
		Optional<List<Allele>> result = alleleRepository.findAll(page, LIMIT, taxon.getPrimaryKey(), locus.getPrimaryKey().getId(), projectId);
		if (expected.length == 0 && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertTrue(result.isPresent());
		List<Allele> alleles = result.get();
		assertEquals(expected.length,  alleles.size());
		assertArrayEquals(expected, alleles.toArray());
	}

	@ParameterizedTest
	@MethodSource("find_params")
	public void find(Allele.PrimaryKey key, long version, Allele[] state, Allele expected) {
		store(AlleleRepositoryTests.state);
		store(state);
		Optional<Allele> result = alleleRepository.find(key, version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("exists_params")
	public void exists(Allele.PrimaryKey key, Allele[] state, boolean expected) {
		store(AlleleRepositoryTests.state);
		store(state);
		boolean result = alleleRepository.exists(key);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("save_params")
	public void save(Allele allele, Allele[] state, UUID projectId, Allele[] expectedState, boolean executed, int nodesCreated, int relationshipsCreated) {
		store(AlleleRepositoryTests.state);
		store(state);
		Optional<QueryStatistics> result = alleleRepository.save(allele);
		Allele[] stateResult = findAll(projectId);
		if(executed) {
			assertTrue(result.isPresent());
			assertEquals(nodesCreated, result.get().getNodesCreated());
			assertEquals(relationshipsCreated, result.get().getRelationshipsCreated());
		} else
			assertFalse(result.isPresent());
		assertArrayEquals(expectedState, stateResult);
	}

	@ParameterizedTest
	@MethodSource("remove_params")
	public void remove(Allele.PrimaryKey key, Allele[] state, UUID projectId, Allele[] expectedState, boolean expectedResult) {
		store(AlleleRepositoryTests.state);
		store(state);
		boolean result = alleleRepository.remove(key);
		Allele[] stateResult = findAll(projectId);
		assertEquals(expectedResult, result);
		assertArrayEquals(expectedState, stateResult);
	}

	@ParameterizedTest
	@MethodSource("saveAll_params")
	public void saveAll(List<Allele> alleles, Allele[] state, UUID projectId, String flag, Allele[] expectedState, boolean executed, int nodesCreated, int relationshipsCreated) {
		store(state);
		Optional<QueryStatistics> result = projectId == null ?
				alleleRepository.saveAll(alleles, flag, locus.getPrimaryKey().getTaxonId(), locus.getPrimaryKey().getId(), null) :
				alleleRepository.saveAll(alleles, flag, locus.getPrimaryKey().getTaxonId(), locus.getPrimaryKey().getId(), projectId.toString());
		if(executed) {
			assertTrue(result.isPresent());
			assertEquals(nodesCreated, result.get().getNodesCreated());
			assertEquals(relationshipsCreated, result.get().getRelationshipsCreated());
		} else
			assertFalse(result.isPresent());
		Allele[] stateResult = findAll(projectId);
		assertArrayEquals(expectedState, stateResult);
	}


}
