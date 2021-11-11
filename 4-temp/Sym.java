import java.util.*;

public abstract class Sym { }

class VariableSym extends Sym {

  private String name;
  private Type type;

  public VariableSym(String name, Type type) {
    this.name = name;
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String toString() {
    return type.toString();
  }
}

class FunctionSym extends Sym {

  private List<VariableSym> parameterSymbols;
  private Type returnType;

  public FunctionSym(Type returnType) {
    this(new LinkedList<VariableSym>(), returnType);
  }

  public FunctionSym(List<VariableSym> parameterSymbols, Type returnType) {
    this.parameterSymbols = parameterSymbols;
    this.returnType = returnType;
  }

  public void addParameterSymbol(VariableSym parameterSymbol) {
    parameterSymbols.add(parameterSymbol);
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < parameterSymbols.size(); i++) {
      if (i > 0) {
        sb.append(',');
      }
      sb.append(parameterSymbols.get(i).toString());
    }
    sb.append("->");
    sb.append(returnType.toString());
    return sb.toString();
  }
}

class StructSym extends Sym {

  private HashMap<String, VariableSym> members;

  public StructSym() {
    members = new HashMap<String, VariableSym>();
  }

  public void addMember(String name, VariableSym variableSymbol) {
    members.put(name, variableSymbol);
  }

  public VariableSym getMember(String name) {
    return members.get(name);
  }
}
