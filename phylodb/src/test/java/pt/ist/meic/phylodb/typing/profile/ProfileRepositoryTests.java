package pt.ist.meic.phylodb.typing.profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.neo4j.ogm.model.QueryStatistics;
import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import pt.ist.meic.phylodb.RepositoryTests;
import pt.ist.meic.phylodb.phylogeny.allele.AlleleRepository;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.locus.LocusRepository;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.phylogeny.taxon.TaxonRepository;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.security.authentication.user.UserRepository;
import pt.ist.meic.phylodb.security.authentication.user.model.User;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.security.authorization.project.ProjectRepository;
import pt.ist.meic.phylodb.security.authorization.project.model.Project;
import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.typing.dataset.DatasetRepository;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.SchemaRepository;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.db.Query;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

public class ProfileRepositoryTests extends RepositoryTests {

	private static final int LIMIT = 2;
	private static final User user = new User("1one", "one", 1, false, Role.USER);
	private static final Project project1 = new Project(UUID.fromString("2023b71c-704f-425e-8dcf-b26fc84300e7"), 1, false, "private1", "private", null, new User.PrimaryKey[]{user.getPrimaryKey()});
	private static final Project project2 = new Project(UUID.fromString("3023b71c-704f-425e-8dcf-b26fc84300e7"), 1, false, "private1", "private", null, new User.PrimaryKey[]{user.getPrimaryKey()});
	private static final String missing = "-";
	private static final Taxon taxon = new Taxon("t", null);
	private static final Locus locus1 = new Locus(taxon.getPrimaryKey(), "1", 1, false, "description");
	private static final Locus locus2 = new Locus(taxon.getPrimaryKey(), "2", 1, false, null);
	private static final Allele allele11 = new Allele(taxon.getPrimaryKey(), locus1.getPrimaryKey().getId(), "1", 1, false, "sequence11", project1.getPrimaryKey());
	private static final Allele allele12 = new Allele(taxon.getPrimaryKey(), locus1.getPrimaryKey().getId(), "2", 1, false, "sequence12", null);
	private static final Allele allele21 = new Allele(taxon.getPrimaryKey(), locus2.getPrimaryKey().getId(), "1", 1, false, "sequence21", project1.getPrimaryKey());
	private static final Allele allele22 = new Allele(taxon.getPrimaryKey(), locus2.getPrimaryKey().getId(), "2", 1, false, "sequence22", null);
	private static final Schema schema = new Schema(taxon.getPrimaryKey(), "1one", 1, false, Method.MLST, null,
			Arrays.asList(new Entity<>(locus1.getPrimaryKey(), locus1.getVersion(), locus1.isDeprecated()), new Entity<>(locus2.getPrimaryKey(), locus2.getVersion(), locus2.isDeprecated())));
	private static final Dataset dataset = new Dataset(project1.getPrimaryKey(), UUID.fromString("1023b71c-704f-425e-8dcf-b26fc84300e7"), 1, false, "name1", schema);
	private static final Profile profile1 = new Profile(project1.getPrimaryKey(), dataset.getPrimaryKey().getId(), "1", 1, false, null,
			Arrays.asList(new Entity<>(allele11.getPrimaryKey(), allele11.getVersion(), allele11.isDeprecated()), new Entity<>(allele21.getPrimaryKey(), allele21.getVersion(), allele21.isDeprecated())));
	private static final Profile profile2 = new Profile(project1.getPrimaryKey(), dataset.getPrimaryKey().getId(), "2", 1, false, null,
			Arrays.asList(new Entity<>(allele12.getPrimaryKey(), allele12.getVersion(), allele12.isDeprecated()), new Entity<>(allele22.getPrimaryKey(), allele22.getVersion(), allele22.isDeprecated())));
	private static final Profile[] state = new Profile[]{profile1, profile2};
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TaxonRepository taxonRepository;
	@Autowired
	private LocusRepository locusRepository;
	@Autowired
	private AlleleRepository alleleRepository;
	@Autowired
	private SchemaRepository schemaRepository;
	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private DatasetRepository datasetRepository;
	@Autowired
	private ProfileRepository profileRepository;

