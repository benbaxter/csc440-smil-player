
package com.team1.maintocomposer;

import com.team1.maintocomposer.HelloActivity;
import com.team1.maintocomposer.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;
import android.content.Intent;

public class ComposerActivity extends Activity
{
    /** Called when the activity is first created. */
    Toast toast;
    View  pawn;

    @Override
    public void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate ( savedInstanceState );

        pawn = new View ( this );

        setContentView ( R.layout.composer );

        Button add = ( Button ) findViewById ( R.id.addBtn );
        Button save = ( Button ) findViewById ( R.id.saveBtn );
        Button undo = ( Button ) findViewById ( R.id.undoBtn );
        Button send = ( Button ) findViewById ( R.id.sendBtn );
        Button newBtn = ( Button ) findViewById ( R.id.newBtn );

        add.setOnClickListener ( mClick );
        save.setOnClickListener ( mClick );
        undo.setOnClickListener ( mClick );
        send.setOnClickListener ( mClick );
        newBtn.setOnClickListener ( mClick );
    }

    OnClickListener mClick = new OnClickListener ( )
                           {
                               @Override
                               public void onClick ( View v )
                               {
                                   Context context = getApplicationContext ( );
                                   toast = Toast.makeText ( context, "",
                                           Toast.LENGTH_SHORT );
                                   if ( v.getId ( ) == R.id.addBtn )
                                   {
                                       FrameLayout fl = ( FrameLayout ) findViewById ( R.id.Canvas );
                                       LayoutInflater inflater = ( LayoutInflater ) getSystemService ( LAYOUT_INFLATER_SERVICE );
                                       View itemView = inflater.inflate (
                                               R.layout.image_add, null );

                                       View image = ( View ) itemView
                                               .findViewById ( R.id.image );
                                       image.setBackgroundResource ( R.drawable.icon );

                                       fl.addView ( itemView,
                                               new FrameLayout.LayoutParams (
                                                       40, 40 ) );

                                       pawn = findViewById ( R.id.image );
                                       pawn.setOnTouchListener ( drag );
                                   }
                                   else if ( v.getId ( ) == R.id.saveBtn )
                                   {
                                       toast.setText ( "Clicking this button allows the user save their current message." );
                                       toast.show ( );
                                   }
                                   else if ( v.getId ( ) == R.id.undoBtn )
                                   {
                                       toast.setText ( "Clicking this button allows the user undo thier last change." );
                                       toast.show ( );
                                   }
                                   else if ( v.getId ( ) == R.id.sendBtn )
                                   {
                                       EditText addrTxt = ( EditText ) ComposerActivity.this.findViewById ( R.id.addrEditText );
                                       addrTxt.setFocusable ( false );

                                       try
                                       {
                                           sendSmsMessage ( addrTxt.getText ( ) .toString ( ), "hello test" );

                                           Toast.makeText (ComposerActivity.this, "SMS Sent", Toast.LENGTH_LONG ).show ( );
                                       }
                                       catch ( Exception e )
                                       {
                                           Toast.makeText (
                                                   ComposerActivity.this,
                                                   "Failed to send SMS",
                                                   Toast.LENGTH_LONG ).show ( );
                                           e.printStackTrace ( );
                                       }
                                   }
                                   else
                                   {
                                       // Open the Hello World form
                                       openHelloActivity ( );
                                   }

                               }
                           };

    // =========================================================
    // Function: openHelloActivity ( )
    // Description: Starts the HelloActivity class/form
    // Parameters: None
    // Returns: None
    // =========================================================

    private void openHelloActivity ( )
    {
        Intent mHelloIntent = new Intent ( getApplicationContext ( ),
                HelloActivity.class );
        mHelloIntent.putExtra ( "Hello Activity", "" );
        startActivity ( mHelloIntent );
    }

    void sendSmsMessage ( String address, String message ) throws Exception
    {
        SmsManager smsMgr = SmsManager.getDefault ( );
        smsMgr.sendTextMessage ( address, null, message, null, null );
        finish ( );
    }

    OnTouchListener  drag = new OnTouchListener ( )
                          {
                              @Override
                              public boolean onTouch ( View v, MotionEvent event )
                              {
                                  FrameLayout.LayoutParams par = ( LayoutParams ) v
                                          .getLayoutParams ( );
                                  switch ( v.getId ( ) )
                                  {// What is being touched
                                      case R.id.image:
                                      {// Which action is being taken
                                          switch ( event.getAction ( ) )
                                          {
                                              case MotionEvent.ACTION_MOVE:
                                              {
                                                  par.topMargin = ( int ) event
                                                          .getRawY ( )
                                                          - ( v.getHeight ( ) );
                                                  par.leftMargin = ( int ) event
                                                          .getRawX ( )
                                                          - ( v.getWidth ( ) / 2 );
                                                  v.setLayoutParams ( par );
                                                  break;
                                              }// inner case MOVE
                                              case MotionEvent.ACTION_UP:
                                              {
                                                  par.height = 40;
                                                  par.width = 40;
                                                  par.topMargin = ( int ) event
                                                          .getRawY ( )
                                                          - ( v.getHeight ( ) );
                                                  par.leftMargin = ( int ) event
                                                          .getRawX ( )
                                                          - ( v.getWidth ( ) / 2 );
                                                  v.setLayoutParams ( par );
                                                  break;
                                              }// inner case UP
                                              case MotionEvent.ACTION_DOWN:
                                              {
                                                  par.height = 60;
                                                  par.width = 60;
                                                  v.setLayoutParams ( par );
                                                  break;
                                              }// inner case UP
                                          }// inner switch
                                          break;
                                      }// case image
                                  }// switch
                                  return true;
                              }// onTouch
                          };  // drag

    static final int MAIN = 1;
    static final int EXIT = 0;

    public boolean onCreateOptionsMenu ( Menu menu )
    {
        menu.add ( 0, MAIN, 0, "Main Menu" );
        menu.add ( 0, EXIT, 0, "Exit" );
        return true;
    }

    public boolean onOptionsItemSelected ( MenuItem item )
    {
        switch ( item.getItemId ( ) )
        {
            case EXIT:
                System.exit ( 0 );
                return true;
            case MAIN:
                finish ( );
                break;
        }
        return false;
    }
}