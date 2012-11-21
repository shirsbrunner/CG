package jrtr;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Stack;

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
		nodeStack.push(root); //add root as base to the Stack
		
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
			buildStack();
		}
		
		@Override
		public boolean hasNext() {
			return nodeStack.size()>1; //bigger than one, since the first element is always a group
		}

		@Override
		public RenderItem next() {
			while (nodeStack.peek() instanceof Group){ //pop unused groups as they appear
				nodeStack.pop();
			}
			
			//Shape shape = itr.next().getShape();
			Shape shape = nodeStack.pop().getShape();
			return new RenderItem(shape, shape.getTransformation());
		}

		/**
		 * builds the stack we will iterate over later
		 * TODO adjust matrices
		 */
		private void buildStack(){
			int i = 0;
			while (i < nodeStack.size()){
				if (nodeStack.elementAt(i) instanceof Group){ 
					//if it's a Group, add children to Stack. Possible to remove Group?
					for (INode child : nodeStack.elementAt(i).getChildren()){
						nodeStack.push(child);
					}
				}
				i++;
			}
			
		}
	}
}
