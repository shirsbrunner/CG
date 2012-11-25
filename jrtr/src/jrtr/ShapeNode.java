package jrtr;

import java.util.LinkedList;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public class ShapeNode extends Leaf {

	
	private Shape shape;
	private Matrix4f transformationMatrix, parentPosition;

	public ShapeNode(){
		this.shape = null;
		this.transformationMatrix = new Matrix4f();
		this.parentPosition = new Matrix4f();
		
		this.transformationMatrix.setIdentity();
		this.parentPosition.setIdentity();
	}
	
	public void setShape(Shape newShape){
		this.shape = newShape;
	}
	
	/**
	 * this is attaching the arm relative to the body (example)
	 * @param trans movement from the body-center
	 */
	public void setTranslation(Vector3f trans){
		this.shape.getTransformation().setTranslation(trans);
	}
	
	@Override
	public void setTransformationMatrix(Matrix4f matrix){
		this.transformationMatrix = matrix;
	}
	
	/**
	 * returns the transformation Matrix of the node
	 */
	@Override
	public Matrix4f getTransformationMatrix() {
		return transformationMatrix;
	}

	@Override
	public Shape getShape() {
		return this.shape;
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
