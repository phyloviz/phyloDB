package algorithm.inference;


import algorithm.inference.model.Analysis;
import algorithm.inference.model.AnalysisKey;
import algorithm.inference.model.Matrix;
import algorithm.repository.RepositoryImpl;

import java.util.UUID;
import java.util.stream.Stream;

public class AnalysisRepository extends RepositoryImpl<Analysis, Matrix, UUID> {

	public Matrix findInput(UUID param) {
		return null;
	}

	@Override
	public void createOutput(Analysis param) {

	}

	/*
	public Stream<GoeBURSTResult> GoeBURST2(@Name("taxon") String taxon, @Name("schema") String schema) {
		return database.findNodes(Label.label(taxon)).stream()
				.filter(n -> n.hasLabel(Label.label(PROFILE)) && hasNeighbourWithProperty(n, RelationshipType.withName(FOLLOWS_SCHEMA), schema))
				.map(GoeBURSTResult::new);

	}
	private boolean hasNeighbourWithProperty(Node node, RelationshipType relationship, String property) {
		return StreamSupport.stream(node.getRelationships(relationship).spliterator(), false)
				.anyMatch(r -> r.getEndNode().getProperty("name").equals(property));
	}
	*/
}
