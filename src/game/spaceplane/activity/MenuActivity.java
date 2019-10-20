package game.spaceplane.activity;

import game.spaceplane.asteroids.R;
import game.spaceplane.asteroids.R.id;
import game.spaceplane.asteroids.R.layout;
import game.spaceplane.asteroids.R.menu;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_activity);

		/**
		 * Buttons
		 */
		Button singleP_game = (Button) findViewById(R.id.singleplayer_button);
		Button multiP_game = (Button) findViewById(R.id.multiplayer_button);
		Button settings_button = (Button) findViewById(R.id.settings_button);
		Button test_button = (Button) findViewById(R.id.test_button);

		/**
		 * Button listeners
		 */
		singleP_game.setOnClickListener(this);
		multiP_game.setOnClickListener(this);
		settings_button.setOnClickListener(this);
		test_button.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		Intent intent;
		switch (view.getId()) {
		case R.id.singleplayer_button:
			intent = new Intent(view.getContext(), SingleplayerActivity.class);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivityForResult(intent, 0);
			break;
		case R.id.multiplayer_button:
			intent = new Intent(view.getContext(), MultiplayerActivity.class);
			startActivityForResult(intent, 0);
			break;
		case R.id.settings_button:
			intent = new Intent(view.getContext(), SettingsActivity.class);
			startActivityForResult(intent, 0);
			break;
		case R.id.test_button:
			intent = new Intent(view.getContext(), TestActivity.class);
			startActivityForResult(intent, 0);
			break;
		}
	}
	

	/**
	 * If a physical button on the phone is pressed
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// If the back button was pressed
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			this.finish();
			return super.onKeyDown(keyCode, event);
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

}
