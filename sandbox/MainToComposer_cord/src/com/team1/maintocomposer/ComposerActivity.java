package com.team1.maintocomposer;

import java.util.LinkedList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
		setContentView(R.layout.composer);

		Button add = (Button) findViewById(R.id.addBtn);
		Button save = (Button) findViewById(R.id.saveBtn);
		Button undo = (Button) findViewById(R.id.undoBtn);
		Button send = (Button) findViewById(R.id.sendBtn);
//		Button newBtn = (Button) findViewById(R.id.newBtn);
		Button backBtn = (Button) findViewById(R.id.backBtn);
		
		add.setOnClickListener(mClick);
		save.setOnClickListener(mClick);
		undo.setOnClickListener(mClick);
		send.setOnClickListener(mClick);
//		newBtn.setOnClickListener(mClick);
		backBtn.setOnClickListener(mClick);
		
		EditText phoneNumber = (EditText) findViewById ( R.id.addrEditText );
		phoneNumber.setText ( getMyPhoneNumber() );
		
		media = new LinkedList<Media>();
	}

	OnClickListener mClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Context context = getApplicationContext();
			toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
			if (v.getId() == R.id.addBtn) {
				showDialog(ADD_DIALOG);

			} else if (v.getId() == R.id.saveBtn) {
				toast.setText("Clicking this button allows the user save their current message.");
				toast.show();
			} else if (v.getId() == R.id.undoBtn) {
				toast.setText("Clicking this button allows the user undo thier last change.");
				toast.show();
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
	
	static final int CLEAR = 2;
    static final int MAIN = 1;
    static final int EXIT = 0;

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
		if (what == IMAGE) {
		    media.add ( new Media ( Media.IMAGE_TYPE ) );
            openMediaPropertiesActivity();
			addImageToCanvas();
		} else if (what == TEXT) {
		    //addTextToCanvas();
		    media.add ( new Media ( Media.TEXT_TYPE ) );
		    openMediaPropertiesActivity();
            addTextToCanvas();
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
		AbsoluteLayout fl = (AbsoluteLayout) findViewById(R.id.Canvas);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.image_add, null);

		ImageView image = (ImageView) itemView.findViewById(R.id.image);
		image.getDrawingCache ( true );
		image.setFocusableInTouchMode(true);
		image.setBackgroundResource(R.drawable.icon);
		image.setOnTouchListener(drag);
		Media mediaImage = media.getLast();
		fl.addView(itemView, new AbsoluteLayout.LayoutParams(mediaImage.getHeight(), mediaImage.getWidth(), 0, 0));
//		fl.addView(itemView, new AbsoluteLayout.LayoutParams(40, 40, 0, 0));
	}
	
	public void addTextToCanvas() {
	    
//	    openMediaPropertiesActivity();
	    
	    AbsoluteLayout fl = (AbsoluteLayout) findViewById(R.id.Canvas);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.text_add, null);
        TextView textToAdd = new TextView(this);
        textToAdd = (TextView) itemView.findViewById(R.id.editText);
        textToAdd.setText( media.getLast().getText() );
        textToAdd.setTextSize(TypedValue.COMPLEX_UNIT_DIP, media.getLast().getFontSize());
        if (media.getLast().getText() != null)
            Log.i("TEXT", media.getLast().getText());
        textToAdd.setFocusableInTouchMode( true );
        //This will place the text string in a random position in the canvas
        textToAdd.setPadding ( (int)(Math.random() * 100), (int)(Math.random() * 100), 0, 0 );
        textToAdd.setOnTouchListener(drag);
        
        fl.addView(itemView, new AbsoluteLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0));
        fl.invalidate();
    }
	
}