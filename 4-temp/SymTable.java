import java.util.*;

public class SymTable {

  private List<HashMap<String, Sym>> scopes;

  public SymTable() {
    this.scopes = new LinkedList<HashMap<String, Sym>>();
    this.scopes.add(new HashMap<String, Sym>());
  }

  public void addDecl(String name, Sym sym)
    throws DuplicateSymException,
           EmptySymTableException,
           WrongArgumentException {
    if (name == null && sym == null) {
      throw new WrongArgumentException("Arguments name and sym are null.");
    } else if (name == null) {
      throw new WrongArgumentException("Argument name is null.");
    } else if (sym == null) {
      throw new WrongArgumentException("Argument sym is null.");
    }
    if (this.scopes.isEmpty()) {
      throw new EmptySymTableException();
    }
    HashMap<String, Sym> scope = this.scopes.get(0);
    if (scope.containsKey(name)) {
      throw new DuplicateSymException();
    }
    scope.put(name, sym);
  }

  public void addScope() {
    this.scopes.add(0, new HashMap<String, Sym>());
  }

  public Sym lookupLocal(String name) {
    if (this.scopes.isEmpty()) {
      return null;
    } 
    return this.scopes.get(0).get(name);
  }

  public Sym lookupGlobal(String name) {
    if (this.scopes.isEmpty()) {
      return null;
    }
    for (HashMap<String, Sym> scope : this.scopes) {
      Sym sym = scope.get(name);
      if (sym != null) {
        return sym;
      }
    }
    return null;
  }

  public void removeScope() throws EmptySymTableException {
    if (this.scopes.isEmpty()) {
      throw new EmptySymTableException();
    }
    this.scopes.remove(0);
  }
    
  public void print() {
    System.out.print("\n=== Sym Table ===\n");
    for (HashMap<String, Sym> scope : this.scopes) {
      System.out.println(scope.toString());
    }
    System.out.println();
  }
}
