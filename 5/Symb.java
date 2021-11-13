import java.util.*;

public abstract class Symb { }

class VariableSymbol extends Symb {

  private Type type;

  public VariableSymbol(Type type) {
    this.type = type;
  }

  public Type getType() {
    return type;
  }
  
  public String toString() {
    return type.toString();
  }
}

class FunctionSymbol extends Symb {

  private List<Symb> formalSymbols;
  private Type returnType;

  public FunctionSymbol(Type returnType) {
    formalSymbols = new LinkedList<Symb>();
    this.returnType = returnType;
  }

  public void addFormalSymbol(Symb symbol) {
    formalSymbols.add(symbol);
  }

  public Type getReturnType() {
    return returnType;
  }

  public List<Symb> getFormalSymbols() {
    return formalSymbols;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < formalSymbols.size(); i++) {
      if (i > 0) {
        sb.append(',');
      }
      sb.append(((VariableSymbol)formalSymbols.get(i)).getType().toString());
    }
    sb.append("->");
    sb.append(returnType.toString());
    return sb.toString();
  }
}

class StructVariableSymbol extends Symb {

  private StructDefinitionSymbol structDefinitionSymbol;

  public StructVariableSymbol(StructDefinitionSymbol structDefinitionSymbol) {
    this.structDefinitionSymbol = structDefinitionSymbol;
  }

  public structDefinitionSymbol getStructDefinitionSymbol() {
    return structDefinitionSymbol;
  }
}

class StructDefinitionSymbol extends Symb {

  private HashMap<String, Symb> members;

  public StructDefinitionSymbol() {
    members = new HashMap<String, Symb>();
  }

  public void addMember(String name, Symb symbol) {
    members.put(name, symbol);
  }

  public Symb getMemberType(String name) {
    return members.get(name);
  }
}
