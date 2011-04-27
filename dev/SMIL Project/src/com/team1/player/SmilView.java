//This is for you Hao

package com.team1.player;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.team1.Smil.SmilAudioComponent;
import com.team1.Smil.SmilComponent;
import com.team1.Smil.SmilConstants;
import com.team1.Smil.SmilMessage;
import com.team1.Smil.SmilVideoComponent;

public class SmilView extends SurfaceView implements SurfaceHolder.Callback
{
	private class SmilThread extends Thread
	{
		private int currentTime = -1;
		
		private SurfaceHolder resHolder;
        private ArrayList<SmilComponent> playList = new ArrayList<SmilComponent>();
		private int beginIndex;
		private int endIndex;
		private int beginMax;
		private int endMax;
		private SmilMessage message;
		
		private Paint timePaint;
		private Paint backgroundPaint;
		
		private SmilThread ( SurfaceHolder holder )
		{
			resHolder = holder;
			timePaint = new Paint();
			timePaint.setColor(Color.DKGRAY);
			timePaint.setTextAlign(Align.LEFT);
			backgroundPaint = new Paint();
			backgroundPaint.setStyle(Style.FILL);
		}
		
		public boolean setDataSet ( SmilMessage set )
		{
			if ( set != null )
			{
				message = set;
				beginIndex = 0;
				endIndex = 0;
				beginMax = message.getResourcesByBeginTime().size ( );
				endMax = message.getResourcesByEndTime().size ( );
				backgroundPaint.setColor ( Color.parseColor ( set.getBackgroundColorString ( ) ) );
                return true;
			}			
			
			return false;
		}
				
		@Override public void run() 
		{
			try{
				currentTime = -2;
				state = PLAYING;
				Log.d("SmilView", "STATE: playing");
	            while ( getPlayState ( ) != STOPPED ) 
	            {
	                Canvas c = null;
	                c = resHolder.lockCanvas ( null );
	                synchronized (resHolder) 
	                {
	                  	SmilComponentLoadThread t = new SmilComponentLoadThread ( c );
	                   	t.start ( );	        
	                   	draw ( c );			    
                    }
	                
	                if ( c != null ) 
	                {
	                    resHolder.unlockCanvasAndPost ( c );
	                }
					
	                try
	                {
	                    Thread.sleep ( 1000 );
	                }
	                catch ( InterruptedException e )
	                {
	                    e.printStackTrace ( );
	                }
	                
	            }
			}
			catch ( Exception e )
			{
				e.printStackTrace ( );
				Log.e ( this.toString ( ), e.toString ( ) );
			}
		}
		
		private void forwardPlay(Canvas c) throws InterruptedException
		{
			try
			{
				if ( getPlayState() == PLAYING )
				{
	        		increment ( );
				}
				
	        	while ( ( beginIndex < beginMax ) && 
                        ( message.getResourcesByBeginTime().get(beginIndex).getBegin ( ) <= getRunTime ( ) ) )
                {
	        		if ( message.getResourcesByBeginTime().get(beginIndex).getType() == SmilConstants.COMPONENT_TYPE_AUDIO )
	        		{
						SmilAudioThread t = new SmilAudioThread((SmilAudioComponent) message.getResourcesByBeginTime().get(beginIndex) );
						t.setPriority ( 3 );
						t.start ( );
					}
					else if ( message.getResourcesByBeginTime().get(beginIndex).getType() != SmilConstants.COMPONENT_TYPE_VIDEO )
					{
						playList.add ( message.getResourcesByBeginTime().get(beginIndex) );
					}
					else
					{
						SmilVideoThread t = new SmilVideoThread((SmilVideoComponent) message.getResourcesByBeginTime().get(beginIndex) );
						t.start ( );
					}
	        		
	        		++beginIndex;
	        	}
	        	
	        	while ( ( endIndex < endMax ) && 
				        ( message.getResourcesByEndTime().get(endIndex).getEnd ( ) <= getRunTime() ) ) 
				{
	        	    if ( message.getResourcesByEndTime().get(endIndex).getType() == SmilConstants.COMPONENT_TYPE_VIDEO )
					{
						message.getResourcesByEndTime().get(endIndex).stop(null);
						if ( owner != null )
						{
							owner.displaySurface(((SmilVideoComponent)message.getResourcesByEndTime().get(endIndex)).getVideoView(), null);
						}
					}
					else
					{
						message.getResourcesByEndTime().get(endIndex).stop ( c );
						playList.remove(message.getResourcesByEndTime().get(endIndex));
					}
					
					++endIndex;
				}
				
				if ( getRunTime ( ) >= message.getLength ( ) )
				{
					stopPlayer ( );
				}
			}
			catch ( Exception e ) 
			{
				e.printStackTrace();
				Log.e("Forward Play", e.toString());
			}
		}
		
		public void draw ( Canvas canvas ) 
		{
			canvas.drawPaint ( backgroundPaint );
			
			Iterator<SmilComponent> iter = playList.iterator();
			while ( iter.hasNext ( ) )
			{
				iter.next().play ( canvas );
			}
		}
		
		private synchronized void increment ( )
		{
			++currentTime;
		}
		
		public int getRunTime ( )
		{
			return currentTime;
		}
		
		public void setRunTime ( int time )
		{
		    currentTime = time;
		    if ( time == 0 )
		    {
		        Canvas c = null;
                c = resHolder.lockCanvas ( null );
                c.drawPaint ( backgroundPaint );
                resHolder.unlockCanvasAndPost ( c );
                
                beginIndex = 0;
                endIndex = 0;
            }
		}

