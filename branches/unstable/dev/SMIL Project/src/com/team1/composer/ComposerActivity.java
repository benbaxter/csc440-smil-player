package com.team1.composer;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import android.app.*;
import android.content.*;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;


import com.team1.composer.drag.DropSpot;
import com.team1.R;
import com.team1.Smil.*;
import com.team1.communication.SendActivity;
import com.team1.composer.drag.DragController;
import com.team1.composer.drag.DragLayer;
import com.team1.player.*;

public class ComposerActivity extends Activity {

    private static final int ADD_DIALOG = 4;
	private static final int SAVE_CONFIRM = 5;

	private static final int CLEAR = 7;
    private static final int MAIN = 8;
    private static final int EXIT = 9;

    private final static int EDIT_MEDIA = 10;
    private final static int ADD_MEDIA = 11;
    private final static int SEND_MESSAGE = 12;
    private final static int MEDIA_PICK = 13;

	private DragController mDragController;
	
	
	private DragLayer mDragLayer;
	private Dialog dialog = null;

	private static int mediaCount = 0;

	public static final boolean Debugging = false;

    static LinkedList<SmilComponent> media;
    private SmilMessage message;

	Toast toast;

	public static LinkedList<SmilComponent> getMedia()
	{
	    return media;
	}

	@Override
	public void onCreate ( Bundle savedInstanceState ) 
	{
		super.onCreate ( savedInstanceState );
		mDragController = new DragController ( this );

		setContentView ( R.layout.composer );

		setupListeners ( );

		media = new LinkedList<SmilComponent>();

		try
		{
		    Intent in = getIntent ( );
		    if ( in.hasExtra ( "fileName" ) )
		    {
		        String fileName = in.getExtras().getString ( "fileName" );
		        message = SmilReader.parseMessage ( fileName );

		        for ( int idx = 0; idx < message.getResourcesByBeginTime().size ( ); idx++ )
		        {
		            int type = message.getResourcesByBeginTime().get(idx).getType ( );
		            String source = message.getResourcesByBeginTime().get(idx).getSource ( );
		            int begin = message.getResourcesByBeginTime().get(idx).getBegin ( );
		            int end = message.getResourcesByBeginTime().get(idx).getEnd ( );		            
		            
		            if ( SmilConstants.COMPONENT_TYPE_AUDIO == type ) 
		            {
		                SmilRegion region = new SmilRegion ( "", 0, 0, 0, 0 );
                        SmilAudioComponent c = new SmilAudioComponent ( source, region, begin, end );
		                c.setTag( "audio" + mediaCount++ );
		                media.add ( c );
		                
		                addAudioToCanvas ( );
		            } 
		            else if ( SmilConstants.COMPONENT_TYPE_IMAGE == type ) 
		            {
		                SmilRegion region = new SmilRegion ( message.getResourcesByBeginTime().get(idx).getRegion ( ) );
                        SmilImageComponent c = new SmilImageComponent ( source, region, begin, end );
		                c.setTag( "image" + mediaCount++ );
		                media.add ( c );

		                addImageToCanvas ( false );
		                
	                    ImageView iv = (ImageView)mDragLayer.findViewWithTag ( media.getLast().getTag ( ) );
	                    iv.setImageBitmap ( media.getLast().getImage ( ) );
	                    iv.setMaxWidth ( media.getLast().getRegion().getRect().width ( ) );
	                    iv.setMaxHeight ( media.getLast().getRegion().getRect().height ( ) );
	                    DragLayer.LayoutParams lp = new DragLayer.LayoutParams (
	                                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
	                                media.getLast().getRegion().getRect().left, media.getLast().getRegion().getRect().top );
	                    mDragLayer.updateViewLayout ( iv, lp );
		            } 
		            else if ( SmilConstants.COMPONENT_TYPE_TEXT == type ) 
		            {
		                SmilRegion region = new SmilRegion ( message.getResourcesByBeginTime().get(idx).getRegion ( ) );
                        SmilTextComponent c = new SmilTextComponent ( source, region, begin, end );
		                c.setTag( "text" + mediaCount++ );
		                media.add ( c );
		                
		                addTextToCanvas ( false );

	                    String text = media.getLast().getText ( );
	                    TextView tv = (TextView)mDragLayer.findViewWithTag( media.getLast().getTag ( ) );
	                    tv.setText ( text );
	                    tv.setTextSize ( TypedValue.COMPLEX_UNIT_DIP, media.getLast().getFontSize ( ) );
	                    DragLayer.LayoutParams lp = new DragLayer.LayoutParams (
	                                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
	                                media.getLast().getRegion().getRect().left, media.getLast().getRegion().getRect().top );
	                    mDragLayer.updateViewLayout ( tv, lp );
		            } 
		            else if ( SmilConstants.COMPONENT_TYPE_VIDEO == type ) 
		            {
		                SmilRegion region = new SmilRegion ( message.getResourcesByBeginTime().get(idx).getRegion ( ) );
                        SmilVideoComponent c = new SmilVideoComponent ( source, region, begin, end );
		                c.setTag( "video" + mediaCount++ );
		                media.add ( c );
		                
		                addVideoToCanvas ( false );

                        ImageView iv = (ImageView)mDragLayer.findViewWithTag ( media.getLast().getTag ( ) );
                        iv.setMaxWidth ( media.getLast().getRegion().getRect().width ( ) );
                        iv.setMaxHeight ( media.getLast().getRegion().getRect().height ( ) );
                        DragLayer.LayoutParams lp = new DragLayer.LayoutParams (
                                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                                    media.getLast().getRegion().getRect().left, media.getLast().getRegion().getRect().top );
                        mDragLayer.updateViewLayout ( iv, lp );
		            }
		        }
		    }
		}
		catch ( Exception e )
		{
		    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		    Log.e("Exception", "error occurred while creating xml file", e);
		}
	}

