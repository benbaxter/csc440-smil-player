package com.team1;

import java.io.File;
import java.util.ArrayList;

import com.team1.Smil.SmilReader;
import com.team1.composer.R;

import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FileBrowserActivity extends ListActivity {
	
	private ArrayList<String> mSmilFiles = new ArrayList<String>();
	private String mFileNameKey;
	private String mFileNameValue;
	private boolean mReturnValue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		// look up the notification manager service
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    
        // cancel the notification that we started in IncomingMessage
        nm.cancel(9);
		
        //this will allow for the background to show through when scrolling is happening
        this.getListView().setCacheColorHint ( Color.TRANSPARENT );
        
        File sdcard = new File ( Environment.getExternalStorageDirectory().toString() );
		
		String[] allFiles = sdcard.list ( );
		
		//are there any files in the sd card root directory
		if ( allFiles != null )
		{
			//filter them based upon file type
			for ( int index = 0; index < allFiles.length; index++ )
			{
				if ( allFiles[index].endsWith ( ".smil" ) == true )
					mSmilFiles.add ( allFiles[index] );
			}
		}

		setContentView ( R.layout.simple_list );
		FileBrowserAdapter adapter = new FileBrowserAdapter ( this, android.R.layout.simple_list_item_1, mSmilFiles );
		setListAdapter ( adapter );

        Intent in = getIntent ( );
        if ( in.hasExtra ( "browseType" ) )
        {
            String title = in.getExtras().getString ( "browseType" );
            TextView tv = (TextView)findViewById ( R.id.Title );
            tv.setText ( title );
        }

		ListView messageView = getListView ( );
		
		messageView.setOnItemClickListener ( new OnItemClickListener ( ) 
		{
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
		    {
		    		mFileNameValue = ((TextView) view).getText().toString();
		    		
		    		//for use by another activity
			    	if ( mReturnValue )
			    	{
			    		Intent file = new Intent();
			    		file.putExtra(mFileNameKey, mFileNameValue);
			    		setResult(RESULT_OK, file);
			    		finish();
			    	}
		    }
		});
	}
	
}
