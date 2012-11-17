package jrtr;

import jrtr.RenderContext;
import jrtr.VertexData.VertexElement;

import java.awt.Color;
import java.awt.image.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.SingularMatrixException;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;


/**
 * A skeleton for a software renderer. It works in combination with
 * {@link SWRenderPanel}, which displays the output image. In project 3 
 * you will implement your own rasterizer in this class.
 * <p>
 * To use the software renderer, you will simply replace {@link GLRenderPanel} 
 * with {@link SWRenderPanel} in the user application.
 */
public class SWRenderContext implements RenderContext {

	private SceneManagerInterface sceneManager;
	private BufferedImage colorBuffer;
	private RenderItem renderItem;
	private int height, width;
	private Matrix4f viewportMatrix;
	

	private float[][] zBuffer;
	
	private Color3f[] colors;
	//private Vector4f[] normals;
	private Vector2f[] textures;
	private Point4f[] edges;
	private int[] indices;
	private BufferedImage texture;
		
	public void setSceneManager(SceneManagerInterface sceneManager)
	{
		this.sceneManager = sceneManager;
	}
	
	/**
	 * This is called by the SWRenderPanel to render the scene to the 
	 * software frame buffer.
	 */
	public void display()
	{
		if(sceneManager == null) return;
		
		beginFrame();
	
		SceneManagerIterator iterator = sceneManager.iterator();	
		while(iterator.hasNext())
		{
			draw(iterator.next());
		}		
		
		endFrame();
	}

	/**
	 * This is called by the {@link SWJPanel} to obtain the color buffer that
	 * will be displayed.
	 */
	public BufferedImage getColorBuffer()
	{
		return colorBuffer;
	}
	
	/**
	 * Set a new viewport size. The render context will also need to store
	 * a viewport matrix, which you need to reset here. 
	 */
	public void setViewportSize(int width, int height)
	{
		colorBuffer = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		
		//ViewportMatrix
		this.height = height;
		this.width = width;
		viewportMatrix = new Matrix4f();
		
		//according to slide 55, chap 3, Projection

		
		viewportMatrix.m00 = width/2;
		viewportMatrix.m03 = (width-1)/2;
		viewportMatrix.m11 = (height/2);
		viewportMatrix.m13 = (height-1)/2;
		viewportMatrix.m22 = 0.5f;
		viewportMatrix.m23 = 0.5f;
		viewportMatrix.m33 = 1;

	}
		
	/**
	 * Clear the framebuffer here.
	 */
	private void beginFrame()
	{
	}
	
	private void endFrame()
	{		
	}
	
	/**
	 * The main rendering method. You will need to implement this to draw
	 * 3D objects. This method is called once per shape (or RenderItem = Shape & Transformation)
	 */
	private void draw(RenderItem renderItem){
		
		this.renderItem = renderItem;
		
		//renderItem has a Transformation & a shape - Transformation is maybe not used??
		//Matrix4f rITransformation = new Matrix4f();
		//rITransformation = renderItem.getT();
		Shape tempShape = renderItem.getShape();
		//shape has a Transformation too, this should be the standard-Transformation of the shape
		Matrix4f shapeTransformation = new Matrix4f(); //the transformation of the shape, used!!!!
		shapeTransformation = tempShape.getTransformation();
		
		//a shape consists of Vertex Data
		VertexData shapeVertexData = tempShape.getVertexData();
		
		//TODO switch implementations
		
		//3.1: give back the corners
		//calculateProjection31(shapeVertexData, shapeTransformation);
		
		//3.2: including edge-functions and z buffering
		//calculateProjection32(shapeVertexData, shapeTransformation);
		
		//3.3: textures bilinearity or nearest neighbor needs to be adjusted below!!
		calculateProjection33(shapeVertexData, shapeTransformation, tempShape);
		
	}
	
