package cop5556sp18;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cop5556sp18.Types.Type;
import cop5556sp18.AST.ASTNode;
import cop5556sp18.AST.ASTVisitor;
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
import cop5556sp18.Scanner.Kind;
import cop5556sp18.CodeGenUtils;


public class CodeGenerator implements ASTVisitor, Opcodes {

	/**
	 * All methods and variable static.
	 */

	static final int Z = 255;

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;

	final int defaultWidth;
	final int defaultHeight;
	
	int slotCount;
	ArrayList<Declaration> allVariable;
	
	// final boolean itf = false;
	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 * @param defaultWidth
	 *            default width of images
	 * @param defaultHeight
	 *            default height of images
	 */
	public CodeGenerator(boolean DEVEL, boolean GRADE, String sourceFileName,
			int defaultWidth, int defaultHeight) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
		this.defaultWidth = defaultWidth;
		this.defaultHeight = defaultHeight;
		
		slotCount = 0;
		allVariable = new ArrayList<Declaration>();
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		// TODO refactor and extend as necessary
		Label start = new Label();
		Label end = new Label();
		mv.visitLabel(start);
		for (ASTNode node : block.decsOrStatements) {
			if (node instanceof Declaration) {
				((Declaration) node).setStart(start);
				((Declaration) node).setEnd(end);
				allVariable.add((Declaration) node);
			}
			node.visit(this, null);
		}
		mv.visitLabel(end);
		return null;
	}

	@Override
	public Object visitBooleanLiteral(
			ExpressionBooleanLiteral expressionBooleanLiteral, Object arg)
			throws Exception {
		mv.visitLdcInsn(expressionBooleanLiteral.value);
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitDeclaration(Declaration declaration, Object arg)
			throws Exception {
		declaration.setSlot(++slotCount);
		if (declaration.decType == Types.Type.IMAGE) {
			if (declaration.width != null && declaration.height != null) {
				declaration.width.visit(this, arg);
				declaration.height.visit(this, arg);
			}
			else if (declaration.decType == Types.Type.IMAGE && declaration.width == null && declaration.height == null) {
				mv.visitLdcInsn(defaultWidth);
				mv.visitLdcInsn(defaultHeight);
			}
			mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "makeImage", RuntimeImageSupport.makeImageSig, false);
			mv.visitVarInsn(ASTORE, declaration.getSlot());
		}
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionBinary(ExpressionBinary expressionBinary,
			Object arg) throws Exception {
		switch(expressionBinary.op) {
		case OP_PLUS:
			if (expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.INTEGER) {
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(IADD);
			}
			else if (expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.FLOAT) {
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(FADD);
			}
			else if (expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.FLOAT) {
				expressionBinary.leftExpression.visit(this, arg);
				mv.visitInsn(I2F);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(FADD);
			}
			else if (expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.INTEGER) {
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(I2F);
				mv.visitInsn(FADD);
			}
			break;
		case OP_MINUS:
			if (expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.INTEGER) {
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(ISUB);
			}
			else if (expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.FLOAT) {
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(FSUB);
			}
			else if (expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.FLOAT) {
				expressionBinary.leftExpression.visit(this, arg);
				mv.visitInsn(I2F);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(FSUB);
			}
			else if (expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.INTEGER) {
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(I2F);
				mv.visitInsn(FSUB);
			}
			break;
		case OP_TIMES:
			if (expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.INTEGER) {
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(IMUL);
			}
			else if (expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.FLOAT) {
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(FMUL);
			}
			else if (expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.FLOAT) {
				expressionBinary.leftExpression.visit(this, arg);
				mv.visitInsn(I2F);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(FMUL);
			}
			else if (expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.INTEGER) {
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(I2F);
				mv.visitInsn(FMUL);
			}
			break;
		case OP_DIV:
			if (expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.INTEGER) {
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(IDIV);
			}
			else if (expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.FLOAT) {
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(FDIV);
			}
			else if (expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.FLOAT) {
				expressionBinary.leftExpression.visit(this, arg);
				mv.visitInsn(I2F);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(FDIV);
			}
			else if (expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.INTEGER) {
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(I2F);
				mv.visitInsn(FDIV);
			}
			break;
		case OP_MOD:
			if (expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.INTEGER) {
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(IREM);
			}
			break;
		case OP_POWER:
			if (expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.INTEGER) {
				expressionBinary.leftExpression.visit(this, arg);
				mv.visitInsn(I2D);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(I2D);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2I);
			}
			else if (expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.FLOAT) {
				expressionBinary.leftExpression.visit(this, arg);
				mv.visitInsn(F2D);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(F2D);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2F);
			}
			else if (expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.FLOAT) {
				expressionBinary.leftExpression.visit(this, arg);
				mv.visitInsn(I2D);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(F2D);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2F);
			}
			else if (expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.INTEGER) {
				expressionBinary.leftExpression.visit(this, arg);
				mv.visitInsn(F2D);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(I2D);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
				mv.visitInsn(D2F);
			}
			break;
		case OP_AND:
			if ((expressionBinary.leftExpression.type == Type.BOOLEAN && expressionBinary.rightExpression.type == Type.BOOLEAN)
					|| (expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.INTEGER)) {
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(IAND);
			}
			break;
		case OP_OR:
			if ((expressionBinary.leftExpression.type == Type.BOOLEAN && expressionBinary.rightExpression.type == Type.BOOLEAN)
					|| (expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.INTEGER)) {
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(IOR);
			}
			break;
		case OP_EQ:
			if ((expressionBinary.leftExpression.type == Type.BOOLEAN && expressionBinary.rightExpression.type == Type.BOOLEAN)
					|| (expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.INTEGER)) {
				Label getFalse = new Label();
				Label getTrue = new Label();
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitJumpInsn(IF_ICMPEQ, getTrue);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, getFalse);
				mv.visitLabel(getTrue);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(getFalse);
			}
			else if (expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.FLOAT) {
				Label getFalse = new Label();
				Label getTrue = new Label();
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(FCMPG); // returns 0, 1 or -1
				mv.visitJumpInsn(IFEQ, getTrue);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, getFalse);
				mv.visitLabel(getTrue);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(getFalse);
			}
			break;
		case OP_NEQ:
			if ((expressionBinary.leftExpression.type == Type.BOOLEAN && expressionBinary.rightExpression.type == Type.BOOLEAN)
					|| (expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.INTEGER)) {
				Label getFalse = new Label();
				Label getTrue = new Label();
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitJumpInsn(IF_ICMPEQ, getFalse);
				mv.visitInsn(ICONST_1);
				mv.visitJumpInsn(GOTO, getTrue);
				mv.visitLabel(getFalse);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(getTrue);
			}
			else if (expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.FLOAT) {
				Label getFalse = new Label();
				Label getTrue = new Label();
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(FCMPG); // returns 0, 1 or -1
				mv.visitJumpInsn(IFNE, getTrue);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, getFalse);
				mv.visitLabel(getTrue);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(getFalse);
			}
			break;
		case OP_GT:
			if ((expressionBinary.leftExpression.type == Type.BOOLEAN && expressionBinary.rightExpression.type == Type.BOOLEAN)
					|| (expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.INTEGER)) {
				Label getFalse = new Label();
				Label getTrue = new Label();
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitJumpInsn(IF_ICMPGT, getTrue);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, getFalse);
				mv.visitLabel(getTrue);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(getFalse);
			}
			else if (expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.FLOAT) {
				Label getFalse = new Label();
				Label getTrue = new Label();
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);				
				mv.visitInsn(FCMPG); // returns 0, 1 or -1
				mv.visitJumpInsn(IFGT, getTrue);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, getFalse);
				mv.visitLabel(getTrue);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(getFalse);
			}
			break;
		case OP_GE:
			if ((expressionBinary.leftExpression.type == Type.BOOLEAN && expressionBinary.rightExpression.type == Type.BOOLEAN)
					|| (expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.INTEGER)) {
				Label getFalse = new Label();
				Label getTrue = new Label();
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitJumpInsn(IF_ICMPGE, getTrue);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, getFalse);
				mv.visitLabel(getTrue);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(getFalse);
			}
			else if (expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.FLOAT) {
				Label getFalse = new Label();
				Label getTrue = new Label();
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(FCMPG);
				mv.visitJumpInsn(IFGE, getTrue);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, getFalse);
				mv.visitLabel(getTrue);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(getFalse);
			}
			break;
		case OP_LT:
			if ((expressionBinary.leftExpression.type == Type.BOOLEAN && expressionBinary.rightExpression.type == Type.BOOLEAN)
					|| (expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.INTEGER)) {
				Label getFalse = new Label();
				Label getTrue = new Label();
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitJumpInsn(IF_ICMPLT, getTrue);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, getFalse);
				mv.visitLabel(getTrue);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(getFalse);
			}
			else if (expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.FLOAT) {
				Label getFalse = new Label();
				Label getTrue = new Label();
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(FCMPG);
				mv.visitJumpInsn(IFLT, getTrue);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, getFalse);
				mv.visitLabel(getTrue);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(getFalse);
			}
			break;
		case OP_LE:
			if ((expressionBinary.leftExpression.type == Type.BOOLEAN && expressionBinary.rightExpression.type == Type.BOOLEAN)
					|| (expressionBinary.leftExpression.type == Type.INTEGER && expressionBinary.rightExpression.type == Type.INTEGER)) {
				Label getFalse = new Label();
				Label getTrue = new Label();
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitJumpInsn(IF_ICMPLE, getTrue);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, getFalse);
				mv.visitLabel(getTrue);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(getFalse);
			}
			else if (expressionBinary.leftExpression.type == Type.FLOAT && expressionBinary.rightExpression.type == Type.FLOAT) {
				Label getFalse = new Label();
				Label getTrue = new Label();
				expressionBinary.leftExpression.visit(this, arg);
				expressionBinary.rightExpression.visit(this, arg);
				mv.visitInsn(FCMPG);
				mv.visitJumpInsn(IFLE, getTrue);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, getFalse);
				mv.visitLabel(getTrue);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(getFalse);
			}
		default:
			break;
		}
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionConditional(
			ExpressionConditional expressionConditional, Object arg)
			throws Exception {
		Label afterTrue = new Label();
		Label afterFalse = new Label();
		expressionConditional.guard.visit(this, arg);
		mv.visitJumpInsn(IFEQ, afterTrue);
		expressionConditional.trueExpression.visit(this, arg);
		mv.visitJumpInsn(GOTO, afterFalse);
		mv.visitLabel(afterTrue);
		expressionConditional.falseExpression.visit(this, arg);
		mv.visitLabel(afterFalse);
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionFloatLiteral(
			ExpressionFloatLiteral expressionFloatLiteral, Object arg)
			throws Exception {
		mv.visitLdcInsn(expressionFloatLiteral.value);
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionFunctionAppWithExpressionArg(
			ExpressionFunctionAppWithExpressionArg expressionFunctionAppWithExpressionArg,
			Object arg) throws Exception {
		expressionFunctionAppWithExpressionArg.e.visit(this, arg);
		switch (expressionFunctionAppWithExpressionArg.e.type) {
		case INTEGER:
			switch (expressionFunctionAppWithExpressionArg.function) {
			case KW_abs:
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(I)I", false);
				break;
			case KW_red:
				mv.visitMethodInsn(INVOKESTATIC, RuntimePixelOps.className, "getRed", "(I)I", false);
				break;
			case KW_green:
				mv.visitMethodInsn(INVOKESTATIC, RuntimePixelOps.className, "getGreen", "(I)I", false);
				break;
			case KW_blue:
				mv.visitMethodInsn(INVOKESTATIC, RuntimePixelOps.className, "getBlue", "(I)I", false);
				break;
			case KW_alpha:
				mv.visitMethodInsn(INVOKESTATIC, RuntimePixelOps.className, "getAlpha", "(I)I", false);
				break;
			case KW_float:
				mv.visitInsn(I2F);
				break;
			case KW_int:
				break;
			default:
				break;
			}
			break;
		case FLOAT:
			switch (expressionFunctionAppWithExpressionArg.function) {
			case KW_abs:
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(F)F", false);
				break;
			case KW_sin:
				mv.visitInsn(F2D);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "sin", "(D)D", false);
				mv.visitInsn(D2F);
				break;
			case KW_cos:
				mv.visitInsn(F2D);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "cos", "(D)D", false);
				mv.visitInsn(D2F);
				break;
			case KW_atan:
				mv.visitInsn(F2D);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "atan", "(D)D", false);
				mv.visitInsn(D2F);
				break;
			case KW_log:
				mv.visitInsn(F2D);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "log", "(D)D", false);
				mv.visitInsn(D2F);
				break;
			case KW_int:
				mv.visitInsn(F2I);
				break;
			case KW_float:
				break;
			default:
				break;
			}
			break;
		case IMAGE:
			switch (expressionFunctionAppWithExpressionArg.function) {
			case KW_width:
				mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "getWidth", RuntimeImageSupport.getWidthSig, false);
				break;
			case KW_height:
				mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "getHeight", RuntimeImageSupport.getHeightSig, false);
				break;
			default:
				break;
			}
		default:
			break;
		}
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionFunctionAppWithPixel(
			ExpressionFunctionAppWithPixel expressionFunctionAppWithPixel,
			Object arg) throws Exception {
		if (expressionFunctionAppWithPixel.name == Kind.KW_cart_x || expressionFunctionAppWithPixel.name == Kind.KW_cart_y) {
			expressionFunctionAppWithPixel.e0.visit(this, arg);
			mv.visitInsn(F2D);
			expressionFunctionAppWithPixel.e1.visit(this, arg);
			mv.visitInsn(F2D);
			if (expressionFunctionAppWithPixel.name == Kind.KW_cart_x)
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "cos", "(D)D", false);
			else
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "sin", "(D)D", false);
			mv.visitInsn(DMUL);
			mv.visitInsn(D2I);
		}
		else if (expressionFunctionAppWithPixel.name == Kind.KW_polar_a || expressionFunctionAppWithPixel.name == Kind.KW_polar_r) {
			if (expressionFunctionAppWithPixel.name == Kind.KW_polar_a) {
				expressionFunctionAppWithPixel.e1.visit(this, arg);
				mv.visitInsn(I2D);
				expressionFunctionAppWithPixel.e0.visit(this, arg);
				mv.visitInsn(I2D);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "atan2", "(DD)D", false);
			}
			else {
				expressionFunctionAppWithPixel.e0.visit(this, arg);
				mv.visitInsn(I2D);
				expressionFunctionAppWithPixel.e1.visit(this, arg);
				mv.visitInsn(I2D);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "hypot", "(DD)D", false);
			}
			
			mv.visitInsn(D2F);
		}
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionIdent(ExpressionIdent expressionIdent,
			Object arg) throws Exception {
		switch (expressionIdent.type) {
		case INTEGER: case BOOLEAN:
			mv.visitVarInsn(ILOAD, expressionIdent.dec.getSlot());
			break;
		case FLOAT:
			mv.visitVarInsn(FLOAD, expressionIdent.dec.getSlot());
			break;
		case FILE: case IMAGE:
			mv.visitVarInsn(ALOAD, expressionIdent.dec.getSlot());
		default:
			break;
		}
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionIntegerLiteral(
			ExpressionIntegerLiteral expressionIntegerLiteral, Object arg)
			throws Exception {
		// This one is all done!
		mv.visitLdcInsn(expressionIntegerLiteral.value);
		return null;
	}

	@Override
	public Object visitExpressionPixel(ExpressionPixel expressionPixel,
			Object arg) throws Exception {
		mv.visitVarInsn(ALOAD, expressionPixel.dec.getSlot());  //load BufferedImage image
		expressionPixel.pixelSelector.visit(this, arg);  //load x and y
		mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "getPixel", RuntimeImageSupport.getPixelSig, false);
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionPixelConstructor(
			ExpressionPixelConstructor expressionPixelConstructor, Object arg)
			throws Exception {
		expressionPixelConstructor.alpha.visit(this, arg);
		expressionPixelConstructor.red.visit(this, arg);
		expressionPixelConstructor.green.visit(this, arg);
		expressionPixelConstructor.blue.visit(this, arg);
		mv.visitMethodInsn(INVOKESTATIC, RuntimePixelOps.className, "makePixel", RuntimePixelOps.makePixelSig, false);
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionPredefinedName(
			ExpressionPredefinedName expressionPredefinedName, Object arg)
			throws Exception {
		switch(expressionPredefinedName.name) {
		case KW_Z:
			mv.visitLdcInsn(Z);
			break;
		case KW_default_width:
			mv.visitLdcInsn(defaultWidth);
			break;
		case KW_default_height:
			mv.visitLdcInsn(defaultHeight);
		default:
			break;
		}		
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionUnary(ExpressionUnary expressionUnary,
			Object arg) throws Exception {
		expressionUnary.expression.visit(this, arg);
		switch(expressionUnary.op) {
		case OP_PLUS:
			break;
		case OP_MINUS:
			if (expressionUnary.expression.type == Type.INTEGER)
				mv.visitInsn(INEG);
			else if (expressionUnary.expression.type == Type.FLOAT)
				mv.visitInsn(FNEG);
			break;
		case OP_EXCLAMATION:
			if (expressionUnary.expression.type == Type.INTEGER) {
				mv.visitInsn(ICONST_M1);  // load -1 in jvm is 32 ones (111...111)
				mv.visitInsn(IXOR);
			}
			else if (expressionUnary.expression.type == Type.BOOLEAN) {
				Label before = new Label();
				Label after = new Label();				
				mv.visitJumpInsn(IFEQ, before);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, after);
				mv.visitLabel(before);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(after);
			}				
		default:
			break;
		}
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitLHSIdent(LHSIdent lhsIdent, Object arg)
			throws Exception {
		switch (lhsIdent.type) {
		case INTEGER: case BOOLEAN:
			mv.visitVarInsn(ISTORE, lhsIdent.dec.getSlot());
			break;
		case FILE:
			mv.visitVarInsn(ASTORE, lhsIdent.dec.getSlot());
			break;
		case FLOAT:
			mv.visitVarInsn(FSTORE, lhsIdent.dec.getSlot());
			break;
		case IMAGE:
			mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "deepCopy", RuntimeImageSupport.deepCopySig, false);
			mv.visitVarInsn(ASTORE, lhsIdent.dec.getSlot());
		default:
			break;
		}
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitLHSPixel(LHSPixel lhsPixel, Object arg)
			throws Exception {
		//already load integer with expression during statementAssignment
		mv.visitVarInsn(ALOAD, lhsPixel.dec.getSlot());   //load BufferedImage image
		lhsPixel.pixelSelector.visit(this, arg);  //load x and y
		mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "setPixel", RuntimeImageSupport.setPixelSig, false);
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitLHSSample(LHSSample lhsSample, Object arg)
			throws Exception {
		//already load integer with expression during statementAssignment
		mv.visitVarInsn(ALOAD, lhsSample.dec.getSlot());   //load BufferedImage image
		lhsSample.pixelSelector.visit(this, arg);  //load x and y
		switch(lhsSample.color) { //load color
		case KW_red:
			mv.visitLdcInsn(RuntimePixelOps.RED);
			break;
		case KW_green:
			mv.visitLdcInsn(RuntimePixelOps.GREEN);
			break;
		case KW_blue:
			mv.visitLdcInsn(RuntimePixelOps.BLUE);
			break;
		case KW_alpha:
			mv.visitLdcInsn(RuntimePixelOps.ALPHA);
			break;
		default:
			break;
		}
		mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "updatePixelColor", RuntimeImageSupport.updatePixelColorSig, false);
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitPixelSelector(PixelSelector pixelSelector, Object arg)
			throws Exception {
		pixelSelector.ex.visit(this, arg);
		pixelSelector.ey.visit(this, arg);
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		// TODO refactor and extend as necessary
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		// cw = new ClassWriter(0); //If the call to mv.visitMaxs(1, 1) crashes,
		// it is
		// sometime helpful to
		// temporarily run it without COMPUTE_FRAMES. You probably
		// won't get a completely correct classfile, but
		// you will be able to see the code that was
		// generated.
		className = program.progName;
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null,
				"java/lang/Object", null);
		cw.visitSource(sourceFileName, null);

		// create main method
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main",
				"([Ljava/lang/String;)V", null, null);
		// initialize
		mv.visitCode();

		// add label before first instruction
		Label mainStart = new Label();
		mv.visitLabel(mainStart);

		CodeGenUtils.genLog(DEVEL, mv, "entering main");

		program.block.visit(this, arg);

		// generates code to add string to log
		CodeGenUtils.genLog(DEVEL, mv, "leaving main");

		// adds the required (by the JVM) return statement to main
		mv.visitInsn(RETURN);

		// adds label at end of code
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart,
				mainEnd, 0);
		for (Declaration d: allVariable)
			mv.visitLocalVariable(d.name, d.decType.jvmType, null, d.getStart(), d.getEnd(), d.getSlot());
		// Because we use ClassWriter.COMPUTE_FRAMES as a parameter in the
		// constructor,
		// asm will calculate this itself and the parameters are ignored.
		// If you have trouble with failures in this routine, it may be useful
		// to temporarily change the parameter in the ClassWriter constructor
		// from COMPUTE_FRAMES to 0.
		// The generated classfile will not be correct, but you will at least be
		// able to see what is in it.
		mv.visitMaxs(0, 0);

		// terminate construction of main method
		mv.visitEnd();

		// terminate class construction
		cw.visitEnd();

		// generate classfile as byte array and return
		return cw.toByteArray();
	}

	@Override
	public Object visitStatementAssign(StatementAssign statementAssign,
			Object arg) throws Exception {
		statementAssign.e.visit(this, arg);
		statementAssign.lhs.visit(this, arg);
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatementIf(StatementIf statementIf, Object arg)
			throws Exception {
		Label afterBlock = new Label();
		statementIf.guard.visit(this, arg);
		mv.visitJumpInsn(IFEQ, afterBlock);
		statementIf.b.visit(this, arg);
		mv.visitLabel(afterBlock);
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatementInput(StatementInput statementInput, Object arg)
			throws Exception {
		mv.visitVarInsn(ALOAD, 0); // load arg string array
		statementInput.e.visit(this, arg);
		mv.visitInsn(AALOAD);  // load the string in array, index is given by e
		switch(statementInput.dec.decType) {
		case INTEGER:
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
			mv.visitVarInsn(ISTORE, statementInput.dec.getSlot());
			break;
		case BOOLEAN:
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
			mv.visitVarInsn(ISTORE, statementInput.dec.getSlot());
			break;
		case FLOAT:
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "parseFloat", "(Ljava/lang/String;)F", false);
			mv.visitVarInsn(FSTORE, statementInput.dec.getSlot());
			break;
		case FILE:
			mv.visitVarInsn(ASTORE, statementInput.dec.getSlot());
			break;
		case IMAGE:
			if (statementInput.dec.width != null) {
				mv.visitTypeInsn(NEW, "java/lang/Integer"); //load Integer X
				mv.visitInsn(DUP);
				statementInput.dec.width.visit(this, arg);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Integer", "<init>", "(I)V", false);
				mv.visitTypeInsn(NEW, "java/lang/Integer"); //load Integer Y
				mv.visitInsn(DUP);
				statementInput.dec.height.visit(this, arg);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Integer", "<init>", "(I)V", false);
			}
			else {
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
			}
				mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "readImage", RuntimeImageSupport.readImageSig, false);
				mv.visitVarInsn(ASTORE, statementInput.dec.getSlot());
		default:
			break;
		}
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatementShow(StatementShow statementShow, Object arg)
			throws Exception {
		/**
		 * TODO refactor and complete implementation.
		 * 
		 * For integers, booleans, and floats, generate code to print to
		 * console. For images, generate code to display in a frame.
		 * 
		 * In all cases, invoke CodeGenUtils.genLogTOS(GRADE, mv, type); before
		 * consuming top of stack.
		 */
		statementShow.e.visit(this, arg);
		Type type = statementShow.e.getType();
		switch (type) {
			case INTEGER : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",	"Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
			}
				break;
			case BOOLEAN : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",	"Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Z)V", false);
				// TODO implement functionality
				//throw new UnsupportedOperationException();
			}
				break; //commented out because currently unreachable. You will need it.
			case FLOAT : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",	"Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(F)V", false);
				// TODO implement functionality
				//throw new UnsupportedOperationException();
			}
				break; //commented out because currently unreachable. You will need it.
