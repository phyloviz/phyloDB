package algorithm;

public interface Algorithm<T, R> {

	void init(Object... params);
	R compute(T param);
}
