import java.util.*;

public class Sym {

  public enum Types {
    STRUCT,
    FUNCTION,
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
      case Types.STRUCT:
        return "struct";

      case Types.FUNCTION:
        return "function";

      case Types.VARIABLE:
        return "variable";
    }
  }
}
