package buildingBlocks;
import java.util.Arrays;

import javax.vecmath.Vector3f;

import jrtr.VertexData;


public class Landscape implements IBasicShape{

	final int DATA_SIZE;
	float[][] landscape; //the landscape calculation base
	
	private float[] v; //vertex positions, every vertex has 3 records
	private Vector3f[] vV3f; //vertex positions as Vector3f Elements
	private float[] vn; //normals on vertices, same length as v
	private Vector3f[] vnV3f; //normals Vector3f Elements
	private Vector3f[] cV3f;
	private float[] c;
	private int[] indices; //every polygon has 3, connects elements of v[]
	private int vertexCount;
	private float colorSum;
	
	/**
	 * @param landscapeSize landscape size will be 2^this+1
	 * @param oneOne top left height value
	 * @param oneX top right height value
	 * @param xOne bottom left height value
	 * @param XX bottom right height value
	 */
	public Landscape(int landscapeSize, float oneOne, float oneX, float xOne, float XX){
		this.DATA_SIZE = (int) (Math.pow(2, landscapeSize)+1);		
		landscape = new float[DATA_SIZE][DATA_SIZE];
		generateLandscape(landscape, oneOne, oneX, xOne, XX);
		
		//vertex Calculation
		this.vV3f = calculateV(landscape);
		center(vV3f);
		this.v = convertVtoFloat(vV3f);
		
		//indices
		this.indices = connectDots();
		
		//normals
		this.vnV3f = calculateNormals();
		this.vn = convertVtoFloat(vnV3f);
		
		//colors
		this.cV3f = calculateColor(vV3f);
		this.c = convertVtoFloat(cV3f);
		
		//vertexData
		vertexCount = DATA_SIZE*DATA_SIZE;
	}
		

	/**
	 * adjusts for Starting at 0/0
	 * @param input base landscape in Vector3f Format
	 */
	private void center(Vector3f[] input) {
		for (int i = 0; i < input.length; i++){
			input[i].x -= DATA_SIZE/2;
			input[i].y -= DATA_SIZE/2;
		}
	}

	/**
	 * generates the basic landscape from the given four corners
	 * @param landscape
	 * @param oneOne
	 * @param oneX
	 * @param xOne
	 * @param XX
	 */
	public void generateLandscape(float[][] landscape, float oneOne, float oneX, float xOne, float XX){
		float[][] data = landscape;
		colorSum = (oneOne+oneX+xOne+XX);
		
		//fill the corners
		data[0][0] = oneOne;
		data[0][DATA_SIZE-1] = oneX;
		data[DATA_SIZE-1][0] = xOne;
		data[DATA_SIZE-1][DATA_SIZE-1] = XX;
	
		float h = (oneOne+oneX+xOne+XX)/4;//the intial range (-h -> +h) for the average offset 
		for(int sideLength = DATA_SIZE-1; sideLength >= 2; sideLength /=2, h/= 2.0){ //make random values & side length smaller
			  int hSide = sideLength/2; //calculating half-Side
		
			  //generate the new square values
			  for(int x=0;x<DATA_SIZE-1;x+=sideLength){
				    for(int y=0;y<DATA_SIZE-1;y+=sideLength){//x, y is upper left corner of square calculate average of existing corners
					      double avg = data[x][y] + //top left
					      data[x+sideLength][y] +//top right
					      data[x][y+sideLength] + //lower left
					      data[x+sideLength][y+sideLength];//lower right
					      avg /= 4.0;
				
					      //center is average plus random offset
					      data[x+hSide][y+hSide] = (float) (avg + (Math.random()*2*h) - h); //calculate random value in range of 2h and then subtract h so the end value is in the range (-h, +h)
					     
					      //colorization help
					      colorSum += data[x+hSide][y+hSide];
				    }
			  }
		
			  //generate the diamond values
			  for(int x=0;x<DATA_SIZE-1;x+=hSide){  //and y is x offset by half a side, but moved by the full side length NOTE: if the data shouldn't wrap then y < DATA_SIZE to generate the far edge values
				    for(int y=(x+hSide)%sideLength;y<DATA_SIZE-1;y+=sideLength){ //x, y is center of diamond; use mod and add DATA_SIZE for subtraction so its possible to wrap around the array to find the corners
					      float avg = 
					      data[(x-hSide+DATA_SIZE-1)%(DATA_SIZE-1)][y] + //left of center
					      data[(x+hSide)%(DATA_SIZE-1)][y] + //right of center
					      data[x][(y+hSide)%(DATA_SIZE-1)] + //below center
					      data[x][(y-hSide+DATA_SIZE-1)%(DATA_SIZE-1)]; //above center
					      avg /= 4.0;
				
					      //new value = average plus random offset We calculate random value in range of 2h and then subtract h so the end value is in the range (-h, +h)
					      avg = (float) (avg + (Math.random()*2*h) - h); //update value for center of diamond
					      data[x][y] = avg;
					      
					      //colorization help
					      colorSum += data[x][y];
					      
				
					      //wrap values on the edges, remove this and adjust loop condition above for non-wrapping values.
					      if(x == 0)  data[DATA_SIZE-1][y] = avg;
					      if(y == 0)  data[x][DATA_SIZE-1] = avg;
				    }
			  }
		}
	}

	/**
	 * creates a Vector3f representation of the landscape
	 * @param landscape
	 * @return
	 */
	private Vector3f[] calculateV(float[][] landscape){
		//save these as Vector3F new!!!! easier to calculate normals!!!
		//create conversion. 
		
		Vector3f[] tempV = new Vector3f[DATA_SIZE*DATA_SIZE];
		int vertexCounter = 0;
		for (int y = 0; y < DATA_SIZE; y++){ //z since elevation is up = y
			for (int x = 0; x < DATA_SIZE; x++){
				tempV[vertexCounter] = new Vector3f();
				tempV[vertexCounter].x = x; 
				tempV[vertexCounter].y = y; 
				tempV[vertexCounter].z = landscape[x][y]; //maybe need to change that
				vertexCounter += 1;
			}
		}
		return tempV;
	}
	
