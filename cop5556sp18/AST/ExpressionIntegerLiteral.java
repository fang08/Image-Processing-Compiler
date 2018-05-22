package cop5556sp18.AST;

import cop5556sp18.Scanner.Token;

public class ExpressionIntegerLiteral extends Expression {
	
	public final int value;

	public ExpressionIntegerLiteral(Token firstToken, Token intLiteral) {
		super(firstToken);
		this.value = intLiteral.intVal();
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitExpressionIntegerLiteral(this,arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + value;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof ExpressionIntegerLiteral))
			return false;
		ExpressionIntegerLiteral other = (ExpressionIntegerLiteral) obj;
		if (value != other.value)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExpressionIntegerLiteral [value=" + value + "]";
	}
	
}
