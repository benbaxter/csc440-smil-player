package smil.mobile.activity;

import java.io.Serializable;
import android.media.MediaPlayer;
import android.graphics.Canvas;

import java.io.File;
import java.io.IOException;

public class SmilAudioComponent extends SmilComponent implements Serializable
{
    private static final long serialVersionUID = 1L;
    private MediaPlayer mediaPlayer;
    
    public SmilAudioComponent ( String source, SmilRegion region, int begin, int end ) 
    {
        super ( source, region, begin, end );
        super.setType ( SmilConstants.COMPONENT_TYPE_AUDIO );
        mediaPlayer = new MediaPlayer ( );
        try 
        {
            File tmpFile = new File ( source );
            if ( tmpFile.exists ( ) )
            {
                mediaPlayer.setDataSource ( source );
                mediaPlayer.prepare ( );
                //mediaPlayer.setOnPreparedListener(this);
            }
        } 
        catch ( IllegalArgumentException e ) 
        {
            e.printStackTrace ( );
        } 
        catch ( IllegalStateException e ) 
        {
            e.printStackTrace ( );
        } 
        catch ( IOException e ) 
        {
            e.printStackTrace ( );
        }
    }

    @Override
    public String toString ( )
    {
        return "Audio File - " + super.getSource ( );
    }
    
    @Override public void play ( Canvas canvas ) 
    {
        //if(mPlayerLoaded && !mPlayer.isPlaying()){
        if ( !mediaPlayer.isPlaying ( ) )
        {
            try 
            {
                mediaPlayer.start ( );
            } 
            catch ( IllegalStateException e ) 
            {
                e.printStackTrace();
            }
        }
    }

    @Override public void stop ( Canvas canvas ) 
    {
        if ( mediaPlayer != null )
        synchronized ( mediaPlayer )
        {
            if ( mediaPlayer != null )
            {
                if ( mediaPlayer.isPlaying ( ) )
                {
                    mediaPlayer.pause ( );
                    mediaPlayer.seekTo ( 0 );
                    releaseMediaPlayer ( );
                }
            }
        }
    }
    
    public void pause ( ) 
    {
        //if ( mediaPlayer.isPlaying ( ) && mPlayerLoaded){
        if ( mediaPlayer.isPlaying ( ) )
        {
            try 
            {
                mediaPlayer.pause ( );
            } 
            catch ( IllegalStateException e ) 
            {
                e.printStackTrace();
            }
        }
    }

    @Override protected void finalize ( ) throws Throwable 
    {
        releaseMediaPlayer ( );
        super.finalize ( );
    }
    
    private void releaseMediaPlayer ( ) 
    {
        if ( mediaPlayer != null ) 
        {
            mediaPlayer.release ( );
            mediaPlayer = null;
        }
    }

}