	private static Stream<Arguments> findAll_params() {
		List<Entity<Allele.PrimaryKey>> alleles1 = Arrays.asList(new Entity<>(allele11.getPrimaryKey(), allele11.getVersion(), allele11.isDeprecated()), null);
		List<Entity<Allele.PrimaryKey>> alleles1Changed = Arrays.asList(new Entity<>(allele11.getPrimaryKey(), allele11.getVersion(), allele11.isDeprecated()),
				new Entity<>(allele21.getPrimaryKey(), allele21.getVersion(), allele21.isDeprecated()));
		List<Entity<Allele.PrimaryKey>> alleles2 = Arrays.asList(null, new Entity<>(allele21.getPrimaryKey(), allele21.getVersion(), allele21.isDeprecated()));
		List<Entity<Allele.PrimaryKey>> alleles3 = Arrays.asList(new Entity<>(allele11.getPrimaryKey(), allele11.getVersion(), allele11.isDeprecated()),
				new Entity<>(allele22.getPrimaryKey(), allele22.getVersion(), allele22.isDeprecated()));
		Profile first = new Profile(project1.getPrimaryKey(), dataset.getPrimaryKey().getId(), "3", 1, false, null, alleles1),
				firstChanged = new Profile(project1.getPrimaryKey(), dataset.getPrimaryKey().getId(), "3", 2, false, null, alleles1Changed),
				second = new Profile(project1.getPrimaryKey(), dataset.getPrimaryKey().getId(), "4", 1, false, "aka4", alleles2),
				third = new Profile(project1.getPrimaryKey(), dataset.getPrimaryKey().getId(), "5", 1, false, "aka5", alleles3),
				thirdChanged = new Profile(project1.getPrimaryKey(), dataset.getPrimaryKey().getId(), "5", 2, false, "aka4changed", alleles3),
				fourth = new Profile(project1.getPrimaryKey(), dataset.getPrimaryKey().getId(), "6", 1, false, "aka6", alleles1);
		return Stream.of(Arguments.of(0, new Profile[0], new Profile[0]),
				Arguments.of(0, new Profile[]{state[0]}, new Profile[]{state[0]}),
				Arguments.of(0, new Profile[]{first, firstChanged}, new Profile[]{firstChanged}),
				Arguments.of(0, new Profile[]{state[0], state[1], first}, state),
				Arguments.of(0, new Profile[]{state[0], state[1], first, firstChanged}, state),
				Arguments.of(1, new Profile[0], new Profile[0]),
				Arguments.of(1, new Profile[]{state[0]}, new Profile[0]),
				Arguments.of(1, new Profile[]{first, firstChanged}, new Profile[0]),
				Arguments.of(1, new Profile[]{state[0], state[1], first}, new Profile[]{first}),
				Arguments.of(1, new Profile[]{state[0], state[1], first, firstChanged}, new Profile[]{firstChanged}),
				Arguments.of(1, new Profile[]{state[0], state[1], first, second}, new Profile[]{first, second}),
				Arguments.of(1, new Profile[]{state[0], state[1], first, firstChanged, second}, new Profile[]{firstChanged, second}),
				Arguments.of(1, new Profile[]{state[0], state[1], first, firstChanged}, new Profile[]{firstChanged}),
				Arguments.of(2, new Profile[0], new Profile[0]),
				Arguments.of(2, new Profile[]{state[0]}, new Profile[0]),
				Arguments.of(2, new Profile[]{first, firstChanged}, new Profile[0]),
				Arguments.of(2, new Profile[]{state[0], state[1], first, second, third}, new Profile[]{third}),
				Arguments.of(2, new Profile[]{state[0], state[1], first, second, third, thirdChanged}, new Profile[]{thirdChanged}),
				Arguments.of(2, new Profile[]{state[0], state[1], first, second, third, fourth}, new Profile[]{third, fourth}),
				Arguments.of(2, new Profile[]{state[0], state[1], first, second, third, thirdChanged, fourth}, new Profile[]{thirdChanged, fourth}),
				Arguments.of(-1, new Profile[0], new Profile[0]));
	}

