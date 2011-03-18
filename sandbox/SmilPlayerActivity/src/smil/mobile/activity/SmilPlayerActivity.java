package smil.mobile.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.graphics.Color;
import android.widget.Button;
import android.widget.Toast;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.widget.FrameLayout;
import android.widget.MediaController;


public class SmilPlayerActivity extends Activity implements Callback
{
	FrameLayout frameLayout;
	private SmilView view;
	private MediaController mediaController;
	private SmilMessage message;

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
        super.onCreate ( savedInstanceState );
        
        setContentView ( R.layout.main );

		frameLayout = (FrameLayout) findViewById ( R.id.frame );
		frameLayout.setWillNotDraw ( true );
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams ( FrameLayout.LayoutParams.WRAP_CONTENT, 
		                                                                       FrameLayout.LayoutParams.WRAP_CONTENT );
		//setContentView ( frameLayout, layoutParams );
		
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
        	            Toast.makeText(getApplicationContext(), "Resuming", Toast.LENGTH_SHORT).show();

        	        }
        	        else
        	        {
        	            view.playPlayer ( message );
                        Toast.makeText(getApplicationContext(), "Playing", Toast.LENGTH_SHORT).show();
        	        }
        	    }
        	    else
        	    {
                    Toast.makeText(getApplicationContext(), "Message is NULL", Toast.LENGTH_SHORT).show();
        	    }    
           	}
        });

        ((Button)findViewById(R.id.pauseBtn)).setOnClickListener(new View.OnClickListener ( ) 
        {
        	@Override
        	public void onClick ( View v ) 
        	{
        		if ( message != null )
                {
                    view.pausePlayer ( );
                    Toast.makeText(getApplicationContext(), "Pausing", Toast.LENGTH_SHORT).show();
                }             
        	}
        });

        ((Button)findViewById(R.id.replayBtn)).setOnClickListener(new View.OnClickListener ( ) 
        {
        	@Override
        	public void onClick ( View v ) 
        	{
        		restartPlayer ( );
        		Toast.makeText(getApplicationContext(), "Replay", Toast.LENGTH_SHORT).show();
            }
        });

        ((Button)findViewById(R.id.respondBtn)).setOnClickListener(new View.OnClickListener ( ) 
        {
        	@Override
        	public void onClick ( View v ) 
        	{
        		//respond to the smil message
        		Toast.makeText(getApplicationContext(), "Respond", Toast.LENGTH_SHORT).show();
        	}
        });

        try
        {
        	String fileName = "example.smil"; //in.getStringExtra(getString(R.string.fileName));
        	
        	message = SmilReader.parseMessage ( fileName );
        	loadVideos ( );
        	//view.playPlayer ( message );
        	
        }
        catch ( Exception e )
        {
    		Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        	Log.e("Exception", "error occurred while creating xml file", e);
        }
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
    
   	private void restartPlayer ( )
   	{
   		startActivity ( getIntent ( ) );
   	}

    MediaController.MediaPlayerControl playerInterface = new MediaController.MediaPlayerControl ( ) 
    {
    		
    	@Override public void start ( ) 
    	{
    		if ( view.getPlayState ( ) == SmilView.STOPPED )
    		{
    			view.playPlayer ( message );
    		}
    		else if ( view.getPlayState ( ) == SmilView.PLAYED )
    		{
    			restartPlayer ( );
    		}
    		else if ( view.getPlayState ( ) == SmilView.PAUSED )
    		{
    			view.resumePlayer ( );
    		}
    	}
    		
    	@Override public void seekTo ( int pos ) 
    	{

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
    		return false; //mFromBrowser;
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
    	//mSurfaceReady = true;
    	if ( message != null )
    	{
    		view.playPlayer ( message );
    	}
    }

    @Override public void surfaceDestroyed ( SurfaceHolder holder ) 
    {
        
    }
}
