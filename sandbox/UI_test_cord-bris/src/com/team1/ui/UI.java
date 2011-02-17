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
	Toast toast;
	
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
    		//toast.cancel();
    		Context context = getApplicationContext();
    		toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    		if(v.getId() == R.id.addBtn)
    		{
    			toast.setText("Clicking this button allows the user to add new objects to the Canvas.");
    			toast.show();
    		}
    		else if(v.getId() == R.id.saveBtn)
    		{
    			toast.setText("Clicking this button allows the user save their current message.");
    			toast.show();
    		}
    		else if(v.getId() == R.id.undoBtn)
    		{
    			toast.setText("Clicking this button allows the user undo thier last change.");
    			toast.show();
    		}
    		else if(v.getId() == R.id.sendBtn)
    		{
    			toast.setText("Clicking this button allows the user send the message to a friend.");
    			toast.show();
    		}
    		else
    		{
    			toast.setText("Clicking this button creates a new message.");
        		toast.show();
    		}

    	}
    };
}