import java.io.*;
import java.net.IDN;
import java.util.*;

// **********************************************************************
// The ASTnode class defines the nodes of the abstract-syntax tree that
// represents a b program.
//
// Internal nodes of the tree contain pointers to children, organized
// either in a list (for nodes that may have a variable number of 
// children) or as a fixed set of fields.
//
// The nodes for literals and ids contain line and character number
// information; for string literals and identifiers, they also contain a
// string; for integer literals, they also contain an integer value.
//
// Here are all the different kinds of AST nodes and what kinds of children
// they have.  All of these kinds of AST nodes are subclasses of "ASTnode".
// Indentation indicates further subclassing:
//
//     Subclass            Kids
//     --------            ----
//     ProgramNode         DeclListNode
//     DeclListNode        linked list of DeclNode
//     DeclNode:
//       VarDeclNode       TypeNode, IdNode, int
//       FnDeclNode        TypeNode, IdNode, FormalsListNode, FnBodyNode
//       FormalDeclNode    TypeNode, IdNode
//       StructDeclNode    IdNode, DeclListNode
//
//     FormalsListNode     linked list of FormalDeclNode
//     FnBodyNode          DeclListNode, StmtListNode
//     StmtListNode        linked list of StmtNode
//     ExpListNode         linked list of ExpNode
//
//     TypeNode:
//       IntNode           --- none ---
//       BoolNode          --- none ---
//       VoidNode          --- none ---
//       StructNode        IdNode
//
//     StmtNode:
//       AssignStmtNode      AssignNode
//       PreIncStmtNode      ExpNode
//       PreDecStmtNode      ExpNode
//       ReceiveStmtNode     ExpNode
//       PrintStmtNode       ExpNode
//       IfStmtNode          ExpNode, DeclListNode, StmtListNode
//       IfElseStmtNode      ExpNode, DeclListNode, StmtListNode,
//                                    DeclListNode, StmtListNode
//       WhileStmtNode       ExpNode, DeclListNode, StmtListNode
//       RepeatStmtNode      ExpNode, DeclListNode, StmtListNode
//       CallStmtNode        CallExpNode
//       ReturnStmtNode      ExpNode
//
//     ExpNode:
//       IntLitNode          --- none ---
//       StrLitNode          --- none ---
//       TrueNode            --- none ---
//       FalseNode           --- none ---
//       IdNode              --- none ---
//       DotAccessNode       ExpNode, IdNode
//       AssignNode          ExpNode, ExpNode
//       CallExpNode         IdNode, ExpListNode
//       UnaryExpNode        ExpNode
//         UnaryMinusNode
//         NotNode
//       BinaryExpNode       ExpNode ExpNode
//         PlusNode     
//         MinusNode
//         TimesNode
//         DivideNode
//         AndNode
//         OrNode
//         EqualsNode
//         NotEqualsNode
//         LessNode
//         GreaterNode
//         LessEqNode
//         GreaterEqNode
//
// Here are the different kinds of AST nodes again, organized according to
// whether they are leaves, internal nodes with linked lists of kids, or
// internal nodes with a fixed number of kids:
//
// (1) Leaf nodes:
//        IntNode,   BoolNode,  VoidNode,  IntLitNode,  StrLitNode,
//        TrueNode,  FalseNode, IdNode
//
// (2) Internal nodes with (possibly empty) linked lists of children:
//        DeclListNode, FormalsListNode, StmtListNode, ExpListNode
//
// (3) Internal nodes with fixed numbers of kids:
//        ProgramNode,     VarDeclNode,     FnDeclNode,      FormalDeclNode,
//        StructDeclNode,  FnBodyNode,      StructNode,      AssignStmtNode,
//        PreIncStmtNode,  PreDecStmtNode,  ReceiveStmtNode, PrintStmtNode,
//        IfStmtNode,      IfElseStmtNode,  WhileStmtNode,   RepeatStmtNode,
//        CallStmtNode,    ReturnStmtNode,  DotAccessNode,   CallExpNode,
//        UnaryExpNode,    BinaryExpNode,   UnaryMinusNode,  NotNode,
//        PlusNode,        MinusNode,       TimesNode,       DivideNode,
//        AndNode,         OrNode,          EqualsNode,      NotEqualsNode,
//        LessNode,        GreaterNode,     LessEqNode,      GreaterEqNode
//
// **********************************************************************

// **********************************************************************
// #### ASTnode class (base class for all other kinds of nodes) ####
// **********************************************************************

abstract class ASTnode {

  // Every subclass must provide an unparse operation
  abstract public void unparse(PrintWriter p, int indent);

  // This method can be used by the unparse methods to add indents
  protected void addIndent(PrintWriter code, int indent) {
    for (int k = 0; k < indent; k++) {
      code.print(" ");
    }
  }
}

// **********************************************************************
// #### ProgramNode, DeclListNode, FormalsListNode, FnBodyNode,
// StmtListNode, ExpListNode ####
// **********************************************************************

class ProgramNode extends ASTnode {

  private DeclListNode declarations;

  public ProgramNode(DeclListNode declarations) {
    this.declarations = declarations;
  }

  public void unparse(PrintWriter code, int indent) {
    declarations.unparse(code, indent);
  }

  public void analyze() {
    SymTable symbolTable = new SymTable();
    declarations.analyze(symbolTable);
  }

  public void typeCheck() {
    declarations.typeCheck();
  }
}

class DeclListNode extends ASTnode implements Iterable<DeclNode> {

  private List<DeclNode> declarations;

