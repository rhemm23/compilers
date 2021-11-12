
public abstract class Type {

  public abstract String toString();

  public abstract boolean equals(Type t);

  public boolean isErrorType() {
    return false;
  }

  public boolean isIntType() {
    return false;
  }

  public boolean isBoolType() {
    return false;
  }

  public boolean isVoidType() {
    return false;
  }

  public boolean isStringType() {
    return false;
  }

  public boolean isFnType() {
    return false;
  }

  public boolean isStructType() {
    return false;
  }

  public boolean isStructDefType() {
    return false;
  }
}

class ErrorType extends Type {

  public boolean isErrorType() {
    return true;
  }

  public boolean equals(Type t) {
    return t.isErrorType();
  }

  public String toString() {
    return "error";
  }
}

class IntType extends Type {

  public boolean isIntType() {
    return true;
  }

  public boolean equals(Type t) {
    return t.isIntType();
  }

  public String toString() {
    return "int";
  }
}

class BoolType extends Type {

  public boolean isBoolType() {
    return true;
  }

  public boolean equals(Type t) {
    return t.isBoolType();
  }

  public String toString() {
    return "bool";
  }
}

class VoidType extends Type {

  public boolean isVoidType() {
    return true;
  }

  public boolean equals(Type t) {
    return t.isVoidType();
  }

  public String toString() {
    return "void";
  }
}

class StringType extends Type {

  public boolean isStringType() {
    return true;
  }

  public boolean equals(Type t) {
    return t.isStringType();
  }

  public String toString() {
    return "String";
  }
}

class FnType extends Type {

  public boolean isFnType() {
    return true;
  }

  public boolean equals(Type t) {
    return t.isFnType();
  }

  public String toString() {
    return "function";
  }
}

class StructType extends Type {

  private IdNode id;
  
  public StructType(IdNode id) {
    this.id = id;
  }
  
  public boolean isStructType() {
    return true;
  }

  public boolean equals(Type t) {
    return t.isStructType();
  }

  public String toString() {
    return id.getValue();
  }
}

class StructDefType extends Type {

  public boolean isStructDefType() {
    return true;
  }

  public boolean equals(Type t) {
    return t.isStructDefType();
  }

  public String toString() {
    return "struct";
  }
}
