 
package com.team1;

import java.io.File;

import com.team1.Smil.SmilConstants;
import com.team1.composer.ComposerActivity;
import com.team1.player.SmilPlayerActivity;
import com.team1.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Main extends Activity
{
    Toast toast;

    protected static final int BROWSE_TYPE_DRAFT  = 0;
    protected static final int BROWSE_TYPE_INBOX  = 1;
    protected static final int BROWSE_TYPE_OUTBOX = 2;
    
    protected static final String ACTION_EDIT = "edit";
    protected static final String ACTION_PLAY = "play";
    protected static final String ACTION_DELETE = "delete";
    
    private static final int COMPOSER = 1;
    private static final int EXIT     = 0;

    private static final int FILE_BROWSER = 1;
    
    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );

        Button newMessage = ( Button ) findViewById ( R.id.newMain    );
        Button drafts     = ( Button ) findViewById ( R.id.draftsMain );
        Button inbox      = ( Button ) findViewById ( R.id.inboxMain  );
        Button outbox     = ( Button ) findViewById ( R.id.outboxMain );

        newMessage.setOnClickListener ( mClick );
        drafts.setOnClickListener     ( mClick );
        inbox.setOnClickListener      ( mClick );
        outbox.setOnClickListener     ( mClick );
        
        try
        {
            File appDir;
            File pathDir = Environment.getExternalStorageDirectory ( );
            appDir = new File ( pathDir, SmilConstants.DRAFT_PATH );
            appDir.mkdirs ( );
            appDir = new File ( pathDir, SmilConstants.INBOX_PATH );
            appDir.mkdirs ( );
            appDir = new File ( pathDir, SmilConstants.OUTBOX_PATH );
            appDir.mkdirs ( );
            appDir = new File ( pathDir, SmilConstants.MEDIA_PATH );
            appDir.mkdirs ( );
        }
        catch ( Exception e )
        {
        }

    }

    OnClickListener mClick = new OnClickListener ( )
    {
       @Override
       public void onClick( View v )
       {
           Context context = getApplicationContext ( );
           
           toast = Toast.makeText( context, "", Toast.LENGTH_SHORT );
           
           // Determine which action the user wishes to take
           if ( v.getId() == R.id.newMain )
           {
               openComposerActivity ( "" );
           }
           else if ( v.getId() == R.id.draftsMain )
           {
               openFileBrowserActivity ( BROWSE_TYPE_DRAFT );
           }
           else if ( v.getId() == R.id.inboxMain )
           {
               openFileBrowserActivity ( BROWSE_TYPE_INBOX );
           }
           else if ( v.getId() == R.id.outboxMain )
           {
               openFileBrowserActivity ( BROWSE_TYPE_OUTBOX );
           }
       }
   };

    private void openFileBrowserActivity ( int type )
    {
        Intent browseIntent = new Intent( getApplicationContext(), FileBrowserActivity.class );
        browseIntent.putExtra( "BROWSE", type );
        startActivityForResult ( browseIntent, FILE_BROWSER );
    }

    private void openComposerActivity ( String fileName )
    {
        Intent mComposerIntent = new Intent( getApplicationContext(), ComposerActivity.class );
        
        // If a file name is passed into the composer activity, the SMIL components should be 
        // pre-loaded into the composer.
        if ( fileName != "" )
        {
            mComposerIntent.putExtra ( "fileName", fileName );
        }
        
        startActivity ( mComposerIntent );
    }
    
    private void openPlayerActivity ( String fileName )
    {
        Intent mPlayerIntent = new Intent( getApplicationContext(), SmilPlayerActivity.class );
        
        // If a file name is passed into the composer activity, the SMIL components should be 
        // pre-loaded into the composer.
        if ( fileName != "" )
        {
            mPlayerIntent.putExtra ( "playFile", fileName );
        }
        
        startActivity ( mPlayerIntent );
    }


    public boolean onCreateOptionsMenu ( Menu menu )
    {
        menu.add( 0, COMPOSER, 0, "New Message" );
        menu.add( 0, EXIT, 0, "Exit" );
        return true;
    }

    public boolean onOptionsItemSelected ( MenuItem item )
    {
        switch ( item.getItemId ( ) )
        {
            case EXIT:
                finish ( );
                android.os.Process.killProcess ( android.os.Process.myPid() );
                return true;
            case COMPOSER:
                openComposerActivity ( "" );
                break;
        }
        return false;
    }
    
    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent intent ) 
    {
        super.onActivityResult ( requestCode, resultCode, intent );
        
        switch ( requestCode )
        {                
            case FILE_BROWSER:
            {
                if ( ( RESULT_OK == resultCode ) &&
                     ( null != intent ) )
                {
                    if(intent.hasExtra( "ACTION" ))
                    {
                        Bundle extras = intent.getExtras();
                        String action = extras.getString( "ACTION" );
                        String filePath = extras.getString( "FILE" );
                        if(action.equals( ACTION_PLAY ))
                        {
                            openPlayerActivity( filePath );
                        }
                        else if(action.equals( ACTION_EDIT ))
                        {
                            openComposerActivity( filePath );
                        }
                        else if(action.equals( ACTION_DELETE ))
                        {
                            File file = new File(filePath);
                            file.delete();
                        }
                    }
                    
                }
                
                break;
            }
        }
    }
}