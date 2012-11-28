package jrtr;

import javax.vecmath.Vector4f;

/**
 * Stores the properties of a light source. To be implemented for 
 * the "Texturing and Shading" project.
 * TODO needs color, type of light, direction
 */
public class Light {
	
	private float xCoord, yCoord, zCoord, radiance; //radiance = intensity
	private Vector4f color;
	
	/**
	 * Constructor for a default light from (0,0,1), radiance 1
	 */
	public Light(){
		this.xCoord = 0; 
		this.yCoord = 0; 
		this.zCoord = 1; 
		this.radiance = 1;
		this.color = new Vector4f(0,0,0,0);
	}
	
	public void setCoordinates(float x, float y, float z){
		this.xCoord = x;
		this.yCoord = y;
		this.zCoord = z;
	}
	
	public float getX(){
		return this.xCoord;
	}
	public float getY(){
		return this.yCoord;
	}
	public float getZ(){
		return this.zCoord;
	}
	
	public float getRadiance(){
		return this.radiance;
	}
	/**
	 * set radiance between 0 and 1
	 * @param radiance
	 */
	public void setRadiance(float radiance){
		this.radiance = radiance;
	}
	
	public void setColor(float r, float g, float b){//sh
		this.color = new Vector4f(r,g,b,1);
	}
	
	public Vector4f getColor(){
		return this.color;
	}
	
	public String toString(){
		String a = "lightcoord: " + this.getX()+","+this.getY()+","+this.getZ();
		
		return a;
	}
	
}
