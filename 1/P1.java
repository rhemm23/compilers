import java.util.HashMap;
import java.util.Map;

/*
 * Performs tests to assure Sym and SymTable are functioning correctly
 */
public class P1 {

	/*
	 * Represents an executable unit test
	 */
	private interface UnitTest {

		/*
		 * Execute the test. Returns true if the test passed, false otherwise
		 */
		boolean execute();
	}

	/*
	 * A test symbol type
	 */
	private static final String TEST_SYM_TYPE = "test-type";

	/*
	 * Used to indicate to the terminal to print red text
	 */
	private static final String ANSI_RESET = "\u001B[0m";

	/*
	 * Used to reset the terminal text color
	 */
	private static final String ANSI_RED = "\u001B[31m";

	/*
	 * Used to indicate to the terminal to print green text
	 */
	private static final String ANSI_GREEN = "\u001B[32m";

	/*
	 * Executes all unit tests and prints results to the terminal
	 */
	public static void main(String[] args) {
		HashMap<String, UnitTest> unitTests = new HashMap<String, UnitTest>() {{
			put("Sym - getType", () -> testSymGetType());
			put("Sym - toString", () -> testSymToString());
			put("SymTable - removeScope", () -> testSymTableRemoveScope());
			put("SymTable - addDeclEmptyTable",
				() -> testSymTableAddDeclEmptyTable());
			put("SymTable - addDeclDuplicateSymbol",
				() -> testSymTableAddDeclDuplicateSym());
			put("SymTable - addDeclNullPointer",
				() -> testSymTableAddDeclNullPointer());
			put("SymTable - lookupLocal", () -> testSymTableLookupLocal());
			put("SymTable - lookupGlobal", () -> testSymTableLookupGlobal());
			put("SymTable - addScope", () -> testSymTableAddScope());
		}};
		for (Map.Entry<String, UnitTest> unitTest : unitTests.entrySet()) {
			if (unitTest.getValue().execute()) {
				printTestPassed(unitTest.getKey());
			} else {
				printTestFailed(unitTest.getKey());
			}
		}
	}

	/*
	 * Prints to the terminal that a test failed
	 */
	private static void printTestFailed(String testName) {
		System.out.println(ANSI_RED + "TEST FAILED: " + testName + ANSI_RESET);
	}

	/*
	 * Prints to the terminal that a test passed
	 */
	private static void printTestPassed(String testName) {
		System.out.println(ANSI_GREEN + "TEST PASSED: " + testName + ANSI_RESET);
	}

	/*
	 * Tests the getType method in the symbol class
	 */
	private static boolean testSymGetType() {

		Sym sym = new Sym(TEST_SYM_TYPE);

		return sym.getType().equals(TEST_SYM_TYPE);
	}

	/*
	 * Tests the toString method in the symbol class
	 */
	private static boolean testSymToString() {
	
		Sym sym = new Sym(TEST_SYM_TYPE);

		return sym.toString().equals(TEST_SYM_TYPE);
	}

	/*
	 * Tests the removeScope of the symbol table class
	 */
	private static boolean testSymTableRemoveScope() {
		
		SymTable symTable = new SymTable();

		try {
			symTable.removeScope();
		} catch (EmptySymTableException e) {
			return false;
		}
		try {
			symTable.lookupLocal(TEST_SYM_TYPE);
			return false;
		} catch (EmptySymTableException e) {
			return true;
		}
	}

	/*
	 * Assures an empty table exception is thrown in the addDecl
	 * method of the symbol table class
	 */
	private static boolean testSymTableAddDeclEmptyTable() {

		SymTable symTable = new SymTable();

		Sym sym = new Sym(TEST_SYM_TYPE);

		// Remove only current scope
		try {
			symTable.removeScope();
		} catch (EmptySymTableException e) {
			return false;
		}

		// Assure empty table exception is thrown
		try {
			symTable.addDecl(TEST_SYM_TYPE, sym);
			return false;
		} catch (DuplicateSymException e) {
			return false;
		} catch (EmptySymTableException e) {
			return true;
		}
	}

