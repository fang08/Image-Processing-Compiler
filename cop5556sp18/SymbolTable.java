package cop5556sp18;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import cop5556sp18.AST.Declaration;


public class SymbolTable {
	final int pervasiveScope;
	int currentScope;
	int nextScope;
	Stack<Integer> scopeStack;
	HashMap<String, ArrayList<Declaration>> hashTable; 
	
	public SymbolTable() {
		pervasiveScope = 0;
		currentScope = 0;
		nextScope = 1; //start from global
		scopeStack = new Stack<Integer>();
		hashTable = new HashMap<String, ArrayList<Declaration>>();
		
		enterScope();
	}
	
	public void enterScope() {
		currentScope = nextScope;
		nextScope++;
		scopeStack.push(currentScope);
	}
	
	public void leaveScope() {
		scopeStack.pop();
		currentScope = scopeStack.peek();
	}
	
	//place a binding into symbol table
	public boolean insert(String name, Declaration dec) {
		if (hashTable.containsKey(name)) {
			ArrayList<Declaration> exists = hashTable.get(name);
			for (Declaration temp: exists) {
				if (temp.name.equals(name)) { //in case of hash collisions
					if (temp.getScope() == currentScope) //duplicated declaration in the same scope
						return false;
				}
			}
			dec.setScope(currentScope);
			exists.add(dec);
			return true;
		}
		else {
			dec.setScope(currentScope);
			ArrayList<Declaration> input = new ArrayList<Declaration>();
			input.add(dec);
			hashTable.put(name, input);
			return true;
		}
	}
	
	//search existing binding and return declaration
	public Declaration lookup(String name) {
		Declaration pervasive = null;
		Declaration best = null;
		ArrayList<Declaration> chain = hashTable.get(name);
		
		//nothing in the arrayList, no such name
		if (chain == null)
			return null;
		//loop the arrayList
		for (Declaration temp: chain) {
			if (temp.name.equals(name)) {
				if (temp.getScope() == pervasiveScope)
					pervasive = temp;
				else {
					for (int i = scopeStack.size() - 1; i >= 0; i--) {
						if (scopeStack.get(i) == temp.getScope()) {
							best = temp;
							break;
						}
						else if (best != null && scopeStack.get(i) == best.getScope())
							break;
					}
				}
			}
		}
		if (best != null)
			return best;
		else if (pervasive != null)
			return pervasive;
		else
			return null;
	}
	
	@Override
	public String toString() {
		return "Current scope: " + currentScope;
	}
	
}
