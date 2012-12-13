package jrtr;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import buildingBlocks.BoundingSphere;

/**
 * this is now a wrapper for a frustum
 * @author Boss
 *
 */

public class EFrustum {

	float nearPlane, farPlane, aspectRatio, vFieldofView;
	Vector3f[] corners;
	Vector3f[] normals;
	Vector3f[] p;
	private Frustum frustum;
	
	public EFrustum(Frustum frustum){
		this.frustum = frustum;
		corners = new Vector3f[8];
		normals = new Vector3f[6];
		p = new Vector3f[6];
	}
	
	public Frustum getFrustum(){
		return this.frustum; 
	}
	
	
	public void setFrustum(float nearPlane, float farPlane, float aspectRatio, float vFieldOfView){
		this.nearPlane = nearPlane;
		this.farPlane = farPlane;
		this.aspectRatio = aspectRatio;
		this.vFieldofView = vFieldOfView;
		this.calculateCorners();
		this.calculateNormals();
		
		this.frustum.setFrustum(nearPlane, farPlane, aspectRatio, vFieldOfView);
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
		
		p[0] = corners[0];
		p[1] = corners[1];
		p[2] = corners[2];
		p[3] = corners[3];
		p[4] = corners[0];
		p[5] = corners[4];
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
		Vector3f center = sphere.getCenter();
		float radius = sphere.getRadius();
		boolean inside = false;
		Vector3f distance = new Vector3f();
		
		for (int i = 0; i<6; i++){
			Vector3f normal = normals[i];
			distance.sub(p[i],center);
			if (distance.dot(normal)<radius){
				inside = true;
			} 
			
		}
		return inside;
	}
}
