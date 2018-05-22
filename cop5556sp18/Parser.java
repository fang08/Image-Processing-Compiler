package cop5556sp18;

import cop5556sp18.Scanner.Token;
import cop5556sp18.Scanner.Kind;
import static cop5556sp18.Scanner.Kind.*;
import java.util.List;
import cop5556sp18.AST.*;
import java.util.ArrayList;


public class Parser {
	
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}



	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}


	public Program parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;
	}

	/*
	 * Program ::= Identifier Block
	 */
	public Program program() throws SyntaxException {
		Token first = t;
		Token progName = t;
		match(IDENTIFIER);
		Block b = block();
		return new Program(first, progName, b);
	}
	
	/*
	 * Block ::=  { (  (Declaration | Statement) ; )* }
	 */
	
	Kind[] firstDec = {KW_int, KW_boolean, KW_image, KW_float, KW_filename};
	Kind[] firstStatement = {KW_input, KW_write, KW_while, KW_if, KW_show, KW_sleep, IDENTIFIER, KW_red, KW_blue, 
			KW_green, KW_alpha};  /* TODO  correct this */ 
	Kind[] predefinedName = {KW_Z, KW_default_width, KW_default_height};
	Kind[] functionName = {KW_sin, KW_cos, KW_atan, KW_abs, KW_log, KW_cart_x, KW_cart_y, KW_polar_a, KW_polar_r, 
			KW_int, KW_float, KW_width, KW_height, KW_red, KW_blue, KW_green, KW_alpha};


	public Block block() throws SyntaxException {
		Token first = t;
		match(LBRACE);
		ArrayList<ASTNode> decsOrStatements = new ArrayList<ASTNode>();
		while (isKind(firstDec)|isKind(firstStatement)) {
			if (isKind(firstDec)) {
				decsOrStatements.add(declaration());
			} else if (isKind(firstStatement)) {
				decsOrStatements.add(statement());
			}
			match(SEMI);
		}
		match(RBRACE);
		return new Block(first, decsOrStatements);
	}

	
	/*
	 * Declaration ::= Type IDENTIFIER | image IDENTIFIER [ Expression , Expression ]
	 */
	public Declaration declaration() throws SyntaxException {
		//TODO
		//throw new UnsupportedOperationException();
		Token first = t;
		Token type = t;
		Token name = null;
		Expression width = null;
		Expression height = null;
		if (isKind(KW_int, KW_float, KW_boolean, KW_filename)) {
			consume();
			name = t;
			match(IDENTIFIER);
		}
		else if (isKind(KW_image)) {
			consume();
			name = t;
			match(IDENTIFIER);
			if (isKind(LSQUARE)) {
				consume();
				width = expression();
				match(COMMA);
				height = expression();
				match(RSQUARE);
			}
		}
		else throw new SyntaxException(t,"Unexpected kind " + t.kind + " in Declaration.");
		return new Declaration(first, type, name, width, height);
	}
	
	
	/*
	 * Statement ::= StatementInput | StatementWrite | StatementAssignment
	 *               | StatementWhile | StatementIf | StatementShow | StatementSleep
	 */
	public Statement statement() throws SyntaxException {
		//TODO
		//throw new UnsupportedOperationException();
		Statement s = null;
		switch(t.kind) {
			case KW_input:
				s = statementInput(); break;
			case KW_write:
				s = statementWrite(); break;
			case IDENTIFIER: case KW_red: case KW_blue: case KW_green: case KW_alpha:
				s = statementAssignment(); break;
			case KW_while:
				s = statementWhile(); break;
			case KW_if:
				s = statementIf(); break;
			case KW_show:
				s = statementShow(); break;
			case KW_sleep:
				s = statementSleep(); break;
			default:
				throw new SyntaxException(t,"Unexpected kind " + t.kind + " in Statement.");
		}
		return s;
	}
	
	
	/*
	 * StatementInput ::= input IDENTIFIER from @ Expression
	 */
	public StatementInput statementInput() throws SyntaxException {
		Token first = t;
		match(KW_input);
		Token destName = t;
		match(IDENTIFIER);
		match(KW_from);
		match(OP_AT);
		Expression e = expression();
		return new StatementInput(first, destName, e);
	}
	
	
	/*
	 * StatementWrite ::= write IDENTIFIER to IDENTIFIER
	 */
	public StatementWrite statementWrite() throws SyntaxException {
		Token first = t;
		match(KW_write);
		Token sourceName = t;
		match(IDENTIFIER);
		match(KW_to);
		Token destName = t;
		match(IDENTIFIER);
		return new StatementWrite(first, sourceName, destName);
	}
	
	
	/*
	 * StatementAssignment ::=  LHS := Expression
	 */
	public StatementAssign statementAssignment() throws SyntaxException {
		Token first = t;
		LHS l = null;
		Expression e = null;
		if (isKind(IDENTIFIER, KW_red, KW_blue, KW_green, KW_alpha)) {
			l = lhs();
			match(OP_ASSIGN);
			e = expression();
		}
		else throw new SyntaxException(t,"Unexpected kind " + t.kind + " in StatementAssignment.");
		return new StatementAssign(first, l, e);
	}
	
	
	/*
	 * StatementWhile ::=  while (Expression ) Block
	 */
	public StatementWhile statementWhile() throws SyntaxException {
		Token first = t;
		match(KW_while);
		match(LPAREN);
		Expression guard = expression();
		match(RPAREN);
		Block b = block();
		return new StatementWhile(first, guard, b);
	}
	
	
	/*
	 * StatementIf ::=  if ( Expression ) Block
	 */
	public StatementIf statementIf() throws SyntaxException {
		Token first = t;
		match(KW_if);
		match(LPAREN);
		Expression guard = expression();
		match(RPAREN);
		Block b = block();
		return new StatementIf(first, guard, b);
	}
	
	
	/*
	 * StatementShow ::=  show Expression
	 */
	public StatementShow statementShow() throws SyntaxException {
		Token first = t;
		match(KW_show);
		Expression e = expression();
		return new StatementShow(first, e);
	}
	
	
	/*
	 * StatementSleep ::=  sleep Expression
	 */
	public StatementSleep statementSleep() throws SyntaxException {
		Token first = t;
		match(KW_sleep);
		Expression duration = expression();
		return new StatementSleep(first, duration);
	}
	
	
	/*
	 * LHS ::=  IDENTIFIER | IDENTIFIER PixelSelector | Color ( IDENTIFIER PixelSelector )
	 */
	public LHS lhs() throws SyntaxException {
		LHS l = null;
		Token first = t;
		if (isKind(KW_red, KW_blue, KW_green, KW_alpha)) {
			Token color = t;
			consume();
			match(LPAREN);
			Token name = t;
			match(IDENTIFIER);
			PixelSelector pixel = pixelSelector();
			match(RPAREN);
			l = new LHSSample(first, name, pixel, color);
		}
		else if (isKind(IDENTIFIER)) {
			Token name = t;
			consume();
			l = new LHSIdent(first, name);
			if (isKind(LSQUARE)) {
				PixelSelector p = pixelSelector();
				l = new LHSPixel(first, name, p);
			}
		}
		else throw new SyntaxException(t,"Unexpected kind " + t.kind + " in LHS.");
		return l;
	}
	
	
	/*
	 * Expression ::=  OrExpression  ?  Expression  :  Expression   
	 *                |   OrExpression
	 */
	public Expression expression() throws SyntaxException {
		Token first = t;
		Expression guard = orExpression();
		Expression trueE = null;
		Expression falseE = null;
		if (isKind(OP_QUESTION)) {
			consume();
			trueE = expression();
			match(OP_COLON);
			falseE = expression();
			return new ExpressionConditional(first, guard, trueE, falseE);
		}
		return guard;
	}
	
	
	/*
	 * OrExpression  ::=  AndExpression   (  |  AndExpression ) *
	 */
	public Expression orExpression() throws SyntaxException {
		Token first = t;
		Expression e0 = andExpression();
		while (isKind(OP_OR)) {
			Token op = t;
			consume();
			Expression e1 = andExpression();
			e0 = new ExpressionBinary(first, e0, op, e1);
		}
		return e0;
	}
	
	
	/*
	 * AndExpression ::=  EqExpression ( & EqExpression )*
	 */
	public Expression andExpression() throws SyntaxException {
		Token first = t;
		Expression e0 = eqExpression();
		while (isKind(OP_AND)) {
			Token op = t;
			consume();
			Expression e1 = eqExpression();
			e0 = new ExpressionBinary(first, e0, op, e1);
		}
		return e0;
	}
	
	
	/*
	 * EqExpression ::=  RelExpression  (  (== | != )  RelExpression )*
	 */
	public Expression eqExpression() throws SyntaxException {
		Token first = t;
		Expression e0 = relExpression();
		while (isKind(OP_EQ, OP_NEQ)) {
			Token op = t;
			consume();
			Expression e1 = relExpression();
			e0 = new ExpressionBinary(first, e0, op, e1);
		}
		return e0;
	}
	
	
	/*
	 * RelExpression ::= AddExpression (  (<  | > |  <=  | >= )   AddExpression)*
	 */
	public Expression relExpression() throws SyntaxException {
		Token first = t;
		Expression e0 = addExpression();
		while (isKind(OP_GE, OP_LE, OP_GT, OP_LT)) {
			Token op = t;
			consume();
			Expression e1 = addExpression();
			e0 = new ExpressionBinary(first, e0, op, e1);
		}
		return e0;
	}
	
	
	/*
	 * AddExpression ::= MultExpression   (  ( + | - ) MultExpression )*
	 */
	public Expression addExpression() throws SyntaxException {
		Token first = t;
		Expression e0 = multExpression();
		while (isKind(OP_PLUS, OP_MINUS)) {
			Token op = t;
			consume();
			Expression e1 = multExpression();
			e0 = new ExpressionBinary(first, e0, op, e1);
		}
		return e0;
	}
	
	
	/*
	 * MultExpression := PowerExpression ( ( * | /  | % ) PowerExpression )*
	 */
	public Expression multExpression() throws SyntaxException {
		Token first = t;
		Expression e0 = powerExpression();
		while (isKind(OP_TIMES, OP_DIV, OP_MOD)) {
			Token op = t;
			consume();
			Expression e1 = powerExpression();
			e0 = new ExpressionBinary(first, e0, op, e1);
		}
		return e0;
	}
	
	
	/* 
	 * PowerExpression := UnaryExpression  (** PowerExpression | ε) 
	 */
	public Expression powerExpression() throws SyntaxException {
		Token first = t;
		Expression e0 = unaryExpression();
		if (isKind(OP_POWER)) {
			Token op = t;
			consume();
			return new ExpressionBinary(first, e0, op, powerExpression());
		}
		return e0;
	}
	
	
	/*
	 * UnaryExpression ::= + UnaryExpression | - UnaryExpression | UnaryExpressionNotPlusMinus
	 */
	public Expression unaryExpression() throws SyntaxException {
		Token first = t;
		if (isKind(OP_PLUS, OP_MINUS)) {
			Token op = t;
			consume();
			return new ExpressionUnary(first, op, unaryExpression());
		}
		else
			return unaryExpressionNotPlusMinus();
	}
	
	
	/* 
	 * UnaryExpressionNotPlusMinus ::=  ! UnaryExpression  | Primary
	 */
	public Expression unaryExpressionNotPlusMinus() throws SyntaxException {
		Token first = t;
		if (isKind(OP_EXCLAMATION)) {
			Token op = t;
			consume();
			return new ExpressionUnary(first, op, unaryExpression());
		}
		else
			return primary();
	}
	
	
	/*
	 * Primary ::= INTEGER_LITERAL | BOOLEAN_LITERAL | FLOAT_LITERAL | 
     *          ( Expression ) | FunctionApplication  | IDENTIFIER | PixelExpression | 
     *           PredefinedName | PixelConstructor
	 */
	public Expression primary() throws SyntaxException {
		System.out.println(t.toString());
		Token first = t;
		if (isKind(INTEGER_LITERAL)) {
			Token i = t;
			consume();
			return new ExpressionIntegerLiteral(first, i);
		}
		else if (isKind(BOOLEAN_LITERAL)) {
			Token b = t;
			consume();
			return new ExpressionBooleanLiteral(first, b);
		}
		else if (isKind(FLOAT_LITERAL)) {
			Token f = t;
			consume();
			return new ExpressionFloatLiteral(first, f);
		}
		else if (isKind(LPAREN)) {
			consume();
			Expression e = expression();
			match(RPAREN);
			return e;
		}
		else if (isKind(functionName)) {
			Expression fa = functionApp();
			return fa;
		}
		else if (isKind(IDENTIFIER)) {
			if (scanner.peek().kind == LSQUARE) {
				ExpressionPixel p = pixelExpression();
				return p;
			}
			else {
				Token id = t;
				consume();
				return new ExpressionIdent(first, id);
			}
		}
		else if (isKind(predefinedName)) {
			Token p = t;
			consume();
			return new ExpressionPredefinedName(first, p);
		}
		else if (isKind(LPIXEL)) {
			ExpressionPixelConstructor pc = pixelConstructor();
			return pc;
		}
		else throw new SyntaxException(t,"Unexpected kind " + t.kind + " in primary.");		
	}
	
	
	/*
	 * FunctionApplication ::= FunctionName ( Expression )  | FunctionName  [ Expression , Expression ]
	 */
	public Expression functionApp() throws SyntaxException {
		Token first = t;
		if (isKind(functionName)) {
			Token name = t;
			consume();
			if (isKind(LPAREN)) {
				consume();
				Expression e = expression();
				match(RPAREN);
				return new ExpressionFunctionAppWithExpressionArg(first, name, e);
			}
			else if (isKind(LSQUARE)) {
				consume();
				Expression e0 = expression();
				match(COMMA);
				Expression e1 = expression();
				match(RSQUARE);
				return new ExpressionFunctionAppWithPixel(first, name, e0, e1);
			}
			else throw new SyntaxException(t,"Incomplete FunctionApplication.");
		}
		else throw new SyntaxException(t,"Unexpected kind " + t.kind + " in FunctionApplication.");
	}
	
	
	/* 
	 * PixelConstructor ::=  <<  Expression , Expression , Expression , Expression  >> 
	 */
	public ExpressionPixelConstructor pixelConstructor() throws SyntaxException {
		Token first = t;
		match(LPIXEL);
		Expression alpha = expression();
		match(COMMA);
		Expression red = expression();
		match(COMMA);
		Expression green = expression();
		match(COMMA);
		Expression blue = expression();
		match(RPIXEL);
		return new ExpressionPixelConstructor(first, alpha, red, green, blue);
	}
	
	
	/* 
	 * PixelExpression ::= IDENTIFIER PixelSelector 
	 */
	public ExpressionPixel pixelExpression() throws SyntaxException {
		Token first = t;
		Token name = t;
		match(IDENTIFIER);
		PixelSelector ps = pixelSelector();
		return new ExpressionPixel(first, name, ps);
	}
	
	
	/* 
	 * PixelSelector ::= [ Expression , Expression ] 
	 */
	public PixelSelector pixelSelector() throws SyntaxException {
		Token first = t;
		match(LSQUARE);
		Expression ex = expression();
		match(COMMA);
		Expression ey = expression();
		match(RSQUARE);
		return new PixelSelector(first, ex, ey);
	}


	protected boolean isKind(Kind kind) {
		return t.kind == kind;
	}

	protected boolean isKind(Kind... kinds) {
		for (Kind k : kinds) {
			if (k == t.kind)
				return true;
		}
		return false;
	}


	/**
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		Token tmp = t;
		if (isKind(kind)) {
			consume();
			return tmp;
		}
		throw new SyntaxException(t,"Expected " + kind + " but got " + t.kind + " at " + t.toString()); //TODO  give a better error message!
	}


	private Token consume() throws SyntaxException {
		Token tmp = t;
		if (isKind(EOF)) {
			throw new SyntaxException(t,"Unexpected EOF!"); //TODO  give a better error message!  
			//Note that EOF should be matched by the matchEOF method which is called only in parse().  
			//Anywhere else is an error. */
		}
		t = scanner.nextToken();
		return tmp;
	}


	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (isKind(EOF)) {
			return t;
		}
		throw new SyntaxException(t,"Expecting EOF!"); //TODO  give a better error message!
	}
	
}