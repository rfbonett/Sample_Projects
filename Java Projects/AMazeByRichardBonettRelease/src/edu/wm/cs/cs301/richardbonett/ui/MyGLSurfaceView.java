package edu.wm.cs.cs301.richardbonett.ui;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * Displays the view for the maze in its first person or map mode. The procedure for displaying each mode is as follows:
 * First Person Mode: The player is placed inside of a maze, viewing walls, floor tiles, and roof tiles. The player can see at most 3 cells
 * 					  farther than the current cell. The player can freely move between walls, but cannot move into a wall. The player moves
 * 					  by pressing one finger on the screen; moving the finger towards the top of the screen will move the player forwards,
 * 					  moving the finger towards the bottom of the screen will back the player up, and moving the finger towards the left and 
 * 					  right of the screen will rotate the player left and right respectively. The speed of any of these movements is dependent on
 * 					  the distance the player moves his/her finger, but is capped at a max speed by GLRendererNew. 
 * 
 * Map Mode: The player can no longer see the walls around him, as he is inspecting a map. The map mode is activated by pressing the map option
 * 			 on the Action Bar. Pressing the Show Solution and Show Walls options will also have effects in the map mode. The default state of 
 * 			 the map mode is to show only those walls close to the player, that is those walls rendered by GLRendererNew and at most three cells
 * 			 away. By activating "Show Walls," the map will now display all of the walls in the maze. The solution is not displayed by default, 
 * 			 but when "Show Solution" is active, a route out of the maze is displayed from the player's current position. If Show Walls is not
 * 			 active, the solution will only be displayed for the cells currently displayed in the map mode. The map can be zoomed in and out by
 * 			 using a simple pinch gesture. 
 * 
 * Responsibilities: 
 * --Display the maze in either first person or map mode
 * --Control user input during PlayActivity
 * 
 * Collaborators:
 * --PlayActivity (parent); starts a MyGLSurfaceView, giving it its context. Controls changing the parameters interesting to MyGLSurfaceView
 * --GLRendererNew; renders the frame to be drawn by MyGLSurfaceView
 * --Camera (internal class); operates on a Thread to control persistent move inputs
 * --ZoomListener (internal class); listens for pinch gestures to zoom the view out or in during map mode
 * 
 * @author rfbonett
 *
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private final GLRendererNew mRenderer;
	private final float TOUCH_SCALE_FACTOR = .0001f;
	private float startX;
	private float startY;
	private float moveX;
	private float moveY;
	private Camera camera;
	private Thread cameraThread;
	private ScaleGestureDetector zoom;

	/**
	 * Constructor for a MyGLSurfaceView
	 * Starts a new renderer to handle the display of the view, as well as a new camera and zoom listener to handle touch input. 
	 * @param context
	 */
    public MyGLSurfaceView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        mRenderer = new GLRendererNew(context);
        setRenderer(mRenderer);
        camera = new Camera();
        zoom = new ScaleGestureDetector(context, new ZoomListener());
    }
    
    /**
     * Determines how to react to various touch inputs. If in robot mode, pauses/resumes the robot. Otherwise, uses the Camera to move
     * the player through the maze. 
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        zoom.onTouchEvent(e);
        switch (e.getAction()) {
        case MotionEvent.ACTION_DOWN:
        	startX = e.getX();
        	startY = e.getY();
        	Log.v("TouchEvent", "ACTION_DOWN");
        	if (Globals.operationType == 0) {
        		cameraThread = new Thread(camera);
        		cameraThread.start();
        	}
        	else {
        		mRenderer.resume();
        	}
        	break;
        case MotionEvent.ACTION_MOVE:
       		moveX = e.getX();
        	moveY = e.getY();
        	Log.v("TouchEvent", "ACTION_MOVE");
        	break;
        case MotionEvent.ACTION_UP:
        	if (Globals.operationType == 0)
        		cameraThread.interrupt();
        	Log.v("TouchEvent", "ACTION_UP");
        	break;
        }  
        return true;
    }
    
    /**
     * Helper class used to handle movement through the maze in a persistent fashion. This class allows for one touch gesture to 
     * persistently navigate the player through the maze
     * @author rfbonett
     */
    class Camera implements Runnable {
    	
    	private boolean interrupted;
    	
    	/**
    	 * Manages the thread used to propagate touch inputs to movement in the maze
    	 */
		@Override
		public void run() {
			interrupted = false;
			while (!interrupted) {
				try {
					mRenderer.moveCamera((moveY - startY) * TOUCH_SCALE_FACTOR);
					mRenderer.rotateCamera((moveX - startX) * TOUCH_SCALE_FACTOR);
					requestRender();
					Thread.sleep(10);
				} catch (InterruptedException e) {interrupt();}
			}
		}
    	
		/**
		 * Stops the camera's thread
		 */
		public void interrupt() {
			interrupted = true;
		}
    }
    
    /**
     * Helper class used to detect and react to pinch gestures, zooming the maze in and out when discovered
     * @author Rich
     *
     */
    class ZoomListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    	private float dist;
    	
    	/**
    	 * Zooms the maze in or out depending on the gesture (pinch in or out)
    	 */
    	@Override
        public boolean onScale(ScaleGestureDetector detector) {
            dist = detector.getScaleFactor();
            dist = (float) (dist > 2 ? 2 : (dist < .5 ? .5 : dist));
            Globals.zoom /= dist;
            return true;
        }
    }
}
