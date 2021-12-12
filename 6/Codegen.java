import java.io.PrintWriter;

import java.util.HashMap;

public class Codegen {

  public static PrintWriter p = null;
  public static HashMap<String, String> stringLabels = new HashMap<String, String>();

  public static final String TRUE = "1";
  public static final String FALSE = "0";

  public static final String FP = "$fp";
  public static final String SP = "$sp";
  public static final String RA = "$ra";
  public static final String V0 = "$v0";
  public static final String V1 = "$v1";
  public static final String A0 = "$a0";
  public static final String T0 = "$t0";
  public static final String T1 = "$t1";

  private static final int MAXLEN = 4;

  private static int labelCounter = 0;

  public static void generateWithComment(String opcode, String comment,
                                      String arg1, String arg2, String arg3) {
    int space = MAXLEN - opcode.length() + 2;

    p.print("\t" + opcode);
    if (arg1 != "") {
      for (int k = 1; k <= space; k++) 
        p.print(" ");
      p.print(arg1);
      if (arg2 != "") {
        p.print(", " + arg2);
        if (arg3 != "")
          p.print(", " + arg3);
      }
    }
    if (comment != "") 
      p.print("\t\t#" + comment);
    p.println();
  }

  public static void generateWithComment(String opcode, String comment,
                                          String arg1, String arg2) {
    generateWithComment(opcode, comment, arg1, arg2, "");
  }

  public static void generateWithComment(String opcode, String comment,
                                          String arg1) {
    generateWithComment(opcode, comment, arg1, "", "");
  }

  public static void generateWithComment(String opcode, String comment) {
    generateWithComment(opcode, comment, "", "", "");
  }

  public static void generate(String opcode, String arg1, String arg2,
                              String arg3) {
    int space = MAXLEN - opcode.length() + 2;

    p.print("\t" + opcode);
    if (arg1 != "") {
      for (int k = 1; k <= space; k++) 
        p.print(" ");
      p.print(arg1);
      if (arg2 != "") {
        p.print(", " + arg2);
        if (arg3 != "") 
          p.print(", " + arg3);
      }
    }
    p.println();
  }

  public static void generate(String opcode, String arg1, String arg2) {
    generate(opcode, arg1, arg2, "");
  }

  public static void generate(String opcode, String arg1) {
    generate(opcode, arg1, "", "");
  }

  public static void generate(String opcode) {
    generate(opcode, "", "", "");
  }

  public static void generate(String opcode, String arg1, String arg2,
                              int arg3) {
    int space = MAXLEN - opcode.length() + 2;

    p.print("\t" + opcode);
    for (int k = 1; k <= space; k++) 
      p.print(" ");
    p.println(arg1 + ", " + arg2 + ", " + arg3);
  }

  public static void generate(String opcode, String arg1, int arg2) {
    int space = MAXLEN - opcode.length() + 2;

    p.print("\t" + opcode);
    for (int k = 1; k <= space; k++) 
      p.print(" ");
    p.println(arg1 + ", " + arg2);
  }

  public static void generateIndexed(String opcode, String arg1, String arg2,
                                      int arg3, String comment) {
    int space = MAXLEN - opcode.length() + 2;

    p.print("\t" + opcode);
    for (int k = 1; k <= space; k++) 
      p.print(" ");
    p.print(arg1 + ", " + arg3 + "(" + arg2 + ")");
    if (comment != "")
      p.print("\t#" + comment);
    p.println();
  }
  
  public static void generateIndexed(String opcode, String arg1, String arg2,
                                      int arg3) {
    generateIndexed(opcode, arg1, arg2, arg3, "");
  }

  public static void generateLabeled(String label, String opcode,
                                      String comment, String arg1) {
    int space = MAXLEN - opcode.length() + 2;

    p.print(label + ":");
    p.print("\t" + opcode);
    if (arg1 != "") {
      for (int k = 1; k <= space; k++) {
        p.print(" ");
      }
      p.print(arg1);
    }
    if (comment != "") 
      p.print("\t# " + comment);
    p.println();
  }

  public static void generateLabeled(String label, String opcode,
                                      String comment) {
    generateLabeled(label, opcode, comment, "");
  }

  public static void genPush(String s) {
    generateIndexed("sw", s, SP, 0, "PUSH");
    generate("subu", SP, SP, 4);
  }

  public static void genPop(String s) {
    generateIndexed("lw", s, SP, 4, "POP");
    generate("addu", SP, SP, 4);
  }

  public static void genLabel(String label, String comment) {
    p.print(label + ":");
    if (comment != "") {
      p.print("\t\t" + "# " + comment);
    }
    p.println();
  }

  public static void genLabel(String label) {
    genLabel(label, "");
  }

  public static void generateGlobalVariable(String name) {
    p.printf("\t.data\n\t.align 2\n_%s:\t.space 4\n", name);
  }

  public static String nextLabel() {
    return String.format(".L%d", labelCounter++);
  }
}
