package pt.ist.meic.phylodb.formatters;

import pt.ist.meic.phylodb.typing.profile.model.Profile;

import java.util.UUID;
import java.util.stream.IntStream;

public class ProfilesFormatterTests extends FormatterTests{

	protected static Profile[] profiles(UUID project, UUID dataset, String[][] profileAlleles) {
		return IntStream.range(0, profileAlleles.length)
				.filter(i -> profileAlleles[i].length != 0)
				.mapToObj(i -> new Profile(project, dataset, String.valueOf(i + 1), null, profileAlleles[i]))
				.toArray(Profile[]::new);
	}
}
