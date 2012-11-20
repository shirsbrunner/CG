package jrtr;

import java.util.LinkedList;

import javax.vecmath.Matrix4f;

public class Group implements INode {

	private LinkedList<INode> children;
	private Shape shape;
	private Matrix4f tMatrix;

	/**
	 * Die Klasse Group soll eine Liste von Referenzen auf Kinder speichern. Sie soll Funktionalität zum
	 * Hinzufügen und Entfernen von Kindern enthalten. Sie können zum Beispiel eine LinkedList
	 * aus der java.util.Collection Package verwenden, um die Kinder zu verwalten.
	 */
	
	public Group(){
		this.children = new LinkedList<INode>();
		this.shape = null;
		this.tMatrix = new Matrix4f();
		tMatrix.setIdentity();	
	}

	/**
	 * sets the TransformationMatrix the group acts as a Transform-Group
	 * @param newTrans
	 */
	public void setTransformationMatrix(Matrix4f newTrans){
		this.tMatrix = newTrans;
	}
	
	/**
	 * adds a child 
	 * @param child
	 */
	public void addChild(INode child){
		this.children.add(child);
	}

	public void removeChild(INode child){
		if (this.children.contains(child)) this.children.remove(child);
		else System.out.println("no such child");	
	}
	
	@Override
	public Matrix4f getTransformationMatrix() {
		return this.tMatrix;
	}

	@Override
	public Shape getShape() {
		return this.shape;
	}

	@Override
	public LinkedList<INode> getChildren() {
		return this.children;
	}
	
	
	
}
