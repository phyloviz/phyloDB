package pt.ist.meic.phylodb.typing.dataset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.neo4j.ogm.model.QueryStatistics;
import org.springframework.boot.test.mock.mockito.MockBean;
import pt.ist.meic.phylodb.Test;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.security.authentication.user.model.User;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.security.authorization.project.model.Project;
import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.profile.ProfileRepository;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.SchemaRepository;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.MockResult;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class DatasetServiceTests extends Test {

	private static final int LIMIT = 2;
	private static final User user = new User("1one", "one", 1, false, Role.USER);
	private static final Project project = new Project(UUID.fromString("2023b71c-704f-425e-8dcf-b26fc84300e7"), 1, false, "private1", "private", null, new User.PrimaryKey[]{user.getPrimaryKey()});
	private static final Taxon taxon = new Taxon("t", null);
	private static final Locus locus1 = new Locus(taxon.getPrimaryKey(), "1", 1, false, "description");
	private static final Locus locus2 = new Locus(taxon.getPrimaryKey(), "2", 1, false, null);
	private static final Schema schema = new Schema(taxon.getPrimaryKey(), "1one", 1, false, Method.MLST, null,
			Arrays.asList(new Entity<>(locus1.getPrimaryKey(), locus1.getVersion(), locus1.isDeprecated()), new Entity<>(locus2.getPrimaryKey(), locus2.getVersion(), locus2.isDeprecated())));
	private static final Entity<Schema.PrimaryKey> schemaReference = new Entity<>(schema.getPrimaryKey(), schema.getVersion(), schema.isDeprecated());
	private static final Dataset dataset1 = new Dataset(project.getPrimaryKey(), UUID.randomUUID(), 1, false, "name1", schemaReference);
	private static final Dataset dataset2 = new Dataset(project.getPrimaryKey(), UUID.randomUUID(), 1, false, "name2", schemaReference);
	private static final Dataset[] state = new Dataset[]{dataset1, dataset2};
	@MockBean
	private DatasetRepository datasetRepository;
	@MockBean
	private SchemaRepository schemaRepository;
	@MockBean
	private ProfileRepository profileRepository;
	@InjectMocks
	private DatasetService service;

	private static Stream<Arguments> getDatasets_params() {
		List<Dataset> expected1 = new ArrayList<Dataset>() {{
			add(state[0]);
		}};
		List<Dataset> expected2 = new ArrayList<Dataset>() {{
			add(state[0]);
			add(state[1]);
		}};
		return Stream.of(Arguments.of(0, Collections.emptyList()),
				Arguments.of(0, expected1),
				Arguments.of(0, expected2),
				Arguments.of(-1, null));
	}

	private static Stream<Arguments> getDataset_params() {
		return Stream.of(Arguments.of(dataset1.getPrimaryKey(), 1, dataset1),
				Arguments.of(dataset1.getPrimaryKey(), 1, null));
	}

	private static Stream<Arguments> saveDataset_params() {
		Dataset withDifferentSchema = new Dataset(project.getPrimaryKey(), state[0].getPrimaryKey().getId(), 1, false, "name1", new Entity<>(new Schema.PrimaryKey("t", "x"), 1, false));
		List<Profile> profiles = Collections.singletonList(new Profile(project.getPrimaryKey(), dataset1.getPrimaryKey().getId(), "id", 1, false, "aka", null));
		return Stream.of(Arguments.of(state[0], true, null, null, new MockResult().queryStatistics()),
				Arguments.of(state[1], false, null, null, null),
				Arguments.of(state[0], true, withDifferentSchema, Collections.emptyList(), new MockResult().queryStatistics()),
				Arguments.of(state[0], true, state[0], Collections.emptyList(), new MockResult().queryStatistics()),
				Arguments.of(state[0], true, withDifferentSchema, profiles, null),
				Arguments.of(state[0], true, state[0], profiles, new MockResult().queryStatistics()),
				Arguments.of(null, false, null, null, null));
	}

	private static Stream<Arguments> deleteDataset_params() {
		return Stream.of(Arguments.of(state[0].getPrimaryKey(), true),
				Arguments.of(state[0].getPrimaryKey(), false));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@ParameterizedTest
	@MethodSource("getDatasets_params")
	public void getDatasets(int page, List<Dataset> expected) {
		Mockito.when(datasetRepository.findAll(anyInt(), anyInt(), any())).thenReturn(Optional.ofNullable(expected));
		Optional<List<Dataset>> result = service.getDatasets(project.getPrimaryKey(), page, LIMIT);
		if (expected == null && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertNotNull(expected);
		assertTrue(result.isPresent());
		List<Dataset> schemas = result.get();
		assertEquals(expected.size(), schemas.size());
		assertEquals(expected, schemas);
	}

	@ParameterizedTest
	@MethodSource("getDataset_params")
	public void getDataset(Dataset.PrimaryKey key, long version, Dataset expected) {
		Mockito.when(datasetRepository.find(any(), anyLong())).thenReturn(Optional.ofNullable(expected));
		Optional<Dataset> result = service.getDataset(key.getProjectId(), key.getId(), version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("saveDataset_params")
	public void saveDataset(Dataset dataset, boolean exists, Dataset dbDataset, List<Profile> profiles, QueryStatistics expected) {
		Mockito.when(schemaRepository.exists(any())).thenReturn(exists);
		Mockito.when(datasetRepository.find(any(), anyLong())).thenReturn(Optional.ofNullable(dbDataset));
		Mockito.when(profileRepository.findAll(anyInt(), anyInt(), any(), any())).thenReturn(Optional.ofNullable(profiles));
		Mockito.when(datasetRepository.save(any())).thenReturn(Optional.ofNullable(expected));
		boolean result = service.saveDataset(dataset);
		assertEquals(expected != null, result);
	}

	@ParameterizedTest
	@MethodSource("deleteDataset_params")
	public void deleteDataset(Dataset.PrimaryKey key, boolean expected) {
		Mockito.when(datasetRepository.remove(any())).thenReturn(expected);
		boolean result = service.deleteDataset(key.getProjectId(), key.getId());
		assertEquals(expected, result);
	}

}