  public DeclListNode(List<DeclNode> declarations) {
    this.declarations = declarations;
  }

  public Iterator<DeclNode> iterator() {
    return declarations.iterator();
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

  public void typeCheck() {
    for (DeclNode declaration : this) {
      declaration.typeCheck();
    }
  }
}

class FormalsListNode extends ASTnode implements Iterable<FormalDeclNode> {

  private List<FormalDeclNode> formals;

  public FormalsListNode(List<FormalDeclNode> formals) {
    this.formals = formals;
  }

  public Iterator<FormalDeclNode> iterator() {
    return formals.iterator();
  }

  public void unparse(PrintWriter code, int indent) {
    boolean first = true;
    addIndent(code, indent);
    for (FormalDeclNode formal : this) {
      if (first) {
        first = false;
      } else {
        code.print(", ");
      }
      formal.unparse(code, 0);
    }
  }

  public void analyze(SymTable symbolTable) {
    for (FormalDeclNode formal : this) {
      formal.analyze(symbolTable);
    }
  }
}

class FnBodyNode extends ASTnode {

  private DeclListNode declarations;
  private StmtListNode statements;

  public FnBodyNode(DeclListNode declarations, StmtListNode statements) {
    this.declarations = declarations;
    this.statements = statements;
  }

  public void unparse(PrintWriter code, int indent) {
    code.println("{");
    declarations.unparse(code, indent + 2);
    statements.unparse(code, indent + 2);
    addIndent(code, indent);
    code.println("}");
  }

  public void analyze(SymTable symbolTable) {
    declarations.analyze(symbolTable);
    statements.analyze(symbolTable);
  }
}

class StmtListNode extends ASTnode implements Iterable<StmtNode> {

  private List<StmtNode> statements;

  public StmtListNode(List<StmtNode> statements) {
    this.statements = statements;
  }

  public Iterator<StmtNode> iterator() {
    return statements.iterator();
  }

  public void unparse(PrintWriter code, int indent) {
    for (StmtNode statement : this) {
      statement.unparse(code, indent);
    }
  }

  public void analyze(SymTable symbolTable) {
    for (StmtNode statement : this) {
      statement.analyze(symbolTable);
    }
  }
}

class ExpListNode extends ASTnode implements Iterable<ExpNode> {

  private List<ExpNode> expressions;

  public ExpListNode(List<ExpNode> expressions) {
    this.expressions = expressions;
  }

  public Iterator<ExpNode> iterator() {
    return expressions.iterator();
  }

  public void unparse(PrintWriter code, int indent) {
    boolean first = true;
    addIndent(code, indent);
    for (ExpNode expression : this) {
      if (first) {
        first = false;
      } else {
        code.print(", ");
      }
      if (expression instanceof AssignNode) {
        code.print("(");
        expression.unparse(code, 0);
        code.print(")");
      } else {
        expression.unparse(code, 0);
      }
    }
  }

  public void analyze(SymTable symbolTable) {
    for (ExpNode expression : this) {
      expression.analyze(symbolTable);
    }
  }
}

// **********************************************************************
// #### DeclNode and its subclasses ####
// **********************************************************************

abstract class DeclNode extends ASTnode {

  public void typeCheck() { }

  public abstract void analyze(SymTable symbolTable);
}

class VarDeclNode extends DeclNode {

  public static int NOT_STRUCT = -1;

  private VariableSym sym;
  private TypeNode type;
  private IdNode id;
  private int size;

  public VarDeclNode(TypeNode type, IdNode id, int size) {
    this.type = type;
    this.size = size;
    this.id = id;
  }
  
  public String name() {
    return id.name();
  }

  public VariableSym getSym() {
    return sym;
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    type.unparse(code, 0);
    code.print(" ");
    id.unparse(code, 0);
    code.println(";");
  }

  public void analyze(SymTable symbolTable) {
    boolean validDeclaration = true;
    if (type instanceof StructNode) {
      StructNode structNode = (StructNode)type;
      Sym structSym = symbolTable.lookupGlobal(structNode.name());
      if (structSym == null || !(structSym instanceof StructSym)) {
        id.fatalError("Invalid name of struct type");
        validDeclaration = false;
      }
    } else if (type instanceof VoidNode) {
      id.fatalError("Non-function declared void");
      validDeclaration = false;
    }
    if (symbolTable.lookupLocal(id.name()) != null) {
      id.fatalError("Multiply declared identifier");
      validDeclaration = false;
    }
    if (validDeclaration) {
      try {
        symbolTable.addDecl(id.name(), sym = new VariableSym(id.name(), type.getType()));
      } catch (Exception e) {
        System.err.println("Unexpected error");
        System.exit(-1);
      }
    }
  }
}

class FnDeclNode extends DeclNode {

  private FormalsListNode formals;
  private FnBodyNode body;
  private TypeNode type;
  private IdNode id;

  public FnDeclNode(
    TypeNode type,
    IdNode id,
    FormalsListNode formals,
    FnBodyNode body
  ) {
    this.formals = formals;
    this.body = body;
    this.type = type;
    this.id = id;
  }

  public void typeCheck() {
    body.typeCheck();
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    type.unparse(code, 0);
    code.print(" ");
    id.unparse(code, 0);
    code.print(" (");
    formals.unparse(code, 0);
    code.print(") ");
    body.unparse(code, indent);
  }

