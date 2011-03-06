package com.team1.maintocomposer;

import java.util.LinkedList;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.telephony.*;
import android.util.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

import com.team1.maintocomposer.drag.DragController;
import com.team1.maintocomposer.drag.DragLayer;

@SuppressWarnings("deprecation")
public class ComposerActivity extends Activity {
	/** Called when the activity is first created. */
	private static final int AUDIO = 0;
	private static final int IMAGE = 1;
	private static final int TEXT = 2;
	private static final int VIDEO = 3;

	private static final int ADD_DIALOG = 4;
	private static final int ADD_TEXT = 5;
	private static final int EDIT_TEXT = 6;

	private static final int CLEAR = 7;
    private static final int MAIN = 8;
    private static final int EXIT = 9;
    
    final static int PICK_CONTACT = 10;
    final static int EDIT_MEDIA = 11;
	
	private DragController mDragController;
	private DragLayer mDragLayer;
	public static final boolean Debugging = false;
	
	static LinkedList<Media> media;
	Toast toast;
	Dialog dialog = null;
	public View image;
	String addingText;
	
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
		
		EditText phoneNumber = (EditText) findViewById ( R.id.addrEditText );
		phoneNumber.setText ( getMyPhoneNumber() );
		
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
				//showDialog(EDIT_TEXT);
			}
			else if(v.getId() == R.id.image)
			{
				toast("You clicked an image");
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
				EditText addrTxt = (EditText) ComposerActivity.this
						.findViewById(R.id.addrEditText);
				addrTxt.setFocusable(false);

				try {
				        String msg = "You have just received a new SMIL message! Go to our application to check it out!";
				        String address = addrTxt.getText().toString().split( " - " )[1];
				        Log.i("ADDRESS", address);
						sendSmsMessage(address, msg);
						Toast.makeText(ComposerActivity.this, "SMS Sent",
						        Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					Toast.makeText(ComposerActivity.this, "Failed to send SMS",
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			} else if ( v.getId() == R.id.backBtn) {
			    finish();
			}
			else if ( v.getId() == R.id.addContactBtn) {
			    changeContactNumber();
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
        Intent mMediaPropIntent = new Intent(this,
                MediaPropertiesActivity.class);
        mMediaPropIntent.putExtra("Media Properties", "");
//        startActivity(mMediaPropIntent);

        startActivityForResult( mMediaPropIntent, EDIT_MEDIA );
    }

	void sendSmsMessage(String address, String message) throws Exception {
		SmsManager smsMgr = SmsManager.getDefault();
		smsMgr.sendTextMessage(address, null, message, null, null);
		finish();
	}
	
	void sendMMSMessage()
	{
	    Intent sendIntent = new Intent(Intent.ACTION_SEND); 
	    sendIntent.putExtra("sms_body", "some text");
	    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(getDir( "smilmessages", MODE_WORLD_READABLE ).getAbsolutePath() ));
	    sendIntent.setType("image/png");
	}
	
	private String getMyPhoneNumber(){  
	    TelephonyManager mTelephonyMgr;  
	    mTelephonyMgr = (TelephonyManager)  
	        getSystemService(Context.TELEPHONY_SERVICE);   
	    return mTelephonyMgr.getLine1Number();  
	}
	
	private void changeContactNumber() {
	    Intent intent = new Intent(Intent.ACTION_PICK, People.CONTENT_URI);
	    startActivityForResult(intent, PICK_CONTACT);
	}
	
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case ADD_DIALOG:
			final CharSequence[] items = { "Audio", "Image", "Text", "Video" };

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Pick a Thing");
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					addToCanvas(item);
				}
			});
			dialog = builder.create();
			break;
		}
		return dialog;
	}
	
	public void addToCanvas(int what) {
        //need to see of user hit cancel...
		if (what == AUDIO) {
		    media.add ( new Media ( Media.AUDIO_TYPE ) );
            openMediaPropertiesActivity();
	    } else if (what == IMAGE) {
		    media.add ( new Media ( Media.IMAGE_TYPE ) );
            openMediaPropertiesActivity();
		} else if (what == TEXT) {
		    media.add ( new Media ( Media.TEXT_TYPE ) );
		    openMediaPropertiesActivity();
		} else if (what == VIDEO) {
		    media.add ( new Media ( Media.VIDEO_TYPE) );
            openMediaPropertiesActivity();
		}
	}

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
	  super.onActivityResult(reqCode, resultCode, data);
	  switch (reqCode) {
	    case (PICK_CONTACT) :
	      if (resultCode == Activity.RESULT_OK) {
	        Uri contactData = data.getData();
	        Cursor c =  managedQuery(contactData, null, null, null, null);
	        if (c.moveToFirst()) {
	          String name = c.getString(c.getColumnIndexOrThrow(People.NAME));
//	          String number = c.getString(c.getColumnIndexOrThrow(People.NUMBER));
	          String numberKey = c.getString(c.getColumnIndexOrThrow(People.NUMBER_KEY));
	          if(numberKey == null )
	          {
	              name = "Defaulting";
	              numberKey = getMyPhoneNumber();
	          }
	          
	          EditText phoneNumber = (EditText) findViewById ( R.id.addrEditText );
	          phoneNumber.setText ( name + " - " + numberKey );
	          // TODO Whatever you want to do with the selected contact name.
	        }
	      }
	      break;
	    case (EDIT_MEDIA) :
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
//        newView.setFocusableInTouchMode(true);
        
//        Media mediaImage = media.getLast();
//        mDragLayer.addView(itemView, new DragLayer.LayoutParams(mediaImage.getHeight(), mediaImage.getWidth(), 0, 0));
        
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
//        newView.setText( media.getLast().getText() );
        newView.setText(text);
        newView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, media.getLast().getFontSize());

        newView.setPadding ( (int)(Math.random() * 100), (int)(Math.random() * 100), 0, 0 );
        
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
		Button backBtn = (Button) findViewById(R.id.backBtn);
		Button addContact = (Button) findViewById(R.id.addContactBtn);

		add.setOnClickListener(buttonClick);
		save.setOnClickListener(buttonClick);
		undo.setOnClickListener(buttonClick);
		send.setOnClickListener(buttonClick);
		backBtn.setOnClickListener(buttonClick);
		addContact.setOnClickListener(buttonClick);
		
	}
	
	public void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}
	
}