package pt.ist.meic.phylodb.typing.schema;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pt.ist.meic.phylodb.ServiceTestsContext;
import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.typing.schema.model.Schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class SchemaServiceTests extends ServiceTestsContext {

	private static final int LIMIT = 2;
	private static final Schema[] STATE = new Schema[]{SCHEMA1, SCHEMA2};

	private static Stream<Arguments> getSchemas_params() {
		List<Schema> expected1 = new ArrayList<Schema>() {{
			add(STATE[0]);
		}};
		List<Schema> expected2 = new ArrayList<Schema>() {{
			add(STATE[0]);
			add(STATE[1]);
		}};
		return Stream.of(Arguments.of(0, Collections.emptyList()),
				Arguments.of(0, expected1),
				Arguments.of(0, expected2),
				Arguments.of(-1, null));
	}

	private static Stream<Arguments> getSchema_params() {
		return Stream.of(Arguments.of(SCHEMA1.getPrimaryKey(), 1, SCHEMA1),
				Arguments.of(SCHEMA1.getPrimaryKey(), 1, null));
	}

	private static Stream<Arguments> saveSchema_params() {
		Schema different = new Schema(TAXON1.getPrimaryKey(), "different", 1, false, Method.MLST, null, null);
		return Stream.of(Arguments.of(STATE[0], false, STATE[0], true),
				Arguments.of(STATE[1], false, different, false),
				Arguments.of(STATE[0], true, STATE[0], false),
				Arguments.of(null, false, STATE[0], false));
	}

	private static Stream<Arguments> deleteSchema_params() {
		return Stream.of(Arguments.of(STATE[0].getPrimaryKey(), true),
				Arguments.of(STATE[0].getPrimaryKey(), false));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@ParameterizedTest
	@MethodSource("getSchemas_params")
	public void getSchema(int page, List<Schema> expected) {
		Mockito.when(schemaRepository.findAll(anyInt(), anyInt(), any())).thenReturn(Optional.ofNullable(expected));
		Optional<List<Schema>> result = schemaService.getSchemas(TAXON1.getPrimaryKey(), page, LIMIT);
		if (expected == null && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertNotNull(expected);
		assertTrue(result.isPresent());
		List<Schema> schemas = result.get();
		assertEquals(expected.size(), schemas.size());
		assertEquals(expected, schemas);
	}

	@ParameterizedTest
	@MethodSource("getSchema_params")
	public void getSchema(Schema.PrimaryKey key, long version, Schema expected) {
		Mockito.when(schemaRepository.find(any(), anyLong())).thenReturn(Optional.ofNullable(expected));
		Optional<Schema> result = schemaService.getSchema(key.getTaxonId(), key.getId(), version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("saveSchema_params")
	public void saveSchema(Schema schema, boolean missing, Schema dbSchema, boolean expected) {
		Mockito.when(locusRepository.anyMissing(any())).thenReturn(missing);
		Mockito.when(schemaRepository.find(any(), any(), any())).thenReturn(Optional.ofNullable(dbSchema));
		Mockito.when(schemaRepository.save(any())).thenReturn(expected);
		boolean result = schemaService.saveSchema(schema);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("deleteSchema_params")
	public void deleteSchema(Schema.PrimaryKey key, boolean expected) {
		Mockito.when(schemaRepository.remove(any())).thenReturn(expected);
		boolean result = schemaService.deleteSchema(key.getTaxonId(), key.getId());
		assertEquals(expected, result);
	}

}
