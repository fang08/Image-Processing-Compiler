package cop5556sp18;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp18.SimpleParser;
import cop5556sp18.Scanner;
import cop5556sp18.SimpleParser.SyntaxException;
import cop5556sp18.Scanner.LexicalException;

public class SimpleParserTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}


	//creates and returns a parser for the given input.
	private SimpleParser makeParser(String input) throws LexicalException {
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		SimpleParser parser = new SimpleParser(scanner);
		return parser;
	}
	
	

	/**
	 * Simple test case with an empty program.  This throws an exception 
	 * because it lacks an identifier and a block. The test case passes because
	 * it expects an exception
	 *  
	 * @throws LexicalException
	 * @throws SyntaxException 
	 */
	@Test
	public void testEmpty() throws LexicalException, SyntaxException {
		String input = "";  //The input is the empty string.  
		SimpleParser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	/**
	 * Smallest legal program.
	 *   
	 * @throws LexicalException
	 * @throws SyntaxException 
	 */
	@Test
	public void testSmallest() throws LexicalException, SyntaxException {
		String input = "b{}";  
		SimpleParser parser = makeParser(input);
		parser.parse();
	}	
	
	
	//This test should pass in your complete parser.  It will fail in the starter code.
	//Of course, you would want a better error message. 
	@Test
	public void testDec0() throws LexicalException, SyntaxException {
		String input = "b{int c;}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	
	// Declarations
	@Test
	public void test01() throws LexicalException, SyntaxException {
		String input = "t{float f[];}";
		SimpleParser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void test02() throws LexicalException, SyntaxException {
		String input = "t{boolean b05; int xyz;}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void test03() throws LexicalException, SyntaxException {
		String input = "t{filename ;}";
		SimpleParser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void test04() throws LexicalException, SyntaxException {
		String input = "t{image ig;}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void test05() throws LexicalException, SyntaxException {
		String input = "t{image pic [3.5, false];}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void test06() throws LexicalException, SyntaxException {
		String input = "t{image pic [+0, -cat;}";
		SimpleParser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	// Expressions in declaration
	// + primary
	// FunctionApplication & PredefinedName
	@Test
	public void test07() throws LexicalException, SyntaxException {
		String input = "test {image pic [cart_x (Z), polar_a[default_height,default_width]];}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	// + PixelConstructor
	@Test
	public void test08() throws LexicalException, SyntaxException {
		String input = "test {image pic [alpha[!27,photos], red (sin[<<+1,-1,0,!3>>,width(true)])];}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	// + PixelExpression, PixelSelector
	@Test
	public void test09() throws LexicalException, SyntaxException {
		String input = "test {image pic [ids[(true),atan(<<true, +-1, ++3.5, !-+0>>)],log(uno[true,false])];}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void test10() throws LexicalException, SyntaxException {
		String input = "test {image pic [(iden [abs,true]),!+++--4.5];}"; //abs does not belongs to primary
		SimpleParser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	// ~expressions
	@Test
	public void test11() throws LexicalException, SyntaxException {
		String input = "test {image pic [+1,-1!];}";
		SimpleParser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void test12() throws LexicalException, SyntaxException {
		String input = "test {image pic [+!-5**true,1025%5];}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void test13() throws LexicalException, SyntaxException {
		String input = "test {image pic [47.5**3*0**5+15,2/3*4-200<=135+7*4];}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void test14() throws LexicalException, SyntaxException {
		String input = "test {image pic [100>=10+7%3 !=true ? 0 : 1, 379**0>3*5 == true<3 & rr ? 3 | 5 : 3.5];}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void test15() throws LexicalException, SyntaxException {
		String input = "test {image pic [!true == 1+!27 & 3<6 | 0 ? aa : bb, +-12 | 4&5 | 3**9];}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void test16() throws LexicalException, SyntaxException {
		String input = "test {image pic [3**, 5>5];}";
		SimpleParser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	// Statements
	// StatementInput
	@Test
	public void test17() throws LexicalException, SyntaxException {
		String input = "test {input files from @ green (files);}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void test18() throws LexicalException, SyntaxException {
		String input = "test {inpu files from @ true;}";
		SimpleParser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	// StatementWrite
	@Test
	public void test19() throws LexicalException, SyntaxException {
		String input = "test {write apples to apps;}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void test20() throws LexicalException, SyntaxException {
		String input = "test {write default_height to apps;}";
		SimpleParser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	// StatementAssignment
	@Test
	public void test21() throws LexicalException, SyntaxException {
		String input = "test {banana := yellow;}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void test22() throws LexicalException, SyntaxException {
		String input = "test {banana := red;}";
		SimpleParser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void test23() throws LexicalException, SyntaxException {
		String input = "test {alphabet == abcde;}";
		SimpleParser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void test24() throws LexicalException, SyntaxException {
		String input = "test {ice[<<1,2,3,4>>, !cold ? true : false] := nice;}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void test25() throws LexicalException, SyntaxException {
		String input = "test { blue (sea [0.5+true != 0 | 10%5 | nan, polar_r(0.4) ? ture : false]) := !0;}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void test26() throws LexicalException, SyntaxException {
		String input = "test {red ([a,b]) := true;}";
		SimpleParser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	// StatementWhile
	@Test
	public void test27() throws LexicalException, SyntaxException {
		String input = "test {while (!true) {write smile to people; boolean ts;} ;}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void test28() throws LexicalException, SyntaxException {
		String input = "test {while (height((8 !=7))) {input fruit from @ trees; while(66) {filename water; while(end) {float aa;} ;} ;} ;}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	// StatementIf
	@Test
	public void test29() throws LexicalException, SyntaxException {
		String input = "test { if (left | right ? 1 : 0) { if(!black) {abc := def ;};};}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void test30() throws LexicalException, SyntaxException {
		String input = "test { if (cart_y(-1)) {green(wood[x,y]) := great;};}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	// StatementShow
	@Test
	public void test31() throws LexicalException, SyntaxException {
		String input = "test {show expect ? +1 : -1 ;}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
	
	// StatementSleep
	@Test
	public void test32() throws LexicalException, SyntaxException {
		String input = "test {sleep goodnight;}";
		SimpleParser parser = makeParser(input);
		parser.parse();
	}
}