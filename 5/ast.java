import java.util.LinkedList;
import java.util.Stack;
import java.util.List;

import java.io.PrintWriter;

/*
 * Base nodes
 */
abstract class ASTNode {

  public abstract void unparse(PrintWriter code, int indent);

  protected void addIndent(PrintWriter code, int indent) {
    for (int i = 0; i < indent; i++) {
      code.print(' ');
    }
  }
}

class ProgramNode extends ASTNode {

  private List<DeclNode> declarations;

  public ProgramNode(List<DeclNode> declarations) {
    this.declarations = declarations;
  }

  public void unparse(PrintWriter code, int indent) {
    for (DeclNode declaration : declarations) {
      declaration.unparse(code, indent);
    }
  }

  public void analyze() {
    SymTable symbolTable = new SymTable();
    for (DeclNode declaration : declarations) {
      declaration.analyze(symbolTable);
    }
  }
}

/*
 * Declaration nodes
 */
abstract class DeclNode extends ASTNode {

  public abstract Symb analyze(SymTable symbolTable);
}

class VarDeclNode extends DeclNode {

  private TypeNode type;
  private IdNode id;

  public VarDeclNode(TypeNode type, IdNode id) {
    this.type = type;
    this.id = id;
  }

  public IdNode getIdNode() {
    return id;
  }

  public TypeNode getTypeNode() {
    return type;
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    type.unparse(code, 0);
    code.print(' ');
    id.unparse(code, 0);
    code.println(';');
  }

  public Symb analyze(SymTable symbolTable) {

    Symb sym = null;
    boolean isVoidType = type instanceof VoidNode;
    boolean isDeclared = symbolTable.lookupLocal(id.getValue()) != null;

    if (isVoidType) {
      id.reportError("Non-function declared void");
    } else if (type instanceof StructNode) {
      StructDefinitionSymbol structDefinitionSymbol = ((StructNode)type).analyze(symbolTable);
      if (structDefinitionSymbol != null) {
        sym = new StructVariableSymbol(structDefinitionSymbol);
      }
    } else {
      sym = new VariableSymbol(type.getType());
    }
    if (isDeclared) {
      id.reportError("Multiply declared identifier");
    }
    if (!isVoidType && !isDeclared && sym != null) {
      symbolTable.addDeclaration(id.getValue(), sym);
      return sym;
    }
    return null;
  }
}

class FnDeclNode extends DeclNode {

  private List<FormalDeclNode> formals;
  private List<DeclNode> declarations;
  private List<StmtNode> statements;
  private TypeNode type;
  private IdNode id;

  public FnDeclNode(
    TypeNode type,
    IdNode id,
    List<FormalDeclNode> formals,
    List<DeclNode> declarations,
    List<StmtNode> statements
  ) {
    this.declarations = declarations;
    this.statements = statements;
    this.formals = formals;
    this.type = type;
    this.id = id;
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    type.unparse(code, 0);
    code.print(' ');
    id.unparse(code, 0);
    code.print(" (");
    for (int i = 0; i < formals.size(); i++) {
      if (i > 0) {
        code.print(", ");
      }
      formals.get(i).unparse(code, 0);
    }
    code.println(") {");
    for (DeclNode declaration : declarations) {
      declaration.unparse(code, indent + 2);
    }
    for (StmtNode statement : statements) {
      statement.unparse(code, indent + 2);
    }
    addIndent(code, indent);
    code.println('}');
  }

  public Symb analyze(SymTable symbolTable) {
    FunctionSymbol sym = new FunctionSymbol(type.getType());
    boolean isDeclared = symbolTable.lookupLocal(id.getValue()) != null;
    if (isDeclared) {
      id.reportError("Multiply declared identifier");
    } else {
      symbolTable.addDeclaration(id.getValue(), sym);
    }
    symbolTable.addScope();
    for (FormalDeclNode formal : formals) {
      Symb formalSym = formal.analyze(symbolTable);
      if (formalSym != null && formalSym instanceof VariableSymbol) {
        sym.addFormalType(((VariableSymbol)formalSym).getType());
      }
    }
    for (DeclNode declaration : declarations) {
      declaration.analyze(symbolTable);
    }
    for (StmtNode statement : statements) {
      statement.analyze(symbolTable);
    }
    symbolTable.removeScope();
    return isDeclared ? null : sym;
  }
}

