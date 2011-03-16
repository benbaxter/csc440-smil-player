package com.team1.composer.send;

import com.team1.composer.R;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendActivity extends Activity{
    
    private static final int PICK_CONTACT = 1;
    private String name = "";
    private String number = "";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send);
        
        Button browse = (Button) findViewById(R.id.browseBtn);
        Button send = (Button) findViewById(R.id.sendBtn);
        Button cancel = (Button) findViewById(R.id.cancelBtn);

        browse.setOnClickListener(buttonClick);
        send.setOnClickListener(buttonClick);
        cancel.setOnClickListener(buttonClick);
    }
    
    OnClickListener buttonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.browseBtn){
                getContact();
            }else if(v.getId() == R.id.sendBtn){
                EditText addrTxt = (EditText) SendActivity.this.findViewById(R.id.addrText);
                addrTxt.setFocusable(false);

                try {
                    String msg = "You have just received a new SMIL message! Go to our application to check it out!";
                    if(number.equals( "" ))
                    {
                        number = addrTxt.getText().toString();
                    }
                    sendSMSMessage(number, msg);
                    
                    
                } catch (Exception e) {
                    toast("Failed to send SMS ");
                    e.printStackTrace();
                }
                
            }else if(v.getId() == R.id.cancelBtn){
                finish();
            }
        }
    };
    
    private void getContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }
    
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (PICK_CONTACT) :
            if (resultCode == Activity.RESULT_OK) {
                Uri contactData = data.getData();
                String id = contactData.getLastPathSegment();
                Cursor c =  getContentResolver().query(Phone.CONTENT_URI,  
                        null, Phone.CONTACT_ID + "=?", new String[] { id },  
                        null);  
                
                if (c.moveToFirst()) {
                    name = c.getString(c.getColumnIndex(Contacts.DISPLAY_NAME));
                    number = c.getString(c.getColumnIndex(Phone.DATA));
                    if(number.equals("") )
                    {
                        toast("Contact has no Number");
                    }
                    EditText phoneNumber = (EditText) findViewById ( R.id.addrText );
                    phoneNumber.setText ( name );
                }
            }
            break;
        }
    }
    
    private void sendSMSMessage(String phoneNumber, String message)
    {        
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
 
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
            new Intent(SENT), 0);
 
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
            new Intent(DELIVERED), 0);
        BroadcastReceiver sentReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off", 
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        BroadcastReceiver deliveredReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;                        
                }
            }
        };
        //---when the SMS has been sent---
        registerReceiver(sentReceiver, new IntentFilter(SENT));
        
        //---when the SMS has been delivered---
        registerReceiver( deliveredReceiver, new IntentFilter(DELIVERED));        
        
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI); 
//        unregisterReceiver( sentReceiver );
//        unregisterReceiver( deliveredReceiver );
        setResult(RESULT_OK);
        finish();
    }
    
    public void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
