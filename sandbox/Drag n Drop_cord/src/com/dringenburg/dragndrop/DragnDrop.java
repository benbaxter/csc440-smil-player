package com.dringenburg.dragndrop;

import android.app.Activity; 
import android.os.Bundle; 
import android.view.MotionEvent; 
import android.view.View; 
import android.view.View.OnTouchListener; 
import android.widget.FrameLayout; 
import android.widget.FrameLayout.LayoutParams; 

public class DragnDrop extends Activity 
{ 
	//public FrameLayout board; 
	public View pawn; 

	@Override 
	public void onCreate(Bundle savedInstanceState) 
	{ 
		super.onCreate(savedInstanceState); 

		//board = new FrameLayout(this); 
		pawn = new View(this); 

		setContentView(R.layout.main); 

		//board = (FrameLayout)findViewById(R.id.Board); 
		pawn = findViewById(R.id.Pawn); 
		pawn.setOnTouchListener(dragt);

	}//onCreate 

	OnTouchListener dragt = new OnTouchListener() 
	{ 
		@Override 
		public boolean onTouch(View v, MotionEvent event) 
		{ 
			FrameLayout.LayoutParams par = (LayoutParams) v.getLayoutParams(); 
			switch(v.getId()) 
			{//What is being touched 
				case R.id.Pawn: 
				{//Which action is being taken 
					switch(event.getAction()) 
					{ 
						case MotionEvent.ACTION_MOVE: 
						{ 	
							par.topMargin = (int)event.getRawY() - (v.getHeight()); 
							par.leftMargin = (int)event.getRawX() - (v.getWidth()/2); 
							v.setLayoutParams(par); 
							break; 
						}//inner case MOVE 
						case MotionEvent.ACTION_UP: 
						{ 
							par.height = 40; 
							par.width = 40; 
							par.topMargin = (int)event.getRawY() - (v.getHeight()); 
							par.leftMargin = (int)event.getRawX() - (v.getWidth()/2); 
							v.setLayoutParams(par); 
							break; 
						}//inner case UP 
						case MotionEvent.ACTION_DOWN: 
						{ 
							par.height = 60; 
							par.width = 60; 
							v.setLayoutParams(par); 
							break; 
						}//inner case UP 
					}//inner switch 
					break; 
				}//case pawn 
			}//switch 
			return true; 
		}//onTouch 
	};//dragt 
} 