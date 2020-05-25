package algorithm.inference.model;

public class Matrix {

	private final String[] ids;
	private final int[] isolates;
	private final int[][] distances;

	public Matrix(String[] ids, int[] isolates, String[][] allelesIds) {
		this.ids = ids;
		this.isolates = isolates;
		this.distances = new int[ids.length][];
		for (int i = 0; i < ids.length; i++) {
			this.distances[i] = new int[i];
			for (int j = 0; j < i; j++) {
				int differences = 0;
				for (int l = 0; l < allelesIds[0].length; l++) {
					String il = allelesIds[i][l];
					String jl = allelesIds[j][l];
					if (il == null || !il.equals(jl))
						differences++;
				}
				this.distances[i][j] = differences;
			}
		}
	}

	public String[] getIds() {
		return ids;
	}

	public int[] getIsolates() {
		return isolates;
	}

	public int[][] getDistances() {
		return distances;
	}

	public int size() {
		return distances.length;
	}

	public int distance(int i, int j) {
		return i == j ? 0 : distances[Math.max(i, j)][Math.min(i, j)];
	}

}
