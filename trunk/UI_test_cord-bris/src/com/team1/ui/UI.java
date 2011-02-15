package com.team1.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class UI extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button add = (Button)findViewById(R.id.addBtn);
        Button save = (Button)findViewById(R.id.saveBtn);
        Button undo = (Button)findViewById(R.id.undoBtn);
        Button send = (Button)findViewById(R.id.sendBtn);
        Button newBtn = (Button)findViewById(R.id.newBtn);
        add.setOnClickListener(mClick);
        save.setOnClickListener(mClick);
        undo.setOnClickListener(mClick);
        send.setOnClickListener(mClick);
        newBtn.setOnClickListener(mClick);
    }
    
    OnClickListener mClick = new OnClickListener() {
    	public void onClick(View v){
    		Context context = getApplicationContext();
    		int duration = Toast.LENGTH_SHORT;
    		CharSequence text = "";
    		if(v.getId() == R.id.addBtn)
    		{
    			text = "Clicking this button allows the user to add new objects to the Canvas.";
    		}
    		else if(v.getId() == R.id.saveBtn)
    		{
    			text = "Clicking this button allows the user save their current message.";
    		}
    		else if(v.getId() == R.id.undoBtn)
    		{
    			text = "Clicking this button allows the user undo thier last change.";
    		}
    		else if(v.getId() == R.id.sendBtn)
    		{
    			text = "Clicking this button allows the user send the message to a friend.";
    		}
    		else
    		{
    			text = "Clicking this button creates a new message.";
    		}

    		Toast toast = Toast.makeText(context, text, duration);
    		toast.show();
    	}
    };
}