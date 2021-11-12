
/*
 * Base nodes
 */
abstract class ASTNode {

  public abstract void reportError(String error);

  public abstract void analyze(SymTable symbolTable);

  public abstract void unparse(PrintWriter code, int indent);

  protected void addIndent(PrintWriter code, int indent) {
    for (int i = 0; i < indent; i++) {
      code.print(' ');
    }
  }
}

class ProgramNode extends ASTNode {

  private DeclListNode declarations;

  public ProgramNode(DeclListNode declarations) {
    this.declarations = declarations;
  }

  public void reportError(String error) {
    declarations.reportError(error);
  }

  public void unparse(PrintWriter code, int indent) {
    declarations.unparse(code, indent);
  }

  public void analyze() {
    SymTable symbolTable = new SymTable();
    declarations.analyze(symbolTable);
  }
}

class DeclListNode extends ASTNode implements Iterable<DeclNode> {

  private List<DeclNode> declarations;

  public DeclListNode(List<DeclNode> declarations) {
    this.declarations = declarations;
  }

  public Iterator<DeclNode> iterator() {
    return declarations.iterator();
  }

  public void reportError(String error) {
    if (declarations.size() > 0) {
      declarations.get(0).reportError(error);
    }
  }

  public void unparse(PrintWriter code, int indent) {
    for (DeclNode declaration : this) {
      declaration.unparse(code, indent);
    }
  }

  public void analyze(SymTable symbolTable) {
    for (DeclNode declaration : this) {
      declaration.analyze(symbolTable);
    }
  }
}

/*
 * Declaration nodes
 */
abstract class DeclNode extends ASTNode { }

class VarDeclNode extends DeclNode {

  private TypeNode type;
  private IdNode id;

  public VarDeclNode(TypeNode type, IdNode id) {
    this.type = type;
    this.id = id;
  }

  public void reportError(String error) {
    id.reportError();
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    type.unparse(code, 0);
    code.print(' ');
    id.unparse(code, 0);
    code.println(';');
  }

  public void analyze(SymTable symbolTable) {
    
    boolean isVoidType = type instanceof VoidNode;
    boolean isDeclared = symbolTable.lookupLocal(id.getValue()) != null;

    if (isVoidType) {
      reportError("Non-function declared void");
    } else {
      type.analyze(symbolTable);
    }
    if (isDeclared) {
      reportError("Multiply declared identifier");
    }
    if (!isVoidType && !isDeclared) {
      symbolTable.addDeclaration(
        id.getValue(),
        new VariableSymbol(type.getType())
      );
    }
  }
}


