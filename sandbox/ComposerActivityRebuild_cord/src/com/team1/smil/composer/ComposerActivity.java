package com.team1.smil.composer;

import com.team1.smil.composer.R;
import com.team1.smil.composer.drag.DragController;
import com.team1.smil.composer.drag.DragLayer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ComposerActivity extends Activity {

	private static final int AUDIO = 0;
	private static final int IMAGE = 1;
	private static final int TEXT = 2;
	private static final int VIDEO = 3;

	private static final int ADD_DIALOG = 4;
	private static final int ADD_TEXT = 5;
	private static final int TEXT_EDIT = 6;
	
	private static final int MAIN = 7;
	private static final int EXIT = 8;
	
	private DragController mDragController;
	private DragLayer mDragLayer;
	public static final boolean Debugging = false;
	
	Dialog dialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDragController = new DragController(this);

		setContentView(R.layout.composer);
		setupListeners();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MAIN, 0, "Main Menu");
		menu.add(0, EXIT, 0, "Exit");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case EXIT:
			System.exit(0);
			return true;
		case MAIN:
			finish();
			break;
		}
		return false;
	}

	OnClickListener viewClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.editText)
			{
				showDialog(TEXT_EDIT);
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
			} else {
				// Open the Hello World form
				openHelloActivity();
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
	
	protected Dialog onCreateDialog(int id) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    final EditText edit = new EditText(this);
	    
		switch (id) {
			case ADD_DIALOG:
				final CharSequence[] items = { "Audio", "Image", "Text", "Video" };
	
				builder.setTitle("Pick Media");
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						addToCanvas(item);
					}
				});
				dialog = builder.create();
				break;
				
			case ADD_TEXT:
				builder.setView(edit);
				builder.setTitle("Enter Text:");
				builder.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								addTextToCanvas(edit.getText().toString());
								dialog.dismiss();
	
							}
						});
	
				builder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss();
							}
						});
				dialog = builder.create();
				break;
				
			case TEXT_EDIT:
			    final TextView editText = new TextView(this);
			    
	            builder.setView(edit);
	            builder.setTitle("Edit the Text:");
	            builder.setPositiveButton("Ok",
	                    new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog,
	                                int whichButton) {
	                        	editText.findViewById(R.id.editText);
	                        	editText.setText(edit.getText().toString());
	                            dialog.dismiss();
	                        }
	                    });
	
	            builder.setNegativeButton("Cancel",
	                    new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog,
	                                int whichButton) {
	                            dialog.dismiss();
	                        }
	                    });
	            dialog = builder.create();
	            break;
		}
		return dialog;
	}
	
	public void addToCanvas(int what) {
	    if (what == AUDIO) {
	        Toast.makeText(ComposerActivity.this, "Audio not supported", Toast.LENGTH_SHORT).show();
	    } else if (what == IMAGE) {
			addImageToCanvas();
		} else if (what == TEXT) {
			showDialog (ADD_TEXT );
		} else if (what == VIDEO) {
		    Toast.makeText(ComposerActivity.this, "Video not supported", Toast.LENGTH_LONG).show();
		}
	}
	
	public void addImageToCanvas() {
		ImageView newView;
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.image_add, null);

		newView = (ImageView) itemView.findViewById(R.id.image);
		newView.setImageResource(R.drawable.icon);
		
		mDragLayer.addView(itemView, new DragLayer.LayoutParams(40, 40, 0, 0));
		
		newView.setOnClickListener(viewClick);
		newView.setOnLongClickListener(viewLongClick);
	}
	
	public void addTextToCanvas(String text) {        
		TextView newView;
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.text_add, null);
		
		newView = (TextView) itemView.findViewById(R.id.editText);
		newView.setText(text);
		
		mDragLayer.addView(itemView, new DragLayer.LayoutParams(LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT, 0, 0));
		
		newView.setOnClickListener(viewClick);
		newView.setOnLongClickListener(viewLongClick);
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
		Button newBtn = (Button) findViewById(R.id.newBtn);

		add.setOnClickListener(buttonClick);
		save.setOnClickListener(buttonClick);
		undo.setOnClickListener(buttonClick);
		send.setOnClickListener(buttonClick);
		newBtn.setOnClickListener(buttonClick);
	}
	
	private void openHelloActivity() {
	    Intent mHelloIntent = new Intent(getApplicationContext(),
	            HelloActivity.class);
	    mHelloIntent.putExtra("Hello Activity", "");
	    startActivity(mHelloIntent);
	}
	
	void sendSmsMessage(String address, String message) throws Exception {
	    SmsManager smsMgr = SmsManager.getDefault();
	    smsMgr.sendTextMessage(address, null, message, null, null);
	    finish();
	}

	public void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}
} 
