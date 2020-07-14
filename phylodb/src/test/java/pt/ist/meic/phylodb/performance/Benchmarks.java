package pt.ist.meic.phylodb.performance;

import org.neo4j.ogm.session.Session;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.springframework.context.ConfigurableApplicationContext;
import pt.ist.meic.phylodb.phylogeny.allele.AlleleService;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.locus.LocusService;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.phylogeny.taxon.TaxonService;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.security.authorization.Visibility;
import pt.ist.meic.phylodb.security.project.ProjectService;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.security.user.UserService;
import pt.ist.meic.phylodb.security.user.model.User;
import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.typing.dataset.DatasetService;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.profile.ProfileService;
import pt.ist.meic.phylodb.typing.schema.SchemaService;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.DbUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static pt.ist.meic.phylodb.utils.DbUtils.clearContext;
import static pt.ist.meic.phylodb.utils.DbUtils.clearProfiles;
import static pt.ist.meic.phylodb.utils.FileUtils.createFile;

public class Benchmarks {

	protected static Session session;
	protected static TaxonService taxonService;
	protected static LocusService locusService;
	protected static AlleleService alleleService;
	protected static SchemaService schemaService;
	protected static UserService userService;
	protected static ProjectService projectService;
	protected static DatasetService datasetService;
	protected static ProfileService profileService;

	protected static final String PROJECT_ID = "project", DATASET_ID = "dataset", INFERENCE_ID = "inference", VISUALIZATION_ID = "visualization";

	protected static void initContext(ConfigurableApplicationContext context) {
		session = context.getBean(Session.class);
		taxonService = context.getBean(TaxonService.class);
		locusService = context.getBean(LocusService.class);
		alleleService = context.getBean(AlleleService.class);
		schemaService = context.getBean(SchemaService.class);
		userService = context.getBean(UserService.class);
		projectService = context.getBean(ProjectService.class);
		datasetService = context.getBean(DatasetService.class);
		profileService = context.getBean(ProfileService.class);
		clearContext(session);
		String taxonId = "taxon";
		taxonService.saveTaxon(new Taxon(taxonId, null));
		List<String> loci = new ArrayList<>();
		for (int i = 1; i <= 7 ; i++) {
			String locusId = "locus" + i;
			loci.add(locusId);
			locusService.saveLocus(new Locus(taxonId, locusId, null));
			for (int j = 1; j <= 500; j++) {
				String sequence = ("sequence" + i) + j;
				alleleService.saveAllele(new Allele(taxonId, locusId, String.valueOf(j), sequence, null));
			}
		}
		String schemaId = "schema";
		schemaService.saveSchema(new Schema(taxonId, schemaId, Method.MLST, null, loci.toArray(new String[0])));
		String userId = "admin", provider = "provider";
		userService.createUser(new User(userId, provider, Role.ADMIN));
		projectService.saveProject(new Project(PROJECT_ID, "name", Visibility.PUBLIC, null, new User.PrimaryKey[0]), new User.PrimaryKey(userId, provider));
		datasetService.saveDataset(new Dataset(PROJECT_ID, DATASET_ID, null, taxonId, schemaId));
	}

	protected static void initProfiles(String filename) throws IOException {
		profileService.saveProfilesOnConflictUpdate(PROJECT_ID, DATASET_ID, false, createFile("performance", filename));
	}

	@State(value = Scope.Benchmark)
	public static class With500Profiles {

		@Setup
		public void setup() throws IOException {
			clearProfiles(session);
			initProfiles("profiles_500.txt");
		}

	}

	@State(value = Scope.Benchmark)
	public static class With500ProfilesAndInference {

		@Setup
		public void setup() throws IOException {
			clearProfiles(session);
			initProfiles("profiles_500.txt");
			DbUtils.goeBURST(session, PROJECT_ID, DATASET_ID, INFERENCE_ID);
		}

	}

	@State(value = Scope.Benchmark)
	public static class With1000Profiles {

		@Setup
		public void setup() throws IOException {
			clearProfiles(session);
			initProfiles("profiles_1000.txt");
		}

	}

	@State(value = Scope.Benchmark)
	public static class With1000ProfilesAndInference {

		@Setup
		public void setup() throws IOException {
			clearProfiles(session);
			initProfiles("profiles_1000.txt");
			DbUtils.goeBURST(session, PROJECT_ID, DATASET_ID, INFERENCE_ID);
		}

	}

	@State(value = Scope.Benchmark)
	public static class With2000Profiles {

		@Setup
		public void setup() throws IOException {
			clearProfiles(session);
			initProfiles("profiles_2000.txt");
		}

	}

	@State(value = Scope.Benchmark)
	public static class With2000ProfilesAndInference {

		@Setup
		public void setup() throws IOException {
			clearProfiles(session);
			initProfiles("profiles_2000.txt");
			DbUtils.goeBURST(session, PROJECT_ID, DATASET_ID, INFERENCE_ID);
		}

	}

	@State(value = Scope.Benchmark)
	public static class With5000Profiles {

		@Setup
		public void setup() throws IOException {
			clearProfiles(session);
			initProfiles("profiles_5000.txt");
		}

	}

	@State(value = Scope.Benchmark)
	public static class With5000ProfilesAndInference {

		@Setup
		public void setup() throws IOException {
			clearProfiles(session);
			initProfiles("profiles_5000.txt");
			DbUtils.goeBURST(session, PROJECT_ID, DATASET_ID, INFERENCE_ID);
		}

	}
}
