/* 
 * Questions to decide upon
 * Do we worry about <seq> and other tags?
 */

package com.team1.Smil;

import java.io.*;
import java.util.*;

import android.os.Environment;
import android.util.Log;
//import com.team1.composer.Media;

public class SmilGenerator
{
    private static File file;
    
    public static void generateSMILFile(List<SmilComponent> list)
    {
        boolean debug = true;
        figureOutFile();
        try {
            BufferedWriter br = new BufferedWriter( new FileWriter(file) );
            //sort by start time
            Collections.sort( list, START_TIME_ORDER);
            //define standards?
            //xmlns="http://www.w3.org/ns/smil" version="3.0"
            br.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<smil>\n<head>\n");
            //need to prepare for text attributes
//            <textStyle xml:id="HeadlineStyle" textFontFamily="serif" textFontSize="12px" 
//                textFontWeight="bold" textFontStyle="italic"          
//                textWrapOption="noWrap" textColor="blue" textBackgroundColor="white" />
//            </textStyling>
            br.write("<layout>\n<root-layout height=\"450\" width=\"300\" background-color=\"gray\" />\n");
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
                    layout.append("\" background-color=\"black\" />\n" );
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
                else
                    par.append("src=\"" + item.getFilePath() + "\" ");
                
                if(item.getType() != SmilConstants.COMPONENT_TYPE_AUDIO)
                    par.append("region=\"" + item.getTag() + "\" ");
                
                par.append("begin=\"" + item.getBegin() + "\" ");
                par.append("end=\"" + (item.getEnd()+item.getBegin()) + "\" ");
                par.append( " />\n" );
            }
            br.write( layout.toString() );
            br.write("</layout>\n</head>\n<body>\n<par>\n");
            br.write(par.toString());
            br.write("</par>\n</body>\n</smil>");
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
            Log.i("FILE", "we fucked up the file");
        }
        
        
    }
    
    private static void figureOutFile()
    {
        File pathDir = Environment.getExternalStorageDirectory();
        File appDir = new File(pathDir, "/Android/data/com.team1/files/");
        appDir.mkdirs();
        file = new File(appDir, "test1.smil");
    }
    static final Comparator<SmilComponent> START_TIME_ORDER =
        new Comparator<SmilComponent>() {
        public int compare(SmilComponent m1, SmilComponent m2) {
            return m1.getBegin() - m2.getBegin();
        }
    };
}
