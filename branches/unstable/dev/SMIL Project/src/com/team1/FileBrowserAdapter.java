package com.team1;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FileBrowserAdapter extends ArrayAdapter<String> 
{
	private Activity mContext;
	private ArrayList<String> mItems;
	
	public FileBrowserAdapter ( Activity context, int textViewResourceId, ArrayList<String> items ) 
	{
        super ( context, textViewResourceId, items );
        this.mContext = context;
        this.mItems = items;
	}
	
	@Override
    public View getView ( int position, View convertView, ViewGroup parent ) 
	{
		View view = convertView;
		if ( view == null ) 
		{
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate ( android.R.layout.simple_list_item_1, null );
        }
		
		TextView text = (TextView)view.findViewById ( android.R.id.text1 );
		text.setText ( mItems.get ( position ) );
		
		return view;
	}
}