class FormalDeclNode extends DeclNode {

  private TypeNode type;
  private IdNode id;

  public FormalDeclNode(TypeNode type, IdNode id) {
    this.type = type;
    this.id = id;
  }

  public TypeNode getTypeNode() {
    return type;
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    type.unparse(code, 0);
    code.print(' ');
    id.unparse(code, 0);
  }

  public Symb analyze(SymTable symbolTable) {

    boolean isVoidType = type instanceof VoidNode;
    boolean isDeclared = symbolTable.lookupLocal(id.getValue()) != null;

    if (isVoidType) {
      id.reportError("Non-function declared void");
    }
    if (isDeclared) {
      id.reportError("Multiply declared identifier");
    }
    if (!isVoidType && !isDeclared) {
      Symb sym = new VariableSymbol(type.getType());
      symbolTable.addDeclaration(id.getValue(), sym);
      return sym;
    }
    return null;
  }
}

class StructDeclNode extends DeclNode {

  private List<DeclNode> declarations;
  private IdNode id;

  public StructDeclNode(IdNode id, List<DeclNode> declarations) {
    this.declarations = declarations;
    this.id = id;
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("struct ");
    id.unparse(code, 0);
    code.println(" {");
    for (DeclNode declaration : declarations) {
      declaration.unparse(code, indent + 2);
    }
    addIndent(code, indent);
    code.println('}');
  }

  public Symb analyze(SymTable symbolTable) {
    if (symbolTable.lookupLocal(id.getValue()) == null) {
      StructDefinitionSymbol sym = new StructDefinitionSymbol();
      symbolTable.addDeclaration(id.getValue(), sym);
      symbolTable.addScope();
      for (DeclNode declaration : declarations) {
        Symb memberSym = declaration.analyze(symbolTable);
        if (memberSym != null) {
          sym.addMember(((VarDeclNode)declaration).getIdNode().getValue(), memberSym);
        }
      }
      symbolTable.removeScope();
      return sym;
    } else {
      id.reportError("Multiply declared identifier");
      return null;
    }
  }
}

/*
 * Type nodes
 */
abstract class TypeNode extends ASTNode {

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

  public StructDefinitionSymbol analyze(SymTable symbolTable) {
    Symb sym = symbolTable.lookupGlobal(id.getValue());
    if (sym == null || !(sym instanceof StructDefinitionSymbol)) {
      id.reportError("Invalid name of struct type");
      return null;
    }
    return (StructDefinitionSymbol)sym;
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("struct ");
    id.unparse(code, 0);
  }

  public Type getType() {
    return new StructType(id.getValue());
  }
}

/*
 * Statement nodes
 */
abstract class StmtNode extends ASTNode {

  public abstract void analyze(SymTable symbolTable);
}

class AssignStmtNode extends StmtNode {

  private AssignNode assign;

  public AssignStmtNode(AssignNode assign) {
    this.assign = assign;
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    assign.unparse(code, -1);
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

  private ExpNode exp;

  public ReceiveStmtNode(ExpNode exp) {
    this.exp = exp;
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("receive >> ");
    exp.unparse(code, 0);
    code.println(';');
  }

  public void analyze(SymTable symbolTable) {
    exp.analyze(symbolTable);
  }
}

class PrintStmtNode extends StmtNode {

  private ExpNode exp;

  public PrintStmtNode(ExpNode exp) {
    this.exp = exp;
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("receive >> ");
    exp.unparse(code, 0);
    code.println(';');
  }

  public void analyze(SymTable symbolTable) {
    exp.analyze(symbolTable);
  }
}

class IfStmtNode extends StmtNode {

  private List<DeclNode> declarations;
  private List<StmtNode> statements;
  private ExpNode exp;

