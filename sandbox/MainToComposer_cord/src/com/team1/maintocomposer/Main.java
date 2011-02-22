package com.team1.maintocomposer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.team1.maintocomposer.HelloActivity;


public class Main extends Activity {
	Toast toast;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	    
	    Button newMessage = (Button)findViewById(R.id.newMain);
	    Button drafts = (Button)findViewById(R.id.draftsMain);
	    Button inbox = (Button)findViewById(R.id.inboxMain);
	    Button outbox = (Button)findViewById(R.id.outboxMain);
	    Button newBtn = (Button)findViewById(R.id.newBtn);
	    
	    newMessage.setOnClickListener(mClick);
        drafts.setOnClickListener(mClick);
        inbox.setOnClickListener(mClick);
        outbox.setOnClickListener(mClick);
        newBtn.setOnClickListener(mClick);
	}
        
        OnClickListener mClick = new OnClickListener() {
        	@Override
        	public void onClick(View v){    	
        		Context context = getApplicationContext();
        		toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        		if(v.getId() == R.id.newMain)
        		{
        			openComposerActivity();
        		}
        		else if(v.getId() == R.id.draftsMain)
        		{  			
        			toast.setText("Clicking this button allows the user to view their drafts.");
        			toast.show();
        		}
        		else if(v.getId() == R.id.inboxMain)
        		{
        			toast.setText("Clicking this button allows the user to view their inbox.");
        			toast.show();
        		}
        		else if(v.getId() == R.id.outboxMain)
        		{
        			toast.setText("Clicking this button allows the user to view their outbox.");
        			toast.show();
        		}
        		else
        		{
        			// Open the Hello World form
        			openHelloActivity ( );    			
        		}
        	}
        };
        
        private void openHelloActivity ( )
        {
    		Intent mHelloIntent = new Intent ( getApplicationContext(), HelloActivity.class );
    		mHelloIntent.putExtra( "Hello Activity", "" );
    		startActivity ( mHelloIntent ); 
        }
        
        private void openComposerActivity()
        {
    		Intent mComposerIntent = new Intent ( getApplicationContext(), ComposerActivity.class );
    		mComposerIntent.putExtra( "Composer Activity", "" );
    		startActivity ( mComposerIntent ); 
        }
	}