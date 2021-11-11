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

  private DeclListNode declarationList;

  public ProgramNode(DeclListNode declarationList) {
    this.declarationList = declarationList;
  }

  public void unparse(PrintWriter code, int indent) {
    this.declarationList.unparse(code, indent);
  }

  public void analyze() {
    SymTable symbolTable = new SymTable();
    this.declarationList.analyze(symbolTable);
  }
}

class DeclListNode extends ASTnode {

  private List<DeclNode> declarations;

  public DeclListNode(List<DeclNode> declarations) {
    this.declarations = declarations;
  }

  public void unparse(PrintWriter code, int indent) {
    for (DeclNode declaration : this.declarations) {
      declaration.unparse(code, indent);
    }
  }

  public void analyze(SymTable symbolTable) {
    for (DeclNode declaration : this.declarations) {
      declaration.analyze(symbolTable);
    }
  }
}

class FormalsListNode extends ASTnode {

  private List<FormalDeclNode> formals;

  public FormalsListNode(List<FormalDeclNode> formals) {
    this.formals = formals;
  }

  public void unparse(PrintWriter code, int indent) {
    this.addIndent(code, indent);
    boolean first = true;
    for (FormalDeclNode formal : this.formals) {
      if (first) {
        first = false;
      } else {
        code.print(", ");
      }
      formal.unparse(code, 0);
    }
  }

  public void analyze(SymTable symbolTable) {
    for (FormalDeclNode formal : this.formals) {
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

    this.declarations.unparse(code, indent + 2);
    this.statements.unparse(code, indent + 2);

    this.addIndent(code, indent);
    code.println("}");
  }

  public void analyze(SymTable symbolTable) {
    this.declarations.analyze(symbolTable);
    this.statements.analyze(symbolTable);
  }
}

class StmtListNode extends ASTnode {

  private List<StmtNode> statements;

  public StmtListNode(List<StmtNode> statements) {
    this.statements = statements;
  }

  public void unparse(PrintWriter code, int indent) {
    for (StmtNode statement : this.statements) {
      statement.unparse(code, indent);
    }
  }

  public void analyze(SymTable symbolTable) {
    for (StmtNode statement : this.statements) {
      statement.analyze(symbolTable);
    }
  }
}

class ExpListNode extends ASTnode {

  private List<ExpNode> expressions;

  public ExpListNode(List<ExpNode> expressions) {
    this.expressions = expressions;
  }

  public void unparse(PrintWriter code, int indent) {
    this.addIndent(code, indent);
    boolean first = true;
    for (ExpNode expression : this.expressions) {
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
    for (ExpNode expression : this.expressions) {
      expression.analyze(symbolTable);
    }
  }
}

// **********************************************************************
// #### DeclNode and its subclasses ####
// **********************************************************************

abstract class DeclNode extends ASTnode {

  public abstract void analyze(SymTable symbolTable);
}

class VarDeclNode extends DeclNode {

  public static int NOT_STRUCT = -1;

  private TypeNode type;
  private IdNode id;
  private int size;

  public VarDeclNode(TypeNode type, IdNode id, int size) {
    this.type = type;
    this.size = size;
    this.id = id;
  }

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
    this.type.unparse(code, 0);

    code.print(" ");
    this.id.unparse(code, 0);
    code.println(";");
  }

  public void analyze(SymTable symbolTable) {
    if (this.type instanceof StructNode) {
      StructNode structNode = (StructNode)this.type;
      structNode.analyze(symbolTable);
    } else if (this.type instanceof VoidNode) {
      ErrMsg.fatal(
        this.id.getLineNum(),
        this.id.getCharNum(),
        "Non-function declared void"
      );
    }
    try {
      symbolTable.addDecl(this.id.getValue(), new Sym(Sym.Types.VARIABLE));
    } catch (DuplicateSymException e) {
      ErrMsg.fatal(
        this.id.getLineNum(),
        this.id.getCharNum(),
        "Multiply declared identifier"
      );
    } catch (Exception e) {
      System.err.println("Encountered an unexpected error");
      System.exit(-1);
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

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
    this.type.unparse(code, 0);

    code.print(" ");
    this.id.unparse(code, 0);

    code.print(" (");
    this.formals.unparse(code, 0);
    
    code.print(") ");
    this.body.unparse(code, indent);
  }

  public void analyze(SymTable symbolTable) {
    if (this.type instanceof StructNode) {
      StructNode structNode = (StructNode)this.type;
      structNode.analyze(symbolTable);
    }
    try {
      symbolTable.addDecl(this.id.getValue(), new Sym(Sym.Types.FUNCTION));
    } catch (DuplicateSymException e) {
      ErrMsg.fatal(
        this.id.getLineNum(),
        this.id.getCharNum(),
        "Multiply declared identifier"
      );
    } catch (Exception e) {
      System.out.println("Encountered an unexpected exception");
      System.exit(-1);
    }
    symbolTable.addScope();
    this.formals.analyze(symbolTable);
    this.body.analyze(symbolTable);
  }
}

class FormalDeclNode extends DeclNode {