  public IfStmtNode(
    ExpNode exp,
    List<DeclNode> declarations,
    List<StmtNode> statements
  ) {
    this.declarations = declarations;
    this.statements = statements;
    this.exp = exp;
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("if (");
    exp.unparse(code, 0);
    code.println(") {");
    for (DeclNode declaration : declarations) {
      declaration.unparse(code, indent + 2);
    }
    for (StmtNode statement : statements) {
      statement.unparse(code, indent + 2);
    }
    addIndent(code, indent);
    code.println('}');
  }

  public void analyze(SymTable symbolTable) {
    exp.analyze(symbolTable);
    symbolTable.addScope();
    for (DeclNode declaration : declarations) {
      declaration.analyze(symbolTable);
    }
    for (StmtNode statement : statements) {
      statement.analyze(symbolTable);
    }
    symbolTable.removeScope();
  }
}

class IfElseStmtNode extends StmtNode {

  private List<DeclNode> thenDeclarations;
  private List<StmtNode> thenStatements;
  private List<DeclNode> elseDeclarations;
  private List<StmtNode> elseStatements;
  private ExpNode exp;

  public IfElseStmtNode(
    ExpNode exp,
    List<DeclNode> thenDeclarations,
    List<StmtNode> thenStatements,
    List<DeclNode> elseDeclarations,
    List<StmtNode> elseStatements
  ) {
    this.thenDeclarations = thenDeclarations;
    this.elseDeclarations = elseDeclarations;
    this.thenStatements = thenStatements;
    this.elseStatements = elseStatements;
    this.exp = exp;
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("if (");
    exp.unparse(code, 0);
    code.println(") {");
    for (DeclNode declaration : thenDeclarations) {
      declaration.unparse(code, indent + 2);
    }
    for (StmtNode statement : thenStatements) {
      statement.unparse(code, indent + 2);
    }
    addIndent(code, indent);
    code.println("} else {");
    for (DeclNode declaration : elseDeclarations) {
      declaration.unparse(code, indent + 2);
    }
    for (StmtNode statement : elseStatements) {
      statement.unparse(code, indent + 2);
    }
    addIndent(code, indent);
    code.println('}');
  }

  public void analyze(SymTable symbolTable) {
    exp.analyze(symbolTable);
    symbolTable.addScope();
    for (DeclNode declaration : thenDeclarations) {
      declaration.analyze(symbolTable);
    }
    for (StmtNode statement : thenStatements) {
      statement.analyze(symbolTable);
    }
    symbolTable.removeScope();
    symbolTable.addScope();
    for (DeclNode declaration : elseDeclarations) {
      declaration.analyze(symbolTable);
    }
    for (StmtNode statement : elseStatements) {
      statement.analyze(symbolTable);
    }
    symbolTable.removeScope();
  }
}

class WhileStmtNode extends StmtNode {

  private List<DeclNode> declarations;
  private List<StmtNode> statements;
  private ExpNode exp;

  public WhileStmtNode(
    ExpNode exp,
    List<DeclNode> declarations,
    List<StmtNode> statements
  ) {
    this.declarations = declarations;
    this.statements = statements;
    this.exp = exp;
  }
	
  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("while (");
    exp.unparse(code, 0);
    code.println(") {");
    for (DeclNode declaration : declarations) {
      declaration.unparse(code, indent + 2);
    }
    for (StmtNode statement : statements) {
      statement.unparse(code, indent + 2);
    }
    addIndent(code, indent);
    code.println('}');
  }

  public void analyze(SymTable symbolTable) {
    exp.analyze(symbolTable);
    symbolTable.addScope();
    for (DeclNode declaration : declarations) {
      declaration.analyze(symbolTable);
    }
    for (StmtNode statement : statements) {
      statement.analyze(symbolTable);
    }
    symbolTable.removeScope();
  }
}

class RepeatStmtNode extends StmtNode {

  private List<DeclNode> declarations;
  private List<StmtNode> statements;
  private ExpNode exp;