		public int getRunLength ( )
	    {
		    if ( message != null )
		    {
		        return message.getLength ( );
		    }
		    return 0;
	    }

		private class SmilAudioThread extends Thread
		{
			private SmilAudioComponent audio;
			private int percievedState;
			
			public SmilAudioThread ( SmilAudioComponent sar )
			{
				audio = sar;
			}
			
			@Override public void run ( ) 
			{
				try
				{
                    while ( ( (percievedState = getPlayState()) > STOPPED ) && 
                            ( getRunTime() < audio.getEnd ( ) ) )
                    {
						if ( percievedState == PLAYING )
						{
							audio.play ( null );
						}
						else
						{
							audio.pause ( );
						}
					}
					
					audio.stop ( null );
					
				}
				catch ( Exception e )
				{
					e.printStackTrace();
					Log.e(audio.toString(), e.toString());
				}
			}
		}
		
		/**
		 * Private thread used to load resources during second pauses.
		 */
		private class SmilComponentLoadThread extends Thread
		{
			private Canvas c;
			
			public SmilComponentLoadThread ( Canvas c )
			{
				this.c = c;
			}
			
			@Override public void run ( )
			{
				try 
				{
					forwardPlay ( c );
				} 
				catch ( InterruptedException e ) 
				{
					e.printStackTrace ( );
					Log.e ( this.toString ( ), e.toString ( ) );
				}
			}
		}
		
		private class SmilVideoThread extends Thread
		{
			private SmilVideoComponent video;
			
			public SmilVideoThread ( SmilVideoComponent video )
			{
				this.video = video;
				if ( owner != null )
				{
					owner.displaySurface ( video.getVideoView(), video.getLayoutParams ( ) );
					Log.i("VideoThread", "created " + video.getSource ( ) );
				}
				else
				{
				    Log.i ( "VideoThread", "Owner is NULL" );
				}
			}
			
			@Override public void run ( )
			{
				try
				{
					int percivedState;
					
					while((percivedState = getPlayState()) > STOPPED && getRunTime() < video.getEnd ( ) )
					{
						try 
						{
							if ( percivedState == PLAYING )
							{							    
								video.play ( null );
								Thread.sleep ( 230 );
							}
							else
							{
								video.pause ( );
							}
						} 
						catch ( InterruptedException e )
						{
							Log.e("VideoThread", e.getMessage());
						}
					}
					Log.i("Video", video.getSource ( ) + " has stopped");
					video.stop(null);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					Log.e(video.getSource ( ), e.toString());
				}
			}
		}
	}
	
	final public static int PLAYING = 1;
	final public static int PAUSED  = 0;
	final public static int STOPPED = -1;
	
	private int state = STOPPED;
	private SmilThread time;
	private SmilMessage playSet;
	private boolean visible = true;
	private boolean ready = false;
	private boolean playWait = false;
	private SmilPlayerActivity owner;
		
 	public SmilView ( Context context, AttributeSet attrs )
 	{
		super ( context, attrs );
		
		SurfaceHolder holder = getHolder ( );
		holder.addCallback ( this );
		
		time = new SmilThread ( holder);
		 
		this.setFocusable ( true );
	}

	public void setCallingActivity ( SmilPlayerActivity owner )
	{
		this.owner = owner;
	}
	
	public synchronized void playPlayer ( SmilMessage data )
	{
		if ( ready )
		{
		    if ( state == STOPPED )
		    {
		        if ( time == null )
		        {
		            SurfaceHolder holder = getHolder ( );
		            holder.addCallback ( this );
		            
		            time = new SmilThread ( holder );
		             
		            this.setFocusable ( true );
		        }
		        
		        if ( time.setDataSet ( data ) ) 
		        {
		            time.start ( );
		        }
		    }
		}
		else
		{
			synchronized ( playSet )
			{
				playWait = true;
				playSet = data;
			}
		}
	}
	
	public synchronized void resumePlayer ( )
	{
		if ( state == PAUSED )
		{
			state = PLAYING;
		}
	}
	
	public synchronized void pausePlayer ( )
	{
		if ( state == PLAYING )
		{
			state = PAUSED;
		}
	}
	
	public synchronized void stopPlayer ( )
	{
	    try
	    {
	        if ( state >= PAUSED )
	        {
	            state = STOPPED;
	        } 
	        
	        Thread.sleep ( 1250 );
	        time.stop ( );
	        time = null;
	        System.gc ( );
	    }
	    catch ( Exception e )
	    {	
	    }
	}
	
	public int getPlayState ( )
	{
		return state;
	} 
	
	public boolean getControlVisiblity ( )
	{
		return visible;
	}
	
	public void setControlVisible ( boolean v )
	{
		visible = v;
	}
	
	public int getRuntime ( )
	{
	    if ( time != null )
	    {
	        return time.getRunTime ( );
	    }
	    return -1;
	}

	public void setRunTime ( int pos )
	{
	    //time.setRunTime ( pos );
	}
	
	public int getRunLength ( )
    {
	    if ( time != null )
	    {
	        return time.getRunLength ( );
	    }
	    return -1;
    }
	
	@Override public void surfaceCreated ( SurfaceHolder arg0 ) 
	{
		this.setFocusable ( true );
		ready = true;
		if ( playWait )
		{
			playPlayer ( playSet );
		}
	}

	@Override public void surfaceDestroyed(SurfaceHolder arg0) {
		 boolean retry = true;
		 state = STOPPED;
	        while ( ( time != null ) &&
	                ( retry        ) ) 
	        {
	            try 
	            {
	            	time.join();
	                retry = false;
	            } 
	            catch (InterruptedException e) {}
	        }
	}

	@Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
}