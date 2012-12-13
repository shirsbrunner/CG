package buildingBlocks;

import java.util.LinkedList;

import javax.vecmath.*;

import jrtr.VertexData;

public class RotationBody implements IBasicShape {

	Vector3f[] basePoints, bezierPoints, bezierNormals;
	LinkedList<Vector3f> lNormals, lRotBodyPoints, lPolygons;
	LinkedList<Vector2f> lTextures;
	
	private float[] v; //vertex positions, every vertex has 3 records
	private float[] vt; //vertextexture positions, every vertex has 3 records
	private float[] c; //vertex colors, same length as v[]
	private float[] n; //vertex normals
	private int[] indices; //every polygon has 3, connects elements of v[]
	private int numberOfSegments;
	private int bezierDetail;
	private int cubes; //number of bezier cubes


	/**
	 * Create a rotation Body from Bezier Curves
	 * @param inputBasePoints a Vector2f[] with the Base-Points for Bezier curve. Length should be in the form (n-1)*3+4
	 * @param detail denseness of the generated bezier-curve the higher, the more detail
	 * @param numberOfSegments how many times is the curve built around the rotation center, the more the higher
	 * @param cubes the number of cubic segments
	 */
	public RotationBody(Vector3f[] inputBasePoints, int detail, int numberOfSegments, int cubes){
		this.basePoints = inputBasePoints; 
		this.numberOfSegments = numberOfSegments;
		this.bezierDetail = detail;
		this.cubes = cubes;
		this.lNormals = new LinkedList<Vector3f>();
		this.lRotBodyPoints = new LinkedList<Vector3f>();
		this.lPolygons = new LinkedList<Vector3f>();
		this.lTextures = new LinkedList<Vector2f>();
		calculateCurve();
		rotate(numberOfSegments);
		createPolygons();
		calcTextures();
		calcArrays();
		System.out.println("finished");
	}
	
	/**
	 * From Vertices to textures....
	 * If I get it right, we just calculate a linear net here...,
	 * If it's similar to a sphere we would do the backmapping of a square map on a sphere
	 * 
	 */
	private void calcTextures() {
		//use bezierDetail
		//use number of Segments
		
		//TODO this does something wrong, maybe not for all cubic segments?
		Vector2f texCoord;
	
			for (int i = 0; i< bezierDetail*cubes; i+=1){
				for (int s = 0; s < numberOfSegments; s+=1){
					texCoord = new Vector2f(s/(float)numberOfSegments,i/(float)(bezierDetail*cubes));
					lTextures.add(texCoord);
				}
			}		
		
	}

	/**
	 * transforms lists to arrays
	 */
	private void calcArrays() {
		v = new float[lRotBodyPoints.size()*3];
		int i = 0;
		for (Vector3f point : lRotBodyPoints){
			v[i] = point.x;
			v[i+1] = point.y;
			v[i+2] = point.z;
			i+=3;
		}
		
		vt = new float[lTextures.size()*2];//3];
		i = 0;
		for (Vector2f tex : lTextures){
			vt[i] = tex.x;
			vt[i+1] = tex.y;
			//vt[i+2] = tex.z;
			i+=2;//3;
		}
		
		
		c = new float[lRotBodyPoints.size()*3];
		for (i = 0; i<lRotBodyPoints.size()*3;i++){
			c[i] = i%2;
		}
		
		n = new float[lNormals.size()*3];
		i = 0;
		for (Vector3f normal : lNormals){
			n[i] = normal.x;
			n[i+1] = normal.y;
			n[i+2] = normal.z;
			i+=3;
		}
		
		indices = new int[lPolygons.size()*3];
		i = 0;
		for (Vector3f poly : lPolygons){
			indices[i] = (int) poly.x;
			indices[i+1] = (int) poly.y;
			indices[i+2] = (int) poly.z;
			i+=3;
		}
		
	}

