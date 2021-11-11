import java.util.*;

public class Sym {

  public enum Types {
    STRUCTDEF,
    STRUCT,
    FUNCTION,
    STANDARD
  }

  private Types type; 
  
  public Sym(Types type) {
    this.type = type;
  }
  
  public Types getType() {
    return type;
  }
  
  public String toString() {
    switch (this.type) {
      case STRUCT:
        return "struct";

      case STRUCTDEF:
        return "structDef";

      case FUNCTION:
        return "function";
      default:
        return "standard";
    }
  }
}

class FnSym extends Sym {
  private List<Sym> formals;
  private Types returnType;

  public FnSym(Types returnType) {
    super(Types.FUNCTION);
    this.returnType = returnType;
  }

  public void addFormals (List<Sym> formals) {
    this.formals = formals;
  }

  public List<Sym> getFormals() {
    return this.formals;
  }

  public Types getReturnType() {
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
    super(Types.STRUCTDEF);
    structSymTab = symTab;
  }

  public SymTable getStructSymTab() {
    return structSymTab;
  }
}

class StructSym extends Sym {
  
  public StructSym() {
    super(Types.STRUCT);
  }
}
