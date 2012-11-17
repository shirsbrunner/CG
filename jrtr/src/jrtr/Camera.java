package jrtr;

import javax.vecmath.*;

/**
 * Stores the specification of a virtual camera. You will extend
 * this class to construct a 4x4 camera matrix, i.e., the world-to-
 * camera transform from intuitive parameters. 
 * 
 * A scene manager (see {@link SceneManagerInterface}, {@link SimpleSceneManager}) 
 * stores a camera.
 */
public class Camera {

	private Matrix4f cameraMatrix;
	
	/**
	 * Construct a camera with a default camera matrix. The camera
	 * matrix corresponds to the world-to-camera transform. This default
	 * matrix places the camera at (0,0,10) in world space, facing towards
	 * the origin (0,0,0) of world space, i.e., towards the negative z-axis.
	 */
	public Camera()
	{
		cameraMatrix = new Matrix4f();
		float f[] = {1.f, 0.f, 0.f, 0.f,
					 0.f, 1.f, 0.f, 0.f,
					 0.f, 0.f, 1.f, -10.f,
					 0.f, 0.f, 0.f, 1.f};
		cameraMatrix.set(f);
	}
	
	/**
	 * Calculates a new Camera Matrix
	 * @param centerOfProjection where the Camera is at
	 * @param lookAtPoint where the Camera is looking at
	 * @param upVector what is "up" for the camera (in world coordinates)
	 */
		public void setCameraMatrix(Vector3f centerOfProjection, Vector3f lookAtPoint, Vector3f upVector){
			
		Matrix4f newCameraMatrix = new Matrix4f();
		newCameraMatrix.setIdentity();
		Vector3f zC = new Vector3f();
		Vector3f xC = new Vector3f();
		Vector3f yC = new Vector3f();
		
		//calculating zC		
		zC.sub(centerOfProjection, lookAtPoint);
		if (zC.length()!=0){	
			zC.normalize();
		}
		
		//calculating xC
		xC.cross(upVector,zC);
		if (xC.length()!=0){	
			xC.normalize();
		}
			
		//calculating yC
		yC.cross(zC, xC);
		if (yC.length()!=0){	
			yC.normalize();
		}

		//create a 3x3 Matrix
		Matrix3f tempTransformationMatrix = new Matrix3f();
		tempTransformationMatrix.setColumn(0, xC);
		tempTransformationMatrix.setColumn(1, yC);
		tempTransformationMatrix.setColumn(2, zC);
		
		//transpose the 3x3 Matrix
		tempTransformationMatrix.transpose();
		newCameraMatrix.set(tempTransformationMatrix);

		
		Matrix4f movementMatrix = new Matrix4f();
		movementMatrix.setIdentity();
		movementMatrix.setM03(-centerOfProjection.x);
		movementMatrix.setM13(-centerOfProjection.y);
		movementMatrix.setM23(-centerOfProjection.z);
		
		
		Matrix4f view = new Matrix4f();
		view.mul(newCameraMatrix, movementMatrix);
		this.cameraMatrix = view;
	}

	/**
	 * sets the new CameraMatrix
	 * @param newCameraMatrix the new CameraMatrix to set to
	 */
	public void setCameraMatrix(Matrix4f newCameraMatrix){
		this.cameraMatrix = newCameraMatrix;
	}
	
	/**
	 * Return the camera matrix, i.e., the world-to-camera transform. For example, 
	 * this is used by the renderer.
	 * 
	 * @return the 4x4 world-to-camera transform matrix
	 */
	public Matrix4f getCameraMatrix()
	{
		return cameraMatrix;
	}
}
