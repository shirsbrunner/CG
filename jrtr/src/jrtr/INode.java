package jrtr;

import java.util.LinkedList;

import javax.vecmath.Matrix4f;

/**
 * Node Interface
 * Allows access to 
 * - transformation Matrix
 * - one shape (geometrical Object)
 * - list of children of said node (linked-list)
 * implementation must return 0 if elements do not exist
 */


public interface INode {
	public abstract Matrix4f getTransformationMatrix();
	public abstract Shape getShape();
	public abstract LinkedList<INode> getChildren();
}
