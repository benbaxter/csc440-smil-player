package com.team1.composer;

import com.team1.composer.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;
 
public class Receiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) 
    {    
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
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                if(msgs[i].getMessageBody().toString().equals( "You have just received a new SMIL " +
                		"message! Go to our application to check it out!" ))
                {
                    str += "SMS from " + msgs[i].getOriginatingAddress();   
                    str += " :";
                    str += msgs[i].getMessageBody().toString();
                    str += "\n";    
                    ++numOfSMIL;
                }
            }
            ticker = "You have " + numOfSMIL + " new SMIL message(s)";
            //---display the new SMS message---
            if(numOfSMIL > 0){                
                Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                displayNotification( ticker, "SMIL MEssages!", context );
            }
        } 
    }
    
    public void displayNotification( String ticker, String msg, Context context)
    {
        NotificationManager manager = ( NotificationManager ) context.getSystemService( Context.NOTIFICATION_SERVICE );
        int icon = R.drawable.icon_mail;
        CharSequence tickerText = ticker;
        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, tickerText, when);
//        Notification notification = new Notification( R.drawable.icon_mail, msg, System.currentTimeMillis() );
        notification.flags = notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        
        long[] vibrate = {0,100,200,300};
//        notification.vibrate = vibrate;
        
        notification.ledARGB = 0xff00ff00;
        notification.ledOnMS = 300;
        notification.ledOffMS = 1000;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        
        Context appContext = context.getApplicationContext();
        CharSequence contentTitle = "New SMIL Messages!";
        CharSequence contentText = "Click here to check them out!";
        //this will take us to the inbox activity
        Intent notificationIntent = new Intent(context, Main.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        
//        notification.setLatestEventInfo( this, "Title here", ".. And here's some more details..", contentIntent );

        manager.notify( 1, notification );
    }
    
 }