  public void analyze(SymTable symbolTable) {
    FunctionSym functionSym = new FunctionSym(type.getType());
    if (symbolTable.lookupLocal(id.name()) != null) {
      id.fatalError("Multiply declared identifier");
    } else {
      try {
        symbolTable.addDecl(id.name(), functionSym);
      } catch (Exception e) {
        System.err.println("Unexpected error");
        System.exit(-1);
      }
    }
    symbolTable.addScope();
    formals.analyze(symbolTable);
    for (FormalDeclNode formal : formals) {
      if (formal.getSym() != null) {
        functionSym.addParameterSymbol(formal.getSym());
      }
    }
    body.analyze(symbolTable);
    try {
      symbolTable.removeScope();
    } catch (Exception e) {
      System.err.println("Unexpected error");
      System.exit(-1);
    }
  }
}

class FormalDeclNode extends DeclNode {

  private VariableSym sym;
  private TypeNode type;
  private IdNode id;

  public FormalDeclNode(TypeNode type, IdNode id) {
    this.type = type;
    this.id = id;
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    type.unparse(code, 0);
    code.print(" ");
    id.unparse(code, 0);
  }

  public VariableSym getSym() {
    return sym;
  }

  public void analyze(SymTable symbolTable) {
    boolean validDeclaration = true;
    if (type instanceof VoidNode) {
      id.fatalError("Non-function declared void");
      validDeclaration = false;
    }
    if (symbolTable.lookupLocal(id.name()) != null) {
      id.fatalError("Multiply declared identifier");
      validDeclaration = false;
    }
    if (validDeclaration) {
      try {
        symbolTable.addDecl(id.name(), sym = new VariableSym(id.name(), type.getType()));
      } catch (Exception e) {
        System.err.println("Unexpected error");
        System.exit(-1);
      }
    }
  }
}

class StructDeclNode extends DeclNode {

  private DeclListNode declarations;
  private StructSym sym;
  private IdNode id;

  public StructDeclNode(IdNode id, DeclListNode declarations) {
    this.declarations = declarations;
    this.id = id;
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("struct ");
    id.unparse(code, 0);
    code.println(" {");
    declarations.unparse(code, indent + 2);
    addIndent(code, indent);
    code.println("}");
  }

  public void analyze(SymTable symbolTable) {
    if (symbolTable.lookupLocal(id.name()) != null) {
      id.fatalError("Multiply declared identifier");
    } else {
      try {
        symbolTable.addDecl(id.name(), sym = new StructSym());
      } catch (Exception e) {
        System.err.println("Unexpected error");
        System.exit(-1);
      }
    }
    symbolTable.addScope();
    for (DeclNode declaration : declarations) {
      VarDeclNode varDecl = (VarDeclNode)declaration;
      varDecl.analyze(symbolTable);
      if (varDecl.getSym() != null) {
        sym.addMember(varDecl.name(), varDecl.getSym());
      }
    }
    try {
      symbolTable.removeScope();
    } catch (Exception e) {
      System.err.println("Unexpected error");
      System.exit(-1);
    }
  }
}

// **********************************************************************
// #### TypeNode and its Subclasses ####
// **********************************************************************

abstract class TypeNode extends ASTnode {

  public abstract Type getType();
}

class IntNode extends TypeNode {

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("int");
  }

  public Type getType() {
    return new IntType();
  }
}

class BoolNode extends TypeNode {

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("bool");
  }

  public Type getType() {
    return new BoolType();
  }
}

class VoidNode extends TypeNode {

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("void");
  }

  public Type getType() {
    return new VoidType();
  }
}

class StructNode extends TypeNode {

  private IdNode id;

  public StructNode(IdNode id) {
    this.id = id;
  }

  public String name() {
    return id.name();
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("struct ");
    id.unparse(code, 0);
  }

  public Type getType() {
    return new StructType(id.name());
  }
}

// **********************************************************************
// #### StmtNode and its subclasses ####
// **********************************************************************

abstract class StmtNode extends ASTnode {

  public abstract void typeCheck();

  public abstract void analyze(SymTable symbolTable);
}

class AssignStmtNode extends StmtNode {

  private AssignNode assign;

  public AssignStmtNode(AssignNode assign) {
    this.assign = assign;
  }

  public void typeCheck() {
    assign.typeCheck();
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    assign.unparse(code, 0);
    code.println(';');
  }

  public void analyze(SymTable symbolTable) {
    assign.analyze(symbolTable);
  }
}

class PreIncStmtNode extends StmtNode {

  private ExpNode exp;

  public PreIncStmtNode(ExpNode exp) {
    this.exp = exp;
  }

  public Type typeCheck() {
    Type type = exp.typeCheck();
    if (type.isErrorType() || type.isIntType()) {
      return type;
    }
    exp.reportError("Arithmetic operator applied to non-numeric operand");
    return new ErrorType();
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("++");
    exp.unparse(code, 0);
    code.println(';');
  }

  public void analyze(SymTable symbolTable) {
    exp.analyze(symbolTable);
  }
}

class PreDecStmtNode extends StmtNode {

  private ExpNode exp;

  public PreDecStmtNode(ExpNode exp) {
    this.exp = exp;
  }

  public Type typeCheck() {
    Type type = exp.typeCheck();
    if (type.isErrorType() || type.isIntType()) {
      return type;
    }
    exp.reportError("Arithmetic operator applied to non-numeric operand");
    return new ErrorType();
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("--");
    exp.unparse(code, 0);
    code.println(';');
  }

  public void analyze(SymTable symbolTable) {
    exp.analyze(symbolTable);
  }
}

class ReceiveStmtNode extends StmtNode {

  private ExpNode expression;

