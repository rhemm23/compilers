import java.util.*;

public class Symb {

  public static int GLOBAL_OFFSET = Integer.MAX_VALUE;

  private int offset;
  private Type type;

  public Symb(Type type) {
    this.type = type;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public int getOffset() {
    return offset;
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
    formalTypes = new LinkedList<Type>();
    this.returnType = returnType;
  }

  public void addFormalType(Type type) {
    formalTypes.add(type);
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

class StructVariableSymbol extends Symb {

  private StructDefinitionSymbol structDefinitionSymbol;

  public StructVariableSymbol(String value, StructDefinitionSymbol structDefinitionSymbol) {
    super(new StructType(value));
    this.structDefinitionSymbol = structDefinitionSymbol;
  }

  public StructDefinitionSymbol getStructDefinitionSymbol() {
    return structDefinitionSymbol;
  }
}

class StructDefinitionSymbol extends Symb {

  private HashMap<String, Symb> members;

  public StructDefinitionSymbol() {
    super(new StructDefinitionType());
    members = new HashMap<String, Symb>();
  }

  public void addMember(String name, Symb symbol) {
    members.put(name, symbol);
  }

  public Symb getMember(String name) {
    return members.get(name);
  }
}
