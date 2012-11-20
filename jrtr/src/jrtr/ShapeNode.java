package jrtr;

import java.util.LinkedList;
import javax.vecmath.Matrix4f;

public class ShapeNode implements INode {

	
	private Shape shape;

	public ShapeNode(){
		this.shape = null;
	}
	
	public void setShape(Shape newShape){
		this.shape = newShape;
	}
	
	
	
	@Override
	public Matrix4f getTransformationMatrix() {
		// maybe give Back this.shape.getTransformation();?
		return null;
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
