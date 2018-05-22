package cop5556sp18.AST;

import cop5556sp18.Scanner.Token;

public class ExpressionPixel extends Expression {

	public final String name;
	public final PixelSelector pixelSelector;
	
	public Declaration dec;

	public ExpressionPixel(Token firstToken, Token name,
			PixelSelector pixelSelector) {
		super(firstToken);
		this.name = name.getText();
		this.pixelSelector = pixelSelector;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitExpressionPixel(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((pixelSelector == null) ? 0 : pixelSelector.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof ExpressionPixel))
			return false;
		ExpressionPixel other = (ExpressionPixel) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pixelSelector == null) {
			if (other.pixelSelector != null)
				return false;
		} else if (!pixelSelector.equals(other.pixelSelector))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExpressionPixel [name=" + name + ", pixelSelector="
				+ pixelSelector + "]";
	}

}
