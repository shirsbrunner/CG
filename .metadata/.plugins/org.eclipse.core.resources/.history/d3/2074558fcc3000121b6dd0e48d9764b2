import jrtr.*;

import javax.swing.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
public class LandscapeFlyer
{	
	static RenderPanel renderPanel;
	static RenderContext renderContext;
	static SimpleSceneManager sceneManager;
	static Shape sLandscape;
	static Camera camera;
	static float angle;
	static boolean initTrigger = true; 
	static JFrame jframe;
	static Matrix4f mouseTurn, keyMove, mouseWorldTurn;
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
			
			Matrix4f oldcTranslation = new Matrix4f(); 
    		oldcTranslation = camera.getCameraMatrix();
    		

    		//world z-Axis-turn
    		if (mouseWorldTurn != null){
    			newTranslation.mul(mouseWorldTurn);
    			mouseWorldTurn.setIdentity();
    		}
    		
    		//camera x-Axis-turn
    		if (mouseTurn != null){
    			newTranslation.mul(mouseTurn);
    			mouseTurn.setIdentity();
    		}
    		
    		//camera movement
    		if (keyMove != null){
    			newTranslation.mul(keyMove);
    			keyMove.setIdentity();
    		}
    		    		
    		newTranslation.mul(oldcTranslation);
    		
    		camera.setCameraMatrix(newTranslation);
    		//something still appears to be wrong while turning
			
