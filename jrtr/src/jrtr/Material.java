package jrtr;

import java.io.IOException;

/**
 * Stores the properties of a material. You will implement this 
 * class in the "Shading and Texturing" project.
 */
public class Material {
	SWTexture texture, glossMap; //sh
	GLTexture glTex, glGloss;
	private String textureFileName, vertexFileName, fragmentFileName, glossmapFileName; //sh cannot include shader, needs a GL-Context, therefore just save reference to the files....
	private GLShader compiledShader;
	private float reflectionCoefficient, specularReflectionCoefficient, phongExponent;
	//needs material-Color
	
	public Material(){
		this.glTex = null;
		this.glGloss = null;
		this.texture = new SWTexture();
		this.glossMap = new SWTexture();
		this.reflectionCoefficient = 1; //standard-value
		this.specularReflectionCoefficient = 1; 
		this.phongExponent = 1;
	}

	/**
	 * used for Assignment 4, loads texture
	 * TODO texture-loading is maybe not needed
	 * @param textureLocation: the location
	 */
	public void loadTexture(String textureLocation){
		try{
			this.texture.load(textureLocation);
		}
		catch(IOException e) {
			System.out.println("cannot read file");
			System.exit(0);
		}
		this.textureFileName = textureLocation;
	}
	
	public SWTexture getTexture() {
		return texture;
	}
	
	public void loadGlossmap(String textureLocation){
		try{
			this.glossMap.load(textureLocation);
		}
		catch(IOException e) {
			System.out.println("cannot read file");
			System.exit(0);
		}
		this.glossmapFileName = textureLocation;
	}
	
	public SWTexture getGlossmap() {
		return glossMap;
	}
	
	public void setGLTexture(GLTexture tex){
		this.glTex = tex;
	}
	
	public GLTexture getGLTexture(){
		return glTex;
	}
	
	public void setGLGloss(GLTexture tex){
		this.glGloss = tex;
	}
	
	public GLTexture getGLGloss(){
		return glGloss;
	}
	
	
	/**
	 * used for Assignment 4, loads Shader
	 * @param vertexFileName
	 * @param fragmentFileName
	 */
	public void loadShader(String vertexFileName, String fragmentFileName){
		this.vertexFileName = vertexFileName;
		this.fragmentFileName = fragmentFileName;
	}
	
	public String getVertexFileName(){
		return vertexFileName;
	}
	
	public String getFragmentFileName(){
		return fragmentFileName;
	}	
	
	public String getTextureFileName(){
		return textureFileName;
	}
	
	public String getGlossmapFileName(){
		return glossmapFileName;
	}
	
	public void setReflectionCoefficient(float coefficient){
		this.reflectionCoefficient = coefficient;
	}
	public float getReflectionCoefficient(){
		return this.reflectionCoefficient;
	}
	
	public void setSpecularReflectionCoefficient(float coefficient){
		this.specularReflectionCoefficient = coefficient;
	}
	public float getSpecularReflectionCoefficient(){
		return this.specularReflectionCoefficient;
	}
	public void setPhongExponent(float exponent){
		this.phongExponent = exponent;
	}
	public float getPhongExponent(){
		return this.phongExponent;
	}
	
	public void setCompiledShader(GLShader shader){
		this.compiledShader = shader;
	}
	public GLShader getCompiledShader(){
		return this.compiledShader;
	}
	
	
}
