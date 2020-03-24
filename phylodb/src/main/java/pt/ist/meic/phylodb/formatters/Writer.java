package pt.ist.meic.phylodb.formatters;

public interface Writer<T> {

	String format(T data);

}
