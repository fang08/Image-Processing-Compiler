package cop5556sp18.AST;

import cop5556sp18.Scanner.Kind;
import cop5556sp18.Scanner.Token;

public class ExpressionUnary extends Expression {

	public final Kind op;
	public final Expression expression;

	public ExpressionUnary(Token firstToken, Token op, Expression expression) {
		super(firstToken);
		this.op = op.kind;
		this.expression = expression;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitExpressionUnary(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + ((op == null) ? 0 : op.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExpressionUnary other = (ExpressionUnary) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		if (op != other.op)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExpressionUnary [op=");
		builder.append(op);
		builder.append(", expression=");
		builder.append(expression);
		builder.append("]");
		return builder.toString();
	}

}
