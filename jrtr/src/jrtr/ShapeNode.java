package jrtr;

import java.util.LinkedList;
import javax.vecmath.Matrix4f;

public class ShapeNode implements INode {

	
	private Shape shape;
	private Matrix4f transformationMatrix;

	public ShapeNode(){
		this.shape = null;
		this.transformationMatrix = new Matrix4f();
		transformationMatrix.setIdentity();
		
	}
	
	public void setShape(Shape newShape){
		this.shape = newShape;
	}
	
	public void setTransformationMatrix(Matrix4f matrix){
		this.transformationMatrix = matrix;
	}
	
	
	
	@Override
	public Matrix4f getTransformationMatrix() {
		// maybe give Back this.shape.getTransformation();?
		return this.transformationMatrix;
	}

	@Override
	public Shape getShape() {
		return this.shape;
	}

	@Override
	public LinkedList<INode> getChildren() {
		return null; //has no children
	}

}
