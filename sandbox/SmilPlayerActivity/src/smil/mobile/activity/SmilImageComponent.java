package smil.mobile.activity;

import smil.mobile.activity.SmilConstants;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;


public class SmilImageComponent extends SmilComponent
{
    private Bitmap image;
    
    public SmilImageComponent ( String source, SmilRegion region, int begin, int end ) 
    {
        super ( source, region, begin, end );
        super.setType ( SmilConstants.COMPONENT_TYPE_IMAGE );
        loadImageFromSource ( );
    }
    
    /**
     * Sets {@link mImage} to the image located at the source. 
     * Should only be called when the source of the image has changed.
     */
    private void loadImageFromSource ( )
    {
        image = BitmapFactory.decodeFile ( super.getSource());
        
        //check to make sure file was decoded
        if ( image == null )
        {
            image = Bitmap.createBitmap ( 50, 50, Bitmap.Config.RGB_565 );
        }
    }
    
    @Override
    public void play ( Canvas canvas ) 
    {
        Rect r = super.getRegion().getRect ( );
        canvas.save ( );
        canvas.clipRect ( r );
        canvas.drawBitmap ( image, r.left, r.top, null );
        canvas.restore ( );
    }

    @Override
    public void stop ( Canvas canvas ) 
    {
    }
}
