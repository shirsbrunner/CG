package buildingBlocks;

import jrtr.VertexData;

/**
 * Creates the vertex-Data for the modeling of a torus
 * @author shirsbrunner
 *
 */

public class Torus implements IBasicShape {

	private float[] v; //vertex positions, every vertex has 3 records
	private float[] c; //vertex colors, same length as v[]
	private int[] indices; //every polygon has 3, connects elements of v[]
	private int countOfVertices;
	private int numberOfSegments;
	private int segmentComplexity;
	private int colorSwitch;
	
	
	/**
	 * See wikipedia for Graphics: http://en.wikipedia.org/wiki/Torus
	 * @param numberOfSegments Slices the torus consists of (3 or higher advised)
	 * @param segmentComplexity Edges of the slices (3 or higher advised)
	 * @param innerRingRadius Distance from the Center, to the mid of the Torus (R)
	 * @param torusRadius Radius of a slice (r)
	 * @param colorSwitch a number > 0 to calculate color indices randomly
	 */
	public Torus(int numberOfSegments, int segmentComplexity, float innerRingRadius, float torusRadius, int colorSwitch){
		this.numberOfSegments = numberOfSegments;
		this.segmentComplexity = segmentComplexity;
		this.colorSwitch = colorSwitch;
		countOfVertices = numberOfSegments*segmentComplexity;
		v = calcVertices(numberOfSegments, segmentComplexity, innerRingRadius, torusRadius);
		c = calcColors();
		indices = combineVertices();
	}
	
	/**
	 * combines vertices to polynoms, always finishes two adjoining polynoms
	 * Attention, a reference without vertex throws an error during runtime
	 * @return links consisting of pointers to vertices
	 */
	private int[] combineVertices() {
		int polynomCount = (countOfVertices)*2; //every vertex has two polynoms 
		int polynomBorderLineCount = polynomCount*3; //each polynom has 3 lines, triangles.
				
		int[] tempindices = new int[polynomBorderLineCount];
		
		int vCounter = 0;
		//first layer, maybe add two layers together?
		
		for (int i = 0; i < countOfVertices; i++){
			tempindices[vCounter] = i;  //first layer
			
			if (i+numberOfSegments >= countOfVertices){
				tempindices[vCounter+1]= i+numberOfSegments-countOfVertices;}
			else {tempindices[vCounter+1] = i+numberOfSegments;} //adjustment for top-Ring (links to bottom ring), first layer
			
			if ((i+1)%numberOfSegments == 0){
				tempindices[vCounter+2] = i+1-numberOfSegments;} //adjust for Segment link, first layer
			else {tempindices[vCounter+2] = i+1;}
			
			if (i+numberOfSegments >= countOfVertices){
				tempindices[vCounter+3] = i+numberOfSegments-countOfVertices;}
			else {tempindices[vCounter+3] = i+numberOfSegments;} //second layer
			
			if ((i+1)%numberOfSegments == 0){
				tempindices[vCounter+4] = i+1-numberOfSegments;}
			else {tempindices[vCounter+4] = i+1;} //second layer
			
			if (i+numberOfSegments + 1 >= countOfVertices){
				tempindices[vCounter+5] = i+numberOfSegments+1-countOfVertices;} //check for border conditions
			else {tempindices[vCounter+5] = i+numberOfSegments+1;} //second layer
			
			if (tempindices[vCounter+5]%numberOfSegments == 0){
				tempindices[vCounter+5] -= numberOfSegments; //seam-correction
				if (tempindices[vCounter+5] < 0){
					tempindices[vCounter+5] += countOfVertices;} //second border-condition, correct if falls below 0-node
			}
			vCounter +=6;	
		}
		return tempindices;
	}

	/**
	 * creates easy colors depending on the index of the vertex
	 * Important has to be the same length as the vertex array, otherwise all 
	 * faces appear red
	 * @return array with 1/0 for r,g,b values
	 */
	private float[] calcColors() {
		float[] tempC = new float[(countOfVertices)*3]; //three elements per vertex, r,g,b];
		
		for (int i = 0; i < tempC.length; i++){
			int temp = i%colorSwitch;
			if (temp == 0){tempC[i]=1;}
			else{tempC[i]=0;}
		}
		return tempC;
	}

	/**
	 * Calculate Vertices
	 * @param numberOfSegments
	 * @param segmentComplexity
	 * @param innerRingRadius
	 * @param torusRadius
	 * @return an array consisting of the calculated vertices
	 */
	private float[] calcVertices(int numberOfSegments, int segmentComplexity, float innerRingRadius, float torusRadius) {
		float[] tempV = new float[(countOfVertices)*3]; //three elements per vertex, x,y,z
		int tempVerticeCounter = 0; 
		
		for (int v = 0; v < segmentComplexity; v++){
			for (int u = 0; u < numberOfSegments; u++){
				
				/**
				the following stands for
				x(u, v) = (R + r*cos(v))*cos(u); 
				y(u, v) = (R + r*cos(v))*sin(u);
				z(u, v) = r*sin(v);
		    
				v = segmentComplexity
				u = number of Segments
				R = innerRingRadius
				r = torusRadius
				**/
				
				tempV[tempVerticeCounter] = (float) ((innerRingRadius + torusRadius*Math.cos(Math.toRadians(360*v/segmentComplexity)))*Math.cos(Math.toRadians(360*u/numberOfSegments)));
				tempV[tempVerticeCounter+1] = (float) ((innerRingRadius + torusRadius*Math.cos(Math.toRadians(360*v/segmentComplexity)))*Math.sin(Math.toRadians(360*u/numberOfSegments)));
				tempV[tempVerticeCounter+2] = (float) (torusRadius*Math.sin(Math.toRadians(360*v/segmentComplexity)));
				
				tempVerticeCounter +=3;
			}
		}
		return tempV;
	}

	/**
	 * moves into x direction, before combination
	 * @param x the distance to move
	 */
	@Override
	public void moveX(float x){
		for (int i = 0; i < countOfVertices*3; i++){
			v[i] += x;
			i+=2;
		}
	}
	
	/**
	 * moves into y direction, before combination
	 * @param y the distance to move
	 */
	@Override
	public void moveY(float y){
		for (int i = 0; i < countOfVertices*3; i++){
			v[i+1] += y;
			i+=2;
		}
	}
	
	/**
	 * moves into z direction, before combination
	 * @param y the distance to move
	 */
	@Override
	public void moveZ(float z){
		for (int i = 0; i < countOfVertices*3; i++){
			v[i+2] += z;
			i+=2;
		}
	}
	
	@Override
	public VertexData getVertexData() {
		int vertexOutput = numberOfSegments*segmentComplexity; //Creates error, if this is wrong!
		
		VertexData vertexData = new VertexData(vertexOutput);
		vertexData.addElement(c, VertexData.Semantic.COLOR, 3); //color consisting of 3 elements
		vertexData.addElement(v, VertexData.Semantic.POSITION, 3); //position consisting of 3 elements

		vertexData.addIndices(indices);
		return vertexData;
	}
	
	@Override
	public int getVertexCount() {
		return countOfVertices;
	}
	
	@Override
	public float[] getV() {
		return v;
	}


	@Override
	public float[] getC() {
		return c;
	}


	@Override
	public int[] getIndices() {
		return indices;
	}

}
