package cop5556sp18.AST;

import cop5556sp18.Types;
import cop5556sp18.Scanner.Kind;
import org.objectweb.asm.Label;

import cop5556sp18.Scanner.Token;

public class Declaration extends ASTNode {
	
	public final Kind type;
	public final String name;
	public final Expression width;  //non null only for images declared with a size
	public final Expression height;  //non null only for images declared with a size
	
	public Types.Type decType;
	int scope;
	int slot;
	Label start;
	Label end;

	public Declaration(Token firstToken, Token type, Token name, Expression width, Expression height) {
		super(firstToken);
		this.type = type.kind;
		this.name = name.getText();
		this.width = width;
		this.height = height;
		
		decType = Types.getType(type.kind);
	}
	
	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitDeclaration(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((height == null) ? 0 : height.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((width == null) ? 0 : width.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof Declaration))
			return false;
		Declaration other = (Declaration) obj;
		if (height == null) {
			if (other.height != null)
				return false;
		} else if (!height.equals(other.height))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		if (width == null) {
			if (other.width != null)
				return false;
		} else if (!width.equals(other.width))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Declaration [type=" + type + ", name=" + name + ", width="
				+ width + ", height=" + height + "]";
	}

	public int getScope() {
		return scope;
	}
	
	public void setScope(int id) {
		scope = id;
	}

	public int getSlot() {
		return slot;
	}
	
	public void setSlot(int snum) {
		slot = snum;
	}
	
	public Label getStart() {
		return start;
	}
	
	public void setStart(Label l) {
		start = l;
	}
	
	public Label getEnd() {
		return end;
	}
	
	public void setEnd(Label l) {
		end = l;
	}
}
