package com.team1.maintocomposer;

public class Media {
    
    public static final int AUDIO_TYPE = 0;
    public static final int IMAGE_TYPE = 1;
    public static final int TEXT_TYPE = 2;
    public static final int VIDEO_TYPE = 3; 
    public static int mediaType;
    public static int startTime;
    

    public static int duration;
    public static int x;
    public static int y;
    public static int height;
    public static int width;
    
    public static int fontSize;
    public static final int HORIZONTAL = 4;
    public static final int VERTICAL = 5;
    public static int orientation;
    public static String text;
    
    public static boolean repeat;
    
    public Media( int mediaType )
    {
        this.mediaType = mediaType;
    }
    
    public Media( int mediaType, int startTime, int duration)
    {
        this.mediaType = mediaType;
        this.startTime = startTime;
        this.duration = duration;
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
