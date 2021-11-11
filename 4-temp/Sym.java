import java.util.*;

public class Sym {

  public enum Types {
    STRUCT,
    FUNCTION,
    FORMAL,
    VARIABLE
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
