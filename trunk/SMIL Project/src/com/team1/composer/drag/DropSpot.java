/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.team1.composer.drag;

import java.util.LinkedList;

import com.team1.R;
import com.team1.Smil.SmilComponent;
import com.team1.composer.ComposerActivity;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * This class describes an area within a DragLayer where a dragged item can be dropped.
 * It is a subclass of MyAbsoluteLayout, which means that it is a ViewGroup and views
 * can be added as child views. 
 *
 * <p> In the onDrop method, the view dropped is not added as a child view. Instead, the
 * view is repositioned within the DragLayer that contains the DropSpot.
 * If the DropSpot is not associated with a DragLayer, it will not accept dropped objects.
 *
 */

public class DropSpot extends MyAbsoluteLayout
    implements DropTarget, DragController.DragListener
{


/**
 */
// Constructors

public DropSpot (Context context) {
   super  (context);
}
public DropSpot (Context context, AttributeSet attrs) {
	super (context, attrs);
}
public DropSpot (Context context, AttributeSet attrs, int style) 
{
	super (context, attrs, style);
}


/**
 */
// Instance Variables

private DragController mDragController;
private DragLayer mDragLayer;

/**
 */
// Properties

/**
 * Get the value of the DragController property.
 * 
 * @return DragController
 */

public DragController getDragController ()
{
   return mDragController;
} // end getDragController

/**
 * Set the value of the DragController property.
 * 
 * @param newValue DragController
 */

public void setDragController (DragController newValue)
{
   mDragController = newValue;
} // end setDragController

/**
 * Get the value of the DragLayer property.
 * 
 * @return DragLayer
 */

public DragLayer getDragLayer ()
{
   return mDragLayer;
} // end getDragLayer

/**
 * Set the value of the DragLayer property.
 * 
 * @param newValue DragLayer
 */

public void setDragLayer (DragLayer newValue)
{
   mDragLayer = newValue;
} // end setDragLayer

/* Property SavedBackground */
/**
 * This variable holds the value of the SavedBackground property.
 */

   private int mSavedBackground = R.color.deleteSpot;

/**
 * Get the value of the SavedBackground property.
 * 
 * @return int
 */

public int getSavedBackground ()
{
   //if (mSavedBackground == null) {}
   return mSavedBackground;
} // end getSavedBackground

/**
 * Set the value of the SavedBackground property.
 * 
 * @param newValue int
 */

public void setSavedBackground (int newValue)
{
   mSavedBackground = newValue;
} // end setSavedBackground
/* end Property SavedBackground */

/**
 */
// DragListener interface implementation

/**
 * A drag has begun
 * 
 * @param source An object representing where the drag originated
 * @param info The data associated with the object that is being dragged
 * @param dragAction The drag action: either {@link DragController#DRAG_ACTION_MOVE}
 *        or {@link DragController#DRAG_ACTION_COPY}
 */
public void onDragStart(DragSource source, Object info, int dragAction) 
{
    setBackgroundResource( R.drawable.delete_passive );
}

/**
 * The drag has eneded
 */
public void onDragEnd() 
{
}


/**
 */
// DropTarget interface implementation

/**
 * Handle an object being dropped on the DropTarget.
 * This is the where a dragged view gets repositioned at the end of a drag.
 * 
 * @param source DragSource where the drag started
 * @param x X coordinate of the drop location
 * @param y Y coordinate of the drop location
 * @param xOffset Horizontal offset with the object being dragged where the original
 *          touch happened
 * @param yOffset Vertical offset with the object being dragged where the original
 *          touch happened
 * @param dragView The DragView that's being dragged around on screen.
 * @param dragInfo Data associated with the object being dragged
 * 
 */
public void onDrop(DragSource source, int x, int y, int xOffset, int yOffset,
        DragView dragView, Object dragInfo)
{

    View v = (View) dragInfo;
    LinkedList<SmilComponent> media = ComposerActivity.getMedia();
    
    mDragLayer.removeView( v );
    
    for ( int i = 0; i < media.size(); i++ )
    {
        if ( media.get( i ).getTag().equals( v.getTag() ))
        {
            ComposerActivity.getMedia().remove( i );
        }
    }
    mDragLayer.findViewById(R.id.delete).setVisibility( View.GONE );

}

/**
 * React to a dragged object entering the area of this DropSpot.
 * Provide the user with some visual feedback.
 */    
public void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset,
        DragView dragView, Object dragInfo)
{
    int bg = R.color.red;
//    setBackgroundResource (bg);
    setBackgroundResource( R.drawable.delete_active );
}

/**
 * React to something being dragged over the drop target.
 */    
public void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset,
        DragView dragView, Object dragInfo)
{
}

/**
 * React to a drag 
 */    
public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset,
        DragView dragView, Object dragInfo)
{
//    setBackgroundResource (getSavedBackground ());
    setBackgroundResource( R.drawable.delete_passive );

}

/**
 * Check if a drop action can occur at, or near, the requested location.
 * This may be called repeatedly during a drag, so any calls should return
 * quickly.
 * 
 * @param source DragSource where the drag started
 * @param x X coordinate of the drop location
 * @param y Y coordinate of the drop location
 * @param xOffset Horizontal offset with the object being dragged where the
 *            original touch happened
 * @param yOffset Vertical offset with the object being dragged where the
 *            original touch happened
 * @param dragView The DragView that's being dragged around on screen.
 * @param dragInfo Data associated with the object being dragged
 * @return True if the drop will be accepted, false otherwise.
 */
public boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset,
        DragView dragView, Object dragInfo)
{
    return isEnabled ();
}

/**
 * Estimate the surface area where this object would land if dropped at the
 * given location.
 * 
 * @param source DragSource where the drag started
 * @param x X coordinate of the drop location
 * @param y Y coordinate of the drop location
 * @param xOffset Horizontal offset with the object being dragged where the
 *            original touch happened
 * @param yOffset Vertical offset with the object being dragged where the
 *            original touch happened
 * @param dragView The DragView that's being dragged around on screen.
 * @param dragInfo Data associated with the object being dragged
 * @param recycle {@link Rect} object to be possibly recycled.
 * @return Estimated area that would be occupied if object was dropped at
 *         the given location. Should return null if no estimate is found,
 *         or if this target doesn't provide estimations.
 */
public Rect estimateDropLocation(DragSource source, int x, int y, int xOffset, int yOffset,
            DragView dragView, Object dragInfo, Rect recycle)
{
    return null;
}

/**
 */
// Methods

/**
 * Return true if this DropSpot is hooked up to a DragLayer.
 * If it is, it means that it will accept dropped views.
 * 
 * @return boolean
 */

public boolean isEnabled ()
{
   return (mDragLayer != null);
} // end getDragLayer

/**
 * Set up the drop spot by connecting it to a drag layer and a drag controller.
 * When this method completes, the drop spot is listed as one of the drop targets of the controller
 * and the drop spot is also set as a DragListener.
 *
 * If the dragLayer is null, the drop spot is set initially in a state where nothing can be dropped on it.
 * 
 * @param layer DragLayer
 * @param controller DragController
 * @param initialColor int - resource id of the initial background color
 */

public void setup (DragLayer layer, DragController controller, int initialColor)
{
    mDragLayer = layer;
    mDragController = controller;
    setSavedBackground (initialColor);    

    if (controller != null) {
       controller.setDragListener (this);
       controller.addDropTarget (this);
    }
}

/**
 */
// More methods

/**
 * Show a string on the screen via Toast.
 * 
 * @param msg String
 * @return void
 */



} // end DropSpot
