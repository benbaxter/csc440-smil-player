/* 
 * Questions to decide upon
 * Do we worry about <seq> and other tags?
 */

package com.team1.Smil;

import java.io.*;
import java.util.*;

import com.team1.communication.cloud.Uploader;
import com.team1.composer.ComposerActivity;

import android.os.Environment;
import android.util.Log;
//import com.team1.composer.Media;
import android.widget.Toast;

public class SmilGenerator
{
    private String filePath = SmilConstants.ROOT_PATH;
    private String fileName = "test1.smil";
    private File file;
    
    public void generateSMILFile(List<SmilComponent> list, int mode)
    {
        boolean debug = true;
        figureOutFile();
        try {
            BufferedWriter br = new BufferedWriter( new FileWriter(file) );
            //sort by start time
            Collections.sort( list, START_TIME_ORDER);
            //define standards?
            //xmlns="http://www.w3.org/ns/smil" version="3.0"
            br.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<smil>\r\n<head>\r\n");
            //need to prepare for text attributes
//            <textStyle xml:id="HeadlineStyle" textFontFamily="serif" textFontSize="12px" 
//                textFontWeight="bold" textFontStyle="italic"          
//                textWrapOption="noWrap" textColor="blue" textBackgroundColor="white" />
//            </textStyling>
            br.write("<layout>\r\n<root-layout height=\"450\" width=\"300\" background-color=\"gray\" />\r\n");
            StringBuilder layout = new StringBuilder();
            StringBuilder par = new StringBuilder();
            for( SmilComponent item : list)
            {
                if(item.getType() != SmilConstants.COMPONENT_TYPE_AUDIO)
                {
                    layout.append("<region id=\"" + item.getTag() + "\" ");
                    layout.append( "top=\"" + item.getRegion().getRect().top + "\" left=\"" + 
                            item.getRegion().getRect().left + "\" " );
                    layout.append( "width=\"" + item.getRegion().getRect().width() + "\" height=\"" +
                            item.getRegion().getRect().height() );
                    layout.append("\" background-color=\"black\" />\r\n" );
                }
                if(item.getType() == SmilConstants.COMPONENT_TYPE_AUDIO)
                    par.append("<audio ");
                else if(item.getType() == SmilConstants.COMPONENT_TYPE_IMAGE)
                    par.append("<img ");
                else if(item.getType() == SmilConstants.COMPONENT_TYPE_TEXT)
                    par.append("<text ");//look into textstream...
                else
                    par.append("<video ");
                //need to look at how/where the src is
                if(item.getType() == SmilConstants.COMPONENT_TYPE_TEXT)
                    par.append("src=\"data:," + item.getText() + "\" ");
                else{
                    if( mode == SmilConstants.MODE_DRAFT)
                        par.append("src=\"" + item.getFilePath() + "\" ");
                    else if( mode == SmilConstants.MODE_SEND){
                        File file = new File(item.getFilePath());
                        item.setFilePath( file.getCanonicalPath() );
                        item.setFileName( file.getName() );
                        par.append("src=\"" + file.getName() + "\" ");
                        Log.i("GENTEST", file.getAbsolutePath());
                        
                        String key = Uploader.uploadGetKey( file );
                        
                        if(key != null)
                        {
                            item.setTitle( key );
                            par.append("title=\"" + item.getTitle() + "\" ");
                        }
                        Log.i("GENTEST", key+"");
                    }
                }   
                
                if(item.getType() != SmilConstants.COMPONENT_TYPE_AUDIO)
                    par.append("region=\"" + item.getTag() + "\" ");
                
                par.append("begin=\"" + item.getBegin() + "\" ");
                par.append("end=\"" + (item.getEnd()+item.getBegin()) + "\" ");
                par.append( " />\r\n" );
            }
            br.write( layout.toString() );
            br.write("</layout>\r\n</head>\r\n<body>\r\n<par>\r\n");
            br.write(par.toString());
            br.write("</par>\r\n</body>\r\n</smil>");
            br.close();
            if(debug)
            {
                Log.i("DEBUG SMIL GEN", "About to read produced SMIL");
                BufferedReader brDebug = new BufferedReader( new FileReader(file) );
                String line = "";
                while((line=brDebug.readLine()) != null)
                    Log.i("DEBUG SMIL GEN", line);
                brDebug.close();
            }
        } catch (Exception e) {
            Log.i("FILE", "File creation failed.");
        }
        
        
    }
    
    private void figureOutFile ( )
    {
        File pathDir = Environment.getExternalStorageDirectory ( );
        File appDir = new File ( pathDir, filePath );
        appDir.mkdirs ( );
        file = new File ( appDir, fileName );
        Log.i("GENERATOR", appDir.toString());
        Log.i("GENERATOR", fileName);
    }
    
    private File copyFile(String originalStr, String fileName)
    {
        File pathDir = Environment.getExternalStorageDirectory ( );
        File appDir = new File ( pathDir, SmilConstants.MEDIA_PATH );
        appDir.mkdirs ( );
        String[] fileArray = fileName.split("\\.");
        String tempFileName = fileArray[0]+"_"+System.currentTimeMillis()+"."+fileArray[1];
        File copy = new File ( appDir, tempFileName );
        File original = new File( originalStr );
        try{
            
            InputStream in = new FileInputStream(original);
            OutputStream out = new FileOutputStream(copy);
    
            byte[] buf = new byte[1024];
            int len;
            
            while ((len = in.read(buf)) > 0){
              out.write(buf, 0, len);
            }
            
            in.close();
            out.close();
            
        } catch(FileNotFoundException ex) {
            System.exit(0);
        } catch(IOException e) { 
        }
        return copy;
    }
    
    final Comparator<SmilComponent> START_TIME_ORDER =
        new Comparator<SmilComponent>() {
        public int compare(SmilComponent m1, SmilComponent m2) {
            return m1.getBegin() - m2.getBegin();
        }
    };
    
    public void setFileName ( String fileName )
    {
        this.fileName = fileName;
    }

    public void setFilePath ( String filePath )
    {
        this.filePath = filePath;
    }
}