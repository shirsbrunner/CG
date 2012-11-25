package jrtr;

import java.util.LinkedList;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public class Group implements INode {

	private LinkedList<INode> children;
	private Shape shape;
	private Matrix4f transformMatrix, translationMatrix, parentPosition;

	/**
	 * Die Klasse Group soll eine Liste von Referenzen auf Kinder speichern. Sie soll Funktionalität zum
	 * Hinzufügen und Entfernen von Kindern enthalten. Sie können zum Beispiel eine LinkedList
	 * aus der java.util.Collection Package verwenden, um die Kinder zu verwalten.
	 */
	
	public Group(){
		this.children = new LinkedList<INode>();
		this.shape = null;
		this.transformMatrix = new Matrix4f();
		this.translationMatrix = new Matrix4f();
		this.parentPosition = new Matrix4f();
		
		this.transformMatrix.setIdentity();
		this.translationMatrix.setIdentity();
		this.parentPosition.setIdentity();
	}

	/**
	 * sets the TransformationMatrix the group acts as a Transform-Group
	 * @param newTrans
	 */
	@Override
	public void setTransformationMatrix(Matrix4f newTrans){
		this.transformMatrix = newTrans;
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
	
	/**
	 * initial moving
	 * @param translation
	 */
	public void setTranslation(Vector3f translation){
		this.translationMatrix.setTranslation(translation);
	}
	
	/**
	 * initial moving
	 * @return
	 */
	public Matrix4f getTranslation(){
		return this.translationMatrix;
	}
	
	/**
	 * turning the node during runtime
	 */
	@Override
	public Matrix4f getTransformationMatrix() { //the one, that is handed down to children...
		return this.transformMatrix;
	}
	
	@Override
	public Shape getShape() {
		return this.shape;
	}

	@Override
	public LinkedList<INode> getChildren() {
		return this.children;
	}

	@Override
	public void setParentPosition(Matrix4f matrix) {
		this.parentPosition = matrix;
		
	}

	@Override
	public Matrix4f getParentPosition() {
		return this.parentPosition;
	}
	
	
	
}
