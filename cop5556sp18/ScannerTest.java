package cop5556sp18;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp18.Scanner.Kind;
import cop5556sp18.Scanner.LexicalException;
import cop5556sp18.Scanner.Token;
import static cop5556sp18.Scanner.Kind.*;

public class ScannerTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 *Retrieves the next token and checks that it is an EOF token. 
	 *Also checks that this was the last token.
	 *
	 * @param scanner
	 * @return the Token that was retrieved
	 */
	
	Token checkNextIsEOF(Scanner scanner) {
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF, token.kind);
		assertFalse(scanner.hasTokens());
		return token;
	}
	
	/**
	 * Retrieves the next token and checks that its kind, position, length, line, and position in line
	 * match the given parameters.
	 * 
	 * @param scanner
	 * @param kind
	 * @param pos
	 * @param length
	 * @param line
	 * @param pos_in_line
	 * @return  the Token that was retrieved
	 */
	Token checkNext(Scanner scanner, Scanner.Kind kind, int pos, int length, int line, int pos_in_line) {
		Token t = scanner.nextToken();
		assertEquals(kind, t.kind);
		assertEquals(pos, t.pos);
		assertEquals(length, t.length);
		assertEquals(line, t.line());
		assertEquals(pos_in_line, t.posInLine());
		return t;
	}

	/**
	 * Retrieves the next token and checks that its kind and length match the given
	 * parameters.  The position, line, and position in line are ignored.
	 * 
	 * @param scanner
	 * @param kind
	 * @param length
	 * @return  the Token that was retrieved
	 */
	Token checkNext(Scanner scanner, Scanner.Kind kind, int length) {
		Token t = scanner.nextToken();
		assertEquals(kind, t.kind);
		assertEquals(length, t.length);
		return t;
	}

	/**
	 * Simple test case with an empty program.  The only Token will be the EOF Token.
	 *   
	 * @throws LexicalException
	 */
	@Test
	public void testEmpty() throws LexicalException {
		String input = "";  //The input is the empty string.  This is legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		checkNextIsEOF(scanner);  //Check that the only token is the EOF token.
	}
	
	/**
	 * Test illustrating how to put a new line in the input program and how to
	 * check content of tokens.
	 * 
	 * Because we are using a Java String literal for input, we use \n for the
	 * end of line character. (We should also be able to handle \n, \r, and \r\n
	 * properly.)
	 * 
	 * Note that if we were reading the input from a file, the end of line 
	 * character would be inserted by the text editor.
	 * Showing the input will let you check your input is 
	 * what you think it is.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void testSemi() throws LexicalException {
		String input = ";;\n;;";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, SEMI, 3, 1, 2, 1);
		checkNext(scanner, SEMI, 4, 1, 2, 2);
		checkNextIsEOF(scanner);
	}
	
	/**
	 * This example shows how to test that your scanner is behaving when the
	 * input is illegal.  In this case, we are giving it an illegal character '~' in position 2
	 * 
	 * The example shows catching the exception that is thrown by the scanner,
	 * looking at it, and checking its contents before rethrowing it.  If caught
	 * but not rethrown, then JUnit won't get the exception and the test will fail.  
	 * 
	 * The test will work without putting the try-catch block around 
	 * new Scanner(input).scan(); but then you won't be able to check 
	 * or display the thrown exception.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void failIllegalChar() throws LexicalException {
		String input = ";;~";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //Catch the exception
			show(e);                    //Display it
			assertEquals(2,e.getPos()); //Check that it occurred in the expected position
			throw e;                    //Rethrow exception so JUnit will see it
		}
	}

	@Test
	public void testParens() throws LexicalException {
		String input = "()";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, LPAREN, 0, 1, 1, 1);
		checkNext(scanner, RPAREN, 1, 1, 1, 2);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testAllKeyWords() throws LexicalException {
		String input = "Z default_width default_height show write to input from" + 
				" cart_x cart_y polar_a polar_r abs sin cos atan log image int" + 
				" float filename boolean red blue green alpha while if width" +
				" height true false sleep";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, KW_Z, 1);
		checkNext(scanner, KW_default_width, 13);
		checkNext(scanner, KW_default_height, 14);
		checkNext(scanner, KW_show, 4);
		checkNext(scanner, KW_write, 5);
		checkNext(scanner, KW_to, 2);
		checkNext(scanner, KW_input, 5);
		checkNext(scanner, KW_from, 4);
		checkNext(scanner, KW_cart_x, 6);
		checkNext(scanner, KW_cart_y, 6);
		checkNext(scanner, KW_polar_a, 7);
		checkNext(scanner, KW_polar_r, 7);
		checkNext(scanner, KW_abs, 3);
		checkNext(scanner, KW_sin, 3);
		checkNext(scanner, KW_cos, 3);
		checkNext(scanner, KW_atan, 4);
		checkNext(scanner, KW_log, 3);
		checkNext(scanner, KW_image, 5);
		checkNext(scanner, KW_int, 3);
		checkNext(scanner, KW_float, 5);
		checkNext(scanner, KW_filename, 8);
		checkNext(scanner, KW_boolean, 7);
		checkNext(scanner, KW_red, 3);
		checkNext(scanner, KW_blue, 4);
		checkNext(scanner, KW_green, 5);
		checkNext(scanner, KW_alpha, 5);
		checkNext(scanner, KW_while, 5);
		checkNext(scanner, KW_if, 2);
		checkNext(scanner, KW_width, 5);
		checkNext(scanner, KW_height, 6);
		checkNext(scanner, BOOLEAN_LITERAL, 4);
		checkNext(scanner, BOOLEAN_LITERAL, 5);
		checkNext(scanner, KW_sleep, 5);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void test1() throws LexicalException {
		String input = "123/*ab*/.2";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 3);
		checkNext(scanner, FLOAT_LITERAL, 2);
		checkNextIsEOF(scanner);
	}
	
	
	@Test
	public void test2() throws LexicalException {
		String input = "A_$8>><*true";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, IDENTIFIER, 4);
		checkNext(scanner, RPIXEL, 2);
		checkNext(scanner, OP_LT, 1);
		checkNext(scanner, OP_TIMES, 1);
		checkNext(scanner, BOOLEAN_LITERAL, 4);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void test3() throws LexicalException {
		String input = "0x Z";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 1);
		checkNext(scanner, IDENTIFIER, 1);
		checkNext(scanner, KW_Z, 1);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void test4() throws LexicalException {
		String input = ">>>>>=>";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, RPIXEL, 2);
		checkNext(scanner, RPIXEL, 2);
		checkNext(scanner, OP_GE, 2);
		checkNext(scanner, OP_GT, 1);
		checkNextIsEOF(scanner);
	}

	@Test   
	public void testFloatOverflow1() throws LexicalException {
		String input = "12312132321321426465765865675138497316480183476151984726148767576443.43434";
		show(input);
		thrown.expect(LexicalException.class);
		try {
			new Scanner(input).scan();
		}catch(LexicalException e) {
			show(e);
			//assertEquals(0,e.getPos());
			throw e;
		}
	}
	
	@Test   
	public void testFloatOverflow2() throws LexicalException {
		String input = "1231213232132142646576586567573428916349109234767289451834593284918757644378964.";
		show(input);
		thrown.expect(LexicalException.class);
		try {
			new Scanner(input).scan();
		}catch(LexicalException e) {
			show(e);
			//assertEquals(0,e.getPos());
			throw e;
		}
	}
	
