package jrtr;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;


public class EFrustum extends Frustum {

	private Matrix4f projectionMatrix;
	float nearPlane, farPlane, aspectRatio, vFieldofView;
	Vector3f[] corners;
	Vector3f[] normals;
	
	public EFrustum(){
		super();
		corners = new Vector3f[8];
		normals = new Vector3f[8];
	}
	
	
	public void setFrustum(float nearPlane, float farPlane, float aspectRatio, float vFieldOfView){
		this.nearPlane = nearPlane;
		this.farPlane = farPlane;
		this.aspectRatio = aspectRatio;
		this.vFieldofView = vFieldOfView;
		calculateCorners();
		calculateNormals();
		
		projectionMatrix = new Matrix4f();
		projectionMatrix.setM00((float) (1/(aspectRatio*Math.tan(Math.toRadians(vFieldOfView/2)))));
		projectionMatrix.setM11((float) (1/Math.tan(vFieldOfView)/2));
		projectionMatrix.setM22((nearPlane+farPlane)/(nearPlane-farPlane));
		projectionMatrix.setM32(-1);
		projectionMatrix.setM23((2*nearPlane*farPlane)/(nearPlane-farPlane));
	}
	
	private void calculateCorners(){
		/**
		 * Field of View: Opening angle, y direction
		 * Aspect Ratio (right/top)
		 * near: distance to near
		 * far: distance to far
		 * in Camera-Space
		 */
		
		//near Corners
		float nearZ = nearPlane;
		float farZ = farPlane;
		
		float nearuY = (float) Math.tan(vFieldofView/2)*nearZ;
		float neardY = -nearuY; 
		float faruY = (float) (Math.tan(vFieldofView/2)*farZ);
		float fardY = -faruY;
		
		float nearR = this.aspectRatio*nearuY;
		float nearL = -nearR;
		float farR = this.aspectRatio*faruY;
		float farL = -farR;
		
		//calculate the corners from this information
		
		//vector description: n: near, f: far, u: up, d: down, l: left, r: right
		Vector3f nul, ndl, ndr, nur, ful, fdl, fdr, fur;
		
		nul = new Vector3f(nearR, nearuY, nearZ);
		ndl = new Vector3f(nearR, neardY, nearZ);
		ndr = new Vector3f(nearL, neardY, nearZ);
		nur = new Vector3f(nearL, nearuY, nearZ);
		ful = new Vector3f(farR, faruY, farZ);
		fdl = new Vector3f(farR, fardY, farZ);
		fdr = new Vector3f(farL, fardY, farZ);
		fur = new Vector3f(farL, faruY, farZ);
		//numbering as in the cube-example
		corners[0] = nul;
		corners[4] = ndl;
		corners[5] = ndr;
		corners[1] = nur;
		corners[3] = ful;
		corners[7] = fdl;
		corners[6] = fdr;
		corners[2] = fur;
	}
	
	private void calculateNormals(){
		//normals should point outwards, go counterclockwise
		Vector3f  normalf = new Vector3f(),
				normalr = new Vector3f(),
				normalb = new Vector3f(),
				normall = new Vector3f(),
				normalu = new Vector3f(),
				normald = new Vector3f();
		Vector3f tempvec1 = new Vector3f();
		Vector3f tempvec2 = new Vector3f();
		
		tempvec1.sub(corners[4],corners[0]);
		tempvec2.sub(corners[1],corners[0]);
		normalf.cross(tempvec1, tempvec2);
		normals[0] = normalf;
		
		tempvec1.sub(corners[5],corners[1]);
		tempvec2.sub(corners[2],corners[1]);
		normalr.cross(tempvec1, tempvec2);
		normals[1] = normalr;
		
		tempvec1.sub(corners[6],corners[2]);
		tempvec2.sub(corners[3],corners[2]);
		normalb.cross(tempvec1, tempvec2);
		normals[2] = normalb;
		
		tempvec1.sub(corners[7],corners[3]);
		tempvec2.sub(corners[0],corners[3]);
		normall.cross(tempvec1, tempvec2);
		normals[3] = normall;
		
		tempvec1.sub(corners[1],corners[0]);
		tempvec2.sub(corners[3],corners[0]);
		normalu.cross(tempvec1, tempvec2);
		normals[4] = normalu;
		
		tempvec1.sub(corners[5],corners[4]);
		tempvec2.sub(corners[7],corners[4]);
		normald.cross(tempvec1, tempvec2);
		normals[5] = normald;
	}
	
	public boolean insideTest(BoundingSphere sphere){
		
		return true;
	}
}
