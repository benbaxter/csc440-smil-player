package com.team1.maintocomposer;

import com.team1.maintocomposer.R;

import android.app.Activity;
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

        Button sendBtn = (Button)findViewById(R.id.sendSmsBtn);

        sendBtn.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View view) {
                EditText addrTxt = 
                      (EditText)SendTxtActivity.this.findViewById(R.id.addrEditText);

                EditText msgTxt = 
                      (EditText)SendTxtActivity.this.findViewById(R.id.msgEditText);

                try {
                    sendSmsMessage(
                        addrTxt.getText().toString(),msgTxt.getText().toString());
                    Toast.makeText(SendTxtActivity.this, "SMS Sent", 
                	        Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(SendTxtActivity.this, "Failed to send SMS", 
                	        Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }});
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void sendSmsMessage(String address,String message)throws Exception
    {
        SmsManager smsMgr = SmsManager.getDefault();
        smsMgr.sendTextMessage(address, null, message, null, null);
    }
}