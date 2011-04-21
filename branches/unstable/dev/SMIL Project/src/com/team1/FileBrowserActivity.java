package com.team1;

import java.io.File;
import java.util.ArrayList;

import com.team1.R;
import com.team1.Smil.SmilConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
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
	private Intent in;
	private Dialog dialog = null;
	
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
        
        in = getIntent ( );
        Bundle extras = in.getExtras();
        final int type = extras.getInt( "BROWSE" );
        
        if ( in.hasExtra ( "BROWSE" ) )
        {
            if ( Main.BROWSE_TYPE_OUTBOX == type)
            {
                filePath = SmilConstants.OUTBOX_PATH;
                dir = new File ( pathDir, filePath );
            }
            else if ( Main.BROWSE_TYPE_INBOX == type)
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
        if ( in.hasExtra ( "BROWSE" ) )
        {
            TextView tv = (TextView)findViewById ( R.id.Title );
            
            if( Main.BROWSE_TYPE_DRAFT == type )
                tv.setText ( "Drafts" );
            else if( Main.BROWSE_TYPE_INBOX == type )
                tv.setText ( "Inbox" );
            else if( Main.BROWSE_TYPE_OUTBOX == type )
                tv.setText ( "Drafts" );
        }

		ListView messageView = getListView ( );
		
		messageView.setOnItemClickListener ( new OnItemClickListener ( ) 
		{
		    public void onItemClick ( AdapterView<?> parent, View view, int position, long id ) 
		    {
		        filePath = filePath + "/" + ((TextView)view).getText().toString();
		        showDialog( type );
		    }
		});
	}
	
	protected Dialog onCreateDialog(int id) {
        switch (id) {
        case Main.BROWSE_TYPE_DRAFT:
            final String[] items = { "Edit", "Delete" };

            AlertDialog.Builder buildSelecter = new AlertDialog.Builder(this);
            buildSelecter.setTitle("Pick Option");
            buildSelecter.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    Intent data = new Intent();
                    
                    if( item == 0 )
                        data.putExtra("ACTION", Main.ACTION_EDIT);
                    else if ( item == 1 )
                        data.putExtra("ACTION", Main.ACTION_DELETE);
                    
                    data.putExtra( "FILE", filePath );

                    setResult( RESULT_OK, data );
                    finish();
                }
            });
            dialog = buildSelecter.create();
            break;
        case Main.BROWSE_TYPE_INBOX:
            final String[] items1 = { "Play", "Delete" };

            AlertDialog.Builder buildSelecter1 = new AlertDialog.Builder(this);
            buildSelecter1.setTitle("Pick Option");
            buildSelecter1.setItems(items1, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    Intent data = new Intent();
                    
                    if( item == 0 )
                        data.putExtra("ACTION", Main.ACTION_PLAY);
                    else if ( item == 1 )
                        data.putExtra("ACTION", Main.ACTION_DELETE);
                    
                    data.putExtra( "FILE", filePath );

                    setResult( RESULT_OK, data );
                    finish();
                }
            });
            dialog = buildSelecter1.create();
            break;
        case Main.BROWSE_TYPE_OUTBOX:
            final String[] items2 = { "Play", "Edit", "Delete" };

            AlertDialog.Builder buildSelecter2 = new AlertDialog.Builder(this);
            buildSelecter2.setTitle("Pick Option");
            buildSelecter2.setItems(items2, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    Intent data = new Intent();
                    
                    if( item == 0 )
                        data.putExtra("ACTION", Main.ACTION_PLAY);
                    else if ( item == 1 )
                        data.putExtra("ACTION", Main.ACTION_EDIT);
                    else if ( item == 2 )
                        data.putExtra("ACTION", Main.ACTION_DELETE);
                    
                    data.putExtra( "FILE", filePath );

                    setResult( RESULT_OK, data );
                    finish();
                }
            });
            dialog = buildSelecter2.create();
            break;
        }
        return dialog;
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
