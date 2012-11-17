package buildingBlocks;
import jrtr.VertexData;

public interface IBasicShape {

	public abstract VertexData getVertexData();
	public abstract int getVertexCount();
	public abstract float[] getV();
	public abstract float[] getC();
	public abstract int[] getIndices();
	void moveZ(float z);
	void moveY(float y);
	void moveX(float x);

}