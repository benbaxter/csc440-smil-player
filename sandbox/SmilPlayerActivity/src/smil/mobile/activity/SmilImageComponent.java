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
    
    private void loadImageFromSource ( )
    {
        // Decode the image file
        image = BitmapFactory.decodeFile ( super.getSource());
        if ( image == null )
        {
            // Use a blank 50x50 box if the image could not be loaded
            image = Bitmap.createBitmap ( 50, 50, Bitmap.Config.RGB_565 );
        }
    }
    
    @Override
    public void play ( Canvas canvas ) 
    {
        // Get the size of the region the image is
        // to be placed in
        Rect r = super.getRegion().getRect ( );
        
        // Get the size of the image
        Rect imageRect = new Rect();
        imageRect.set ( 0, 0, image.getWidth ( ), image.getHeight ( ) );
        
        // Crop the image to fit within the region and draw it
        canvas.save ( );
        canvas.drawBitmap ( image, imageRect, r, null );
        canvas.restore ( );
    }

    @Override
    public void stop ( Canvas canvas ) 
    {
    }
}