			// Trigger redrawing of the render window
    		renderPanel.getCanvas().repaint(); 
		}
	}
	
	/**
	 * KeyListener
	 * @author Boss
	 *
	 */
	public static class MyKeyListener implements KeyListener {
		int scale = 1; //scale movements
		
		public void keyPressed(KeyEvent e) {
			keyMove = new Matrix4f();
			keyMove.setIdentity();
			Vector3f keyMovement = new Vector3f();
			
			switch(e.getKeyChar()) {
			case 's':  
				keyMovement.z = -1*scale;
				keyMove.setTranslation(keyMovement);
			break;
			
			case 'w': 
				keyMovement.z = 1*scale;
				keyMove.setTranslation(keyMovement);
			break;
			
			case 'a': 
				keyMovement.x = 1*scale;
				keyMove.setTranslation(keyMovement);
			break;
			
			case 'd': 
				keyMovement.x = -1*scale;
				keyMove.setTranslation(keyMovement);
			break;
			default: System.out.println("invalid key");
            break;
            
            
            
			}
		}
		
		public void keyReleased(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}	
		/**
		 * set a movement scale (int)
		 * @param scale
		 */
		public void setScale(int scale){
			this.scale = scale;
		}
	}
	
	
	/**
	 * A mouse listener for the main window of this application. This can be
	 * used to process mouse events.
	 */
	public static class SimpleMouseListener implements MouseListener, MouseMotionListener
	{
    	private Vector3f oldMoveVector, newMoveVector, oldWorldZMoveVector, newWorldZMoveVector;
    	float oldX, oldY, oldZ;
   		float newX, newY, newZ;
   		float fixOldX = 0;
   		float fixOldY = 0;
   		float fixNewY = 0;
   		float fixNewX = 0;
    	
		public void mousePressed(MouseEvent e) {

    		//get initial x,y
    		oldX = e.getX()/(adjustToScreenSize/2) - 1; //used to be (float)jframe.getWidth()
    		oldY = 1 - e.getY()/(adjustToScreenSize/2); //used to be (float)jframe.getWidth()
    		oldZ = calculateZ(oldX, oldY);
    		
    	}
    	public void mouseReleased(MouseEvent e) {}
    	public void mouseEntered(MouseEvent e) {}
    	public void mouseExited(MouseEvent e) {}
    	public void mouseClicked(MouseEvent e) {}
		public void mouseDragged(MouseEvent e) {

			oldMoveVector = new Vector3f(fixOldX, oldY, oldZ);
			oldWorldZMoveVector = new Vector3f(oldX, oldY, oldZ);

			//get x,y
    		newX = e.getX()/(adjustToScreenSize/2) - 1; //used to be (float)jframe.getWidth()
    		newY = 1 - e.getY()/(adjustToScreenSize/2); //used to be (float)jframe.getWidth()
    		newZ = calculateZ(newX, newY);
    		
			newMoveVector = new Vector3f(fixNewX, newY, newZ);
			newWorldZMoveVector = new Vector3f(newX, newY, newZ);
			
			//now calculate the Camera movement
			Vector3f moveAxis = new Vector3f(); //moving around this axis
			moveAxis.cross(oldMoveVector, newMoveVector); //TODO identify, if the axis points down or upwards => adjust the angle
			float moveAngle = oldMoveVector.angle(newMoveVector); //moving by this angle
			
			//special Vector including the angle
			AxisAngle4f moveAxisAngle = new AxisAngle4f();
			moveAxisAngle.set(moveAxis, moveAngle); 
			
			//calculate turning axis
			mouseTurn = new Matrix4f();
			mouseTurn.setIdentity();
			mouseTurn.set(moveAxisAngle); //the new Multiplication-Matrix
		
			
			
			//now calculate the World movement
			Vector3f worldMoveAxis = new Vector3f(); //moving around this axis
			worldMoveAxis.x=0;
			worldMoveAxis.y=0;
			worldMoveAxis.z=1;
			float worldMoveAngle = oldWorldZMoveVector.angle(newWorldZMoveVector); //moving by this angle
	
			//adjust world Angle for turning back
			Vector3f worldMoveAxisTemp = new Vector3f();
			worldMoveAxisTemp.cross(oldWorldZMoveVector, newWorldZMoveVector);
			if (worldMoveAxisTemp.z < 0){worldMoveAngle = -worldMoveAngle;}
			
			//special Vector including the angle
			AxisAngle4f worldMoveAxisAngle = new AxisAngle4f();
			worldMoveAxisAngle.set(worldMoveAxis, worldMoveAngle); 

			
			//calculate turning axis
			mouseWorldTurn = new Matrix4f();
			mouseWorldTurn.setIdentity();
			mouseWorldTurn.set(worldMoveAxisAngle); //the new Multiplication-Matrix
			
			
			
			//override the initial starting values
			fixOldX = fixNewX;
			oldX = newX;
			oldY = newY;
			oldZ = newZ;
			
		}
		public void mouseMoved(MouseEvent e) {}
		
		/**
		 * calculates z and adjusts for border conditions
		 * @param x
		 * @param y
		 * @return
		 */
		private float calculateZ(float x, float y){
			float z = 0;
			z = 1-x*x - y*y;
    		if (z > 0){ z = (float) Math.sqrt(z);}
    		else {z = 0;}	
			return z;
		}
	}
	
	/**
	 * The main function opens a 3D rendering window, constructs a simple 3D
	 * scene, and starts a timer task to generate an animation.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{		
		
		sceneManager = new SimpleSceneManager();
		
		Landscape landscape = new Landscape(7, 50,20,10,5);
		// Set up the vertex data and integrate
		VertexData vLandscape = landscape.getVertexData();
		sLandscape = new Shape(vLandscape);
		sceneManager.addShape(sLandscape);
		
		//adjust the camera
		camera = sceneManager.getCamera();

		Vector3f centerOfProjection = new Vector3f(0,0,40);
		Vector3f lookAtPoint = new Vector3f(0,0,0);
		Vector3f upVector = new Vector3f(0,1,0);
		camera.setCameraMatrix(centerOfProjection, lookAtPoint, upVector);
		
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
		renderPanel.getCanvas().addKeyListener(new MyKeyListener());
	    
		   	    	    
	    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    jframe.setVisible(true); // show window
	}

}
