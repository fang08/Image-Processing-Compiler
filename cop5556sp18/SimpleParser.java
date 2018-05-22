package cop5556sp18;

import cop5556sp18.Scanner.Token;
import cop5556sp18.Scanner.Kind;
import static cop5556sp18.Scanner.Kind.*;


public class SimpleParser {
	
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

	SimpleParser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}


	public void parse() throws SyntaxException {
		program();
		matchEOF();
	}

	/*
	 * Program ::= Identifier Block
	 */
	public void program() throws SyntaxException {
		match(IDENTIFIER);
		block();
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


	public void block() throws SyntaxException {
		match(LBRACE);
		while (isKind(firstDec)|isKind(firstStatement)) {
	     if (isKind(firstDec)) {
			declaration();
		} else if (isKind(firstStatement)) {
			statement();
		}
			match(SEMI);
		}
		match(RBRACE);
	}

	
	/*
	 * Declaration ::= Type IDENTIFIER | image IDENTIFIER [ Expression , Expression ]
	 */
	public void declaration() throws SyntaxException {
		//TODO
		//throw new UnsupportedOperationException();
		if (isKind(KW_int, KW_float, KW_boolean, KW_filename)) {
			consume();
			match(IDENTIFIER);
		}
		else if (isKind(KW_image)) {
			consume();
			match(IDENTIFIER);
			if (isKind(LSQUARE)) {
				consume();
				expression();
				match(COMMA);
				expression();
				match(RSQUARE);
			}
		}
		else throw new SyntaxException(t,"Unexpected kind " + t.kind + " in Declaration.");
	}
	
	
	/*
	 * Statement ::= StatementInput | StatementWrite | StatementAssignment
	 *               | StatementWhile | StatementIf | StatementShow | StatementSleep
	 */
	public void statement() throws SyntaxException {
		//TODO
		//throw new UnsupportedOperationException();
		switch(t.kind) {
			case KW_input:
				statementInput(); break;
			case KW_write:
				statementWrite(); break;
			case IDENTIFIER: case KW_red: case KW_blue: case KW_green: case KW_alpha:
				statementAssignment(); break;
			case KW_while:
				statementWhile(); break;
			case KW_if:
				statementIf(); break;
			case KW_show:
				statementShow(); break;
			case KW_sleep:
				statementSleep(); break;
			default:
				throw new SyntaxException(t,"Unexpected kind " + t.kind + " in Statement.");
		}
	}
	
	
	/*
	 * StatementInput ::= input IDENTIFIER from @ Expression
	 */
	public void statementInput() throws SyntaxException {
		match(KW_input);
		match(IDENTIFIER);
		match(KW_from);
		match(OP_AT);
		expression();
	}
	
	
	/*
	 * StatementWrite ::= write IDENTIFIER to IDENTIFIER
	 */
	public void statementWrite() throws SyntaxException {
		match(KW_write);
		match(IDENTIFIER);
		match(KW_to);
		match(IDENTIFIER);
	}
	
	
	/*
	 * StatementAssignment ::=  LHS := Expression
	 */
	public void statementAssignment() throws SyntaxException {
		if (isKind(IDENTIFIER, KW_red, KW_blue, KW_green, KW_alpha)) {
			lhs();
			match(OP_ASSIGN);
			expression();
		}
		else throw new SyntaxException(t,"Unexpected kind " + t.kind + " in StatementAssignment.");
	}
	
	
	/*
	 * StatementWhile ::=  while (Expression ) Block
	 */
	public void statementWhile() throws SyntaxException {
		match(KW_while);
		match(LPAREN);
		expression();
		match(RPAREN);
		block();
	}
	
	
	/*
	 * StatementIf ::=  if ( Expression ) Block
	 */
	public void statementIf() throws SyntaxException {
		match(KW_if);
		match(LPAREN);
		expression();
		match(RPAREN);
		block();
	}
	
	
	/*
	 * StatementShow ::=  show Expression
	 */
	public void statementShow() throws SyntaxException {
		match(KW_show);
		expression();
	}
	
	
	/*
	 * StatementSleep ::=  sleep Expression
	 */
	public void statementSleep() throws SyntaxException {
		match(KW_sleep);
		expression();
	}
	
	
	/*
	 * LHS ::=  IDENTIFIER | IDENTIFIER PixelSelector | Color ( IDENTIFIER PixelSelector )
	 */
	public void lhs() throws SyntaxException {
		if (isKind(KW_red, KW_blue, KW_green, KW_alpha)) {
			consume();
			match(LPAREN);
			match(IDENTIFIER);
			pixelSelector();
			match(RPAREN);
		}
		else if (isKind(IDENTIFIER)) {
			consume();
			if (isKind(LSQUARE)) {
				pixelSelector();
			}
		}
		else throw new SyntaxException(t,"Unexpected kind " + t.kind + " in LHS.");
	}
	
	
	/*
	 * Expression ::=  OrExpression  ?  Expression  :  Expression   
	 *                |   OrExpression
	 */
	public void expression() throws SyntaxException {
		orExpression();
		if (isKind(OP_QUESTION)) {
			consume();
			expression();
			match(OP_COLON);
			expression();
		}
	}
	
	
	/*
	 * OrExpression  ::=  AndExpression   (  |  AndExpression ) *
	 */
	public void orExpression() throws SyntaxException {
		andExpression();
		while (isKind(OP_OR)) {
			consume();
			andExpression();
		}
	}
	
	
	/*
	 * AndExpression ::=  EqExpression ( & EqExpression )*
	 */
	public void andExpression() throws SyntaxException {
		eqExpression();
		while (isKind(OP_AND)) {
			consume();
			eqExpression();
		}
	}
	
	
	/*
	 * EqExpression ::=  RelExpression  (  (== | != )  RelExpression )*
	 */
	public void eqExpression() throws SyntaxException {
		relExpression();
		while (isKind(OP_EQ, OP_NEQ)) {
			consume();
			relExpression();
		}
	}
	
	
	/*
	 * RelExpression ::= AddExpression (  (<  | > |  <=  | >= )   AddExpression)*
	 */
	public void relExpression() throws SyntaxException {
		addExpression();
		while (isKind(OP_GE, OP_LE, OP_GT, OP_LT)) {
			consume();
			addExpression();
		}
	}
	
	
	/*
	 * AddExpression ::= MultExpression   (  ( + | - ) MultExpression )*
	 */
	public void addExpression() throws SyntaxException {
		multExpression();
		while (isKind(OP_PLUS, OP_MINUS)) {
			consume();
			multExpression();
		}
	}
	
	
	/*
	 * MultExpression := PowerExpression ( ( * | /  | % ) PowerExpression )*
	 */
	public void multExpression() throws SyntaxException {
		powerExpression();
		while (isKind(OP_TIMES, OP_DIV, OP_MOD)) {
			consume();
			powerExpression();
		}
	}
	
	
	/* 
	 * PowerExpression := UnaryExpression  (** PowerExpression | ε) 
	 */
	public void powerExpression() throws SyntaxException {
		unaryExpression();
		if (isKind(OP_POWER)) {
			consume();
			powerExpression();
		}
	}
	
	
	/*
	 * UnaryExpression ::= + UnaryExpression | - UnaryExpression | UnaryExpressionNotPlusMinus
	 */
	public void unaryExpression() throws SyntaxException {
		if (isKind(OP_PLUS, OP_MINUS)) {
			consume();
			unaryExpression();
		}
		else
			unaryExpressionNotPlusMinus();
	}
	
	
	/* 
	 * UnaryExpressionNotPlusMinus ::=  ! UnaryExpression  | Primary 
	 */
	public void unaryExpressionNotPlusMinus() throws SyntaxException {
		if (isKind(OP_EXCLAMATION)) {
			consume();
			unaryExpression();
		}
		else
			primary();
	}
	
	
	/*
	 * Primary ::= INTEGER_LITERAL | BOOLEAN_LITERAL | FLOAT_LITERAL | 
     *          ( Expression ) | FunctionApplication  | IDENTIFIER | PixelExpression | 
     *           PredefinedName | PixelConstructor
	 */
	public void primary() throws SyntaxException {
		if (isKind(INTEGER_LITERAL, BOOLEAN_LITERAL, FLOAT_LITERAL))
			consume();
		else if (isKind(LPAREN)) {
			consume();
			expression();
			match(RPAREN);
		}
		else if (isKind(functionName))
			functionApp();
		else if (isKind(IDENTIFIER)) {
			if (scanner.peek().kind == LSQUARE)
				pixelExpression();
			else
				consume();
		}
		else if (isKind(predefinedName))
			consume();
		else if (isKind(LPIXEL))
			pixelConstructor();
		else throw new SyntaxException(t,"Unexpected kind " + t.kind + " in primary.");		
	}
	
	
	/*
	 * FunctionApplication ::= FunctionName ( Expression )  | FunctionName  [ Expression , Expression ]
	 */
	public void functionApp() throws SyntaxException {
		if (isKind(functionName)) {
			consume();
			if (isKind(LPAREN)) {
				consume();
				expression();
				match(RPAREN);
			}
			else if (isKind(LSQUARE)) {
				consume();
				expression();
				match(COMMA);
				expression();
				match(RSQUARE);
			}
			else throw new SyntaxException(t,"Incomplete FunctionApplication.");
		}
		else throw new SyntaxException(t,"Unexpected kind " + t.kind + " in FunctionApplication.");
	}
	
	
	/* 
	 * PixelConstructor ::=  <<  Expression , Expression , Expression , Expression  >> 
	 */
	public void pixelConstructor() throws SyntaxException {
		match(LPIXEL);
		for (int i = 0; i < 3; i++) {
			expression();
			match(COMMA);
		}
		expression();
		match(RPIXEL);
	}
	
	
	/* 
	 * PixelExpression ::= IDENTIFIER PixelSelector 
	 */
	public void pixelExpression() throws SyntaxException {
		match(IDENTIFIER);
		pixelSelector();
	}
	
	
	/* 
	 * PixelSelector ::= [ Expression , Expression ] 
	 */
	public void pixelSelector() throws SyntaxException {
		match(LSQUARE);
		expression();
		match(COMMA);
		expression();
		match(RSQUARE);
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