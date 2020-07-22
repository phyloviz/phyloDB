package algorithm.inference.implementation;

import algorithm.inference.model.Edge;
import algorithm.inference.model.Inference;
import algorithm.inference.model.Matrix;

import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.IntStream;

/**
 * GoeBURST is an InferenceAlgorithm which implements the goeBURST algorithm
 */
public class GoeBURST extends InferenceAlgorithm {

	public static final String NAME = "goeburst";
	private int lvs;

	@Override
	public void init(Object... params) {
		this.projectId = (String) params[0];
		this.datasetId = (String) params[1];
		this.id = (String) params[2];
		this.lvs = Math.toIntExact((long) params[3]);
	}

	@Override
	public Inference compute(Matrix matrix) {
		Inference inference = new Inference(projectId, datasetId, id, matrix.getIds(), NAME);
		int size = matrix.size();
		int[][] lv = new int[size][lvs];
		int[] clusters = new int[size];
		Iterator<Edge> it = IntStream.range(0, size)
				.peek(i -> clusters[i] = i)
				.mapToObj(i -> IntStream.range(0, i)
						.mapToObj(j -> new Edge(j, i, matrix.distance(i, j)))
						.filter(edge -> edge.distance() > 0 && edge.distance() <= lvs)
						.peek(e -> {
							lv[e.to()][e.distance() - 1]++;
							lv[e.from()][e.distance() - 1]++;
						}))
				.flatMap(i -> i)
				.sorted(Comparator.comparingInt(Edge::distance).thenComparing((i, j) -> tiebreak(lv, matrix.getIsolates(), matrix.getIds(), i.from(), i.to(), j.from(), j.to())))
				.iterator();
		while (it.hasNext() && inference.edges().count() < size - 1) {
			Edge edge = it.next();
			if (clusters[edge.from()] != clusters[edge.to()]) {
				int i = clusters[edge.from()];
				int j = clusters[edge.to()];
				for (int index = 0; index < clusters.length; index++)
					if (clusters[index] == j)
						clusters[index] = i;
				inference.add(edge);
			}
		}
		return inference;
	}

	private int tiebreak(int[][] lv, int[] isolates, String[] ids, int ifrom, int ito, int jfrom, int jto) {
		int diff;
		for (int index = 0; index < lvs; index++) {
			diff = Integer.compare(Math.max(lv[jfrom][index], lv[jto][index]), Math.max(lv[ifrom][index], lv[ito][index]));
			if (diff != 0)
				return diff;
			diff = Integer.compare(Math.min(lv[jfrom][index], lv[jto][index]), Math.min(lv[ifrom][index], lv[ito][index]));
			if (diff != 0)
				return diff;
		}
		diff = Integer.compare(Math.max(isolates[jfrom], isolates[jto]), Math.max(isolates[ifrom], isolates[ito]));
		if (diff != 0)
			return diff;
		diff = Integer.compare(Math.min(isolates[jfrom], isolates[jto]), Math.min(isolates[ifrom], isolates[ito]));
		if (diff != 0)
			return diff;
		diff = Integer.compare(Math.min(ifrom, ito), Math.min(jfrom, jto));
		return diff != 0 ? diff : compare(ids[compare(ids[ifrom], ids[ito]) > 0 ? ifrom : ito], ids[compare(ids[jfrom], ids[jto]) > 0 ? jfrom : jto]);
	}

	private int compare(String s1, String s2) {
		return s1.length() == s2.length() ? s1.compareTo(s2) : (s1.length() - s2.length());
	}

}
