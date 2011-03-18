package smil.mobile.activity;

import android.graphics.Canvas;


public abstract class SmilComponent
{
    private int type = SmilConstants.COMPONENT_TYPE_NONE;
    private String src; 
    private int begin;
    private int end;
    private SmilRegion region;
    

    public SmilComponent ( String source, SmilRegion region, int begin, int end ) 
    {
        this.begin = begin;
        this.end = end;
        this.src = source;
        this.region = region;
    }
    
    public int getType ( )
    {
        return type;
    }

    public void setType ( int type )
    {
        this.type = type;
    }

    public String getSource ( ) 
    {
        return src;
    }
    
    public void setSource ( String source ) 
    {
        this.src = source;
    }
    
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
   
    
    public abstract void play ( Canvas canvas );
    public abstract void stop ( Canvas canvas );
    
}
