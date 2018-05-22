package cop5556sp18.AST;

import cop5556sp18.Scanner.Token;

public abstract class Statement extends ASTNode {

	public Statement(Token firstToken) {
		super(firstToken);
	}

}
