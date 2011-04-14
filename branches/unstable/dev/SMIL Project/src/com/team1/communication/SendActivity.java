package com.team1.communication;

import java.io.File;
import java.util.*;
import com.team1.R;
import com.team1.Smil.SmilConstants;
import com.team1.Smil.SmilGenerator;
import com.team1.communication.cloud.Uploader;
import com.team1.composer.ComposerActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
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
                //ProgressDialog dialog = ProgressDialog.show(getApplicationContext(), "", "Please wait for few seconds...", true);
                EditText addrTxt = (EditText) SendActivity.this.findViewById(R.id.addrText);
                addrTxt.setFocusable(false);
                Intent in = getIntent ( );
                String smilURL = in.getExtras().getString( "smilFile" );


                try {
                    String msg = "You have just received a new SMIL message! Go to our application to check it out!";
                    if(number.equals( "" ))
                    {
                        number = addrTxt.getText().toString();
                    }
                    msg += " Look for the file in your inbox called " + smilURL;
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
                    if(number.matches( ".*[0-9]*" ) )
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
    
    public void sendSMILMessage(String number, File SMILfile)
    {
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
        SmsManager sms = SmsManager.getDefault();
        
        try {
        sms.sendTextMessage(number, null, SMILfile.getName(), sentPI, deliveredPI); 
        } catch (Exception e) {toast("There was a problem sending SMIL message to " + number);}
        
        boolean itworked = Uploader.upload(SMILfile);
        if (itworked) toast(SMILfile.getName() + "uploaded to cloud");
        else toast("There was a problem uploading the file");
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
//        sms.sendDataMessage( phoneNumber, null, SMS_PORT, message.getBytes(), sentPI, deliveredPI );
        unregisterReceiver( sentReceiver );
        unregisterReceiver( deliveredReceiver );
        //will get rid of extras if this works
        Intent in = getIntent ( );
        if ( in.hasExtra ( "smilFile" ) )
        {
            String smilURL = in.getExtras().getString( "smilFile" );
            saveSmilFile( smilURL );
            Log.i("SMILE FILE ABOUT TO SEND", ":"+smilURL+":");
            File file = new File(Environment.getExternalStorageDirectory ( ) 
                    + SmilConstants.OUTBOX_PATH + smilURL);
            Log.i ( "SEND", file.getAbsolutePath() );
            toast( "About to upload: " + smilURL );
            try {
                boolean uploaded = Uploader.upload( file );
                if(uploaded)
                {
                    toast( "File uploaded: " + smilURL );
                }
                else
                {
                    toast( "Failed to upload: " + smilURL );
                }
            } 
            catch (Exception e)
            {
                toast( e.toString());
            }
//            Intent sendIntent = new Intent(Intent.ACTION_SEND); 
//            sendIntent.putExtra("sms_body", "some text");     
//            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(smilURL));
//            sendIntent.setType("text/plain");
//            startActivity(sendIntent); 
        }
         
        
        setResult(RESULT_OK);
        
        finish();
    }
    
//    private void uploadToCloud()
//    {
//        Intent in = getIntent ( );
//        if ( in.hasExtra ( "mediaFiles" ) )
//        {
//            ArrayList<String> fileNames = in.getExtras().getStringArrayList( "mediaFiles" );
//            for( String f : fileNames )
//            {
//                File file = new File(f);
//                toast( "does file exist: " + f );
//                if(file.exists())
//                {
//                    Log.i("FILENAME", f);
//                    toast( "About to upload" );
//                    try {
//                        boolean uploaded = Uploader.upload( file );
//                        if(uploaded)
//                        {
//                            toast( "File uploaded: " + f );
//                        }
//                        else
//                        {
//                            toast( "Failed to upload: " + f );
//                        }
//                    } 
//                    catch (Exception e)
//                    {
//                        toast( e.toString());
//                    }
//                }
//                else
//                {
//                    toast( "file does not exist: " + f);
//                }
//            }
//        }
//    }
    
    
    private void saveSmilFile ( String fileName )
    {
        try
        {
            SmilGenerator sg = new SmilGenerator ( );
            sg.setFileName ( fileName );
            sg.setFilePath ( SmilConstants.OUTBOX_PATH );
            sg.generateSMILFile ( ComposerActivity.getMedia(), SmilConstants.MODE_SEND );
        }
        catch ( Exception e )
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Exception", "error occurred while creating xml file", e);
        }
    }
    
    
    private String getMyPhoneNumber(){
        TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
        return mTelephonyMgr.getLine1Number();
    }
    
    public void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
