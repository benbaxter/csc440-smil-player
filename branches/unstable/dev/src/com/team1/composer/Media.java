package com.team1.composer;

import android.graphics.Bitmap;

public class Media {
    
    public static final int AUDIO_TYPE = 0;
    public static final int IMAGE_TYPE = 1;
    public static final int TEXT_TYPE = 2;
    public static final int VIDEO_TYPE = 3; 
    public static final int HORIZONTAL = 4;
    public static final int VERTICAL = 5;
    
    private int mediaType;
    private int startTime;
    private int duration;
    private int x;
    private int y;
    private int height;
    private int width;
    private int fontSize;
    private int orientation;
    private String mediaTag;
    private String text;
    private String fileName;
    private String path;
    private boolean repeat;
    private Bitmap image;
    
    
    public Media( int mediaType, String mediaTag )
    {
        this.mediaType = mediaType;
        this.mediaTag = mediaTag;
    }
    
    public Media( int mediaType, int startTime, int duration)
    {
        this.mediaType = mediaType;
        this.startTime = startTime;
        this.duration = duration;
    }
    
    public String getMediaTag()
    {
        return mediaTag;
    }
    
    public int getMediaType()
    {
        return mediaType;
    }

    public void setMediaType( int mediaType )
    {
        this.mediaType = mediaType;
    }

    public int getStartTime()
    {
        return startTime;
    }

    public void setStartTime( int startTime )
    {
        this.startTime = startTime;
    }

    public int getDuration()
    {
        return duration;
    }

    public void setDuration( int duration )
    {
        this.duration = duration;
    }

    public int getX()
    {
        return x;
    }

    public void setX( int x )
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY( int y )
    {
        this.y = y;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight( int height )
    {
        this.height = height;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth( int width )
    {
        this.width = width;
    }

    public int getFontSize()
    {
        return fontSize;
    }

    public void setFontSize( int fontSize )
    {
        this.fontSize = fontSize;
    }

    public int getOrientation()
    {
        return orientation;
    }

    public void setOrientation( int orientation )
    {
        this.orientation = orientation;
    }

    public String getText()
    {
        return text;
    }

    public void setText( String text )
    {
        this.text = text;
    }

    public boolean isRepeat()
    {
        return repeat;
    }

    public void setRepeat( boolean repeat )
    {
        this.repeat = repeat;
    }

    public void setFileName( String fileName )
    {
        this.fileName = fileName;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setPath( String path )
    {
        this.path = path;
    }

    public String getPath()
    {
        return path;
    }

    public void setImage( Bitmap image )
    {
        this.image = image;
    }

    public Bitmap getImage()
    {
        return image;
    }
}