  private TypeNode type;
  private IdNode id;

  public FormalDeclNode(TypeNode type, IdNode id) {
    this.type = type;
    this.id = id;
  }

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
    this.type.unparse(code, 0);

    code.print(" ");
    this.id.unparse(code, 0);
  }

  public void analyze(SymTable symbolTable) {
    if (this.type instanceof StructNode) {
      StructNode structNode = (StructNode)this.type;
      structNode.analyze(symbolTable);
    } else if (this.type instanceof VoidNode) {
      ErrMsg.fatal(
        this.id.getLineNum(),
        this.id.getCharNum(),
        "Non-function declared void"
      );
    }
    try {
      symbolTable.addDecl(this.id.getValue(), new Sym(Sym.Types.FORMAL));
    } catch (DuplicateSymException e) {
      ErrMsg.fatal(
        this.id.getLineNum(),
        this.id.getCharNum(),
        "Multiply declared identifier"
      );
    } catch (Exception e) {
      System.err.println("Encountered an unexpected error");
      System.exit(-1);
    }
  }
}

class StructDeclNode extends DeclNode {

  private DeclListNode declarations;
  private IdNode id;

  public StructDeclNode(IdNode id, DeclListNode declarations) {
    this.declarations = declarations;
    this.id = id;
  }

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
    code.print("struct ");

    this.id.unparse(code, 0);
    code.println(" {");

    this.declarations.unparse(code, indent + 2);

    this.addIndent(code, indent);
    code.println("}");
  }

  public void analyze(SymTable symbolTable) {
    StructSym structSymbol = new StructSym();
    try {
      symbolTable.addDecl(this.id.getValue(), structSymbol);
    } catch (DuplicateSymException e) {
      ErrMsg.fatal(
        this.id.getLineNum(),
        this.id.getCharNum(),
        "Multiply declared identifier"
      );
    }
    this.declarations.analyze(structSymbol.getSymTable());
  }
}

// **********************************************************************
// #### TypeNode and its Subclasses ####
// **********************************************************************

abstract class TypeNode extends ASTnode {

  public abstract void analyze(SymTable symbolTable);
}

class IntNode extends TypeNode {

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
    code.print("int");
  }

  public void analyze(SymTable symbolTable) { }
}

class BoolNode extends TypeNode {

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
    code.print("bool");
  }

  public void analyze(SymTable symbolTable) { }
}

class VoidNode extends TypeNode {

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
    code.print("void");
  }

  public void analyze(SymTable symbolTable) { }
}

class StructNode extends TypeNode {

  private IdNode id;

  public StructNode(IdNode id) {
    this.id = id;
  }

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
    code.print("struct ");

    this.id.unparse(code, 0);
  }

  public void analyze(SymTable symbolTable) {
    Sym symbol = symbolTable.lookupGlobal(this.id.getValue());
    if (symbol == null || !(symbol instanceof StructSym)) {
      ErrMsg.fatal(
        this.id.getLineNum(),
        this.id.getCharNum(),
        "Invalid name of struct type"
      );
    }
  }
}

// **********************************************************************
// #### StmtNode and its subclasses ####
// **********************************************************************

abstract class StmtNode extends ASTnode {

  public abstract void analyze(SymTable symbolTable);
}

class AssignStmtNode extends StmtNode {

  private AssignNode assign;

  public AssignStmtNode(AssignNode assign) {
    this.assign = assign;
  }

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
    this.assign.unparse(code, 0);

    code.println(';');
  }

  public void analyze(SymTable symbolTable) {
    this.assign.analyze(symbolTable);
  }
}

class PreIncStmtNode extends StmtNode {

  private ExpNode expression;

  public PreIncStmtNode(ExpNode expression) {
    this.expression = expression;
  }

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
    code.print("++");

    this.expression.unparse(code, 0);
    code.println(';');
  }

  public void analyze(SymTable symbolTable) {
    this.expression.analyze(symbolTable);
  }
}

class PreDecStmtNode extends StmtNode {

  private ExpNode expression;

  public PreDecStmtNode(ExpNode expression) {
    this.expression = expression;
  }

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
    code.print("--");

    this.expression.unparse(code, 0);
    code.println(';');
  }

  public void analyze(SymTable symbolTable) {
    this.expression.analyze(symbolTable);
  }
}

