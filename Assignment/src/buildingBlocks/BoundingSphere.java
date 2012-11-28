package buildingBlocks;
import javax.vecmath.Vector3f;

import buildingBlocks.IBasicShape;


public class BoundingSphere {

	private Vector3f center;
	private float radius;
	
	public BoundingSphere(IBasicShape shape){
		this.center = new Vector3f();; 
		this.radius = 0;
		calculateCenter(shape);
	}
	
	private void calculateCenter(IBasicShape shape){
		float[] v = shape.getV();
		
		//change to points
		Vector3f[] dots = new Vector3f[v.length/3];
		for (int i = 0; i<v.length; i+=3){
			dots[i/3] = new Vector3f(v[i], v[i+1], v[i+2]);
		}
		
		Vector3f average = new Vector3f();
		//calculate Center "average of all vertices"
		for (int i = 0; i<dots.length; i++){
			average.add(dots[i]);
		}
		
		average.x = average.x/dots.length;
		average.y = average.y/dots.length;
		average.z = average.z/dots.length;
		
		float maxdistance = 0; 
		Vector3f comparator = new Vector3f();

		//distance from Center to dot, take the biggest distance, that's your Radius
		for (int i = 0; i<dots.length; i++){
			comparator.sub(average, dots[i]);
			if (comparator.length() > maxdistance) {maxdistance = comparator.length();}
		}
		
		//this calculates the radius of the sphere
		this.radius = maxdistance;///2;
		
		this.center = average;
	}
	
	public Vector3f getCenter(){
		return this.center;
	}
	
	public float getRadius(){
		return this.radius;
	}
	
}

