package com.team1.player;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.*;
import android.graphics.Color;
import android.widget.*;
import android.content.Intent;
import android.view.SurfaceHolder.Callback;

import com.team1.Smil.*; 
import com.team1.communication.Receiver;
import com.team1.communication.cloud.Downloader;
import com.team1.R;

public class SmilPlayerActivity extends Activity implements Callback
{
	FrameLayout             frameLayout;
	private SmilView        view;
	private MediaController mediaController;
	private SmilMessage     message;
    private Timer           myTimer;
	private Bundle          instance;
	private boolean         userStopped = false;

	   private class DisplaySurfaceRunnable implements Runnable
	   {
	        private SurfaceView surface;
	        private FrameLayout.LayoutParams newParams;
	        
	        public DisplaySurfaceRunnable ( SurfaceView sv, 
	                                        FrameLayout.LayoutParams lp )
	        {
	            this.surface = sv;
	            this.newParams = lp;
	        }
	        
	        @Override public void run ( )
	        {
	            if ( surface != null )
	            {
	                if ( newParams == null )
	                {
	                    surface.setVisibility ( View.INVISIBLE );
	                    surface.invalidate ( );
	                }
	                else
	                {
	                    FrameLayout.LayoutParams videoParams = new FrameLayout.LayoutParams ( surface.getWidth ( ), 
	                                                                                          surface.getHeight ( ) );
	                    videoParams.gravity = Gravity.TOP;
	                    videoParams.topMargin = newParams.topMargin;
	                    videoParams.leftMargin = newParams.leftMargin;
	                    surface.setLayoutParams ( videoParams );
	                    surface.invalidate ( );
	                }
	            }
	        }
	    }
	    
