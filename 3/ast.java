import java.io.*;
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
// ####ASTnode class (base class for all other kinds of nodes)####
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
// ####ProgramNode,  DeclListNode, FormalsListNode, FnBodyNode,
// StmtListNode, ExpListNode####
// **********************************************************************

class ProgramNode extends ASTnode {
  
  private DeclListNode declarationList;

  public ProgramNode(DeclListNode declarationList) {
    this.declarationList = declarationList;
  }

  public void unparse(PrintWriter code, int indent) {
    declarationList.unparse(code, indent);
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

  public int getNumberOfDeclarations() {
    return this.declarations.size();
  }
}

class FormalsListNode extends ASTnode {

  private List<FormalDeclNode> formals;

  public FormalsListNode(List<FormalDeclNode> formals) {
    this.formals = formals;
  }

  public void unparse(PrintWriter code, int indent) {
    for (int i = 0; i < this.formals.size(); i++) {
      if (i > 0) {
        code.print(", ");
      }
      this.formals.get(i).unparse(code, indent);
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
    code.print("{");
    if (this.declarations.getNumberOfDeclarations() > 0) {

    }
  }
}

class StmtListNode extends ASTnode {
    public StmtListNode(List<StmtNode> S) {
        myStmts = S;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // list of kids (StmtNodes)
    private List<StmtNode> myStmts;
}

class ExpListNode extends ASTnode {
    public ExpListNode(List<ExpNode> S) {
        myExps = S;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // list of kids (ExpNodes)
    private List<ExpNode> myExps;
}

// **********************************************************************
// ####DeclNode and its subclasses####
// **********************************************************************

abstract class DeclNode extends ASTnode {
}

class VarDeclNode extends DeclNode {

  private IdNode structId;
  private TypeNode type;
  private IdNode id;

  public VarDeclNode(TypeNode type, IdNode id) {
    this.structId = null;
    this.type = type;
    this.id = id;
  }

  public VarDeclNode(TypeNode structType, TypeNode type, IdNode id) {
    this.structId = stru
  }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
        p.println(";");
    }
}

class FnDeclNode extends DeclNode {

  private FormatsListNode formals;
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
    super.addIndent(code, indent);
    this.type.unparse(code, indent);
    code.print(" ");
    this.id.unparse(code, indent);
    code.print(" (");
    this.formals.unparse(code, indent);
    code.print(") ");
    this.body.unparse(code, indent);
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
    this.type.unparse(code, indent);
    code.print(" ");
    this.id.unparse(code, indent);
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
    code.print("struct ");
    this.id.unparse(code, indent);
    code.print("{");
    this.declarations.unparse(code, indent);
    code.print("}");
  }
}

// **********************************************************************
// ####TypeNode and its Subclasses####
// **********************************************************************

abstract class TypeNode extends ASTnode {
}

class IntNode extends TypeNode {
    public IntNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("int");
    }
}

class BoolNode extends TypeNode {
    public BoolNode() {
    }

    public void unparse(PrintWriter p, int indent) {
    }
}

class VoidNode extends TypeNode {
    public VoidNode() {
    }

    public void unparse(PrintWriter p, int indent) {
    }
}

class StructNode extends TypeNode {
    public StructNode(IdNode id) {
		myId = id;
    }

    public void unparse(PrintWriter p, int indent) {
    }
	
	// 1 kid
    private IdNode myId;
}

// **********************************************************************
// ####StmtNode and its subclasses####
// **********************************************************************

abstract class StmtNode extends ASTnode {
}

class AssignStmtNode extends StmtNode {
    public AssignStmtNode(AssignNode assign) {
        myAssign = assign;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // 1 kid
    private AssignNode myAssign;
}

class PreIncStmtNode extends StmtNode {
    public PreIncStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // 1 kid
    private ExpNode myExp;
}

class PreDecStmtNode extends StmtNode {
    public PreDecStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // 1 kid
    private ExpNode myExp;
}

class ReceiveStmtNode extends StmtNode {
    public ReceiveStmtNode(ExpNode e) {
        myExp = e;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // 1 kid (actually can only be an IdNode or an ArrayExpNode)
    private ExpNode myExp;
}

class PrintStmtNode extends StmtNode {
    public PrintStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // 1 kid
    private ExpNode myExp;
}

class IfStmtNode extends StmtNode {
    public IfStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myDeclList = dlist;
        myExp = exp;
        myStmtList = slist;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // e kids
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class IfElseStmtNode extends StmtNode {
    public IfElseStmtNode(ExpNode exp, DeclListNode dlist1,
                          StmtListNode slist1, DeclListNode dlist2,
                          StmtListNode slist2) {
        myExp = exp;
        myThenDeclList = dlist1;
        myThenStmtList = slist1;
        myElseDeclList = dlist2;
        myElseStmtList = slist2;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // 5 kids
    private ExpNode myExp;
    private DeclListNode myThenDeclList;
    private StmtListNode myThenStmtList;
    private StmtListNode myElseStmtList;
    private DeclListNode myElseDeclList;
}

class WhileStmtNode extends StmtNode {
    public WhileStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }
	
    public void unparse(PrintWriter p, int indent) {
    }

    // 3 kids
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class RepeatStmtNode extends StmtNode {
    public RepeatStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }
	
    public void unparse(PrintWriter p, int indent) {
    }

    // 3 kids
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class CallStmtNode extends StmtNode {

  private CallExpNode callExpression;

  public CallStmtNode(CallExpNode callExpression) {
    this.callExpression = callExpression;
  }

  public void unparse(PrintWriter code, int indent) {
    this.callExpression.unparse(code, indent);
    code.print(';');
  }
}

class ReturnStmtNode extends StmtNode {

  private ExpNode expression;

  public ReturnStmtNode(ExpNode expression) {
    this.expression = expression;
  }

  public void unparse(PrintWriter code, int indent) {
    code.print("return");
    if (this.expression != null) {
      code.print(' ');
      this.expression.unparse(code, indent);
    }
    code.print(';');
  }
}

// **********************************************************************
// #### ExpNode and its subclasses ####
// **********************************************************************

abstract class ExpNode extends ASTnode { }

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
    code.print(this.value);
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

  public void unparse(PrintWriter code, int indent) {
    code.printf("\"%s\"", this.value);
  }
}

class TrueNode extends ExpNode {

