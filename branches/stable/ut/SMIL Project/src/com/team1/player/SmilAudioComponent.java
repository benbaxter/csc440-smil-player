package com.team1.player;

import android.media.MediaPlayer;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.io.File;
import java.io.IOException;

public class SmilAudioComponent extends SmilComponent
{    
    private MediaPlayer mediaPlayer;
    
    public SmilAudioComponent ( ) 
    {
        super ( null, null, 0, 0 );
        super.setType ( SmilConstants.COMPONENT_TYPE_AUDIO );
    }

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
}
