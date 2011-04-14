package com.team1.Smil;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;

public abstract class SmilComponent
{    
    private int        type = SmilConstants.COMPONENT_TYPE_NONE;
    private String     fileName; 
    private String     filePath; 
    private Uri        mediaUri;
    private String     tag;
    private int        begin;
    private int        end;
    private SmilRegion region;
    private boolean    repeat;
    
    public SmilComponent ( ) 
    {
        this.begin = 0;
        this.end   = 0;
        this.fileName = null;
        this.filePath = null;
        this.region   = null;
        this.repeat   = false;
    }

    public SmilComponent ( String source, SmilRegion region, int begin, int end ) 
    {
        this.begin    = begin;
        this.end      = end;
        if ( source != null ) {
            String[] derivingName = source.split( "/" );
            if( derivingName.length > 1){
                this.fileName = derivingName[derivingName.length - 1];
            }
        }
        fileName = source;
        filePath = source;
        this.region   = region;
        this.repeat   = false;
    }
    
    public int getType ( )
    {
        return type;
    }

    public String getTypeAsString ( )
    {
        if ( type == SmilConstants.COMPONENT_TYPE_TEXT  ) return "text";
        if ( type == SmilConstants.COMPONENT_TYPE_IMAGE ) return "img";
        if ( type == SmilConstants.COMPONENT_TYPE_AUDIO ) return "audio";
        return "video";
    }

    public void setType ( int type )
    {
        this.type = type;
    }

    public String getTag ( )
    {
        return tag;
    }

    public void setTag ( String tag )
    {
        this.tag = tag;
    }

    public String getSource ( ) 
    {
        if(this.fileName.startsWith( "data:," ))
            return this.fileName;
        else
            return this.filePath;// + this.fileName;
    }
    
    public String getFilePath ( )
    {
        return this.filePath;
    }

    public void setFilePath ( String path )
    {
        this.filePath = path;
    }

    public Uri getMediaUri ( )
    {
        return this.mediaUri;
    }

    public void setMediaUri ( Uri p )
    {
        this.mediaUri = p;
    }

    public String getFileName ( )
    {
        return this.fileName;
    }
    
    public void setFileName ( String name )
    {
        this.fileName = name;
    }
    
    //public void setSource ( String source ) 
    //{
    //    this.src = source;
    //}
    
    public int getBegin ( )
    {
        return begin;
    }

    public void setBegin ( int begin )
    {
        this.begin = begin;
    }

    public int getEnd ( )
    {
        return end;
    }

    public void setEnd ( int end )
    {
        this.end = end;
    }
    
    public SmilRegion getRegion ( ) 
    {
        return region;
    }

    public void setRegion ( SmilRegion region )  
    {
        this.region = region;
    }

    public boolean getRepeat ( )
    {
        return this.repeat;
    }

    public void setRepeat ( boolean repeat )
    {
        this.repeat = repeat;
    }

    
    public abstract void play ( Canvas canvas );
    public abstract void stop ( Canvas canvas );
    public abstract String getText ( );
    public abstract void setText ( String text );
    public abstract Bitmap getImage ( );
    public abstract void setImage ( Bitmap image );
    public abstract int getFontSize ( );
    public abstract void setFontSize ( int size );
}