  private int charNum;
  private int lineNum;

  public TrueNode(int lineNum, int charNum) {
    this.charNum = charNum;
    this.lineNum = lineNum;
  }

  public void unparse(PrintWriter code, int indent) {
    code.print("true");
  }
}

class FalseNode extends ExpNode {

  private int charNum;
  private int lineNum;

  public FalseNode(int lineNum, int charNum) {
    this.charNum = charNum;
    this.lineNum = lineNum;
  }

  public void unparse(PrintWriter code, int indent) {
    code.print("false");
  }
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

  public void unparse(PrintWriter code, int indent) {
    code.print(this.value);
  }
}

class DotAccessExpNode extends ExpNode {

  private ExpNode accessor;
  private IdNode id;

  public DotAccessExpNode(ExpNode accessor, IdNode id) {
    this.accessor = accessor;
    this.id = id;
  }

  public void unparse(PrintWriter code, int indent) {
    this.accessor.unparse(code, indent);
    code.print('.');
    this.id.unparse(code, indent);
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
    this.leftHandSide.unparse(code, indent);
    code.print(" = ");
    this.expression.unparse(code, indent);
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
    this.methodId.unparse(code, indent);
    code.print('(');
    this.parameterExpressions.unparse(code, indent);
    code.print(')');
  }
}

abstract class UnaryExpNode extends ExpNode {

  protected ExpNode exp;

  public UnaryExpNode(ExpNode exp) {
    this.exp = exp;
  }
}

abstract class BinaryExpNode extends ExpNode {

  protected ExpNode exp1;
  protected ExpNode exp2;

  public BinaryExpNode(ExpNode exp1, ExpNode exp2) {
    this.exp1 = exp1;
    this.exp2 = exp2;
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
    code.print('-');
    this.exp.unparse(code, indent);
  }
}

class NotNode extends UnaryExpNode {

  public NotNode(ExpNode exp) {
    super(exp);
  }

  public void unparse(PrintWriter code, int indent) {
    code.print('!');
    this.exp.unparse(code, indent);
  }
}

// **********************************************************************
// ####Subclasses of BinaryExpNode####
// **********************************************************************

class PlusNode extends BinaryExpNode {

  public PlusNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public void unparse(PrintWriter code, int indent) {
    this.exp1.unparse(code, indent);
    code.print(" + ");
    this.exp2.unparse(code, indent);
  }
}

class MinusNode extends BinaryExpNode {

  public MinusNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public void unparse(PrintWriter code, int indent) {
    this.exp1.unparse(code, indent);
    code.print(" - ");
    this.exp2.unparse(code, indent);
  }
}

class TimesNode extends BinaryExpNode {

  public TimesNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public void unparse(PrintWriter code, int indent) {
    this.exp1.unparse(code, indent);
    code.print(" * ");
    this.exp2.unparse(code, indent);
  }
}

class DivideNode extends BinaryExpNode {

  public DivideNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public void unparse(PrintWriter code, int indent) {
    this.exp1.unparse(code, indent);
    code.print(" / ");
    this.exp2.unparse(code, indent);
  }
}

class AndNode extends BinaryExpNode {

  public AndNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public void unparse(PrintWriter code, int indent) {
    this.exp1.unparse(code, indent);
    code.print(" && ");
    this.exp2.unparse(code, indent);
  }
}

class OrNode extends BinaryExpNode {

  public OrNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public void unparse(PrintWriter code, int indent) {
    this.exp1.unparse(code, indent);
    code.print(" || ");
    this.exp2.unparse(code, indent);
  }
}

class EqualsNode extends BinaryExpNode {

  public EqualsNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public void unparse(PrintWriter code, int indent) {
    this.exp1.unparse(code, indent);
    code.print(" == ");
    this.exp2.unparse(code, indent);
  }
}

class NotEqualsNode extends BinaryExpNode {

  public NotEqualsNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public void unparse(PrintWriter code, int indent) {
    this.exp1.unparse(code, indent);
    code.print(" != ");
    this.exp2.unparse(code, indent);
  }
}

class LessNode extends BinaryExpNode {

  public LessNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public void unparse(PrintWriter code, int indent) {
    this.exp1.unparse(code, indent);
    code.print(" < ");
    this.exp2.unparse(code, indent);
  }
}

class GreaterNode extends BinaryExpNode {

  public GreaterNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public void unparse(PrintWriter code, int indent) {
    this.exp1.unparse(code, indent);
    code.print(" > ");
    this.exp2.unparse(code, indent);
  }
}

class LessEqNode extends BinaryExpNode {

  public LessEqNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public void unparse(PrintWriter code, int indent) {
    this.exp1.unparse(code, indent);
    code.print(" <= ");
    this.exp2.unparse(code, indent);
  }
}

class GreaterEqNode extends BinaryExpNode {

  public GreaterEqNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public void unparse(PrintWriter code, int indent) {
    this.exp1.unparse(code, indent);
    code.print(" >= ");
    this.exp2.unparse(code, indent);
  }
}
