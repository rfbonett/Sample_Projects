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
 * A Wall handles all wall rendering in the first person perspective. Walls are essentially four rectangles, with two short ends and
 * two long ends. 
 * 
 * Responsibilities:
 * --render the wall in the appropriate position using a given texture and shader
 * 
 * Collaborators:
 * --GLRendererNew; calls this class' draw method with a model-view-project matrix, uses this class' solution data
 * --GeneratingActivity; constructs this class during maze generation
 * 
 * @author rfbonett
 *
 */
public class Wall {
	
    private FloatBuffer vertexBuffer;
    private int mPositionHandle;
    private int mColorHandle;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    static final int COORDS_PER_VERTEX = 3;
    private float[] coords;
    float color[] = { 1f, 1f, 1f, 1.0f };
    private float WALL_HEIGHT = (float) 1;
    private float WALL_THICKNESS = (float) 0.1;
    private float startX;
    private float startZ;
    private float endX;
    private float endZ;

    /**
     * Constructor for a Wall. Builds the FloatBuffer to be used in rendering the roof based on input conditions for location.
     * 
     * @param startX the x-coordinate of the starting corner
     * @param startZ the z-coordinate of the starting corner
     * @param endX the x-coordinate of the corner opposite the start
     * @param endZ the z-coordinate of the corner opposite the start
     */
    public Wall(float startX, float startZ, float endX, float endZ) {
    	this.startX = startX;
    	this.startZ = startZ;
    	this.endX = endX;
    	this.endZ = endZ;
    	
    	if (startX == endX) {
    		startX -= WALL_THICKNESS;
    		endX += WALL_THICKNESS;
    	}
    	else {
    		startZ -= WALL_THICKNESS;
    		endZ += WALL_THICKNESS;
    	}

    	float[] drawCoords = {   // in counterclockwise order:

                (startX),  2*WALL_HEIGHT, (startZ), // top Left front 0
                (startX), -WALL_HEIGHT, (startZ), // bottom left front 1
                (endX), -WALL_HEIGHT, (startZ),  // bottom right front 2
                (startX),  2*WALL_HEIGHT, (startZ), // top Left front 0
                (endX), -WALL_HEIGHT, (startZ),  // bottom right front 2
                (endX), 2*WALL_HEIGHT, (startZ), // top right front 3

                (startX),  2*WALL_HEIGHT, (endZ), // top Left front 4
                (startX), -WALL_HEIGHT, (endZ), // bottom left front 5
                (endX), -WALL_HEIGHT, (endZ),  // bottom right front 6
                (startX),  2*WALL_HEIGHT, (endZ), // top Left front 4
                (endX), -WALL_HEIGHT, (endZ),  // bottom right front 6
                (endX), 2*WALL_HEIGHT, (endZ), // top right front 7
                
                (startX),  2*WALL_HEIGHT, (endZ), // top Left front 4
                (startX), -WALL_HEIGHT, (endZ), // bottom left front 5
                (startX), -WALL_HEIGHT, (startZ), // bottom left front 1
                (startX),  2*WALL_HEIGHT, (endZ), // top Left front 4
                (startX), -WALL_HEIGHT, (startZ), // bottom left front 1
                (startX),  2*WALL_HEIGHT, (startZ), // top Left front 0
                
                (endX), 2*WALL_HEIGHT, (startZ), // top right front 3
                (endX), -WALL_HEIGHT, (startZ),  // bottom right front 2
                (endX), -WALL_HEIGHT, (endZ),  // bottom right front 6
                (endX), 2*WALL_HEIGHT, (startZ), // top right front 3
                (endX), -WALL_HEIGHT, (endZ),  // bottom right front 6
                (endX), 2*WALL_HEIGHT, (endZ), // top right front 7
       }; 
    	coords = drawCoords;
        vertexBuffer = ByteBuffer.allocateDirect(coords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(coords).position(0);
    }
    
    /**
     * Renders this wall in the frame using the input model-view-project matrix, shader program and texture coordinate info. 
     * 
     * @param mvpMatrix model-view-project matrix to use for rendering
     * @param program shader program to use for rendering
     * @param textureCoords the coordinates for the texture. Used to display the texture properly on all four sides of the wall.
     */
    public void draw(float[] mvpMatrix, int program, FloatBuffer textureCoords) {
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
        int mTextureCoordinateHandle = GLES20.glGetAttribLocation(program, "a_TexCoordinate");
        textureCoords.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, textureCoords);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 24);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
    
    /**
     * Returns the wall's "boundaries," used to render the wall in map mode, modified by the zoom setting. 
     * @return
     */
    public float[] getBoundaries() {
    	return new float[] {startX/Globals.zoom, (Globals.height - startZ)/Globals.zoom, -1.9f, endX/Globals.zoom, (Globals.height - endZ)/Globals.zoom, -1.9f};
    }
    
}
