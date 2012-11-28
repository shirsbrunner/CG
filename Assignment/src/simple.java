import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import jrtr.GLRenderPanel;
import jrtr.GraphSceneManager;
import jrtr.Light;
import jrtr.Material;
import jrtr.ObjReader;
import jrtr.RenderContext;
import jrtr.RenderPanel;
import jrtr.SWRenderPanel;
import jrtr.Shape;
import jrtr.ShapeNode;
import jrtr.TransformGroup;
import jrtr.VertexData;

/**
 * Implements a simple application that opens a 3D rendering window and 
 * shows a rotating cube.
 */
public class simple
{	
	static RenderPanel renderPanel;
	static RenderContext renderContext;
	static GraphSceneManager sceneManager;
	static Shape sCube, sTeapot;
	static float angle;
	static TransformGroup worldBase, secondBase;

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
			
			//the shapes are still handled... need to handle the groups as well
			//and propagate Group-changes in GraphSceneManager to the shapes
			
			
			// Update transformation
			Matrix4f t = sCube.getTransformation();
    		Matrix4f rotX = new Matrix4f();
    		rotX.rotX(angle);
    		Matrix4f rotY = new Matrix4f();
    		rotY.rotY(angle);
    		t.mul(rotX);
    		t.mul(rotY);
    		sCube.setTransformation(t);
    		
    		Matrix4f s = sTeapot.getTransformation();
    		Matrix4f tRot = new Matrix4f();
    		tRot.rotY(angle);
    	
    		//tRot.mul(s);
    		s.mul(tRot);
    		
    		sTeapot.setTransformation(s);
    		
    		
    		//create a world matrix and apply to world-group
    		Matrix4f world = new Matrix4f();
    		world.setIdentity();
    		Matrix4f wrotY = new Matrix4f();
    		wrotY.rotY(angle);
    		world.mul(wrotY);
    		worldBase.setTransformationMatrix(world); //not working yet, second base works like a charm
    		
    		//matrix for secondBase-Group: identity, no changes
    		Matrix4f second = new Matrix4f();
    		second.setIdentity();
    		secondBase.setTransformationMatrix(second);
    		
    		
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
		// Make a simple geometric object: a cube
		
		// The vertex positions of the cube
		float v[] = {-1,-1,1, 1,-1,1, 1,1,1, -1,1,1,		// front face
			         -1,-1,-1, -1,-1,1, -1,1,1, -1,1,-1,	// left face
				  	 1,-1,-1,-1,-1,-1, -1,1,-1, 1,1,-1,		// back face
					 1,-1,1, 1,-1,-1, 1,1,-1, 1,1,1,		// right face
					 1,1,1, 1,1,-1, -1,1,-1, -1,1,1,		// top face
					-1,-1,1, -1,-1,-1, 1,-1,-1, 1,-1,1};	// bottom face

		// The vertex colors
		float c[] = {1,0,0, 1,0,0, 1,0,0, 1,0,0,
				     0,1,0, 0,1,0, 0,1,0, 0,1,0,
					 1,0,0, 1,0,0, 1,0,0, 1,0,0,
					 0,1,0, 0,1,0, 0,1,0, 0,1,0,
					 0,0,1, 0,0,1, 0,0,1, 0,0,1,
					 0,0,1, 0,0,1, 0,0,1, 0,0,1};

		float n[] = {0,0,1, 0,0,1, 0,0,1, 0,0,1,
		         	-1,0,0, -1,0,0, -1,0,0, -1,0,0,
			  	    0,0,-1, 0,0,-1, 0,0,-1, 0,0,-1, 
				    1,0,0, 1,0,0, 1,0,0, 1,0,0,
				    0,1,0, 0,1,0, 0,1,0, 0,1,0, 
				    0,-1,0, 0,-1,0, 0,-1,0,  0,-1,0};  
		
		float uv[] = {0,0, 1,0, 1,1, 0,1,
					  0,0, 1,0, 1,1, 0,1,
					  0,0, 1,0, 1,1, 0,1,
					  0,0, 1,0, 1,1, 0,1,
					  0,0, 1,0, 1,1, 0,1,
					  0,0, 1,0, 1,1, 0,1};
		
		// Construct a data structure that stores the vertices, their
		// attributes, and the triangle mesh connectivity
		VertexData vertexData = new VertexData(24);
		vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
		vertexData.addElement(v, VertexData.Semantic.POSITION, 3);
		vertexData.addElement(n, VertexData.Semantic.NORMAL, 3);
		vertexData.addElement(uv, VertexData.Semantic.TEXCOORD, 2);
		
		// The triangles (three vertex indices for each triangle)
		int indices[] = {0,2,3, 0,1,2,			// front face
						 4,6,7, 4,5,6,			// left face
						 8,10,11, 8,9,10,		// back face
						 12,14,15, 12,13,14,	// right face
						 16,18,19, 16,17,18,	// top face
						 20,22,23, 20,21,22};	// bottom face

		vertexData.addIndices(indices);
				
		// Make a scene manager and add the object
		sceneManager = new GraphSceneManager();
		
		/**
		 * used for 4.1 / 4.2 already supports textures
		 */
		//cube
		
		sCube = new Shape(vertexData);
		ShapeNode nCube = new ShapeNode();
		nCube.setShape(sCube);
		
		//addShape(sCube);
		//create Material
		Material cubeMaterial = new Material();
		//set texture in material
		cubeMaterial.loadTexture("textures/plant.jpg");
		//set glossmap
		cubeMaterial.loadGlossmap("textures/Glossmap.jpg");
		//set shaderlinks in material
		cubeMaterial.loadShader("../jrtr/shaders/shPhongColor.vert","../jrtr/shaders/shPhongColor.frag");
		//set shininess
		cubeMaterial.setReflectionCoefficient(0.5f);
		//set specularRflection
		cubeMaterial.setSpecularReflectionCoefficient(0.9f);
		//set PhongExponent
		cubeMaterial.setPhongExponent(5);
		//add material
		sCube.setMaterial(cubeMaterial);
		//move the Thing
		sCube = moveShape(sCube,2,0,0);
		

		
		//Teapot
		ObjReader reader = new ObjReader();
		VertexData vTeapot = reader.read("./Objects/teapot_tex.obj", 2f);
		sTeapot = new Shape(vTeapot);
		ShapeNode nTeapot = new ShapeNode();
		nTeapot.setShape(sTeapot);
		
		Material teapotMaterial = new Material();
		teapotMaterial.loadTexture("textures/tribal.jpg");
		teapotMaterial.loadGlossmap("textures/Glossmap.jpg");
		teapotMaterial.loadShader("../jrtr/shaders/shPhongColor.vert","../jrtr/shaders/shPhongColor.frag");
		teapotMaterial.setReflectionCoefficient(0.6f);
		teapotMaterial.setSpecularReflectionCoefficient(0.9f);
		teapotMaterial.setPhongExponent(50);
		sTeapot.setMaterial(teapotMaterial);
		sTeapot = moveShape(sTeapot, -2, 0, 0);
		
		
		//TODO SH define light-sources
		
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
		worldBase = (TransformGroup) sceneManager.getRoot();
		worldBase.addChild(nTeapot);
		//worldBase.addChild(nCube);
		
		secondBase = new TransformGroup();
		secondBase.addChild(nCube);
		worldBase.addChild(secondBase);
		
		
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
