package jrtr;

import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.*;

import buildingBlocks.BoundingSphere;
import buildingBlocks.IBasicShape;

/**
 * This class implements a {@link RenderContext} (a renderer) using OpenGL version 3 (or later).
 */
public class GLRenderContext implements RenderContext {

	private SceneManagerInterface sceneManager;
	private GL3 gl;
	private GLShader activeShader;
	private GLShader shaderOne;
	private GLShader shaderTwo;
	private EFrustum efrustum;
	
	
	/**
	 * This constructor is called by {@link GLRenderPanel}.
	 * 
	 * @param drawable 	the OpenGL rendering context. All OpenGL calls are
	 * 					directed to this object.
	 */
	
	public GLRenderContext(GLAutoDrawable drawable) //used for assignment4, had to change from GLRenderContext_DefaultMaterial(GLAutoDrawable drawable)
	{
		gl = drawable.getGL().getGL3();
		gl.glEnable(GL3.GL_DEPTH_TEST);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
       	
	}

		
	/**
	 * Set the scene manager. The scene manager contains the 3D
	 * scene that will be rendered. The scene includes geometry
	 * as well as the camera and viewing frustum.
	 */
	public void setSceneManager(SceneManagerInterface sceneManager)
	{
		this.sceneManager = sceneManager;
		EFrustum efrustum = new EFrustum(sceneManager.getFrustum());
		efrustum.setFrustum(1, 100, 1, 120); //this does somehow not set the frustum as intended... who knows
		
	}
	
	/**
	 * This method is called by the GLRenderPanel to redraw the 3D scene.
	 * The method traverses the scene using the scene manager and passes
	 * each object to the rendering method.
	 */
	public void display(GLAutoDrawable drawable)
	{
		gl = drawable.getGL().getGL3();
		
		beginFrame();
		
		SceneManagerIterator iterator = sceneManager.iterator();	
		while(iterator.hasNext())
		{

			
			RenderItem r = iterator.next();
			BoundingSphere bSphere = new BoundingSphere(r.getShape());
			
			//if(r.getShape()!=null && efrustum.insideTest(bSphere)) draw(r);
			if(r.getShape()!=null) draw(r);
		}		
		
		endFrame();
	}
		
	/**
	 * This method is called at the beginning of each frame, i.e., before
	 * scene drawing starts.
	 */
	private void beginFrame()
	{
		//setLights(); TODO need to be able to set lights here, this would mean that the lights need to be stored, since
		//I want to work with two different shaders, otherwise: no different textures maybe
		
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
        gl.glClear(GL3.GL_DEPTH_BUFFER_BIT);
	}

	/**
	 * This method is called at the end of each frame, i.e., after
	 * scene drawing is complete.
	 */
	private void endFrame()
	{
        gl.glFlush();		
	}
	
	/**
	 * Convert a Matrix4f to a float array in column major ordering,
	 * as used by OpenGL.
	 */
	private float[] matrix4fToFloat16(Matrix4f m)
	{
		float[] f = new float[16];
		for(int i=0; i<4; i++)
			for(int j=0; j<4; j++)
				f[j*4+i] = m.getElement(i,j);
		return f;
	}
	
