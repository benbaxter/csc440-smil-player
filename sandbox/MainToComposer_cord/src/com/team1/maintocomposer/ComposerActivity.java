package com.team1.maintocomposer;

import com.team1.maintocomposer.drag.DragController;
import com.team1.maintocomposer.drag.DragLayer;
import java.util.*;
import android.app.*;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.ViewGroup.*;
import android.widget.*;

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
            AbsoluteLayout layout = (AbsoluteLayout) findViewById(R.id.Canvas);
            layout.removeAllViews();
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
					TextView msgTxt = (TextView) ComposerActivity.this
							.findViewById(R.id.editText);
					if (msgTxt != null && !msgTxt.getText().equals("")) {
					    String msg = msgTxt.getText().toString();
						sendSmsMessage(addrTxt.getText().toString(), msg);
						
						Toast.makeText(ComposerActivity.this, "SMS Sent",
						        Toast.LENGTH_LONG).show();
					}
					else 
					{
					    Toast.makeText(ComposerActivity.this, "No Text to send.",
                                Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
					Toast.makeText(ComposerActivity.this, "Failed to send SMS",
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			} else if ( v.getId() == R.id.backBtn) {
			    finish();
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

        startActivityForResult( mMediaPropIntent, RESULT_OK );
        Log.i("Method", "Done Start");
        stopService( mMediaPropIntent );
        
        Log.i("Method", "Done Stop");
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
	/*
	OnTouchListener drag = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
		    AbsoluteLayout.LayoutParams par = (android.widget.AbsoluteLayout.LayoutParams) v.getLayoutParams();
		    int padding = 10;
		    int topmargin = findViewById ( R.id.number ).getBottom ( );
		    int x = (int) event.getRawX() - (v.getWidth() / 2) - padding;
		    int y = (int) event.getRawY() - (v.getHeight()) - topmargin - padding;
		    if( v instanceof TextView )
		    {
		        x = (int) event.getRawX() - v.getWidth();
		    }
			switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE: {
			    par.x = x; 
				par.y = y;
				v.setLayoutParams(par);
				break;
			}// inner case MOVE
			case MotionEvent.ACTION_UP: {
//				par.height = 40;
//				par.width = 40;
				par.x = x;
				par.y = y;
				v.setLayoutParams(par);
				break;
			}// inner case UP
			case MotionEvent.ACTION_DOWN: {
				par.x = x;
				par.y = y;
//				par.height = 60;
//				par.width = 60;
				v.setLayoutParams(par);
				break;
			}// inner case UP
			}// onTouch
			return true;
		}
	}; // drag


*/
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
	        Toast.makeText(ComposerActivity.this, "Audio not supported", Toast.LENGTH_SHORT).show();
	    } else if (what == IMAGE) {
		    media.add ( new Media ( Media.IMAGE_TYPE ) );
            openMediaPropertiesActivity();
			addImageToCanvas();
		} else if (what == TEXT) {
		    //addTextToCanvas();
		    media.add ( new Media ( Media.TEXT_TYPE ) );
		    openMediaPropertiesActivity();
		    String text = media.getLast().getText();
            addTextToCanvas(text);
		} else if (what == VIDEO) {
		    Toast.makeText(ComposerActivity.this, "Video not supported", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
	    Log.i("CODE", Integer.toString( requestCode ) );
	    Log.i("CODE", Integer.toString( resultCode) );
//	    if( requestCode == requestCode)
//	    addTextToCanvas();
	    super.onActivityResult( requestCode, resultCode, data );
	    
	};

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
        if (media.getLast().getText() != null)
            Log.i("TEXT", media.getLast().getText());

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

		add.setOnClickListener(buttonClick);
		save.setOnClickListener(buttonClick);
		undo.setOnClickListener(buttonClick);
		send.setOnClickListener(buttonClick);
		backBtn.setOnClickListener(buttonClick);
		
	}
	
	public void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}
	
}