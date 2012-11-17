import jrtr.*;
import javax.swing.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

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
 * added camera change possibilities
 * added frustum change possibilities
 * added drag & drop possibilities 
 * 
 */
public class ObjReaderInput
{	
	static RenderPanel renderPanel;
	static RenderContext renderContext;
	static SimpleSceneManager sceneManager;
	static Shape sTeapot;
	static float angle;
	static boolean initTrigger = true; 
	static JFrame jframe;
	static Matrix4f mouseTurn;
	static float adjustToScreenSize;
	//static Vector3f newMoveVector, oldMoveVector;

	/**
	 * An extension of {@link GLRenderPanel} or {@link SWRenderPanel} to 
	 * provide a call-back function for initialization. 
	 */ 
	public final static class SimpleRenderPanel extends GLRenderPanel
	{
		/**
		 * Initialization call-back. We initialize our renderer here.
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
			adjustToScreenSize = (float) Math.min(jframe.getWidth(), jframe.getHeight()); //used here, since you can change the screen-Size
			
			Matrix4f newTranslation = new Matrix4f();
			newTranslation.setIdentity();
			
			Matrix4f oldTranslation = new Matrix4f(); 
    		oldTranslation = sTeapot.getTransformation();
    		
    		if (mouseTurn != null){
    			newTranslation.mul(mouseTurn);
    			mouseTurn.setIdentity();
    		}
    		    		
    		newTranslation.mul(oldTranslation);
    		
    		sTeapot.setTransformation(newTranslation);
    		//something still appears to be wrong while turning
			
			// Trigger redrawing of the render window
    		renderPanel.getCanvas().repaint(); 
		}
		
		public Matrix4f moveTo(float x, float y, float z){
			Matrix4f tempTrans = new Matrix4f();
			tempTrans.setM00(1);
			tempTrans.setM11(1);
			tempTrans.setM22(1);
			tempTrans.setM33(1);
			tempTrans.setM03(x);
			tempTrans.setM13(y);
			tempTrans.setM23(z);
			return tempTrans;
		}
	}
	
	/**
	 * A mouse listener for the main window of this application. This can be
	 * used to process mouse events.
	 */
	public static class SimpleMouseListener implements MouseListener, MouseMotionListener
	{
    	private Vector3f oldMoveVector, newMoveVector;
    	float oldX, oldY, oldZ2, oldZ;
   		float newX, newY, newZ2, newZ;
   		boolean zMove = false;
    	
		public void mousePressed(MouseEvent e) {

			zMove = false;
    		//get initial x,y
    		oldX = e.getX()/(adjustToScreenSize/2) - 1; //used to be (float)jframe.getWidth()
    		oldY = 1 - e.getY()/(adjustToScreenSize/2); //used to be (float)jframe.getWidth()
    		
    		//calculate z
    		oldZ2 = 1-oldX*oldX - oldY*oldY;
    		if (oldZ2 > 0){ oldZ = (float) Math.sqrt(oldZ2);}
    		else {
    			oldZ = 0;
    			zMove = true;
    		}
    		
    	}
    	public void mouseReleased(MouseEvent e) {}
    	public void mouseEntered(MouseEvent e) {}
    	public void mouseExited(MouseEvent e) {}
    	public void mouseClicked(MouseEvent e) {}
		public void mouseDragged(MouseEvent e) {

			oldMoveVector = new Vector3f(oldX, oldY, oldZ);
			//get x,y
    		newX = e.getX()/(adjustToScreenSize/2) - 1; //used to be (float)jframe.getWidth()
    		newY = 1 - e.getY()/(adjustToScreenSize/2); //used to be (float)jframe.getWidth()
    		
    		//calculate z
    		if(zMove == true){ //fixes the z axis
    			newZ2 = 0;}
    		else{
    			newZ2 = 1-newX*newX - newY*newY;
    		}
    		if (newZ2 > 0){ newZ = (float) Math.sqrt(newZ2);}
    		else {newZ = 0;}
    		
			newMoveVector = new Vector3f(newX, newY, newZ);
			
			//now calculate the movement
			Vector3f moveAxis = new Vector3f(); //moving around this axis
			moveAxis.cross(oldMoveVector, newMoveVector);
			float moveAngle = oldMoveVector.angle(newMoveVector); //moving by this angle
			
			//special Vector including the angle
			AxisAngle4f moveAxisAngle = new AxisAngle4f();
			moveAxisAngle.set(moveAxis, moveAngle); 
			
			//calculate turning axis
			mouseTurn = new Matrix4f();
			mouseTurn.setIdentity();
			mouseTurn.set(moveAxisAngle); //the new Multiplication-Matrix
			
			//override the initial starting values
			oldX = newX;
			oldY = newY;
			oldZ = newZ;
			
		}
		public void mouseMoved(MouseEvent e) {}
	}
	
