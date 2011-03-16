package com.team1.composer;

import com.team1.composer.R;
import com.team1.composer.filechooser.FileArrayAdapter;
import com.team1.composer.filechooser.Option;

import java.io.File;
import java.util.*;
import android.app.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class FileChooserListActivity extends ListActivity {
    
    FileArrayAdapter adapter;
    
    private File currentDir;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        currentDir = new File("/sdcard/");
        fill(currentDir);

    }
    
    private void fill(File f)
    {
        File[]dirs = f.listFiles();
         this.setTitle("Current Dir: "+f.getName());
         List<Option>dir = new ArrayList<Option>();
         List<Option>fls = new ArrayList<Option>();
         try{
             for(File ff: dirs)
             {
                if(ff.isDirectory())
                    dir.add(new Option(ff.getName(),"Folder",ff.getAbsolutePath()));
                else
                {
                    fls.add(new Option(ff.getName(),"File Size: "+ff.length(),ff.getAbsolutePath()));
                }
             }
         }catch(Exception e)
         {
         }
         Collections.sort(dir);
         Collections.sort(fls);
         dir.addAll(fls);
         if(!f.getName().equalsIgnoreCase("sdcard"))
             dir.add(0,new Option("..","Parent Directory",f.getParent()));
 
         adapter = new FileArrayAdapter(FileChooserListActivity.this,R.layout.file_chooser,dir);
         this.setListAdapter(adapter);
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Option o = adapter.getItem(position);
        if(o.getData().equalsIgnoreCase("folder")||o.getData().equalsIgnoreCase("parent directory")){
                currentDir = new File(o.getPath());
                fill(currentDir);
        }
        else
        {
            onFileClick( o );
        }
    }
    
    private void onFileClick(Option o)
    {
        Toast.makeText(this, "File Clicked: "+o.getName(), Toast.LENGTH_SHORT).show();
    }

}