	/**
	 * delivers the Screen representation for 3_3
	 * @param shapeVertexData
	 * @param shapeTransformation
	 */
	private void calculateProjection33(VertexData shapeVertexData, Matrix4f shapeTransformation, Shape tempShape){
			//reset the screen-buffer otherwise turning is a bit of a hassle...
			colorBuffer = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
			//create zBuffer
			this.zBuffer = new float[width][height];
			//initialize zBuffer, set all fields to max
			for (int i = 0; i<width; i++){
				for (int j = 0; j < height; j++){
					zBuffer[i][j] = Float.MAX_VALUE;
				}
			}
			
			calculateEdges33(shapeVertexData, shapeTransformation, tempShape); //fills edge-lists, color-lists and so on & paints the triangles
	}

	private void calculateEdges33(VertexData shapeVertexData, Matrix4f shapeTransformation, Shape tempShape) {
		
		Material material = new Material();
		tempShape.setMaterial(material);
		
		try { //TODO switch pattern here
			//tempShape.getMaterial().getTexture().load("texture/texture.jpg"); //more complex pattern
			//tempShape.getMaterial().getTexture().load("texture/testpattern.jpg"); //pattern with up/left/down marked
			tempShape.getMaterial().getTexture().load("texture/bilinearitytest.jpg"); //two-color-pattern
		} catch (IOException e) {
			System.out.println("cannot read file");
			System.exit(0);
		}
		
		this.texture = tempShape.getMaterial().getTexture().texture;
		
		float[] colors = null;
		float[] vertices = null;
		float[] textureCoord = null;
		
		this.edges = new Point4f[3]; //only handle 3 Points at a time
		this.colors = new Color3f[3];
		this.textures = new Vector2f[3];
		
		//the indices
		this.indices = shapeVertexData.getIndices();
		
		//for the transformation
		Matrix4f mCamera;
		Matrix4f mProjection;
		
		mCamera = sceneManager.getCamera().getCameraMatrix();
		mProjection = sceneManager.getFrustum().getProjectionMatrix();

		//get the content
		for(VertexElement element : shapeVertexData.getElements()) {
			if(element.getSemantic() == VertexData.Semantic.POSITION)
				vertices = element.getData();
			if(element.getSemantic() == VertexData.Semantic.COLOR)
				colors = element.getData();
			if(element.getSemantic() == VertexData.Semantic.TEXCOORD)
				textureCoord = element.getData();
				
		}
		
		//this is the hint from the assignment-sheet, edges are saved in a vector-array though
		//read out the
		int k = 0;
		for(int j = 0; j < indices.length; j++){
			int i = indices[j];

			Point4f tempPoint = new Point4f(vertices[i*3],vertices[(i*3)+1],vertices[(i*3)+2], 1);

			shapeTransformation.transform(tempPoint);
			mCamera.transform(tempPoint);
			mProjection.transform(tempPoint);
			
			viewportMatrix.transform(tempPoint);
			this.edges[k] = new Point4f(tempPoint);
			
			Color3f tempColor = new Color3f(colors[i*3], colors[i*3+1], colors[i*3+2]);
			this.colors[k]=tempColor;
			
			//everything should have a texture, if textures have to be shown
			Vector2f tempTexture = new Vector2f(textureCoord[(i * 2)], textureCoord[(i * 2) + 1]);
			this.textures[k] = tempTexture;
			
			k++;
			if (k == 3){ //if three points: paint!!!
				rasterize();

				//reset k
				k = 0;
			}
		}
		
	}

	/**
	 * Delivers the Screen representation for 3_2
	 * @param shapeVertexData
	 * @param shapeTransformation
	 */
	
	private void calculateProjection32(VertexData shapeVertexData, Matrix4f shapeTransformation){
		//reset the screen-buffer otherwise turning is a bit of a hassle...
		colorBuffer = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		//create zBuffer
		this.zBuffer = new float[width][height];
		//initialize zBuffer, set all fields to max
		for (int i = 0; i<width; i++){
			for (int j = 0; j < height; j++){
				zBuffer[i][j] = Float.MAX_VALUE;
			}
		}
		
		calculateEdges32(shapeVertexData, shapeTransformation); //fills edge-lists, color-lists and so on & paints the triangles
	}				

