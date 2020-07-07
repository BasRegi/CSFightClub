package data;

/**
 * A class to store a pair of ints for every possible piece of logic
 * @author Ronan
 *
 */
public class Intpair {

	private int first;
	private int second;
	
	/**
	 * Create a pair of integers
	 * @param f the first integer
	 * @param s the second integer
	 */
	public Intpair(int f, int s) {
		first = f;
		second = s;
	}
	
	public int getFirst() {
		return first;
	}
	
	public int getSecond() {
		return second;
	}
	
	public void setFirst(int f) {
		first = f;
	}
	
	public void setSecond(int s) {
		second = s;
	}
}