class ReceiveStmtNode extends StmtNode {

  private ExpNode expression;

  public ReceiveStmtNode(ExpNode expression) {
    this.expression = expression;
  }

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
    code.print("receive >> ");

    this.expression.unparse(code, 0);
    code.println(';');
  }

  public void analyze(SymTable symbolTable) {
    this.expression.analyze(symbolTable);
  }
}

class PrintStmtNode extends StmtNode {

  private ExpNode expression;

  public PrintStmtNode(ExpNode expression) {
    this.expression = expression;
  }

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
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

  public void analyze(SymTable symbolTable) {
    this.expression.analyze(symbolTable);
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

    this.addIndent(code, indent);
    code.print("if (");

    if (expression instanceof AssignNode) {
      code.print("(");
      expression.unparse(code, 0);
      code.print(")");
    } else {
      expression.unparse(code, 0);
    }
    code.println(") {");

    this.declarations.unparse(code, indent + 2);
    this.statements.unparse(code, indent + 2);

    this.addIndent(code, indent);
    code.println("}");
  }

  public void analyze(SymTable symbolTable) {
    this.expression.analyze(symbolTable);
    symbolTable.addScope();
    this.declarations.analyze(symbolTable);
    this.statements.analyze(symbolTable);
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

    this.addIndent(code, indent);
    code.print("if (");

    if (expression instanceof AssignNode) {
      code.print("(");
      expression.unparse(code, 0);
      code.print(")");
    } else {
      expression.unparse(code, 0);
    }
    code.println(") {");

    this.thenDeclarations.unparse(code, indent + 2);
    this.thenStatements.unparse(code, indent + 2);

    this.addIndent(code, indent);
    code.println("} else {");

    this.elseDeclarations.unparse(code, indent + 2);
    this.elseStatements.unparse(code, indent + 2);

    this.addIndent(code, indent);
    code.println("}");
  }

  public void analyze(SymTable symbolTable) {

    this.expression.analyze(symbolTable);

    symbolTable.addScope();
    this.thenDeclarations.analyze(symbolTable);
    this.thenStatements.analyze(symbolTable);
    try {
      symbolTable.removeScope();
    } catch (Exception e) {
      System.err.println("Encountered an unexpected error");
      System.exit(-1);
    }

    symbolTable.addScope();
    this.elseDeclarations.analyze(symbolTable);
    this.elseStatements.analyze(symbolTable);
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

    this.addIndent(code, indent);
    code.print("while (");

    if (expression instanceof AssignNode) {
      code.print("(");
      expression.unparse(code, 0);
      code.print(")");
    } else {
      expression.unparse(code, 0);
    }
    code.println(") {");

    this.declarations.unparse(code, indent + 2);
    this.statements.unparse(code, indent + 2);

    this.addIndent(code, indent);
    code.println("}");
  }

  public void analyze(SymTable symbolTable) {
    this.expression.analyze(symbolTable);
    symbolTable.addScope();
    this.declarations.analyze(symbolTable);
    this.statements.analyze(symbolTable);
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

    this.addIndent(code, indent);
    code.print("repeat (");

    if (expression instanceof AssignNode) {
      code.print("(");
      expression.unparse(code, 0);
      code.print(")");
    } else {
      expression.unparse(code, 0);
    }
    code.println(") {");

    this.declarations.unparse(code, indent + 2);
    this.statements.unparse(code, indent + 2);

    this.addIndent(code, indent);
    code.println("}");
  }

  public void analyze(SymTable symbolTable) {
    this.expression.analyze(symbolTable);
    symbolTable.addScope();
    this.declarations.analyze(symbolTable);
    this.statements.analyze(symbolTable);
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

    this.addIndent(code, indent);
    this.callExpression.unparse(code, 0);

    code.println(';');
  }

  public void analyze(SymTable symbolTable) {
    this.callExpression.analyze(symbolTable);
  }
}

class ReturnStmtNode extends StmtNode {

  private ExpNode expression;

  public ReturnStmtNode(ExpNode expression) {
    this.expression = expression;
  }

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
    code.print("ret");

    if (this.expression != null) {
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
    this.expression.analyze(symbolTable);
  }
}

// **********************************************************************
// #### ExpNode and its subclasses ####
// **********************************************************************

abstract class ExpNode extends ASTnode {

  public abstract void analyze(SymTable symbolTable);
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

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
    code.print(this.value);
  }

  public void analyze(SymTable symbolTable) { }
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

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
    code.printf(this.value);
  }

  public void analyze(SymTable symbolTable) { }
}

class TrueNode extends ExpNode {

  private int charNum;
  private int lineNum;

