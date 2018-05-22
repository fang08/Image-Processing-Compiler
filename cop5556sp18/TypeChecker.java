package cop5556sp18;

import cop5556sp18.Scanner.Kind;
import cop5556sp18.Scanner.Token;
import cop5556sp18.Types.Type;
import cop5556sp18.AST.ASTVisitor;

import static cop5556sp18.Scanner.Kind.KW_boolean;
import static cop5556sp18.Scanner.Kind.KW_filename;
import static cop5556sp18.Scanner.Kind.KW_float;
import static cop5556sp18.Scanner.Kind.KW_image;
import static cop5556sp18.Scanner.Kind.KW_int;

import cop5556sp18.AST.ASTNode;
import cop5556sp18.AST.Block;
import cop5556sp18.AST.Declaration;
import cop5556sp18.AST.ExpressionBinary;
import cop5556sp18.AST.ExpressionBooleanLiteral;
import cop5556sp18.AST.ExpressionConditional;
import cop5556sp18.AST.ExpressionFloatLiteral;
import cop5556sp18.AST.ExpressionFunctionAppWithExpressionArg;
import cop5556sp18.AST.ExpressionFunctionAppWithPixel;
import cop5556sp18.AST.ExpressionIdent;
import cop5556sp18.AST.ExpressionIntegerLiteral;
import cop5556sp18.AST.ExpressionPixel;
import cop5556sp18.AST.ExpressionPixelConstructor;
import cop5556sp18.AST.ExpressionPredefinedName;
import cop5556sp18.AST.ExpressionUnary;
import cop5556sp18.AST.LHSIdent;
import cop5556sp18.AST.LHSPixel;
import cop5556sp18.AST.LHSSample;
import cop5556sp18.AST.PixelSelector;
import cop5556sp18.AST.Program;
import cop5556sp18.AST.StatementAssign;
import cop5556sp18.AST.StatementIf;
import cop5556sp18.AST.StatementInput;
import cop5556sp18.AST.StatementShow;
import cop5556sp18.AST.StatementSleep;
import cop5556sp18.AST.StatementWhile;
import cop5556sp18.AST.StatementWrite;

public class TypeChecker implements ASTVisitor {


	TypeChecker() {
	}

	@SuppressWarnings("serial")
	public static class SemanticException extends Exception {
		Token t;

		public SemanticException(Token t, String message) {
			super(message);
			this.t = t;
		}
	}

	SymbolTable symTab = new SymbolTable();
	
