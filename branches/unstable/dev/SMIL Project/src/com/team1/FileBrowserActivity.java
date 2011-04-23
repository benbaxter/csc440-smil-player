package com.team1;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TreeMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.team1.Smil.SmilConstants;


public class FileBrowserActivity extends ListActivity 
{
	private String filePath;
	private TreeMap<String, String> smilFilesMap = new TreeMap<String, String>();
	private Intent in;
	private Dialog dialog = null;
	private FileBrowserAdapter adapter;
	private Bundle instance;
	
	private void refresh ( )
	{
	    super.onCreate ( instance );
	    
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
                {
                    String baseName = allFiles[index];
                    
                    String displayName = baseName.substring( 0, baseName.indexOf( "_" ) ) + "\n";
                    
                    long milliseconds = Long.parseLong( baseName.substring( baseName.indexOf( "_" ) + 1, baseName.indexOf( "." ) ) );
                    Calendar time = Calendar.getInstance();
                    time.setTimeInMillis( milliseconds );
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("M/dd hh:mm");
                    displayName += sdf.format( time.getTime() );
                    if ( Main.BROWSE_TYPE_DRAFT == type)
                        smilFilesMap.put( baseName, baseName );
                    else
                        smilFilesMap.put( baseName, displayName );
                }
            }
        }

        setContentView ( R.layout.simple_list );
		adapter = new FileBrowserAdapter ( this, smilFilesMap );
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
	@Override
	protected void onCreate ( Bundle savedInstanceState ) 
	{
		this.instance = savedInstanceState;
		refresh();
	}
	
	protected Dialog onCreateDialog(int id ) {
        switch (id) {
        case Main.BROWSE_TYPE_DRAFT:
            final String[] items = { "Edit", "Delete" };

            AlertDialog.Builder buildSelecter = new AlertDialog.Builder(this);
            buildSelecter.setTitle("Pick Option");
            buildSelecter.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    Intent data = new Intent();
                    
                    if( item == 0 )
                    {
                        data.putExtra("ACTION", Main.ACTION_EDIT);
                        data.putExtra( "FILE", filePath );
                        setResult( RESULT_OK, data );
                        finish();
                    }
                    else if ( item == 1 )
                    {
                        deleteFileAndRefresh( filePath );
                    }
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
                    {
                        data.putExtra("ACTION", Main.ACTION_PLAY);
                        data.putExtra( "FILE", filePath );
                        
                        setResult( RESULT_OK, data );
                        finish();
                    }
                    else if ( item == 1 )
                        deleteFileAndRefresh( filePath );
                    
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
                    {
                        data.putExtra("ACTION", Main.ACTION_PLAY);
                        data.putExtra( "FILE", filePath );

                        setResult( RESULT_OK, data );
                        finish();

                    }
                    else if ( item == 1 )
                    {
                        data.putExtra("ACTION", Main.ACTION_EDIT);
                        data.putExtra( "FILE", filePath );

                        setResult( RESULT_OK, data );
                        finish();
                    }
                    else if ( item == 2 )
                        deleteDatabase( filePath );
                    
                                    }
            });
            dialog = buildSelecter2.create();
            break;
        }
        return dialog;
	}
	
	private void deleteFileAndRefresh( String filename )
    {
        File file = new File ( filename );
        file.delete ( );
        adapter.clearView ( );
        refresh ( );
    }
	
	private class FileBrowserAdapter extends BaseAdapter 
	{
	    private TreeMap<String, String> mData = new TreeMap<String, String>();
        private String[] mKeys;
        private Activity context;
        
        public FileBrowserAdapter(Activity context, TreeMap<String, String> data){

            this.context = context;
            mData  = data;
            mKeys = mData.keySet().toArray(new String[data.size()]);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(mKeys[position]);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            String key = mKeys[pos];
            String value = getItem(pos).toString();

            View view = convertView;
            if ( view == null ) 
            {
                LayoutInflater layout = (LayoutInflater)context.getSystemService ( Context.LAYOUT_INFLATER_SERVICE );
                view = layout.inflate ( android.R.layout.simple_list_item_1, null );
            }
            
            TextView text = (TextView)view.findViewById ( android.R.id.text1 );
            text.setText ( value );
            text.setTag( key );
            
            return view;
        }
	    public void clearView ( ) 
	    {            
	        mData.clear ( );            
	        notifyDataSetChanged ( );        
	    }
	}
}
