package edu.wm.cs.cs301.richardbonett.ui;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

/**
 * A Roof handles all roof (ceiling may be more accurate) rendering in the first person perspective
 * 
 * Responsibilities:
 * --render the roof in the appropriate position using a given texture and shader
 * 
 * Collaborators:
 * --GLRendererNew; calls this class' draw method with a model-view-project matrix, uses this class' solution data
 * --GeneratingActivity; constructs this class during maze generation
 * 
 * @author rfbonett
 *
 */
public class Roof {
    private FloatBuffer vertexBuffer;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    static final int COORDS_PER_VERTEX = 3;
    private float[] coords;
    float color[] = { 1f, 1f, 1f, 1.0f };
    private int mPositionHandle;
    private int mColorHandle;
    
    /**
     * Constructor for a Roof. Builds the FloatBuffer to be used in rendering the roof based on input conditions for location.
     * 
     * @param startX the x-coordinate of the starting corner
     * @param startZ the z-coordinate of the starting corner
     * @param endX the x-coordinate of the corner opposite the start
     * @param endZ the z-coordinate of the corner opposite the start
     * @param height - the y level at which to draw the floor
     */
    public Roof(float startX, float startZ, float endX, float endZ, float height) {
 
    	float[] drawCoords = {   // in counterclockwise order:
                (startX),  height, (startZ), // top Left front
                (startX), height, (endZ), // bottom left front
                (endX), height, (endZ),  // bottom right front
                (startX),  height, (startZ), // top Left front
                (endX), height, (endZ),  // bottom right front
                (endX), height, (startZ), // top right front
        }; 
    	coords = drawCoords;
        vertexBuffer = ByteBuffer.allocateDirect(coords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(coords).position(0);
    }
    
    /**
    * Renders this roof in the frame using the input model-view-project matrix and shader program
    * @param mvpMatrix model-view-project matrix to use for rendering
    * @param program shader program to use for rendering
    */
    public void draw(float[] mvpMatrix, int program) {
        int mMVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(program, "vColor");
        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
