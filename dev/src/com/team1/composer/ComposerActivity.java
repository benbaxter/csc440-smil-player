package com.team1.composer;

import java.util.LinkedList;
import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.telephony.*;
import android.util.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

import com.team1.composer.drag.DragController;
import com.team1.composer.drag.DragLayer;

public class ComposerActivity extends Activity {
	/** Called when the activity is first created. */
	private static final int AUDIO = 0;
	private static final int IMAGE = 1;
	private static final int TEXT = 2;
	private static final int VIDEO = 3;

	private static final int ADD_DIALOG = 4;
	private static final int SAVE_CONFIRM = 5;
	private static final int EDIT_TEXT = 6;

	private static final int CLEAR = 7;
    private static final int MAIN = 8;
    private static final int EXIT = 9;
    
    private final static int PICK_CONTACT = 10;
    private final static int EDIT_MEDIA = 11;
    private final static int ADD_MEDIA = 12;
	
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
			    for(int i=0; i<media.size(); i++)
			    {
			        if(media.get( i ).getMediaTag().equals( v.getTag() ))
			        {
			            editMediaPropertiesActivity(i);
			            //toast("click me baby one more time");
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
				EditText addrTxt = (EditText) ComposerActivity.this
						.findViewById(R.id.addrEditText);
				addrTxt.setFocusable(false);

				try {
				        String msg = "You have just received a new SMIL message! Go to our application to check it out!";
				        String[] addr = addrTxt.getText().toString().split( " - " );
				        String address = addr[addr.length - 1];
				        Log.i("ADDRESS", address);
						sendSMSMessage(address, msg);
				} catch (Exception e) {
					Toast.makeText(ComposerActivity.this, "Failed to send SMS",
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			} else if ( v.getId() == R.id.homeBtn) {
			    showDialog( SAVE_CONFIRM );
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
        Intent mMediaPropIntent = new Intent(this, MediaPropertiesActivity.class);
        startActivityForResult( mMediaPropIntent, ADD_MEDIA );
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

	/*
	void sendSMSMessage(String address, String message) throws Exception {
		SmsManager smsMgr = SmsManager.getDefault();
		smsMgr.sendTextMessage(address, null, message, null, null);
		finish();
	}
	*/
	
    //---sends an SMS message to another device---
    private void sendSMSMessage(String phoneNumber, String message)
    {        
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
 
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
            new Intent(SENT), 0);
 
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
            new Intent(DELIVERED), 0);
        BroadcastReceiver sentReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off", 
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        BroadcastReceiver deliveredReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;                        
                }
            }
        };
        //---when the SMS has been sent---
        registerReceiver(sentReceiver, new IntentFilter(SENT));
        
        //---when the SMS has been delivered---
        registerReceiver( deliveredReceiver, new IntentFilter(DELIVERED));        
        
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI); 
//        unregisterReceiver( sentReceiver );
//        unregisterReceiver( deliveredReceiver );

        finish();
    }
	
	/*void sendMMSMessage()
	{
	    Intent sendIntent = new Intent(Intent.ACTION_SEND); 
	    sendIntent.putExtra("sms_body", "some text");
	    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(getDir( "smilmessages", MODE_WORLD_READABLE ).getAbsolutePath() ));
	    sendIntent.setType("image/png");
	}*/
	
	private String getMyPhoneNumber(){  
	    TelephonyManager mTelephonyMgr;  
	    mTelephonyMgr = (TelephonyManager)  
	        getSystemService(Context.TELEPHONY_SERVICE);   
	    return mTelephonyMgr.getLine1Number();  
	}
	
	private void changeContactNumber() {
	    
	    Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
	    startActivityForResult(intent, PICK_CONTACT);
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
		    openFileChooserActivity();
		    mediaCount++;
	    } else if (what == IMAGE) {
		    media.add ( new Media ( Media.IMAGE_TYPE, "image" + mediaCount ) );
            openMediaPropertiesActivity();
            mediaCount++;
		} else if (what == TEXT) {
		    media.add ( new Media ( Media.TEXT_TYPE, "text" + mediaCount ) );
		    openMediaPropertiesActivity();
		    mediaCount++;
		} else if (what == VIDEO) {
		    media.add ( new Media ( Media.VIDEO_TYPE, "video" + mediaCount ) );
            openMediaPropertiesActivity();
            mediaCount++;
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
	    case (PICK_CONTACT) :
	      if (resultCode == Activity.RESULT_OK) {
	        Uri contactData = data.getData();
	        String id = contactData.getLastPathSegment();
	        Cursor c =  getContentResolver().query(Phone.CONTENT_URI,  
                    null, Phone.CONTACT_ID + "=?", new String[] { id },  
                    null);  

	        if (c.moveToFirst()) {
	          String name = c.getString(c.getColumnIndex(Contacts.DISPLAY_NAME));
	          String number = c.getString(c.getColumnIndex(Phone.DATA));
//	          String numberKey = c.getString(c.getColumnIndexOrThrow(People.NUMBER_KEY));
	          if(number == null )
	          {
	              name = "Defaulting";
	              number = getMyPhoneNumber();
	          }

	          EditText phoneNumber = (EditText) findViewById ( R.id.addrEditText );
	          phoneNumber.setText ( name + " - " + number );
	          
	          // TODO Whatever you want to do with the selected contact name.
	        }
	      }
	      break;
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
        
       // mDragLayer.addView(itemView, new DragLayer.LayoutParams(media.getLast().getHeight(), media.getLast().getWidth(), 0, 0));
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
		Button addContact = (Button) findViewById(R.id.addContactBtn);

		add.setOnClickListener(buttonClick);
		save.setOnClickListener(buttonClick);
		undo.setOnClickListener(buttonClick);
		send.setOnClickListener(buttonClick);
		homeBtn.setOnClickListener(buttonClick);
		addContact.setOnClickListener(buttonClick);
		
	}
	
	public void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}
	
}