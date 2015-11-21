package edu.wm.cs.cs301.richardbonett.ui;

import java.util.ArrayList;

import edu.wm.cs.cs301.richardbonett.falstad.BasicRobot;
import edu.wm.cs.cs301.richardbonett.falstad.Cells;
import edu.wm.cs.cs301.richardbonett.falstad.Maze;
import edu.wm.cs.cs301.richardbonett.falstad.Robot;
import edu.wm.cs.cs301.richardbonett.falstad.RobotDriver;
import edu.wm.cs.cs301.richardbonett.falstad.Wizard;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.VideoView;

/**
 * GeneratingActivity handles the maze generation, providing a graphical representation of the generation's progress. 
 * Pressing the back button will return to AMazeActivity, the title screen. The GeneratingActivity needs to fully generate
 * a new maze based on settings from AMazeActivity. GeneratingActivity also prepares all of the objects needed by the GLRendererNew
 * to ensure their prompt access upon its launch. 
 * 
 * Responsibilities: handle maze generation, provide graphical representation of generation progress
 * Collaborators: AMazeActivity; provides information via intent for settings for maze generation, 
 * 				  PlayActivity; called via an intent after the maze has been generated,
 * 				  Maze; used to generate a maze with the specified parameters
 * @author rfbonett
 *
 */
public class GeneratingActivity extends Activity implements Runnable {

	private ProgressBar generationProgress;
	private int progress = 0;
	private Thread generationThread;
	private String ROBOT = "ROBOT";
	private Maze maze;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_generating);
		generationProgress = (ProgressBar) findViewById(R.id.generation_progress_bar);
		Log.v("GeneratingActivity", "GeneratingActivity Started with Data: ");
		Log.v("GeneratingActivity", "--Generation Algorithm/File :" + Globals.generationAlgorithm);
		Log.v("GeneratingActivity", "--Operation Type: " + Globals.operationType);
		Log.v("GeneratingActivity", "--Difficulty: " + Globals.difficulty);
		generateMaze();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.generating, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Begins a new thread to generate the maze and renderer components.
	 */
	public void generateMaze() {
		generationThread = new Thread(this);
		generationThread.start();	
	}
	
	/**
	 * Handles maze generation and represents generation progress by updating the progress bar. 
	 * When maze generation is complete, begins PlayActivity. Progress is determined by the stage of the maze generation's progress, 
	 * and by the number of developed renderer objects compared to the total. The renderer stage often takes longer than the maze
	 * generation, so it has been allotted 3/4 of the progress time (arbitrarily sure, but it usually works). 
	 */
	public void run() {
		try {
			// Set renderer settings to default values
			Globals.result = false;
			Globals.pathLength = 0;
			Globals.showMap = false;
			Globals.showWalls = false;
			Globals.showSolution = false;
			
			// Generate a new maze
			Log.v("GeneratingActivityNew", "Starting Maze Generation");
			maze = new Maze(Globals.generationAlgorithm);
			maze.build(Globals.difficulty);
			while (!maze.ready()) {
				progress = maze.getProgress();
				generationProgress.setProgress(progress/4);
			}
			Globals.maze = maze;
			Globals.mazecells = maze.getCells();
			Globals.mazedists = maze.getDists();
			Thread.sleep(100); // check if interrupted
			
			// Generate the renderer objects
	        int width = maze.getWidth();
	        int height = maze.getHeight();
	        Globals.height = maze.getHeight();
	        Globals.zoom = 4;
	        int WALL_WIDTH = 4;
	        final SparseArray<Floor> floors = new SparseArray<Floor>();
	        final SparseArray<Roof> roofs = new SparseArray<Roof>();
	        final SparseArray<ArrayList<Wall>> walls = new SparseArray<ArrayList<Wall>>();
	        for (int x = 0; x < width; x++) {
	        	for (int y = 0; y < height; y++) {
	        		generationProgress.setProgress(25 + (75*x/width));
	        		floors.put(x + y*width,  new Floor(WALL_WIDTH*x, WALL_WIDTH*(height-y), WALL_WIDTH*(x+1), WALL_WIDTH*((height-y)+1), -1, x, y));
	        		roofs.put(x + y*width,  new Roof(WALL_WIDTH*x, WALL_WIDTH*(height-y), WALL_WIDTH*(x+1), WALL_WIDTH*((height-y)+1), 2));
	        		ArrayList<Wall> cellWalls = new ArrayList<Wall>();
	        		if (Globals.mazecells.hasWallOnTop(x, y))
	        			cellWalls.add(new Wall(WALL_WIDTH*x, WALL_WIDTH*((height-y)+1), WALL_WIDTH*(x+1), WALL_WIDTH*((height-y)+1)));
	        		if (Globals.mazecells.hasWallOnRight(x, y))
	        			cellWalls.add(new Wall(WALL_WIDTH*(x+1), WALL_WIDTH*((height-y)+1), WALL_WIDTH*(x+1), WALL_WIDTH*((height-y))));
	        		if (Globals.mazecells.hasWallOnLeft(x, y))
	        			cellWalls.add(new Wall(WALL_WIDTH*x, WALL_WIDTH*((height-y)+1), WALL_WIDTH*x, WALL_WIDTH*(height-y)));
	        		if (Globals.mazecells.hasWallOnBottom(x, y))
	        			cellWalls.add(new Wall(WALL_WIDTH*x, WALL_WIDTH*(height-y), WALL_WIDTH*(x+1), WALL_WIDTH*(height-y)));
	        		walls.put(x + y*width, cellWalls);
	        	}
	        } 
	        Globals.floors = floors;
	        Globals.roofs = roofs;
	        Globals.walls = walls;
	        Thread.sleep(100); // Check if interrupted
		}
		// If an exception is encountered, terminate the thread and return to AMazeActivity
		catch (Exception e) {
			Log.v("Issue", "Exception caught by GeneratingActivity Thread: " + e);
			onStop();
			return;
		}
		/*
		try {
			Robot robot = new BasicRobot();
			RobotDriver driver = new Wizard();
			robot.setMaze(maze);
			driver.setRobot(robot);
			driver.setDimensions(maze.getWidth(), maze.getHeight());
			driver.setDistance(maze.getDists());
			driver.drive2Exit();
			while (!driver.isFinished()) {
				generationProgress.setProgress(99);
			} 
		} catch (Exception e) {Log.v("GeneratingActivity", "Exception caught: " + e);} */
		
		// If generation was successful, launches PlayActivity and quietly terminates
		Log.v("GeneratingActivity", "GeneratingActivity Completed, Starting PlayActivity");
		Intent intent = new Intent(this, PlayActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
	}
	
	/**
	 * When the generating activity is paused, it must also be stopped. 
	 */
	@Override
	public void onPause() {
		super.onPause();
		onStop();
	}
	
	/**
	 * Handles termination of the generation thread, resets generation progress to 0.
	 */
	@Override
	public void onStop() {
		super.onStop();
		generationThread.interrupt();
		progress = 0;
		Log.v("GeneratingActivity", "GeneratingActivity Stopped");
		this.finish();
	}

}
