package edu.wm.cs.cs301.richardbonett.ui;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import edu.wm.cs.cs301.richardbonett.falstad.BasicRobot;
import edu.wm.cs.cs301.richardbonett.falstad.Cells;
import edu.wm.cs.cs301.richardbonett.falstad.Maze;
import edu.wm.cs.cs301.richardbonett.falstad.Robot;
import edu.wm.cs.cs301.richardbonett.falstad.RobotDriver;
import edu.wm.cs.cs301.richardbonett.falstad.WallFollower;
import edu.wm.cs.cs301.richardbonett.falstad.Wizard;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.util.SparseArray;

/**
 * A GLRendererNew renders the display for the maze using Wall, Floor, Roof, and MapView objects. This class controls the display of objects in
 * the maze, as well as controlling the location of the "camera," i.e. the position of the player in the maze. An explanation of what to expect
 * from the renderer can be found in MyGLSurfaceView. 
 * 
 * Responsibilities: Render the display of the maze in various modes:
 * 						-- First Person: display a first-person view of the maze
 * 						-- Map Mode: display a "map" using a MapView with various settings (showWalls, showSolution)
 * 
 * Collaborators: Wall; handle the rendering of visible walls in first person mode, and provide locations for walls in Map Mode
 * 				  Floor; handle the rendering of visible floors in first person mode, and provide details on finding the solution in Map Mode
 * 				  Roof; handle the rendering of visible roofs in first person mode
 * 				  MapView; handle the rendering of components of a Map (overhead view of maze)
 *                Robot; used to navigate the maze in robot-mode
 *                RobotDriver; used to drive the robot in robot-mode
 *                Cells; used to provide information regarding the existence of walls
 *                GLSurfaceView (parent); used to display the rendering 
 *                
 * @author Rich
 *
 */
public class GLRendererNew implements GLSurfaceView.Renderer {
	
	private final float[] mMVPMatrix = new float[16];
	private final float[] mProjectionMatrix = new float[16];
	private final float[] mViewMatrix = new float[16];
	private final float[] mRotationMatrix = new float[16];

	private Maze maze;
	private Cells mazecells;
	private Robot robot;
	private RobotDriver driver;
	private SparseArray<Floor> floors;
	private SparseArray<Roof> roofs;
	private SparseArray<ArrayList<Wall>> wallsMap;
	private int WALL_WIDTH = 4;
	private Context context;
	private int width;
	private int height;
	private float pathLength;
	private double boundXMax;
	private double boundXMin;
	private double boundZMax;
	private double boundZMin;
    public volatile float cameraAngle = 0;
    public volatile float cameraX;
	public volatile float cameraZ = -1.5f;
	private int program;
	private FloatBuffer textureCoordsFloor;
	private int textureFloor;
	private FloatBuffer textureCoordsWall;
	private int textureWall;
	private int textureMap;
	private int textureRoof;
	private int[] cellsToDraw = {};
	private MapView map;

	private final String vertexShaderCode =
			"uniform mat4 uMVPMatrix;" +
	        "attribute vec4 vPosition;" +
			"attribute vec2 a_TexCoordinate;" +
	        "varying vec2 v_TexCoordinate;" +
	        "void main() {" +
	        "  gl_Position = uMVPMatrix * vPosition;" +
	        "  v_TexCoordinate = a_TexCoordinate;" +
	        "}";

	private final String fragmentShaderCode =
	        "precision mediump float;" +
	        "uniform sampler2D u_Texture;" +
	        "varying vec2 v_TexCoordinate;" +
	        "uniform vec4 vColor;" +
	        "void main() {" +
	        "  gl_FragColor = vColor*texture2D(u_Texture, v_TexCoordinate);" +
	        "}";
	
	/**
	 * Constructor for a GLRenderer
	 * @param context the activity containing this renderer
	 */
	public GLRendererNew(Context context) {
		this.context = context;
	}
	
	/**
	 * Specifies actions to carry out on the creation of the renderer's surface. The renderer needs to fulfill the following tasks:
	 * --Gain handles to shaders and a program. 
	 * --Prepare texture data
	 * --Prepare a MapView
	 * --Prepare a robot if in robot mode
	 */
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        // Gain references to required collaborators
        maze = Globals.maze;
        mazecells = maze.getCells();
        width = maze.getWidth();
        height = maze.getHeight();
        floors = Globals.floors.clone();
        roofs = Globals.roofs.clone();
        wallsMap = Globals.walls.clone();    
        
        // cellsToDraw specifies the nearby cells to render in drawframe. Essentially, this modification specifies all cells
        // within three cells of the cell presently occupied in the maze. 
        int[] cellsToDrawTemp = {-2 - 2*width, -1 - 2*width, -2*width, 1 - 2*width, 2 - 2*width,
        						-2 + 2*width, -1 + 2*width, 2*width, 1 + 2*width, 2 + 2*width,
        						-2 - width, 2 - width,
        						-2 + width, 2 + width,
        						-2, 2,
        						-1 - width, 1 - width, -1 + width, 1 + width,
        						-width, width, -1, 1, 0        						
        };
        cellsToDraw = cellsToDrawTemp;
       
