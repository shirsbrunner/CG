package jrtr;


import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;
import javax.media.opengl.*;


public class CubeMap {
    private GL3 gl;
    private int tid;
 
    public CubeMap(GL3 gl) {
        this.gl = gl;
    }
 
    public void bind() {
        gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, tid);
        gl.glTexGeni(GL.GL_S, GL.GL_TEXTURE_GEN_MODE, GL.GL_NORMAL_MAP);
        gl.glTexGeni(GL.GL_T, GL.GL_TEXTURE_GEN_MODE, GL.GL_NORMAL_MAP);
        gl.glTexGeni(GL.GL_R, GL.GL_TEXTURE_GEN_MODE, GL.GL_NORMAL_MAP);
        gl.glEnable(GL.GL_TEXTURE_GEN_S);
        gl.glEnable(GL.GL_TEXTURE_GEN_T);
        gl.glEnable(GL.GL_TEXTURE_GEN_R);
        gl.glEnable(GL.GL_TEXTURE_CUBE_MAP);
    }
 
    public boolean loadFromFile(String[] filenames) {
        if (filenames.length < 6)
            return false;
        try {
            IntBuffer idBuffer = IntBuffer.allocate(1);
            gl.glGenTextures(1, idBuffer);
            tid = idBuffer.get();
            gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, tid);
 
            for (int i = 0; i < 6; i++) {
            	String tmpFile = filenames[i];
        		
            	//the following is from GL Texture mixed with the tutorial
            	BufferedImage image;
        		
        		File f = new File(tmpFile);
        		image = ImageIO.read(f);
        		
        		int w = image.getWidth();
        		int h = image.getHeight();
        		
        		IntBuffer buf = getData(image);
	        	gl.glTexImage2D(GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0,
                        GL.GL_RGB8, w, h, 0,
                        GL.GL_RGB, GL.GL_UNSIGNED_BYTE, buf); //pixel-file is loaded as buffer
 
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
    
    IntBuffer getData(BufferedImage img)
	{
		IntBuffer buf = IntBuffer.allocate(img.getWidth()*img.getHeight());
		
		for(int i=0; i<img.getHeight(); i++)
		{
			for(int j=0; j<img.getWidth(); j++)
			{
				// We need to shuffle the RGB values to pass them correctly to OpenGL. 
				int in = img.getRGB(j,i);
				int out = ((in & 0x000000FF) << 16) | (in & 0x0000FF00) | ((in & 0x00FF0000) >> 16);
				buf.put((img.getHeight()-i-1)*img.getWidth()+j, out);
			}
		}
		return buf;
	}
}