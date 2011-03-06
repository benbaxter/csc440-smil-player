package com.team1.composer;

import com.team1.composer.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class HelloActivity extends Activity {
	Toast toast;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.hello);

		Button newBtn = (Button) findViewById(R.id.newBtn);
		newBtn.setOnClickListener(mClick);
	}

	OnClickListener mClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Context context = getApplicationContext();
			toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
			if (v.getId() == R.id.newBtn) {
				toast.setText("Clicking this button creates a new message.");
				toast.show();
			}

		}
	};

}