	/**
	 * The main rendering method.
	 * 
	 * @param renderItem	the object that needs to be drawn
	 */
	private void draw(RenderItem renderItem)
	{
		VertexData vertexData = renderItem.getShape().getVertexData();
		LinkedList<VertexData.VertexElement> vertexElements = vertexData.getElements();
		int indices[] = vertexData.getIndices();

		// Don't draw if there are no indices
		if(indices == null) return;
		
		// Set the material
		setMaterial(renderItem.getShape().getMaterial());		
		
		// Compute the modelview matrix by multiplying the camera matrix and the 
		// transformation matrix of the object
		Matrix4f t = new Matrix4f();
		t.set(sceneManager.getCamera().getCameraMatrix());
		t.mul(renderItem.getT());
		
		// Set modelview and projection matrices in shader
		gl.glUniformMatrix4fv(gl.glGetUniformLocation(activeShader.programId(), "modelview"), 1, false, matrix4fToFloat16(t), 0);
		gl.glUniformMatrix4fv(gl.glGetUniformLocation(activeShader.programId(), "projection"), 1, false, matrix4fToFloat16(sceneManager.getFrustum().getProjectionMatrix()), 0);
		gl.glUniformMatrix4fv(gl.glGetUniformLocation(activeShader.programId(), "cameraMatrix"), 1, false, matrix4fToFloat16(sceneManager.getCamera().getCameraMatrix()), 0);
		
		// Steps to pass vertex data to OpenGL:
		// 1. For all vertex attributes (position, normal, etc.)
			// Copy vertex data into float buffers that can be passed to OpenGL
			// Make/bind vertex buffer objects
			// Tell OpenGL which "in" variable in the shader corresponds to the current attribute
		// 2. Render vertex buffer objects
		// 3. Clean up
		// Note: Of course it would be more efficient to store the vertex buffer objects (VBOs) in a
		// vertex array object (VAO), and simply bind ("load") the VAO each time to render. But this
		// requires a bit more logic in the rendering engine, so we render every time "from scratch".
		        
        // 1. For all vertex attributes, make vertex buffer objects
        IntBuffer vboBuffer = IntBuffer.allocate(vertexElements.size());
        gl.glGenBuffers(vertexElements.size(), vboBuffer);
		ListIterator<VertexData.VertexElement> itr = vertexElements.listIterator(0);
		while(itr.hasNext())
		{
			// Copy vertex data into float buffer
			VertexData.VertexElement e = itr.next();
			int dim = e.getNumberOfComponents();

	        FloatBuffer varr = FloatBuffer.allocate(indices.length * dim);
	        for (int i = 0; i < indices.length; i++) {
	            for (int j = 0; j < dim; j++) {
	                varr.put(e.getData()[dim * indices[i] + j]);
	            }
	        }
	        	        
	        // Make vertex buffer object 
	        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vboBuffer.get());
	        // Upload vertex data
	        gl.glBufferData(GL3.GL_ARRAY_BUFFER, varr.array().length * 4, FloatBuffer.wrap(varr.array()), GL3.GL_DYNAMIC_DRAW);	        
	        
	        // Tell OpenGL which "in" variable in the vertex shader corresponds to the current vertex buffer object
	        // We use our own convention to name the variables, i.e., "position", "normal", "color", "texcoord", or others if necessary
	        int attribIndex = -1;
	        if(e.getSemantic() == VertexData.Semantic.POSITION) {
	        	attribIndex = gl.glGetAttribLocation(activeShader.programId(), "position");
	        }	        	       
	        else if(e.getSemantic() == VertexData.Semantic.NORMAL) {
	        	attribIndex = gl.glGetAttribLocation(activeShader.programId(), "normal");
	        }	        
	        else if(e.getSemantic() == VertexData.Semantic.COLOR) {
	        	attribIndex  = gl.glGetAttribLocation(activeShader.programId(), "color");
	        }
	        else if(e.getSemantic() == VertexData.Semantic.TEXCOORD) {
	        	attribIndex = gl.glGetAttribLocation(activeShader.programId(), "texcoord");
	        }
        	gl.glVertexAttribPointer(attribIndex, dim, GL3.GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(attribIndex);        
		}
		
		// 3. Render the vertex buffer objects
		gl.glDrawArrays(GL3.GL_TRIANGLES, 0, indices.length);
		
		// 4. Clean up
		gl.glDeleteBuffers(vboBuffer.array().length, vboBuffer.array(), 0);
        
        cleanMaterial(renderItem.getShape().getMaterial());
	}

	/**
	 * Pass the material properties to OpenGL, including textures and shaders.
	 * Answer: shaders are only compiled once (singleton)
	 * this does not work for dynamic lights (if lights should be dynamic add them later, precompile shader)
	 * All done by SH
	 */
	private void setMaterial(Material m){
		
		int id; //SH this is used and reused over the whole code to match uniforms to input. 
		
		//create & pass shader
		if (m.getCompiledShader() == null){ //the shader doesn't exist
			GLShader defaultShader = new GLShader(gl);
	        try {
	        	defaultShader.load(m.getVertexFileName(), m.getFragmentFileName());
	        } catch(Exception e) {
		    	System.out.print("Problem with shader:\n");
		    	System.out.print(e.getMessage());
		    }
	        defaultShader.use();	  
	        
	        //pass the Lights to the current shader
	        setLights(defaultShader);
	        
	        //pass shininess (reflectionCoefficient)
	        id = gl.glGetUniformLocation(defaultShader.programId(), "reflectionCoefficient"); //active Shader
	        gl.glUniform1f(id, m.getReflectionCoefficient());
	        
	        //this is used for 4.2 and on (Phong-Shader)
	        id = gl.glGetUniformLocation(defaultShader.programId(), "phongExponent");
	        gl.glUniform1f(id, m.getPhongExponent());
	        
	        id = gl.glGetUniformLocation(defaultShader.programId(), "specularReflection");
	        gl.glUniform1f(id, m.getSpecularReflectionCoefficient());
	        
	        id = gl.glGetUniformLocation(defaultShader.programId(), "cameraPosition");
	        
	        float cameraX = sceneManager.getCamera().getCameraMatrix().m03;
	        float cameraY = sceneManager.getCamera().getCameraMatrix().m13;
	        float cameraZ = sceneManager.getCamera().getCameraMatrix().m23;
	        float cameraW = sceneManager.getCamera().getCameraMatrix().m33;
	        
	        gl.glUniform4f(id, cameraX, cameraY, cameraZ, cameraW );

	        //create & pass Object texture
			m.setCompiledShader(defaultShader);
			activeShader = m.getCompiledShader();
		}
		else{
			GLShader tempShader = m.getCompiledShader();
			setLights(tempShader);
			loadTexture(m, tempShader); //TODO loads texture and glossmap, disable and everthing runs smoooother 
			tempShader.use();
			activeShader = tempShader;	
		}
	}

