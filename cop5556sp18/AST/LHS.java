package cop5556sp18.AST;

import cop5556sp18.Scanner.Token;
import cop5556sp18.Types;

public abstract class LHS extends ASTNode {

	public Types.Type type;
	
	public LHS(Token firstToken) {
		super(firstToken);
	}

}