  public ReceiveStmtNode(ExpNode expression) {
    this.expression = expression;
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("receive >> ");
    expression.unparse(code, 0);
    code.println(';');
  }

  public void analyze(SymTable symbolTable) {
    expression.analyze(symbolTable);
  }
}

class PrintStmtNode extends StmtNode {

  private ExpNode expression;

  public PrintStmtNode(ExpNode expression) {
    this.expression = expression;
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("print << ");
    if (expression instanceof AssignNode) {
      code.print("(");
      expression.unparse(code, 0);
      code.print(")");
    } else {
      expression.unparse(code, 0);
    }
    code.println(';');
  }

  public void typeCheck() {
    Type type = expression.typeCheck();
    if (type.isStructType()) {
      IdNode 
      if (expression instanceof IdNode) {

      } else {
        DotAccessExpNode dotAccess = (DotAccessExpNode)expression;
      }
      ErrMsg.fatal()
    }
  }

  public void analyze(SymTable symbolTable) {
    expression.analyze(symbolTable);
  }
}

class IfStmtNode extends StmtNode {

  private DeclListNode declarations;
  private StmtListNode statements;
  private ExpNode expression;

  public IfStmtNode(
    ExpNode expression,
    DeclListNode declarations,
    StmtListNode statements
  ) {
    this.declarations = declarations;
    this.statements = statements;
    this.expression = expression;
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("if (");
    if (expression instanceof AssignNode) {
      code.print("(");
      expression.unparse(code, 0);
      code.print(")");
    } else {
      expression.unparse(code, 0);
    }
    code.println(") {");
    declarations.unparse(code, indent + 2);
    statements.unparse(code, indent + 2);
    addIndent(code, indent);
    code.println("}");
  }

  public void analyze(SymTable symbolTable) {
    expression.analyze(symbolTable);
    symbolTable.addScope();
    declarations.analyze(symbolTable);
    statements.analyze(symbolTable);
    try {
      symbolTable.removeScope();
    } catch (Exception e) {
      System.err.println("Encountered an unexpected error");
      System.exit(-1);
    }
  }
}

class IfElseStmtNode extends StmtNode {

  private DeclListNode thenDeclarations;
  private DeclListNode elseDeclarations;
  private StmtListNode thenStatements;
  private StmtListNode elseStatements;
  private ExpNode expression;

  public IfElseStmtNode(
    ExpNode expression,
    DeclListNode thenDeclarations,
    StmtListNode thenStatements,
    DeclListNode elseDeclarations,
    StmtListNode elseStatements
  ) {
    this.thenDeclarations = thenDeclarations;
    this.elseDeclarations = elseDeclarations;
    this.thenStatements = thenStatements;
    this.elseStatements = elseStatements;
    this.expression = expression;
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("if (");
    if (expression instanceof AssignNode) {
      code.print("(");
      expression.unparse(code, 0);
      code.print(")");
    } else {
      expression.unparse(code, 0);
    }
    code.println(") {");
    thenDeclarations.unparse(code, indent + 2);
    thenStatements.unparse(code, indent + 2);
    addIndent(code, indent);
    code.println("} else {");
    elseDeclarations.unparse(code, indent + 2);
    elseStatements.unparse(code, indent + 2);
    addIndent(code, indent);
    code.println("}");
  }

  public void analyze(SymTable symbolTable) {
    expression.analyze(symbolTable);
    symbolTable.addScope();
    thenDeclarations.analyze(symbolTable);
    thenStatements.analyze(symbolTable);
    try {
      symbolTable.removeScope();
    } catch (Exception e) {
      System.err.println("Encountered an unexpected error");
      System.exit(-1);
    }
    symbolTable.addScope();
    elseDeclarations.analyze(symbolTable);
    elseStatements.analyze(symbolTable);
    try {
      symbolTable.removeScope();
    } catch (Exception e) {
      System.err.println("Encountered an unexpected error");
      System.exit(-1);
    }
  }
}

class WhileStmtNode extends StmtNode {

  private DeclListNode declarations;
  private StmtListNode statements;
  private ExpNode expression;

  public WhileStmtNode(
    ExpNode expression,
    DeclListNode declarations,
    StmtListNode statements
  ) {
    this.declarations = declarations;
    this.statements = statements;
    this.expression = expression;
  }
	
  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("while (");
    if (expression instanceof AssignNode) {
      code.print("(");
      expression.unparse(code, 0);
      code.print(")");
    } else {
      expression.unparse(code, 0);
    }
    code.println(") {");
    declarations.unparse(code, indent + 2);
    statements.unparse(code, indent + 2);
    addIndent(code, indent);
    code.println("}");
  }

  public void analyze(SymTable symbolTable) {
    expression.analyze(symbolTable);
    symbolTable.addScope();
    declarations.analyze(symbolTable);
    statements.analyze(symbolTable);
    try {
      symbolTable.removeScope();
    } catch (Exception e) {
      System.err.println("Encountered an unexpected error");
      System.exit(-1);
    }
  }
}

class RepeatStmtNode extends StmtNode {

  private DeclListNode declarations;
  private StmtListNode statements;
  private ExpNode expression;