//	@Test   
//	public void testFloatOverflow3() throws LexicalException {
//		String input = ".127585923213214264657658656757576443788598648120384762834521293714239487281964";
//		show(input);
//		thrown.expect(LexicalException.class);
//		try {
//			new Scanner(input).scan();
//		}catch(LexicalException e) {
//			show(e);
//			//assertEquals(0,e.getPos());
//			throw e;
//		}
//	}
	
	@Test   
	public void testIntOverflow() throws LexicalException {
		String input = "12312854213214264657658656757576443788598648120384762834521293714239487281964";
		show(input);
		thrown.expect(LexicalException.class);
		try {
			new Scanner(input).scan();
		}catch(LexicalException e) {
			show(e);
			//assertEquals(0,e.getPos());
			throw e;
		}
	}
	
	@Test
	public void testAllSep() throws LexicalException {
		String input = "()[];,{}<<>>.";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, LPAREN, 1);
		checkNext(scanner, RPAREN, 1);
		checkNext(scanner, LSQUARE, 1);
		checkNext(scanner, RSQUARE, 1);
		checkNext(scanner, SEMI, 1);
		checkNext(scanner, COMMA, 1);
		checkNext(scanner, LBRACE, 1);
		checkNext(scanner, RBRACE, 1);
		checkNext(scanner, LPIXEL, 2);
		checkNext(scanner, RPIXEL, 2);
		checkNext(scanner, DOT, 1);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testAllOp() throws LexicalException {
		String input = "><!?: ==!=<=>=&|+-*/%**:=@";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, OP_GT, 1);
		checkNext(scanner, OP_LT, 1);
		checkNext(scanner, OP_EXCLAMATION, 1);
		checkNext(scanner, OP_QUESTION, 1);
		checkNext(scanner, OP_COLON, 1);
		checkNext(scanner, OP_EQ, 2);
		checkNext(scanner, OP_NEQ, 2);
		checkNext(scanner, OP_LE, 2);
		checkNext(scanner, OP_GE, 2);
		checkNext(scanner, OP_AND, 1);
		checkNext(scanner, OP_OR, 1);
		checkNext(scanner, OP_PLUS, 1);
		checkNext(scanner, OP_MINUS, 1);
		checkNext(scanner, OP_TIMES, 1);
		checkNext(scanner, OP_DIV, 1);
		checkNext(scanner, OP_MOD, 1);
		checkNext(scanner, OP_POWER, 2);
		checkNext(scanner, OP_ASSIGN, 2);
		checkNext(scanner, OP_AT, 1);
		checkNextIsEOF(scanner);
	}
	

	@Test
	public void test5() throws LexicalException {
		String input = "/**xyz";
		show(input);
		thrown.expect(LexicalException.class);
		try {
			new Scanner(input).scan();
		}catch(LexicalException e) {
			show(e);
			//assertEquals(0,e.getPos());
			throw e;
		}
	}

	@Test
	public void test6() throws LexicalException {
		String input = "/*/**/*/";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, OP_TIMES, 1);
		checkNext(scanner, OP_DIV, 1);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void test7() throws LexicalException {
		String input = "/*/012**/abc";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, IDENTIFIER, 3);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void test8() throws LexicalException {
		String input = "012345.input";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL, 1);
		checkNext(scanner, FLOAT_LITERAL, 6);
		checkNext(scanner, KW_input, 5);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void test9() throws LexicalException {
		String input = "abc.135";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, IDENTIFIER, 3);
		checkNext(scanner, FLOAT_LITERAL, 4);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void test10() throws LexicalException {
		String input = "$Axy_";
		show(input);
		thrown.expect(LexicalException.class);
		try {
			new Scanner(input).scan();
		}catch(LexicalException e) {
			show(e);
			//assertEquals(0,e.getPos());
			throw e;
		}
	}
	
	@Test
	public void test11() throws LexicalException {
		String input = "/***/";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNextIsEOF(scanner);
	}
}