	private static Stream<Arguments> find_params() {
		Profile.PrimaryKey key = new Profile.PrimaryKey(project1.getPrimaryKey(), dataset.getPrimaryKey().getId(), "3");
		List<Entity<Allele.PrimaryKey>> allelesAll = Arrays.asList(new Entity<>(allele11.getPrimaryKey(), allele11.getVersion(), allele11.isDeprecated()), new Entity<>(allele22.getPrimaryKey(), allele22.getVersion(), allele22.isDeprecated())),
				allelesChangedAll = Arrays.asList(new Entity<>(allele11.getPrimaryKey(), allele11.getVersion(), allele11.isDeprecated()), new Entity<>(allele21.getPrimaryKey(), allele21.getVersion(), allele21.isDeprecated())),
				allelesMissing = Arrays.asList(null, new Entity<>(allele21.getPrimaryKey(), allele21.getVersion(), allele21.isDeprecated())),
				allelesChangedMissing = Arrays.asList(new Entity<>(allele11.getPrimaryKey(), allele11.getVersion(), allele11.isDeprecated()), null);
		Profile first = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 1, false, null, allelesAll),
				firstChanged = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 2, false, null, allelesChangedAll),
				second = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 1, false, "aka5", allelesMissing),
				secondChanged = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 2, false, "aka4changed", allelesChangedMissing);
		return Stream.of(Arguments.of(key, 1, new Profile[0], null),
				Arguments.of(key, 1, new Profile[]{first}, first),
				Arguments.of(key, 1, new Profile[]{second}, second),
				Arguments.of(key, 2, new Profile[]{first, firstChanged}, firstChanged),
				Arguments.of(key, 2, new Profile[]{second, secondChanged}, secondChanged),
				Arguments.of(key, -3, new Profile[0], null),
				Arguments.of(key, -11, new Profile[]{first}, null),
				Arguments.of(key, -16, new Profile[]{second}, null),
				Arguments.of(key, 20, new Profile[]{first, firstChanged}, null),
				Arguments.of(key, 30, new Profile[]{second, secondChanged}, null),
				Arguments.of(null, 1, new Profile[0], null));
	}

	private static Stream<Arguments> exists_params() {
		Profile.PrimaryKey key = new Profile.PrimaryKey(project1.getPrimaryKey(), dataset.getPrimaryKey().getId(), "3");
		List<Entity<Allele.PrimaryKey>> allelesAll = Arrays.asList(new Entity<>(allele11.getPrimaryKey(), allele11.getVersion(), allele11.isDeprecated()), new Entity<>(allele22.getPrimaryKey(), allele22.getVersion(), allele22.isDeprecated())),
				allelesMissing = Arrays.asList(null, new Entity<>(allele21.getPrimaryKey(), allele21.getVersion(), allele21.isDeprecated()));
		Profile first = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 1, false, null, allelesAll),
				firstDeleted = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 1, true, null, allelesAll),
				second = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 1, false, null, allelesMissing),
				secondDeleted = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 1, true, null, allelesMissing);
		return Stream.of(Arguments.of(key, new Profile[0], false),
				Arguments.of(key, new Profile[]{first}, true),
				Arguments.of(key, new Profile[]{firstDeleted}, false),
				Arguments.of(key, new Profile[]{second}, true),
				Arguments.of(key, new Profile[]{secondDeleted}, false),
				Arguments.of(null, new Profile[0], false));
	}

	private static Stream<Arguments> save_params() {
		Profile.PrimaryKey key = new Profile.PrimaryKey(project1.getPrimaryKey(), dataset.getPrimaryKey().getId(), "3");
		List<Entity<Allele.PrimaryKey>> allelesAllPublic = Arrays.asList(new Entity<>(allele12.getPrimaryKey(), allele12.getVersion(), allele12.isDeprecated()), new Entity<>(allele22.getPrimaryKey(), allele22.getVersion(), allele22.isDeprecated())),
				allelesAllPrivate = Arrays.asList(new Entity<>(allele11.getPrimaryKey(), allele11.getVersion(), allele11.isDeprecated()), new Entity<>(allele21.getPrimaryKey(), allele21.getVersion(), allele21.isDeprecated())),
				allelesMissingPublic = Arrays.asList(null, new Entity<>(allele22.getPrimaryKey(), allele22.getVersion(), allele22.isDeprecated())),
				allelesMissingPrivate = Arrays.asList(new Entity<>(allele11.getPrimaryKey(), allele11.getVersion(), allele11.isDeprecated()), null);
		Profile firstPublic = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 1, false, null, allelesAllPublic),
				firstPublicChanged = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 2, false, null, allelesAllPrivate),
				firstPrivate = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 1, false, null, allelesAllPrivate),
				firstPrivateChanged = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 2, false, null, allelesAllPublic),
				secondPublic = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 1, false, "aka5public", allelesMissingPublic),
				secondPublicChanged = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 2, false, "aka5publicChanged", allelesMissingPrivate),
				secondPrivate = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 1, false, "aka5private", allelesMissingPrivate),
				secondPrivateChanged = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 2, false, "aka5privateChanged", allelesMissingPublic);
		return Stream.of(Arguments.of(firstPublic, new Profile[0], new Profile[]{state[0], state[1], firstPublic}, true, 2, 4),
				Arguments.of(firstPrivate, new Profile[0], new Profile[]{state[0], state[1], firstPrivate}, true, 2, 4),
				Arguments.of(secondPublic, new Profile[0], new Profile[]{state[0], state[1], secondPublic}, true, 2, 3),
				Arguments.of(secondPrivate, new Profile[0], new Profile[]{state[0], state[1], secondPrivate}, true, 2, 3),
				Arguments.of(firstPublicChanged, new Profile[]{firstPublic}, new Profile[]{state[0], state[1], firstPublic, firstPublicChanged}, true, 1, 3),
				Arguments.of(firstPrivateChanged, new Profile[]{firstPrivate}, new Profile[]{state[0], state[1], firstPrivate, firstPrivateChanged}, true, 1, 3),
				Arguments.of(secondPublicChanged, new Profile[]{secondPublic}, new Profile[]{state[0], state[1], secondPublic, secondPublicChanged}, true, 1, 2),
				Arguments.of(secondPrivateChanged, new Profile[]{secondPrivate}, new Profile[]{state[0], state[1], secondPrivate, secondPrivateChanged}, true, 1, 2),
				Arguments.of(null, new Profile[0], state, false, 0, 0));
	}

	private static Stream<Arguments> remove_params() {
		Profile.PrimaryKey key = new Profile.PrimaryKey(project1.getPrimaryKey(), dataset.getPrimaryKey().getId(), "3");
		List<Entity<Allele.PrimaryKey>> allelesAll = Arrays.asList(new Entity<>(allele11.getPrimaryKey(), allele11.getVersion(), allele11.isDeprecated()), new Entity<>(allele22.getPrimaryKey(), allele22.getVersion(), allele22.isDeprecated()));
		Profile first = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 1, false, null, allelesAll),
				firstDeleted = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 1, true, null, allelesAll);
		return Stream.of(Arguments.of(key, new Profile[0], state, false),
				Arguments.of(key, new Profile[]{first}, new Profile[]{state[0], state[1], firstDeleted}, true),
				Arguments.of(null, new Profile[0], state, false));
	}

	private static Stream<Arguments> saveAll_params() {
		Profile.PrimaryKey key = new Profile.PrimaryKey(project1.getPrimaryKey(), dataset.getPrimaryKey().getId(), profile1.getPrimaryKey().getId());
		List<Entity<Allele.PrimaryKey>> allelesAllPublic = Arrays.asList(new Entity<>(allele12.getPrimaryKey(), allele12.getVersion(), allele12.isDeprecated()), new Entity<>(allele22.getPrimaryKey(), allele22.getVersion(), allele22.isDeprecated())),
				allelesAllPrivate = Arrays.asList(new Entity<>(allele11.getPrimaryKey(), allele11.getVersion(), allele11.isDeprecated()), new Entity<>(allele21.getPrimaryKey(), allele21.getVersion(), allele21.isDeprecated())),
				allelesMissingPublic = Arrays.asList(null, new Entity<>(allele22.getPrimaryKey(), allele22.getVersion(), allele22.isDeprecated())),
				allelesMissingPrivate = Arrays.asList(new Entity<>(allele11.getPrimaryKey(), allele11.getVersion(), allele11.isDeprecated()), null);
		Profile firstPrivateExists = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 1, false, "test", allelesAllPrivate),
				firstPublicExists = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 1, false, "test", allelesAllPublic),
				firstPrivateExistsConflict = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 2, false, "aka", allelesAllPrivate),
				firstPublicExistsConflict = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 2, false, "aka", allelesAllPublic),
				firstPrivateMissing = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 1, false, "aka test", allelesMissingPrivate),
				firstPrivateMissingConflict = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 2, false, "teste aka", allelesMissingPrivate),
				firstPublicMissing = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 1, false, null, allelesMissingPublic),
				firstPublicMissingConflict = new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), 2, false, null, allelesMissingPublic),
				secondPrivateExists = new Profile(key.getProjectId(), key.getDatasetId(), key.getId() + "1", 1, false, null, allelesAllPrivate),
				secondPublicExists = new Profile(key.getProjectId(), key.getDatasetId(), key.getId() + "1", 1, false, null, allelesAllPublic);
		return Stream.of(Arguments.of(Collections.emptyList(), new Profile[]{state[0], state[1]}, new Profile[]{state[0], state[1]}, false, 0, 0),
				Arguments.of(Collections.singletonList(firstPrivateExists), new Profile[]{state[1]}, new Profile[]{firstPrivateExists, state[1]}, true, 2, 4),
				Arguments.of(Collections.singletonList(firstPrivateExistsConflict), new Profile[]{state[0]}, new Profile[]{state[0], firstPrivateExistsConflict}, true, 1, 3),
				Arguments.of(Collections.singletonList(firstPublicExists), new Profile[]{state[1]}, new Profile[]{firstPublicExists, state[1]}, true, 2, 4),
				Arguments.of(Collections.singletonList(firstPublicExistsConflict), new Profile[]{state[0]}, new Profile[]{state[0], firstPublicExistsConflict}, true, 1, 3),
				Arguments.of(Collections.singletonList(firstPrivateMissing), new Profile[]{state[1]}, new Profile[]{firstPrivateMissing, state[1]}, true, 2, 3),
				Arguments.of(Collections.singletonList(firstPrivateMissingConflict), new Profile[]{state[0]}, new Profile[]{state[0], firstPrivateMissingConflict}, true, 1, 2),
				Arguments.of(Collections.singletonList(firstPublicMissing), new Profile[]{state[1]}, new Profile[]{firstPublicMissing, state[1]}, true, 2, 3),
				Arguments.of(Collections.singletonList(firstPublicMissingConflict), new Profile[]{state[0]}, new Profile[]{state[0], firstPublicMissingConflict}, true, 1, 2),
				Arguments.of(Arrays.asList(firstPrivateExists, secondPrivateExists), new Profile[0], new Profile[]{firstPrivateExists, secondPrivateExists}, true, 4, 8),
				Arguments.of(Arrays.asList(firstPrivateExistsConflict, secondPrivateExists), new Profile[]{state[0]}, new Profile[]{state[0], firstPrivateExistsConflict, secondPrivateExists}, true, 3, 7),
				Arguments.of(Arrays.asList(firstPublicExists, secondPublicExists), new Profile[0], new Profile[]{firstPublicExists, secondPublicExists}, true, 4, 8),
				Arguments.of(Arrays.asList(firstPublicExistsConflict, secondPublicExists), new Profile[]{state[0]}, new Profile[]{state[0], firstPublicExistsConflict, secondPublicExists}, true, 3, 7),
				Arguments.of(Arrays.asList(firstPrivateMissing, secondPrivateExists), new Profile[0], new Profile[]{firstPrivateMissing, secondPrivateExists}, true, 4, 7),
				Arguments.of(Arrays.asList(firstPrivateMissingConflict, secondPrivateExists), new Profile[]{state[0]}, new Profile[]{state[0], firstPrivateMissingConflict, secondPrivateExists}, true, 3, 6),
				Arguments.of(Arrays.asList(firstPublicMissing, secondPublicExists), new Profile[0], new Profile[]{firstPublicMissing, secondPublicExists}, true, 4, 7),
				Arguments.of(Arrays.asList(firstPublicMissingConflict, secondPublicExists), new Profile[]{state[0]}, new Profile[]{state[0], firstPublicMissingConflict, secondPublicExists}, true, 3, 6));
	}

	private void store(Profile[] profiles) {
		for (Profile profile : profiles) {
			Profile.PrimaryKey key = profile.getPrimaryKey();
			String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $}) WHERE pj.deprecated = false AND d.deprecated = false\n" +
					"MERGE (d)-[:CONTAINS]->(p:Profile {id: $}) SET p.deprecated = $ WITH pj, d, p\n" +
					"OPTIONAL MATCH (p)-[r:CONTAINS_DETAILS]->(pd:ProfileDetails)\n" +
					"WHERE NOT EXISTS(r.to) SET r.to = datetime()\n" +
					"WITH pj, d, p, COALESCE(r.version, 0) + 1 as v\n" +
					"CREATE (p)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(pd:ProfileDetails {aka: $})\n" +
					"WITH pj, d, pd\n" +
					"MATCH (d)-[r1:CONTAINS_DETAILS]->(dd:DatasetDetails)-[h:HAS]->(s:Schema)-[r2:CONTAINS_DETAILS]->(sd:SchemaDetails)\n" +
					"WHERE NOT EXISTS(r1.to) AND r2.version = h.version\n" +
					"WITH pj, d, pd, sd\n";
			Query query = new Query(statement);
			query.addParameter(key.getProjectId(), key.getDatasetId(), key.getId(), profile.isDeprecated(), profile.getAka());
			statement = "MATCH (sd)-[:HAS {part: %s}]->(l:Locus)\n" +
					"MATCH (l)-[:CONTAINS]->(a:Allele {id: $})-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)\n" +
					"WHERE NOT EXISTS(r.to) AND %s\n" +
					"CREATE (pd)-[:HAS {version: r.version}]->(a)\n" +
					"WITH pj, d, pd, sd\n";
			List<Entity<Allele.PrimaryKey>> allelesIds = profile.getAllelesReferences();
			for (int i = 0; i < allelesIds.size(); i++) {
				Entity<Allele.PrimaryKey> reference = allelesIds.get(i);
				if (reference == null || reference.getPrimaryKey().getId().matches(missing))
					continue;
				String referenceId = reference.getPrimaryKey().getId();
				String where = reference.getPrimaryKey().getProject() != null ? "(a)<-[:CONTAINS]-(pj)" : "NOT (a)<-[:CONTAINS]-(:Project)";
				query.appendQuery(statement, i + 1, where).addParameter(referenceId);
			}
			query.subQuery(query.length() - "WITH pj, d, pd, sd\n".length());
			execute(query);
		}
	}

	private Profile parse(Map<String, Object> row) {
		int size = Math.toIntExact((long) row.get("size"));
		ArrayList<Entity<Allele.PrimaryKey>> allelesReferences = new ArrayList<>(size);
		for (int i = 0; i < size; i++)
			allelesReferences.add(null);
		for (Map<String, Object> a : (Map<String, Object>[]) row.get("alleles")) {
			int position = Math.toIntExact((long) a.get("part"));
			Object projectId = a.get("project");
			UUID project = projectId == null ? null : UUID.fromString((String) projectId);
			Allele.PrimaryKey key = new Allele.PrimaryKey((String) a.get("taxon"), (String) a.get("locus"), (String) a.get("id"), project);
			Entity<Allele.PrimaryKey> reference = new Entity<>(key, (long) a.get("version"), (boolean) a.get("deprecated"));
			allelesReferences.set(position - 1, reference);
		}
		return new Profile(UUID.fromString(row.get("projectId").toString()),
				UUID.fromString(row.get("datasetId").toString()),
				(String) row.get("id"),
				(long) row.get("version"),
				(boolean) row.get("deprecated"),
				(String) row.get("aka"),
				allelesReferences
		);
	}

	private Profile[] findAll() {
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})-[:CONTAINS_DETAILS]->(dd:DatasetDetails)\n" +
				"MATCH (dd)-[h:HAS]->(s:Schema)-[r:CONTAINS_DETAILS]->(sd:SchemaDetails)-[sp:HAS]->(:Locus)\n" +
				"WHERE r.version = h.version\n" +
				"WITH pj, d, s, sd, COUNT(sp) as schemaSize\n" +
				"MATCH (sd)-[sp:HAS]->(l:Locus)<-[:CONTAINS]-(t:Taxon)\n" +
				"MATCH (d)-[:CONTAINS]->(p:Profile)-[r:CONTAINS_DETAILS]->(pd:ProfileDetails)-[h:HAS]->(a:Allele)<-[:CONTAINS]-(l)\n" +
				"OPTIONAL MATCH (a)<-[:CONTAINS]-(pj2:Project)\n" +
				"RETURN pj.id as projectId, d.id as datasetId, p.id as id, schemaSize as size, r.version as version, p.deprecated as deprecated,\n" +
				"pd.aka as aka, collect(DISTINCT {project: pj2.id, taxon: t.id, locus: l.id, id: a.id, version: h.version, deprecated: a.deprecated, part:sp.part}) as alleles\n" +
				"ORDER BY pj.id, d.id, p.id, version";
		Result result = query(new Query(statement, project1.getPrimaryKey(), dataset.getPrimaryKey().getId()));
		if (result == null) return new Profile[0];
		return StreamSupport.stream(result.spliterator(), false)
				.map(this::parse)
				.toArray(Profile[]::new);
	}

	@BeforeEach
	public void init() {
		taxonRepository.save(taxon);
		locusRepository.save(locus1);
		locusRepository.save(locus2);
		userRepository.save(user);
		projectRepository.save(project1);
		projectRepository.save(project2);
		schemaRepository.save(schema);
		datasetRepository.save(dataset);
		alleleRepository.save(allele11);
		alleleRepository.save(allele12);
		alleleRepository.save(allele21);
		alleleRepository.save(allele22);
	}

	@ParameterizedTest
	@MethodSource("findAll_params")
	public void findAll(int page, Profile[] state, Profile[] expected) {
		store(state);
		Optional<List<Profile>> result = profileRepository.findAll(page, LIMIT, project1.getPrimaryKey(), dataset.getPrimaryKey().getId());
		if (expected.length == 0 && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertTrue(result.isPresent());
		List<Profile> schemas = result.get();
		assertEquals(expected.length, schemas.size());
		assertArrayEquals(expected, schemas.toArray());
	}

	@ParameterizedTest
	@MethodSource("find_params")
	public void find(Profile.PrimaryKey key, long version, Profile[] state, Profile expected) {
		store(ProfileRepositoryTests.state);
		store(state);
		Optional<Profile> result = profileRepository.find(key, version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("exists_params")
	public void exists(Profile.PrimaryKey key, Profile[] state, boolean expected) {
		store(ProfileRepositoryTests.state);
		store(state);
		boolean result = profileRepository.exists(key);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("save_params")
	public void save(Profile profile, Profile[] state, Profile[] expectedState, boolean executed, int nodesCreated, int relationshipsCreated) {
		store(ProfileRepositoryTests.state);
		store(state);
		Optional<QueryStatistics> result = profileRepository.save(profile);
		if (executed) {
			assertTrue(result.isPresent());
			assertEquals(nodesCreated, result.get().getNodesCreated());
			assertEquals(relationshipsCreated, result.get().getRelationshipsCreated());
		} else
			assertFalse(result.isPresent());
		Profile[] stateResult = findAll();
		assertArrayEquals(expectedState, stateResult);
	}

	@ParameterizedTest
	@MethodSource("remove_params")
	public void remove(Profile.PrimaryKey key, Profile[] state, Profile[] expectedState, boolean expectedResult) {
		store(ProfileRepositoryTests.state);
		store(state);
		boolean result = profileRepository.remove(key);
		Profile[] stateResult = findAll();
		assertEquals(expectedResult, result);
		assertArrayEquals(expectedState, stateResult);
	}

	@ParameterizedTest
	@MethodSource("saveAll_params")
	public void saveAll(List<Profile> profiles, Profile[] state, Profile[] expectedState, boolean executed, int nodesCreated, int relationshipsCreated) {
		store(state);
		Optional<QueryStatistics> result = profileRepository.saveAll(profiles, project1.getPrimaryKey().toString(), dataset.getPrimaryKey().getId().toString());
		if (executed) {
			assertTrue(result.isPresent());
			assertEquals(nodesCreated, result.get().getNodesCreated());
			assertEquals(relationshipsCreated, result.get().getRelationshipsCreated());
		} else
			assertFalse(result.isPresent());

		Profile[] stateResult = findAll();
		assertArrayEquals(expectedState, stateResult);
	}

}
