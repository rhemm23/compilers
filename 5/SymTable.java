import java.util.*;

public class SymTable {

  private List<HashMap<String, Symb>> scopes;
    
  public SymTable() {
    scopes = new LinkedList<HashMap<String, Symb>>();
    addScope();
  }
    
  public void addDeclaration(String name, Symb sym) {        
    if (!scopes.isEmpty() && !scopes.get(0).containsKey(name)) {
      scopes.get(0).put(name, sym);
    }
  }
    
  public void addScope() {
    scopes.add(0, new HashMap<String, Symb>());
  }
    
  public Symb lookupLocal(String name) {
    if (!scopes.isEmpty()) {
      return scopes.get(0).get(name);
    }
    return null;
  }
    
  public Symb lookupGlobal(String name) {
    for (HashMap<String, Symb> scope : scopes) {
      Symb sym = scope.get(name);
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
    System.out.println("\n=== Symb Table ===");
    for (HashMap<String, Symb> scope : scopes) {
      System.out.println(scope.toString());
    }
    System.out.println();
  }
}