  public RepeatStmtNode(
    ExpNode exp,
    List<DeclNode> declarations,
    List<StmtNode> statements
  ) {
    this.declarations = declarations;
    this.statements = statements;
    this.exp = exp;
  }
	
  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("repeat (");
    exp.unparse(code, 0);
    code.println(") {");
    for (DeclNode declaration : declarations) {
      declaration.unparse(code, indent + 2);
    }
    for (StmtNode statement : statements) {
      statement.unparse(code, indent + 2);
    }
    addIndent(code, indent);
    code.println('}');
  }

  public void analyze(SymTable symbolTable) {
    exp.analyze(symbolTable);
    symbolTable.addScope();
    for (DeclNode declaration : declarations) {
      declaration.analyze(symbolTable);
    }
    for (StmtNode statement : statements) {
      statement.analyze(symbolTable);
    }
    symbolTable.removeScope();
  }
}

class CallStmtNode extends StmtNode {

  private CallExpNode exp;

  public CallStmtNode(CallExpNode exp) {
    this.exp = exp;
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    exp.unparse(code, 0);
    code.println(';');
  }

  public void analyze(SymTable symbolTable) {
    exp.analyze(symbolTable);
  }
}

class ReturnStmtNode extends StmtNode {

  private ExpNode exp;

  public ReturnStmtNode() { }

  public ReturnStmtNode(ExpNode exp) {
    this.exp = exp;
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print("ret");
    if (exp != null) {
      code.print(' ');
      exp.unparse(code, 0);
    }
    code.println(';');
  }

  public void analyze(SymTable symbolTable) {
    if (exp != null) {
      exp.analyze(symbolTable);
    }
  }
}

/*
 * Expression nodes
 */
abstract class ExpNode extends ASTNode {

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
    code.print(value);
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
    code.print("tru");
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
    code.print("fls");
  }
}

class IdNode extends ExpNode {

  private String value;
  private int charNum;
  private int lineNum;
  private Symb sym;