  public RepeatStmtNode(
    ExpNode expression,
    DeclListNode declarations,
    StmtListNode statements
  ) {
    this.declarations = declarations;
    this.statements = statements;
    this.expression = expression;
  }
	
  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("repeat (");
    if (expression instanceof AssignNode) {
      code.print("(");
      expression.unparse(code, 0);
      code.print(")");
    } else {
      expression.unparse(code, 0);
    }
    code.println(") {");
    declarations.unparse(code, indent + 2);
    statements.unparse(code, indent + 2);
    addIndent(code, indent);
    code.println("}");
  }

  public void analyze(SymTable symbolTable) {
    expression.analyze(symbolTable);
    symbolTable.addScope();
    declarations.analyze(symbolTable);
    statements.analyze(symbolTable);
    try {
      symbolTable.removeScope();
    } catch (Exception e) {
      System.err.println("Encountered an unexpected error");
      System.exit(-1);
    }
  }
}

class CallStmtNode extends StmtNode {

  private CallExpNode callExpression;

  public CallStmtNode(CallExpNode callExpression) {
    this.callExpression = callExpression;
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    callExpression.unparse(code, 0);
    code.println(';');
  }

  public void analyze(SymTable symbolTable) {
    callExpression.analyze(symbolTable);
  }
}

class ReturnStmtNode extends StmtNode {

  private ExpNode expression;

  public ReturnStmtNode(ExpNode expression) {
    this.expression = expression;
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("ret");
    if (expression != null) {
      code.print(" ");
      if (expression instanceof AssignNode) {
        code.print("(");
        expression.unparse(code, 0);
        code.print(")");
      } else {
        expression.unparse(code, 0);
      }
    }
    code.println(';');
  }

  public void analyze(SymTable symbolTable) {
    expression.analyze(symbolTable);
  }
}

// **********************************************************************
// #### ExpNode and its subclasses ####
// **********************************************************************

abstract class ExpNode extends ASTnode {

  public abstract Type typeCheck();

  public void analyze(SymTable symbolTable) { }

  public abstract void reportError(String error);
}

class IntLitNode extends ExpNode {

  private int charNum;
  private int lineNum;
  private int value;

  public IntLitNode(int lineNum, int charNum, int value) {
    this.charNum = charNum;
    this.lineNum = lineNum;
    this.value = value;
  }

  public void reportError(String error) {
    ErrMsg.fatal(lineNum, charNum, error);
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print(value);
  }

  public Type typeCheck() {
    return new IntType();
  }
}

class StringLitNode extends ExpNode {

  private String value;
  private int charNum;
  private int lineNum;

  public StringLitNode(int lineNum, int charNum, String value) {
    this.charNum = charNum;
    this.lineNum = lineNum;
    this.value = value;
  }

  public void reportError(String error) {
    ErrMsg.fatal(lineNum, charNum, error);
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.printf(value);
  }

  public Type typeCheck() {
    return new StringType();
  }
}

class TrueNode extends ExpNode {

  private int charNum;
  private int lineNum;

  public TrueNode(int lineNum, int charNum) {
    this.charNum = charNum;
    this.lineNum = lineNum;
  }

  public void reportError(String error) {
    ErrMsg.fatal(lineNum, charNum, error);
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("true");
  }

  public Type typeCheck() {
    return new BoolType();
  }
}

class FalseNode extends ExpNode {

  private int charNum;
  private int lineNum;

  public FalseNode(int lineNum, int charNum) {
    this.charNum = charNum;
    this.lineNum = lineNum;
  }

  public void reportError(String error) {
    ErrMsg.fatal(lineNum, charNum, error);
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("false");
  }

  public Type typeCheck() {
    return new BoolType();
  }
}

class IdNode extends ExpNode {

  private String value;
  private int charNum;
  private int lineNum;
  private Sym sym;

  public IdNode(int lineNum, int charNum, String value) {
    this.lineNum = lineNum;
    this.charNum = charNum;
    this.value = value;
  }

  public void reportError(String error) {
    ErrMsg.fatal(lineNum, charNum, error);
  }

  public int lineNum() {
    return lineNum;
  }

  public int charNum() {
    return charNum;
  }

  public String name() {
    return value;
  }

  public void linkSymbol(Sym symbol) {
    sym = symbol;
  }

  public void analyze(SymTable symbolTable) {
    Sym symbol = symbolTable.lookupGlobal(value);
    if (symbol == null) {
      fatalError("Undeclared identifier");
    } else {
      linkSymbol(symbol);
    }
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print(value);
    if (sym != null) {
      code.print('(');
      code.print(sym.toString());
      code.print(')');
    }
  }

  public Type typeCheck() {
    return sym.getType();
  }
}

class DotAccessExpNode extends ExpNode {

  private ExpNode accessor;
  private IdNode id;

  public DotAccessExpNode(ExpNode accessor, IdNode id) {
    this.accessor = accessor;
    this.id = id;
  }

  public void reportError(String error) {
    accessor.reportError(error);
  }

  public IdNode getId() {
    return id;
  }

  public Type typeCheck() {
    return id.typeCheck();
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    accessor.unparse(code, 0);
    code.print('.');
    id.unparse(code, 0);
  }

  public void analyze(SymTable symbolTable) {
    IdNode structId = null;
    if (accessor instanceof IdNode) {
      structId = (IdNode)accessor;
    } else {
      DotAccessExpNode dotAccessNode = (DotAccessExpNode)accessor;
      structId = dotAccessNode.id;
    }
    Sym symbol = symbolTable.lookupGlobal(structId.name());
    if (symbol == null || !(symbol instanceof VariableSym)) {
      structId.fatalError("Undeclared identifier");
    } else {
      VariableSym variableSym = (VariableSym)symbol;
      Type variableType = variableSym.getType();
      if (variableType instanceof StructType) {
        StructType structType = (StructType)variableType;
        StructSym structSym = (StructSym)symbolTable.lookupGlobal(structType.name());
        VariableSym structMember = structSym.getMember(id.name());
        if (structMember != null) {
          id.linkSymbol(structMember);
        } else {
          id.fatalError("Invalid struct field name");
        }
      } else {
        structId.fatalError("Dot-access of non-struct type");
      }
    }
    accessor.analyze(symbolTable);
  }
}

