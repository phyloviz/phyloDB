package pt.ist.meic.phylodb.stress;

import org.neo4j.ogm.session.Session;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import pt.ist.meic.phylodb.PhylodbApplication;
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
import static pt.ist.meic.phylodb.utils.FileUtils.createFile;

public class Context {

	public static void main(String[] args) throws IOException {
		ConfigurableApplicationContext context = SpringApplication.run(PhylodbApplication.class, args);
		Session session = context.getBean(Session.class);
		TaxonService taxonService = context.getBean(TaxonService.class);
		LocusService locusService = context.getBean(LocusService.class);
		AlleleService alleleService = context.getBean(AlleleService.class);
		SchemaService schemaService = context.getBean(SchemaService.class);
		UserService userService = context.getBean(UserService.class);
		ProjectService projectService = context.getBean(ProjectService.class);
		DatasetService datasetService = context.getBean(DatasetService.class);
		ProfileService profileService = context.getBean(ProfileService.class);
		clearContext(session);
		String taxonId = "taxon";
		taxonService.saveTaxon(new Taxon(taxonId, null));
		List<String> loci = new ArrayList<>();
		for (int i = 1; i <= 7 ; i++) {
			String locusId = "locus" + i;
			loci.add(locusId);
			locusService.saveLocus(new Locus(taxonId, locusId, null));
			for (int j = 1; j <= 100; j++) {
				String sequence = ("sequence" + i) + j;
				alleleService.saveAllele(new Allele(taxonId, locusId, String.valueOf(j), sequence, null));
			}
		}
		String schemaId = "schema";
		schemaService.saveSchema(new Schema(taxonId, schemaId, Method.MLST, null, loci.toArray(new String[0])));
		String userId = "admin", provider = "provider";
		userService.createUser(new User(userId, provider, Role.ADMIN));
		String projectId = "project", datasetId = "dataset";
		projectService.saveProject(new Project(projectId, "name", Visibility.PUBLIC, null, new User.PrimaryKey[0]), new User.PrimaryKey(userId, provider));
		datasetService.saveDataset(new Dataset(projectId, datasetId, null, taxonId, schemaId));
		profileService.saveProfilesOnConflictUpdate(projectId, datasetId, false, createFile("stress", "profiles_500.txt"));
		String inferenceId = "inference";
		String visualizationId = "visualization";
		DbUtils.goeBURST(session, projectId, datasetId, inferenceId);
		DbUtils.radial(session, projectId, datasetId, inferenceId, visualizationId);
		System.out.println("...Context data initialized...");
		System.exit(0);
	}
}
