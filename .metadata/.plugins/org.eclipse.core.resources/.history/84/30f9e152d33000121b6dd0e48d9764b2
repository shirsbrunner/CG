package buildingBlocks;
import jrtr.VertexData;


public class ExampleCube implements IBasicShape {

	//shirsbrunner
	private float[] v; //vertex positions, every vertex has 3 records
	private float[] c; //vertex colors, same length as v[]
	private int[] indices; //every polygon has 3, connects elements of v[]
	private int countOfVertices = 24;
	//shirsbrunner
	
	public ExampleCube(){
		
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
		
		// The triangles (three vertex indices for each triangle)
		int indices[] = {0,2,3, 0,1,2,			// front face
						 4,6,7, 4,5,6,			// left face
						 8,10,11, 8,9,10,		// back face
						 12,14,15, 12,13,14,	// right face
						 16,18,19, 16,17,18,	// top face
						 20,22,23, 20,21,22};	// bottom face
		
		this.v = v;
		this.c = c;
		this.indices = indices;
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
	
	/* (non-Javadoc)
	 * @see IShape#getVertexData()
	 */
	@Override
	public VertexData getVertexData() {

		// Construct a data structure that stores the vertices, their
		// attributes, and the triangle mesh connectivity
		VertexData vertexData = new VertexData(24);
		vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
		vertexData.addElement(v, VertexData.Semantic.POSITION, 3);
		
		vertexData.addIndices(indices);		
		return vertexData;
	}
	
	
	@Override
	public int getVertexCount() {
		return 24;
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
