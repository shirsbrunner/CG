import jrtr.*;

import javax.swing.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.vecmath.*;

import buildingBlocks.Cylinder;
import buildingBlocks.IBasicShape;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Implements a simple application that opens a 3D rendering window and 
 * shows a rotating cube.
 */
public class Bot
{	
	static RenderPanel renderPanel;
	static RenderContext renderContext;
	static GraphSceneManager sceneManager;
	static Shape sBody, sHead, srArm, srlArm, slArm, sllArm, srLeg, srlLeg, slLeg, sllLeg;
	static float angle;
	static Group worldBase, bodyBase, headBase, rArmBase, rElbowBase, lArmBase, lElbowBase, rLegBase, lLegBase, rKneeBase, lKneeBase;

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
			
			Matrix4f 	world = new Matrix4f(),
						body = bodyBase.getTransformationMatrix(),
						head = headBase.getTransformationMatrix(),
						ruArm = rArmBase.getTransformationMatrix(),
						luArm = lArmBase.getTransformationMatrix(),
						rlArm = rElbowBase.getTransformationMatrix(),
						llArm = lElbowBase.getTransformationMatrix();
						

			
			
			
			Matrix4f rotX = new Matrix4f();
    		
			
			rotX.rotY(angle*2);
    		body.mul(rotX);
    		bodyBase.setTransformationMatrix(body);
    		
    		
			rotX = new Matrix4f();
    		rotX.rotZ(angle*5);
    		ruArm.mul(rotX);
    		
    		//rArmBase.setTransformationMatrix(ruArm);
    		
			
    		//create a world matrix and apply to world-group
    		/*
			world.setIdentity();
    		Matrix4f wrotY = new Matrix4f();
    		wrotY.rotY(angle);
    		world.mul(wrotY);
    		worldBase.setTransformationMatrix(world); //not working yet, second base works like a charm
    		*/
    		

