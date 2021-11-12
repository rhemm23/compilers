import java.util.*;

public class Symb {

  private Type type;
  
  public Symb(Type type) {
    this.type = type;
  }
  
  public Type getType() {
    return type;
  }
  
  public String toString() {
    return type.toString();
  }
}

class FnSym extends Symb {

  private List<Type> formalTypes;
  private Type returnType;
    
  public FnSym(Type returnType) {
    super(new FnType());
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

class StructSym extends Symb {

  private IdNode id;

  public StructSym(IdNode id) {
    super(new StructType(id));
    this.id = id;
  }

  public IdNode getStructId() {
    return id;
  }
}

class StructDefSym extends Symb {

  private HashMap<String, Symb> members;

  public StructDefSym() {
    super(new StructDefType());
    members = new HashMap<String, Symb>();
  }

  public void addMember(String name, Symb symbol) {
    members.put(name, symbol);
  }

  public Symb getMember(String name) {
    return members.get(name);
  }
}
