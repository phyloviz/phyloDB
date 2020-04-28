package pt.ist.meic.phylodb.typing.schema;

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
import pt.ist.meic.phylodb.phylogeny.locus.LocusRepository;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.utils.MockResult;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class SchemaServiceTests extends Test {

	@MockBean
	private LocusRepository locusRepository;
	@MockBean
	private SchemaRepository schemaRepository;

	@InjectMocks
	private SchemaService service;

	private static final int LIMIT = 2;
	private static final Taxon taxon = new Taxon("t", null);
	private static final Locus locus1 = new Locus(taxon.getPrimaryKey(), "1", 1, false, "description");
	private static final Locus locus2 = new Locus(taxon.getPrimaryKey(), "2", 1, false, null);
	private static final Schema schema1 = new Schema(taxon.getPrimaryKey(), "1one", 1, false, Method.MLST, null,
			Arrays.asList(new Entity<>(locus1.getPrimaryKey().getId(), locus1.getVersion(), locus1.isDeprecated()), new Entity<>(locus2.getPrimaryKey().getId(), locus2.getVersion(), locus2.isDeprecated())));
	private static final Schema schema2 = new Schema(taxon.getPrimaryKey(), "2two", 1, false, Method.MLST, null,
			Arrays.asList(new Entity<>(locus2.getPrimaryKey().getId(), locus2.getVersion(), locus2.isDeprecated()), new Entity<>(locus1.getPrimaryKey().getId(), locus1.getVersion(), locus1.isDeprecated())));
	private static final Schema[] state = new Schema[]{schema1, schema2};

	private static Stream<Arguments> getSchemas_params() {
		List<Schema> expected1 = new ArrayList<Schema>() {{ add(state[0]); }};
		List<Schema> expected2 = new ArrayList<Schema>() {{ add(state[0]); add(state[1]); }};
		return Stream.of(Arguments.of(0, Collections.emptyList()),
				Arguments.of(0, expected1),
				Arguments.of(0, expected2),
				Arguments.of(-1, null));
	}

	private static Stream<Arguments> getSchema_params() {
		return Stream.of(Arguments.of(schema1.getPrimaryKey(), 1, schema1),
				Arguments.of(schema1.getPrimaryKey(), 1, null));
	}

	private static Stream<Arguments> saveSchema_params() {
		Schema different =  new Schema(taxon.getPrimaryKey(), "different", 1, false, Method.MLST, null, null);
		return Stream.of(Arguments.of(state[0], false, state[0], new MockResult().queryStatistics()),
				Arguments.of(state[1], false, different, null),
				Arguments.of(state[0], true, state[0], null),
				Arguments.of(null, false, state[0], null));
	}

	private static Stream<Arguments> deleteSchema_params() {
		return Stream.of(Arguments.of(state[0].getPrimaryKey(), true),
				Arguments.of(state[0].getPrimaryKey(), false));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@ParameterizedTest
	@MethodSource("getSchemas_params")
	public void getSchema(int page, List<Schema> expected) {
		Mockito.when(schemaRepository.findAll(anyInt(), anyInt(), any())).thenReturn(Optional.ofNullable(expected));
		Optional<List<Schema>> result = service.getSchemas(taxon.getPrimaryKey(), page, LIMIT);
		if(expected == null && !result.isPresent()) {
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
		Optional<Schema> result = service.getSchema(key.getTaxonId(), key.getId(), version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("saveSchema_params")
	public void saveSchema(Schema schema, boolean missing, Schema dbSchema, QueryStatistics expected) {
		Mockito.when(locusRepository.anyMissing(any())).thenReturn(missing);
		Mockito.when(schemaRepository.find(any(), any(), any())).thenReturn(Optional.ofNullable(dbSchema));
		Mockito.when(schemaRepository.save(any())).thenReturn(Optional.ofNullable(expected));
		boolean result = service.saveSchema(schema);
		assertEquals(expected != null, result);
	}

	@ParameterizedTest
	@MethodSource("deleteSchema_params")
	public void deleteSchema(Schema.PrimaryKey key, boolean expected) {
		Mockito.when(schemaRepository.remove(any())).thenReturn(expected);
		boolean result = service.deleteSchema(key.getTaxonId(), key.getId());
		assertEquals(expected, result);
	}

}
