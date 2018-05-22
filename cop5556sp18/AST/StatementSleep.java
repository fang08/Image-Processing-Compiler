package cop5556sp18.AST;

import cop5556sp18.Scanner.Token;

public class StatementSleep extends Statement {
	
	public final Expression duration;
	
	public StatementSleep(Token firstToken, Expression duration) {
		super(firstToken);
		this.duration = duration;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitStatementSleep(this,arg);
	}

	@Override
	public String toString() {
		return "StatementSleep [duration=" + duration + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((duration == null) ? 0 : duration.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof StatementSleep))
			return false;
		StatementSleep other = (StatementSleep) obj;
		if (duration == null) {
			if (other.duration != null)
				return false;
		} else if (!duration.equals(other.duration))
			return false;
		return true;
	}



}
