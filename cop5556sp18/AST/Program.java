package cop5556sp18.AST;

import cop5556sp18.Scanner.Token;

public class Program extends ASTNode {

	public final String progName;
	public final Block block;

	public Program(Token firstToken, Token progName, Block block) {
		super(firstToken);
		this.progName = progName.getText();
		this.block = block;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitProgram(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((block == null) ? 0 : block.hashCode());
		result = prime * result
				+ ((progName == null) ? 0 : progName.hashCode());
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
		Program other = (Program) obj;
		if (block == null) {
			if (other.block != null)
				return false;
		} else if (!block.equals(other.block))
			return false;
		if (progName == null) {
			if (other.progName != null)
				return false;
		} else if (!progName.equals(other.progName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Program [progName=" + progName + ", block=" + block + "]";
	}

}