	    public synchronized void displaySurface ( SurfaceView v, 
	                                              FrameLayout.LayoutParams p )
	    {
	        DisplaySurfaceRunnable hvr = new DisplaySurfaceRunnable ( v, p );
	        runOnUiThread ( hvr );
	    }

	    
    @Override
    public void onCreate ( Bundle savedInstanceState )
    {
        instance = savedInstanceState;
        
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                timerMethod();
            }
        }, 0, 500);

        startPlayer ( );
    }

    private void loadVideos ( )
    {
        //load surface views for all the videos in the smil
        synchronized ( view )
        {
            SurfaceView surfaceView;
            SmilVideoComponent video = null;
            int startIndex = 0;
            int nextIndex = message.nextIndexOfType ( SmilConstants.COMPONENT_TYPE_VIDEO, 0 );
            
            while ( nextIndex != -1 )
            {
                System.gc ( );    //signal to garbage collect to help grant mediaplayer objects to video
                
                video = ( SmilVideoComponent ) message.getResourcesByBeginTime().get ( nextIndex );
                surfaceView = new SurfaceView ( frameLayout.getContext ( ) );
                surfaceView.setDrawingCacheBackgroundColor ( Color.TRANSPARENT );
                surfaceView = video.prepareVideo ( surfaceView );
                frameLayout.addView ( surfaceView );
                ++startIndex;
                nextIndex = message.nextIndexOfType ( SmilConstants.COMPONENT_TYPE_VIDEO, nextIndex + 1 );
            }
        }
    }

    @Override protected void onPause ( ) 
    {
        super.onPause ( );
        finish ( );
    }
    
    @Override public void finish ( ) 
    {
        view.stopPlayer ( );
        mediaController.hide ( );   
        myTimer.cancel ( );
        System.gc ( );        
        super.finish ( );
    }
    
    private void startPlayer ( )
    {
        super.onCreate ( instance );
        
        setContentView ( R.layout.player );

        frameLayout = (FrameLayout) findViewById ( R.id.frame );
        frameLayout.setWillNotDraw ( true );
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams ( FrameLayout.LayoutParams.WRAP_CONTENT,
                                                                               FrameLayout.LayoutParams.WRAP_CONTENT );
        
                
        view = new SmilView ( frameLayout.getContext ( ), null );
        view.setZOrderMediaOverlay ( true );
        view.getHolder().addCallback ( this );
        view.setLayoutParams ( layoutParams );
        view.setCallingActivity ( this );
        frameLayout.addView ( view );
        
        mediaController = new MediaController ( this );
        mediaController.setMediaPlayer ( playerInterface );
        mediaController.setEnabled ( true );
        mediaController.setAnchorView ( frameLayout );
        mediaController.setLayoutParams ( layoutParams );
        
        userStopped = false;
        
        ((Button)findViewById(R.id.playBtn)).setOnClickListener(new View.OnClickListener ( ) 
        {
            @SuppressWarnings ( "static-access" )
            @Override
            public void onClick ( View v ) 
            {
                if ( message != null )
                {
                    if ( view.getPlayState ( ) == view.PAUSED )
                    {
                        view.resumePlayer ( );
                        Button playPause = (Button)findViewById( R.id.playBtn );
                        playPause.setBackgroundDrawable(getResources().getDrawable( R.drawable.pausebuttonplayer));
                    }
                    else if ( view.getPlayState ( ) == view.PLAYING )
                    {
                        view.pausePlayer ( );
                        Button playPause = (Button)findViewById( R.id.playBtn );
                        playPause.setBackgroundDrawable(getResources().getDrawable( R.drawable.playbuttonplayer));
                    }
                    else
                    {
                        
                        startPlayer ( );
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Message is NULL", Toast.LENGTH_SHORT).show();
                }    
            }
        });

        ((Button)findViewById(R.id.stopBtn)).setOnClickListener(new View.OnClickListener ( ) 
        {
            @Override
            public void onClick ( View v ) 
            {
                ((ProgressBar)findViewById(R.id.progressBar)).setProgress(0);
                stopMessage ( );
            }
        });


        try
        {
            Intent in = getIntent ( );
            if ( in.hasExtra ( "playFile" ) )
            {
                String fileName = in.getExtras().getString ( "playFile" );
                message = SmilReader.parseMessage ( fileName );
                downloadMedia( message );
                loadVideos ( );
            }            
            else if ( in.hasExtra( "RecievedSmil" ))
            {
                String fileName = in.getExtras().getString ( "RecievedSmil" );
                Toast.makeText( getApplicationContext(), "Received: " + fileName, Toast.LENGTH_LONG );
                Log.i("RECEIVE", "file name: " + fileName);
                
                File pathDir = Environment.getExternalStorageDirectory ( );
                File dir = new File ( pathDir, SmilConstants.INBOX_PATH );
                File[] fileList = dir.listFiles();
                
                File recentFile = null;
                if(fileList != null && fileList.length > 0)
                {
                    long biggestIndex = 0;
                    for( File f : fileList)
                    {
                        String name =  f.getName();
                        Log.i("Files", name);
                        long index = Long.parseLong( name.substring( name.indexOf( "_" ) + 1, name.indexOf( "." ) ) );
                        if ( index > biggestIndex)
                        {
                            biggestIndex = index;
                            recentFile = f;
                        }
                    }
                }
                message = SmilReader.parseMessage ( recentFile.getAbsolutePath() );
                downloadMedia( message );
                loadVideos ( );
            }
        }
        catch ( Exception e )
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Exception", "error occurred while creating xml file", e);
        }
    }
   	
    private void downloadMedia ( SmilMessage message )
    {
        ArrayList < SmilComponent > components = message.getResourcesByBeginTime();
        for ( int i = 0; i < components.size(); i++ )
        {
            SmilComponent comp = components.get(i);
           
            File f = new File(comp.getSource());
            
            if( !f.exists() && comp.getTitle() != null && comp.getTitle().length() > 0)
            {
                Log.i("DOWNLOAD", "Attempting to download key " + comp.getTitle());
                boolean downloaded = Downloader.downloadKey( comp.getTitle(), comp.getSource() );
                if ( !downloaded )
                {
                    Log.e( "DOWNLOAD", "File failed to download." );
                }
            }
        }
    }
    
    private void stopMessage ( )
   	{
        view.stopPlayer ( );  
        Button playPause = (Button)findViewById( R.id.playBtn );
        playPause.setBackgroundDrawable(getResources().getDrawable( R.drawable.playbuttonplayer));
        userStopped = true;
   	}

    MediaController.MediaPlayerControl playerInterface = new MediaController.MediaPlayerControl ( ) 
    {
    		
    	@Override public void start ( ) 
    	{
    		if ( view.getPlayState ( ) == SmilView.STOPPED )
    		{
    			view.playPlayer ( message );
    		}
    		else if ( view.getPlayState ( ) == SmilView.PAUSED )
    		{
    			view.resumePlayer ( );
    		}
    	}

    	@Override public void seekTo ( int pos ) 
    	{
    	    view.setRunTime ( pos );
    	}
        
    	@Override public void pause ( ) 
    	{
    		view.pausePlayer ( );
    	}
    	
    	@Override public boolean isPlaying ( ) 
    	{
    		if ( view.getPlayState ( ) == SmilView.PLAYING )
    		{
    			return true;
    		}
    		return false;
    	}
    		
    	@Override public int getDuration ( ) 
    	{
    		return message.getLength ( ) * 1000;
    	}
    		
    	@Override public int getCurrentPosition ( ) 
    	{
    		return (int)(view.getRuntime ( ) * 1000);
    	}
    		
    	@Override public int getBufferPercentage ( ) 
    	{
    		return 100;
    	}
    		
    	@Override public boolean canSeekForward ( )  
    	{
    		return false; //mFromBrowser;
    	}
    		
    	@Override public boolean canSeekBackward ( ) 
    	{
    		return false;
    	}
    		
    	@Override public boolean canPause ( ) 
    	{
    		return true;
    	}
    };

    @Override public void surfaceChanged ( SurfaceHolder holder, 
                                           int format, 
                                           int width, 
                                           int height ) 
    {
        
    }

    @Override public void surfaceCreated ( SurfaceHolder holder ) 
    {
    	if ( message != null )
    	{
    		view.playPlayer ( message );
    	}
    }

    @Override public void surfaceDestroyed ( SurfaceHolder holder ) 
    {
        myTimer.cancel ( );
    }
    
    
    

    private void timerMethod ( )
    {
        this.runOnUiThread ( Timer_Tick );
    }

    private Runnable Timer_Tick = new Runnable ( ) 
    {
        boolean messagePlayed = false;
        public void run ( ) 
        {
            NumberFormat format;
            
            String timeDisplay = "";
            int time = view.getRuntime ( );
            
            int duration = 0;
    
            format = NumberFormat.getNumberInstance();
            format.setMinimumIntegerDigits(2); // pad with 0 if necessary
            if( time == duration )
                messagePlayed = true;
            
            if( (time >= 0 && view.getPlayState ( ) != view.STOPPED))
            {
                duration = view.getRunLength ( );
                timeDisplay = "  " +  
                format.format ( time / 60 ) + ":" + format.format ( time % 60  ) + " / " + 
                format.format ( duration / 60 ) + ":" + format.format ( duration % 60  );
            }
//            if ( ( time < 0 ) ||
//                 ( view.getPlayState ( ) == view.STOPPED && time != duration) )
            else 
            {
                timeDisplay = "  Press play to start.";
                
                if(time > 0)
                {
                    Button playPause = (Button)findViewById( R.id.playBtn );
                    playPause.setBackgroundDrawable(getResources().getDrawable( R.drawable.playbuttonplayer));            
                }
                
            } 
            
           
            int progress = 0;
            if(duration!=0)
            {
                progress = (int) (((float) time / (float) duration) * 100);
            }
            else if (messagePlayed && !userStopped)
            {
                progress = 100;
                timeDisplay = "  " +  
                format.format ( time / 60 ) + ":" + format.format ( time % 60  ) + " / " + 
                format.format ( time / 60 ) + ":" + format.format ( time % 60  );
                Button playPause = (Button)findViewById( R.id.playBtn );
                playPause.setBackgroundDrawable(getResources().getDrawable( R.drawable.pausebuttonplayer));
                messagePlayed = false;
            }
            
            ((ProgressBar)findViewById(R.id.progressBar)).setProgress(progress);
            ((TextView)findViewById( R.id.timerLbl )).setText( timeDisplay );
        }
            
    };
}