			/*
    		body.setIdentity();
    		head.setIdentity();
    		ruArm.setIdentity();
    		*/
    		
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
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{		

		// Make a scene manager and add the object
		sceneManager = new GraphSceneManager();
		
		/**
		 * used for 4.1 / 4.2 already supports textures
		 */
		//Tube
		int complexity = 16;
		float 	bodyDiam = 1, 
				bodyHeight = 2,
				neckHeight = 0.5f,
				armDiam = 0.25f,
				uArmLength = 1f,
				lArmLength = 1.5f,
				headDiam = 0.5f,
				headHeight = 1,
				shoulderWidth = armDiam*2,
				elbowSize = 0.1f, 
				legDiam = 0.2f,
				uLegLength = 1f,
				lLegLength = 1.5f;
		
		IBasicShape bodyCylinder = new Cylinder(complexity, bodyHeight, bodyDiam, bodyDiam, 2);
		
		sBody = new Shape(bodyCylinder.getVertexData());
		ShapeNode nBody = new ShapeNode();
		nBody.setShape(sBody);
		
		IBasicShape headCylinder = new Cylinder(complexity, headHeight, headDiam, headDiam, 2);		
		sHead = new Shape(headCylinder.getVertexData());
		ShapeNode nHead = new ShapeNode();
		nHead.setShape(sHead);
		
		IBasicShape armCylinder = new Cylinder(complexity ,uArmLength, armDiam, armDiam, 2);
		srArm = new Shape(armCylinder.getVertexData());
		ShapeNode nrArm = new ShapeNode();
		nrArm.setShape(srArm);
		
		slArm = new Shape(armCylinder.getVertexData());
		ShapeNode nlArm = new ShapeNode();
		nlArm.setShape(slArm);
		
		IBasicShape lowACylinder = new Cylinder(complexity, lArmLength, armDiam, armDiam, 2);
		srlArm = new Shape(lowACylinder.getVertexData());
		ShapeNode nrlArm = new ShapeNode();
		nrlArm.setShape(srlArm);
		
		sllArm = new Shape(lowACylinder.getVertexData());
		ShapeNode nllArm = new ShapeNode();
		nllArm.setShape(sllArm);

		IBasicShape legCylinder = new Cylinder(complexity ,uLegLength, legDiam, legDiam, 2);
		srLeg = new Shape(legCylinder.getVertexData());
		ShapeNode nrLeg = new ShapeNode();
		nrLeg.setShape(srLeg);
		
		slLeg = new Shape(legCylinder.getVertexData());
		ShapeNode nlLeg = new ShapeNode();
		nlLeg.setShape(slLeg);
		
		IBasicShape lowLCylinder = new Cylinder(complexity, lLegLength, legDiam, legDiam, 2);
		srlLeg = new Shape(lowLCylinder.getVertexData());
		ShapeNode nrlLeg = new ShapeNode();
		nrlLeg.setShape(srlLeg);
		
		sllLeg = new Shape(lowLCylinder.getVertexData());
		ShapeNode nllLeg = new ShapeNode();
		nllLeg.setShape(sllLeg);

		//create Material
		Material tubeMaterial = new Material();
		tubeMaterial.loadTexture("textures/plant.jpg");
		tubeMaterial.loadGlossmap("textures/Glossmap.jpg");
		tubeMaterial.loadShader("../jrtr/shaders/shPhongColor.vert","../jrtr/shaders/shPhongColor.frag");
		tubeMaterial.setReflectionCoefficient(0.5f);
		tubeMaterial.setSpecularReflectionCoefficient(0.9f);
		tubeMaterial.setPhongExponent(5);
		sBody.setMaterial(tubeMaterial);
		sHead.setMaterial(tubeMaterial);
		srArm.setMaterial(tubeMaterial);
		srlArm.setMaterial(tubeMaterial);
		slArm.setMaterial(tubeMaterial);
		sllArm.setMaterial(tubeMaterial);
		srLeg.setMaterial(tubeMaterial);
		srlLeg.setMaterial(tubeMaterial);
		slLeg.setMaterial(tubeMaterial);
		sllLeg.setMaterial(tubeMaterial);
		
		Light lightRight = new Light();
		sceneManager.addLight(lightRight);
		lightRight.setCoordinates(5, 1, 0);
		lightRight.setRadiance(50);
		lightRight.setColor(1, 0, 0);


		Light lightleft = new Light();
		sceneManager.addLight(lightleft);
		lightleft.setCoordinates(-5,0,0);
		lightleft.setRadiance(50);
		lightleft.setColor(1, 1, 0);
		
		
		//compose GraphScene, Groups and so on
		worldBase = (Group) sceneManager.getRoot();
		
		bodyBase = new Group();
		bodyBase.setTranslation(new Vector3f(2f,0,0));
		bodyBase.addChild(nBody);
		
		headBase = new Group();
		headBase.setTranslation(new Vector3f(0,(bodyHeight/2+headHeight/2)+neckHeight, 0));
		headBase.addChild(nHead);
		
		bodyBase.addChild(headBase);
		
		rArmBase = new Group();
		rArmBase.addChild(nrArm);
		rArmBase.setTranslation(new Vector3f(bodyDiam+shoulderWidth,0,0));
		
		rElbowBase = new Group();
		rElbowBase.addChild(nrlArm);
		rElbowBase.setTranslation(new Vector3f(0,-(uArmLength/2+lArmLength/2+elbowSize),0));
		rArmBase.addChild(rElbowBase);
		
		lArmBase = new Group();
		lArmBase.addChild(nlArm);
		lArmBase.setTranslation(new Vector3f(-(bodyDiam+shoulderWidth),0,0));
		
		lElbowBase = new Group();
		lElbowBase.addChild(nllArm);
		lElbowBase.setTranslation(new Vector3f(0,-(uArmLength/2+lArmLength/2+elbowSize),0));
		lArmBase.addChild(lElbowBase);
		
		bodyBase.addChild(rArmBase);
		bodyBase.addChild(lArmBase);
		
		rLegBase = new Group();
		rLegBase.addChild(nrLeg);
		rLegBase.setTranslation(new Vector3f(bodyDiam-shoulderWidth,-(bodyHeight/2+uLegLength/2+elbowSize),0));
		
		rKneeBase = new Group();
		rKneeBase.addChild(nrlLeg);
		rKneeBase.setTranslation(new Vector3f(0,-(uLegLength/2+lLegLength/2+elbowSize),0));
		rLegBase.addChild(rKneeBase);
		
		lLegBase = new Group();
		lLegBase.addChild(nlLeg);
		lLegBase.setTranslation(new Vector3f(-(bodyDiam-shoulderWidth),-(bodyHeight/2+uLegLength/2+elbowSize),0));
		
		lKneeBase = new Group();
		lKneeBase.addChild(nllLeg);
		lKneeBase.setTranslation(new Vector3f(0,-(uLegLength/2+lLegLength/2+elbowSize),0));
		lLegBase.addChild(lKneeBase);
		
		bodyBase.addChild(rLegBase);
		bodyBase.addChild(lLegBase);
		
		worldBase.addChild(bodyBase);
		
		
		
		// Make a render panel. The init function of the renderPanel
		// (see above) will be called back for initialization.
		renderPanel = new SimpleRenderPanel();
		
		// Make the main window of this application and add the renderer to it
		JFrame jframe = new JFrame("simple");
		jframe.setSize(500, 500);
		jframe.setLocationRelativeTo(null); // center of screen
		jframe.getContentPane().add(renderPanel.getCanvas());// put the canvas into a JFrame window

		// Add a mouse listener
	    jframe.addMouseListener(new SimpleMouseListener());
		   	    	    
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
