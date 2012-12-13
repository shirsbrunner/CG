import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import jrtr.GLRenderPanel;
import jrtr.GraphSceneManager;
import jrtr.Light;
import jrtr.LightNode;
import jrtr.Material;
import jrtr.RenderContext;
import jrtr.RenderPanel;
import jrtr.SWRenderPanel;
import jrtr.Shape;
import jrtr.ShapeNode;
import jrtr.TransformGroup;
import buildingBlocks.BoundingSphere;
import buildingBlocks.Cylinder;
import buildingBlocks.IBasicShape;
import buildingBlocks.RotationBody;
import buildingBlocks.Torus;

/**
 * Implements a simple application that opens a 3D rendering window and 
 * shows a rotating cube.
 */
public class Bezier
{	
	static RenderPanel renderPanel;
	static RenderContext renderContext;
	static GraphSceneManager sceneManager;
	//mouse-turning
	static float adjustToScreenSize;
	static JFrame jframe;
	static Matrix4f mouseTurn;
	//animation
	static ShapeNode nBody, nsrotBod; //TODO this doesnt exist anymore
	static float angle;
	static TransformGroup worldBase, bodyToWorld;
	static boolean initialized = false;

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
			adjustToScreenSize = (float) Math.min(jframe.getWidth(), jframe.getHeight());
			Matrix4f 	world = bodyToWorld.getTransformationMatrix(); //was worldbase
						
			if (initialized == false){
				
				initialized = true;
			}			
			
			//world-rotation
			Matrix4f newTranslation = new Matrix4f();
			newTranslation.setIdentity();
			Matrix4f oldTranslation = new Matrix4f(); 
    		oldTranslation = world;
    		
    		if (mouseTurn != null){
    			newTranslation.mul(mouseTurn);
    			mouseTurn.setIdentity();
    		}
    		
    		newTranslation.mul(oldTranslation);
    		bodyToWorld.setTransformationMatrix(newTranslation); //was worldbase
    		  		
    		// Trigger redrawing of the render window
    		renderPanel.getCanvas().repaint(); 
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

		// Make a scene manager and add the object
		sceneManager = new GraphSceneManager();
		
		Vector3f[] appleBase = new Vector3f[7]; 
		Vector3f bPoint = new Vector3f(0,-3,0);
		appleBase[0] = bPoint;
		
		bPoint = new Vector3f(1.5f,-3.8f,0);
		appleBase[1] = bPoint;
		
		bPoint = new Vector3f(2.5f,-2.5f,0);
		appleBase[2] = bPoint;
		
		bPoint = new Vector3f(2.8f,-1,0);
		appleBase[3] = bPoint;
		
		bPoint = new Vector3f(3,1.5f,0);
		appleBase[4] = bPoint;
		
		bPoint = new Vector3f(1.5f,2,0);
		appleBase[5] = bPoint;
		
		bPoint = new Vector3f(0,1,0);
		appleBase[6] = bPoint;
		
		
		IBasicShape rotBod = new RotationBody(appleBase, 10, 20,2);
		Shape srotBod = new Shape(rotBod.getVertexData());
		nsrotBod = new ShapeNode();
		nsrotBod.setShape(srotBod);

		//create Material supports multiple materials now
		Material tubeMaterial = new Material();
		tubeMaterial.loadTexture("textures/plant.jpg");
		tubeMaterial.loadGlossmap("textures/Glossmap.jpg");
		tubeMaterial.loadShader("../jrtr/shaders/shPhongColor.vert","../jrtr/shaders/shPhongColor.frag"); //default for all the elements shPhongColor
		tubeMaterial.setReflectionCoefficient(0.5f);
		tubeMaterial.setSpecularReflectionCoefficient(0.9f);
		tubeMaterial.setPhongExponent(5);
		
		
		//TODO: improve the shPhongTex-Shader
		Material appleMaterial = new Material();
		appleMaterial.loadTexture("textures/apple2.jpg");
		appleMaterial.loadGlossmap("textures/isogloss.jpg");
		appleMaterial.loadShader("../jrtr/shaders/shPhongMTex.vert","../jrtr/shaders/shPhongMTex.frag"); //default for all the elements shPhongColor
		appleMaterial.setReflectionCoefficient(0.5f);
		appleMaterial.setSpecularReflectionCoefficient(0.5f);
		appleMaterial.setPhongExponent(20);
		
		srotBod.setMaterial(appleMaterial);
				
		Light lightRight = new Light();
		sceneManager.addLight(lightRight); //the lights have to be added, otherwise they are missing from time to time..
		//lightRight.setCoordinates(5, 1, 0);
		lightRight.setRadiance(50);
		lightRight.setColor(1, 1, 1);
		
		LightNode nlightR = new LightNode();
		nlightR.setLight(lightRight);
		TransformGroup glightR = new TransformGroup();
		glightR.addChild(nlightR);
		glightR.setTranslation(new Vector3f(-5,1,0));

		Light lightleft = new Light();
		sceneManager.addLight(lightleft);
		//lightleft.setCoordinates(-5,0,0);
		lightleft.setRadiance(50);
		lightleft.setColor(1, 1, 1);
		
		LightNode nlightL = new LightNode();
		nlightL.setLight(lightleft);
		TransformGroup glightL = new TransformGroup();
		glightL.addChild(nlightL);
		glightL.setTranslation(new Vector3f(5,1,0));
		
		
		//compose GraphScene, Groups and so on
		worldBase = (TransformGroup) sceneManager.getRoot();

		
		bodyToWorld = new TransformGroup();
		worldBase.addChild(bodyToWorld); //first one must not be a light (furthermore makes no sense to only have lights...)
		worldBase.addChild(glightR); //TODO add the outer light
		worldBase.addChild(glightL); //TODO LIGHT
		bodyToWorld.addChild(nsrotBod);		


		
		
		// Make a render panel. The init function of the renderPanel
		// (see above) will be called back for initialization.
		renderPanel = new SimpleRenderPanel();
		
		// Make the main window of this application and add the renderer to it
		jframe = new JFrame("simple");
		jframe.setSize(500, 500);
		jframe.setLocationRelativeTo(null); // center of screen
		jframe.getContentPane().add(renderPanel.getCanvas());// put the canvas into a JFrame window

		// Add a mouse listener
		SimpleMouseListener mouse = new SimpleMouseListener();
		renderPanel.getCanvas().addMouseListener(mouse);
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
	
}