	public boolean onCreateOptionsMenu ( Menu menu ) 
	{
        menu.add ( 0, CLEAR, 0, "Clear"     );
        menu.add ( 0,  MAIN, 0, "Main Menu" );
        menu.add ( 0,  EXIT, 0, "Exit"      );
        return true;
    }

    public boolean onOptionsItemSelected ( MenuItem item ) 
    {
        switch ( item.getItemId ( ) ) 
        {
            case MAIN:
            case EXIT:
                finish ( );
                return true;
        
            case CLEAR:
                mDragLayer.removeAllViews ( );
                View audio = findViewById ( R.id.audio );
                audio.setVisibility ( View.GONE );
                media.clear ( );
                break;
        }
        
        return false;
    }

    @Override
    public boolean onKeyDown ( int keyCode, KeyEvent event ) 
    {
        if ( keyCode == KeyEvent.KEYCODE_BACK ) 
        {
            showDialog ( SAVE_CONFIRM );
            return true;
        }
        
        return super.onKeyDown ( keyCode, event );
    }
    
	OnClickListener viewClick = new OnClickListener ( ) 
	{
		@Override
		public void onClick ( View v ) 
		{
		    int index = 0;
		    for ( int i = 0; i < media.size ( ); i++ ) 
		    {
                if ( media.get(i).getTag().equals ( v.getTag ( ) ) ) 
                {
                   index = i;
                }
            }

		    if ( v.getId ( ) == R.id.audio )
	        {               
	            for ( int i = 0; i < media.size ( ); i++ ) 
	            {
	                if ( media.get(i).getType ( ) == SmilConstants.COMPONENT_TYPE_AUDIO ) 
	                {
	                   index = i;
	                   break;
	                }
	            }
	        }
		    
            editMediaPropertiesActivity ( index );

		}
	};

