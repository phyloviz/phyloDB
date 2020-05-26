package pt.ist.meic.phylodb.formatters;

import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.ArrayList;
import java.util.List;

public class ProfilesFormatterTests extends FormatterTests {

	public static Profile[] profiles(String project, String dataset, Schema schema, String[][] profileAlleles, boolean authorized) {
		List<Profile> profiles = new ArrayList<>();
		List<Entity<Locus.PrimaryKey>> lociReferences = schema.getLociReferences();
		for (int j = 0; j < profileAlleles.length; ++j) {
			List<Entity<Allele.PrimaryKey>> references = new ArrayList<>();
			String[] alleles = profileAlleles[j];
			for (int i = 0; i < alleles.length; ++i) {
				if (alleles[i] != null) {
					Allele.PrimaryKey key = authorized ?
							new Allele.PrimaryKey(schema.getPrimaryKey().getTaxonId(), lociReferences.get(i).getPrimaryKey().getId(), alleles[i], project) :
							new Allele.PrimaryKey(schema.getPrimaryKey().getTaxonId(), lociReferences.get(i).getPrimaryKey().getId(), alleles[i]);
					references.add(new Entity<>(key, EntityRepository.CURRENT_VERSION_VALUE, false));
					continue;
				}
				references.add(null);
			}
			profiles.add(new Profile(project, dataset, String.valueOf(j + 1), EntityRepository.CURRENT_VERSION_VALUE, false, null, references));
		}
		return profiles.toArray(new Profile[0]);
	}

}
