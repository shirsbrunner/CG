package jrtr;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Iterator;

/**
 * A Tree scene manager that stores objects in a Tree.
 * Allows to pass parent transformations to children
 */
public class TreeSceneManager implements SceneManagerInterface {

	private LinkedList<Shape> shapes;
	private Camera camera;
	private Frustum frustum;
	private LinkedList<Light> lightlist; 
	
	public TreeSceneManager()
	{
		shapes = new LinkedList<Shape>();
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
	
	public void addShape(Shape shape)
	{
		shapes.add(shape);
	}
	
	public SceneManagerIterator iterator()
	{
		return new SimpleSceneManagerItr(this);
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
	 * @param light
	 */
	public void addLight(Light light){
		lightlist.add(light);
	}

	private class SimpleSceneManagerItr implements SceneManagerIterator {
		
		public SimpleSceneManagerItr(TreeSceneManager sceneManager)
		{
			itr = sceneManager.shapes.listIterator(0);
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
}
