package pt.ist.meic.phylodb.typing.dataset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pt.ist.meic.phylodb.ServiceTestsContext;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class DatasetServiceTests extends ServiceTestsContext {

	private static final int LIMIT = 2;
	private static final Dataset[] STATE = new Dataset[]{DATASET1, DATASET2};

	private static Stream<Arguments> getDatasets_params() {
		List<Dataset> expected1 = new ArrayList<Dataset>() {{
			add(STATE[0]);
		}};
		List<Dataset> expected2 = new ArrayList<Dataset>() {{
			add(STATE[0]);
			add(STATE[1]);
		}};
		return Stream.of(Arguments.of(0, Collections.emptyList()),
				Arguments.of(0, expected1),
				Arguments.of(0, expected2),
				Arguments.of(-1, null));
	}

	private static Stream<Arguments> getDataset_params() {
		return Stream.of(Arguments.of(DATASET1.getPrimaryKey(), 1, DATASET1),
				Arguments.of(DATASET1.getPrimaryKey(), 1, null));
	}

	private static Stream<Arguments> saveDataset_params() {
		Dataset withDifferentSchema = new Dataset(PROJECT1.getPrimaryKey(), STATE[0].getPrimaryKey().getId(), 1, false, "name1", new VersionedEntity<>(new Schema.PrimaryKey("t", "x"), 1, false));
		List<Profile> profiles = Collections.singletonList(new Profile(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), "id", 1, false, "aka", null));
		return Stream.of(Arguments.of(STATE[0], true, null, null, true),
				Arguments.of(STATE[1], false, null, null, false),
				Arguments.of(STATE[0], true, withDifferentSchema, Collections.emptyList(), true),
				Arguments.of(STATE[0], true, STATE[0], Collections.emptyList(), false),
				Arguments.of(STATE[0], true, withDifferentSchema, profiles, false),
				Arguments.of(STATE[0], true, STATE[0], profiles, true),
				Arguments.of(null, false, null, null, false));
	}

	private static Stream<Arguments> deleteDataset_params() {
		return Stream.of(Arguments.of(STATE[0].getPrimaryKey(), true),
				Arguments.of(STATE[0].getPrimaryKey(), false));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@ParameterizedTest
	@MethodSource("getDatasets_params")
	public void getDatasets(int page, List<Dataset> expected) {
		Mockito.when(datasetRepository.findAllEntities(anyInt(), anyInt(), any())).thenReturn(Optional.ofNullable(expected));
		Optional<List<Dataset>> result = datasetService.getDatasets(PROJECT1.getPrimaryKey(), page, LIMIT);
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
		Optional<Dataset> result = datasetService.getDataset(key.getProjectId(), key.getId(), version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("saveDataset_params")
	public void saveDataset(Dataset dataset, boolean exists, Dataset dbDataset, List<Profile> profiles, boolean expected) {
		Mockito.when(schemaRepository.exists(any())).thenReturn(exists);
		Mockito.when(datasetRepository.find(any(), anyLong())).thenReturn(Optional.ofNullable(dbDataset));
		Mockito.when(profileRepository.findAllEntities(anyInt(), anyInt(), any(), any())).thenReturn(Optional.ofNullable(profiles));
		Mockito.when(datasetRepository.save(any())).thenReturn(expected);
		boolean result = datasetService.saveDataset(dataset);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("deleteDataset_params")
	public void deleteDataset(Dataset.PrimaryKey key, boolean expected) {
		Mockito.when(datasetRepository.remove(any())).thenReturn(expected);
		boolean result = datasetService.deleteDataset(key.getProjectId(), key.getId());
		assertEquals(expected, result);
	}

}
