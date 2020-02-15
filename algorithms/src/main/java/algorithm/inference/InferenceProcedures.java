package algorithm.inference;

import algorithm.inference.implementation.GoeBURST;
import algorithm.inference.model.Analysis;
import algorithm.inference.model.Matrix;
import algorithm.procedure.Procedure;
import org.neo4j.procedure.Mode;
import org.neo4j.procedure.Name;

import java.util.UUID;

public class InferenceProcedures implements Procedure<InferenceAlgorithm, String> {

	@Override
	public void execute(InferenceAlgorithm algorithm, String param) {
		AnalysisRepository repository =  new AnalysisRepository();
		Matrix distanceMatrix = repository.findInput(UUID.fromString(param));
		Analysis phylogeneticTree = algorithm.compute(distanceMatrix);
		repository.createOutput(phylogeneticTree);
	}

	@org.neo4j.procedure.Procedure(value = "algorithms.inference.goeBURST", mode = Mode.WRITE)
	public void goeBURST(@Name("dataset") String dataset) {
		execute(new GoeBURST(), dataset);
	}

}
