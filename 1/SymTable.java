import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * Represents a table of symbols
 */
public class SymTable {

	/*
	 * A list of maps, where each element represents a scope and is a map of
	 * symbol names to symbol objects
	 */
	private List<HashMap<String, Sym>> scopes;

	/*
	 * Initializes the symbol table with a single scope
	 */
	public SymTable() {
		this.scopes = new ArrayList<HashMap<String, Sym>>();
		this.scopes.add(new HashMap<String, Sym>());
	}

	/*
	 * Adds a new symbol declaration to the table
	 */
	public void addDecl(String name, Sym sym)
		throws DuplicateSymException, EmptySymTableException {
		if (this.scopes.isEmpty()) {
			throw new EmptySymTableException();
		}
		if (name == null || sym == null) {
			throw new NullPointerException();
		}
		if (this.scopes.get(0).containsKey(name)) {
			throw new DuplicateSymException();
		}
		this.scopes.get(0).put(name, sym);
	}

	/*
	 * Adds a new scope to the symbol table
	 */
	public void addScope() {
		this.scopes.add(0, new HashMap<String, Sym>());
	}

	/*
	 * Looks for a symbol in the local scope and returns it,
	 * returns null if one does not exist
	 */
	public Sym lookupLocal(String name) throws EmptySymTableException {
		if (this.scopes.isEmpty()) {
			throw new EmptySymTableException();
		}
		return this.scopes.get(0).get(name);
	}

	/*
	 * Looks for a symbol in the entire table and returns it,
	 * returns null if one does not exist
	 */
	public Sym lookupGlobal(String name) throws EmptySymTableException {
		if (this.scopes.isEmpty()) {
			throw new EmptySymTableException();
		}
		for (HashMap<String, Sym> scope : this.scopes) {
			if (scope.containsKey(name)) {
				return scope.get(name);
			}
		}
		return null;
	}

	/*
	 * Removes all symbols from the local scope from the symbol table
	 */
	public void removeScope() throws EmptySymTableException {
		if (this.scopes.isEmpty()) {
			throw new EmptySymTableException();
		}
		this.scopes.remove(0);
	}

	/*
	 * Prints a readable version of the symbol table
	 */
	public void print() {
		System.out.println("\nSym Table");
		for (HashMap<String, Sym> scope : this.scopes) {
			System.out.println(scope.toString());
		}
		System.out.println();
	}
}
