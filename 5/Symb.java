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

  private List<Type> formalTypes;
  private Type returnType;

  public FunctionSymbol(Type returnType) {
    super(new FunctionType());
    this.formalTypes = new LinkedList<Type>();
    this.returnType = returnType;
  }

  public void setFormalTypes(List<Type> formalTypes) {
    this.formalTypes = formalTypes;
  }
    
  public Type getReturnType() {
    return returnType;
  }

  public List<Type> getFormalTypes() {
    return formalTypes;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < formalTypes.size(); i++) {
      if (i > 0) {
        sb.append(',');
      }
      sb.append(formalTypes.get(i).toString());
    }
    sb.append("->");
    sb.append(returnType.toString());
    return sb.toString();
  }
}

class StructSymbol extends Symb {

  private IdNode id;

  public StructSymbol(IdNode id) {
    super(new StructType(id));
    this.id = id;
  }

  public IdNode getStructId() {
    return id;
  }
}

class StructDefinitionSymbol extends Symb {

  private HashMap<String, Type> members;

  public StructDefinitionSymbol() {
    super(new StructDefinitionType());
    members = new HashMap<String, Type>();
  }

  public void addMember(String name, Type type) {
    members.put(name, symbol);
  }

  public Type getMemberType(String name) {
    return members.get(name);
  }
}