  public TrueNode(int lineNum, int charNum) {
    this.charNum = charNum;
    this.lineNum = lineNum;
  }

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
    code.print("true");
  }

  public void analyze(SymTable symbolTable) { }
}

class FalseNode extends ExpNode {

  private int charNum;
  private int lineNum;

  public FalseNode(int lineNum, int charNum) {
    this.charNum = charNum;
    this.lineNum = lineNum;
  }

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
    code.print("false");
  }

  public void analyze(SymTable symbolTable) { }
}

class IdNode extends ExpNode {

  private String value;
  private int charNum;
  private int lineNum;

  public IdNode(int lineNum, int charNum, String value) {
    this.charNum = charNum;
    this.lineNum = lineNum;
    this.value = value;
  }

  public void analyze(SymTable symbolTable) {
    /* Parent of ID node will handle analysis */
  }

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
    code.print(this.value);
  }

  public String getValue() {
    return this.value;
  }

  public int getCharNum() {
    return this.charNum;
  }

  public int getLineNum() {
    return this.lineNum;
  }
}

class DotAccessExpNode extends ExpNode {

  private ExpNode accessor;
  private IdNode id;

  public DotAccessExpNode(ExpNode accessor, IdNode id) {
    this.accessor = accessor;
    this.id = id;
  }

  public IdNode getId() {
    return this.id;
  }

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
    this.accessor.unparse(code, 0);

    code.print('.');
    this.id.unparse(code, 0);
  }

  public void analyze(SymTable symbolTable) {
    IdNode lhs = null;
    if (this.accessor instanceof IdNode) {
      lhs = (IdNode)this.accessor;
    } else {
      DotAccessExpNode dotAccessNode = (DotAccessExpNode)this.accessor;
      lhs = dotAccessNode.getId();
    }
    Sym symbol = symbolTable.lookupGlobal(lhs.getValue());
    if (symbol == null || !(symbol instanceof StructSym)) {
      ErrMsg.fatal(
        lhs.getLineNum(),
        lhs.getCharNum(),
        "Dot-access of non-struct type"
      );
    } else {
      StructSym structSymbol = (StructSym)symbol;
      Sym memberSymbol = structSymbol.getSymTable().lookupGlobal(this.id.getValue());
      if (memberSymbol == null) {
        ErrMsg.fatal(
          this.id.getLineNum(),
          this.id.getCharNum(),
          "Invalid struct field name"
        );
      }
    }
    this.accessor.analyze(symbolTable);
  }
}

class AssignNode extends ExpNode {

  private ExpNode leftHandSide;
  private ExpNode expression;

  public AssignNode(ExpNode leftHandSide, ExpNode expression) {
    this.leftHandSide = leftHandSide;
    this.expression = expression;
  }

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
    this.leftHandSide.unparse(code, 0);

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
    this.leftHandSide.analyze(symbolTable);
    this.expression.analyze(symbolTable);
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

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
    this.methodId.unparse(code, 0);

    code.print('(');
    this.parameterExpressions.unparse(code, 0);
    code.print(')');
  }

  public void analyze(SymTable symbolTable) {
    Sym symbol = symbolTable.lookupGlobal(this.methodId.getValue());
    if (symbol == null || symbol.getType() != Sym.Types.FUNCTION) {
      ErrMsg.fatal(
        this.methodId.getLineNum(),
        this.methodId.getCharNum(),
        "Undeclared identifier"
      );
    }
    this.parameterExpressions.analyze(symbolTable);
  }
}

abstract class UnaryExpNode extends ExpNode {

  protected ExpNode exp;

  public UnaryExpNode(ExpNode exp) {
    this.exp = exp;
  }

  public void analyze(SymTable symbolTable) {
    this.exp.analyze(symbolTable);
  }
}

abstract class BinaryExpNode extends ExpNode {

  protected ExpNode exp1;
  protected ExpNode exp2;

  public BinaryExpNode(ExpNode exp1, ExpNode exp2) {
    this.exp1 = exp1;
    this.exp2 = exp2;
  }

  public void analyze(SymTable symbolTable) {
    this.exp1.analyze(symbolTable);
    this.exp2.analyze(symbolTable);
  }
}

// **********************************************************************
// #### Subclasses of UnaryExpNode ####
// **********************************************************************

class UnaryMinusNode extends UnaryExpNode {

  public UnaryMinusNode(ExpNode exp) {
    super(exp);
  }

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
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

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
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

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
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

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
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

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
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

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
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

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
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

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
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
    
    this.addIndent(code, indent);
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

    this.addIndent(code, indent);
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

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
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

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
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

  public void unparse(PrintWriter code, int indent) {
    
    this.addIndent(code, indent);
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

  public void unparse(PrintWriter code, int indent) {

    this.addIndent(code, indent);
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
