###
# This Makefile can be used to make a parser for the b language
# (parser.class) and to make a program (P5.class) that tests the parser and
# the unparse methods in ast.java.
#
# make clean removes all generated files.
#
###

JC = javac

CP = ./deps:.

P5.class: P5.java parser.class Yylex.class ASTnode.class
	$(JC) -g -cp $(CP) P5.java

parser.class: parser.java ASTnode.class Yylex.class ErrMsg.class
	$(JC) -g -cp $(CP) parser.java

parser.java: b.cup
	java -cp $(CP) java_cup.Main < b.cup

Yylex.class: b.jlex.java sym.class ErrMsg.class
	$(JC) -g -cp $(CP) b.jlex.java

ASTnode.class: ast.java Type.java Symb.class
	$(JC) -g -cp $(CP) ast.java Type.java

b.jlex.java: b.jlex sym.class
	java -cp $(CP) JLex.Main b.jlex

sym.class: sym.java
	$(JC) -g -cp $(CP) sym.java

sym.java: b.cup
	java java_cup.Main < b.cup

ErrMsg.class: ErrMsg.java
	$(JC) -g -cp $(CP) ErrMsg.java

Symb.class: Symb.java Type.class ast.java
	$(JC) -g -cp $(CP) Symb.java ast.java

SymTable.class: SymTable.java Symb.class DuplicateSymException.class EmptySymTableException.class WrongArgumentException.class
	$(JC) -g -cp $(CP) SymTable.java

Type.class: Type.java
	$(JC) -g -cp $(CP) Type.java ast.java

WrongArgumentException.class: WrongArgumentException.java
	$(JC) -g -cp $(CP) WrongArgumentException.java

DuplicateSymException.class: DuplicateSymException.java
	$(JC) -g -cp $(CP) DuplicateSymException.java

EmptySymTableException.class: EmptySymTableException.java
	$(JC) -g -cp $(CP) EmptySymTableException.java

###
# test
#
test:
	java -cp $(CP) P5 test.b test.out

###
# clean
###
clean:
	rm -f *~ *.class parser.java b.jlex.java sym.java

cleantest:
	rm -f test.out