	//new multitex inkl glossmap
	private void loadTexture(Material m, GLShader defaultShader) {
		if (m.getGLTexture() == null && m.getGLGloss() == null){
			System.out.println("Will try to load");
			try { //to load texture

	
		        GLTexture Texture0 = new GLTexture(gl);
		        Texture0.load(m.getTextureFileName());
		        m.setGLTexture(Texture0); //new
		        
		        GLTexture Texture1 = new GLTexture(gl);
		        Texture1.load(m.getGlossmapFileName());
		        m.setGLGloss(Texture1); //new
	
		        
			} catch(Exception e) {
				System.out.print("Could not load texture\n");
			}
		}
		
		GLTexture Texture0 = m.getGLTexture();
		GLTexture Texture1 = m.getGLGloss();
		
		gl.glUniform1i(gl.glGetUniformLocation(activeShader.programId(), "Texture0"), 0);
        gl.glUniform1i(gl.glGetUniformLocation(activeShader.programId(), "Texture1"), 2);
        
        int idTex0 = ((GLTexture) Texture0).getId();
        int idTex1 = ((GLTexture) Texture1).getId();
        
        gl.glActiveTexture(GL3.GL_TEXTURE0+0);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, idTex0);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
        
        gl.glActiveTexture(GL3.GL_TEXTURE0+2);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, idTex1);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);  
	}
	/* old singleTex
	private void loadTexture(Material m, GLShader defaultShader) {
		int id;
		GLTexture tex;
		try {
			// Load texture from filelink
			tex = new GLTexture(gl);
			tex.load(m.getTextureFileName());
			
			// OpenGL calls to activate the texture 
			gl.glActiveTexture(0);	// Work with texture unit 0
			gl.glEnable(GL3.GL_TEXTURE_2D);
			gl.glBindTexture(GL3.GL_TEXTURE_2D, tex.getId());
			gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
			gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
			id = gl.glGetUniformLocation(defaultShader.programId(), "myTexture"); //active Shader
			gl.glUniform1i(id, 0);	// The variable in the shader needs to be set to the desired texture unit, i.e., 0
		} catch(Exception e) {
			System.out.print("Could not load texture\n");
		}
	}
	*/
	
	/**
	 * Pass the light properties to OpenGL. This assumes the list of lights in 
	 * the scene manager is accessible via a method Iterator<Light> lightIterator().
	 * 
	 * To be implemented in the "Textures and Shading" project.
	 */
	void setLights(GLShader defaultShader){
		
		Iterator<Light> lItr = this.sceneManager.lightIterator(); 
        
		int id;
		int lightCounter = 0;
        //pass the lights, coordinates & Radiance
        while (lItr.hasNext()){//while there are lights, need to adjust this for size of the lightArray, shader is set to 5
        	//create light-Array to pass
        	Light light = lItr.next();
        	
        	//could do this differently... see Assignment 4 slides P38 
        	String lightPositionString = "lightPositionArray["+lightCounter+"]";
        	String lightIntensityString = "lightIntensityArray["+lightCounter+"]";
        	String lightColorString = "lightColorArray["+lightCounter+"]";
        	
        	id = gl.glGetUniformLocation(defaultShader.programId(), lightPositionString); //active Shader
      		gl.glUniform4f(id, light.getX(), light.getY(), light.getZ(), 1); //w = 1 since its a point, not a vector //haha the error was here
      		
        	id = gl.glGetUniformLocation(defaultShader.programId(), lightIntensityString); //active Shader
      		gl.glUniform1f(id, light.getRadiance());
      		
        	id = gl.glGetUniformLocation(defaultShader.programId(), lightColorString); //active Shader
      		gl.glUniform4f(id, light.getColor().x, light.getColor().y, light.getColor().z, light.getColor().w);
      		
      		lightCounter++;
        }
	}

	/**
	 * Disable a material.
	 * TODO: i do nothing here...
	 * To be implemented in the "Textures and Shading" project.
	 */
	private void cleanMaterial(Material m){

	}

	public Shader makeShader()
	{
		return new GLShader(gl);
	}
	
	public Texture makeTexture()
	{
		return new GLTexture(gl);
	}
}
	