	/**
	 * basically fills arrays and transforms dots
	 * @param shapeVertexData
	 * @param shapeTransformation
	 */
	private void calculateEdges32(VertexData shapeVertexData, Matrix4f shapeTransformation){
		
		float[] colors = null;
		float[] vertices = null;
		
		this.edges = new Point4f[3]; //only handle 3 Points at a time
		this.colors = new Color3f[3];
		
		//the indices
		this.indices = shapeVertexData.getIndices();
		
		//for the transformation
		Matrix4f mCamera;
		Matrix4f mProjection;
		
		mCamera = sceneManager.getCamera().getCameraMatrix();
		mProjection = sceneManager.getFrustum().getProjectionMatrix();

		//get the content
		for(VertexElement element : shapeVertexData.getElements()) {
			if(element.getSemantic() == VertexData.Semantic.POSITION)
				vertices = element.getData();
			if(element.getSemantic() == VertexData.Semantic.COLOR)
				colors = element.getData();
		}
		
		//this is the hint from the assignment-sheet, edges are saved in a vector-array though
		//read out the
		int k = 0;
		for(int j = 0; j < indices.length; j++){
			int i = indices[j];

			Point4f tempPoint = new Point4f(vertices[i*3],vertices[(i*3)+1],vertices[(i*3)+2], 1);

			shapeTransformation.transform(tempPoint);
			mCamera.transform(tempPoint);
			mProjection.transform(tempPoint);
			
			viewportMatrix.transform(tempPoint);
			this.edges[k] = new Point4f(tempPoint);
			
			Color3f tempColor = new Color3f(colors[i*3], colors[i*3+1], colors[i*3+2]);
			this.colors[k]=tempColor;
			
			k++;
			if (k == 3){ //if three points: paint!!!
				rasterize();

				//reset k
				k = 0;
			}
		}
	}
	
	/**
	 * this calculates the edge-functions and draws. 
	 */
	private void rasterize(){

		//get the dots
		Point4f a = this.edges[0];
		Point4f b = this.edges[1];
		Point4f c = this.edges[2];
		
		//set coefficient-matrix 3x3
		Matrix3f coeff = new Matrix3f();
		coeff.setM00(a.x);
		coeff.setM01(a.y);
		coeff.setM02(a.w);
		
		coeff.setM10(b.x);
		coeff.setM11(b.y);
		coeff.setM12(b.w);
		
		coeff.setM20(c.x);
		coeff.setM21(c.y);
		coeff.setM22(c.w);
			
		Color3f aColor = this.colors[0];
		Color3f bColor = this.colors[1];
		Color3f cColor = this.colors[2];
		
		int lazyColor = calculateColor(aColor);
		
		//invert and check for singularity, if not possible to invert 
		boolean singularMatrix = false;
		boolean backface = false; 
		
		float coeffDet = coeff.determinant();
		if (coeffDet <= 0){ //is a backface if coeff < 0 or sideview if = 0;
			backface = true;
		}
		
		try{ coeff.invert();} //this throws a singularmatrixexception if coeff is singular
		catch (SingularMatrixException e){
			singularMatrix = true; //if singular we don't have to paint anything (04 Rasterization, Slide 29)
			System.out.println("singular");
		}
		
		//only paint
		if (!singularMatrix && !backface){ //remember, coeff is already inverted
			//calculate a bounding box
			int xLeft = (int) Math.min(Math.min(a.x/a.w, b.x/b.w), c.x/c.w);
			int xRight = (int) Math.ceil(Math.max(Math.max(a.x/a.w, b.x/b.w), c.x/c.w));
			int yTop = (int) Math.ceil(Math.max(Math.max(a.y/a.w, b.y/b.w), c.y/c.w));
			int yBottom = (int) Math.min(Math.min(a.y/a.w, b.y/b.w), c.y/c.w);
			
			//don't check pixels outside of the box
			if (xLeft<0){xLeft = 0;}
			if (xRight>width){xRight = width;}
			if (yTop>height){yTop = height;}
			if (yBottom<0){yBottom = 0;}

			//for every pixel in the bounding box, decide whether to paint or not
			for (int xCoord = xLeft; xCoord < xRight; xCoord++) {
				for (int yCoord = yTop; yCoord > yBottom; yCoord--) {
						
					//already adjusted, w = 1
					float alpha = coeff.m00*xCoord + coeff.m10*yCoord + coeff.m20;
					float beta = coeff.m01*xCoord + coeff.m11*yCoord + coeff.m21;
					float gamma = coeff.m02*xCoord + coeff.m12*yCoord + coeff.m22;
					
					if (alpha > 0 && beta > 0 && gamma > 0){ //inside the triangle: paint
						float zBufferTest = calculateDepth(coeff, xCoord, yCoord); //calculate the z-Value
					
						if(this.zBuffer[xCoord][height-yCoord] > zBufferTest){
							this.zBuffer[xCoord][height-yCoord] = zBufferTest;
							
							if(this.texture != null){//TODO Switch here between bilinear & nearest neighbor
								//int texColor = nearestNeighborInterpolation(xCoord,yCoord, coeff);
								int texColor = bilinearInterpolation(xCoord,yCoord, coeff);
								colorBuffer.setRGB(xCoord, height-yCoord, texColor);

							}
							else{//if there is no texture
								int complexColor = calculateComplexColor(aColor, bColor, cColor, xCoord, yCoord, coeff);
								colorBuffer.setRGB(xCoord , height-yCoord , complexColor);
							}
							
						}
					}
				}
			}
		}
	}

