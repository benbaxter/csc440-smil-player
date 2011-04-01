
package com.team1;

import com.team1.composer.ComposerActivity;
import com.team1.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Main extends Activity
{
    Toast toast;

    private static final String BROWSE_TYPE_DRAFT  = "Browse Drafts";
    private static final String BROWSE_TYPE_INBOX  = "Browse Inbox";
    private static final String BROWSE_TYPE_OUTBOX = "Browse Outbox";
    
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
    }

    OnClickListener mClick = new OnClickListener()
   {
       @Override
       public void onClick( View v )
       {
           Context context = getApplicationContext();
           toast = Toast.makeText( context, "", Toast.LENGTH_SHORT );
           if ( v.getId() == R.id.newMain )
           {
               openComposerActivity();
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

    private void openFileBrowserActivity ( String type )
    {
        Intent mBrowseIntent = new Intent( getApplicationContext(), FileBrowserActivity.class );
        mBrowseIntent.putExtra( "browseType", type );
        startActivity ( mBrowseIntent );
    }

    private void openComposerActivity()
    {
        Intent mComposerIntent = new Intent( getApplicationContext(), ComposerActivity.class );
        mComposerIntent.putExtra( "Composer Activity", "" );
        startActivity( mComposerIntent );
    }

    static final int COMPOSER = 1;
    static final int EXIT     = 0;

    public boolean onCreateOptionsMenu( Menu menu )
    {
        menu.add( 0, COMPOSER, 0, "New Message" );
        menu.add( 0, EXIT, 0, "Exit" );
        return true;
    }

    public boolean onOptionsItemSelected( MenuItem item )
    {
        switch ( item.getItemId() )
        {
            case EXIT:
                finish();
                android.os.Process.killProcess( android.os.Process.myPid() );
                return true;
            case COMPOSER:
                openComposerActivity();
                break;
        }
        return false;
    }
}