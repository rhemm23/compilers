import java.util.*;
import java.io.*;
import java_cup.runtime.*;  // defines Symbol

/**
 * This program is to be used to test the cflat scanner.
 * This version is set up to test all tokens, but more code is needed to test 
 * other aspects of the scanner (e.g., input that causes errors, character 
 * numbers, values associated with tokens).
 */
public class P2 {
    public static void main(String[] args) throws IOException {
                                           // exception may be thrown by yylex
        // test all tokens
        testAllTokens();
        CharNum.num = 1;
        testStringLiteralErrors();
        CharNum.num = 1;
        testIllegalCharacters();
        CharNum.num = 1;
        testComments();
        CharNum.num = 1;
        testBadIntegerLiterals();
        CharNum.num = 1;
        testCharNumbers();
    }

    /**
     * testAllTokens
     *
     * Open and read from file allTokens.txt
     * For each token read, write the corresponding string to allTokens.out
     * If the input file contains all tokens, one per line, we can verify
     * correctness of the scanner by comparing the input and output files
     * (e.g., using a 'diff' command).
     */
    private static void testAllTokens() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("allTokens.in");
            outFile = new PrintWriter(new FileWriter("allTokens.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("File allTokens.in not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("allTokens.out cannot be opened.");
            System.exit(-1);
        }

        // create and call the scanner
        Yylex my_scanner = new Yylex(inFile);
        Symbol my_token = my_scanner.next_token();
        while (my_token.sym != sym.EOF) {
            switch (my_token.sym) {
            case sym.BOOL:
                outFile.println("bool");
                break;
			case sym.INT:
                outFile.println("int");
                break;
            case sym.VOID:
                outFile.println("void");
                break;
            case sym.TRUE:
                outFile.println("tru"); 
                break;
            case sym.FALSE:
                outFile.println("fls"); 
                break;
            case sym.STRUCT:
                outFile.println("struct"); 
                break;
            case sym.RECEIVE:
                outFile.println("receive"); 
                break;
            case sym.PRINT:
                outFile.println("print");
                break;				
            case sym.IF:
                outFile.println("if");
                break;
            case sym.ELSE:
                outFile.println("else");
                break;
            case sym.WHILE:
                outFile.println("while");
                break;
            case sym.RETURN:
                outFile.println("ret");
                break;
            case sym.ID:
                outFile.println(((IdTokenVal)my_token.value).idVal);
                break;
            case sym.INTLITERAL:  
                outFile.println(((IntLitTokenVal)my_token.value).intVal);
                break;
            case sym.STRINGLITERAL: 
                outFile.println(((StrLitTokenVal)my_token.value).strVal);
                break;    
            case sym.LCURLY:
                outFile.println("{");
                break;
            case sym.RCURLY:
                outFile.println("}");
                break;
            case sym.LPAREN:
                outFile.println("(");
                break;
            case sym.RPAREN:
                outFile.println(")");
                break;
            case sym.SEMICOLON:
                outFile.println(";");
                break;
            case sym.COMMA:
                outFile.println(",");
                break;
            case sym.DOT:
                outFile.println(".");
                break;
            case sym.WRITE:
                outFile.println("<<");
                break;
            case sym.READ:
                outFile.println(">>");
                break;				
            case sym.PLUSPLUS:
                outFile.println("++");
                break;
            case sym.MINUSMINUS:
                outFile.println("--");
                break;	
            case sym.PLUS:
                outFile.println("+");
                break;
            case sym.MINUS:
                outFile.println("-");
                break;
            case sym.TIMES:
                outFile.println("*");
                break;
            case sym.DIVIDE:
                outFile.println("/");
                break;
            case sym.NOT:
                outFile.println("!");
                break;
            case sym.AND:
                outFile.println("&&");
                break;
            case sym.OR:
                outFile.println("||");
                break;
            case sym.EQUALS:
                outFile.println("==");
                break;
            case sym.NOTEQUALS:
                outFile.println("!=");
                break;
            case sym.LESS:
                outFile.println("<");
                break;
            case sym.GREATER:
                outFile.println(">");
                break;
            case sym.LESSEQ:
                outFile.println("<=");
                break;
            case sym.GREATEREQ:
                outFile.println(">=");
                break;
			case sym.ASSIGN:
                outFile.println("=");
                break;
			default:
				outFile.println("UNKNOWN TOKEN");
            } // end switch

            my_token = my_scanner.next_token();
        } // end while
        outFile.close();
    }


