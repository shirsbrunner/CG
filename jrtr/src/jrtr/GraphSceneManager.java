package jrtr;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Stack;

import javax.vecmath.Matrix4f;

/**
 * A Tree scene manager that stores objects in a Tree.
 * Allows to pass parent transformations to children
 */
public class GraphSceneManager implements SceneManagerInterface {

	private INode root;
	private Camera camera;
	private Frustum frustum;
	private LinkedList<Light> lightlist;
	private Stack<INode> nodeStack;
	
	public GraphSceneManager()
	{
		nodeStack = new Stack<INode>(); //create new Stack
		root = new Group();	//create an empty Root
		
		camera = new Camera();
		frustum = new Frustum();
		lightlist = new LinkedList<Light>();
	}
	
	public Camera getCamera()
	{
		return camera;
	}
	
	public Frustum getFrustum()
	{
		return frustum;
	}
	
	public SceneManagerIterator iterator()
	{
		return new GraphSceneManagerItr(this);
	}
	
	public INode getRoot(){
		return this.root;
	}
	
	/**
	 * To be implemented in the "Textures and Shading" project.
	 * Returns an iterator over the lightlist (sh)
	 */
	public Iterator<Light> lightIterator(){
		Iterator<Light> itr = lightlist.iterator();
		return itr;
	}
	
	/**
	 * adds a light to the light-list (sh)
	 * TODO this needs to be changed, since Light becomes a light-List
	 * @param light
	 */
	public void addLight(Light light){
		lightlist.add(light);
	}

	private class GraphSceneManagerItr implements SceneManagerIterator{

		public GraphSceneManagerItr(GraphSceneManager sceneManager){
			nodeStack.push(root); //add root as base to the Stack
		}
		
		@Override
		public boolean hasNext() {
			return nodeStack.size()>0; //bigger than 0 since we automatically pop groups, everything is a leaf
		}

		@Override
		public RenderItem next() {
			while (nodeStack.peek() instanceof Group){ //pop unused groups as they appear
				Group tempMaster = (Group) nodeStack.pop(); //pop the group
				Matrix4f parentMatrix = tempMaster.getTransformationMatrix(); //record transformation of group
				for (INode child : tempMaster.getChildren()){
					Matrix4f childMatrix = child.getTransformationMatrix(); //get node-transformation of child
					childMatrix.mul(parentMatrix); //set group transformation
					nodeStack.push(child); //push child to the stack
				}	
			}
			
			INode node = nodeStack.pop(); //this can be a light or a shape, but here it's only shapes
			
			Shape shape = node.getShape();
			Matrix4f identity = new Matrix4f(); 
			identity.setIdentity();
			Matrix4f transformation = node.getTransformationMatrix(); //shape node transformation
			Matrix4f shapeTransformation = shape.getTransformation(); //shape inner transformation
			
			identity.mul(transformation); //node-transformation
			identity.mul(shapeTransformation); //shape-transformation


			
			return new RenderItem(shape, identity);
		}
	}
}
