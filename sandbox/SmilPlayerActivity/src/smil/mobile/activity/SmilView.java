package smil.mobile.activity;

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

public class SmilView extends SurfaceView implements SurfaceHolder.Callback{

	private class SmilThread extends Thread
	{
		private int mCurTime = -1;
		
		private SurfaceHolder mHolder;
        private ArrayList<SmilComponent> playList = new ArrayList<SmilComponent>();
		private int mBeginIndex, mEndIndex;
		private int mBeginMax, mEndMax;
		private SmilMessage message;
		
		private Paint timePaint;
		private Paint backgroundPaint;
		
		private SmilThread ( SurfaceHolder holder )
		{
			mHolder = holder;
			
			//Set the paint for the time slider to dark gray, and to paint text left aligned.
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
				mBeginIndex = 0;
				mEndIndex = 0;
				mBeginMax = message.getResourcesByBeginTime().size ( );
				mEndMax = message.getResourcesByEndTime().size ( );
				backgroundPaint.setColor ( Color.parseColor ( set.getBackgroundColorString ( ) ) );
                return true;
			}			
			
			return false;
		}
				
		@Override public void run() 
		{
			try{
				mCurTime = -2;
				state = PLAYING;
				Log.d("SmilView", "STATE: playing");
	            while (getPlayState() > STOPPED) 
	            {
	                Canvas c = null;
	                try {
	                    c = mHolder.lockCanvas(null);
	                    synchronized (mHolder) {
	                    	SmilComponentLoadThread t = new SmilComponentLoadThread ( c );
	                    	t.start ( );	         //load resources while sleeping
	                    	Thread.sleep ( 1000 );	 //pause for a second to keep track of time
	                    	draw ( c) ;			     //draw any resource on the canvas
	                    }
	                } catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
	                    // do this in a finally so that if an exception is thrown
	                    // during the above, we don't leave the Surface in an
	                    // inconsistent state
	                    if (c != null) {
	                        mHolder.unlockCanvasAndPost(c);
	                    }
					}
	            }
			}catch(Exception e){
				e.printStackTrace();
				Log.e(this.toString(), e.toString());
			}
		}
		
		private void forwardPlay(Canvas c) throws InterruptedException{
			try{
				if ( getPlayState() == PLAYING )
				{
	        		increment ( );
				}
				
	        	//add Resources to play
                while ( ( mBeginIndex < mBeginMax ) && 
                        ( message.getResourcesByBeginTime().get(mBeginIndex).getBegin ( ) <= getRunTime ( ) ) )
                {
	        		if ( message.getResourcesByBeginTime().get(mBeginIndex).getType() == SmilConstants.COMPONENT_TYPE_AUDIO )
	        		{
						SmilAudioThread t = new SmilAudioThread((SmilAudioComponent) message.getResourcesByBeginTime().get(mBeginIndex) );
						t.setPriority ( 3 );
						t.start ( );
					}
					else if ( message.getResourcesByBeginTime().get(mBeginIndex).getType() != SmilConstants.COMPONENT_TYPE_VIDEO )
					{
						playList.add ( message.getResourcesByBeginTime().get(mBeginIndex) );
					}
					else
					{
						SmilVideoThread t = new SmilVideoThread((SmilVideoComponent) message.getResourcesByBeginTime().get(mBeginIndex) );
						t.start ( );
					}
	        		
	        		++mBeginIndex;
	        	}
	        	
	        	//remove Resources
				while ( ( mEndIndex < mEndMax ) && 
				        ( message.getResourcesByEndTime().get(mEndIndex).getEnd ( ) <= getRunTime() ) ) 
				{
					if ( message.getResourcesByEndTime().get(mEndIndex).getType() == SmilConstants.COMPONENT_TYPE_VIDEO )
					{
						message.getResourcesByEndTime().get(mEndIndex).stop(null);
						if ( owner != null )
						{
							owner.displaySurface(((SmilVideoComponent)message.getResourcesByEndTime().get(mEndIndex)).getVideoView(), null);
						}
					}
					else
					{
						message.getResourcesByEndTime().get(mEndIndex).stop ( c );
						playList.remove(message.getResourcesByEndTime().get(mEndIndex));
					}
					
					++mEndIndex;
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
			++mCurTime;
		}
		
		public int getRunTime ( )
		{
			return mCurTime;
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
		private class SmilComponentLoadThread extends Thread{
			private Canvas c;
			
			public SmilComponentLoadThread(Canvas c){
				this.c = c;
			}
			
			@Override public void run(){
				try {
					forwardPlay(c);
				} catch (InterruptedException e) {
					e.printStackTrace();
					Log.e(this.toString(), e.toString());
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
			}
			
			@Override public void run ( )
			{
				try
				{
					int percivedState;
					
					while((percivedState = getPlayState()) > STOPPED && getRunTime() < video.getEnd ( ) )
					{
						try {
							if(percivedState == PLAYING){
								video.play(null);
								Thread.sleep(230);
								//video.pause();
							}else
								video.pause();
						} catch (InterruptedException e){
							Log.e("VideoThread", e.getMessage());
						}
					}
					Log.i("Video", video.getSource ( ) + " has stoped");
					video.stop(null);
				}catch(Exception e){
					e.printStackTrace();
					Log.e(video.getSource ( ), e.toString());
				}
			}
		}
	}
	
	final public static int PLAYING = 1;
	final public static int PAUSED  = 0;
	final public static int STOPPED = -1;
	final public static int PLAYED  = -2;
	
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
		if ( state == STOPPED && ready )
		{
			if ( time.setDataSet ( data ) ) 
			{
				time.start ( );
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
		if ( state >= PAUSED )
		{
			state = PLAYED;
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
		return time.getRunTime ( );
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
	        while (retry) {
	            try {
	            	time.join();
	                retry = false;
	            } catch (InterruptedException e) {}
	        }
	}

	@Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
}