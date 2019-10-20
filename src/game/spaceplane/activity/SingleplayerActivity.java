package game.spaceplane.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import game.spaceplane.view.SinglePlayerView;

public class SingleplayerActivity extends Activity {

	private static final String TAG = SingleplayerActivity.class
			.getSimpleName();

	private SinglePlayerView SPV;
	private boolean active = false;

	/**
	 * Override the create, destroy and stop methods to set the view and log
	 * information
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// turn the title off
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		SPV = new SinglePlayerView(this);
		// set our view to the singleplayerview
		setContentView(SPV);
		Log.d(TAG, "Singleplayer View added");
	}

	@Override
	public void onStart() {
		Log.d(TAG, "Creating a new Singleplayer Activity");
		super.onStart();
		active = true;
	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG, "Destroying a Singleplayer Activity");
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "Stopping a Singleplayer Activity");
		super.onStop();
		active = false;
	}

	/**
	 * If a physical button on the phone is pressed
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// If the back button was pressed
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			// properly dispose of this activity
			SPV.thread.onStop();
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	
	public boolean getActive() {
		return active;
	}
}