	/**
	 * bilinear interpolation
	 * @param xCoord
	 * @param yCoord
	 * @param coeff
	 * @return
	 */
	private int bilinearInterpolation(int xCoord, int yCoord, Matrix3f coeff) {

		float alpha = coeff.m00*xCoord + coeff.m10*yCoord + coeff.m20;
		float beta = coeff.m01*xCoord + coeff.m11*yCoord + coeff.m21;
		float gamma = coeff.m02*xCoord + coeff.m12*yCoord + coeff.m22;
		
		int sizeX = this.texture.getWidth();
		int sizeY = this.texture.getHeight();
		
		Vector2f texCoordA = this.textures[0];
		Vector2f texCoordB = this.textures[1];
		Vector2f texCoordC = this.textures[2];
		
		//could reuse the color-calculator as well, same thing...
		float u = -1 * (alpha * texCoordA.getX() + beta * texCoordB.getX() + gamma * texCoordC.getX())/calculateDepth(coeff, xCoord, yCoord);
		float v = -1 * (alpha * texCoordA.getY() + beta * texCoordB.getY() + gamma * texCoordC.getY())/calculateDepth(coeff, xCoord, yCoord);
				
		float xPos = (sizeX * u);
		float yPos = (sizeY * v);

		   int uu = (int) Math.floor(xPos);
		   int vv = (int) Math.floor(yPos);
		   float u_ratio = xPos - uu;
		   float v_ratio = yPos - vv;
		   float u_opposite = 1 - u_ratio;
		   float v_opposite = 1 - v_ratio;

		   int uw = uu+1;
		   int vw = vv+1;
		   
		   //for border conditions, doesn't work though...
		   if (uw > texture.getWidth()){uw = texture.getWidth();}
		   if (vw > texture.getHeight()){vw = texture.getHeight();}
		   
		   try{
			   //if Abfrage für Border-condition
			   Color topLeft = new Color(texture.getRGB(uu, sizeY-vv));//xPos, sizeY-yPos)); //invert texture
			   Color topRight = new Color(texture.getRGB(uw, sizeY-vv));//xPos+1, sizeY-yPos));
			   Color bottomLeft = new Color(texture.getRGB(uu, sizeY-vw));//;xPos, sizeY-yPos+1));
			   Color bottomRight = new Color(texture.getRGB(uw, sizeY-vw));//xPos+1, sizeY-yPos+1));		
		   
		   
			   float red = (topLeft.getRed() * u_opposite  + topRight.getRed()   * u_ratio) * v_opposite + (bottomLeft.getRed() * u_opposite  + bottomRight.getRed() * u_ratio) * v_ratio;
			   float blue = (topLeft.getBlue() * u_opposite  + topRight.getBlue()   * u_ratio) * v_opposite + (bottomLeft.getBlue() * u_opposite  + bottomRight.getBlue() * u_ratio) * v_ratio;
			   float green = (topLeft.getGreen() * u_opposite  + topRight.getGreen()   * u_ratio) * v_opposite + (bottomLeft.getGreen() * u_opposite  + bottomRight.getGreen() * u_ratio) * v_ratio;
	
			   Color color = new Color((float) Math.floor(red)/255f, (float) Math.floor(green)/255f, (float) Math.floor(blue)/255f);
			   return color.getRGB();
		   } 
		   catch(ArrayIndexOutOfBoundsException e) {
			   return nearestNeighborInterpolation(xCoord,yCoord,coeff);
		   }
	}

