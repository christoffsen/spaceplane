package game.spaceplane.activity;

import game.spaceplane.asteroids.R;
import game.spaceplane.network.NetworkEngine;
import game.spaceplane.view.MultiplayerClientView;
import game.spaceplane.view.MultiplayerHostView;

import java.net.InetAddress;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class MultiplayerActivity extends Activity implements OnClickListener {
    private static final String TAG = MultiplayerActivity.class.getSimpleName();
    private NetworkEngine network = new NetworkEngine();
    private MultiplayerHostView MHV;
    private MultiplayerClientView MCV;
    private boolean active = false;
    boolean host = false;
    private Intent intent;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.multiplayer_activity);

        Button join = (Button) findViewById(R.id.JoinButton);
        Button host = (Button) findViewById(R.id.HostButton);
        Button launch = (Button) findViewById(R.id.LaunchButton);

        join.setOnClickListener(this);
        host.setOnClickListener(this);
        launch.setOnClickListener(this);

        TextView t = (TextView) findViewById(R.id.YourIPBox);
        String text = "";
        ArrayList<InetAddress> list = NetworkEngine.getIP();

        for (InetAddress add : list) {
            String temp = add.getHostAddress();
            if (NetworkEngine.isIPv4(temp)) {
                text = temp;
                break;
            }
        }

        t.setText(text);
    }

    public void host() {
        host = true;
        network.startHost();
        // network.startDummyClient();
        CheckBox c = (CheckBox) findViewById(R.id.ConnectedCheckBox);
        c.setChecked(true);
    }

    public void join() {
        host = false;
        EditText text = (EditText) findViewById(R.id.IPBox);
        String IP = text.getText().toString();

        network.startClient(NetworkEngine.makeAddress(IP));
        CheckBox c = (CheckBox) findViewById(R.id.ConnectedCheckBox);
        c.setChecked(true);
    }

    public void launch(View view) {
        if (host) {
            MHV = new MultiplayerHostView(this);
            setContentView(MHV);
            Log.d(TAG, "Multiplayer Host View added");
        } else {
            MCV = new MultiplayerClientView(this);
            setContentView(MCV);
            Log.d(TAG, "Multiplayer Client View added");
        }
        // network.setWaiting(false);

        // TextView t = (TextView)findViewById(R.id.YourIPBox);
        // t.setText(network.getClientList().get(0).getInetAddress().toString());
    }

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.JoinButton:
            join();
            break;
        case R.id.HostButton:
            host();
            break;
        case R.id.LaunchButton:
            launch(v);
        }

    }

    @Override
    public void onStart() {
        Log.d(TAG, "Creating a new MultiplayerHost Activity");
        super.onStart();
        active = true;
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Destroying a MultiplayerHost Activity");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopping a MultiplayerHost Activity");
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
            if (host) {
                if (MHV.thread != null)
                    MHV.thread.onStop();
            } else {
                if (MCV != null) {
                    if (MCV.thread != null)
                        MCV.thread.onStop();
                }
            }
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean getActive() {
        return active;
    }

    public NetworkEngine getNetwork() {
        return network;
    }
}