	/**
	 * interpolates dots and creates the bezier-curve that is later on turned
	 * normals are calculated here as well
	 */
	private void calculateCurve(){		
		float bezierIncrement = 1f/((float) bezierDetail);
		//first dot of bezier and his normal
		lRotBodyPoints.add(basePoints[0]);
		Vector3f normal = calcNormal(basePoints[0], basePoints[1]);
		lNormals.add(normal);
		
		//mid points
		Vector3f tempPoint = new Vector3f();
		for (int i = 0; i < basePoints.length-3; i+=3){ //for every cubic segment
			for(float t = bezierIncrement; t<1; t+=bezierIncrement){ //do number of segment times, first point allready added, last later
				tempPoint = new Vector3f();
				tempPoint = cubicBezier(basePoints[i],basePoints[i+1],basePoints[i+2],basePoints[i+3],t);
				lRotBodyPoints.add(tempPoint); 
			}
		}
		
		//last Point and his normal
		lRotBodyPoints.add(basePoints[basePoints.length-1]);
		normal = calcNormal(basePoints[basePoints.length-2], basePoints[basePoints.length-1]);
		lNormals.add(normal);
		
		//convert LinkedList into Array, for easier handling, maybe not needed... or later
		bezierPoints = new Vector3f[lRotBodyPoints.size()];
		int i = 0;
		for (Vector3f vector: lRotBodyPoints){
			bezierPoints[i] = vector;
			i++;
		}
		
		//create a base for all the normals
		bezierNormals = new Vector3f[lNormals.size()];
		i = 0; 
		for (Vector3f vector: lNormals){
			bezierNormals[i] = vector;
			i++;
		}
		lRotBodyPoints.clear();
		lNormals.clear();
	}

	
	/**
	 * calculates the normal on vector between two given points
	 * @param point1
	 * @param point2
	 * @return
	 */
	private Vector3f calcNormal(Vector3f point1, Vector3f point2) {
		Vector3f tangent = new Vector3f();
		tangent.sub(point1, point2);
		
		Vector3f normal = new Vector3f(-tangent.y, tangent.x,0);
		normal.normalize();
		System.out.println(normal);
		return normal;
	}

	/**
	 * calculates a cubic bezier interpolation out of given 4 points and a parameter t
	 * @param point0
	 * @param point1
	 * @param point2
	 * @param point3
	 * @param t
	 * @return
	 */
	private Vector3f cubicBezier(Vector3f point0, Vector3f point1, Vector3f point2, Vector3f point3, float t){
		Vector3f q0 = lerp(point0, point1, t);
		Vector3f q1 = lerp(point1, point2, t);
		Vector3f q2 = lerp(point2, point3, t);
		
		Vector3f r0 = lerp(q0, q1, t);
		Vector3f r1 = lerp(q1, q2, t);
		Vector3f cubicBezierPoint = lerp(r0, r1, t);
		
		//calculate the normal on the dot TODO maybe these need to be inverted
		Vector3f normal = calcNormal(r0, r1);
		lNormals.add(normal);
		
		return cubicBezierPoint;
	}
	
	/**
	 * linearely interpolates the point t on the line between Point1 and Point2
	 * @param point0 start-point
	 * @param point1 end-point
	 * @param t should be between 1/0
	 */
	private Vector3f lerp(Vector3f point0, Vector3f point1, float t){
		Vector3f interpolatedT = new Vector3f();
		assert(t>=0 && t<=1);
		interpolatedT.x = (1-t)*point0.x+t*point1.x; 
		interpolatedT.y = (1-t)*point0.y+t*point1.y; 
		interpolatedT.z = (1-t)*point0.z+t*point1.z; 
		return interpolatedT;
	}
	
