package com.team1.composer;

import java.util.LinkedList;
import java.util.StringTokenizer;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
	private static final int AUDIO_DIALOG = 6;
	
	private static final int CLEAR = 7;
    private static final int MAIN = 8;
    private static final int EXIT = 9;
    
    private final static int EDIT_MEDIA = 10;
    private final static int ADD_MEDIA = 11;
    private final static int SEND_MESSAGE = 12;
    private final static int MEDIA_PICK = 13;
//    private final static int IMAGE_PICK = 14;
//    private final static int VIDEO_PICK = 15;
	
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
            View audio = findViewById(R.id.audio);
            audio.setVisibility( View.GONE );
            media.clear();
            break;
        }
        return false;
    }
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showDialog( SAVE_CONFIRM );
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
	OnClickListener viewClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
		    int index = 0;
		    for(int i=0; i<media.size(); i++)
            {
                if(media.get( i ).getMediaTag().equals( v.getTag() ))
                {
                   index = i; 
                }
            }
		    
		    if(v.getId() == R.id.audio){
		        int audioCount = 0;
		        for(int i=0; i<media.size(); i++)
	            {
	                if(media.get( i ).getMediaType() == Media.AUDIO_TYPE)
	                {
	                   audioCount++; 
	                }
	            }
		        
		        if(audioCount == 1) {
		            for(int i=0; i<media.size(); i++)
		            {
		                if(media.get( i ).getMediaTag().equals( v.getTag() ))
		                {
		                   index = i; 
		                }
		            }
		            editMediaPropertiesActivity(index);
		        }
		        else {
		            //showDialog(AUDIO_DIALOG);
		        }
		    }
		    else if(v.getId() == R.id.editText)
			    editMediaPropertiesActivity(index);
			else if(v.getId() == R.id.image)
			    editMediaPropertiesActivity(index);
			else if(v.getId() == R.id.video)
			    editMediaPropertiesActivity(index);
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
	
	private void openMediaChoserActivity(String type){
        Intent intentBrowseFiles = new Intent(Intent.ACTION_GET_CONTENT);
        intentBrowseFiles.setType(type);
        startActivityForResult( Intent.createChooser(intentBrowseFiles, "Make a Selection"), MEDIA_PICK);
    }
	
	private void editMediaPropertiesActivity( int index ) {
        Intent mMediaPropIntent = new Intent(this, MediaPropertiesActivity.class);
        mMediaPropIntent.putExtra("INDEX", index);
        startActivityForResult( mMediaPropIntent, EDIT_MEDIA );
    }
	
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
	    super.onActivityResult(reqCode, resultCode, data);
	    switch (reqCode) {
	        case (ADD_MEDIA) :
	            if (resultCode == Activity.RESULT_OK) {
	                int type = media.getLast().getMediaType();
	                if( type == Media.AUDIO_TYPE){
	                    addAudioToCanvas();
	                } else if (type == Media.IMAGE_TYPE) {
	                    addImageToCanvas();
	                } else if (type == Media.TEXT_TYPE) {
	                    addTextToCanvas();
	                } else if (type == Media.VIDEO_TYPE) {
	                    addVideoToCanvas();
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
	                    ImageView iv = (ImageView)mDragLayer.findViewWithTag (media.get( index ).getMediaTag() );
	                    iv.setImageBitmap( media.get( index ).getImage() );
	                    iv.setMaxWidth( media.get( index ).getWidth());
	                    iv.setMaxHeight( media.get( index ).getHeight());
	                    DragLayer.LayoutParams lp = new DragLayer.LayoutParams (
                                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                                media.get( index ).getX(), media.get( index ).getY());
                        mDragLayer.updateViewLayout(iv, lp); 
	                } else if (type == Media.TEXT_TYPE) {
	                    String text = media.get( index ).getText();
	                    TextView tv = (TextView)mDragLayer.findViewWithTag( media.get( index ).getMediaTag() );
	                    tv.setText(text);
	                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, media.get( index ).getFontSize());
	                    DragLayer.LayoutParams lp = new DragLayer.LayoutParams (
	                            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
	                            media.get( index ).getX(), media.get( index ).getY());
	                    mDragLayer.updateViewLayout(tv, lp); 
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
	        case (MEDIA_PICK) :
	            if (resultCode == Activity.RESULT_OK) {
    	            Uri selectedUri = data.getData();
                    String selectedPath = null;
                    
                    String dataType = null;
                    if(media.getLast().getMediaType() == Media.AUDIO_TYPE) {
                        dataType = MediaStore.Audio.Media.DATA;
                    } else if(media.getLast().getMediaType() == Media.IMAGE_TYPE) {
                        dataType = MediaStore.Images.Media.DATA;
                    } else if(media.getLast().getMediaType() == Media.VIDEO_TYPE) {
                        dataType = MediaStore.Video.Media.DATA;
                    }
                    
                    String[] projection = { dataType };
                    Cursor cursor = managedQuery(selectedUri, projection, null, null, null);
                    
                    if(cursor != null){
                        int column_index = cursor.getColumnIndexOrThrow(dataType);
                        cursor.moveToFirst();
                        selectedPath = cursor.getString(column_index);
                    }
                    
                    if(selectedPath == null){
                        selectedPath = selectedUri.getPath();
                    }
                    
                    media.getLast().setPath( selectedPath );
                    
                    String[] fileName = selectedPath.split( "/" );
                    media.getLast().setFileName(fileName[fileName.length - 1]);
                        
                    openMediaPropertiesActivity();
    	            
    	            break;
	            }
	        default :
	            Log.i("CODE", Integer.toString( reqCode ) );
	            Log.i("CODE", Integer.toString( resultCode) );
	            break;
	    }
	}

    protected Dialog onCreateDialog(int id) {
		switch (id) {
		case ADD_DIALOG:
			final String[] items = { "Audio", "Image", "Text", "Video" };

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
		case AUDIO_DIALOG:
            final String[] audioItems = new String[5];
            int count = 0;
            for(int i=0; i<media.size(); i++)
            {
                if(media.get( i ).getMediaType() == Media.AUDIO_TYPE)
                {
                   audioItems[count] = "stuff";//media.get( i ).getFileName();
                   count++;
                }
            }
            
           
            toast(count+"");
            AlertDialog.Builder buildAudio = new AlertDialog.Builder(this);
            buildAudio.setTitle("Pick Audio to Edit");
            buildAudio.setItems(audioItems, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    //addToCanvas(item);
                }
            });
            dialog = buildAudio.create();
            break;
		}
		return dialog;
	}
	
	public void addToCanvas(int what) {
		if (what == AUDIO) {
		    media.add ( new Media ( Media.AUDIO_TYPE, "audio" + mediaCount ) );
		    mediaCount++;
		    openMediaChoserActivity("audio/*");
	    } else if (what == IMAGE) {
		    media.add ( new Media ( Media.IMAGE_TYPE, "image" + mediaCount ) );
		    mediaCount++;
		    openMediaChoserActivity("image/*");
		} else if (what == TEXT) {
		    media.add ( new Media ( Media.TEXT_TYPE, "text" + mediaCount ) );
		    mediaCount++;
		    openMediaPropertiesActivity();
		} else if (what == VIDEO) {
		    media.add ( new Media ( Media.VIDEO_TYPE, "video" + mediaCount ) );
		    mediaCount++;
		    openMediaChoserActivity("video/*");
		}
	}
	
	public void addAudioToCanvas() {
	    View audio = findViewById(R.id.audio);
	    audio.setVisibility( View.VISIBLE );
	}
	
	public void addImageToCanvas() {
	    ImageView newView;
        
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.image_add, null);

        newView = (ImageView) itemView.findViewById(R.id.image);
        newView.setImageBitmap(media.getLast().getImage());
        newView.setAdjustViewBounds( true );
        newView.setMaxHeight( media.getLast().getHeight() );
        newView.setMaxWidth( media.getLast().getWidth() );
        
        newView.setTag( media.getLast().getMediaTag() );
        
        mDragLayer.addView(itemView, new DragLayer.LayoutParams(LayoutParams.WRAP_CONTENT, 
                LayoutParams.WRAP_CONTENT, 0, 0));
        
        newView.setOnClickListener(viewClick);
        newView.setOnLongClickListener(viewLongClick);
        mDragLayer.invalidate();
        
	}
	
	public void addTextToCanvas() {
	    TextView newView;
        
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.text_add, null);
        
        newView = (TextView) itemView.findViewById(R.id.editText);
        newView.setText( media.getLast().getText() );
        newView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, media.getLast().getFontSize());
        
        newView.setTag( media.getLast().getMediaTag() );
        
        mDragLayer.addView(itemView, new DragLayer.LayoutParams(LayoutParams.WRAP_CONTENT, 
                LayoutParams.WRAP_CONTENT, 0, 0));
        
        newView.setOnClickListener(viewClick);
        newView.setOnLongClickListener(viewLongClick);
        mDragLayer.invalidate();
    }
	
	public void addVideoToCanvas() {
	    VideoView newView;
	    
	    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
	    View itemView = inflater.inflate (R.layout.video_add, null);
	    
	    newView = (VideoView) itemView.findViewById(R.id.video);
//	    newView.setAdjustViewBounds( true );
//	    newView.setMaxHeight( media.getLast().getHeight() );
//	    newView.setMaxWidth( media.getLast().getWidth() );
//	    
	    newView.setVideoPath((media.getLast().getFileName()));
	    
	    newView.setTag( media.getLast().getMediaTag() );
	    
	    newView.stopPlayback();
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
		
		View audio = findViewById(R.id.audio);
		audio.setOnClickListener(viewClick);
		
	}
	
	public void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}
}