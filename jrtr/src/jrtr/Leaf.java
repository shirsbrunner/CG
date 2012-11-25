package jrtr;

import java.util.LinkedList;

public abstract class Leaf implements INode{

	@Override
	public LinkedList<INode> getChildren() {
		return null; //has no children
	}
	
	
}
