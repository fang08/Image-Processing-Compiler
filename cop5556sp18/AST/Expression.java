package cop5556sp18.AST;

import cop5556sp18.Scanner.Token;
import cop5556sp18.Types;

public abstract class Expression extends ASTNode {
	public Types.Type type;
	
	public Expression(Token firstToken) {
		super(firstToken);
	}
	
	public Types.Type getType() {
		return type;
	}

}
