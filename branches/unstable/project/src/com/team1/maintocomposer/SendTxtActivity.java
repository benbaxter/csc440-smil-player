package com.team1.maintocomposer;

import com.team1.maintocomposer.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendTxtActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sendtext);

		/*
		 * Button sendBtn = (Button) findViewById(R.id.sendSmsBtn);
		 * sendBtn.setOnClickListener(sendSMS);
		 */

		Button sendVertBtn = (Button) findViewById(R.id.sendBtn);
		sendVertBtn.setOnClickListener(sendSMS);

		Button back = (Button) findViewById(R.id.back);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}

	OnClickListener sendSMS = new OnClickListener() {

		@Override
		public void onClick(View view) {
			EditText addrTxt = (EditText) SendTxtActivity.this
					.findViewById(R.id.addrEditText);
			addrTxt.setFocusable(false);

			EditText msgTxt = (EditText) SendTxtActivity.this
					.findViewById(R.id.msgEditText);
			msgTxt.setFocusable(false);

			Button sendVertBtn = (Button) findViewById(R.id.sendBtn);
			sendVertBtn.setFocusable(true);
			try {
				sendSmsMessage(addrTxt.getText().toString(), msgTxt.getText()
						.toString());
				Toast.makeText(SendTxtActivity.this, "SMS Sent",
						Toast.LENGTH_LONG).show();
				openComposerActivity();
			} catch (Exception e) {
				Toast.makeText(SendTxtActivity.this, "Failed to send SMS",
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	};

	private void openComposerActivity() {
		Intent mComposerIntent = new Intent(getApplicationContext(),
				ComposerActivity.class);
		mComposerIntent.putExtra("Composer", "");
		startActivity(mComposerIntent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void sendSmsMessage(String address, String message)
			throws Exception {
		SmsManager smsMgr = SmsManager.getDefault();
		smsMgr.sendTextMessage(address, null, message, null, null);
		finish();
	}
}