	/**
	 * pick the nearest neighbor and show it :)
	 * @param xCoord
	 * @param yCoord
	 * @param coeff
	 * @return
	 */
	private int nearestNeighborInterpolation(int xCoord, int yCoord, Matrix3f coeff) {
		
		float alpha = coeff.m00*xCoord + coeff.m10*yCoord + coeff.m20;
		float beta = coeff.m01*xCoord + coeff.m11*yCoord + coeff.m21;
		float gamma = coeff.m02*xCoord + coeff.m12*yCoord + coeff.m22;
		
		int sizeX = this.texture.getWidth();
		int sizeY = this.texture.getHeight();
		
		Vector2f texCoordA = this.textures[0];
		Vector2f texCoordB = this.textures[1];
		Vector2f texCoordC = this.textures[2];
		
		//could reuse the color-calculator as well, same thing...
		float xx = -1 * (alpha * texCoordA.getX() + beta * texCoordB.getX() + gamma * texCoordC.getX())/calculateDepth(coeff, xCoord, yCoord);
		float yy = -1 * (alpha * texCoordA.getY() + beta * texCoordB.getY() + gamma * texCoordC.getY())/calculateDepth(coeff, xCoord, yCoord);
				
		int xPos = Math.round((sizeX * xx));
		int yPos = Math.round((sizeY * yy));

		int color;
		try {
			color = texture.getRGB(xPos, sizeY-yPos); //invert texture
		} catch(ArrayIndexOutOfBoundsException e) {
			//adjust for border-rounding
			if(xPos >= sizeX) 
				xPos = sizeX-1;
			if(sizeY-yPos >= sizeY)  
				yPos = yPos+1;//sizeY-1;
			color = texture.getRGB(xPos, sizeY-yPos); //invert texture
		}
		return color;
	}

	/**
	 * interpolates the Color-value (int) out of 3 Vertexes
	 * @param aColor
	 * @param bColor
	 * @param cColor
	 * @param xCoord
	 * @param yCoord
	 * @param coeff
	 * @return
	 */
	private int calculateComplexColor(Color3f aColor, Color3f bColor, Color3f cColor, int xCoord, int yCoord, Matrix3f coeff) {
		int resultColor;
		float resultRed = calculateSingleColor(aColor.x, bColor.x, cColor.x, coeff, xCoord, yCoord)/calculateDepth(coeff, xCoord, yCoord);
		float resultGreen = calculateSingleColor(aColor.y, bColor.y, cColor.y, coeff, xCoord, yCoord)/calculateDepth(coeff, xCoord, yCoord);
		float resultBlue = calculateSingleColor(aColor.z, bColor.z, cColor.z, coeff, xCoord, yCoord)/calculateDepth(coeff, xCoord, yCoord);
		Color3f tempColor = new Color3f(resultRed, resultGreen, resultBlue);
		resultColor = calculateColor(tempColor);
		
		return resultColor;
	}