    /**
     * testStringLiteralErrors
     *
     * Open and read from file testStringLiteralErrors.txt
     * Print 'unterminated string literal ignored'
     * for statements with unterminated string literals.
     * Print 'unterminated string literal with bad escaped character ignored'
     * for unterminated statements with bad escape characters.
     * Print 'string literal with bad escaped character ignored'
     * for a bad escape character only.
     * Expected output is also printed to the console
     */
    private static void testStringLiteralErrors() throws IOException {
        // open input and output files
        System.out.println("TESTING Unterminated String Errors:");
        FileReader inFile = null;
        try {
            inFile = new FileReader("testStringLiteralErrors.in");
        } catch (FileNotFoundException ex) {
            System.err.println("File testStringLiteralErrors.in not found.");
            System.exit(-1);
        }
        
        // Capture error output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream errorOutput = new PrintStream(baos);
        System.setErr(errorOutput);

        // create and call the scanner
        Yylex my_scanner = new Yylex(inFile);
        Symbol my_token = my_scanner.next_token();
        while (my_token.sym != sym.EOF) {
            my_token = my_scanner.next_token();
        }

        String[] expectedOutputLines = new String[] {
            "1:1 ***ERROR*** unterminated string literal ignored",
            "2:1 ***ERROR*** unterminated string literal ignored",
            "3:1 ***ERROR*** unterminated string literal ignored",
            "4:1 ***ERROR*** string literal with bad escaped character ignored",
            "6:1 ***ERROR*** string literal with bad escaped character ignored",
            "7:1 ***ERROR*** unterminated string literal with bad escaped character ignored",
            "8:1 ***ERROR*** unterminated string literal ignored"
        };
        assertErrorMessagesArePrinted(baos, expectedOutputLines);
    }

    /**
     * testIllegalCharacters
     *
     * Open and read from file testIllegalCharacters.txt
     * Throws errors for illegal character use
     * Ensure console output is the same as expected
     */
    private static void testIllegalCharacters() throws IOException {
        // open input and output files
        System.out.println("TESTING Illegal Characters:");
        FileReader inFile = null;
        try {
            inFile = new FileReader("testIllegalCharacters.in");
        } catch (FileNotFoundException ex) {
            System.err.println("File testIllegalCharacters.in not found.");
            System.exit(-1);
        }
        
        // Capture error output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream errorOutput = new PrintStream(baos);
        System.setErr(errorOutput);

        // create and call the scanner
        Yylex my_scanner = new Yylex(inFile);
        Symbol my_token = my_scanner.next_token();
        while (my_token.sym != sym.EOF) {
            my_token = my_scanner.next_token();
        }
        String[] expectedOutputLines = new String[] {
            "1:1 ***ERROR*** illegal character ignored: ~",
            "2:1 ***ERROR*** illegal character ignored: $",
            "3:1 ***ERROR*** illegal character ignored: @"
        };
        assertErrorMessagesArePrinted(baos, expectedOutputLines);
    }

