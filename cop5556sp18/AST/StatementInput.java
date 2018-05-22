package cop5556sp18.AST;

import cop5556sp18.Scanner.Token;
import cop5556sp18.Types;

public class StatementInput extends Statement {

	public final String destName;
	public final Expression e;

	public Declaration dec;
	
	public StatementInput(Token firstToken, Token destName, Expression e) {
		super(firstToken);
		this.destName = destName.getText();
		this.e = e;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitStatementInput(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((destName == null) ? 0 : destName.hashCode());
		result = prime * result + ((e == null) ? 0 : e.hashCode());
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
		StatementInput other = (StatementInput) obj;
		if (destName == null) {
			if (other.destName != null)
				return false;
		} else if (!destName.equals(other.destName))
			return false;
		if (e == null) {
			if (other.e != null)
				return false;
		} else if (!e.equals(other.e))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StatementInput [destName=" + destName + ", e=" + e + "]";
	}

}
