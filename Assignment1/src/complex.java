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
public class complex
{	
	static RenderPanel renderPanel;
	static RenderContext renderContext;
	static SimpleSceneManager sceneManager;
	static Shape sChopter;
	static Shape sRotor;
	static Shape sGround;
	static Shape sObjectOfInterest;
	static Shape sSRotor;
	static float angle;
	static boolean initTrigger = true; 

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
System.out.println("to see this, you need to change the shaders");		
			//small Rotor
			Matrix4f sr1 = sSRotor.getTransformation();
			Matrix4f sRot = new Matrix4f();
			sRot.rotY(-angle*20);
			
			sr1.mul(sRot);
			
			Matrix4f iRot = new Matrix4f();
			iRot.rotZ(-angle);
			iRot.mul(sr1);
			
			sSRotor.setTransformation(iRot);
					
			
			//rotor
			Matrix4f r1 = sRotor.getTransformation();
			Matrix4f rot = new Matrix4f();
			rot.rotZ(angle*5);
			
			r1.mul(rot);
		
			Matrix4f oRot = new Matrix4f();
			oRot.rotZ(-angle);
			oRot.mul(r1);
			sRotor.setTransformation(oRot);
			
			//chopter
			Matrix4f c1 = sChopter.getTransformation();
			Matrix4f oChop = new Matrix4f(); 
			oChop.rotZ(-angle);
			
			oChop.mul(c1);
			sChopter.setTransformation(oChop);
			
	
    		//Object of Interest
    			Matrix4f o1 = sObjectOfInterest.getTransformation();
    			Matrix4f oRotZ = new Matrix4f();
    			Matrix4f oRotY = new Matrix4f();
    			
    			oRotZ.rotZ(-angle*2);
    			oRotY.rotY(angle*2);
    			o1.mul(oRotZ);
    			o1.mul(oRotY);
    			sObjectOfInterest.setTransformation(o1);

 
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
		
		IBasicShape bRotor1 = new Cube(0.2f,4,0.2f,4);
		IBasicShape bRotor2 = new Cube(4,0.2f,0.2f,4);
		
		IBasicShape bRotor = new ShapeCombinator(bRotor1,bRotor2);
		
		IBasicShape bChopterBody = new Cylinder(20, 1, 0.5f, 0.5f, 6);
		IBasicShape bChopterNose = new Cylinder(20, 0.5f, 0.25f, 0.5f, 6);
		bChopterNose.moveY(1);
		
		IBasicShape bAdder2 = new ShapeCombinator(bChopterBody, bChopterNose);
		IBasicShape bChopterTail = new Cylinder(20, 3, 0.125f, 0.125f, 6);
		bChopterTail.moveY(-0.5f);
		bAdder2 = new ShapeCombinator(bAdder2, bChopterTail);
		IBasicShape bChopter = bAdder2;
		bChopter.moveZ(-1);
		
		IBasicShape bGround = new Cube(20,20,0.1f, 1);
		IBasicShape bOofInterest = new Torus(20,20, 1, 0.2f, 9);
		
		IBasicShape bSRotor = new Cube(0.5f, 0.1f, 0.1f, 3);
		
		
		VertexData vChopter = bChopter.getVertexData(); //extract Vertices from shape
		VertexData vRotor = bRotor.getVertexData(); //extract Vertices from shape
		VertexData vGround = bGround.getVertexData();		
		VertexData vSOOI = bOofInterest.getVertexData();
		VertexData vSRotor = bSRotor.getVertexData();
		
		// Make a scene manager and add the objects
		sceneManager = new SimpleSceneManager();
		sChopter = new Shape(vChopter);
		sRotor = new Shape(vRotor);
		sGround = new Shape(vGround);
		sObjectOfInterest = new Shape(vSOOI);
		sSRotor = new Shape(vSRotor);
		
		//Place Objects relative to 0,0,0 and turn
		sChopter = moveShape(sChopter, 0, -4.5f, 0);
		sChopter = turnShape(sChopter, 0, 0, 90);
		sRotor = moveShape(sRotor, 0, -4, 0);
		sSRotor = moveShape(sSRotor, 1.7f, -4.4f, 0);
		sGround = moveShape(sGround, 0,0,-6);
		sObjectOfInterest = moveShape(sObjectOfInterest, 0, 0, -5);
		
		sceneManager.addShape(sChopter);
		sceneManager.addShape(sRotor);
		sceneManager.addShape(sGround);
		sceneManager.addShape(sObjectOfInterest);
		sceneManager.addShape(sSRotor);
		
		// Make a render panel. The init function of the renderPanel
		// (see above) will be called back for initialization.
		renderPanel = new SimpleRenderPanel();
		
		// Make the main window of this application and add the renderer to it
		JFrame jframe = new JFrame("simple");
		jframe.setSize(750, 750);
		jframe.setLocationRelativeTo(null); // center of screen
		jframe.getContentPane().add(renderPanel.getCanvas());// put the canvas into a JFrame window

		// Add a mouse listener
		renderPanel.getCanvas().addMouseListener(new SimpleMouseListener()); //change proposed in forum
	    //jframe.addMouseListener(new SimpleMouseListener()); //original
		   	    	    
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
