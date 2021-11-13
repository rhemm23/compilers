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

  public boolean isFunctionType() {
    return false;
  }

  public boolean isStructType() {
    return false;
  }

  public boolean isStructDefinitionType() {
    return false;
  }
}

class ErrorType extends Type {

  public boolean isErrorType() {
    return true;
  }

  public boolean equals(Type type) {
    return type.isErrorType();
  }

  public String toString() {
    return "error";
  }
}

class IntType extends Type {

  public boolean isIntType() {
    return true;
  }

  public boolean equals(Type type) {
    return type.isIntType();
  }

  public String toString() {
    return "int";
  }
}

class BoolType extends Type {

  public boolean isBoolType() {
    return true;
  }

  public boolean equals(Type type) {
    return type.isBoolType();
  }

  public String toString() {
    return "bool";
  }
}

class VoidType extends Type {

  public boolean isVoidType() {
    return true;
  }

  public boolean equals(Type type) {
    return type.isVoidType();
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

class FunctionType extends Type {

  public boolean isFunctionType() {
    return true;
  }

  public boolean equals(Type type) {
    return type.isFunctionType();
  }

  public String toString() {
    return "function";
  }
}

class StructType extends Type {

  private String id;
  
  public StructType(String id) {
    this.id = id;
  }
  
  public boolean isStructType() {
    return true;
  }

  public boolean equals(Type type) {
    return type.isStructType() && ((StructType)type).id.equals(id);
  }

  public String toString() {
    return id;
  }
}

class StructDefinitionType extends Type {

  public boolean isStructDefinitionType() {
    return true;
  }

  public boolean equals(Type type) {
    return type.isStructDefinitionType();
  }

  public String toString() {
    return "struct";
  }
}
