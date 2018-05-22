package cop5556sp18.AST;

import cop5556sp18.Scanner.Kind;
import cop5556sp18.Scanner.Token;

public class ExpressionPredefinedName extends Expression {

	public final Kind name;

	public ExpressionPredefinedName(Token firstToken, Token name) {
		super(firstToken);
		this.name = name.kind;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitExpressionPredefinedName(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof ExpressionPredefinedName))
			return false;
		ExpressionPredefinedName other = (ExpressionPredefinedName) obj;
		if (name != other.name)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExpressionPredefinedName [name=" + name + "]";
	}

}
