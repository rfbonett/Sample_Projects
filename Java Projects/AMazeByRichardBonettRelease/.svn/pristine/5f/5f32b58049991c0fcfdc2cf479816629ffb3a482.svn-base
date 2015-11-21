package edu.wm.cs.cs301.richardbonett.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.VideoView;

/**
 * Activity to handle the Maze's title screen. Main Activity which launches on Application launch and allows the user to
 * select options for running a new maze or loading a maze from a previously saved file. The available options include the
 * generation algorithm used for the maze (Falstad's DFS, Prim's or Kruskal's), the type of operation used to solve the maze
 * (Manual (user-controlled), WallFollower Robot, or Wizard Robot), as well as the difficulty of the maze.  
 * 
 * Responsibilities: Handle the Application's title screen, provide settings for maze generation
 * Collaborators: GeneratingActivity; launched to begin generation once the user selects "start"
 * 
 * @author rfbonett
 *
 */
public class AMazeActivity extends Activity {
	
	private Spinner genAlgorithmsSpinner;
	private Spinner opTypesSpinner;
	private SeekBar difficultySeekBar;
	private VideoView video;
	private boolean triedSmallerResolution;
	
	/**
	 * Creates the initial display. The display uses a high quality video which some older models (and the sad emulator) cannot properly
	 * display, so if the high quality video throws an error it tries again with a lower quality version. If this version also throws an 
	 * error, it simply displays a blank background. 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_amaze);
		addViews();
		addListeners();
		triedSmallerResolution = false;
		addVideo(R.raw.sun_background);
		Log.v("AMazeActivity", "Started");
		Log.v("amaze", getPackageName());
	}
	
	/**
	 * Restarts the video on activity resume
	 */
	@Override
	public void onResume() {
		super.onResume();
		video.start();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.amaze, menu);
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
	 * Launches a Dialog to handle the loading of a previously saved file. Currently, the 'available' files do not 
	 * correspond to existent files as this code will be implemented later. The user has the option to either select one
	 * of the files found in the save directory or give the name of a file saved elsewhere.
	 * 
	 * @param view the view which launched the method, usually the load button (R.id.button_load)
	 */
	public void loadFile(View view) {
		Log.v("AMazeActivity", "Load File Button Pressed");
		final EditText fileNameInput = new EditText(this);
		final String[] loadFiles = getResources().getStringArray(R.array.save_files);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dialog_load_file);
		builder.setItems(loadFiles, new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialog, int selection) {
				Log.v("AMazeActivity", "Load File Selected: " + loadFiles[selection]);
				setAlgorithmToFile(loadFiles[selection]);
			}
		});
		builder.setView(fileNameInput);
		builder.setPositiveButton(R.string.confirm_selection, new DialogInterface.OnClickListener() { 
		  public void onClick( DialogInterface dialog, int whichButton) 
		  { 
			Log.v("AMazeActivity", "Load File Confirm Button Pressed. File Loaded: " + fileNameInput.getText().toString());
		    setAlgorithmToFile(fileNameInput.getText().toString());
		  } 
		 });
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { 
			  public void onClick( DialogInterface dialog, int whichButton) 
			  { 
				Log.v("AMazeActivity", "Load File Cancel Button Pressed");
			  } 
			 });
		builder.show();
	}
	
	/**
	 * Launches GeneratingActivity with the currently selected settings
	 * @param view the view which launched the method, usually the start button (R.id.button_start)
	 */
	public void startMazeGeneration(View view) {
		Log.v("AMazeActivity", "Start Button Pressed");
		Intent intent = new Intent(this, GeneratingActivity.class);
		Globals.generationAlgorithm = genAlgorithmsSpinner.toString();
		Globals.operationType = opTypesSpinner.getSelectedItemPosition();
		Globals.difficulty = difficultySeekBar.getProgress();
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
	}
	
	/**
	 * Adds the loaded file's name to the Spinner for generation algorithms and sets the current value of this Spinner to 
	 * the file's name. This is done primarily for aesthetics' sake, but it is also useful to allow for operation selection
	 * in conjunction with loading a file. 
	 * @param fileName the name of the file to add to the spinner
	 */
	private void setAlgorithmToFile(String fileName) {
		ArrayList<String> list= new ArrayList<String>();
	    list.add("Falstad");
	    list.add("Prim");
	    list.add("Kruskal");
	    list.add(fileName);
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, list);
	    genAlgorithmsSpinner.setAdapter(adapter);
	    genAlgorithmsSpinner.setSelection(3);
	}
	
	/**
	 * Adds the two spinners to the layout, sets the maximum value of the difficulty seekbar to 13, and provides 
	 * references to the spinners and seekbar.
	 */
	private void addViews() {
		genAlgorithmsSpinner = (Spinner) findViewById(R.id.spinner_generation_algorithms);
		opTypesSpinner = (Spinner) findViewById(R.id.spinner_operation_types);
		difficultySeekBar = (SeekBar) findViewById(R.id.seekBar_difficulty);
		difficultySeekBar.setMax(13);
		
		ArrayList<String> algorithms = new ArrayList<String>();
	    algorithms.add("Falstad");
	    algorithms.add("Prim");
	    algorithms.add("Kruskal");
	    ArrayAdapter<String> algAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, algorithms);
	    genAlgorithmsSpinner.setAdapter(algAdapter);
	    
	    ArrayList<String> operations = new ArrayList<String>();
	    operations.add("Manual");
	    operations.add("WallFollower Robot");
	    operations.add("Wizard Robot");
	    ArrayAdapter<String> opAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, operations);
	    opTypesSpinner.setAdapter(opAdapter);
	}
	
	/**
	 * Helper method to start the video background
	 * @param resourceID the id of the video to display
	 */
	private void addVideo(int resourceID) {
		video = (VideoView) findViewById(R.id.videoView_amaze);
		video.setVideoURI(Uri.parse("android.resource://edu.wm.cs.cs301.richardbonett.ui/" + resourceID));
	    video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
	        public void onCompletion(MediaPlayer mp) {
	            video.start();
	        }
	    });
		video.setOnErrorListener(new MediaPlayer.OnErrorListener() {
	        public boolean onError(MediaPlayer mp, int what, int extra) {
	            notifyVideoFailure();
	            return true;
	        }
	    });
		video.start();
	}
	
	/**
	 * Handles errors in displaying the video. Attempts a smaller resolution if available, or hides the VideoView
	 */
	private void notifyVideoFailure() {
		if (triedSmallerResolution) {
			video.setVisibility(View.GONE);
		}
		else {
			addVideo(R.raw.sun_background_small);
		}
	}
	
	/**
	 * Adds listeners to the seekbar and spinners to allow for response on selection/value change.
	 * Used for logging purposes.
	 */
	private void addListeners() {
		genAlgorithmsSpinner.setOnItemSelectedListener(new SpinnerListener());
		opTypesSpinner.setOnItemSelectedListener(new SpinnerListener());
		difficultySeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override       
		    public void onStopTrackingTouch(SeekBar seekBar) {      
		        Log.v("AMazeActivity", "SeekBar Value Changed. New Value: " + seekBar.getProgress());      
		    }       

		    @Override       
		    public void onStartTrackingTouch(SeekBar seekBar) {     
		        Log.v("AMazeActivity", "SeekBar Tracking Started. Starting Value: " + seekBar.getProgress());    
		    }       

		    @Override       
		    public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {     
		        // Do Nothing      
		    }
		});
	}
	
	/**
	 * class used to provide listeners for the spinners. Used for logging purposes.
	 * @author rfbonett
	 *
	 */
	class SpinnerListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			Log.v("AMazeActivity", "Item Selected: " + parent.getItemAtPosition(pos));
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {	
			// Do Nothing
		}
		
	}
	
}
