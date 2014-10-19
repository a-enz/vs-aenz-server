package ch.ethz.inf.vs.a2.server;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	
	private Intent serverIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ToggleButton tb = (ToggleButton) findViewById(R.id.btn_toggle);
		tb.setText(R.string.btn_stopped);
		serverIntent = new Intent(this.getApplicationContext(), ServerService.class);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	
	public void onClickToggle(View v) {
		ToggleButton tb = (ToggleButton) v;
		if (tb.isChecked()){
			tb.setText(R.string.btn_running);
			startService(serverIntent);
//			serverIntent.start(this.getApplicationContext());
			
		} else {
			tb.setText(R.string.btn_stopped);
		}
	}
}
