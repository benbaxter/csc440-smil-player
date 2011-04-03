package com.team1.player;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.graphics.Color;
import android.widget.*;
import android.view.SurfaceHolder.Callback;
import android.content.Intent;

import com.team1.Smil.*;
import com.team1.R;

public class SmilPlayerActivity extends Activity implements Callback
{
	FrameLayout frameLayout;
	private SmilView view;
	private MediaController mediaController;
	private SmilMessage message;
	private Bundle instance;

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
                TimerMethod();
            }

        }, 0, 1000);
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
                        //view.playPlayer ( message );
                        Button playPause = (Button)findViewById( R.id.playBtn );
                        playPause.setBackgroundDrawable(getResources().getDrawable( R.drawable.playbuttonplayer));
                    }
                    else if ( view.getPlayState ( ) == view.PLAYED )
                    {
                        Button playPause = (Button)findViewById( R.id.playBtn );
                        playPause.setBackgroundDrawable(getResources().getDrawable( R.drawable.playbuttonplayer));
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Message is NULL", Toast.LENGTH_SHORT).show();
                }    
            }
        });

//        ((Button)findViewById(R.id.pauseBtn)).setOnClickListener(new View.OnClickListener ( ) 
//        {
//            @Override
//            public void onClick ( View v ) 
//            {
//                if ( message != null )
//                {
//                    //view.pausePlayer ( );
//                }             
//            }
//        });

        ((Button)findViewById(R.id.replayBtn)).setOnClickListener(new View.OnClickListener ( ) 
        {
            @Override
            public void onClick ( View v ) 
            {
                restartPlayer ( );
            }
        });

        ((Button)findViewById(R.id.respondBtn)).setOnClickListener(new View.OnClickListener ( ) 
        {
            @Override
            public void onClick ( View v ) 
            {
                //TBD: Respond to the smil message
                Toast.makeText(getApplicationContext(), "Respond", Toast.LENGTH_SHORT).show();
            }
        });
        
        try
        {
            Intent in = getIntent ( );
            if ( in.hasExtra ( "playFile" ) )
            {
                String fileName = in.getExtras().getString ( "playFile" );
                message = SmilReader.parseMessage ( fileName );
            
            
/*******************************************            
            SmilRegion r;
            SmilComponent c;
            
            message = new SmilMessage ( );
            message.setBackgroundColor ( "blue" );
            message.setCanvasHeight ( 450 );
            message.setCanvasWidth ( 350 );
            
            r = new SmilRegion ( "text_region1", "yellow", 100, 75, 150, 25 );
            c = new SmilTextComponent ( "text.txt", r, 0, 5 );
            message.addComponent ( c );
            
            r = new SmilRegion ( "image_region1", "yellow", 100, 100, 250, 250 );
            c = new SmilImageComponent ( "image.jpg", r, 5, 10 );
            message.addComponent ( c );
            
            message.saveAsXML ( SmilConstants.ROOT_PATH + "my_file.smil" );
*******************************************/            
            
                loadVideos ( );
                
                time = 0;
            }            
        }
        catch ( Exception e )
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Exception", "error occurred while creating xml file", e);
        }
    }

   	
    private void restartPlayer ( )
   	{
        startPlayer ( );
   	}

    MediaController.MediaPlayerControl playerInterface = new MediaController.MediaPlayerControl ( ) 
    {
    		
    	@Override public void start ( ) 
    	{
    		if ( view.getPlayState ( ) == SmilView.STOPPED )
    		{
    			view.playPlayer ( message );
    		}
    		//else if ( view.getPlayState ( ) == SmilView.PLAYED )
    		//{
    		//	restartPlayer ( );
    		//}
    		else if ( view.getPlayState ( ) == SmilView.PAUSED )
    		{
    			view.resumePlayer ( );
    		}
    	}

    	@Override public void seekTo ( int pos ) 
    	{
    	    view.setRuntime ( pos );
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
    		return view.getRuntime ( ) * 1000;
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
    }
    
    private Timer myTimer;
    private int time = 0;


    private void TimerMethod()
    {
        this.runOnUiThread(Timer_Tick);
    }

    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            String timeDisplay = "";
            if( time == -1 )
            {
                timeDisplay = "Click repeat to play again.";
            } 
            else if ( time < 10 )
            {
                timeDisplay = "00:0" + time;
            }
            else if( time < 60 )
            {
                timeDisplay = "00:" + time;
            }
            else if ( time < 70 )
            {
                timeDisplay = (time / 60) + ":0" + (time % 60);
            }
            else 
            {
                timeDisplay = (time / 60) + ":" + (time % 60);
            }
            
            ((TextView)findViewById( R.id.timerLbl )).setText( "     " +  timeDisplay + "          " );
            
            if ( view.getPlayState ( ) == view.PLAYING )
            {
                time++;
            }
            else if ( view.getPlayState ( ) == view.PLAYED )
            {
                time = -1;
            }
        }
    };
}
