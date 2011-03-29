package com.team1.player;

import java.io.File;

import android.R;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.widget.FrameLayout;


public class SmilVideoComponent extends SmilComponent implements OnPreparedListener, Callback, OnVideoSizeChangedListener
{    
    public int playState = SmilView.STOPPED;
    private MediaPlayer mediaPlayer;
    private SurfaceView preview;
    private SurfaceHolder holder;
    private FrameLayout.LayoutParams videoParams;
    private String path;
    private boolean videoReady = false;
    private int videoWidth;
    private int videoHeight;
    private boolean tryAtPlay = false;

    public SmilVideoComponent ( ) 
    {
        super ( null, null, 0, 0 );
        super.setType ( SmilConstants.COMPONENT_TYPE_VIDEO );
    }

    public SmilVideoComponent ( String source, SmilRegion region, int begin, int end ) 
    {
        super ( source, region, begin, end );
        super.setType ( SmilConstants.COMPONENT_TYPE_VIDEO );
        path = source;
    }
    
    public SurfaceView prepareVideo ( SurfaceView view )
    {
        preview = view;
        
        File tmpFile = new File ( getSource());
        if ( tmpFile.exists ( ) )
        {           
            videoWidth = getRegion().getRect().width ( );
            videoHeight = getRegion().getRect().height ( );
            
            FrameLayout.LayoutParams vidParms = new FrameLayout.LayoutParams(videoWidth, videoHeight);
            
            vidParms.gravity = Gravity.TOP;
            vidParms.topMargin = getRegion().getRect().top;
            
            if ( getBegin ( ) > 0 )
            {
                vidParms.leftMargin = 0 - videoWidth;
            }
            else
            {
                vidParms.leftMargin = getRegion().getRect().left;
            }
            
            preview.setBackgroundColor ( Color.TRANSPARENT );
            preview.setLayoutParams ( vidParms );
            holder = preview.getHolder ( );
            holder.addCallback ( this );
            holder.setType ( SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS );
            if ( !holder.isCreating ( ) )
            {
                playVideo ( );
            }
            
            videoParams = new FrameLayout.LayoutParams ( videoWidth, videoHeight );
            videoParams.gravity = Gravity.TOP;
            videoParams.topMargin = getRegion().getRect().top;
            videoParams.leftMargin = getRegion().getRect().left;
        }
        
        return preview;
    }
    
    public FrameLayout.LayoutParams getLayoutParams ( ) 
    {
        return videoParams;
    }
    
    public SurfaceView getVideoView ( )
    {
        return preview;
    } 
    
    public void pause ( )
    {
        if ( videoReady && playState == SmilView.PLAYING )
        {
            mediaPlayer.pause ( );
            playState = SmilView.PAUSED;
        }
    }

    @Override public void play ( Canvas canvas )
    {
        if ( !tryAtPlay && videoReady && playState != SmilView.PLAYING )
        {
            mediaPlayer.start ( );
            playState = SmilView.PLAYING;
        }
        else if ( tryAtPlay || !videoReady )
        {
            reset ( );
        }
    }

    @Override public String getText ( )
    {
        return null;
    }
    
    @Override public void setText ( String text )
    {
    }
    
    @Override public Bitmap getImage ( )
    {
        return null;
    }

    @Override public void setImage ( Bitmap image )
    {
    }

    @Override public int getFontSize ( )
    {
        return 0;
    }

    @Override public void setFontSize ( int size )
    {
    }

    private void reset ( )
    {
        tryAtPlay = false;
        videoReady = false;
        synchronized ( mediaPlayer )
        {
            mediaPlayer.reset ( );
            try 
            {
                mediaPlayer.setDataSource ( path );
                mediaPlayer.setDisplay ( holder );
                mediaPlayer.prepare ( );
                mediaPlayer.setOnPreparedListener ( this );
                mediaPlayer.setOnVideoSizeChangedListener ( this );
                preview.getParent().invalidateChild(preview, getRegion().getRect());
            } 
            catch ( Exception e ) 
            {
                e.printStackTrace ( );
            }
        }
    }

    @Override public void stop ( Canvas canvas )
    {
        if ( videoReady && playState > SmilView.STOPPED )
        {
            mediaPlayer.stop ( );
            releaseMediaPlayer ( );
            playState = SmilView.STOPPED;
        }
    }
    
    @Override protected void finalize ( ) throws Throwable 
    {
        releaseMediaPlayer ( );
        super.finalize ( );
    }

    private void playVideo ( ) 
    {
        try 
        {
            // Create a new media player and set the listeners
            mediaPlayer = new MediaPlayer ( );
            mediaPlayer.setDataSource ( path );
            mediaPlayer.setDisplay ( holder );
            mediaPlayer.prepare ( );
            mediaPlayer.setOnPreparedListener ( this );
            mediaPlayer.setOnVideoSizeChangedListener ( this );
        } 
        catch ( Exception e ) 
        {
            mediaPlayer = null;
            System.gc ( );
            try 
            {
                mediaPlayer = new MediaPlayer ( );
                mediaPlayer.setDataSource ( path );
                mediaPlayer.setDisplay ( holder );
                mediaPlayer.prepare ( );
                mediaPlayer.setOnPreparedListener ( this );
                mediaPlayer.setOnVideoSizeChangedListener ( this );
            } 
            catch ( Exception e1 ) 
            {
                e1.printStackTrace();
                preview.setBackgroundResource ( R.drawable.stat_notify_error );
                tryAtPlay = ( !tryAtPlay ) ? true : false;
            }
        }
    }
    
    public void onPrepared ( MediaPlayer mp ) 
    {
        videoReady = true;
    }

    public void surfaceChanged ( SurfaceHolder surfaceholder, int i, int j, int k ) 
    {
    }

    public void surfaceDestroyed ( SurfaceHolder surfaceholder ) 
    {
        releaseMediaPlayer ( );
    }

    public void surfaceCreated ( SurfaceHolder holder ) 
    {
        playVideo ( );
    }
    
    private void releaseMediaPlayer ( ) 
    {
        if ( mediaPlayer != null ) 
        {
            videoReady = false;
            mediaPlayer.release ( );
            mediaPlayer = null;
        }
    }

    public void onVideoSizeChanged ( MediaPlayer mp, int width, int height ) 
    {
        videoWidth = width;
        videoHeight = height;
        
        holder.setFixedSize ( videoWidth, videoHeight );
    }
}
