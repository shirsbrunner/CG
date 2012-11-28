package jrtr;

import java.util.LinkedList;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public class LightNode extends Leaf {

	
	private Light light;
	private Matrix4f transformationMatrix, parentPosition;

	public LightNode(){
		this.transformationMatrix = new Matrix4f();
		this.parentPosition = new Matrix4f();
		
		this.transformationMatrix.setIdentity();
		this.parentPosition.setIdentity();
	}
	
	public void setLight(Light newLight){
		this.light = newLight;
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
		return this.transformationMatrix;
	}

	public Light getLight(){
		return this.light;
	}
	
	@Override
	public Shape getShape() {
		return null;
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
