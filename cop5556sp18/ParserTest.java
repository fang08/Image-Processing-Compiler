package cop5556sp18;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp18.Scanner.Kind;
import cop5556sp18.Parser;
import cop5556sp18.Scanner;
import cop5556sp18.AST.*;
import cop5556sp18.Parser.SyntaxException;
import cop5556sp18.Scanner.LexicalException;
import cop5556sp18.Scanner.Token;
import static cop5556sp18.Scanner.Kind.*;

import java.util.ArrayList;
import java.util.List;

public class ParserTest {

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
	private Parser makeParser(String input) throws LexicalException {
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);
		return parser;
	}
	

	
	/**
	 * Simple test case with an empty program.  This throws an exception 
	 * because it lacks an identifier and a block
	 *   
	 * @throws LexicalException
	 * @throws SyntaxException 
	 */
	@Test
	public void testEmpty() throws LexicalException, SyntaxException {
		String input = "";  //The input is the empty string.  
		thrown.expect(SyntaxException.class);
		Parser parser = makeParser(input);
		@SuppressWarnings("unused")
		Program p = parser.parse();
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
		Parser parser = makeParser(input);
		Program p = parser.parse();
		show(p);
		assertEquals("b", p.progName);
		assertEquals(0, p.block.decsOrStatements.size());
	}	
	

	/**
	 * Checks that an element in a block is a declaration with the given type and name.
	 * The element to check is indicated by the value of index.
	 * 
	 * @param block
	 * @param index
	 * @param type
	 * @param name
	 * @return
	 */
	Declaration checkDec(Block block, int index, Kind type,
			String name) {
		ASTNode node = block.decOrStatement(index);
		assertEquals(Declaration.class, node.getClass());
		Declaration dec = (Declaration) node;
		assertEquals(type, dec.type);
		assertEquals(name, dec.name);
		return dec;
	}	
	
	@Test
	public void testDec0() throws LexicalException, SyntaxException {
		String input = "b{int c; image j;}";
		Parser parser = makeParser(input);
		Program p = parser.parse();
		show(p);	
		checkDec(p.block, 0, Kind.KW_int, "c");
		checkDec(p.block, 1, Kind.KW_image, "j");
	}
	
	
	/** This test illustrates how you can test specific grammar elements by themselves by
	 * calling the corresponding parser method directly, instead of calling parse.
	 * This requires that the methods are visible (not private). 
	 * 
	 * @throws LexicalException
	 * @throws SyntaxException
	 */
	
	@Test
	public void testExpression() throws LexicalException, SyntaxException {
		String input = "x + 2";
		Parser parser = makeParser(input);
		Expression e = parser.expression();  //call expression here instead of parse
		show(e);	
		assertEquals(ExpressionBinary.class, e.getClass());
		ExpressionBinary b = (ExpressionBinary)e;
		assertEquals(ExpressionIdent.class, b.leftExpression.getClass());
		ExpressionIdent left = (ExpressionIdent)b.leftExpression;
		assertEquals("x", left.name);
		assertEquals(ExpressionIntegerLiteral.class, b.rightExpression.getClass());
		ExpressionIntegerLiteral right = (ExpressionIntegerLiteral)b.rightExpression;
		assertEquals(2, right.value);
		assertEquals(OP_PLUS, b.op);
	}
	
	
	// Declarations
	@Test
	public void test01() throws LexicalException, SyntaxException {
		String input = "t{float f[];}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void test02() throws LexicalException, SyntaxException {
		String input = "t{boolean b05; int xyz;}";
		Parser parser = makeParser(input);
		Program p = parser.parse();
		assertEquals(p.progName, "t");
		assertEquals(p.block.decsOrStatements.size(), 2);
		ArrayList<ASTNode> a = (ArrayList<ASTNode>) p.block.decsOrStatements;
		assertEquals(a.get(0).getClass(), Declaration.class);
		assertEquals(((Declaration)a.get(0)).type, KW_boolean);
		assertEquals(((Declaration)a.get(0)).name, "b05");
		assertEquals(((Declaration)a.get(1)).type, KW_int);
		assertEquals(((Declaration)a.get(1)).name, "xyz");
	}
	
	@Test
	public void test03() throws LexicalException, SyntaxException {
		String input = "t{filename ;}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void test04() throws LexicalException, SyntaxException {
		String input = "t{image ig;}";
		Parser parser = makeParser(input);
		Program p = parser.parse();
		assertEquals(p.progName, "t");
		assertEquals(p.block.decsOrStatements.size(), 1);
		ArrayList<ASTNode> a = (ArrayList<ASTNode>) p.block.decsOrStatements;
		assertEquals(a.get(0).getClass(), Declaration.class);
		assertEquals(((Declaration)a.get(0)).type, KW_image);
		assertEquals(((Declaration)a.get(0)).name, "ig");
		assertEquals(((Declaration)a.get(0)).width, null);
		assertEquals(((Declaration)a.get(0)).height, null);
	}
	
	@Test
	public void test05() throws LexicalException, SyntaxException {
		String input = "t{image pic [3.5, false];}";
		Parser parser = makeParser(input);
		Program p = parser.parse();
		assertEquals(p.progName, "t");
		assertEquals(p.block.decsOrStatements.size(), 1);
		ArrayList<ASTNode> a = (ArrayList<ASTNode>) p.block.decsOrStatements;
		assertEquals(a.get(0).getClass(), Declaration.class);
		assertEquals(((Declaration)a.get(0)).type, KW_image);
		assertEquals(((Declaration)a.get(0)).name, "pic");
		assertEquals(((Declaration)a.get(0)).width.getClass(), ExpressionFloatLiteral.class);
		assertEquals(((Declaration)a.get(0)).height.getClass(), ExpressionBooleanLiteral.class);
	}
	
	@Test
	public void test06() throws LexicalException, SyntaxException {
		String input = "t{image pic [+0, -cat;}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	// Expressions in declaration
	// + primary
	// FunctionApplication & PredefinedName
	@Test
	public void test07() throws LexicalException, SyntaxException {
		String input = "image pic [cart_x (Z), polar_a[default_height,default_width]]";
		Parser parser = makeParser(input);
		Declaration d = parser.declaration();
		assertEquals(d.type, KW_image);
		assertEquals(d.name, "pic");
		Expression e1 = d.width; //FunctionApplication
		Expression e2 = d.height; //FunctionApplication
		assertEquals(e1.getClass(), ExpressionFunctionAppWithExpressionArg.class);
		assertEquals(e2.getClass(), ExpressionFunctionAppWithPixel.class);
		assertEquals(((ExpressionFunctionAppWithExpressionArg)e1).function, KW_cart_x);
		Expression e3 = ((ExpressionFunctionAppWithExpressionArg)e1).e; //should be PredefinedName Z
		assertEquals(e3.getClass(), ExpressionPredefinedName.class);
		assertEquals(((ExpressionPredefinedName)e3).name, KW_Z);
		assertEquals(((ExpressionFunctionAppWithPixel)e2).name, KW_polar_a);
		Expression e4 = ((ExpressionFunctionAppWithPixel)e2).e0; //should be PredefinedName default_height
		Expression e5 = ((ExpressionFunctionAppWithPixel)e2).e1; //should be PredefinedName default_width
		assertEquals(e4.getClass(), ExpressionPredefinedName.class);
		assertEquals(e5.getClass(), ExpressionPredefinedName.class);
		assertEquals(((ExpressionPredefinedName)e4).name, KW_default_height);
		assertEquals(((ExpressionPredefinedName)e5).name, KW_default_width);
	}
	
	// + PixelConstructor
	@Test
	public void test08() throws LexicalException, SyntaxException {
		String input = "image pic [alpha[!27,photos], red (sin[<<+1,-1,0,!3>>,width(true)])]";
		Parser parser = makeParser(input);
		Declaration d = parser.declaration();
		ExpressionUnary e0 = new ExpressionUnary(parser.scanner.new Token(OP_EXCLAMATION,17,1), parser.scanner.new Token(OP_EXCLAMATION,17,1),
				new ExpressionIntegerLiteral(parser.scanner.new Token(INTEGER_LITERAL,18,2), parser.scanner.new Token(INTEGER_LITERAL,18,2)));
		ExpressionFunctionAppWithPixel e1 = new ExpressionFunctionAppWithPixel(parser.scanner.new Token(KW_alpha,11,5),
				parser.scanner.new Token(KW_alpha,11,5), e0, 
				new ExpressionIdent(parser.scanner.new Token(IDENTIFIER,21,6), parser.scanner.new Token(IDENTIFIER,21,6)));
		ExpressionIntegerLiteral i1 = new ExpressionIntegerLiteral(parser.scanner.new Token(INTEGER_LITERAL,42,1),parser.scanner.new Token(INTEGER_LITERAL,42,1));
		ExpressionIntegerLiteral i2 = new ExpressionIntegerLiteral(parser.scanner.new Token(INTEGER_LITERAL,45,1),parser.scanner.new Token(INTEGER_LITERAL,45,1));
		ExpressionIntegerLiteral i3 = new ExpressionIntegerLiteral(parser.scanner.new Token(INTEGER_LITERAL,47,1),parser.scanner.new Token(INTEGER_LITERAL,47,1));
		ExpressionIntegerLiteral i4 = new ExpressionIntegerLiteral(parser.scanner.new Token(INTEGER_LITERAL,50,1),parser.scanner.new Token(INTEGER_LITERAL,50,1));
		ExpressionPixelConstructor e2 = new ExpressionPixelConstructor(parser.scanner.new Token(LPIXEL,39,2),
				new ExpressionUnary(parser.scanner.new Token(OP_PLUS,41,1),parser.scanner.new Token(OP_PLUS,41,1),i1),
				new ExpressionUnary(parser.scanner.new Token(OP_MINUS,44,1),parser.scanner.new Token(OP_MINUS,44,1),i2), i3,
				new ExpressionUnary(parser.scanner.new Token(OP_EXCLAMATION,49,1),parser.scanner.new Token(OP_EXCLAMATION,49,1),i4));
		ExpressionFunctionAppWithExpressionArg e3 = new ExpressionFunctionAppWithExpressionArg(parser.scanner.new Token(KW_width,54,5),
				parser.scanner.new Token(KW_width,54,5),
				new ExpressionBooleanLiteral(parser.scanner.new Token(BOOLEAN_LITERAL,60,4),parser.scanner.new Token(BOOLEAN_LITERAL,60,4)));
		ExpressionFunctionAppWithPixel e4 = new ExpressionFunctionAppWithPixel(parser.scanner.new Token(KW_sin,35,3),
				parser.scanner.new Token(KW_sin,35,3), e2, e3);
		ExpressionFunctionAppWithExpressionArg e5 = new ExpressionFunctionAppWithExpressionArg(parser.scanner.new Token(KW_red,30,3),
				parser.scanner.new Token(KW_red,30,3), e4);
		Declaration d1 = new Declaration(parser.scanner.new Token(KW_image,0,5), parser.scanner.new Token(KW_image,0,5),
				parser.scanner.new Token(IDENTIFIER,6,3), e1, e5);
		assertEquals(d, d1);
	}
	
	// + PixelExpression, PixelSelector
	@Test
	public void test09() throws LexicalException, SyntaxException {
		String input = "image pic [ids[(true),atan(<<true, 1, !+3.5, 0>>)],log(uno[true,false])]";
		Parser parser = makeParser(input);
		Declaration d = parser.declaration();
		ExpressionBooleanLiteral b1 = new ExpressionBooleanLiteral(parser.scanner.new Token(BOOLEAN_LITERAL,29,4),parser.scanner.new Token(BOOLEAN_LITERAL,29,4));
		ExpressionIntegerLiteral i1 = new ExpressionIntegerLiteral(parser.scanner.new Token(INTEGER_LITERAL,35,1),parser.scanner.new Token(INTEGER_LITERAL,35,1));
		ExpressionFloatLiteral f1 = new ExpressionFloatLiteral(parser.scanner.new Token(FLOAT_LITERAL,40,3),parser.scanner.new Token(FLOAT_LITERAL,40,3));
		ExpressionIntegerLiteral i2 = new ExpressionIntegerLiteral(parser.scanner.new Token(INTEGER_LITERAL,45,1),parser.scanner.new Token(INTEGER_LITERAL,45,1));
		ExpressionUnary u1 = new ExpressionUnary(parser.scanner.new Token(OP_PLUS,39,1),parser.scanner.new Token(OP_PLUS,39,1),f1);
		ExpressionUnary u2 = new ExpressionUnary(parser.scanner.new Token(OP_EXCLAMATION,38,1),parser.scanner.new Token(OP_EXCLAMATION,38,1),u1);
		ExpressionPixelConstructor e0 = new ExpressionPixelConstructor(parser.scanner.new Token(LPIXEL,27,2), b1, i1, u2, i2);
		ExpressionBooleanLiteral b2 = new ExpressionBooleanLiteral(parser.scanner.new Token(BOOLEAN_LITERAL,16,4),parser.scanner.new Token(BOOLEAN_LITERAL,16,4));
		PixelSelector s1 = new PixelSelector(parser.scanner.new Token(LSQUARE,14,1), b2,
				new ExpressionFunctionAppWithExpressionArg(parser.scanner.new Token(KW_atan,22,4), parser.scanner.new Token(KW_atan,22,4), e0));
		ExpressionPixel px1 = new ExpressionPixel(parser.scanner.new Token(IDENTIFIER,11,3), parser.scanner.new Token(IDENTIFIER,11,3), s1);
		ExpressionBooleanLiteral b3 = new ExpressionBooleanLiteral(parser.scanner.new Token(BOOLEAN_LITERAL,59,4),parser.scanner.new Token(BOOLEAN_LITERAL,59,4));
		ExpressionBooleanLiteral b4 = new ExpressionBooleanLiteral(parser.scanner.new Token(BOOLEAN_LITERAL,64,5),parser.scanner.new Token(BOOLEAN_LITERAL,64,5));
		PixelSelector s2 = new PixelSelector(parser.scanner.new Token(LSQUARE,58,1), b3, b4);
		ExpressionPixel px2 = new ExpressionPixel(parser.scanner.new Token(IDENTIFIER,55,3), parser.scanner.new Token(IDENTIFIER,55,3), s2);
		ExpressionFunctionAppWithExpressionArg e1 = new ExpressionFunctionAppWithExpressionArg(parser.scanner.new Token(KW_log,51,3),
				parser.scanner.new Token(KW_log,51,3), px2);
		Declaration d1 = new Declaration(parser.scanner.new Token(KW_image,0,5), parser.scanner.new Token(KW_image,0,5),
				parser.scanner.new Token(IDENTIFIER,6,3), px1, e1);
		assertEquals(d, d1);
	}
	
	@Test
	public void test10() throws LexicalException, SyntaxException {
		String input = "test {image pic [(iden [abs,true]),!+++--4.5];}"; //abs does not belongs to primary
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	// ~expressions
	@Test
	public void test11() throws LexicalException, SyntaxException {
		String input = "test {image pic [+1,-1!];}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void test12() throws LexicalException, SyntaxException {
		String input = "image pic [5**true,1025%5]";
		Parser parser = makeParser(input);
		Declaration d = parser.declaration();
		Expression e1 = d.width;
		Expression e2 = d.height;
		assertEquals(((ExpressionBinary)e1).leftExpression.getClass(), ExpressionIntegerLiteral.class);
		assertEquals(((ExpressionBinary)e1).op, OP_POWER);
		assertEquals(((ExpressionBinary)e1).rightExpression.getClass(), ExpressionBooleanLiteral.class);
		assertEquals(((ExpressionBinary)e2).leftExpression.getClass(), ExpressionIntegerLiteral.class);
		assertEquals(((ExpressionBinary)e2).op, OP_MOD);
		assertEquals(((ExpressionBinary)e2).rightExpression.getClass(), ExpressionIntegerLiteral.class);
	}
	
	@Test
	public void test13() throws LexicalException, SyntaxException {
		//String input = "test {image pic [47.5**3*0**5+15,2/3*4-200<=135+7*4];}";
		String input = "true == 1 ? 3<6 : a&b";
		Parser parser = makeParser(input);
		Expression e = parser.expression();
		ExpressionBinary eb1 = new ExpressionBinary(parser.scanner.new Token(BOOLEAN_LITERAL,0,4), 
				new ExpressionBooleanLiteral(parser.scanner.new Token(BOOLEAN_LITERAL,0,4), parser.scanner.new Token(BOOLEAN_LITERAL,0,4)), 
				parser.scanner.new Token(OP_EQ,5,2), 
				new ExpressionIntegerLiteral(parser.scanner.new Token(INTEGER_LITERAL,8,1), parser.scanner.new Token(INTEGER_LITERAL,8,1)));
		ExpressionBinary eb2 = new ExpressionBinary(parser.scanner.new Token(INTEGER_LITERAL,12,1),
				new ExpressionIntegerLiteral(parser.scanner.new Token(INTEGER_LITERAL,12,1), parser.scanner.new Token(INTEGER_LITERAL,12,1)),
				parser.scanner.new Token(OP_LT,13,1),
				new ExpressionIntegerLiteral(parser.scanner.new Token(INTEGER_LITERAL,14,1), parser.scanner.new Token(INTEGER_LITERAL,14,1)));
		ExpressionBinary eb3 = new ExpressionBinary(parser.scanner.new Token(IDENTIFIER,18,1),
				new ExpressionIdent(parser.scanner.new Token(IDENTIFIER,18,1),parser.scanner.new Token(IDENTIFIER,18,1)),
				parser.scanner.new Token(OP_AND,19,1),
				new ExpressionIdent(parser.scanner.new Token(IDENTIFIER,20,1),parser.scanner.new Token(IDENTIFIER,20,1)));
		ExpressionConditional ec = new ExpressionConditional(parser.scanner.new Token(BOOLEAN_LITERAL,0,4), eb1, eb2, eb3);
		assertEquals(e, ec);
	}
	
	@Test
	public void test14() throws LexicalException, SyntaxException {
		String input = "test {image pic [100>=10+7%3 !=true ? 0 : 1, 379**0>3*5 == true<3 & rr ? 3 | 5 : 3.5];}";
		Parser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void test15() throws LexicalException, SyntaxException {
		String input = "test {image pic [!true == 1+!27 & 3<6 | 0 ? aa : bb, +-12 | 4&5 | 3**9];}";
		Parser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void test16() throws LexicalException, SyntaxException {
		String input = "test {image pic [3**, 5>5];}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	// Statements
	// StatementInput
	@Test
	public void test17() throws LexicalException, SyntaxException {
		String input = "test {input files from @ green (files);}";
		Parser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void test18() throws LexicalException, SyntaxException {
		String input = "test {inpu files from @ true;}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	// StatementWrite
	@Test
	public void test19() throws LexicalException, SyntaxException {
		String input = "test {write apples to apps;}";
		Parser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void test20() throws LexicalException, SyntaxException {
		String input = "test {write default_height to apps;}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	// StatementAssignment
	@Test
	public void test21() throws LexicalException, SyntaxException {
		String input = "test {banana := yellow;}";
		Parser parser = makeParser(input);
		parser.parse();
	}
	
	@Test
	public void test22() throws LexicalException, SyntaxException {
		String input = "test {banana := red;}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void test23() throws LexicalException, SyntaxException {
		String input = "test {alphabet == abcde;}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	@Test
	public void test24() throws LexicalException, SyntaxException {
		String input = "ice[<<1,2,3,4>>, !cold ? true : false]";
		Parser parser = makeParser(input);
		LHS lhs = parser.lhs();
		
		assertEquals(lhs.getClass(), LHSPixel.class);
		assertEquals(((LHSPixel)lhs).pixelSelector.ex.getClass(), ExpressionPixelConstructor.class);
		assertEquals(((LHSPixel)lhs).pixelSelector.ey.getClass(), ExpressionConditional.class);
	}
	
	@Test
	public void test25() throws LexicalException, SyntaxException {
		String input = "blue (sea [0.5+true != 0 | 10%5 | nan, polar_r(0.4) ? ture : false]) := !0";
		Parser parser = makeParser(input);
		Statement s = parser.statement();
		
		assertEquals(s.getClass(), StatementAssign.class);
		assertEquals(((StatementAssign)s).lhs.getClass(), LHSSample.class);
		assertEquals(((StatementAssign)s).e.getClass(), ExpressionUnary.class);
		assertEquals(((LHSSample)((StatementAssign)s).lhs).pixelSelector.ex.getClass(), ExpressionBinary.class);
		assertEquals(((LHSSample)((StatementAssign)s).lhs).pixelSelector.ey.getClass(), ExpressionConditional.class);
	}
	
	@Test
	public void test26() throws LexicalException, SyntaxException {
		String input = "test {red ([a,b]) := true;}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	
	// StatementWhile
	@Test
	public void test27() throws LexicalException, SyntaxException {
		String input = "while (!true) {write smile to people; boolean ts;}";
		Parser parser = makeParser(input);
		Statement s = parser.statement();
		assertEquals(s.getClass(), StatementWhile.class);
		assertEquals(((StatementWhile)s).guard.getClass(), ExpressionUnary.class);
		assertEquals(((StatementWhile)s).b.decsOrStatements.get(0).getClass(), StatementWrite.class);
		assertEquals(((StatementWhile)s).b.decsOrStatements.get(1).getClass(), Declaration.class);
		assertEquals(((StatementWrite)((StatementWhile)s).b.decsOrStatements.get(0)).sourceName, "smile");
		assertEquals(((StatementWrite)((StatementWhile)s).b.decsOrStatements.get(0)).destName, "people");
		assertEquals(((Declaration)((StatementWhile)s).b.decsOrStatements.get(1)).name, "ts");
		assertEquals(((Declaration)((StatementWhile)s).b.decsOrStatements.get(1)).type, Kind.KW_boolean);
	}
	
	@Test
	public void test28() throws LexicalException, SyntaxException {
		String input = "while (height((8 !=7))) {input fruit from @ trees; while(66) {filename water; while(end) {float aa;} ;} ;}";
		Parser parser = makeParser(input);
		Statement s = parser.statement();
		
		ArrayList<ASTNode> l1 = new ArrayList<ASTNode>();
		l1.add(new Declaration(parser.scanner.new Token(Kind.KW_float, 90, 5), parser.scanner.new Token(Kind.KW_float, 90, 5), 
				parser.scanner.new Token(Kind.IDENTIFIER, 96, 2), null, null));
		Block b1 = new Block(parser.scanner.new Token(Kind.LBRACE, 89, 1), l1);
		StatementWhile sw1 = new StatementWhile(parser.scanner.new Token(Kind.KW_while, 78, 5), 
				new ExpressionIdent(parser.scanner.new Token(Kind.IDENTIFIER, 84, 3), parser.scanner.new Token(Kind.IDENTIFIER, 84, 3)), b1);
		
		ArrayList<ASTNode> l2 = new ArrayList<ASTNode>();
		l2.add(new Declaration(parser.scanner.new Token(Kind.KW_filename, 62, 8), parser.scanner.new Token(Kind.KW_filename, 62, 8), 
				parser.scanner.new Token(Kind.IDENTIFIER, 71, 5), null, null));
		l2.add(sw1);
		Block b2 = new Block(parser.scanner.new Token(Kind.LBRACE, 61, 1), l2);
		StatementWhile sw2 = new StatementWhile(parser.scanner.new Token(Kind.KW_while, 51, 5), 
				new ExpressionIntegerLiteral(parser.scanner.new Token(Kind.INTEGER_LITERAL, 57, 2), parser.scanner.new Token(Kind.INTEGER_LITERAL, 57, 2)), b2);
		
		StatementInput si = new StatementInput(parser.scanner.new Token(Kind.KW_input, 25, 5), parser.scanner.new Token(Kind.IDENTIFIER, 31, 5), 
				new ExpressionIdent(parser.scanner.new Token(Kind.IDENTIFIER, 44, 5), parser.scanner.new Token(Kind.IDENTIFIER, 44, 5)));
		ArrayList<ASTNode> l3 = new ArrayList<ASTNode>();
		l3.add(si);
		l3.add(sw2);
		Block b3 = new Block(parser.scanner.new Token(Kind.LBRACE, 24, 1), l3);
		
		ExpressionFunctionAppWithExpressionArg fa = new ExpressionFunctionAppWithExpressionArg(parser.scanner.new Token(Kind.KW_height, 7, 6), 
				parser.scanner.new Token(Kind.KW_height, 7, 6), new ExpressionBinary(parser.scanner.new Token(Kind.INTEGER_LITERAL, 15, 1), 
						new ExpressionIntegerLiteral(parser.scanner.new Token(Kind.INTEGER_LITERAL, 15, 1), parser.scanner.new Token(Kind.INTEGER_LITERAL, 15, 1)), 
						parser.scanner.new Token(Kind.OP_NEQ, 17, 2), 
						new ExpressionIntegerLiteral(parser.scanner.new Token(Kind.INTEGER_LITERAL, 19, 1), parser.scanner.new Token(Kind.INTEGER_LITERAL, 19, 1))));
		
		StatementWhile sw3 = new StatementWhile(parser.scanner.new Token(Kind.KW_while, 0, 5), fa, b3);
		
		assertEquals(sw3, s);
	}
	
	// StatementIf
	@Test
	public void test29() throws LexicalException, SyntaxException {
		String input = "if (left | right ? 1 : 0) { if(!black) {abc := def ;};}";
		Parser parser = makeParser(input);
		Statement s = parser.statement();
		assertEquals(s.getClass(), StatementIf.class);
		assertEquals(((StatementIf)s).guard.getClass(), ExpressionConditional.class);
		assertEquals(((ExpressionConditional)((StatementIf)s).guard).guard.getClass(), ExpressionBinary.class);
		assertEquals(((ExpressionConditional)((StatementIf)s).guard).trueExpression.getClass(), ExpressionIntegerLiteral.class);
		assertEquals(((StatementIf)s).b.decsOrStatements.get(0).getClass(), StatementIf.class);
		assertEquals(((StatementIf)((StatementIf)s).b.decsOrStatements.get(0)).guard.getClass(), ExpressionUnary.class);
		assertEquals(((StatementIf)((StatementIf)s).b.decsOrStatements.get(0)).b.decsOrStatements.get(0).getClass(), StatementAssign.class);
	}
	
	@Test
	public void test30() throws LexicalException, SyntaxException {
		String input = "if (cart_y(-1)) {green(wood[x,y]) := great;}";
		Parser parser = makeParser(input);
		Statement s = parser.statement();
		
		PixelSelector ps = new PixelSelector(parser.scanner.new Token(Kind.LSQUARE, 27, 1), 
				new ExpressionIdent(parser.scanner.new Token(Kind.IDENTIFIER, 28, 1), parser.scanner.new Token(Kind.IDENTIFIER, 28, 1)), 
				new ExpressionIdent(parser.scanner.new Token(Kind.IDENTIFIER, 30, 1), parser.scanner.new Token(Kind.IDENTIFIER, 30, 1)));
		LHSSample lhss = new LHSSample(parser.scanner.new Token(Kind.KW_green, 17, 5), 
				parser.scanner.new Token(Kind.IDENTIFIER, 23, 4), ps, parser.scanner.new Token(Kind.KW_green, 17, 5));
		StatementAssign ss = new StatementAssign(parser.scanner.new Token(Kind.KW_green, 17, 5), lhss, 
				new ExpressionIdent(parser.scanner.new Token(Kind.IDENTIFIER, 37, 5), parser.scanner.new Token(Kind.IDENTIFIER, 37, 5)));
		ArrayList<ASTNode> l = new ArrayList<ASTNode>();
		l.add(ss);
		Block b = new Block(parser.scanner.new Token(Kind.LBRACE, 16, 1), l);
		ExpressionIntegerLiteral il = new ExpressionIntegerLiteral(parser.scanner.new Token(Kind.INTEGER_LITERAL, 12, 1), 
				parser.scanner.new Token(Kind.INTEGER_LITERAL, 12, 1));
		ExpressionUnary eu = new ExpressionUnary(parser.scanner.new Token(Kind.OP_MINUS, 11, 1), 
				parser.scanner.new Token(Kind.OP_MINUS, 11, 1), il);
		ExpressionFunctionAppWithExpressionArg fa = new ExpressionFunctionAppWithExpressionArg(parser.scanner.new Token(Kind.KW_cart_y, 4, 6), 
				parser.scanner.new Token(Kind.KW_cart_y, 4, 6), eu);
		StatementIf si = new StatementIf(parser.scanner.new Token(Kind.KW_if, 0, 2), fa, b);
		
		assertEquals(si, s);
	}
	
	// StatementShow
	@Test
	public void test31() throws LexicalException, SyntaxException {
		String input = "test {show expect ? +1 : -1 ;}";
		Parser parser = makeParser(input);
		Program p = parser.parse();
		assertEquals(p.progName, "test");
		assertEquals(p.block.decsOrStatements.size(), 1);
		assertEquals(p.block.decsOrStatements.get(0).getClass(), StatementShow.class);
		assertEquals(((StatementShow)p.block.decsOrStatements.get(0)).e.getClass(), ExpressionConditional.class);
	}
	
	// StatementSleep
	@Test
	public void test32() throws LexicalException, SyntaxException {
		String input = "test {sleep goodnight;}";
		Parser parser = makeParser(input);
		Program p = parser.parse();
		assertEquals(p.progName, "test");
		assertEquals(p.block.decsOrStatements.size(), 1);
		List<ASTNode> l = p.block.decsOrStatements;
		assertEquals(l.get(0).getClass(), StatementSleep.class);
	}

}
