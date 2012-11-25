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
	/**
	 * transform this node
	 * @param matrix
	 */
	public abstract void setTransformationMatrix(Matrix4f matrix);
	/**
	 * get transformation of this node
	 * @return
	 */
	public abstract Matrix4f getTransformationMatrix();
	
	/**
	 * save transformation of parent-node
	 * @param matrix
	 */
	public abstract void setParentPosition(Matrix4f matrix);
	/**
	 * get transformation of parent-node
	 * @return
	 */
	public abstract Matrix4f getParentPosition();
	
	public abstract Shape getShape();
	public abstract LinkedList<INode> getChildren();
}
