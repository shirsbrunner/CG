package buildingBlocks;

import jrtr.VertexData;

/**
 * Creates the vertex-Data for the modeling of a cylinder
 * @author shirsbrunner
 *
 */


public class Cylinder implements IBasicShape {

	private float[] v; //vertex positions, every vertex has 3 records
	private float[] c; //vertex colors, same length as v[]
	private int[] indices; //every polygon has 3, connects elements of v[]
	private int countOfVertices;
	private int numberOfSegments;
	private int colorSwitch;
	
	/**
	 * Constructs a cylinder
	 * @param numberOfSegments complexity of the mesh, a higher number generates more polynoms
	 * 			equals the number of Corners per Ring
	 * @param height the height of the cylinder
	 * @param upperRadius the radius of the top cylinder
	 * @param lowerRadius the radius of the bottom cylinder
	 * @param colorSwitch a number > 0 to calculate color indices randomly
	 */
	public Cylinder(int numberOfSegments, float height, float upperRadius, float lowerRadius, int colorSwitch){
		this.numberOfSegments = numberOfSegments;
		this.colorSwitch = colorSwitch;
		countOfVertices = numberOfSegments*2+2; //two circles, the center dots  
		v = calcVertices(numberOfSegments, height, upperRadius, lowerRadius);
		c = calcColors();
		indices = combineVertices();			
	}


	/**
	 * combines vertices to polynoms
	 * Attention, a reference without vertex throws an error during runtime
	 * @return links consisting of pointers to vertices
	 */
	private int[] combineVertices() {
		//calculate the number of lines
		int polynomCount = numberOfSegments*4; //TODO was (countOfVertices-2)*3; //only circumference points, have 3 polynoms each 
		int polynomBorderLineCount = polynomCount*3; //each polynom has 3 lines, triangles.
		
		int[] tempindices = new int[polynomBorderLineCount];
		
		//upper ring (point, point below, point to the right)
		int vCounter = 0;
		for (int i = 0; i < numberOfSegments; i++){
			tempindices[vCounter] = i; //originating vertex
			tempindices[vCounter+1] = i + numberOfSegments; 
			if (i+1 == numberOfSegments){tempindices[vCounter+2] = 0;}
			else{tempindices[vCounter+2] = i + 1; //vertex on the right of originating vertex 
			}
			vCounter += 3;
		}
		
		//lower ring (point, point to the right, upper point)
		for (int i = numberOfSegments; i < numberOfSegments*2; i++){
			tempindices[vCounter] = i; //originating vertex
			if (i+1 == numberOfSegments*2){tempindices[vCounter+1] = numberOfSegments;}
			else{tempindices[vCounter+1] = i + 1; //vertex on the right of originating vertex 
			}
			
			if (i-numberOfSegments+1 == numberOfSegments){tempindices[vCounter+2] = 0;}
			else{tempindices[vCounter+2] = i - numberOfSegments+1; //vertex on the right of originating vertex 
			}
			vCounter += 3;
		}
		
		//upper Center (Center, Point, point to the right)
		for (int i = 0; i < numberOfSegments; i++){
			tempindices[vCounter] = countOfVertices-2; 
			tempindices[vCounter+1] = i;
			if (i+1 == numberOfSegments){tempindices[vCounter+2] = 0;}
			else{tempindices[vCounter+2] = i + 1; //vertex on the right of originating vertex 
			}
			vCounter += 3;
		}
		
		//lower Center
		for (int i = numberOfSegments; i < numberOfSegments*2; i++){
			tempindices[vCounter] = countOfVertices-1; 
			tempindices[vCounter+1] = i;
			if (i+1 == numberOfSegments*2){tempindices[vCounter+2] = numberOfSegments;}
			else{tempindices[vCounter+2] = i + 1; //vertex on the right of originating vertex 
			}
			vCounter += 3;
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
	 * @param height height of the cylinder
	 * @param upperRadius radius of the top cylinder
	 * @param lowerRadius radius of the bottom cylinder
	 * @return
	 */
	private float[] calcVertices(int numberOfSegments, float height, float upperRadius, float lowerRadius) {
		//float angle = 360/numberOfSegments;
		float[] tempV = new float[(countOfVertices)*3]; //three elements per vertex, x,y,z
		
		//upper ring, every turn adds one vertex consisting of 3 elements
		//float tempAngle = 0;
		int vertexCounter = 0;
		for (int i = 0; i < numberOfSegments; i++){
			
			tempV[vertexCounter]=(float) (upperRadius*Math.cos(Math.toRadians(360*i/numberOfSegments)));//tempAngle))); //x-value important, cos/sin needs Radians
			tempV[vertexCounter+1]=height/2; //y value
			tempV[vertexCounter+2]=(float) (upperRadius*Math.sin(Math.toRadians(360*i/numberOfSegments)));//tempAngle)));  //z-value
			vertexCounter+=3;
		}
		
		//lower ring
		//tempAngle = 0;
		for (int i = 0; i <numberOfSegments;i++ ){
			tempV[vertexCounter]=(float) (lowerRadius*Math.cos(Math.toRadians(360*i/numberOfSegments)));//tempAngle))); //x-value
			tempV[vertexCounter+1]=-height/2; //y value
			tempV[vertexCounter+2]=(float) (lowerRadius*Math.sin(Math.toRadians(360*i/numberOfSegments)));//tempAngle)));  //z-value
			vertexCounter+=3;
		}
		//upper centerpoint		
		tempV[tempV.length-1-5] = 0;
		tempV[tempV.length-1-4] = height/2;
		tempV[tempV.length-1-3] = 0;
		
		//lower centerpoint
		tempV[tempV.length-1-2] = 0;
		tempV[tempV.length-1-1] = -height/2;
		tempV[tempV.length-1] = 0;
		
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
		int vertexOutput = numberOfSegments*2+2; //Creates error if this is wrong!
		
		VertexData vertexData = new VertexData(vertexOutput); //TODO is this right?
		vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
		vertexData.addElement(v, VertexData.Semantic.POSITION, 3);

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
