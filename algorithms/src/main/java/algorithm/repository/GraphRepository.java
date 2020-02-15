package algorithm.repository;

public interface GraphRepository<T, R, U> {


	R findInput(U param);
	void createOutput(T param);

}