	/**
	 * this rotates the curve around the Y-Axiss
	 * @param numberOfSegments: the number of times the curve is projected around the X-Axis
	 */
	private void rotate(int numberOfSegments){
		for (int i = 0; i< bezierPoints.length; i++){
			for (int segCounter = 0; segCounter < numberOfSegments; segCounter++){
				//calculate Points
				Vector3f baseBezier = new Vector3f(bezierPoints[i]);
				float radius = baseBezier.x;
				baseBezier.x =(float) (radius*Math.cos(Math.toRadians(360*segCounter/numberOfSegments)));//tempAngle))); //x-value important, cos/sin needs Radians
				//y value is not changed, since we turn around y
				baseBezier.z=(float) (radius*Math.sin(Math.toRadians(360*segCounter/numberOfSegments)));//tempAngle)));  //z-value
				lRotBodyPoints.add(baseBezier);
				
				//calculate Normals
				Vector3f baseNormal = new Vector3f(bezierNormals[i]);
				radius = baseNormal.x;
				baseNormal.x =(float) (radius*Math.cos(Math.toRadians(360*segCounter/numberOfSegments)));//tempAngle))); //x-value important, cos/sin needs Radians
				//y value is not changed
				baseNormal.z=(float) (radius*Math.sin(Math.toRadians(360*segCounter/numberOfSegments)));//tempAngle)));  //z-value
				lNormals.add(baseNormal);
			}
		}		
		/*
		//calculate upper/lower center-dot
		Vector3f topCenter = new Vector3f(0, bezierPoints[0].y, 0);
		lRotBodyPoints.add(topCenter);
		
		Vector3f bottomCenter = new Vector3f(0, bezierPoints[bezierPoints.length-1].y, 0);
		lRotBodyPoints.add(bottomCenter);
		
		Vector3f topNormal = new Vector3f(0,1,0);
		Vector3f bottomNormal = new Vector3f(0,-1,0);
		
		if (topCenter.y > bottomCenter.y){ //"top" is bigger than "bottom", ergo "tops" Normal points up
			lNormals.add(topNormal);
			lNormals.add(bottomNormal);
		}
		else { //here "tops" normal points down
			lNormals.add(bottomNormal);
			lNormals.add(topNormal);
		}
		*/
	}
	
	/**
	 * link polygons as in Cylinder or landscape
	 */
	private void createPolygons(){ //TODO this should be right, but doesn't wrap corners yet, furthermore the top/bottom-circles are not implemented
		/**
		 * fill in the indices[]
		 * create triangle mesh & tex coordinates P13, similar like in assignment 1, 
		 * should not depend on the control-points. Every rotational object is from a topological object a sphere.
		 */
		//number of Segments around the body
		int nOfPoints = bezierPoints.length;
		lPolygons = new LinkedList<Vector3f>();
		for (int n = 0; n<nOfPoints-1; n++){
			for (int s = 0; s < numberOfSegments; s++){ //this is for sure wrong numberOfSegments-3
				//this always calculates a square
				int edgeOne = s+n*numberOfSegments; //ul
				int edgeTwo = edgeOne+numberOfSegments; //ll, is number of Segments bigger than the first
				int edgeTree = edgeOne+1; //ur, is to the right of account for border condition...
				
				if (edgeTree % numberOfSegments == 0){ //corner-condition
					edgeTree = edgeTree-numberOfSegments;}
				
				int edgeFour = edgeTwo+1; //lr, is to the right of 
				
				if (edgeFour% numberOfSegments == 0){ //corner-condition
					edgeFour = edgeFour-numberOfSegments;}
				
				lPolygons.add(new Vector3f(edgeOne, edgeTwo, edgeTree));
				lPolygons.add(new Vector3f(edgeTwo, edgeFour, edgeTree));
			}
		}
	}
	
	
	@Override
	public VertexData getVertexData() {
		int vertexOutput = lRotBodyPoints.size(); //Creates error if this is wrong! how many vertices do you have
		
		VertexData vertexData = new VertexData(vertexOutput); //TODO is this right?
		vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
		vertexData.addElement(v, VertexData.Semantic.POSITION, 3);
		vertexData.addElement(n, VertexData.Semantic.NORMAL, 3);
		vertexData.addElement(vt, VertexData.Semantic.TEXCOORD, 2);
		vertexData.addIndices(indices);
		return vertexData;
	}

	@Override
	public float[] getV() {
		return this.v;
	}

}