	/**
	 * calculates a color value (R, G or B) out of 3 color values (from 3 vertexes) and point coordinates in 3d 
	 * with a given point transformation-matrix
	 * @param aC
	 * @param bC
	 * @param cC
	 * @param coeff
	 * @param x
	 * @param y
	 * @return
	 */
	private float calculateSingleColor(float aC, float bC, float cC, Matrix3f coeff, float x, float y) {
		Vector3f colorPart = new Vector3f(aC,bC,cC);
		coeff.transform(colorPart);
		float result = ((colorPart.x * x) + (colorPart.y * y) + colorPart.z)*(-1);
		return result;
	}

	/**
	 * calculates the depht-value for z-Buffering, can be used for other stuff as well
	 * @param coeff
	 * @param x
	 * @param y
	 * @return
	 */
	private float calculateDepth(Matrix3f coeff, int x, int y) {
		Vector3f vec = new Vector3f(1f, 1f, 1f);
		coeff.transform(vec);
		return ((vec.x * x) + (vec.y * y) + vec.z)*(-1);
	}
	
	/**
	 * calculates an int-color out of a normal color
	 * @param color
	 * @return
	 */
	private int calculateColor(Color3f color){ //<< shifts bits for x Elements
		int iColor;
		iColor = (int) (255f * color.x) << 16 | (int) (255f * color.y) << 8 | (int) (255f * color.z);
		return iColor;
	}
	

	
	
	
	/**
	 * delivers the screen representation for 3_1
	 * @param shapeVertexData the vertexData of the Shape
	 * @param shapeTransformation the transformation of the Shape
	 */
	private void calculateProjection31(VertexData shapeVertexData, Matrix4f shapeTransformation) {
		//reset the screen-buffer otherwise turning is a bit of a hassle...
		colorBuffer = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		
		//Vertex Data consists of a linkedList of Vertex Elements
		LinkedList<VertexElement> shapeVertexElements = shapeVertexData.getElements();
		
		//for the transformation
		Matrix4f mCamera;
		Matrix4f mProjection;
		Matrix4f multiplicationTemplate; 
		mCamera = sceneManager.getCamera().getCameraMatrix();
		mProjection = sceneManager.getFrustum().getProjectionMatrix();
		multiplicationTemplate = new Matrix4f();
		
		//calculate the change-matrix
		multiplicationTemplate.set(viewportMatrix);
		multiplicationTemplate.mul(mProjection);
		multiplicationTemplate.mul(mCamera);
		multiplicationTemplate.mul(shapeTransformation);		
				
		//for every Element out of VertexElements: basically trying to find the positions
		for(VertexElement vElement:shapeVertexElements){
			
			if(vElement.getSemantic() == VertexData.Semantic.POSITION){
				float[] data = vElement.getData(); 
				
				//paint the points
				for (int i = 0; i<data.length; i+=3){
					// cannot multiply Matrix with Vector... therefore create a matrix...
					Matrix4f tempMatrix = new Matrix4f();
					tempMatrix.setM03(data[i]);
					tempMatrix.setM13(data[i+1]);
					tempMatrix.setM23(data[i+2]);
					tempMatrix.setM33(1);
					
					//multiply
					Matrix4f result = new Matrix4f();
					result.set(multiplicationTemplate);
					result.mul(tempMatrix);
					
					float newX = result.m03;
					float newY = result.m13; 
					float newZ = result.m23;
					float newW = result.m33;
					
					//adjust these since 0/0 = upper left corner, 1/1 = lower right. 
					int pictureX = (int) (newX/newW);
					int pictureY = (int) (height-newY/newW); 
				
					//catch dots out of the picture (creates an error)
					if (pictureX > 0 && pictureX < width && pictureY > 0 && pictureY < height){
						colorBuffer.setRGB(pictureX, pictureY, 256*256*256-1); //256*256*256 = 0 is black
					}
					else {System.out.println("point not on field");}
				}									
			}				
		}				
	}

	/**
	 * Does nothing. We will not implement shaders for the software renderer.
	 */
	public Shader makeShader()	
	{
		return new SWShader();
	}

	/**
	 * Does nothing. We will not implement textures for the software renderer.
	 */
	public Texture makeTexture()
	{
		return new SWTexture();
	}
}
