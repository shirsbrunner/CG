import jrtr.*;
import javax.swing.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.vecmath.*;

import buildingBlocks.*;


import java.util.Timer;
import java.util.TimerTask;

/**
 * Implements a simple application that opens a 3D rendering window and 
 * shows a rotating cube.
 * 
 * upgrade shirsbrunner
 * 
 * added shape Interface
 * added torus
 * added cylinder
 * added shape combinator (combines shape data of two shapes to a new shape
 * created scene with all three shapes combined into one
 * 
 */
public class simple
{	
	static RenderPanel renderPanel;
	static RenderContext renderContext;
	static SimpleSceneManager sceneManager;
	static Shape shape;
	static float angle;

	/**
	 * An extension of {@link GLRenderPanel} or {@link SWRenderPanel} to 
	 * provide a call-back function for initialization. 
	 */ 
	public final static class SimpleRenderPanel extends GLRenderPanel
	{
		/**
		 * Initialization call-back. We initialize our renderer here.
		 * 
		 * @param r	the render context that is associated with this render panel
		 */
		public void init(RenderContext r)
		{
			renderContext = r;
			renderContext.setSceneManager(sceneManager);
	
			// Register a timer task
		    Timer timer = new Timer();
		    angle = 0.01f;
		    timer.scheduleAtFixedRate(new AnimationTask(), 0, 10);
		}
	}

	/**
	 * A timer task that generates an animation. This task triggers
	 * the redrawing of the 3D scene every time it is executed.
	 */
	public static class AnimationTask extends TimerTask
	{
		public void run()
		{
System.out.println("to see this, you need to change the shaders");
			
			// Update transformation
    		Matrix4f t = shape.getTransformation();
    		Matrix4f rotX = new Matrix4f();
    		rotX.rotX(angle);
    		Matrix4f rotY = new Matrix4f();
    		rotY.rotY(angle);
    		t.mul(rotX);
    		t.mul(rotY);
    		shape.setTransformation(t);
    		
    		// Trigger redrawing of the render window
    		renderPanel.getCanvas().repaint(); 
		}
	}

	/**
	 * A mouse listener for the main window of this application. This can be
	 * used to process mouse events.
	 */
	public static class SimpleMouseListener implements MouseListener
	{
    	public void mousePressed(MouseEvent e) {}
    	public void mouseReleased(MouseEvent e) {}
    	public void mouseEntered(MouseEvent e) {}
    	public void mouseExited(MouseEvent e) {}
    	public void mouseClicked(MouseEvent e) {}
	}
	
	/**
	 * The main function opens a 3D rendering window, constructs a simple 3D
	 * scene, and starts a timer task to generate an animation.
	 */
	public static void main(String[] args)
	{		
		
		/**
		 * basic shapes
		 */
		//IBasicShape exampleShape = new ExampleCube();
		//IBasicShape exampleShape = new Cylinder(20, 2, 1, 1 , 2);
		//IBasicShape exampleShape = new Torus(20, 20, 2, 1, 8);

		/** 
		 * example of torus, Cylinder, basic cube and shapeCombynator
		 */
		IBasicShape baseCylinder = new Cylinder(20, 9, 0.5f,0.25f,10); //needs a cast for 0.5 numbers: 0.5f
		IBasicShape baseTorus = new Torus(50, 50, 3, 1, 11);
		IBasicShape exampleCube = new ExampleCube();
		IBasicShape tempCombyShape = new ShapeCombinator(exampleCube, baseTorus);
		IBasicShape exampleShape = new ShapeCombinator(tempCombyShape, baseCylinder);
		
		/**
		IBasicShape cubeOne = new ExampleCube();
		IBasicShape cubeTwo = new ExampleCube();
		
		cubeOne.moveX(2);
		cubeOne.moveY(2);
		cubeOne.moveZ(2);
		
		IBasicShape exampleShape = new ShapeCombinator(cubeOne, cubeTwo);
		*/
		
		VertexData vertexData = exampleShape.getVertexData();//exampleShape.getVertexData(); //extract Vertices from shape
		


				
		// Make a scene manager and add the object
		sceneManager = new SimpleSceneManager();
		shape = new Shape(vertexData);
		sceneManager.addShape(shape);

		// Make a render panel. The init function of the renderPanel
		// (see above) will be called back for initialization.
		renderPanel = new SimpleRenderPanel();
		
		// Make the main window of this application and add the renderer to it
		JFrame jframe = new JFrame("simple");
		jframe.setSize(750, 750);
		jframe.setLocationRelativeTo(null); // center of screen
		jframe.getContentPane().add(renderPanel.getCanvas());// put the canvas into a JFrame window

		// Add a mouse listener
	    jframe.addMouseListener(new SimpleMouseListener());
		   	    	    
	    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    jframe.setVisible(true); // show window
	}
	
	/**
	 * method to initially turn things
	 */
	public Shape turnShape(Shape tempShape, float xAngle, float yAngle, float zAngle){
		
		Matrix4f t = tempShape.getTransformation();
		Matrix4f rotX = new Matrix4f();
		rotX.rotX(xAngle);
		Matrix4f rotY = new Matrix4f();
		rotY.rotY(yAngle);
		Matrix4f rotZ = new Matrix4f();
		rotZ.rotZ(yAngle);
		
		t.mul(rotX);
		t.mul(rotY);
		t.mul(rotZ);
		tempShape.setTransformation(t);
		
		return tempShape;
	}


}
