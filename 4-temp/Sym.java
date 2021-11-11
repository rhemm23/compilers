import java.util.*;

public abstract class Sym { }

class VariableSym extends Sym {

  private String type;

  public VariableSym(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public String toString() {
    return type;
  }
}

class FunctionSym extends Sym {

  private List<Sym> formalSyms;
  private String returnType;

  public FunctionSym(String returnType) {
    this.returnType = returnType;
  }

  public void setFormalSyms(List<Sym> formalSyms) {
    this.formalSyms = formalSyms;
  }
}

class StructSym extends Sym {

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
