
package com.team1.composer;

import com.team1.Smil.SmilComponent;
import com.team1.Smil.SmilConstants;
import com.team1.Smil.SmilRegion;
import com.team1.R;
import com.team1.player.*;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

public class MediaPropertiesActivity extends Activity
{
    static final int EXIT   = 0;

    private SmilComponent media;
    private int index;
    private Bitmap image;

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.mediaproperties );

        Button okBtn = ( Button ) findViewById( R.id.okBtn );
        Button cancelBtn = ( Button ) findViewById( R.id.cancelBtn );
        Button backBtn = ( Button ) findViewById( R.id.backBtn );
        Button rotateBtn = ( Button ) findViewById( R.id.rotateBtn );
        Button leftBtn = ( Button ) findViewById( R.id.leftBtn );
        Button rightBtn = ( Button ) findViewById( R.id.rightBtn );

        okBtn.setOnClickListener( mClick );
        cancelBtn.setOnClickListener( mClick );
        backBtn.setOnClickListener( mClick );
        rotateBtn.setOnClickListener( mClick );
        leftBtn.setOnClickListener( mClick );
        rightBtn.setOnClickListener( mClick );

        Bundle extras = getIntent().getExtras();
        
        if(extras != null)
        {
            index = extras.getInt( "INDEX" );
            media = ComposerActivity.getMedia().get( index );
        }
        else
        {
            media = ComposerActivity.getMedia().getLast();
        }
        
        if( media.getType() == SmilConstants.COMPONENT_TYPE_AUDIO) {
            findViewById( R.id.repeatInfo ).setVisibility( View.VISIBLE );
            if(extras != null)
            {
                int count = 0;
                for(int i=0; i<ComposerActivity.getMedia().size(); i++) {
                    if(ComposerActivity.getMedia().get( i ).getType() == SmilConstants.COMPONENT_TYPE_AUDIO) {
                       count++;
                    }
                }
                
                if(count > 1)
                {
                    findViewById( R.id.audioSearch ).setVisibility( View.VISIBLE );
                }
            }   
            else
            {
                int duration = 0;
                MediaPlayer mp = new MediaPlayer();
                try
                {
                    mp.setDataSource(media.getFilePath());
                    mp.prepare();
                    duration = mp.getDuration();
                }
                catch ( Exception e )
                {
                    toast("Bad path for Audio");
                }
                double durationDouble = duration/1000.0;
                duration = ( int ) Math.ceil( durationDouble );
                media.setEnd( duration );
                media.setBegin( 0 );
            }
            
            EditText et = (EditText)findViewById( R.id.startTime );
            et.setText( Integer.toString( media.getBegin() ) );
            et = (EditText)findViewById( R.id.duration);
            et.setText( Integer.toString( media.getEnd() ) );
            
        } 
        else if ( media.getType() == SmilConstants.COMPONENT_TYPE_TEXT ) {
            EditText et;
            if(extras != null)
            {
                et = (EditText)findViewById( R.id.inputString );
                et.setText( media.getText() );
                et = (EditText)findViewById( R.id.x );
                et.setText( Integer.toString( media.getRegion().getRect().left ) );
                et = (EditText)findViewById( R.id.y );
                et.setText( Integer.toString( media.getRegion().getRect().top ) );
            }
            else
            {
                et = (EditText)findViewById( R.id.inputString );
                et.setText( null );
                et = (EditText)findViewById( R.id.x );
                et.setText( "0" );
                et = (EditText)findViewById( R.id.y );
                et.setText( "0" );
                media.setEnd( 1 );
                media.setBegin( 0 );
            }
            
            et = (EditText)findViewById( R.id.duration);
            et.setText( Integer.toString( media.getEnd() ) );
            et = (EditText)findViewById( R.id.startTime );
            et.setText( Integer.toString( media.getBegin() ) );
            //font size drop down.
            //This code will also set the font size
            Spinner spinner = (Spinner) findViewById(R.id.fontSizeSpinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this, R.array.font_size_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(media.getFontSize()/10 - 1);
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
            
            
        } 
        else if (media.getType() == SmilConstants.COMPONENT_TYPE_IMAGE ) {
            if(extras != null)
            {
                EditText et = (EditText)findViewById( R.id.height);
                et.setText( Integer.toString( media.getRegion().getRect().height() ) );
                et = (EditText)findViewById( R.id.width );
                et.setText( Integer.toString( media.getRegion().getRect().width() ) );
                et = (EditText)findViewById( R.id.x );
                et.setText( Integer.toString( media.getRegion().getRect().left ) );
                et = (EditText)findViewById( R.id.y );
                et.setText( Integer.toString( media.getRegion().getRect().top ) );
                image = media.getImage();
            }
            else
            {
                EditText et = (EditText)findViewById( R.id.height);
                et.setText( "150" );
                et = (EditText)findViewById( R.id.width );
                et.setText( "150" );
                et = (EditText)findViewById( R.id.x );
                et.setText( "0" );
                et = (EditText)findViewById( R.id.y );
                et.setText( "0" );
                image = BitmapFactory.decodeFile( media.getFilePath() );
                media.setEnd( 1 );
                media.setBegin( 0 );
            }
            
            EditText et = (EditText)findViewById( R.id.duration);
            et.setText( Integer.toString( media.getEnd() ) );
            et = (EditText)findViewById( R.id.startTime );
            et.setText( Integer.toString( media.getBegin() ) ); 
            
            findViewById( R.id.hwInfo ).setVisibility( View.VISIBLE );
            findViewById( R.id.rotation).setVisibility( View.VISIBLE );
        } 
        else if (media.getType() == SmilConstants.COMPONENT_TYPE_VIDEO) {
            if(extras != null)
            {
                EditText et = (EditText)findViewById( R.id.height);
                et.setText( Integer.toString( media.getRegion().getRect().height() ) );
                et = (EditText)findViewById( R.id.width );
                et.setText( Integer.toString( media.getRegion().getRect().width() ) );
                et = (EditText)findViewById( R.id.x );
                et.setText( Integer.toString( media.getRegion().getRect().left ) );
                et = (EditText)findViewById( R.id.y );
                et.setText( Integer.toString( media.getRegion().getRect().top ) );
            }
            else
            {
                EditText et = (EditText)findViewById( R.id.height);
                et.setText( "150" );
                et = (EditText)findViewById( R.id.width );
                et.setText( "150" );
                et = (EditText)findViewById( R.id.x );
                et.setText( "0" );
                et = (EditText)findViewById( R.id.y );
                et.setText( "0" );
                ContentResolver cr = getContentResolver();
                int duration = 0;

                String[] proj={MediaStore.Video.VideoColumns.DURATION, BaseColumns._ID};
                Cursor c = MediaStore.Video.query(cr, media.getMediaUri(), proj);
                if (c.moveToFirst()) {
                    int durationIndex = c.getColumnIndex( MediaStore.Video.VideoColumns.DURATION );
                    int idIndex = c.getColumnIndex( BaseColumns._ID );
                    
                    duration = c.getInt( durationIndex );
                    int id = c.getInt( idIndex );
                    Bitmap b = MediaStore.Video.Thumbnails.getThumbnail(cr, 
                    id, MediaStore.Video.Thumbnails.MINI_KIND, null);
                    media.setImage( b );
                }
                c.close();
                
                double durationDouble = duration/1000.0;
                duration = ( int ) Math.ceil( durationDouble );
                media.setEnd( duration );
                media.setBegin( 0 );
                
                et = (EditText)findViewById( R.id.startTime );
                et.setText( "0" ); 
            }
            
            EditText et = (EditText)findViewById( R.id.duration);
            et.setText( Integer.toString( media.getEnd() ) );
            et = (EditText)findViewById( R.id.startTime );
            et.setText( Integer.toString( media.getBegin() ) ); 
            findViewById( R.id.hwInfo ).setVisibility( View.VISIBLE );
           
        }
        
        if(media.getType() == SmilConstants.COMPONENT_TYPE_AUDIO)
            findViewById( R.id.optional ).setVisibility( View.GONE );
        else
        {
            findViewById( R.id.x ).setEnabled( false );
            findViewById( R.id.y ).setEnabled( false );
            findViewById( R.id.xLabel ).setEnabled( false );
            findViewById( R.id.yLabel ).setEnabled( false );
            findViewById( R.id.xyInfo ).setEnabled( false );
            findViewById( R.id.optional ).setEnabled( false );
        }
        
        TextView tv = (TextView)findViewById( R.id.Title );
        tv.setText(media.getFileName());
    }
    
    //radio button listener
    //this code will also set the orientation of the text
    private OnClickListener radio_listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //Reference for vertical text
            //http://stackoverflow.com/questions/1258275/vertical-rotated-label-in-android
            //http://code.google.com/p/chartdroid/
            
            //RadioButton rb = (RadioButton) v;
            //if( rb.getText().equals( R.string.horizontal_label ) )
            //{
            //    media.setOrientation( Media.HORIZONTAL );
            //}
            //else 
            //{
            //    media.setOrientation( Media.VERTICAL );
            //}
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
                if( media.getType() == SmilConstants.COMPONENT_TYPE_AUDIO )
                {
//                    media.setRepeat( ((CheckBox)findViewById( R.id.repeatCheckBox )).isChecked() );
                }
                else if ( media.getType() == SmilConstants.COMPONENT_TYPE_TEXT )
                {
                    media.setText( ( (EditText)findViewById( R.id.inputString ) ).getText().toString() );
//                    Log.i( "TEXT2", ComposerActivity.getMedia().getLast().getText() );
                }
                else if( media.getType() == SmilConstants.COMPONENT_TYPE_IMAGE)
                {
                    String height = ((EditText)findViewById( R.id.height )).getText().toString();
                    String width = ((EditText)findViewById( R.id.width )).getText().toString();
                    String top = ((EditText)findViewById( R.id.y )).getText().toString();
                    String left = ((EditText)findViewById( R.id.x )).getText().toString();

                    
                    //set default width and height here
                    if( height.equals( "" ))
                        height = "150";
                    if( width.equals( "" ))
                        width = "150";

                    // construct SmilRegion object
                    SmilRegion region = new SmilRegion(media.getTag(), "#000000", Integer.parseInt ( left ), 
                            Integer.parseInt ( top ), Integer.parseInt ( width  ), Integer.parseInt ( height ) );
                    media.setRegion( region );

                    media.setImage( image );
                }
                else if( media.getType() == SmilConstants.COMPONENT_TYPE_VIDEO)
                {
                    media.setRepeat( ((CheckBox)findViewById( R.id.repeatCheckBox )).isChecked() );
                    
                    String height = ((EditText)findViewById( R.id.height )).getText().toString();
                    String width = ((EditText)findViewById( R.id.width )).getText().toString();
                    String top = ((EditText)findViewById( R.id.y )).getText().toString();
                    String left = ((EditText)findViewById( R.id.x )).getText().toString();
                    
                    //set default width and height here
                    if( height.equals( "" ))
                        height = "150";
                    if( width.equals( "" ))
                        width = "150";
                    
                    // construct SmilRegion object
                    SmilRegion region = new SmilRegion(media.getTag(), "#000000", Integer.parseInt ( left ), 
                            Integer.parseInt ( top ), Integer.parseInt ( width  ), Integer.parseInt ( height ) );
                    media.setRegion( region ); 
                }
                
                String startTime = ((EditText)findViewById( R.id.startTime )).getText().toString();
                String dur = ((EditText)findViewById( R.id.duration )).getText().toString();
                if( startTime.equals( "" ))
                    startTime= "0";
                if( dur.equals( "" ))
                    dur = "1";
                media.setBegin( Integer.parseInt(startTime) );
                media.setEnd( Integer.parseInt(dur) );
                
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
            else if ( v.getId() == R.id.leftBtn )
            {
                for(int i=index-1; i>=0; i--) {
                    if(ComposerActivity.getMedia().get( i ).getType() == SmilConstants.COMPONENT_TYPE_AUDIO) {
                        index = i;
                        break;
                    }
                }
                media = ComposerActivity.getMedia().get( index );
                
                findViewById( R.id.rightBtn ).setVisibility( View.VISIBLE );

                TextView tv = (TextView)findViewById( R.id.Title );
                tv.setText(media.getFileName());
                EditText et = (EditText)findViewById( R.id.startTime );
                et.setText( Integer.toString( media.getBegin() ) );
                et = (EditText)findViewById( R.id.duration);
                et.setText( Integer.toString( media.getEnd() ) );
                
                int count =0;
                for(int i=index-1; i>=0; i--) {
                    if(ComposerActivity.getMedia().get( i ).getType() == SmilConstants.COMPONENT_TYPE_AUDIO) {
                        count++;
                    }
                }
                if(count == 0)
                    findViewById( R.id.leftBtn ).setVisibility( View.INVISIBLE );
            }
            else if ( v.getId() == R.id.rightBtn )
            {
                for(int i=index+1; i<ComposerActivity.getMedia().size(); i++) {
                    if(ComposerActivity.getMedia().get( i ).getType() == SmilConstants.COMPONENT_TYPE_AUDIO) {
                        index = i;
                        break;
                    }
                }
                media = ComposerActivity.getMedia().get( index );
                
                findViewById( R.id.leftBtn ).setVisibility( View.VISIBLE );
                
                TextView tv = (TextView)findViewById( R.id.Title );
                tv.setText(media.getFileName());
                EditText et = (EditText)findViewById( R.id.startTime );
                et.setText( Integer.toString( media.getBegin() ) );
                et = (EditText)findViewById( R.id.duration);
                et.setText( Integer.toString( media.getEnd() ) );
                
                int count =0;
                for(int i=index+1; i<ComposerActivity.getMedia().size(); i++) {
                    if(ComposerActivity.getMedia().get( i ).getType() == SmilConstants.COMPONENT_TYPE_AUDIO) {
                        count++;
                    }
                }
                if(count == 0)
                    findViewById( R.id.rightBtn ).setVisibility( View.INVISIBLE );
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