	/**
	 * creates the color-Base from the Vector3f representation of the landscape, 
	 * @param vertexBase
	 * @return
	 */
	private Vector3f[] calculateColor(Vector3f[] vertexBase){
		Vector3f[] tempV = new Vector3f[DATA_SIZE*DATA_SIZE];
		Vector3f colorOne = new Vector3f();
		Vector3f colorTwo = new Vector3f();
		colorSum /= DATA_SIZE*DATA_SIZE; //calculate average height
		
		colorOne.x = 0;
		colorOne.y = 1;
		colorOne.z = 0;
		colorTwo.x = 1;
		colorTwo.y = 1;
		colorTwo.z = 1;
		
		for(int i = 0; i < vertexBase.length; i++){
			tempV[i] = new Vector3f();
			if (vertexBase[i].getZ() <= colorSum){tempV[i] = colorOne; } //used to be mediumHeight
			else {tempV[i] = colorTwo;}
		}
		return tempV;		
	}
	
	/**
	 * converts Vector3f Array into a float Array (x,y,z),(a,s,d), to (x,y,z,a,s,d)
	 * used for color and Vertex-Arrays
	 * @param input
	 * @return
	 */
	private float[] convertVtoFloat(Vector3f[] input){
		float[] tempV = new float[3*DATA_SIZE*DATA_SIZE];
		
		int vertexCounter = 0;
		for (int i = 0; i < input.length; i++){
			tempV[vertexCounter] = input[i].x;
			tempV[vertexCounter+1] = input[i].y;
			tempV[vertexCounter+2] = input[i].z;
			vertexCounter+=3; 
		}
		return tempV;
	}
	
	/**
	 * creates triangles for a 2d Map
	 * @return
	 */
	private int[] connectDots(){
		
		int linksLength = ((DATA_SIZE-1)*(DATA_SIZE-1)*2*3); //DataSize^2 Squares * 2 Polynoms * 3 Lines
		int[] links = new int[linksLength];
		
		int polyCounter = 0;
		
		for (int z = 0; z < DATA_SIZE-1; z++){
			for (int x = 0; x < DATA_SIZE-1; x++){
				// first poly
				links[polyCounter] = x + z*DATA_SIZE; //first row
				links[polyCounter+1] = x + z*DATA_SIZE + DATA_SIZE; //one row higher 
				links[polyCounter+2] = x + z*DATA_SIZE + 1; //first row, one higher
				// second poly
				links[polyCounter+3] = x + z*DATA_SIZE + 1; //first row
				links[polyCounter+4] = x + z*DATA_SIZE + DATA_SIZE; //one row higher, one back
				links[polyCounter+5] = x + z*DATA_SIZE + DATA_SIZE + 1; //one row higher
				polyCounter += 6;		
			}
		}
		return links;
	}
	
	/**
	 * calculates normals for shading low pro Solution
	 * @return
	 */
	private Vector3f[] calculateNormals() {
		int neighR, neighD, neighL, neighU;		
		
		int normalsLength = DATA_SIZE*DATA_SIZE;
		Vector3f[] normals = new Vector3f[normalsLength];
		
		for (int i = 0; i < normalsLength; i++){
			normals[i] = new Vector3f();
			//right neighbor
				if ((i+1)%DATA_SIZE == 0){neighR = i+1-DATA_SIZE;}
				else{neighR = i+1;}
			//left neighbor
				if (i%DATA_SIZE == 0){neighL = i-1+DATA_SIZE;}
				else{neighL = i-1;}
			//up neighbor
				if (i < DATA_SIZE){neighU = i-DATA_SIZE+normalsLength;}
				else {neighU = i-DATA_SIZE;}
			//down neighbor
				if (i > normalsLength-1-DATA_SIZE){neighD = i%DATA_SIZE;}
				else{neighD = i+DATA_SIZE;}
				
			//calculate the normal values by doing cross-product of the two neighbors
			normals[i].cross(sub(vV3f[neighL],vV3f[neighR]), sub(vV3f[neighU], vV3f[neighD]));
			normals[i].normalize();
			//adjust y to let the normals face upwards. 
			if (normals[i].z < 0){normals[i].z = -normals[i].z;}
		}
		return normals;
	}
	
	private Vector3f sub(Vector3f one, Vector3f two){
		Vector3f result = new Vector3f();
		result.x = one.x - two.x;
		result.y = one.y - two.y;
		result.z = one.z - two.z;		
		return result;
	}

	/**
	 * toStringMethod for display of heights 
	 */
	public String toString(){
		
		StringBuilder builder = new StringBuilder();
		for (float[] is : landscape) {
			   builder.append(Arrays.toString(is));
			   builder.append("\n");
			}
		String result = builder.toString();
		
		return result;
	}

	/**
	 * returns the vertex Data of the created landscape
	 * @return Vertex Data including Color and Normals
	 */
	public VertexData getVertexData() {
		VertexData vLandscape = new VertexData(vertexCount);

		vLandscape.addElement(v, VertexData.Semantic.POSITION, 3);
		vLandscape.addElement(c, VertexData.Semantic.COLOR, 3);
		vLandscape.addElement(vn, VertexData.Semantic.NORMAL, 3);
		vLandscape.addIndices(indices);
		
		return vLandscape;
	}


	@Override
	public float[] getV() {
		return this.v;
	}


	
	
}
