package com.team1.player;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class SmilRegion 
{    
	private String id;
	private Rect region;
	private String colorString; 
	private Paint color = new Paint();
	
    private static final String DEFAULT_BACKGROUND_COLOR = "#000000";

	public SmilRegion ( String id, int top, int left, int width, int height ) 
	{
		this.id = id;
		region = new Rect ( left, top, left + width, top + height );
		
        colorString = DEFAULT_BACKGROUND_COLOR;
        color = new Paint ( );
        color.setColor ( Color.parseColor ( ( colorString ) ) );
	}
	
	public SmilRegion ( String id, String color, int left, int top, int width, int height ) 
	{
		this.id = id;
		region = new Rect ( left, top, left + width, top + height );

        colorString = color;
        this.color = new Paint ( );
        this.color.setColor ( Color.parseColor ( ( colorString ) ) );
	}
	
	public SmilRegion ( SmilRegion region ) 
	{
		Rect rect = region.getRect ( );
		this.id = region.getId ( );
		this.region = new Rect ( rect.top, rect.left, rect.width ( ), rect.height ( ) );
		color.setStyle ( region.getColor ( ).getStyle ( ) );
		color.setColor ( region.getColor ( ).getColor ( ) );
	}
	
	public String getId ( ) 
	{
		return id;
	}
	
	public void setRect ( Rect region ) 
	{
		this.region = region;
	}

	public Rect getRect ( ) 
	{
		return region;
	}
	
	public Paint getColor ( ) 
	{
		return color;
	}
	
	public String getColorAsString ( )
	{
	    return colorString;
	}

	public void setColor ( String color ) 
	{
        colorString = color;
        this.color.setColor ( Color.parseColor ( ( colorString ) ) );
	}
}
