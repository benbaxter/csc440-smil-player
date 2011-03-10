package com.team1.composer;

public class Media {
    
    public static final int AUDIO_TYPE = 0;
    public static final int IMAGE_TYPE = 1;
    public static final int TEXT_TYPE = 2;
    public static final int VIDEO_TYPE = 3; 
    public static final int HORIZONTAL = 4;
    public static final int VERTICAL = 5;
    
    private static int mediaType;
    private static int startTime;
    private static int duration;
    private static int x;
    private static int y;
    private static int height;
    private static int width;
    private static int fontSize;
    private static int orientation;
    private static String mediaTag;
    private static String text;
    private static boolean repeat;
    
    
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

    public static int getStartTime()
    {
        return startTime;
    }

    public void setStartTime( int startTime )
    {
        this.startTime = startTime;
    }

    public static int getDuration()
    {
        return duration;
    }

    public void setDuration( int duration )
    {
        this.duration = duration;
    }

    public static int getX()
    {
        return x;
    }

    public void setX( int x )
    {
        this.x = x;
    }

    public static int getY()
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

    public static int getOrientation()
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

    public static boolean isRepeat()
    {
        return repeat;
    }

    public void setRepeat( boolean repeat )
    {
        this.repeat = repeat;
    }


}