	OnClickListener buttonClick = new OnClickListener ( ) 
	{
		@Override
		public void onClick ( View v ) 
		{
			if ( v.getId() == R.id.addBtn ) 
			{
				showDialog ( ADD_DIALOG );
			} 
			else if ( v.getId() == R.id.saveBtn ) 
			{
		        // Determine where to look (ie Inbox, Outbox, Drafts )
		        int fileIndex = 0;

			    File pathDir = Environment.getExternalStorageDirectory ( );
		        File dir = new File ( pathDir, SmilConstants.DRAFT_PATH );

		        String[] allFiles = dir.list ( );
		        
		        NumberFormat format;
	            format = NumberFormat.getNumberInstance();
	            format.setMinimumIntegerDigits(2); // pad with 0 if necessary

		        if ( allFiles != null )
		        {
		            // Find all of the .smil files in this directory
		            for ( int index = 0; index < allFiles.length; index++ )
		            {
		                if ( allFiles[index].endsWith ( ".smil" ) == true )
		                    ++fileIndex;
		            }
		        }

		        toast ( "Generating SMIL file" );
                
                String file = "draft_" + format.format ( fileIndex ) + ".smil";
			    
                saveSmilFile ( file  );
			} 
			else if ( v.getId() == R.id.undoBtn ) 
			{
			    previewSmilFile ( );
			} 
			else if ( v.getId() == R.id.sendBtn ) 
			{
			    openSendActivity ( );
			} 
			else if ( v.getId() == R.id.homeBtn ) 
			{
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

	public SmilMessage getMessage ( )
	{
        SmilComponent component;
        SmilMessage message;

        message = new SmilMessage ( );
        message.setBackgroundColor ( "blue" );
        message.setCanvasHeight ( 450 );
        message.setCanvasWidth ( 350 );

        for ( int i = 0; i < media.size ( ); i++ )
        {                
            component = media.get( i );
            message.addComponent( component );
        }

        return message;
	}
	
	private void previewSmilFile ( )
	{
        try
        {
            // Save this draft to a temporary .smil file
            SmilGenerator sg = new SmilGenerator ( );
            sg.generateSMILFile ( media );
            
            
            // Preview the temporary .smil file
            Intent mPlayerIntent = new Intent ( this, SmilPlayerActivity.class );
            mPlayerIntent.putExtra ( "playFile", SmilConstants.ROOT_PATH + "test1.smil" );
            startActivity ( mPlayerIntent );
        }
        catch ( Exception e )
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Exception", "error occurred while previewing xml file", e);
        }
	}
	
	private void saveSmilFile ( String fileName )
    {
        try
        {
            // Save this draft
            SmilGenerator sg = new SmilGenerator ( );
            sg.setFileName ( fileName );
            sg.setFilePath ( SmilConstants.DRAFT_PATH );
            sg.generateSMILFile ( media );
        }
        catch ( Exception e )
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Exception", "error occurred while creating xml file", e);
        }
    }

	private void openMediaPropertiesActivity() {
        Intent mMediaPropIntent = new Intent(this, MediaPropertiesActivity.class);
        startActivityForResult( mMediaPropIntent, ADD_MEDIA );
    }

	private void openSendActivity ( ) 
	{
        String smilFile = Environment.getExternalStorageDirectory().getAbsolutePath() + SmilConstants.ROOT_PATH + "test1.smil";
        
        saveSmilFile( getMyPhoneNumber() + ".smil" );

        Intent mSendIntent = new Intent(this.getApplicationContext(), SendActivity.class);
        ArrayList<String> fileNames = new ArrayList<String>();
        for( SmilComponent item : media )
        {
            //if not a text item, then it is an audio, image or video. Send those links to the cloud!
            if( !(item instanceof SmilTextComponent)) 
            {
                fileNames.add( item.getSource() );
            }
        }
        
        mSendIntent.putStringArrayListExtra ( "mediaFiles",  fileNames );
        Log.i ( "SMILE FILE TO BE SENT", smilFile );
        mSendIntent.putExtra ( "smilFile",   smilFile );
        startActivityForResult ( mSendIntent, SEND_MESSAGE );
    }

	private void openMediaChooserActivity(String type){
	    Intent intentBrowseFiles;
        if(type == "video/*") {
            intentBrowseFiles = new Intent(Intent.ACTION_PICK);
            intentBrowseFiles.setType(type);
            startActivityForResult ( intentBrowseFiles, MEDIA_PICK );
        }
        else {
            intentBrowseFiles = new Intent(Intent.ACTION_GET_CONTENT);
            intentBrowseFiles.setType(type);
            startActivityForResult( Intent.createChooser(intentBrowseFiles, "Make a Selection"), MEDIA_PICK);
        }
    }

	private void editMediaPropertiesActivity( int index ) {
        Intent mMediaPropIntent = new Intent(this, MediaPropertiesActivity.class);
        mMediaPropIntent.putExtra("INDEX", index);
        startActivityForResult( mMediaPropIntent, EDIT_MEDIA );
    }

	private String getMyPhoneNumber(){
        TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
        return mTelephonyMgr.getLine1Number();
    }

    private String getMy10DigitPhoneNumber(){
        String s = getMyPhoneNumber();
        return s.substring(2);
    }
    
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
	    super.onActivityResult(reqCode, resultCode, data);
	    switch (reqCode) {
	        case (ADD_MEDIA) :
	            if ( resultCode == Activity.RESULT_OK ) 
	            {	                
	                int type = media.getLast().getType ( );
	                
	                if( type == SmilConstants.COMPONENT_TYPE_AUDIO )
	                {
	                    addAudioToCanvas ( );
	                } 
	                else if ( type == SmilConstants.COMPONENT_TYPE_IMAGE ) 
	                {
	                    addImageToCanvas ( true );
	                } 
	                else if ( type == SmilConstants.COMPONENT_TYPE_TEXT ) 
	                {
	                    addTextToCanvas ( true );
	                } 
	                else if ( type == SmilConstants.COMPONENT_TYPE_VIDEO ) 
	                {
	                    addVideoToCanvas ( true );
	                }
	            } 
	            else if ( resultCode == Activity.RESULT_CANCELED ) 
	            {
	                media.removeLast ( );
	            }
	        break;
	        case (EDIT_MEDIA) :
	            if (resultCode == Activity.RESULT_OK) {
	                Bundle extras = data.getExtras();
	                int index = extras.getInt( "INDEX" );

	                int type = media.get( index ).getType ( );
	                
	                if( type == SmilConstants.COMPONENT_TYPE_AUDIO )
	                {
	                    toast( "AUDIO EDITED" );
	                } 
	                else if ( type == SmilConstants.COMPONENT_TYPE_IMAGE ) 
	                {
	                    ImageView iv = (ImageView)mDragLayer.findViewWithTag (media.get( index ).getTag() );
	                    iv.setImageBitmap( media.get( index ).getImage() );
	                    iv.setMaxWidth( media.get( index ).getRegion().getRect().width());
	                    iv.setMaxHeight( media.get( index ).getRegion().getRect().height());
	                    DragLayer.LayoutParams lp = new DragLayer.LayoutParams (
                                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                                media.get( index ).getRegion().getRect().left, media.get( index ).getRegion().getRect().top);
                        mDragLayer.updateViewLayout(iv, lp);
	                } 
	                else if ( type == SmilConstants.COMPONENT_TYPE_TEXT ) 
	                {
	                    String text = media.get( index ).getText();
	                    TextView tv = (TextView)mDragLayer.findViewWithTag( media.get( index ).getTag() );
	                    tv.setText(text);
	                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, media.get( index ).getFontSize());
	                    DragLayer.LayoutParams lp = new DragLayer.LayoutParams (
	                            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
	                            media.get( index ).getRegion().getRect().left, media.get( index ).getRegion().getRect().top);
	                    mDragLayer.updateViewLayout(tv, lp);
	                } 
	                else if ( type == SmilConstants.COMPONENT_TYPE_VIDEO ) 
	                {
	                    ImageView iv = (ImageView)mDragLayer.findViewWithTag (media.get( index ).getTag() );
                        iv.setMaxWidth( media.get( index ).getRegion().getRect().width());
                        iv.setMaxHeight( media.get( index ).getRegion().getRect().height());
                        DragLayer.LayoutParams lp = new DragLayer.LayoutParams (
                                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                                media.get( index ).getRegion().getRect().left, media.get( index ).getRegion().getRect().top);
                        mDragLayer.updateViewLayout(iv, lp);
	                }
	            } 
	            else if ( resultCode == Activity.RESULT_CANCELED ) 
	            {
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
                    
                    if(media.getLast().getType() == SmilConstants.COMPONENT_TYPE_AUDIO) {
                        dataType = MediaStore.Audio.Media.DATA;
                    } else if(media.getLast().getType() == SmilConstants.COMPONENT_TYPE_IMAGE) {
                        dataType = MediaStore.Images.Media.DATA;
                    } else if(media.getLast().getType() == SmilConstants.COMPONENT_TYPE_VIDEO) {
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
                    Log.i("VIDEO", selectedUri.toString());
                    String[] fileName = selectedPath.split( "/" );
                    media.getLast().setFileName(fileName[fileName.length - 1]);
                    media.getLast().setMediaUri( selectedUri );
                    media.getLast().setFilePath( selectedPath );

                    openMediaPropertiesActivity();

    	            break;
	            }
	        default :
	            Log.i("CODE", Integer.toString( reqCode ) );
	            Log.i("CODE", Integer.toString( resultCode) );
	            break;
	    }
	}

