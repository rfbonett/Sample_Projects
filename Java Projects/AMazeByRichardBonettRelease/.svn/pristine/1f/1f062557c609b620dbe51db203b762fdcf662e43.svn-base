package edu.wm.cs.cs301.richardbonett.ui;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import edu.wm.cs.cs301.richardbonett.falstad.Cells;
import edu.wm.cs.cs301.richardbonett.falstad.Distance;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * A floor handles all floor rendering in the first person perspective, as well as containing data for the solution in map mode. 
 * 
 * Responsibilities:
 * --render the floor in the appropriate position using a given texture and shader
 * --provide details concerning the solution out of the maze from this cell
 * 
 * Collaborators:
 * --GLRendererNew; calls this class' draw method with a model-view-project matrix, uses this class' solution data
 * --GeneratingActivity; constructs this class during maze generation
 * --Distance & Cells; used to determine the solution out of this cell
 * 
 * @author rfbonett
 *
 */
public class Floor {

    private FloatBuffer vertexBuffer;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    static final int COORDS_PER_VERTEX = 3;
    private float[] coords;
    float color[] = { 1f, 1f, 1f, 1.0f };
    private int mPositionHandle;
    private int mColorHandle;
    private float solutionStartX;
    private float solutionStartZ;
    private float solutionEndX;
    private float solutionEndZ;
    private int nextSolution;
    
    /**
     * Constructor for a Floor. Builds the floor's FloatBuffer for use in rendering, as well as determining how to render the solution
     * out of this floor's cell in the maze
     * 
     * @param startX the x-coordinate of the starting corner
     * @param startZ the z-coordinate of the starting corner
     * @param endX the x-coordinate of the corner opposite the start
     * @param endZ the z-coordinate of the corner opposite the start
     * @param height - the y level at which to draw the floor
     * @param x the maze's x coordinate for this floor's cell
     * @param y the maze's y coordinate for this floor's cell
     */
    public Floor(float startX, float startZ, float endX, float endZ, float height, int x, int y) {
    	// The coordinates to draw for two triangles (completing a square). Placed in a FloatBuffer for efficiency
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
        
        // Used to determine the solution out of this cell
        height = Globals.maze.getHeight();
        int width = Globals.maze.getWidth();
        int dist = Globals.mazedists.getDistance(x, y);
        solutionStartX = solutionEndX = (startX + endX)/2;
        solutionStartZ = solutionEndZ = height - ((startZ + endZ)/2);
        if (dist != 1) {
        	if (Globals.mazecells.hasNoWallOnTop(x, y) && Globals.mazedists.getDistance(x, y - 1) < dist) {
        		solutionEndX = (startX + endX)/2;
        		solutionEndZ = height - endZ;
        		nextSolution = x + (y-1)*width;
        	}
        	else if (Globals.mazecells.hasNoWallOnBottom(x, y) && Globals.mazedists.getDistance(x, y + 1) < dist) {
        		solutionEndX = (startX + endX)/2;
        		solutionEndZ = height - startZ;
        		nextSolution = x + (y + 1)*width;
        	}
        	else if (Globals.mazecells.hasNoWallOnRight(x, y) && Globals.mazedists.getDistance(x + 1, y) < dist) {
        		solutionEndX = endX;
        		solutionEndZ = height - ((startZ + endZ)/2);
        		nextSolution = x + 1 + (y*width);
        	}
        	else if (Globals.mazecells.hasNoWallOnLeft(x, y) && Globals.mazedists.getDistance(x - 1, y) < dist) {
        		solutionEndX = startX;
        		solutionEndZ = height - ((startZ + endZ)/2);
        		nextSolution = x - 1 + (y*width);
        	}
        }
        else
        	nextSolution = -1;
    }
    
    /**
     * Renders this floor in the frame using the input model-view-project matrix and shader program
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
    
    /**
     * Returns the predetermined data for the solution out of this cell, modified by the zoom setting
     * @return the vector used to draw this floor's cell's solution in map mode
     */
    public float[] getSolution() {
    	return new float[] {solutionStartX/Globals.zoom, solutionStartZ/Globals.zoom, -1.9f, solutionEndX/Globals.zoom, solutionEndZ/Globals.zoom, -1.9f};
    }
    
    /**
     * Returns the index of the next floor to be considered in solution drawing. Returns -1 if this is the last floor. 
     * @return the index of the next floor whose solution ought to be drawn
     */
    public int getNextSolution() {
    	return nextSolution;
    }
}