class AssignNode extends ExpNode {

  private ExpNode leftHandSide;
  private ExpNode expression;

  public AssignNode(ExpNode leftHandSide, ExpNode expression) {
    this.leftHandSide = leftHandSide;
    this.expression = expression;
  }

  public void reportError(String error) {
    leftHandSide.reportError(error)
  }

  public Type typeCheck() {
    Type leftType = leftHandSide.typeCheck();
    Type rightType = expression.typeCheck();
    if (leftType.isErrorType()) {
      return leftType;
    } else if (rightType.isErrorType()) {
      return rightType;
    } else if (!leftType.equals(rightType)) {
      reportError("Type mismatch");
      return new ErrorType();
    } else if (leftType.isFnType()) {
      reportError("Function assignment");
      return new ErrorType();
    } else if (leftType.isStructDefType()) {
      reportError("Struct name assignment");
      return new ErrorType();
    } else if (leftType.isStructType()) {
      reportError("Struct variable assignment");
      return new ErrorType();
    } else {
      return leftType;
    }
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    leftHandSide.unparse(code, 0);
    code.print(" = ");
    if (expression instanceof AssignNode) {
      code.print("(");
      expression.unparse(code, 0);
      code.print(")");
    } else {
      expression.unparse(code, 0);
    }
  }

  public void analyze(SymTable symbolTable) {
    leftHandSide.analyze(symbolTable);
    expression.analyze(symbolTable);
  }
}

class CallExpNode extends ExpNode {

  private ExpListNode parameterExpressions;
  private IdNode methodId;

  public CallExpNode(IdNode methodId, ExpListNode parameterExpressions) {
    this.parameterExpressions = parameterExpressions;
    this.methodId = methodId;
  }

  public CallExpNode(IdNode methodId) {
    this.parameterExpressions = new ExpListNode(new LinkedList<ExpNode>());
    this.methodId = methodId;
  }

  public void reportError(String error) {
    methodId.reportError(error);
  }

  public Type typeCheck() {
    // TODO
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    methodId.unparse(code, 0);
    code.print('(');
    parameterExpressions.unparse(code, 0);
    code.print(')');
  }

  public void analyze(SymTable symbolTable) {
    Sym symbol = symbolTable.lookupGlobal(methodId.name());
    if (symbol == null || !(symbol instanceof FunctionSym)) {
      methodId.fatalError("Undeclared identifier");
    } else {
      methodId.linkSymbol(symbol);
    }
    parameterExpressions.analyze(symbolTable);
  }
}

abstract class UnaryExpNode extends ExpNode {

  protected ExpNode exp;

  public UnaryExpNode(ExpNode exp) {
    this.exp = exp;
  }

  public void reportError(String error) {
    exp.reportError(error);
  }

  public void analyze(SymTable symbolTable) {
    exp.analyze(symbolTable);
  }
}

abstract class BinaryExpNode extends ExpNode {

  protected ExpNode exp1;
  protected ExpNode exp2;

  public BinaryExpNode(ExpNode exp1, ExpNode exp2) {
    this.exp1 = exp1;
    this.exp2 = exp2;
  }

  public void reportError(String error) {
    exp1.reportError(error);
  }

  public void analyze(SymTable symbolTable) {
    exp1.analyze(symbolTable);
    exp2.analyze(symbolTable);
  }
}

// **********************************************************************
// #### Subclasses of UnaryExpNode ####
// **********************************************************************

class UnaryMinusNode extends UnaryExpNode {

  public UnaryMinusNode(ExpNode exp) {
    super(exp);
  }

  public Type typeCheck() {
    Type type = exp.typeCheck();
    if (type.isErrorType() || type.isIntType()) {
      return type;
    }
    exp.reportError("Arithmetic operator applied to non-numeric operand");
    return new ErrorType();
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("(-");
    if (exp instanceof AssignNode) {
      code.print("(");
      exp.unparse(code, 0);
      code.print(")");
    } else {
      exp.unparse(code, 0);
    }
    code.print(")");
  }
}

class NotNode extends UnaryExpNode {

  public NotNode(ExpNode exp) {
    super(exp);
  }

  public Type typeCheck() {
    Type type = exp.typeCheck();
    if (type.isErrorType() || type.isBoolType()) {
      return type;
    }
    exp.reportError("Logical operator applied to non-bool operand");
    return new ErrorType();
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("(!");
    if (exp instanceof AssignNode) {
      code.print("(");
      exp.unparse(code, 0);
      code.print(")");
    } else {
      exp.unparse(code, 0);
    }
    code.print(")");
  }
}

// **********************************************************************
// #### Subclasses of BinaryExpNode ####
// **********************************************************************

class PlusNode extends BinaryExpNode {

