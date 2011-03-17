
package com.team1.composer;

import java.util.StringTokenizer;

import com.team1.composer.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

public class MediaPropertiesActivity extends Activity
{
    static final int EXIT   = 0;

    private Media media;
    private int index;
    private Bitmap image;

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.mediaproperties );

        Button okBtn = ( Button ) findViewById( R.id.okBtn );
        Button cancelBtn = ( Button ) findViewById( R.id.cancelBtn );

        okBtn.setOnClickListener( mClick );
        cancelBtn.setOnClickListener( mClick );

        Button backBtn = ( Button ) findViewById( R.id.backBtn );
        backBtn.setOnClickListener( mClick );
        
        Button rotateBtn = ( Button ) findViewById( R.id.rotateBtn );
        rotateBtn.setOnClickListener( mClick );
        
        Bundle extras = getIntent().getExtras();
        
        if(extras != null)
        {
            if(extras.containsKey("INDEX"))
            {
                index = extras.getInt( "INDEX" );
                media = ComposerActivity.getMedia().get( index );
            }
            else if(extras.containsKey("PATH"))
            {
                media = ComposerActivity.getMedia().getLast();
                
                String path = extras.getString("PATH");
                media.setPath( path );
                StringTokenizer st = new StringTokenizer(media.getPath(), "/");
                while (st.hasMoreTokens()) {
                    media.setFileName(st.nextToken());
                }
            }
        }
        else
            media = ComposerActivity.getMedia().getLast();
        
        if( media.getMediaType() == Media.AUDIO_TYPE) {
            findViewById( R.id.repeatInfo ).setVisibility( View.VISIBLE );
        } 
        else if ( media.getMediaType() == Media.TEXT_TYPE ) {
            //font size drop down.
            //This code will also set the font size
            Spinner spinner = (Spinner) findViewById(R.id.fontSizeSpinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this, R.array.font_size_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new OnItemSelectedListener()
            {
                @Override
                public void onItemSelected( AdapterView<?> parent, View view, int pos, long id )
                {
                    int size = Integer.parseInt( parent.getItemAtPosition(pos).toString() );
                    media.setFontSize( size );
                }
                @Override
                public void onNothingSelected( AdapterView<?> arg0 )
                {
                    
                }
              });
            
            final RadioButton radio_horizontal = (RadioButton) findViewById(R.id.radio_horizontal);
            final RadioButton radio_veritical = (RadioButton) findViewById(R.id.radio_vertical);
            radio_horizontal.setOnClickListener(radio_listener);
            radio_veritical.setOnClickListener(radio_listener);
            findViewById( R.id.inputStringInfo).setVisibility( View.VISIBLE );
            findViewById( R.id.textInfo).setVisibility( View.VISIBLE );
            findViewById( R.id.fontSizeInfo ).setVisibility( View.VISIBLE );
            findViewById( R.id.orientationInfo ).setVisibility( View.VISIBLE );
            
            EditText et = (EditText)findViewById(R.id.inputString);
            
            if(extras != null)
            {
                et.setText( media.getText() );
                et = (EditText)findViewById( R.id.x );
                et.setText( Integer.toString( media.getX() ) );
                et = (EditText)findViewById( R.id.y );
                et.setText( Integer.toString( media.getY() ) );
            }
            else
                et.setText( null );
        } 
        else if (media.getMediaType() == Media.IMAGE_TYPE ) {
            if(extras.containsKey("INDEX"))
            {
                EditText et = (EditText)findViewById( R.id.height);
                et.setText( Integer.toString( media.getHeight() ) );
                et = (EditText)findViewById( R.id.width );
                et.setText( Integer.toString( media.getWidth() ) );
                et = (EditText)findViewById( R.id.x );
                et.setText( Integer.toString( media.getX() ) );
                et = (EditText)findViewById( R.id.y );
                et.setText( Integer.toString( media.getY() ) );
                image = media.getImage();
            }
            else
            {
                EditText et = (EditText)findViewById( R.id.height);
                et.setText( "150" );
                et = (EditText)findViewById( R.id.width );
                et.setText( "150" );
                image = BitmapFactory.decodeFile( media.getPath() );
            }
            
            findViewById( R.id.hwInfo ).setVisibility( View.VISIBLE );
            findViewById( R.id.rotation).setVisibility( View.VISIBLE );
            
            TextView tv = (TextView)findViewById( R.id.Title );
            tv.setText(media.getFileName());
            
        } 
        else if (media.getMediaType() == Media.VIDEO_TYPE) {
            findViewById( R.id.repeatInfo ).setVisibility( View.VISIBLE );
            findViewById( R.id.hwInfo ).setVisibility( View.VISIBLE );
        }
        
        
        findViewById( R.id.x ).setEnabled( false );
        findViewById( R.id.y ).setEnabled( false );
        findViewById( R.id.xLabel ).setEnabled( false );
        findViewById( R.id.yLabel ).setEnabled( false );
        findViewById( R.id.xyInfo ).setEnabled( false );
        findViewById( R.id.optional ).setEnabled( false );
    }
    
    //radio button listener
    //this code will also set the orientation of the text
    private OnClickListener radio_listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //Reference for vertical text
            //http://stackoverflow.com/questions/1258275/vertical-rotated-label-in-android
            //http://code.google.com/p/chartdroid/
            
            RadioButton rb = (RadioButton) v;
            if( rb.getText().equals( R.string.horizontal_label ) )
            {
                media.setOrientation( Media.HORIZONTAL );
            }
            else 
            {
                media.setOrientation( Media.VERTICAL );
            }
        }
    };

    OnClickListener  mClick = new OnClickListener()
    {
        @Override
        public void onClick( View v )
        {
            if ( v.getId() == R.id.okBtn )
            {
                // media =
                // ComposerActivity.getMedia().getLast();
                if( media.getMediaType() == Media.AUDIO_TYPE )
                {
                    media.setRepeat( ((CheckBox)findViewById( R.id.repeatCheckBox )).isChecked() );
                }
                else if ( media.getMediaType() == Media.TEXT_TYPE )
                {
                    media.setText( ( (EditText)findViewById( R.id.inputString ) ).getText().toString() );
//                    Log.i( "TEXT2", ComposerActivity.getMedia().getLast().getText() );
                }
                else if( media.getMediaType() == Media.IMAGE_TYPE)
                {
                    String height = ((EditText)findViewById( R.id.height )).getText().toString();
                    String width = ((EditText)findViewById( R.id.width )).getText().toString();
                    //set default width and height here
                    if( height.equals( "" ))
                        height = "200";
                    if( width.equals( "" ))
                        width = "200";
                    media.setHeight( Integer.parseInt( height ));
                    media.setWidth( Integer.parseInt( width ));
                    media.setImage( image );
                }
                else if( media.getMediaType() == Media.VIDEO_TYPE)
                {
                    media.setRepeat( ((CheckBox)findViewById( R.id.repeatCheckBox )).isChecked() );
                    String height = ((EditText)findViewById( R.id.height )).getText().toString();
                    String width = ((EditText)findViewById( R.id.width )).getText().toString();
                    //set default width and height here
                    if( height.equals( "" ))
                        height = "40";
                    if( width.equals( "" ))
                        width = "40";
                    media.setHeight( Integer.parseInt( height ));
                    media.setWidth( Integer.parseInt( width ));
                }
                
                String startTime = ((EditText)findViewById( R.id.startTime )).getText().toString();
                String dur = ((EditText)findViewById( R.id.duration )).getText().toString();
                if( startTime.equals( "" ))
                    startTime= "0";
                if( dur.equals( "" ))
                    dur = "0";
                media.setStartTime( Integer.parseInt(startTime) );
                media.setDuration( Integer.parseInt(dur) );
                
                Intent data = new Intent();
                data.putExtra("INDEX", index);
                
                setResult(RESULT_OK, data);
                finish();
            }
            else if ( v.getId() == R.id.rotateBtn )
            {
                int w = image.getWidth();
                int h = image.getHeight();
                Matrix mtx = new Matrix();
                mtx.postRotate(90);
                image = Bitmap.createBitmap(image, 0, 0, w, h, mtx, true);
                toast("Image rotated 90 degrees");
            }
            else if ( v.getId() == R.id.cancelBtn )
            {
                setResult( RESULT_CANCELED );
                finish();
            }
            else if ( v.getId() == R.id.backBtn )
            {
                setResult( RESULT_CANCELED );
                finish();
            }
        }
    };

    public boolean onCreateOptionsMenu( Menu menu )
    {
        menu.add( 0, EXIT, 0, "Exit" );
        return true;
    }

    public boolean onOptionsItemSelected( MenuItem item )
    {
        switch ( item.getItemId() )
        {
            case EXIT:
                finish();
                return true;
        }
        return false;
    }
    
    public void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}