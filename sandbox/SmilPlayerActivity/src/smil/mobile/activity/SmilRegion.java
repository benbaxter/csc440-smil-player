package smil.mobile.activity;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class SmilRegion 
{
	private String id;
	private Rect region;
	private Paint color = new Paint();
	
	public SmilRegion ( String id, int top, int left, int width, int height ) 
	{
		this.id = id;
		region = new Rect ( left, top, left + width, top + height );
		color.setColor ( Color.parseColor ( "#000000" ) );
	}
	
	public SmilRegion ( String id, String color, int top, int left, int width, int height ) 
	{
		this.id = id;
		region = new Rect ( left, top, left + width, top + height );
		this.color.setColor ( Color.parseColor ( color ) );
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

	public void setColor ( Paint color ) 
	{
		this.color = color;
	}
}
