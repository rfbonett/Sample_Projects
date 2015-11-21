package edu.wm.cs.cs301.richardbonett.ui;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import edu.wm.cs.cs301.richardbonett.falstad.Cells;
import edu.wm.cs.cs301.richardbonett.falstad.Distance;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

/**
 * A MapView handles all rendering of the map in map mode. 
 * 
 * Responsibilities:
 * --Render the map texture
 * --Render the walls on the map
 * --Render the solution on the map if given
 * 
 * Collaborators:
 * --GLRendererNew; passes in data used to render the map
 * 
 * @author rfbonett
 *
 */
public class MapView {
	private FloatBuffer vertexBuffer;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    static final int COORDS_PER_VERTEX = 3;
    float color[] = { 1f, 1f, 1f, 1.0f };
    float colorBlack[] = {0f, 0f, 0f, 1f};
    float colorGray[] = {0.5f, 0.5f, 0.5f, 1f};
    float colorRed[] = {1f, 0f, 0f, 1f};
    private int mPositionHandle;
    private int mColorHandle;
    
    /**
     * Draws the map in the frame
     * @param mvpMatrix the model-view-projection matrix to use in rendering the map
     * @param program the shader program to use in rendering the map
     * @param walls the arrays to draw for walls in the map. MapView does not determine which walls to draw, it draws all those it is given.
     * @param numWalls the number of walls to draw
     * @param camX the x position to draw the camera pointer
     * @param camY the y position to draw the camera pointer
     * @param camAngle the angle at which to draw the camera pointer, in radians
     * @param solution the solution cells to draw. MapView does not determine which solutions to draw, it draws all those it is given. 
     * @param numCells the number of cells whose solution needs to be drawn
     */
	public void draw(float[] mvpMatrix, int program, FloatBuffer walls, int numWalls, float camX, float camY, float camAngle, FloatBuffer solution, int numCells) {		
		// the coordinates of the map texture, put in a floatbuffer
		float[] drawCoords = {   // in counterclockwise order:
                camX - 12, camY + 12, -2,
                camX - 12, camY - 12, -2,
                camX + 12, camY - 12, -2,
                camX - 12, camY + 12, -2, 
                camX + 12, camY - 12, -2, 
                camX + 12, camY + 12, -2                
        }; 
        vertexBuffer = ByteBuffer.allocateDirect(drawCoords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(drawCoords).position(0);
        
        // the coordinates for the pointer representing the player's current location, put in a floatbuffer. 
        double shift = Math.PI/2;
		float[] camCoords = new float[] {
				(float) (camX + Math.cos(camAngle+shift)*.2), (float) (camY - Math.sin(camAngle+shift)*.2), -1.9f,
				(float) (camX + Math.cos(camAngle-shift)*.2), (float) (camY - Math.sin(camAngle-shift)*.2), -1.9f,
				(float) (camX + Math.cos(camAngle)*.5), (float) (camY - Math.sin(camAngle)*.5), -1.9f
		};
		FloatBuffer camBuffer = ByteBuffer.allocateDirect(camCoords.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		camBuffer.put(camCoords).position(0);
		
		// Draws the map, camera pointer, and given walls. 
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
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, walls);
        GLES20.glUniform4fv(mColorHandle, 1, colorBlack, 0);
        GLES20.glLineWidth(4f);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, numWalls);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, camBuffer);
        GLES20.glUniform4fv(mColorHandle, 1, colorGray, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        
        // If a solution was given, draws all of the solution cells
        if (solution != null) {
        	GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, solution);
        	GLES20.glUniform4fv(mColorHandle, 1, colorRed, 0);
        	GLES20.glDrawArrays(GLES20.GL_LINES, 0, numCells);
        	GLES20.glDisableVertexAttribArray(mPositionHandle);
        }
	}
}