        // Initialize variables
        pathLength = 0;
        cameraX = 2 + (maze.getCurrentPosition()[0]*WALL_WIDTH);
        cameraZ = 2 + (-(maze.getCurrentPosition()[1] - height)*WALL_WIDTH);
        evaluateBounds();

        // Prepare Texture Data. The Wall needs texture coordinates for its faces
        float[] textureDataWall = {
    			0.0f, 0.0f,
    	        0.0f, 1.0f,
    	        1.0f, 1.0f,
    	        0.0f, 0.0f,
    	        1.0f, 1.0f,
    	        1.0f, 0.0f,

    	        0.0f, 0.0f,
    	        0.0f, 1.0f,
    	        1.0f, 1.0f,
    	        0.0f, 0.0f,
    	        1.0f, 1.0f,
    	        1.0f, 0.0f,
    	        
    	        0.0f, 0.0f,
    	        0.0f, 1.0f,
    	        1.0f, 1.0f,
    	        0.0f, 0.0f,
    	        1.0f, 1.0f,
    	        1.0f, 0.0f,
    	        
    	        0.0f, 0.0f,
    	        0.0f, 1.0f,
    	        1.0f, 1.0f,
    	        0.0f, 0.0f,
    	        1.0f, 1.0f,
    	        1.0f, 0.0f,      
    	};
    	textureCoordsWall = ByteBuffer.allocateDirect(textureDataWall.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    	textureCoordsWall.put(textureDataWall).position(0);
    	
    	// Prepare the Shaders and program. Set the active program to the prepared program.
        int vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        program = createAndLinkProgram(vertexShader, fragmentShader, null);
        GLES20.glUseProgram(program);
        
        // Prepare the textures -- begin by generating four texture names
        final int[] textureNames = new int[4];
        GLES20.glGenTextures(4, textureNames, 0);
        textureFloor = textureNames[0];
        textureWall = textureNames[1];
        textureMap = textureNames[2];
        textureRoof = textureNames[3];
        
        // Texture to be used for the floor
        final BitmapFactory.Options optionsFloor = new BitmapFactory.Options();
        optionsFloor.inScaled = false;   // No pre-scaling
        final Bitmap bitmapFloor = BitmapFactory.decodeResource(context.getResources(), R.drawable.floor_texture_sandstone, optionsFloor);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureFloor);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmapFloor, 0);
        bitmapFloor.recycle();
        
        // Texture to be used for the wall
        final BitmapFactory.Options optionsWall = new BitmapFactory.Options();
        optionsWall.inScaled = false;   // No pre-scaling
        final Bitmap bitmapWall = BitmapFactory.decodeResource(context.getResources(), R.drawable.stacked_stone_med, optionsWall);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureWall);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmapWall, 0);
        bitmapWall.recycle();
        
        // Texture to be used for the Map
        final BitmapFactory.Options optionsMap = new BitmapFactory.Options();
        optionsMap.inScaled = false;
        final Bitmap bitmapMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.parchment_texture, optionsMap);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureMap);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmapMap, 0);
        bitmapMap.recycle();
        
        // Texture to be used for the roof
        final BitmapFactory.Options optionsRoof = new BitmapFactory.Options();
        optionsMap.inScaled = false;
        final Bitmap bitmapRoof = BitmapFactory.decodeResource(context.getResources(), R.drawable.roof_texture, optionsRoof);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureRoof);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmapRoof, 0);
        bitmapRoof.recycle();
        Log.v("Renderer", "Operation Type: " + Globals.operationType);
        
        // Initialize MapView
        map = new MapView();
    }

    /**
     * Controls all drawing of the frame. This method is used to update changes to the view.
     * The following tasks must be accomplished here:
     * --If in first person mode, render all nearby walls, floors, and roofs. 
     * --If in map mode, render the map and bonus options (all walls vs. nearby walls, solution) per Global settings
     */
    public void onDrawFrame(GL10 unused) {
    	// Clear the frame and generate a new model-view-projection matrix
    	float[] scratch = new float[16];
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.setRotateM(mRotationMatrix, 0, 0, 0, 0, -1.0f);
        
        // x and y are the points in the maze; they differ from the camera's position by the factor of WALL_WIDTH
        int x = (int) cameraX / WALL_WIDTH;
    	int y = height - (int) cameraZ / WALL_WIDTH;
    	
    	// If in map mode, display the map
        if (Globals.showMap) {
        	float camX = cameraX / Globals.zoom;
        	float camY = (height - cameraZ) / Globals.zoom; 
        	Matrix.setLookAtM(mViewMatrix, 0, camX, camY, 4, camX, camY, -5, 0, 1, 0);
        	Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureMap);
        	ArrayList<float[]> wallsList = new ArrayList<float[]>();
        	// Displays all walls in the map, as opposed to only nearby walls (rendered in first person view)
        	if (Globals.showWalls) {
        		for (int ndx = 0; ndx < wallsMap.size(); ndx++) {
        			if (wallsMap.get(ndx) != null) {
        				for (Wall wall : wallsMap.get(ndx)) {
        					wallsList.add(wall.getBoundaries());
        				}
        			}
        		}
        	}
        	// Displays only those walls which would be rendered in first person view
        	else {
        		for (int mod : cellsToDraw) {
            		int ndx = mod + x + width*y;
            		if (wallsMap.get(ndx) != null) {
            			for (Wall wall : wallsMap.get(ndx)) {
            				wallsList.add(wall.getBoundaries());
            			}
            		}
            	}
        	}
        	// If in solution mode, also displays the solution on the map. The solution is displayed using predetermined lines for each cell,
        	// where the line points from the middle of the cell in the direction the player ought to travel
        	FloatBuffer solutionBuffer = null;
        	float[] solutions = {};
        	if (Globals.showSolution) {
        		ArrayList<float[]> solutionList = new ArrayList<float[]>();
        		Floor floor = floors.get(x + width*y);
        		// while there exists another cell to check, add this cell to the list to display
        		while (floor.getNextSolution() != -1) {
        			if (!Globals.showWalls && (floor.getNextSolution()%width > x + 3 || floor.getNextSolution()%width < x - 3 ||
        					floor.getNextSolution()/width > y + 3 || floor.getNextSolution()/width < y - 3))
        				break;
        			solutionList.add(floor.getSolution());
        			floor = floors.get(floor.getNextSolution());
        		}
        		//Display the list of cells whose solution is relevant to the current position and settings
        		solutions = new float[solutionList.size()*6];
            	int j = 0;
            	for (float[] solution : solutionList)
            		for (float sol : solution) {
            			solutions[j] = sol;
            			j += 1;
            		}
            	solutionBuffer = ByteBuffer.allocateDirect(solutions.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            	solutionBuffer.put(solutions).position(0);
        	}
        	// Convert the list of walls to draw into a FloatBuffer and pass this to the MapView to draw
        	float[] wallBoundaries = new float[wallsList.size()*6];
        	int i = 0;
        	for (float[] bounds : wallsList)
        		for (float bound : bounds) {
        			wallBoundaries[i] = bound;
        			i += 1;
        		}
        	FloatBuffer wallsBuffer = ByteBuffer.allocateDirect(wallBoundaries.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        	wallsBuffer.put(wallBoundaries).position(0);
        	map.draw(scratch, program, wallsBuffer, wallBoundaries.length/3, camX, camY, cameraAngle, solutionBuffer, solutions.length/3);
        }
        // If not in Map Mode, draw the First Person view. 
        else {
        	// The camera moves in the first person view, while objects such as walls are considered "stationary" 
        	Matrix.setLookAtM(mViewMatrix, 0, cameraX, 0, cameraZ, (float) Math.cos(cameraAngle) + cameraX, 0f, 
        			(float) Math.sin(cameraAngle) + cameraZ, 0f, 1.0f, 0.0f);
        	Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        
        	// Draw all floors first
        	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureFloor);
        	for (int mod : cellsToDraw) {
        		int ndx = mod + x + width*y;
        		if (floors.get(ndx) != null) {
        			floors.get(ndx).draw(scratch, program);
        		}
        	}
        	// Then draw all roofs
        	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureRoof);
        	for (int mod : cellsToDraw) {
        		int ndx = mod + x + width*y;
        		if (roofs.get(ndx) != null) {
        			roofs.get(ndx).draw(scratch, program);
        		}
        	}
        	// Finally draw all walls. This order is done to minimize clipping, though it sadly is not perfect
        	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureWall);
        	for (int mod : cellsToDraw) {
        		int ndx = mod + x + width*y;
        		if (wallsMap.get(ndx) != null) {
        			for (Wall wall : wallsMap.get(ndx)) {
        				wall.draw(scratch, program, textureCoordsWall);
        			}
        		}
        	}
        }
    }
    
    /**
     * Called when the screen orientation is changed. Not advised to change screen orientation for this project, as it causes the display 
     * to appear to travel through walls, though it technically works
     */
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
    }
    
	/**
	 * Helper function to compile a shader
	 * @param shaderType 
	 * @param shaderSource
	 * @return
	 */
	private int compileShader(final int shaderType, final String shaderSource) {
		int shaderHandle = GLES20.glCreateShader(shaderType);
		
		GLES20.glShaderSource(shaderHandle, shaderSource);
		GLES20.glCompileShader(shaderHandle);
		return shaderHandle;		
	}
	
	/**
	 * Helper function to compile and link a program
	 * @param vertexShaderHandle
	 * @param fragmentShaderHandle
	 * @param attributes
	 * @return
	 */
	private int createAndLinkProgram(final int vertexShaderHandle,
			final int fragmentShaderHandle, final String[] attributes) {
		
		int programHandle = GLES20.glCreateProgram();
		GLES20.glAttachShader(programHandle, vertexShaderHandle);
		GLES20.glAttachShader(programHandle, fragmentShaderHandle);
		if (attributes != null) {
			final int size = attributes.length;
			for (int i = 0; i < size; i++) {
				GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
			}
		}
		GLES20.glLinkProgram(programHandle);
		return programHandle;
	}
	
	/**
	 * Evaluates the Boundaries for the camera within the current cell. Called to prevent the player from walking through walls, and used
	 * to determine when the player has reached the exit. 
	 */
    private void evaluateBounds() {
    	int curX = (int) cameraX / WALL_WIDTH;
        int curZ = height - (int) cameraZ / WALL_WIDTH;
        maze.setCurrentPosition(curX, curZ);
        if (maze.isAtEndPosition(curX, curZ)) {
        	Activity parentActivity = (Activity) context;
        	parentActivity.finish();
        	Globals.result = true;
        	Globals.pathLength = (int) pathLength;
        	return;
        }
    	boundZMax = mazecells.hasWallOnTop(curX, curZ) ? (height-curZ)*WALL_WIDTH + 2.7 : (height-curZ)*WALL_WIDTH + WALL_WIDTH + .1;
        boundZMin = mazecells.hasWallOnBottom(curX, curZ) ? (height-curZ)*WALL_WIDTH + 1.3 : (height-curZ)*WALL_WIDTH - .1;
        boundXMax = mazecells.hasWallOnRight(curX, curZ) ? curX*WALL_WIDTH + 2.7 : curX*WALL_WIDTH + WALL_WIDTH + .1;
        boundXMin = mazecells.hasWallOnLeft(curX, curZ) ? curX*WALL_WIDTH + 1.3 : curX*WALL_WIDTH - .1;
    }
    
    /**
     * Move the camera in the current direction by the factor of dist. If moving the camera would conflict with a boundary, instead calls 
     * evaluateBounds() and does not move the camera in the conflicting direction. If the camera has somehow already passed a boundary, as
     * is common with edges and corners, moves the camera back to an acceptable location. 
     * @param dist the distance to move the camera
     */
    public void moveCamera(float dist) {
    	double distX = Math.cos(cameraAngle)*dist;
    	double distZ = Math.sin(cameraAngle)*dist;
    	if (cameraX < boundXMin - .05 || cameraX > boundXMax + .05)
    		cameraX = (float) (cameraX < boundXMin ? boundXMin : boundXMax);
    	else if (cameraX - distX > boundXMax || cameraX - distX < boundXMin)
    		evaluateBounds();
    	else 
    		cameraX -= Math.cos(cameraAngle)*dist;
    	
    	if (cameraZ < boundZMin - .05 || cameraZ > boundZMax + .05)
    		cameraZ = (float) (cameraZ < boundZMin ? boundZMin : boundZMax);
    	else if (cameraZ - distZ > boundZMax || cameraZ - distZ < boundZMin)
    		evaluateBounds();
    	else
    		cameraZ -= Math.sin(cameraAngle)*dist;
    	pathLength += Math.abs(dist);
    	Log.v("renderer", "CameraX: " + cameraX + " |CameraZ: " + cameraZ);
    }
        
    /**
     * Helper function for the robot to move the camera through the maze. 
     * @param dist the absolute distance to move the robot
     */
    public void move(float dist) {
    	cameraX -= Math.cos(cameraAngle)*dist;
    	cameraZ -= Math.sin(cameraAngle)*dist;
    	int curX = (int) cameraX / WALL_WIDTH;
        int curZ = height - (int) cameraZ / WALL_WIDTH;
        maze.setCurrentPosition(curX, curZ);
    }
    
    /**
     * Rotate the camera by a factor of the input angle. Operates using radians, not degrees. 
     * @param angle the angle by which to rotate the camera
     */
    public void rotateCamera(float angle) {
    	cameraAngle += angle;
    	Log.v("renderer", "CameraAngle: " + cameraAngle);
    }
    
    /**
     * Pause the robot by terminating its drive thread. 
     */
    public void pause() {
    	//driver.interrupt();
    }
    
    /**
     * Resume the robotdriver by calling its drive2Exit method.  
     */
    public void resume() {
    	try {
    		driver.drive2Exit();
    	} catch (Exception e) {}
    }

}
