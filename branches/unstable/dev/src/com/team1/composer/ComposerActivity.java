package com.team1.composer;

import java.util.LinkedList;
import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.util.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

import com.team1.composer.drag.DragController;
import com.team1.composer.drag.DragLayer;
import com.team1.composer.send.SendActivity;

public class ComposerActivity extends Activity {
	/** Called when the activity is first created. */
	private static final int AUDIO = 0;
	private static final int IMAGE = 1;
	private static final int TEXT = 2;
	private static final int VIDEO = 3;

	private static final int ADD_DIALOG = 4;
	private static final int SAVE_CONFIRM = 5;
	
	private static final int CLEAR = 6;
    private static final int MAIN = 7;
    private static final int EXIT = 8;
    
    private final static int EDIT_MEDIA = 9;
    private final static int ADD_MEDIA = 10;
    private final static int SEND_MESSAGE = 11;
	
	private DragController mDragController;
	private DragLayer mDragLayer;
	private Dialog dialog = null;
	
	private static int mediaCount = 0;
	
	public static final boolean Debugging = false;
	
	static LinkedList<Media> media;
	Toast toast;
	
	public static LinkedList<Media> getMedia()
	{
	    return media;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDragController = new DragController(this);

		setContentView(R.layout.composer);
		
		setupListeners();
	
		media = new LinkedList<Media>();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, CLEAR, 0, "Clear");
        menu.add(0, MAIN, 0, "Main Menu");
        menu.add(0, EXIT, 0, "Exit");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case EXIT:
            finish();
            return true;
        case MAIN:
            finish();
            break;
        case CLEAR:
            mDragLayer.removeAllViews();
            media.clear();
            break;
        }
        return false;
    }
	
	OnClickListener viewClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.editText)
			{
			    for(int i=0; i<media.size(); i++)
			    {
			        if(media.get( i ).getMediaTag().equals( v.getTag() ))
			        {
			           editMediaPropertiesActivity(i);
			        }
			    }
			}
			else if(v.getId() == R.id.image)
			{
				toast("You clicked an image with tag " + v.getTag());
			}
		}
	};

	OnClickListener buttonClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.addBtn) {
				showDialog(ADD_DIALOG);
			} else if (v.getId() == R.id.saveBtn) {
				toast("Clicking this button allows the user save their current message.");
			} else if (v.getId() == R.id.undoBtn) {
				toast("Clicking this button allows the user undo thier last change.");
			} else if (v.getId() == R.id.sendBtn) {
			    
			    openSendActivity();
				/**/
			} else if ( v.getId() == R.id.homeBtn) {
			    showDialog( SAVE_CONFIRM );
			}
		}
	};

	OnLongClickListener viewLongClick = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			if (!v.isInTouchMode()) {
				toast("isInTouchMode returned false. Try touching the view again.");
				return false;
			}
			return startDrag(v);
		}
	};
	
	private void openMediaPropertiesActivity() {
        Intent mMediaPropIntent = new Intent(this, MediaPropertiesActivity.class);
        startActivityForResult( mMediaPropIntent, ADD_MEDIA );
    }
	
	private void openSendActivity() {
        Intent mSendIntent = new Intent(this.getApplicationContext(), SendActivity.class);
        startActivityForResult( mSendIntent, SEND_MESSAGE );
    }
	
	public void openGalleryActivity(){
	    Intent intentBrowseFiles = new Intent(Intent.ACTION_GET_CONTENT);
	    intentBrowseFiles.setType("image/*");
	    intentBrowseFiles.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(intentBrowseFiles);
	}
	
	private void editMediaPropertiesActivity( int index ) {
        Intent mMediaPropIntent = new Intent(this, MediaPropertiesActivity.class);
        mMediaPropIntent.putExtra("INDEX", index);
        startActivityForResult( mMediaPropIntent, EDIT_MEDIA );
    }
	
	private void openFileChooserActivity(){
        Intent mIntent = new Intent( this.getApplicationContext(),
                FileChooserListActivity.class);
        mIntent.putExtra("File Chooser", "");
        startActivityForResult( mIntent, 10 );
    }

    protected Dialog onCreateDialog(int id) {
		switch (id) {
		case ADD_DIALOG:
			final CharSequence[] items = { "Audio", "Image", "Text", "Video" };

			AlertDialog.Builder buildSelecter = new AlertDialog.Builder(this);
			buildSelecter.setTitle("Pick a Thing");
			buildSelecter.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					addToCanvas(item);
				}
			});
			dialog = buildSelecter.create();
			break;
		case SAVE_CONFIRM:
		    //Ask the user if they want to quit
            AlertDialog.Builder buidSave = new AlertDialog.Builder(this);
            buidSave.setIcon(android.R.drawable.ic_dialog_alert);
            buidSave.setTitle(R.string.quit);
            buidSave.setMessage(R.string.quit_message);
            buidSave.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Stop the activity
                    ComposerActivity.this.finish();    
                }
            });
            buidSave.setNegativeButton(R.string.no, null);
            dialog = buidSave.create();
		    break;
		}
		return dialog;
	}
	
	public void addToCanvas(int what) {
        //need to see of user hit cancel...
		if (what == AUDIO) {
		    media.add ( new Media ( Media.AUDIO_TYPE, "audio" + mediaCount ) );
//            openMediaPropertiesActivity();
		    mediaCount++;
		    openFileChooserActivity();
	    } else if (what == IMAGE) {
		    media.add ( new Media ( Media.IMAGE_TYPE, "image" + mediaCount ) );
		    mediaCount++;
            openMediaPropertiesActivity();
		} else if (what == TEXT) {
		    media.add ( new Media ( Media.TEXT_TYPE, "text" + mediaCount ) );
		    mediaCount++;
		    openMediaPropertiesActivity();
		} else if (what == VIDEO) {
		    media.add ( new Media ( Media.VIDEO_TYPE, "video" + mediaCount ) );
		    mediaCount++;
            openGalleryActivity();
		}
	}
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        showDialog( SAVE_CONFIRM );
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
	  super.onActivityResult(reqCode, resultCode, data);
	  switch (reqCode) {
	    case (ADD_MEDIA) :
	          if (resultCode == Activity.RESULT_OK) {
	              int type = media.getLast().getMediaType();
	              if( type == Media.AUDIO_TYPE){
	                  toast( "AUDIO ADDED" );
                  } else if (type == Media.IMAGE_TYPE) {
                      addImageToCanvas();
                  } else if (type == Media.TEXT_TYPE) {
                      String text = media.getLast().getText();
                      addTextToCanvas(text);
                  } else if (type == Media.VIDEO_TYPE) {
                      toast( "Video comming soon" );
                  }
	          } else if ( resultCode == Activity.RESULT_CANCELED) {
	            media.removeLast();  
	          }
	          break;
	    case (EDIT_MEDIA) :
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                int index = extras.getInt( "INDEX" );
                
                int type = media.get( index ).getMediaType();
                if( type == Media.AUDIO_TYPE){
                    toast( "AUDIO EDITED" );
                } else if (type == Media.IMAGE_TYPE) {
                    toast("IMAGE EDITED");
                } else if (type == Media.TEXT_TYPE) {
                    String text = media.get( index ).getText();
                    TextView tv = (TextView)mDragLayer.findViewWithTag(media.get( index ).getMediaTag());
                    tv.setText(text);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, media.get( index ).getFontSize());
                    tv.setWidth(LayoutParams.WRAP_CONTENT);
                    tv.setHeight(LayoutParams.WRAP_CONTENT);
                } else if (type == Media.VIDEO_TYPE) {
                    toast( "Video comming soon" );
                }
            } else if ( resultCode == Activity.RESULT_CANCELED) {
              //media.removeLast();  
            }
            break;
	    case (SEND_MESSAGE) :
            if (resultCode == Activity.RESULT_OK) {
                finish();
            } 
            break;
	    default :
	        Log.i("CODE", Integer.toString( reqCode ) );
	        Log.i("CODE", Integer.toString( resultCode) );
	        break;
	  }
	}
	
	public void addImageToCanvas() {
	    ImageView newView;
        
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.image_add, null);

        newView = (ImageView) itemView.findViewById(R.id.image);
        newView.setImageResource(R.drawable.icon);
        
        newView.setTag( media.getLast().getMediaTag() );
        
        //mDragLayer.addView(itemView, new DragLayer.LayoutParams(media.getLast().getHeight(), media.getLast().getWidth(), 0, 0));
        mDragLayer.addView(itemView, new DragLayer.LayoutParams(40, 40, 0, 0));
        
        newView.setOnClickListener(viewClick);
        newView.setOnLongClickListener(viewLongClick);
        mDragLayer.invalidate();
        
	}
	
	public void addTextToCanvas(String text) {
	    TextView newView;
        
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.text_add, null);
        
        newView = (TextView) itemView.findViewById(R.id.editText);
        newView.setText(text);
        newView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, media.getLast().getFontSize());
        
        newView.setTag( media.getLast().getMediaTag() );
        
        mDragLayer.addView(itemView, new DragLayer.LayoutParams(LayoutParams.WRAP_CONTENT, 
                LayoutParams.WRAP_CONTENT, 0, 0));
        
        newView.setOnClickListener(viewClick);
        newView.setOnLongClickListener(viewLongClick);
        mDragLayer.invalidate();
    }
	
	public boolean startDrag(View v) {
		Object dragInfo = v;
		mDragController.startDrag(v, mDragLayer, dragInfo,
				DragController.DRAG_ACTION_MOVE);
		return true;
	}

	private void setupListeners() {
		DragController dragController = mDragController;

		mDragLayer = (DragLayer) findViewById(R.id.Canvas);
		mDragLayer.setDragController(dragController);
		dragController.addDropTarget(mDragLayer);
		
		Button add = (Button) findViewById(R.id.addBtn);
		Button save = (Button) findViewById(R.id.saveBtn);
		Button undo = (Button) findViewById(R.id.undoBtn);
		Button send = (Button) findViewById(R.id.sendBtn);
		Button homeBtn = (Button) findViewById(R.id.homeBtn);

		add.setOnClickListener(buttonClick);
		save.setOnClickListener(buttonClick);
		undo.setOnClickListener(buttonClick);
		send.setOnClickListener(buttonClick);
		homeBtn.setOnClickListener(buttonClick);
		
	}
	
	public void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}
}