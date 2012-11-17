package buildingBlocks;

import jrtr.VertexData;

/**
 * Combines two shapes, does not cut polynoms or add new vertices
 * @author shirsbrunner
 *
 */

public class ShapeCombinator implements IBasicShape {

	private int oneVertexCount;
	private int newVertexCount;
	
	private float[] v; //vertex positions, every vertex has 3 records
	private float[] c; //vertex colors, same length as v[]
	private int[] indices; //every polygon has 3, connects elements of v[]
	
	
	public ShapeCombinator(IBasicShape shapeOne, IBasicShape shapeTwo){
		oneVertexCount = shapeOne.getVertexCount();
		newVertexCount = oneVertexCount + shapeTwo.getVertexCount();
		
		this.v = new float[shapeOne.getV().length+shapeTwo.getV().length];
		this.c = new float[shapeOne.getC().length+shapeTwo.getC().length];
		this.indices = new int[shapeOne.getIndices().length+shapeTwo.getIndices().length];
		
		//add first shape
		float[] tempV = shapeOne.getV();
		float[] tempC = shapeOne.getC();
		int[] tempIndices = shapeOne.getIndices();
		
		for (int i = 0; i<tempV.length; i++){
			v[i] = tempV[i];}
		
		for (int i = 0; i<tempC.length; i++){
			c[i] = tempC[i];}
		
		for (int i = 0; i<tempIndices.length; i++){
			indices[i] = tempIndices[i];}

		//add second shape
		tempV = shapeTwo.getV();
		tempC = shapeTwo.getC();
		tempIndices = shapeTwo.getIndices();

	
		for (int i = 0; i<tempV.length; i++){
			v[oneVertexCount*3+i] = tempV[i];}
		
		for (int i = 0; i<tempC.length; i++){
			c[oneVertexCount*3+i] = tempC[i];}
		
		//indices have to be adjusted
		for (int i = 0; i < tempIndices.length; i++){
			tempIndices[i]+=oneVertexCount;
			indices[shapeOne.getIndices().length+i] = tempIndices[i];}	
	}
	
	/**
	 * moves into x direction, before combination
	 * @param x the distance to move
	 */
	@Override
	public void moveX(float x){
		for (int i = 0; i < newVertexCount*3; i++){
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
		for (int i = 0; i < newVertexCount*3; i++){
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
		for (int i = 0; i < newVertexCount*3; i++){
			v[i+2] += z;
			i+=2;
		}
	}
	
	@Override
	public VertexData getVertexData() {
		int vertexOutput = newVertexCount; //maybe the wrong variable
		
		VertexData vertexData = new VertexData(vertexOutput);
		vertexData.addElement(c, VertexData.Semantic.COLOR, 3); //color consisting of 3 elements
		vertexData.addElement(v, VertexData.Semantic.POSITION, 3); //position consisting of 3 elements

		vertexData.addIndices(indices);
		return vertexData;
	}
	
	@Override
	public int getVertexCount() {
		return newVertexCount;
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