//			case FILE : {
//				CodeGenUtils.genLogTOS(GRADE, mv, type);
//				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",	"Ljava/io/PrintStream;");
//				mv.visitInsn(Opcodes.SWAP);
//				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object)V", false);
//				// TODO implement functionality
//				//throw new UnsupportedOperationException();
//			}
//				break; //commented out because currently unreachable. You will need it.
			case IMAGE : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "makeFrame", RuntimeImageSupport.makeFrameSig, false);
				mv.visitInsn(POP);
				// TODO implement functionality
				//throw new UnsupportedOperationException();
			}
		default:
			break;
		}
		return null;
	}

	@Override
	public Object visitStatementSleep(StatementSleep statementSleep, Object arg)
			throws Exception {
		statementSleep.duration.visit(this, arg);
		mv.visitInsn(I2L);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);		
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatementWhile(StatementWhile statementWhile, Object arg)
			throws Exception {
		Label guard = new Label();
		Label afterBlock = new Label();
		mv.visitLabel(guard);
		statementWhile.guard.visit(this, arg);
		mv.visitJumpInsn(IFEQ, afterBlock);
		statementWhile.b.visit(this, arg);
		mv.visitJumpInsn(GOTO, guard);
		mv.visitLabel(afterBlock);
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatementWrite(StatementWrite statementWrite, Object arg)
			throws Exception {
		mv.visitVarInsn(ALOAD, statementWrite.sourceDec.getSlot());
		mv.visitVarInsn(ALOAD, statementWrite.destDec.getSlot());
		mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "write", RuntimeImageSupport.writeSig, false);
		return null;
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
	}

}
