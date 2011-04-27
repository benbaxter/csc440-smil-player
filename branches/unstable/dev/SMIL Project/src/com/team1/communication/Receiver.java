//This is for you Hao

package com.team1.communication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.team1.R;
import com.team1.Smil.SmilComponent;
import com.team1.Smil.SmilConstants;
import com.team1.Smil.SmilMessage;
import com.team1.Smil.SmilReader;
import com.team1.communication.cloud.Downloader;
import com.team1.player.SmilPlayerActivity;
 
public class Receiver extends BroadcastReceiver
{
    Context toastContext;
    String smilFile;
    
    static boolean downloadFlag = false;
    @Override
    public void onReceive(Context context, Intent intent) 
    {    
        Log.i("RECEIVE", "onReceive started");
        toastContext = context;
//        //this stops notifications to others
        this.abortBroadcast();

        //---get the SMS message passed in---
        Bundle bundle = intent.getExtras();   
        SmsMessage[] msgs = null;
        String str = "";         
        String ticker = "";
        if (bundle != null)
        {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];            
            int numOfSMIL = 0;
            //int i = 0;
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
                if(msgs[i].getMessageBody().toString().contains( "You have just received a new SMIL message!" +
                		" Go to our application to check it out!" ))
                {
                    Log.i("RECEIVE", "Message is a SMIL message.");
                    str += "SMS from " + msgs[i].getOriginatingAddress().replace( "+", "" );   
                    str += " :";
                    str += msgs[i].getMessageBody().toString();
                    str += "\n";    
                    ++numOfSMIL;
                    String from = msgs[i].getOriginatingAddress().replace( "+", "" );
                    if(from.startsWith( "1" ))
                        from = from.substring( 1 );
                    smilFile = from + "_" + msgs[i].getMessageBody().split("_")[msgs[i].getMessageBody().split("_").length - 1];
                    Toast.makeText( toastContext, "About to download: " + smilFile, Toast.LENGTH_LONG );
                    boolean downloaded = false;
                    String fileName = Environment.getExternalStorageDirectory ( ) 
                    + SmilConstants.INBOX_PATH + smilFile;
                    
                    Log.i("RECEIVE", "about to download");
                    
                    
                    
                    downloadThread t = new downloadThread();
                    Log.i("DOWNLOADTHREAD", smilFile);
                    t.execute( "filename", smilFile, fileName );
                    try
                    {
                        t.get();
                    }
                    catch ( InterruptedException e1 )
                    {
                        e1.printStackTrace();
                    }
                    catch ( ExecutionException e1 )
                    {
                        e1.printStackTrace();
                    }
                    //
                    Log.i("RECEIVE", "downloaded");
                    downloaded = new File(fileName).exists();
                    
                    
                    if(downloaded)
                    {
                        Toast.makeText( toastContext, "Received: " + from + ".smil", Toast.LENGTH_LONG );
                        Log.i("DOWNLOAD", "downloaded: " + downloaded);
                        
                        try
                        {
                            SmilMessage message = SmilReader.parseMessage( fileName );
                            Log.i("RECEIVE", "downloading media");
                            downloadMedia(message);
                            while(!downloadFlag)
                            {
                                Thread.sleep( 500l );
                            }
                            Log.i("RECEIVE", "all media downloaded");
                        }
                        catch ( Exception e )
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        Toast.makeText( toastContext, "Did not download: " + from + ".smil", Toast.LENGTH_LONG );
                        Log.i("DOWNLOAD", "downloaded: " + downloaded);
                    }
                    
                    ticker = "You have " + numOfSMIL + " new SMIL message(s)";
                    //---display the new SMS message---
                    if(numOfSMIL > 0){                
                        //Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                        displayNotification( ticker, "SMIL Messages!", context );
                    }
                    
                }
                else{
                    //continue the normal process of sms and will get alert and reaches inbox
                    this.clearAbortBroadcast();
                }
            }
            
        } 
        else
        {
            Log.i("RECEIVE", "Message is not a SMIL message");
        }
    }    
    
    private static void downloadMedia ( SmilMessage message )
    {
        ArrayList < SmilComponent > components = message.getResourcesByBeginTime();
        ArrayList < downloadThread > threads = new ArrayList< downloadThread >();
        for ( int i = 0; i < components.size(); i++ )
        {
            SmilComponent comp = components.get(i);
           
            if( comp.getTitle() != null && comp.getTitle().length() > 0)
            {
                Log.i("DOWNLOAD", "Attempting to download key " + comp.getTitle());
                downloadThread t = new downloadThread();
                threads.add(t);
                t.execute( "key", comp.getTitle(), comp.getSource() );
            }
            
        }
        for(int i = 0; i < threads.size(); i++)
        {
            try
            {
                threads.get( i ).get();
            }
            catch ( InterruptedException e )
            {
                e.printStackTrace();
            }
            catch ( ExecutionException e )
            {
                e.printStackTrace();
            }
        }
        downloadFlag = true;
        
    }
    
    
    public void displayNotification( String ticker, String msg, Context context)
    {
        NotificationManager manager = ( NotificationManager ) context.getSystemService( Context.NOTIFICATION_SERVICE );
        manager.cancelAll();
        
        int icon = R.drawable.icon_mail;
        CharSequence tickerText = ticker;
        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, tickerText, when);
        notification.flags  = Notification.FLAG_AUTO_CANCEL;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;

        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        
        long[] vibrate = {0,100,200,300};
        notification.vibrate = vibrate;
        
        notification.ledARGB = 0xff00ff00;
        notification.ledOnMS = 300;
        notification.ledOffMS = 1000;
        
        CharSequence contentTitle = "New SMIL Messages!";
        CharSequence contentText = "Click here to check them out!";
        
        // Construct the file path to the received SMIL file
        String smilFile2 = Environment.getExternalStorageDirectory() + SmilConstants.INBOX_PATH + smilFile;
        Log.i("RECEIVE", "smil file: " + smilFile2);
        
        // Pass the SMIL file path into the SMIL Player Activity intent
        Intent notificationIntent = new Intent(context, SmilPlayerActivity.class);
        notificationIntent.putExtra( "RecievedSmil", smilFile2 );
        
        // When the user selects the notification, the SMIL Player Activity should start
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        manager.notify( 1, notification );
    }
 }


class downloadThread extends AsyncTask<String, Void, Void>
{

    @Override
    protected Void doInBackground( String... params )
    {
        String type = params[0];
        String data = params[1];
        String saveAs = params[2];
        Log.e("DOWNLOADTHREAD", params[0]+ " + " + params[1] + " + " + params[2]);
        try{
        if(type.equals("filename"))
            Downloader.downloadFilename( data, saveAs );
        else
            Downloader.downloadKey( data, saveAs );
        }
        catch(IOException e)
        {
            e.printStackTrace();
            Log.e("DOWNLOADTHREAD", "Something broke");
        }
        return null;
    }
    
}

