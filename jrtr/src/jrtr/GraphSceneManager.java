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
			return !nodeStack.isEmpty();
		}

		@Override
		public RenderItem next() {
			
			/**
			 * this builds the stack
			 */
			while (nodeStack.peek() instanceof Group){ //pop unused groups as they appear
				
				Group parent = (Group) nodeStack.pop(); //pop the group
				Matrix4f pTransformationM = parent.getTransformationMatrix(); //record transformation of group
				Matrix4f pTranslationM = parent.getTranslation();
				Matrix4f pPM = parent.getParentPosition();
				
				Matrix4f parentPosition = new Matrix4f();
				parentPosition.mul(pTransformationM, pTranslationM);
				parentPosition.mul(pPM, parentPosition);
				
				for (INode child : parent.getChildren()){
					
					child.setParentPosition(parentPosition); //this is where the parent center is
					
					nodeStack.push(child); //push child to the stack
					
				}	
			}
			
			INode node = nodeStack.pop(); //now, we have only shapes
			
			//the node should be at parentPosition*finalTransformation
			
			Shape shape = node.getShape();
			Matrix4f nodeTransformation = new Matrix4f();
			Matrix4f finalTransformation = new Matrix4f();
			
			finalTransformation.setIdentity();
			finalTransformation.mul(node.getParentPosition());
			//finalTransformation.mul(shape.getTransformation()); 
			finalTransformation.mul(node.getTransformationMatrix());
			
			return new RenderItem(shape, finalTransformation);
		}
	}
}
