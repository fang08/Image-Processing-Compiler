package cop5556sp18.AST;

import cop5556sp18.Scanner.Token;

public class StatementWrite extends Statement {

	public final String sourceName;
	public final String destName;

	public Declaration sourceDec;
	public Declaration destDec;
	
	public StatementWrite(Token firstToken, Token sourceName, Token destName) {
		super(firstToken);
		this.sourceName = sourceName.getText();
		this.destName = destName.getText();
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitStatementWrite(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((destName == null) ? 0 : destName.hashCode());
		result = prime * result
				+ ((sourceName == null) ? 0 : sourceName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof StatementWrite))
			return false;
		StatementWrite other = (StatementWrite) obj;
		if (destName == null) {
			if (other.destName != null)
				return false;
		} else if (!destName.equals(other.destName))
			return false;
		if (sourceName == null) {
			if (other.sourceName != null)
				return false;
		} else if (!sourceName.equals(other.sourceName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StatementWrite [sourceName=" + sourceName + ", destName="
				+ destName + "]";
	}

}