  public PlusNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public Type typeCheck() {
    Type leftType = exp1.typeCheck();
    Type rightType = exp2.typeCheck();
    if (leftType.isErrorType()) {
      return leftType;
    } else if (rightType.isErrorType()) {
      return rightType;
    } else if (!leftType.isIntType()) {
      exp1.reportError("Arithmetic operator applied to non-numeric operand");
      return new ErrorType();
    } else if (!rightType.isIntType()) {
      exp2.reportError("Arithmetic operator applied to non-numeric operand");
      return new ErrorType();
    }
    return new IntType();
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("(");
    if (exp1 instanceof AssignNode) {
      code.print("(");
      exp1.unparse(code, 0);
      code.print(")");
    } else {
      exp1.unparse(code, 0);
    }
    code.print(" + ");
    if (exp2 instanceof AssignNode) {
      code.print("(");
      exp2.unparse(code, 0);
      code.print(")");
    } else {
      exp2.unparse(code, 0);
    }
    code.print(")");
  }
}

class MinusNode extends BinaryExpNode {

  public MinusNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public Type typeCheck() {
    Type leftType = exp1.typeCheck();
    Type rightType = exp2.typeCheck();
    if (leftType.isErrorType()) {
      return leftType;
    } else if (rightType.isErrorType()) {
      return rightType;
    } else if (!leftType.isIntType()) {
      exp1.reportError("Arithmetic operator applied to non-numeric operand");
      return new ErrorType();
    } else if (!rightType.isIntType()) {
      exp2.reportError("Arithmetic operator applied to non-numeric operand");
      return new ErrorType();
    }
    return new IntType();
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("(");
    if (exp1 instanceof AssignNode) {
      code.print("(");
      exp1.unparse(code, 0);
      code.print(")");
    } else {
      exp1.unparse(code, 0);
    }
    code.print(" - ");
    if (exp2 instanceof AssignNode) {
      code.print("(");
      exp2.unparse(code, 0);
      code.print(")");
    } else {
      exp2.unparse(code, 0);
    }
    code.print(")");
  }
}

class TimesNode extends BinaryExpNode {

  public TimesNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public Type typeCheck() {
    Type leftType = exp1.typeCheck();
    Type rightType = exp2.typeCheck();
    if (leftType.isErrorType()) {
      return leftType;
    } else if (rightType.isErrorType()) {
      return rightType;
    } else if (!leftType.isIntType()) {
      exp1.reportError("Arithmetic operator applied to non-numeric operand");
      return new ErrorType();
    } else if (!rightType.isIntType()) {
      exp2.reportError("Arithmetic operator applied to non-numeric operand");
      return new ErrorType();
    }
    return new IntType();
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("(");
    if (exp1 instanceof AssignNode) {
      code.print("(");
      exp1.unparse(code, 0);
      code.print(")");
    } else {
      exp1.unparse(code, 0);
    }
    code.print(" * ");
    if (exp2 instanceof AssignNode) {
      code.print("(");
      exp2.unparse(code, 0);
      code.print(")");
    } else {
      exp2.unparse(code, 0);
    }
    code.print(")");
  }
}

class DivideNode extends BinaryExpNode {

  public DivideNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public Type typeCheck() {
    Type leftType = exp1.typeCheck();
    Type rightType = exp2.typeCheck();
    if (leftType.isErrorType()) {
      return leftType;
    } else if (rightType.isErrorType()) {
      return rightType;
    } else if (!leftType.isIntType()) {
      exp1.reportError("Arithmetic operator applied to non-numeric operand");
      return new ErrorType();
    } else if (!rightType.isIntType()) {
      exp2.reportError("Arithmetic operator applied to non-numeric operand");
      return new ErrorType();
    }
    return new IntType();
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("(");
    if (exp1 instanceof AssignNode) {
      code.print("(");
      exp1.unparse(code, 0);
      code.print(")");
    } else {
      exp1.unparse(code, 0);
    }
    code.print(" / ");
    if (exp2 instanceof AssignNode) {
      code.print("(");
      exp2.unparse(code, 0);
      code.print(")");
    } else {
      exp2.unparse(code, 0);
    }
    code.print(")");
  }
}

class AndNode extends BinaryExpNode {

  public AndNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public Type typeCheck() {
    Type leftType = exp1.typeCheck();
    Type rightType = exp2.typeCheck();
    if (leftType.isErrorType()) {
      return leftType;
    } else if (rightType.isErrorType()) {
      return rightType;
    } else if (!leftType.isBoolType()) {
      exp1.reportError("Logical operator applied to non-bool operand");
      return new ErrorType();
    } else if (!rightType.isBoolType()) {
      exp2.reportError("Logical operator applied to non-bool operand");
      return new ErrorType();
    }
    return new BoolType();
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("(");
    if (exp1 instanceof AssignNode) {
      code.print("(");
      exp1.unparse(code, 0);
      code.print(")");
    } else {
      exp1.unparse(code, 0);
    }
    code.print(" && ");
    if (exp2 instanceof AssignNode) {
      code.print("(");
      exp2.unparse(code, 0);
      code.print(")");
    } else {
      exp2.unparse(code, 0);
    }
    code.print(")");
  }
}

class OrNode extends BinaryExpNode {

  public OrNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public Type typeCheck() {
    Type leftType = exp1.typeCheck();
    Type rightType = exp2.typeCheck();
    if (leftType.isErrorType()) {
      return leftType;
    } else if (rightType.isErrorType()) {
      return rightType;
    } else if (!leftType.isBoolType()) {
      exp1.reportError("Logical operator applied to non-bool operand");
      return new ErrorType();
    } else if (!rightType.isBoolType()) {
      exp2.reportError("Logical operator applied to non-bool operand");
      return new ErrorType();
    }
    return new BoolType();
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("(");
    if (exp1 instanceof AssignNode) {
      code.print("(");
      exp1.unparse(code, 0);
      code.print(")");
    } else {
      exp1.unparse(code, 0);
    }
    code.print(" || ");
    if (exp2 instanceof AssignNode) {
      code.print("(");
      exp2.unparse(code, 0);
      code.print(")");
    } else {
      exp2.unparse(code, 0);
    }
    code.print(")");
  }
}

