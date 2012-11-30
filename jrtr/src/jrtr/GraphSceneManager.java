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
		root = new TransformGroup();	//create an empty Root
		
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

		GraphSceneManager sceneManager;
		
		public GraphSceneManagerItr(GraphSceneManager sceneManager){
			nodeStack.push(root); //add root as base to the Stack
			//clear lightlist
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
			
	
			while (nodeStack.peek() instanceof TransformGroup){ //pop unused groups as they appear
					
				TransformGroup parent = (TransformGroup) nodeStack.pop(); //pop the group
				Matrix4f pTransformationM = parent.getTransformationMatrix(); //record transformation of group
				Matrix4f pTranslationM = parent.getTranslation();
				Matrix4f pPM = parent.getParentPosition();
					
				Matrix4f parentPosition = new Matrix4f();
				parentPosition.mul(pTransformationM, pTranslationM);
				parentPosition.mul(pPM, parentPosition);
					
				for (INode child : parent.getChildren()){
						
					child.setParentPosition(parentPosition); //this is where the parent center is
					
					if (child instanceof LightNode){ //unpack lights
						if (!lightlist.isEmpty()){
							lightlist.removeFirst();} //delete the first light, as it should be the light, that is replaced (Tree doesn't change)
						LightNode light = (LightNode) child;
						Matrix4f finalTransformation = new Matrix4f();
						finalTransformation.setIdentity();
						finalTransformation.mul(light.getParentPosition());
						finalTransformation.mul(light.getTransformationMatrix());
						float lightx = finalTransformation.m03;
						float lighty = finalTransformation.m13;
						float lightz = finalTransformation.m23;
						
						//System.out.println("x,y,z: "+lightx+","+lighty+","+lightz);
						
						light.getLight().setCoordinates(lightx, lighty, lightz);
						//System.out.println(light.getLight().toString());
						lightlist.add(light.getLight());
					}
						
					else {nodeStack.push(child);} //push child to the stack			
				}	
			}
			ShapeNode node = (ShapeNode) nodeStack.pop(); //now, we have only shapes
			//the node should be at parentPosition*finalTransformation
			
			Shape shape = node.getShape();
			Matrix4f finalTransformation = new Matrix4f();
			
			finalTransformation.setIdentity();
			finalTransformation.mul(node.getParentPosition());
			finalTransformation.mul(node.getTransformationMatrix());
			
			return new RenderItem(shape, finalTransformation);
		}
	}
}