	/**
	 * The main function opens a 3D rendering window, constructs a simple 3D
	 * scene, and starts a timer task to generate an animation.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{		
		
		sceneManager = new SimpleSceneManager();
		ObjReader reader = new ObjReader();
		VertexData vTeapot = reader.read("./Objects/teapot.obj", 10f);
		sTeapot = new Shape(vTeapot);
		
		sceneManager.addShape(sTeapot);
		
		 
		//Parameter Bild 1
		//adjust the camera
		Camera camera = sceneManager.getCamera();

		Vector3f centerOfProjection = new Vector3f(0,0,40);
		Vector3f lookAtPoint = new Vector3f(0,0,0);
		Vector3f upVector = new Vector3f(0,1,0);
		camera.setCameraMatrix(centerOfProjection, lookAtPoint, upVector);
		
		
		/*
		//Parameter Bild 2
		//adjust the camera
		Camera camera = sceneManager.getCamera();

		Vector3f centerOfProjection = new Vector3f(-10,40,40);
		Vector3f lookAtPoint = new Vector3f(-5,0,0);
		Vector3f upVector = new Vector3f(0,1,0);
		camera.setCameraMatrix(centerOfProjection, lookAtPoint, upVector);
		*/
		
		//adjust the frustum
		Frustum frustum = sceneManager.getFrustum();
		
		float nearPlane = 1;
		float farPlane = 100;
		float aspectRatio = 1;
		float vFieldOfView = 60;
		frustum.setFrustum(nearPlane, farPlane, aspectRatio, vFieldOfView);
		
		

		
		// Make a render panel. The init function of the renderPanel
		// (see above) will be called back for initialization.
		renderPanel = new SimpleRenderPanel();
		
		// Make the main window of this application and add the renderer to it
		//JFrame 
		jframe = new JFrame("simple");
		jframe.setSize(750, 750);
		jframe.setLocationRelativeTo(null); // center of screen
		jframe.getContentPane().add(renderPanel.getCanvas());// put the canvas into a JFrame window

		// Add a mouse listener
		SimpleMouseListener mouse = new SimpleMouseListener();
		renderPanel.getCanvas().addMouseListener(mouse); //change proposed in forum
		renderPanel.getCanvas().addMouseMotionListener(mouse);
		   	    	    
	    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    jframe.setVisible(true); // show window
	}
	

	/**
	 * Method to ease initial movement of things, while setting up the scene
	 * could maybe use setTranslate with a Vector3f
	 * Moves the Shape and his relative center!!!
	 * @param tempShape
	 * @return
	 */
	public static Shape moveShape(Shape tempShape, float x, float y, float z){

		Matrix4f t = tempShape.getTransformation();
		Matrix4f move = new Matrix4f();
		Vector3f goal = new Vector3f();
		goal.setX(x);
		goal.setY(y);
		goal.setZ(z);

		move.setTranslation(goal);
		t.add(move);
		
		tempShape.setTransformation(t);
		
		return tempShape;
	}
	
	/**
	 * Method to ease initial turning of things
	 * different Order would give different Results...
	 * turns the shape and his relative center
	 * 
	 * @param tempShape the shape to turn
	 * @param xAngle x-angle (float, degrees)
	 * @param yAngle y-angle (float, degrees)
	 * @param zAngle z-angle (float, degrees)
	 * @return the turned shape
	 */
	public static Shape turnShape(Shape tempShape, float xAngle, float yAngle, float zAngle){
		
		Matrix4f t = tempShape.getTransformation();
		Matrix4f rotX = new Matrix4f();
		rotX.rotX((float) Math.toRadians(xAngle));
		Matrix4f rotY = new Matrix4f();
		rotY.rotY((float) Math.toRadians(yAngle));
		Matrix4f rotZ = new Matrix4f();
		rotZ.rotZ((float) Math.toRadians(zAngle));
		
		t.mul(rotX);
		t.mul(rotY);
		t.mul(rotZ);
		tempShape.setTransformation(t);
		
		return tempShape;
	}
	
}
