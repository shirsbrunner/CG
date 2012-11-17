package jrtr;

import javax.vecmath.Matrix4f;

/**
 * Stores the specification of a viewing frustum, or a viewing
 * volume. The viewing frustum is represented by a 4x4 projection
 * matrix. You will extend this class to construct the projection 
 * matrix from intuitive parameters.
 * <p>
 * A scene manager (see {@link SceneManagerInterface}, {@link SimpleSceneManager}) 
 * stores a frustum.
 */
public class Frustum {

	private Matrix4f projectionMatrix;
	
	/**
	 * Construct a default viewing frustum. The frustum is given by a 
	 * default 4x4 projection matrix.
	 */
	public Frustum()
	{
		projectionMatrix = new Matrix4f();
		float f[] = {2.f, 0.f, 0.f, 0.f, 
					 0.f, 2.f, 0.f, 0.f,
				     0.f, 0.f, -1.02f, -2.02f,
				     0.f, 0.f, -1.f, 0.f};
		projectionMatrix.set(f);
	}
	
	
	public void setFrustum(float nearPlane, float farPlane, float aspectRatio, float vFieldOfView){
		projectionMatrix = new Matrix4f();
		projectionMatrix.setM00((float) (1/(aspectRatio*Math.tan(Math.toRadians(vFieldOfView/2)))));
		projectionMatrix.setM11((float) (1/Math.tan(vFieldOfView)/2));
		projectionMatrix.setM22((nearPlane+farPlane)/(nearPlane-farPlane));
		projectionMatrix.setM32(-1);
		projectionMatrix.setM23((2*nearPlane*farPlane)/(nearPlane-farPlane));
	}
	
	
	/**
	 * Return the 4x4 projection matrix, which is used for example by 
	 * the renderer.
	 * 
	 * @return the 4x4 projection matrix
	 */
	public Matrix4f getProjectionMatrix()
	{
		return projectionMatrix;
	}
}