  public IdNode(int lineNum, int charNum, String value) {
    this.lineNum = lineNum;
    this.charNum = charNum;
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void reportError(String error) {
    ErrMsg.fatal(lineNum, charNum, error);
  }

  public Symb getSym() {
    return sym;
  }

  public void link(Symb sym) {
    this.sym = sym;
  }

  public void analyze(SymTable symbolTable) {
    Symb sym = symbolTable.lookupGlobal(value);
    if (sym == null) {
      reportError("Undeclared identifier");
    } else {
      link(sym);
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
}

class DotAccessExpNode extends ExpNode {

  private ExpNode accessor;
  private IdNode id;

  public DotAccessExpNode(ExpNode accessor, IdNode id) {
    this.accessor = accessor;
    this.id = id;
  }

  public void reportError(String error) {
    id.reportError(error);
  }

  public IdNode getIdNode() {
    return id;
  }

  public ExpNode getAccessorNode() {
    return accessor;
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    accessor.unparse(code, 0);
    code.print('.');
    id.unparse(code, 0);
  }

  public void analyze(SymTable symbolTable) {
    Symb sym;
    accessor.analyze(symbolTable);
    if (accessor instanceof IdNode) {
      IdNode leftId = (IdNode)accessor;
      sym = leftId.getSym();
    } else {
      DotAccessExpNode dotAccess = (DotAccessExpNode)accessor;
      sym = dotAccess.getIdNode().getSym();
    }
    if (sym != null && sym instanceof StructVariableSymbol) {
      StructDefinitionSymbol structDef = ((StructVariableSymbol)sym).getStructDefinitionSymbol();
      Symb memberSym = structDef.getMember(id.getValue());
      if (memberSym == null) {
        id.reportError("Invalid struct field name");
      } else {
        id.link(memberSym);
      }
    } else {
      accessor.reportError("Dot-access of non-struct type");
    }
  }
}

class AssignNode extends ExpNode {

  private ExpNode leftHandSide;
  private ExpNode exp;

  public AssignNode(ExpNode leftHandSide, ExpNode exp) {
    this.leftHandSide = leftHandSide;
    this.exp = exp;
  }

  public void analyze(SymTable symbolTable) {
    leftHandSide.analyze(symbolTable);
    exp.analyze(symbolTable);
  }

  public void reportError(String error) {
    leftHandSide.reportError(error);
  }

  public void unparse(PrintWriter code, int indent) {
    if (indent >= 0) {
      addIndent(code, indent);
      code.print('(');
    }
    leftHandSide.unparse(code, 0);
    code.print(" = ");
    exp.unparse(code, 0);
    if (indent >= 0) {
      code.print(')');
    }
  }
}

class CallExpNode extends ExpNode {

  private List<ExpNode> expressions;
  private IdNode id;

  public CallExpNode(IdNode id, List<ExpNode> expressions) {
    this.expressions = expressions;
    this.id = id;
  }

  public void reportError(String error) {
    id.reportError(error);
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    id.unparse(code, 0);
    code.print('(');
    for (int i = 0; i < expressions.size(); i++) {
      if (i > 0) {
        code.print(", ");
      }
      expressions.get(i).unparse(code, 0);
    }
    code.print(')');
  }

  public void analyze(SymTable symbolTable) {
    id.analyze(symbolTable);
    for (ExpNode expression : expressions) {
      expression.analyze(symbolTable);
    }
  }
}

/*
 * Unary expression nodes
 */
abstract class UnaryExpNode extends ExpNode {

  protected ExpNode exp;

  public abstract String getOperatorValue();

  public UnaryExpNode(ExpNode exp) {
    this.exp = exp;
  }

  public void reportError(String error) {
    exp.reportError(error);
  }

  public void analyze(SymTable symbolTable) {
    exp.analyze(symbolTable);
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print('(');
    code.print(getOperatorValue());
    exp.unparse(code, 0);
    code.print(')');
  }
}

class UnaryMinusNode extends UnaryExpNode {

  public UnaryMinusNode(ExpNode exp) {
    super(exp);
  }

  public String getOperatorValue() {
    return "-";
  }
}

class NotNode extends UnaryExpNode {

  public NotNode(ExpNode exp) {
    super(exp);
  }

  public String getOperatorValue() {
    return "!";
  }
}

/*
 * Binary expression nodes
 */
abstract class BinaryExpNode extends ExpNode {

  protected ExpNode exp1;
  protected ExpNode exp2;

  public BinaryExpNode(ExpNode exp1, ExpNode exp2) {
    this.exp1 = exp1;
    this.exp2 = exp2;
  }

  public abstract String getOperatorValue();

  public void reportError(String error) {
    exp1.reportError(error);
  }

  public void analyze(SymTable symbolTable) {
    exp1.analyze(symbolTable);
    exp2.analyze(symbolTable);
  }

  public void unparse(PrintWriter code, int indent) {
    addIndent(code, indent);
    code.print('(');
    exp1.unparse(code, 0);
    code.print(' ');
    code.print(getOperatorValue());
    code.print(' ');
    exp2.unparse(code, 0);
    code.print(')');
  }
}

class PlusNode extends BinaryExpNode {

  public PlusNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public String getOperatorValue() {
    return "+";
  }
}

class MinusNode extends BinaryExpNode {

  public MinusNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public String getOperatorValue() {
    return "-";
  }
}

class TimesNode extends BinaryExpNode {

  public TimesNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public String getOperatorValue() {
    return "*";
  }
}

class DivideNode extends BinaryExpNode {

  public DivideNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public String getOperatorValue() {
    return "/";
  }
}

class AndNode extends BinaryExpNode {

  public AndNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public String getOperatorValue() {
    return "&&";
  }
}

class OrNode extends BinaryExpNode {

  public OrNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public String getOperatorValue() {
    return "||";
  }
}

class EqualsNode extends BinaryExpNode {

  public EqualsNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public String getOperatorValue() {
    return "==";
  }
}

class NotEqualsNode extends BinaryExpNode {

  public NotEqualsNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public String getOperatorValue() {
    return "!=";
  }
}

class LessNode extends BinaryExpNode {

  public LessNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public String getOperatorValue() {
    return "<";
  }
}

class GreaterNode extends BinaryExpNode {

  public GreaterNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public String getOperatorValue() {
    return ">";
  }
}

class LessEqNode extends BinaryExpNode {

  public LessEqNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public String getOperatorValue() {
    return "<=";
  }
}

class GreaterEqNode extends BinaryExpNode {

  public GreaterEqNode(ExpNode exp1, ExpNode exp2) {
    super(exp1, exp2);
  }

  public String getOperatorValue() {
    return ">=";
  }
}
