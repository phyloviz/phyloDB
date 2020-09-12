package algorithm.utils;

/**
 * Class that defines an algorithm
 *
 * @param <T> algorithm input
 * @param <R> algorithm result
 */
public interface Algorithm<T, R> {

	/**
	 * Initializes the algorithm executor
	 *
	 * @param params state to initialize
	 */
	void init(Object... params);

	/**
	 * Executes the algorithm
	 *
	 * @param param algorithm input
	 * @return algorithm result
	 */
	R compute(T param);

}
