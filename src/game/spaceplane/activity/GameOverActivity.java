package game.spaceplane.activity;

import game.spaceplane.asteroids.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class GameOverActivity extends Activity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		setContentView(R.layout.gameover);
		TextView playerOne = (TextView) findViewById(R.id.player_one);
		TextView playerTwo = (TextView)findViewById(R.id.player_two);
		int[] playerScores = new int[1];
		int spacing = 20;
		if (extras != null) {
			playerScores = extras.getIntArray("PlayerScores");
			playerOne.setText("Player 1: " + Integer.toString(playerScores[0]));
			try {
			if(playerScores.length > 1) {
				playerTwo.setText("Player 2: " + Integer.toString(playerScores[1]));
			}
			} catch(Exception e) {
			    e.printStackTrace();
			}
		}
		Button menu = (Button) findViewById(R.id.menu_button);
		menu.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.menu_button:
			Intent intent = new Intent(this, MenuActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
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
			Intent intent = new Intent(this, MenuActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return super.onKeyDown(keyCode, event);
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
}
