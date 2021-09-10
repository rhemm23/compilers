/*
 * Represents a function or variable name
 */
public class Sym {

	/*
	 * A string value of the type of the current symbol
	 */
	private String type;

	/*
	 * Initialize a symbol with the given type
	 */
	public Sym(String type) {
		this.type = type;
	}

	/*
	 * Get the symbol's type
	 */
	public String getType() {
		return this.type;
	}

	/*
	 * Get a string representation of the symbol, used for debugging
	 */
	public String toString() {
		return this.type;
	}
}