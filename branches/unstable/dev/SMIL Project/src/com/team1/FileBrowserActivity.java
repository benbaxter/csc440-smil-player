package com.team1;

import java.io.File;
import java.util.ArrayList;

import com.team1.R;
import com.team1.Smil.SmilConstants;

import android.app.Activity;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FileBrowserActivity extends ListActivity 
{
	private String filePath;
	private ArrayList<String> smilFiles = new ArrayList<String>();
	
	
	@Override
	protected void onCreate ( Bundle savedInstanceState ) 
	{
		super.onCreate ( savedInstanceState );
		
		// Look up the notification manager service
        NotificationManager manager = (NotificationManager) getSystemService ( NOTIFICATION_SERVICE );
    
        // Cancel the notification that we started in IncomingMessage
        manager.cancel ( 9 );
		
        // Allow for the background to show through when scrolling is happening
        this.getListView().setCacheColorHint ( Color.TRANSPARENT );

        // Determine where to look (ie Inbox, Outbox, Drafts )
        File pathDir = Environment.getExternalStorageDirectory ( );
        File dir = new File ( pathDir, SmilConstants.DRAFT_PATH );
        
        Intent in = getIntent ( );
        if ( in.hasExtra ( "browseType" ) )
        {
            String type = in.getExtras().getString ( "browseType" );
            if ( Main.BROWSE_TYPE_OUTBOX.equals(type))
            {
                filePath = SmilConstants.OUTBOX_PATH;
                dir = new File ( pathDir, filePath );
            }
            else if ( Main.BROWSE_TYPE_INBOX.equals(type))
            {
                filePath = SmilConstants.INBOX_PATH;
                dir = new File ( pathDir, filePath );                
            }
        }

        filePath = dir.getAbsolutePath().toString();
        
		String[] allFiles = dir.list ( );
		if ( allFiles != null )
		{
			// Find all of the .smil files in this directory
			for ( int index = 0; index < allFiles.length; index++ )
			{
				if ( allFiles[index].endsWith ( ".smil" ) == true )
					smilFiles.add ( allFiles[index] );
			}
		}

		setContentView ( R.layout.simple_list );
		FileBrowserAdapter adapter = new FileBrowserAdapter ( this, android.R.layout.simple_list_item_1, smilFiles );
		setListAdapter ( adapter );

		// Set the title
        if ( in.hasExtra ( "browseType" ) )
        {
            String title = in.getExtras().getString ( "browseType" );
            TextView tv = (TextView)findViewById ( R.id.Title );
            tv.setText ( title );
        }

		ListView messageView = getListView ( );
		
		messageView.setOnItemClickListener ( new OnItemClickListener ( ) 
		{
		    public void onItemClick ( AdapterView<?> parent, View view, int position, long id ) 
		    {
                Intent returnIntent = new Intent ( );

			    returnIntent.putExtra ( "fileName", filePath + "/" + ((TextView) view).getText().toString ( ) );
			    setResult ( RESULT_OK, returnIntent );
			    finish ( );
		    }
		});
	}
	
	private class FileBrowserAdapter extends ArrayAdapter<String> 
	{
	    private Activity context;
	    private ArrayList<String> items;
	    
	    public FileBrowserAdapter ( Activity context, int textViewId, ArrayList<String> items ) 
	    {
	        super ( context, textViewId, items );
	        this.context = context;
	        this.items = items;
	    }
	    
	    @Override
	    public View getView ( int position, View convertView, ViewGroup parent ) 
	    {
	        View view = convertView;
	        if ( view == null ) 
	        {
	            LayoutInflater layout = (LayoutInflater)context.getSystemService ( Context.LAYOUT_INFLATER_SERVICE );
	            view = layout.inflate ( android.R.layout.simple_list_item_1, null );
	        }
	        
	        TextView text = (TextView)view.findViewById ( android.R.id.text1 );
	        text.setText ( items.get ( position ) );
	        
	        return view;
	    }
	}
}
