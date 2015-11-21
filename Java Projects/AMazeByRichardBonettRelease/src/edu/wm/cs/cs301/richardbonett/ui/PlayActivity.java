package edu.wm.cs.cs301.richardbonett.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * PlayActivity handles the graphical implementation of the maze, providing capabilities for navigating to the solution
 * if in manual mode or providing pause/resume capabilities if in robot-controlled mode. PlayActivity also allows for the
 * toggling on/off of the overhead map view, the solution in this view, and showing currently visible walls in this view. The
 * maze can be saved at any time using the PlayActivity's save button. 
 * 
 * Responsibilities: Display graphical representation of the maze, necessary buttons to allow for navigation to the exit and
 * 					 additional functionality. 
 * Collaborators: GeneratingActivity; launches PlayActivity with generated maze. FinishActivity; launched upon natural termination of
 * 				  PlayActivity, be it successful or otherwise ('natural' excluding back button, which returns to title screen),
 * 				  Maze; used to handle the underlying data structure
 * 				  MyGLSurfaceView; used to handle the display
 * 
 * @author rfbonett
 *
 */
public class PlayActivity extends Activity {

	private GLSurfaceView glView;
	private String ENERGY = "ENERGY";private String RESULT = "RESULT";
	private String PATH_LENGTH = "PATH_LENGTH";
	private String TIME = "TIME";
	private boolean robotMode;
	private ProgressBar energyProgressBar;
	private boolean paused;
	private int startTime;
	private int pauseTime;
	private int startPauseTime;
	private FrameLayout layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		startTime = startPauseTime = (int) SystemClock.elapsedRealtime();
		addViews();
		pauseTime = 0;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.play, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_toggle_map :
			toggleMap();
			break;
		case R.id.action_toggle_solution :
			toggleSolution();
			break;
		case R.id.action_show_walls :
			toggleWalls();
			break;
		case R.id.action_save :
			save();
			break;
		case R.id.action_settings :
			showSettings();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onDestroy() {
		launchFinish();
		super.onDestroy();
	}
	
	/**
	 * Adds needed references to the views, and hides conflicting buttons. Adds a surface view to manage the maze display
	 * via OpenGLES 2.0
	 */
	private void addViews() {
		layout = (FrameLayout) findViewById(R.id.container);
		glView = new MyGLSurfaceView(this);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layout.addView(glView, lp);
		
		robotMode = Globals.operationType != 0;
		Log.v("PlayActivity", "Starting PlayActivity in " + (robotMode ? "Robot Mode" : "Manual Mode"));
		energyProgressBar = (ProgressBar) findViewById(R.id.progressBar_energy);
		if (!robotMode) {
			energyProgressBar.setVisibility(View.GONE);
			TextView energyBarTitle = (TextView) findViewById(R.id.textView_energy_bar_title);
			energyBarTitle.setVisibility(View.GONE);
		}
		else {
			energyProgressBar.setProgress(2500);
		} 
	}
	
	/**
	 * Toggles the display of the overhead map.
	 */
	public void toggleMap() {
		Log.v("PlayActivity", "Toggling Map " + (Globals.showMap ? "OFF" : "ON"));
		if (Globals.showMap) {
		}
		else {
		}
		Globals.showMap = !Globals.showMap;
	}
	
	/**
	 * Toggles the display of the solution in the overhead map. Need not have the map toggled on to view. 
	 */
	public void toggleSolution() {
		Log.v("PlayActivity", "Toggling Solution " + (Globals.showSolution ? "OFF" : "ON"));
		if (Globals.showSolution) {
			// Hide the Solution Display
		}
		else {
			// Show the Solution Display
		}
		Globals.showSolution = !Globals.showSolution;
	}
	
	/**
	 * Toggles the display of currently visible walls in the overhead view.
	 */
	public void toggleWalls() {
		Log.v("PlayActivity", "Toggling Walls " + (Globals.showWalls ? "OFF" : "ON"));
		Globals.showWalls = !Globals.showWalls;
	}
	
	/**
	 * Launches a dialog to handle saving the maze to a file. 
	 */
	public void save() {
		Log.v("PlayActivity", "Launching Save Dialog");
		final EditText fileNameInput = new EditText(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dialog_save_file);
		builder.setView(fileNameInput);
		builder.setPositiveButton(R.string.confirm_selection, new DialogInterface.OnClickListener() { 
		  public void onClick( DialogInterface dialog, int whichButton) 
		  { 
			writeMazeToFile(fileNameInput.getText().toString());
		  } 
		 });
		builder.setNegativeButton(R.string.cancel, null);
		builder.show();	
	}
	
	
	/**
	 * Launches a dialog containing available settings. Currently none are implemented. 
	 */
	public void showSettings() {
		Log.v("PlayActivity", "Launching Settings Dialog");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dialog_settings);
		builder.setPositiveButton(R.string.close, null);
		builder.show();
	}
	
	/**
	 * Writes the maze to a file of name fileName in the save file directory.
	 * TODO implement actual code to achieve this. 
	 * @param fileName
	 */
	public void writeMazeToFile(String fileName) {
		Log.v("PlayActivity", "Saving Maze as " + fileName);
	}

	/**
	 * Pauses the robot's progress. Includes calculations for accurate timekeeping.
	 * @param view the view which launched this method, usually the pause button (R.id.button_pause)
	 */
	public void pause(View view) {
		Log.v("PlayActivity", "Pause Button Pressed: " + (paused ? "Resuming" : "Pausing"));
		if (paused) {
			pauseTime += (int) SystemClock.elapsedRealtime() - startPauseTime;
			
			// Resume/Start the Robot
		}
		else {
			startPauseTime = (int) SystemClock.elapsedRealtime();
			// Pause the Robot
		}
		paused = !paused;
	}
	
	/**
	 * Launches the FinishActivity, passing values for the success/failure of PlayActivity, the usage of a robot, the 
	 * remaining energy, the pathlength and the time elapsed. 
	 * @param result success/failure of the PlayActivity, success if true, failure if false
	 */
	private void launchFinish() {
		if (paused && robotMode)
			pauseTime += SystemClock.elapsedRealtime() - startPauseTime;
		Intent intent = new Intent(this, FinishActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		if (robotMode)
			intent.putExtra(ENERGY, 2500 - energyProgressBar.getProgress());
		intent.putExtra(TIME, (int) (SystemClock.elapsedRealtime() - (startTime + pauseTime))/1000);
		startActivity(intent);
	}
	
}


