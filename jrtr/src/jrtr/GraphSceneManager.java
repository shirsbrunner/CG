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
	private Stack nodeStack;
	
	public GraphSceneManager()
	{
		nodeStack = new Stack(); //create new Stack
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
	
	/* shape-adding is done in the root, not here anymore, everything ist (if new Object added to root), else added to a sub-Object of Root
	public void addShape(Shape shape)
	{
		//maybe rename this to add ShapeNode();
		
		ShapeNode newShape = new ShapeNode();
		newShape.setShape(shape);
		root.getChildren().add(newShape);
		
		//shapes.add(shape);
	}
	*/
	
	public SceneManagerIterator iterator()
	{
		return new GraphSceneManagerItr(this);
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

		StackIterator itr;
		
		public GraphSceneManagerItr(GraphSceneManager sceneManager){
			itr = sceneManager.nodeStack.listIterator();
		}
		
		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public RenderItem next() {
			// TODO Auto-generated method stub
			//take next thing from Stack, this needs to be a RenderItem, not a Shape, go figure...
			//build said render Item out of the group-Transformation-Matrix and the Shape
			return null;
		}
		
	}
	
	/*
	private class SimpleSceneManagerItr implements SceneManagerIterator {
		
		public SimpleSceneManagerItr(GraphSceneManager sceneManager)
		{
			//itr = sceneManager.shapes.listIterator(0); //TODO Think about that
		}
		
		public boolean hasNext()
		{
			return itr.hasNext();
		}
		
		public RenderItem next()
		{
			Shape shape = itr.next();
			// Here the transformation in the RenderItem is simply the 
			// transformation matrix of the shape. More sophisticated 
			// scene managers will set the transformation for the 
			// RenderItem differently.
			return new RenderItem(shape, shape.getTransformation());
		}
		
		ListIterator<Shape> itr;
	}
	*/
}