	@Override
    protected Dialog onCreateDialog(int id) {
		switch (id) {
		case ADD_DIALOG:
			final String[] items = { "Audio", "Image", "Text", "Video" };

			AlertDialog.Builder buildSelecter = new AlertDialog.Builder(this);
			buildSelecter.setTitle("Pick Media");
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

	public void addToCanvas ( int what ) 
	{
		if ( SmilConstants.COMPONENT_TYPE_AUDIO == what ) 
		{
		    SmilAudioComponent c = new SmilAudioComponent ( );
            c.setTag( "audio" + mediaCount );

            media.add ( c );

            mediaCount++;
		    openMediaChooserActivity("audio/*");
	    } 
		else if ( SmilConstants.COMPONENT_TYPE_IMAGE == what ) 
		{
		    SmilImageComponent c = new SmilImageComponent ( );
            c.setTag( "image" + mediaCount );
            
            media.add ( c );
           
            mediaCount++;
		    openMediaChooserActivity("image/*");
		} 
		else if ( SmilConstants.COMPONENT_TYPE_TEXT == what ) 
		{
		    SmilTextComponent c = new SmilTextComponent ( );
            c.setTag( "text" + mediaCount );

            media.add ( c );
            
            mediaCount++;
		    openMediaPropertiesActivity();
		} 
		else if ( SmilConstants.COMPONENT_TYPE_VIDEO == what ) 
		{
		    SmilVideoComponent c = new SmilVideoComponent ( );
            c.setTag( "video" + mediaCount );

            media.add ( c );

            mediaCount++;
		    openMediaChooserActivity("video/*");
		}
	}

	public void addAudioToCanvas() {
	    View audio = findViewById(R.id.audio);
	    audio.setVisibility( View.VISIBLE );
	}

	public void addImageToCanvas ( boolean bIsNew ) 
	{
	    ImageView newView;

	    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.video_add, null);

        newView = (ImageView) itemView.findViewById(R.id.video);
        newView.setImageBitmap(media.getLast().getImage());
        
        newView.setAdjustViewBounds( true );
        newView.setMaxHeight( media.getLast().getRegion().getRect().height() );
        newView.setMaxWidth( media.getLast().getRegion().getRect().width() );

        newView.setTag( media.getLast().getTag() );

        if ( true == bIsNew )
        {
            mDragLayer.addView(itemView, new DragLayer.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, 0, 0));
        }
        else
        {
            mDragLayer.addView ( itemView, new DragLayer.LayoutParams ( LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT, media.getLast().getRegion().getRect().left, media.getLast().getRegion().getRect().top ) );
        }

        newView.setOnClickListener(viewClick);
        newView.setOnLongClickListener(viewLongClick);
        mDragLayer.invalidate();

	}