    /**
     * testBadIntegerLiterals
     *
     * Open and read from file testBadIntegerLiterals.txt
     * Throws errors for bad interger literal use
     * Ensure console output is the same as expected
     */
    private static void testBadIntegerLiterals() throws IOException {
        // open input and output files
        System.out.println("TESTING Bad Integer Literals:");
        FileReader inFile = null;
        try {
            inFile = new FileReader("testBadIntegerLiterals.in");
        } catch (FileNotFoundException ex) {
            System.err.println("File testBadIntegerLiterals.in not found.");
            System.exit(-1);
        }
        
        // create and call the scanner
        Yylex my_scanner = new Yylex(inFile);
        Symbol my_token = my_scanner.next_token();
        int lineNum = 1;
        int charNum = 1;

        // Capture error output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream errorOutput = new PrintStream(baos);
        System.setErr(errorOutput);

        while (my_token.sym != sym.EOF) {
            TokenVal token_val = (TokenVal)my_token.value;
            if (token_val.linenum != lineNum && token_val.charnum != charNum) {
                System.out.println("ERROR testBadIntegerLiterals: linenum or charnum is not correct.");
            }
            switch (lineNum) {
                case 1: 
                    if (((IntLitTokenVal)my_token.value).intVal != 10) {
                        System.out.println("ERROR testBadIntegerLiterals: integer should be 10");
                    }
                    break;

                case 2:
                    if (((IntLitTokenVal)my_token.value).intVal != 2147483646) {
                        System.out.println("ERROR testBadIntegerLiterals: integer should be 2147483646");
                    }
                    break;

                case 3:
                case 4:
                case 5:
                    if (((IntLitTokenVal)my_token.value).intVal != 2147483647) {
                        System.out.println("ERROR testBadIntegerLiterals: integer should be 2147483647");
                    }
                    break;
            }
            lineNum = lineNum + 1;
            my_token = my_scanner.next_token();
        }
        String[] expectedOutput = new String[] {
            "4:1 ***WARNING*** integer literal too large; using max value",
            "5:1 ***WARNING*** integer literal too large; using max value"
        };
        assertErrorMessagesArePrinted(baos, expectedOutput);
    }

    /**
     * testCharNumbers
     *
     * Open and read from file testCharNumbers.in
     * Assures that tokens read on the same line have the correct CharNum
     */
    private static void testCharNumbers() throws IOException {
        // open input and output files
        System.out.println("TESTING char numbers:");
        FileReader inFile = null;
        try {
            inFile = new FileReader("testCharNumbers.in");
        } catch (FileNotFoundException ex) {
            System.err.println("File testCharNumbers.inn not found.");
            System.exit(-1);
        }

        // create and call the scanner
        Yylex my_scanner = new Yylex(inFile);
        Symbol my_token = my_scanner.next_token();
        int lineNum = 1;
        int charNum = 1;

        // Capture error output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream errorOutput = new PrintStream(baos);
        System.setErr(errorOutput);

        int[] expectedCharNums = new int[] {
            1,
            4,
            7,
            10,
            13,
            21,
            27,
            33,
            42,
            45,
            51,
            54,
            57,
            59
        };

        boolean passed = true;
        for (int i = 0; i < 14; i++) {
            TokenVal token = (TokenVal)my_token.value;
            if (token.charnum != expectedCharNums[i]) {
                System.out.println(String.format("FAILED: Wrong CharNum for token %d", token.charnum));
                passed = false;
                break;
            }
            my_token = my_scanner.next_token();
        }
        if (passed) {
            System.out.println("\tSUCCESS: All tokens have correct CharNum\n");
        }
    }

    /**
     * testIllegalCharacters
     *
     * Open and read from file testIllegalCharacters.txt
     * Throws errors for illegal character use
     * Ensure console output is the same as expected
     */
    private static void testComments() throws IOException {
        // open input and output files
        System.out.println("TESTING comments:");
        FileReader inFile = null;
        try {
            inFile = new FileReader("testComments.in");
        } catch (FileNotFoundException ex) {
            System.err.println("File testComments.in not found.");
            System.exit(-1);
        }
        
        // create and call the scanner
        Yylex scan = new Yylex(inFile);
        if (scan.next_token().sym != sym.EOF) {
            System.out.println("\tFAILED: Comment interpreted as symbol\n");
        } else {
            System.out.println("\tSUCCESS: Comments correctly ignored\n");
        }
    }

    /**
     * assertErrorMessagesArePrinted
     *
     * Takes an output stream and an array of error messages
     * Reads from the output stream and splits by newline
     * Assures all expected output messages are received from the stream
     */
    private static void assertErrorMessagesArePrinted(ByteArrayOutputStream errorStream, String[] expectedOutputLines) {
        String[] outputLines = errorStream.toString().split("\n");
        if (outputLines.length != expectedOutputLines.length) {
            System.out.println("\tFAILED: Number of output lines did not match expected\n");
        } else {
            boolean passed = true;
            for (int i = 0; i < outputLines.length; i++) {
                if (!outputLines[i].equals(expectedOutputLines[i])) {
                    System.out.println("\tFAILED: Error output did not match expected\n");
                    passed = false;
                    break;
                }
            }
            if (passed) {
                System.out.println("\tSUCCESS: All error messages are correct\n");
            }
        }
    }
}
