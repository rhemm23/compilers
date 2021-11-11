import java.util.*;

public class Sym {

  private String type; 
  
  public Sym(String type) {
    this.type = type;
  }
  
  public String getType() {
    return type;
  }
  
  public String toString() {
    return type;
  }
}

class FnSym extends Sym {
  private List<Sym> formals;
  private String returnType;

  public FnSym(String returnType) {
    super("function");
    this.returnType = returnType;
  }

  public void addFormals (List<Sym> formals) {
    this.formals = formals;
  }

  public List<Sym> getFormals() {
    return this.formals;
  }

  public String getReturnType() {
    return returnType;
  }

  public String toString() {
    // TODO: for debug
    return "";
  }
}

class StructDefSym extends Sym {
  private SymTable structSymTab;

  public StructDefSym(SymTable symTab) {
    super("structDef");
    structSymTab = symTab;
  }

  public SymTable getStructSymTab() {
    return structSymTab;
  }
}

class StructSym extends Sym {
  
  public StructSym() {
    super("struct");
  }
}
