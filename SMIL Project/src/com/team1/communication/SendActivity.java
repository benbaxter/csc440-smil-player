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
        boolean itworked = Uploader.upload(SMILfile);
        try {
        sms.sendTextMessage(number, null, SMILfile.getName(), sentPI, deliveredPI); 
        } catch (Exception e) {toast("There was a problem sending SMIL message to " + number);}
        
        
        if (itworked) toast(SMILfile.getName() + "uploaded to cloud");
        else toast("There was a problem uploading the file");
    }
    
    private void sendSMSMessage(String phoneNumber, String message)
    {        
        Intent in = getIntent ( );
        if ( in.hasExtra ( "smilFile" ) )
        {
            String smilName = in.getExtras().getString( "smilFile" );
            saveSmilFile( smilName, SmilConstants.MODE_SEND );
            Log.i("SMILE FILE ABOUT TO SEND", ":"+smilName+":");
            File file = new File(Environment.getExternalStorageDirectory ( ) 
                    + SmilConstants.OUTBOX_PATH + smilName);
            Log.i ( "SEND", file.getAbsolutePath() );
            toast( "About to upload: " + smilName );
            try {
                boolean uploaded = Uploader.upload( file );
                if(uploaded)
                {
                    toast( "File uploaded: " + smilName );
                    
                    String SENT = "SMS_SENT";
                    String DELIVERED = "SMS_DELIVERED";
             
                    PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                        new Intent(SENT), 0);
             
                    PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                        new Intent(DELIVERED), 0);
                    
                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI); 
//                    sms.sendDataMessage( phoneNumber, null, SMS_PORT, message.getBytes(), sentPI, deliveredPI );
                   
                     
                    
                    setResult(RESULT_OK);
                    
                    finish();
                }
                else
                {
                    toast( "Failed to upload: " + smilName );
                }
            } 
            catch (Exception e)
            {
                toast( e.toString());
            }
            
            file.delete();
            String[] fileName = smilName.split( "_" );
            smilName = phoneNumber + "_" + fileName[1];
            saveSmilFile( smilName, SmilConstants.MODE_DRAFT );
        }
        
        finish();
        
    }
    
    
    
    private void saveSmilFile ( String fileName, int mode )
    {
        try
        {
            SmilGenerator sg = new SmilGenerator ( );
            sg.setFileName ( fileName );
            if(mode == SmilConstants.MODE_SEND) {
                sg.setFilePath ( SmilConstants.OUTBOX_PATH );
                sg.generateSMILFile ( ComposerActivity.getMedia(), SmilConstants.MODE_SEND );
            } else if (mode == SmilConstants.MODE_DRAFT) {
                sg.setFilePath ( SmilConstants.OUTBOX_PATH );
                sg.generateSMILFile ( ComposerActivity.getMedia(), SmilConstants.MODE_DRAFT );
            }
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
