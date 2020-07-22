package algorithm.visualization.model;

import java.util.Arrays;

/**
 * A tree is the result of an {@link algorithm.inference.implementation.InferenceAlgorithm}. It contains a set of {@link Vertex} which are the
 * roots of the tree
 */
public class Tree {

	private final Vertex[] roots;

	public Tree(Vertex[] roots) {
		this.roots = roots;
	}

	public Vertex[] getRoots() {
		return roots;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Tree tree = (Tree) o;
		return Arrays.equals(roots, tree.roots);
	}

}
