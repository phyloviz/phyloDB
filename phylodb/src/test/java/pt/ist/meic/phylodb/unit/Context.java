package pt.ist.meic.phylodb.unit;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import pt.ist.meic.phylodb.PhylodbApplication;
import pt.ist.meic.phylodb.analysis.Analysis;
import pt.ist.meic.phylodb.analysis.inference.model.Edge;
import pt.ist.meic.phylodb.analysis.inference.model.Inference;
import pt.ist.meic.phylodb.analysis.inference.model.InferenceAlgorithm;
import pt.ist.meic.phylodb.analysis.visualization.model.Coordinate;
import pt.ist.meic.phylodb.analysis.visualization.model.Visualization;
import pt.ist.meic.phylodb.analysis.visualization.model.VisualizationAlgorithm;
import pt.ist.meic.phylodb.job.model.Job;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.security.user.model.User;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.security.authorization.Visibility;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.isolate.model.Ancillary;
import pt.ist.meic.phylodb.typing.isolate.model.Isolate;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.Arrays;
import java.util.UUID;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = PhylodbApplication.class)
public abstract class Context {

	protected static final User USER1 = new User("1one", "one", 1, false, Role.USER);
	protected static final User USER2 = new User("2two", "two", 1, false, Role.USER);
	protected static final Project PROJECT1 = new Project("2023b71c-704f-425e-8dcf-b26fc84300e7", 1, false, "private1", Visibility.PRIVATE, null, new User.PrimaryKey[]{USER1.getPrimaryKey()});
	protected static final Project PROJECT2 = new Project("3023b71c-704f-425e-8dcf-b26fc84300e7", 1, false, "private1", Visibility.PRIVATE, null, new User.PrimaryKey[]{USER1.getPrimaryKey()});
	protected static final Taxon TAXON1 = new Taxon("1one", 1, false, "description");
	protected static final Taxon TAXON2 = new Taxon("2two", 1, false, null);
	protected static final Locus LOCUS1 = new Locus(TAXON1.getPrimaryKey(), "1", 1, false, "description");
	protected static final Locus LOCUS2 = new Locus(TAXON1.getPrimaryKey(), "2", 1, false, null);
	protected static final Allele ALLELE11 = new Allele(TAXON1.getPrimaryKey(), LOCUS1.getPrimaryKey().getId(), "1", 1, false, "sequence", null);
	protected static final Allele ALLELE12 = new Allele(TAXON1.getPrimaryKey(), LOCUS1.getPrimaryKey().getId(), "2", 1, false, null, null);
	protected static final Allele ALLELE11P = new Allele(TAXON1.getPrimaryKey(), LOCUS1.getPrimaryKey().getId(), "1", 1, false, "sequence11", PROJECT1.getPrimaryKey());
	protected static final Allele ALLELE12P = new Allele(TAXON1.getPrimaryKey(), LOCUS1.getPrimaryKey().getId(), "2", 1, false, null, PROJECT1.getPrimaryKey());
	protected static final Allele ALLELE21 = new Allele(TAXON1.getPrimaryKey(), LOCUS2.getPrimaryKey().getId(), "1", 1, false, "sequence21", PROJECT1.getPrimaryKey());
	protected static final Allele ALLELE22 = new Allele(TAXON1.getPrimaryKey(), LOCUS2.getPrimaryKey().getId(), "2", 1, false, "sequence22", null);
	protected static final Schema SCHEMA1 = new Schema(TAXON1.getPrimaryKey(), "1one", 1, false, Method.MLST, null,
			Arrays.asList(new VersionedEntity<>(LOCUS1.getPrimaryKey(), LOCUS1.getVersion(), LOCUS1.isDeprecated()), new VersionedEntity<>(LOCUS2.getPrimaryKey(), LOCUS2.getVersion(), LOCUS2.isDeprecated())));
	protected static final Schema SCHEMA2 = new Schema(TAXON1.getPrimaryKey(), "2two", 1, false, Method.MLST, null,
			Arrays.asList(new VersionedEntity<>(LOCUS2.getPrimaryKey(), LOCUS2.getVersion(), LOCUS2.isDeprecated()), new VersionedEntity<>(LOCUS1.getPrimaryKey(), LOCUS1.getVersion(), LOCUS1.isDeprecated())));
	protected static final VersionedEntity<Schema.PrimaryKey> SCHEMA1REFERENCE = new VersionedEntity<>(SCHEMA1.getPrimaryKey(), SCHEMA1.getVersion(), SCHEMA1.isDeprecated());
	protected static final VersionedEntity<Schema.PrimaryKey> SCHEMA2REFERENCE = new VersionedEntity<>(SCHEMA2.getPrimaryKey(), SCHEMA2.getVersion(), SCHEMA2.isDeprecated());
	protected static final Dataset DATASET1 = new Dataset(PROJECT1.getPrimaryKey(), "1023b71c-704f-425e-8dcf-b26fc84300e7", 1, false, "name1", SCHEMA1REFERENCE);
	protected static final Dataset DATASET2 = new Dataset(PROJECT1.getPrimaryKey(), "2023b71c-704f-425e-8dcf-b26fc84300e7", 1, false, "name2", SCHEMA2REFERENCE);
	protected static final Profile PROFILE1 = new Profile(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), "1", 1, false, null,
			Arrays.asList(new VersionedEntity<>(ALLELE11P.getPrimaryKey(), ALLELE11P.getVersion(), ALLELE11P.isDeprecated()), new VersionedEntity<>(ALLELE21.getPrimaryKey(), ALLELE21.getVersion(), ALLELE21.isDeprecated())));
	protected static final Profile PROFILE2 = new Profile(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), "2", 1, false, null,
			Arrays.asList(new VersionedEntity<>(ALLELE12.getPrimaryKey(), ALLELE12.getVersion(), ALLELE12.isDeprecated()), new VersionedEntity<>(ALLELE22.getPrimaryKey(), ALLELE22.getVersion(), ALLELE22.isDeprecated())));
	protected static final Profile PROFILE3 = new Profile(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), "3", 1, false, null,
			Arrays.asList(new VersionedEntity<>(ALLELE12.getPrimaryKey(), ALLELE12.getVersion(), ALLELE12.isDeprecated()), new VersionedEntity<>(ALLELE22.getPrimaryKey(), ALLELE22.getVersion(), ALLELE22.isDeprecated())));
	protected static final Ancillary ANCILLARY1 = new Ancillary("key1", "value1");
	protected static final Ancillary ANCILLARY2 = new Ancillary("key2", "value2");
	protected static final Isolate ISOLATE1 = isolate("1", 1, false, null, new Ancillary[]{ANCILLARY1, ANCILLARY2}, PROFILE1);
	protected static final Isolate ISOLATE2 = isolate("2", 1, false, null, new Ancillary[]{ANCILLARY1, ANCILLARY2}, PROFILE2);
	protected static final Edge EDGES1 = new Edge(new VersionedEntity<>(PROFILE1.getPrimaryKey(), PROFILE1.getVersion(), PROFILE1.isDeprecated()), new VersionedEntity<>(PROFILE2.getPrimaryKey(), PROFILE2.getVersion(), PROFILE2.isDeprecated()), 1);
	protected static final Edge EDGES2 = new Edge(new VersionedEntity<>(PROFILE2.getPrimaryKey(), PROFILE2.getVersion(), PROFILE2.isDeprecated()), new VersionedEntity<>(PROFILE3.getPrimaryKey(), PROFILE3.getVersion(), PROFILE3.isDeprecated()), 2);
	protected static final Inference INFERENCE1 = new Inference(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), "5023b71c-704f-425e-8dcf-b26fc84300e7", false, InferenceAlgorithm.GOEBURST, Arrays.asList(EDGES1, EDGES2));
	protected static final Inference INFERENCE2 = new Inference(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), "6023b71c-704f-425e-8dcf-b26fc84300e7", false, InferenceAlgorithm.GOEBURST, Arrays.asList(EDGES1, EDGES2));
	protected static final Coordinate COORDINATE11 = new Coordinate(PROFILE1.getPrimaryKey(), 1, 11, 11);
	protected static final Coordinate COORDINATE12 = new Coordinate(PROFILE2.getPrimaryKey(), 1, 12, 12);
	protected static final Coordinate COORDINATE13 = new Coordinate(PROFILE3.getPrimaryKey(), 1, 13, 13);
	protected static final Coordinate COORDINATE21 = new Coordinate(PROFILE1.getPrimaryKey(), 1, 21, 21);
	protected static final Coordinate COORDINATE22 = new Coordinate(PROFILE2.getPrimaryKey(), 1, 22, 22);
	protected static final Coordinate COORDINATE23 = new Coordinate(PROFILE3.getPrimaryKey(), 1, 23, 23);
	protected static final Visualization VISUALIZATION1 = new Visualization(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), INFERENCE1.getPrimaryKey().getId(), "2023b71c-704f-425e-8dcf-b26fc84300e7", false, VisualizationAlgorithm.RADIAL, Arrays.asList(COORDINATE11, COORDINATE12, COORDINATE13));
	protected static final Visualization VISUALIZATION2 = new Visualization(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), INFERENCE1.getPrimaryKey().getId(), "3023b71c-704f-425e-8dcf-b26fc84300e7", false, VisualizationAlgorithm.RADIAL,  Arrays.asList(COORDINATE21, COORDINATE22, COORDINATE23));
	protected static final Job JOB1 = new Job(PROJECT1.getPrimaryKey(), "j1", Analysis.INFERENCE.getName() + "." + InferenceAlgorithm.GOEBURST.getName(), UUID.randomUUID().toString(), new Object[]{DATASET1.getPrimaryKey().getId(), 3});
	protected static final Job JOB2 = new Job(PROJECT1.getPrimaryKey(), "j2", Analysis.VISUALIZATION.getName() + "." + VisualizationAlgorithm.RADIAL.getName(), UUID.randomUUID().toString(), new Object[]{DATASET1.getPrimaryKey().getId(), INFERENCE1.getPrimaryKey().getId()});

	protected static Isolate isolate(String id, long version, boolean deprecated, String description, Ancillary[] ancillary, Profile profile) {
		VersionedEntity<Profile.PrimaryKey> reference = null;
		if(profile != null)
			reference = new VersionedEntity<>(profile.getPrimaryKey(), profile.getVersion(), profile.isDeprecated());
		return new Isolate(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), id, version, deprecated, description, ancillary, reference);
	}

}
