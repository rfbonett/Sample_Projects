package edu.wm.cs.cs301.richardbonett.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * FinishActivity displays the result of the PlayActivity; success if the end of the maze was reached, failure if the end of 
 * the maze was not reached due to some constraint (i.e. robot ran out of power). Additionally, FinishActivity displays various
 * additional data about the result of PlayActivity, including the number of cells traversed, time spent, energy consumption. 
 * 
 * Responsibilities: Display result of maze traversal
 * Collaborators: PlayActivity; gives FinishActivity the necessary information to display.
 * 
 * @author rfbonett
 *
 */
public class FinishActivity extends Activity {

	private TextView resultText;
	private TextView energyText;
	private TextView pathLengthText;
	private TextView timeText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finish);
		setDisplay();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.finish, menu);
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
	 * Sets the value of views on the FinishActivity to correspond with data in its intent.
	 */
	public void setDisplay() {
		Intent intent = getIntent();
		resultText = (TextView) findViewById(R.id.textView_result);
		energyText = (TextView) findViewById(R.id.textView_energy_consumed);
		pathLengthText = (TextView) findViewById(R.id.textView_path_length);
		timeText = (TextView) findViewById(R.id.textView_time);
		if (!Globals.result) {
			resultText.setText("Failure!");
			Log.v("FinishActivity", "FinishActivity Launching with Failure Attribute");
		}
		else
			Log.v("FinishActivity", "FinishActivity Launching with Success Attribute");
		
		if (intent.getBooleanExtra("ROBOT", false))
			energyText.setText("Energy Consumed: " + intent.getIntExtra("ENERGY", 0));
		else
			energyText.setVisibility(View.INVISIBLE);
		
		pathLengthText.setText("Path Length: " + Globals.pathLength);
		timeText.setText("Time Taken: " + intent.getIntExtra("TIME", 0) + " Seconds");
	}
	
	/**
	 * Returns to the AMazeActivity title screen, ending this activity
	 * @param view the view which launched this method, usually the "Return to Title" button (R.id.button_return_to_title)
	 */
	public void returnToTitle(View view) {
		Log.v("FinishActivity", "Returning To Title (AMazeActivity)");
		this.finish();
	}
}