	public void addTextToCanvas ( boolean bIsNew ) 
	{
	    TextView newView;

        LayoutInflater inflater = (LayoutInflater) getSystemService ( LAYOUT_INFLATER_SERVICE );
        View itemView = inflater.inflate ( R.layout.text_add, null );

        newView = (TextView) itemView.findViewById ( R.id.editText );
        newView.setText ( media.getLast().getText ( ) );
        newView.setTextSize ( TypedValue.COMPLEX_UNIT_DIP, media.getLast().getFontSize ( ) );

        newView.setTag ( media.getLast().getTag ( ) );

        SmilRegion region;
        if ( true == bIsNew )
        {
            mDragLayer.addView ( itemView, new DragLayer.LayoutParams ( LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, 0, 0 ) );
        
            region = new SmilRegion(media.getLast().getTag(), "#000000", 0, 
                0, newView.getWidth(), newView.getHeight() ); 

            media.getLast().setRegion ( region );
        }
        else
        {
            mDragLayer.addView ( itemView, new DragLayer.LayoutParams ( LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT, media.getLast().getRegion().getRect().left, media.getLast().getRegion().getRect().top ) );
        }

        newView.setOnClickListener ( viewClick );
        newView.setOnLongClickListener ( viewLongClick );
        mDragLayer.invalidate ( );
    }

	public void addVideoToCanvas ( boolean bIsNew ) 
	{
	    ImageView newView;

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.video_add, null);

        newView = (ImageView) itemView.findViewById(R.id.video);
        Resources res = getResources();
        BitmapDrawable thumb = new BitmapDrawable(res, media.getLast().getImage());
        newView.setBackgroundDrawable( thumb );
        
        newView.setAdjustViewBounds( true );
        newView.setMaxHeight( media.getLast().getRegion().getRect().height() );
        newView.setMaxWidth( media.getLast().getRegion().getRect().width() );

        newView.setTag( media.getLast().getTag() );

        if ( true == bIsNew )
        {
            mDragLayer.addView ( itemView, new DragLayer.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, 0, 0));
        }
        else
        {
            mDragLayer.addView ( itemView, new DragLayer.LayoutParams ( LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT, media.getLast().getRegion().getRect().left, media.getLast().getRegion().getRect().top ) );
        }

        newView.setOnClickListener(viewClick);
        newView.setOnLongClickListener(viewLongClick);
        mDragLayer.invalidate();
	}

	public boolean startDrag(View v) {
	    findViewById(R.id.delete).setVisibility( View.VISIBLE );
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
		
		DropSpot deleteSpot = (DropSpot) mDragLayer.findViewById (R.id.deleteSpot);
		deleteSpot.setup (mDragLayer, dragController, R.color.deleteSpot);

		
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