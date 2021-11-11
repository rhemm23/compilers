import java.util.*;

public abstract class Sym { }

public class VariableSym extends Sym {

  private TypeNode type;

  public VariableSym(TypeNode type) {
    this.type = type;
  }

  public TypeNode getType() {
    return type;
  }

  public String toString() {
    return type.toString();
  }
}

public class FunctionSym extends Sym {

  private List<Sym> formalSyms;
  private TypeNode returnType;

  public FunctionSym(TypeNode returnType) {
    this.returnType = returnType;
  }

  public void setFormalSyms(List<Sym> formalSyms) {
    this.formalSyms = formalSyms;
  }
}

public class StructSym extends Sym {

  private HashMap<String, VariableSym> members;

  public StructSym() {
    members = new HashMap<String, VariableSym>();
  }

  public void addMember(String name, VariableSym varSym) {
    members.put(name, varSym);
  }

  public boolean hasMember(String name) {
    return members.containsKey(name);
  }
}