class EqualsNode extends BinaryExpNode {

  public EqualsNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("(");
    if (exp1 instanceof AssignNode) {
      code.print("(");
      exp1.unparse(code, 0);
      code.print(")");
    } else {
      exp1.unparse(code, 0);
    }
    code.print(" == ");
    if (exp2 instanceof AssignNode) {
      code.print("(");
      exp2.unparse(code, 0);
      code.print(")");
    } else {
      exp2.unparse(code, 0);
    }
    code.print(")");
  }
}

class NotEqualsNode extends BinaryExpNode {

  public NotEqualsNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("(");
    if (exp1 instanceof AssignNode) {
      code.print("(");
      exp1.unparse(code, 0);
      code.print(")");
    } else {
      exp1.unparse(code, 0);
    }
    code.print(" != ");
    if (exp2 instanceof AssignNode) {
      code.print("(");
      exp2.unparse(code, 0);
      code.print(")");
    } else {
      exp2.unparse(code, 0);
    }
    code.print(")");
  }
}

class LessNode extends BinaryExpNode {

  public LessNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public Type typeCheck() {
    Type leftType = exp1.typeCheck();
    Type rightType = exp2.typeCheck();
    if (leftType.isErrorType()) {
      return leftType;
    } else if (rightType.isErrorType()) {
      return rightType;
    } else if (!leftType.isIntType()) {
      exp1.reportError("Relational operator applied to non-numeric operand");
      return new ErrorType();
    } else if (!rightType.isIntType()) {
      exp2.reportError("Relational operator applied to non-numeric operand");
      return new ErrorType();
    }
    return new BoolType();
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("(");
    if (exp1 instanceof AssignNode) {
      code.print("(");
      exp1.unparse(code, 0);
      code.print(")");
    } else {
      exp1.unparse(code, 0);
    }
    code.print(" < ");
    if (exp2 instanceof AssignNode) {
      code.print("(");
      exp2.unparse(code, 0);
      code.print(")");
    } else {
      exp2.unparse(code, 0);
    }
    code.print(")");
  }
}

class GreaterNode extends BinaryExpNode {

  public GreaterNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public Type typeCheck() {
    Type leftType = exp1.typeCheck();
    Type rightType = exp2.typeCheck();
    if (leftType.isErrorType()) {
      return leftType;
    } else if (rightType.isErrorType()) {
      return rightType;
    } else if (!leftType.isIntType()) {
      exp1.reportError("Relational operator applied to non-numeric operand");
      return new ErrorType();
    } else if (!rightType.isIntType()) {
      exp2.reportError("Relational operator applied to non-numeric operand");
      return new ErrorType();
    }
    return new BoolType();
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("(");
    if (exp1 instanceof AssignNode) {
      code.print("(");
      exp1.unparse(code, 0);
      code.print(")");
    } else {
      exp1.unparse(code, 0);
    }
    code.print(" > ");
    if (exp2 instanceof AssignNode) {
      code.print("(");
      exp2.unparse(code, 0);
      code.print(")");
    } else {
      exp2.unparse(code, 0);
    }
    code.print(")");
  }
}

class LessEqNode extends BinaryExpNode {

  public LessEqNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public Type typeCheck() {
    Type leftType = exp1.typeCheck();
    Type rightType = exp2.typeCheck();
    if (leftType.isErrorType()) {
      return leftType;
    } else if (rightType.isErrorType()) {
      return rightType;
    } else if (!leftType.isIntType()) {
      exp1.reportError("Relational operator applied to non-numeric operand");
      return new ErrorType();
    } else if (!rightType.isIntType()) {
      exp2.reportError("Relational operator applied to non-numeric operand");
      return new ErrorType();
    }
    return new BoolType();
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("(");
    if (exp1 instanceof AssignNode) {
      code.print("(");
      exp1.unparse(code, 0);
      code.print(")");
    } else {
      exp1.unparse(code, 0);
    }
    code.print(" <= ");
    if (exp2 instanceof AssignNode) {
      code.print("(");
      exp2.unparse(code, 0);
      code.print(")");
    } else {
      exp2.unparse(code, 0);
    }
    code.print(")");
  }
}

class GreaterEqNode extends BinaryExpNode {

  public GreaterEqNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public Type typeCheck() {
    Type leftType = exp1.typeCheck();
    Type rightType = exp2.typeCheck();
    if (leftType.isErrorType()) {
      return leftType;
    } else if (rightType.isErrorType()) {
      return rightType;
    } else if (!leftType.isIntType()) {
      exp1.reportError("Relational operator applied to non-numeric operand");
      return new ErrorType();
    } else if (!rightType.isIntType()) {
      exp2.reportError("Relational operator applied to non-numeric operand");
      return new ErrorType();
    }
    return new BoolType();
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("(");
    if (exp1 instanceof AssignNode) {
      code.print("(");
      exp1.unparse(code, 0);
      code.print(")");
    } else {
      exp1.unparse(code, 0);
    }
    code.print(" >= ");
    if (exp2 instanceof AssignNode) {
      code.print("(");
      exp2.unparse(code, 0);
      code.print(")");
    } else {
      exp2.unparse(code, 0);
    }
    code.print(")");
  }
}
