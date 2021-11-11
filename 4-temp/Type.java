
public abstract class Type { }

class BoolType extends Type {

  public String toString() {
    return "bool";
  }
}

class IntType extends Type {

  public String toString() {
    return "int";
  }
}

class VoidType extends Type {

  public String toString() {
    return "void";
  }
}

class StructType extends Type {

  private String name;

  public StructType(String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }

  public String toString() {
    return name;
  }
}
