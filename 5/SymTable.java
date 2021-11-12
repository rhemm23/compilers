import java.util.*;

public class SymTable {

  private List<HashMap<String, Sym>> scopes;
    
  public SymTable() {
    scopes = new LinkedList<HashMap<String, Sym>>();
    addScope();
  }
    
  public void addDeclaration(String name, Sym sym) {        
    if (!scopes.isEmpty() && !scopes.get(0).containsKey(name)) {
      scopes.get(0).put(name, sym);
    }
  }
    
  public void addScope() {
    scopes.add(0, new HashMap<String, Sym>());
  }
    
  public Sym lookupLocal(String name) {
    if (!scopes.isEmpty()) {
      return scopes.get(0).get(name);
    }
    return null;
  }
    
  public Sym lookupGlobal(String name) {
    for (HashMap<String, Sym> scope : scopes) {
      Sym sym = scope.get(name);
      if (sym != null) {
        return sym;
      }
    }
    return null;
  }
    
  public void removeScope() {
    if (!scopes.isEmpty()) {
      scopes.remove(0);
    }
  }
    
  public void print() {
    System.out.println("\n=== Sym Table ===");
    for (HashMap<String, Sym> scope : scopes) {
      System.out.println(scope.toString());
    }
    System.out.println();
  }
}