	// Name is only used for naming the output file. 
	// Visit the child block to type check program.
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		program.block.visit(this, arg);
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		symTab.enterScope();
		for (ASTNode n : block.decsOrStatements)
			n.visit(this, arg);
		symTab.leaveScope();
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitDeclaration(Declaration declaration, Object arg) throws Exception {
		if ((declaration.width == null) != (declaration.height == null))
			throw new SemanticException(declaration.firstToken, "Expression asymmetric (should be all null or all not null). Semantic exception at " + declaration.firstToken.pos);
		
		if (declaration.width != null && declaration.height != null) {
			declaration.width.visit(this, arg);
			declaration.height.visit(this, arg);
		}
		
		if (declaration.width != null) { //then declaration.height must be not null
			if (declaration.decType != Types.Type.IMAGE || declaration.width.type != Types.Type.INTEGER 
					|| declaration.height.type != Types.Type.INTEGER )
				throw new SemanticException(declaration.firstToken, "Expression type mismatch. Semantic exception at " + declaration.firstToken.pos);
		}
		
		if (!symTab.insert(declaration.name, declaration))
			throw new SemanticException(declaration.firstToken, "Variable has been declared. Semantic exception at " + declaration.firstToken.pos);
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatementWrite(StatementWrite statementWrite, Object arg) throws Exception {
		statementWrite.sourceDec = symTab.lookup(statementWrite.sourceName);
		if (statementWrite.sourceDec == null)
			throw new SemanticException(statementWrite.firstToken, "Empty StatementWrite source declaration. Semantic exception at " + statementWrite.firstToken.pos);
		if (statementWrite.sourceDec.decType != Types.Type.IMAGE)
			throw new SemanticException(statementWrite.firstToken, "Source declaration type error. Semantic exception at " + statementWrite.firstToken.pos);
		
		statementWrite.destDec = symTab.lookup(statementWrite.destName);
		if (statementWrite.destDec == null)
			throw new SemanticException(statementWrite.firstToken, "Empty StatementWrite destination declaration. Semantic exception at " + statementWrite.firstToken.pos);
		if (statementWrite.destDec.decType != Types.Type.FILE)
			throw new SemanticException(statementWrite.firstToken, "Destination declaration type error. Semantic exception at " + statementWrite.firstToken.pos);
		
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatementInput(StatementInput statementInput, Object arg) throws Exception {
		statementInput.e.visit(this, arg);
		statementInput.dec = symTab.lookup(statementInput.destName);
		if (statementInput.dec == null)
			throw new SemanticException(statementInput.firstToken, "Empty StatementInput declaration. Semantic exception at " + statementInput.firstToken.pos);
		if (statementInput.e.type != Types.Type.INTEGER)
			throw new SemanticException(statementInput.firstToken, "StatementInput expression type error. Semantic exception at " + statementInput.firstToken.pos);
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws Exception {
		pixelSelector.ex.visit(this, arg);
		pixelSelector.ey.visit(this, arg);
		if (	pixelSelector.ex.type != pixelSelector.ey.type)
			throw new SemanticException(pixelSelector.firstToken, "Expression types in PixelSelector not equal. Semantic exception at " + pixelSelector.firstToken.pos);
		if (	pixelSelector.ex.type != Types.Type.INTEGER && pixelSelector.ex.type != Types.Type.FLOAT)
			throw new SemanticException(pixelSelector.firstToken, "Expression types error. Semantic exception at " + pixelSelector.firstToken.pos);
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionConditional(ExpressionConditional expressionConditional, Object arg) throws Exception {
		expressionConditional.guard.visit(this, arg);
		expressionConditional.trueExpression.visit(this, arg);
		expressionConditional.falseExpression.visit(this, arg);
		if (expressionConditional.guard.type != Types.Type.BOOLEAN)
			throw new SemanticException(expressionConditional.firstToken, "Conditional expression type error. Semantic exception at " + expressionConditional.firstToken.pos);
		if (expressionConditional.trueExpression.type != expressionConditional.falseExpression.type)
			throw new SemanticException(expressionConditional.firstToken, "True/False expression types mismatch. Semantic exception at " + expressionConditional.firstToken.pos);
		expressionConditional.type = expressionConditional.trueExpression.type;
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionBinary(ExpressionBinary expressionBinary, Object arg) throws Exception {
		expressionBinary.leftExpression.visit(this, arg);
		expressionBinary.rightExpression.visit(this, arg);
		
		if (expressionBinary.leftExpression.type == Types.Type.INTEGER && expressionBinary.rightExpression.type == Types.Type.INTEGER &&
				isKind(expressionBinary.op, Kind.OP_PLUS, Kind.OP_MINUS, Kind.OP_TIMES, Kind.OP_DIV, Kind.OP_MOD, Kind.OP_POWER, Kind.OP_AND, Kind.OP_OR))
			expressionBinary.type = Types.Type.INTEGER;
		else if (expressionBinary.leftExpression.type == Types.Type.FLOAT && expressionBinary.rightExpression.type == Types.Type.FLOAT &&
				isKind(expressionBinary.op, Kind.OP_PLUS, Kind.OP_MINUS, Kind.OP_TIMES, Kind.OP_DIV, Kind.OP_POWER))
			expressionBinary.type = Types.Type.FLOAT;
		else if (expressionBinary.leftExpression.type == Types.Type.FLOAT && expressionBinary.rightExpression.type == Types.Type.INTEGER &&
				isKind(expressionBinary.op, Kind.OP_PLUS, Kind.OP_MINUS, Kind.OP_TIMES, Kind.OP_DIV, Kind.OP_POWER))
			expressionBinary.type = Types.Type.FLOAT;
		else if (expressionBinary.leftExpression.type == Types.Type.INTEGER && expressionBinary.rightExpression.type == Types.Type.FLOAT &&
				isKind(expressionBinary.op, Kind.OP_PLUS, Kind.OP_MINUS, Kind.OP_TIMES, Kind.OP_DIV, Kind.OP_POWER))
			expressionBinary.type = Types.Type.FLOAT;
		else if (expressionBinary.leftExpression.type == Types.Type.BOOLEAN && expressionBinary.rightExpression.type == Types.Type.BOOLEAN &&
				isKind(expressionBinary.op, Kind.OP_AND, Kind.OP_OR))
			expressionBinary.type = Types.Type.BOOLEAN;
		else if (expressionBinary.leftExpression.type == Types.Type.INTEGER && expressionBinary.rightExpression.type == Types.Type.INTEGER &&
				isKind(expressionBinary.op, Kind.OP_EQ, Kind.OP_NEQ, Kind.OP_GE, Kind.OP_GT, Kind.OP_LE, Kind.OP_LT))
			expressionBinary.type = Types.Type.BOOLEAN;
		else if (expressionBinary.leftExpression.type == Types.Type.FLOAT && expressionBinary.rightExpression.type == Types.Type.FLOAT &&
				isKind(expressionBinary.op, Kind.OP_EQ, Kind.OP_NEQ, Kind.OP_GE, Kind.OP_GT, Kind.OP_LE, Kind.OP_LT))
			expressionBinary.type = Types.Type.BOOLEAN;
		else if (expressionBinary.leftExpression.type == Types.Type.BOOLEAN && expressionBinary.rightExpression.type == Types.Type.BOOLEAN &&
				isKind(expressionBinary.op, Kind.OP_EQ, Kind.OP_NEQ, Kind.OP_GE, Kind.OP_GT, Kind.OP_LE, Kind.OP_LT))
			expressionBinary.type = Types.Type.BOOLEAN;
		else
			throw new SemanticException(expressionBinary.firstToken, "Wrong inferred types. Semantic exception at " + expressionBinary.firstToken.pos);
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionUnary(ExpressionUnary expressionUnary, Object arg) throws Exception {
		expressionUnary.expression.visit(this, arg);
		expressionUnary.type = expressionUnary.expression.type;
		if (expressionUnary.op == Kind.OP_EXCLAMATION) {
			if (expressionUnary.expression.type != Type.INTEGER && expressionUnary.expression.type != Type.BOOLEAN)
				throw new SemanticException(expressionUnary.firstToken, "Wrong operand types. Semantic exception at " + expressionUnary.firstToken.pos);
		}
		else if (expressionUnary.op == Kind.OP_PLUS || expressionUnary.op == Kind.OP_MINUS) {
			if (expressionUnary.expression.type != Type.INTEGER && expressionUnary.expression.type != Type.FLOAT)
				throw new SemanticException(expressionUnary.firstToken, "Wrong operand types. Semantic exception at " + expressionUnary.firstToken.pos);
		}
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionIntegerLiteral(ExpressionIntegerLiteral expressionIntegerLiteral, Object arg)
			throws Exception {
		expressionIntegerLiteral.type = Types.Type.INTEGER;
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitBooleanLiteral(ExpressionBooleanLiteral expressionBooleanLiteral, Object arg) throws Exception {
		expressionBooleanLiteral.type = Types.Type.BOOLEAN;
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionPredefinedName(ExpressionPredefinedName expressionPredefinedName, Object arg)
			throws Exception {
		expressionPredefinedName.type = Types.Type.INTEGER;
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionFloatLiteral(ExpressionFloatLiteral expressionFloatLiteral, Object arg)
			throws Exception {
		expressionFloatLiteral.type = Types.Type.FLOAT;
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionFunctionAppWithExpressionArg(
			ExpressionFunctionAppWithExpressionArg expressionFunctionAppWithExpressionArg, Object arg) throws Exception {
		expressionFunctionAppWithExpressionArg.e.visit(this, arg);
		
		if (expressionFunctionAppWithExpressionArg.e.type == Types.Type.INTEGER && isKind(expressionFunctionAppWithExpressionArg.function,
				Kind.KW_abs, Kind.KW_red, Kind.KW_green, Kind.KW_blue, Kind.KW_alpha))
			expressionFunctionAppWithExpressionArg.type = Types.Type.INTEGER;
		else if (expressionFunctionAppWithExpressionArg.e.type == Types.Type.FLOAT && isKind(expressionFunctionAppWithExpressionArg.function,
				Kind.KW_abs, Kind.KW_sin, Kind.KW_cos, Kind.KW_atan, Kind.KW_log))
			expressionFunctionAppWithExpressionArg.type = Types.Type.FLOAT;
		else if (expressionFunctionAppWithExpressionArg.e.type == Types.Type.IMAGE && isKind(expressionFunctionAppWithExpressionArg.function,
				Kind.KW_width, Kind.KW_height))
			expressionFunctionAppWithExpressionArg.type = Types.Type.INTEGER;
		else if (expressionFunctionAppWithExpressionArg.e.type == Types.Type.INTEGER && isKind(expressionFunctionAppWithExpressionArg.function,
				Kind.KW_float))
			expressionFunctionAppWithExpressionArg.type = Types.Type.FLOAT;
		else if (expressionFunctionAppWithExpressionArg.e.type == Types.Type.FLOAT && isKind(expressionFunctionAppWithExpressionArg.function,
				Kind.KW_float))
			expressionFunctionAppWithExpressionArg.type = Types.Type.FLOAT;
		else if (expressionFunctionAppWithExpressionArg.e.type == Types.Type.FLOAT && isKind(expressionFunctionAppWithExpressionArg.function,
				Kind.KW_int))
			expressionFunctionAppWithExpressionArg.type = Types.Type.INTEGER;
		else if (expressionFunctionAppWithExpressionArg.e.type == Types.Type.INTEGER && isKind(expressionFunctionAppWithExpressionArg.function,
				Kind.KW_int))
			expressionFunctionAppWithExpressionArg.type = Types.Type.INTEGER;
		else
			throw new SemanticException(expressionFunctionAppWithExpressionArg.firstToken, "Wrong inferred types. Semantic exception at " + expressionFunctionAppWithExpressionArg.firstToken.pos);
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionFunctionAppWithPixel(ExpressionFunctionAppWithPixel expressionFunctionAppWithPixel,
			Object arg) throws Exception {
		expressionFunctionAppWithPixel.e0.visit(this, arg);
		expressionFunctionAppWithPixel.e1.visit(this, arg);
		if (expressionFunctionAppWithPixel.name == Kind.KW_cart_x || expressionFunctionAppWithPixel.name == Kind.KW_cart_y) {
			if (expressionFunctionAppWithPixel.e0.type != Types.Type.FLOAT || expressionFunctionAppWithPixel.e1.type != Types.Type.FLOAT) {
				throw new SemanticException(expressionFunctionAppWithPixel.firstToken, "FunctionApplication expressions type error. Semantic exception at "
						+ expressionFunctionAppWithPixel.firstToken.pos);
			}
			expressionFunctionAppWithPixel.type = Types.Type.INTEGER;
		}
		if (expressionFunctionAppWithPixel.name == Kind.KW_polar_a || expressionFunctionAppWithPixel.name == Kind.KW_polar_r) {
			if (expressionFunctionAppWithPixel.e0.type != Types.Type.INTEGER || expressionFunctionAppWithPixel.e1.type != Types.Type.INTEGER) {
				throw new SemanticException(expressionFunctionAppWithPixel.firstToken, "FunctionApplication expressions type error. Semantic exception at "
						+ expressionFunctionAppWithPixel.firstToken.pos);
			}
			expressionFunctionAppWithPixel.type = Types.Type.FLOAT;
		}
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionPixelConstructor(ExpressionPixelConstructor expressionPixelConstructor, Object arg)
			throws Exception {
		expressionPixelConstructor.alpha.visit(this, arg);
		expressionPixelConstructor.red.visit(this, arg);
		expressionPixelConstructor.green.visit(this, arg);
		expressionPixelConstructor.blue.visit(this, arg);
		if (expressionPixelConstructor.alpha.type != Types.Type.INTEGER || expressionPixelConstructor.red.type != Types.Type.INTEGER
				||expressionPixelConstructor.green.type != Types.Type.INTEGER || expressionPixelConstructor.blue.type != Types.Type.INTEGER)
			throw new SemanticException(expressionPixelConstructor.firstToken, "PixelConstructor expressions type error. Semantic exception at " + expressionPixelConstructor.firstToken.pos);
		expressionPixelConstructor.type = Types.Type.INTEGER;
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatementAssign(StatementAssign statementAssign, Object arg) throws Exception {
		statementAssign.e.visit(this, arg);
		statementAssign.lhs.visit(this, arg);
		if (statementAssign.e.type != statementAssign.lhs.type) 
			throw new SemanticException(statementAssign.firstToken, "Left-hand side and expression type mismatch. Semantic exception at " + statementAssign.firstToken.pos);
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatementShow(StatementShow statementShow, Object arg) throws Exception {
		statementShow.e.visit(this, arg);
		if (statementShow.e.type != Types.Type.INTEGER && statementShow.e.type != Types.Type.BOOLEAN 
				&& statementShow.e.type != Types.Type.FLOAT && statementShow.e.type != Types.Type.IMAGE)
			throw new SemanticException(statementShow.firstToken, "StatementShow expression type error. Semantic exception at " + statementShow.firstToken.pos);
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionPixel(ExpressionPixel expressionPixel, Object arg) throws Exception {
		expressionPixel.pixelSelector.visit(this, arg);
		expressionPixel.dec = symTab.lookup(expressionPixel.name);
		if (expressionPixel.dec == null)
			throw new SemanticException(expressionPixel.firstToken, "Empty expressionPixel declaration. Semantic exception at " + expressionPixel.firstToken.pos);
		if (expressionPixel.dec.decType != Types.Type.IMAGE)
			throw new SemanticException(expressionPixel.firstToken, "ExpressionPixel declaration type error. Semantic exception at " + expressionPixel.firstToken.pos);
		expressionPixel.type = Types.Type.INTEGER;
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionIdent(ExpressionIdent expressionIdent, Object arg) throws Exception {
		expressionIdent.dec = symTab.lookup(expressionIdent.name);
		if (expressionIdent.dec == null)
			throw new SemanticException(expressionIdent.firstToken, "Empty expressionIdent declaration. Semantic exception at " + expressionIdent.firstToken.pos);
		expressionIdent.type = expressionIdent.dec.decType;
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitLHSSample(LHSSample lhsSample, Object arg) throws Exception {
		lhsSample.pixelSelector.visit(this, arg);
		lhsSample.dec = symTab.lookup(lhsSample.name);
		if (lhsSample.dec == null)
			throw new SemanticException(lhsSample.firstToken, "Empty LHSSample declaration. Semantic exception at " + lhsSample.firstToken.pos);
		if (lhsSample.dec.decType != Types.Type.IMAGE)
			throw new SemanticException(lhsSample.firstToken, "LHSSample declaration type error. Semantic exception at " + lhsSample.firstToken.pos);
		lhsSample.type = Types.Type.INTEGER;
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitLHSPixel(LHSPixel lhsPixel, Object arg) throws Exception {
		lhsPixel.pixelSelector.visit(this, arg);
		lhsPixel.dec = symTab.lookup(lhsPixel.name);
		if (lhsPixel.dec == null)
			throw new SemanticException(lhsPixel.firstToken, "Empty LHSPixel declaration. Semantic exception at " + lhsPixel.firstToken.pos);
		if (lhsPixel.dec.decType != Types.Type.IMAGE)
			throw new SemanticException(lhsPixel.firstToken, "LHSPixel declaration type error. Semantic exception at " + lhsPixel.firstToken.pos);
		lhsPixel.type = Types.Type.INTEGER;
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitLHSIdent(LHSIdent lhsIdent, Object arg) throws Exception {
		lhsIdent.dec = symTab.lookup(lhsIdent.name);
		if (lhsIdent.dec == null)
			throw new SemanticException(lhsIdent.firstToken, "Empty LHSIdent declaration. Semantic exception at " + lhsIdent.firstToken.pos);
		lhsIdent.type = lhsIdent.dec.decType;
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatementIf(StatementIf statementIf, Object arg) throws Exception {
		statementIf.guard.visit(this, arg);
		statementIf.b.visit(this, arg);
		if (statementIf.guard.type != Types.Type.BOOLEAN)
			throw new SemanticException(statementIf.firstToken, "Statementwhile expression type error. Semantic exception at " + statementIf.firstToken.pos);
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatementWhile(StatementWhile statementWhile, Object arg) throws Exception {
		statementWhile.guard.visit(this, arg);
		statementWhile.b.visit(this, arg);
		if (statementWhile.guard.type != Types.Type.BOOLEAN)
			throw new SemanticException(statementWhile.firstToken, "StatementWhile expression type error. Semantic exception at " + statementWhile.firstToken.pos);
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatementSleep(StatementSleep statementSleep, Object arg) throws Exception {
		statementSleep.duration.visit(this, arg);
		if (statementSleep.duration.type != Types.Type.INTEGER)
			throw new SemanticException(statementSleep.firstToken, "StatementSleep expression type error. Semantic exception at " + statementSleep.firstToken.pos);
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}


	protected boolean isKind(Kind input, Kind... kinds) {
		for (Kind k : kinds) {
			if (k == input)
				return true;
		}
		return false;
	}
}