	/*
	 * Assures a duplicate symbol exception is thrown in the addDecl
	 * method of the symbol table class
	 */
	private static boolean testSymTableAddDeclDuplicateSym() {

		SymTable symTable = new SymTable();

		Sym sym = new Sym(TEST_SYM_TYPE);

		// Insert first symbol
		try {
			symTable.addDecl(TEST_SYM_TYPE, sym);
		} catch (Exception e) {
			return false;
		}

		// Insert the same symbol a second time
		try {
			symTable.addDecl(TEST_SYM_TYPE, sym);
			return false;
		} catch (EmptySymTableException e) {
			return false;
		} catch (DuplicateSymException e) {
			return true;
		}
	}

	/*
	 * Assures the add decl method checks for null arguments
	 */
	private static boolean testSymTableAddDeclNullPointer() {

		SymTable symTable = new SymTable();

		Sym sym = new Sym(TEST_SYM_TYPE);

		try {
			symTable.addDecl(null, sym);
			return false;
		} catch (Exception e) {
			if (!(e instanceof NullPointerException)) {
				return false;
			}
		}
		try {
			symTable.addDecl(TEST_SYM_TYPE, null);
			return false;
		} catch (Exception e) {
			if (!(e instanceof NullPointerException)) {
				return false;
			}
		}
		try {
			symTable.addDecl(null, null);
			return false;
		} catch (Exception e) {
			if (!(e instanceof NullPointerException)) {
				return false;
			}
		}
		try {
			symTable.addDecl(TEST_SYM_TYPE, sym);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/*
	 * Tests the lookupLocal method of the symbol table class
	 */
	private static boolean testSymTableLookupLocal() {

		SymTable symTable = new SymTable();

		Sym sym = new Sym(TEST_SYM_TYPE);

		try {
			symTable.addDecl(TEST_SYM_TYPE, sym);
		} catch (Exception e) {
			return false;
		}
		try {
			Sym foundSym = symTable.lookupLocal(TEST_SYM_TYPE);
			if (!foundSym.getType().equals(sym.getType())) {
				return false;
			} else {
				symTable.addScope();
				try {
					Sym nextSym = symTable.lookupLocal(TEST_SYM_TYPE);
					return nextSym == null;
				} catch (EmptySymTableException e) {
					return false;
				}
			}
		} catch (EmptySymTableException e) {
			return false;
		}
	}

	/*
	 * Tests the lookupGlobal method of the symbol table class
	 */
	private static boolean testSymTableLookupGlobal() {

		SymTable symTable = new SymTable();

		Sym sym = new Sym(TEST_SYM_TYPE);

		try {
			symTable.addDecl(TEST_SYM_TYPE, sym);
		} catch (Exception e) {
			return false;
		}
		try {
			Sym foundSym = symTable.lookupGlobal(TEST_SYM_TYPE);
			if (!foundSym.getType().equals(sym.getType())) {
				return false;
			} else {
				symTable.addScope();
				try {
					Sym nextSym = symTable.lookupGlobal(TEST_SYM_TYPE);
					return nextSym.getType().equals(sym.getType());
				} catch (EmptySymTableException e) {
					return false;
				}
			}
		} catch (EmptySymTableException e) {
			return false;
		}
	}

	/*
	 * Tests the addScope method of the symbol table class
	 */
	private static boolean testSymTableAddScope() {

		SymTable symTable = new SymTable();

		Sym sym = new Sym(TEST_SYM_TYPE);

		try {
			symTable.addDecl(TEST_SYM_TYPE, sym);
		} catch (Exception e) {
			return false;
		}
		try {
			Sym foundSym = symTable.lookupLocal(TEST_SYM_TYPE);
			if (foundSym == null) {
				return false;
			} else {
				symTable.addScope();
				try {
					Sym nextSym = symTable.lookupLocal(TEST_SYM_TYPE);
					return nextSym == null;
				} catch (EmptySymTableException e) {
					return false;
				}
			}
		} catch (EmptySymTableException e) {
			return false;
		}
	}
}
