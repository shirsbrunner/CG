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
	static ShapeNode nBody, nHead, nNose, nrArm, nrlArm, nlArm, nllArm, nrLeg, nrlLeg, nlLeg, nllLeg;
	static float angle;
	static Group worldBase, bodyBase, headBase, rArmBase, rElbowBase, lArmBase, lElbowBase, rLegBase, lLegBase, rKneeBase, lKneeBase
		,lShoulderBase, rShoulderBase, luArmBase, ruArmBase, lHipBase, rHipBase, luLegBase, ruLegBase, bodyToWorld;
	static float rLegCounter, lLegCounter;
	static int rHipturn = 1, lHipturn = 1;
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
			Matrix4f 	world = new Matrix4f(),
						body = bodyBase.getTransformationMatrix(),
						btw = bodyToWorld.getTransformationMatrix(),
						rArm = rArmBase.getTransformationMatrix(),
						lArm = lArmBase.getTransformationMatrix(),
						ruArm = ruArmBase.getTransformationMatrix(),
						luArm = luArmBase.getTransformationMatrix(),
						lLeg = lLegBase.getTransformationMatrix(),
						rLeg = rLegBase.getTransformationMatrix(), 
						luLeg = luLegBase.getTransformationMatrix(),
						ruLeg = ruLegBase.getTransformationMatrix();
			
			int stepsize = 70;
			int leanangle = 10;
			float legspeed = 2.5f;
			float limbMove = angle*legspeed;
			
			if (initialized == false){
				//lean forward
				Matrix4f rotation = new Matrix4f();
				rotation.rotX((float) Math.toRadians(-leanangle));
				body.mul(rotation);
				
				int initialR = stepsize;
				int initialL = 0;
				
				//legs & arms in the right position
				rLegCounter = (float) Math.toRadians(initialR);
				rotation = new Matrix4f();
				rotation.rotX((float) Math.toRadians(initialR));
				rLeg.mul(rotation); //move leg back
				luArm.mul(rotation); //move lowerarm back
				ruArm.mul(rotation);
				rotation.invert();
				ruLeg.mul(rotation); //lower/upper-part is inverted
				lArm.mul(rotation);
				
				lLegCounter = (float) Math.toRadians(initialL);
				rotation = new Matrix4f();
				rotation.rotX((float) Math.toRadians(initialL));
				lLeg.mul(rotation);

				rotation.invert();
				luLeg.mul(rotation);
				rArm.mul(rotation);
				
				initialized = true;
			}			
			
			Matrix4f rotation = new Matrix4f();
			rotation.rotY(angle);
    		btw.mul(rotation);  		
			
    		//same principles as on the upper part apply
    		Matrix4f mruLeg = new Matrix4f();
    		mruLeg.rotX(limbMove*rHipturn);
    		rLeg.mul(mruLeg);
    		mruLeg.invert();
    		ruLeg.mul(mruLeg);
    		lArm.mul(mruLeg);
    		
    		Matrix4f mluLeg = new Matrix4f();
    		mluLeg.rotX(limbMove*lHipturn);
    		lLeg.mul(mluLeg);
    		mluLeg.invert();
    		luLeg.mul(mluLeg);
    		rArm.mul(mluLeg);
    		
    		//counts and counts back
    		if (rLegCounter > Math.toRadians(stepsize)){ 
    			rLegCounter = (float) Math.toRadians(0);
    			rHipturn *= -1;}
    		else {rLegCounter += angle*legspeed;}
    		
    		if (lLegCounter > Math.toRadians(stepsize)){ 
    			lLegCounter = (float) Math.toRadians(0);
    			lHipturn *= -1;}
    		else {lLegCounter += angle*legspeed;}
    		
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
		float 	bodyDiam = 0.5f, 
				bodyHeight = 2,
				neckHeight = 0.5f,
				armDiam = 0.25f,
				uArmLength = 1f,
				lArmLength = 1.5f,
				headDiam = 0.5f,
				headHeight = 1,
				shoulderWidth = armDiam,
				elbowSize = 0.1f, 
				legDiam = 0.2f,
				uLegLength = 1f,
				lLegLength = 1.5f;
		
		Shape sBody, sHead, srArm, srlArm, slArm, sllArm, srLeg, srlLeg, slLeg, sllLeg;
		
		IBasicShape bodyCylinder = new Cylinder(complexity, bodyHeight, bodyDiam, bodyDiam, 2);
		((Cylinder) bodyCylinder).moveCenterY(bodyHeight/2);
		IBasicShape headCylinder = new Cylinder(complexity, headHeight, headDiam, headDiam, 2);
		((Cylinder) headCylinder).moveCenterY(headHeight/2);
		IBasicShape armCylinder = new Cylinder(complexity ,uArmLength, armDiam, armDiam, 2);
		((Cylinder) armCylinder).moveCenterY(uArmLength/2);
		IBasicShape lowACylinder = new Cylinder(complexity, lArmLength, armDiam, armDiam, 2);
		((Cylinder) lowACylinder).moveCenterY(lArmLength/2);
		IBasicShape legCylinder = new Cylinder(complexity ,uLegLength, legDiam, legDiam, 2);
		((Cylinder) legCylinder).moveCenterY(uLegLength/2);
		IBasicShape lowLCylinder = new Cylinder(complexity, lLegLength, legDiam, legDiam, 2);
		((Cylinder) lowLCylinder).moveCenterY(lLegLength/2);
		
		sBody = new Shape(bodyCylinder.getVertexData());
		nBody = new ShapeNode();
		nBody.setShape(sBody);
		
		sHead = new Shape(headCylinder.getVertexData());
		nHead = new ShapeNode();
		nHead.setShape(sHead);

		
		srArm = new Shape(armCylinder.getVertexData());
		nrArm = new ShapeNode();
		nrArm.setShape(srArm);
		
		slArm = new Shape(armCylinder.getVertexData());
		nlArm = new ShapeNode();
		nlArm.setShape(slArm);
		
		srlArm = new Shape(lowACylinder.getVertexData());
		nrlArm = new ShapeNode();
		nrlArm.setShape(srlArm);
		
		sllArm = new Shape(lowACylinder.getVertexData());
		nllArm = new ShapeNode();
		nllArm.setShape(sllArm);

		srLeg = new Shape(legCylinder.getVertexData());
		nrLeg = new ShapeNode();
		nrLeg.setShape(srLeg);
		
		slLeg = new Shape(legCylinder.getVertexData());
		nlLeg = new ShapeNode();
		nlLeg.setShape(slLeg);
		
		srlLeg = new Shape(lowLCylinder.getVertexData());
		nrlLeg = new ShapeNode();
		nrlLeg.setShape(srlLeg);
		
		sllLeg = new Shape(lowLCylinder.getVertexData());
		nllLeg = new ShapeNode();
		nllLeg.setShape(sllLeg);

		//create Material
		Material tubeMaterial = new Material();
		tubeMaterial.loadTexture("textures/plant.jpg");
		tubeMaterial.loadGlossmap("textures/Glossmap.jpg");
		tubeMaterial.loadShader("../jrtr/shaders/shToon.vert","../jrtr/shaders/shToon.frag");
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
		lightRight.setRadiance(100);
		lightRight.setColor(1, 0, 0);


		Light lightleft = new Light();
		sceneManager.addLight(lightleft);
		lightleft.setCoordinates(-5,0,0);
		lightleft.setRadiance(100);
		lightleft.setColor(1, 1, 0);
		
		
		//compose GraphScene, Groups and so on
		worldBase = (Group) sceneManager.getRoot();
		
		bodyToWorld = new Group();
		worldBase.addChild(bodyToWorld);
		
		bodyBase = new Group();
		bodyToWorld.addChild(bodyBase);
		bodyBase.setTranslation(new Vector3f(2f,0,0));
		bodyBase.addChild(nBody);
		
		headBase = new Group();
		headBase.setTranslation(new Vector3f(0,headHeight+neckHeight, 0));
		headBase.addChild(nHead);
		
		bodyBase.addChild(headBase);
		
		//right arm
		rShoulderBase = new Group();
		bodyBase.addChild(rShoulderBase);
		rShoulderBase.setTranslation(new Vector3f(bodyDiam+shoulderWidth,0,0));
		
		rArmBase = new Group();
		rShoulderBase.addChild(rArmBase);
		rArmBase.addChild(nrArm);
		rElbowBase = new Group();
		rArmBase.addChild(rElbowBase);
		ruArmBase = new Group();
		rElbowBase.addChild(ruArmBase);
		ruArmBase.addChild(nrlArm);
		rElbowBase.setTranslation(new Vector3f(0,-(uArmLength+elbowSize),0));

		//left arm
		lShoulderBase = new Group();
		bodyBase.addChild(lShoulderBase);
		lShoulderBase.setTranslation(new Vector3f(-(bodyDiam+shoulderWidth),0,0));
		
		lArmBase = new Group();
		lShoulderBase.addChild(lArmBase);
		lArmBase.addChild(nlArm);
		lElbowBase = new Group();
		lArmBase.addChild(lElbowBase);
		luArmBase = new Group();
		lElbowBase.addChild(luArmBase);
		luArmBase.addChild(nllArm);
		lElbowBase.setTranslation(new Vector3f(0,-(uArmLength+elbowSize),0));

		//right leg
		rHipBase = new Group();
		bodyBase.addChild(rHipBase);
		rHipBase.setTranslation(new Vector3f(bodyDiam-shoulderWidth,-(bodyHeight+elbowSize),0));
		
		rLegBase = new Group();
		rHipBase.addChild(rLegBase);
		rLegBase.addChild(nrLeg);
		

		rKneeBase = new Group();
		rLegBase.addChild(rKneeBase);
		rKneeBase.setTranslation(new Vector3f(0,-(uLegLength+elbowSize),0));
		ruLegBase = new Group();
		rKneeBase.addChild(ruLegBase);
		ruLegBase.addChild(nrlLeg);
		
		

		//left leg
		lHipBase = new Group();
		bodyBase.addChild(lHipBase);
		lHipBase.setTranslation(new Vector3f(-(bodyDiam-shoulderWidth),-(bodyHeight+elbowSize),0));
		
		lLegBase = new Group();
		lHipBase.addChild(lLegBase);
		lLegBase.addChild(nlLeg);
		
		lKneeBase = new Group();
		lLegBase.addChild(lKneeBase);
		lKneeBase.setTranslation(new Vector3f(0,-(uLegLength+elbowSize),0));
		luLegBase = new Group();
		lKneeBase.addChild(luLegBase);
		luLegBase.addChild(nllLeg);

		

		


		
		
		
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
