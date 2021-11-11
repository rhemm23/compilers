import java.util.*;

public class Sym {

  public enum Types {
    STRUCT,
    FUNCTION,
    FORMAL,
    VARIABLE,
    STRUCT_VARIABLE
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

      case FUNCTION:
        return "function";

      case FORMAL:
        return "formal";

      case STRUCT_VARIABLE:
        return "struct variable";

      default:
        return "variable";
    }
  }
}

class StructSym extends Sym {

  private SymTable symbolTable;

  public StructSym() {
    super(Sym.Types.STRUCT);
    this.symbolTable = new SymTable();
  }

  public SymTable getSymTable() {
    return this.symbolTable;
  }
}

class StructVariableSym extends Sym {

  private StructSym structSymbol;

  public StructVariableSym(StructSym structSymbol) {
    super(Sym.Types.STRUCT_VARIABLE);
    this.structSymbol = structSymbol;
  }

  public StructSym getStructSym() {
    return this.structSymbol;
  }
}
