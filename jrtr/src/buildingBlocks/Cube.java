package buildingBlocks;

import jrtr.VertexData;

/**
 * Creates the vertex-Data for the modeling of a cylinder
 * @author shirsbrunner
 *
 */


public class Cube implements IBasicShape {

	private float[] v; //vertex positions, every vertex has 3 records
	private float[] c; //vertex colors, same length as v[]
	private int[] indices; //every polygon has 3, connects elements of v[]
	private int countOfVertices;
	private int colorSwitch;
	
	/**
	 * Constructs a cube
	 * @param numberOfSegments complexity of the mesh, a higher number generates more polynoms
	 * 			equals the number of Corners per Ring
	 * @param x x-length
	 * @param y y-length
	 * @param z z-length
	 * @param colorSwitch a number > 0 to calculate color indices randomly
	 */
	public Cube(float x, float y, float z, int colorSwitch){

		this.colorSwitch = colorSwitch;
		countOfVertices = 8; //it's a cube  
		v = calcVertices(x,y,z);
		c = calcColors();
		indices = combineVertices();			
	}


	/**
	 * combines vertices to polynoms
	 * Attention, a reference without vertex throws an error during runtime
	 * @return links consisting of pointers to vertices
	 */
	private int[] combineVertices() {
		int[] tempindices = {
			0,4,1,
			1,5,2,
			2,6,3,
			3,7,0,
			
			1,4,5,
			2,5,6,
			3,6,7,
			0,7,4,
			
			3,0,1,
			3,1,2,
			7,4,5,
			7,5,6};		
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
			int temp = i%colorSwitch; //change this number to change colors
			if (temp == 0){tempC[i]=1;}
			else{tempC[i]=0;}
		}
		return tempC;
	}


	/**
	 * Calculate Vertices
	 * 
	 * @param numberOfSegments complexity of the mesh
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private float[] calcVertices(float x, float y, float z) {
		float [] tempV = {
			-x/2, y/2, z/2,
			x/2, y/2, z/2,
			x/2, y/2, -z/2,
			-x/2, y/2, -z/2,
			
			-x/2, -y/2, z/2,
			x/2, -y/2, z/2,
			x/2, -y/2, -z/2,
			-x/2, -y/2, -z/2};
		return tempV;
	}
		
	
	@Override
	public VertexData getVertexData() {
		int vertexOutput = 8; //Creates error if this is wrong!
		
		VertexData vertexData = new VertexData(vertexOutput);
		vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
		vertexData.addElement(v, VertexData.Semantic.POSITION, 3);

		vertexData.addIndices(indices);
		return vertexData;
	}



	public int getVertexCount() {
		return countOfVertices;
	}


	public float[] getV() {
		return v;
	}


	public float[] getC() {
		return c;
	}


	public int[] getIndices() {
		return indices;
	}


}
