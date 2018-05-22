package cop5556sp18.AST;

import cop5556sp18.Scanner.Token;

public class StatementAssign extends Statement {

	public final LHS lhs;
	public final Expression e;

	public StatementAssign(Token firstToken, LHS lhs, Expression e) {
		super(firstToken);
		this.lhs = lhs;
		this.e = e;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitStatementAssign(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((e == null) ? 0 : e.hashCode());
		result = prime * result + ((lhs == null) ? 0 : lhs.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof StatementAssign))
			return false;
		StatementAssign other = (StatementAssign) obj;
		if (e == null) {
			if (other.e != null)
				return false;
		} else if (!e.equals(other.e))
			return false;
		if (lhs == null) {
			if (other.lhs != null)
				return false;
		} else if (!lhs.equals(other.lhs))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StatementAssign [lhs=" + lhs + ", e=" + e + "]";
	}

}
