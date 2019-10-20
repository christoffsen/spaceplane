package game.spaceplane.activity;

import game.spaceplane.asteroids.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

public class SettingsActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//run parent code
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        // If the back button was pressed
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            // properly dispose of this activity
            finish();
        }
        return super.onKeyDown(keyCode, event);
	}
}
