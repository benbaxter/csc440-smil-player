/*
 * Questions to decide upon
 * Do we worry about <seq> and other tags?
 */

package com.team1.composer.generator;

import java.io.*;
import java.util.*;

import android.os.Environment;
import android.util.Log;
//import com.team1.composer.Media;
import com.team1.player.SmilComponent;
import com.team1.player.SmilConstants;

public class SMILGenerator
{
    private static File file;
    
    public static void generateSMILFile(List<SmilComponent> list)
    {
        figureOutFile();
        try {
            BufferedWriter br = new BufferedWriter( new FileWriter(file) );
            //sort by start time
            Collections.sort( list, START_TIME_ORDER);
            //define standards?
            //xmlns="http://www.w3.org/ns/smil" version="3.0"
            br.write("<smil>\n<head>\n");
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
                layout.append("<region id=\"" + item.getTag() + "\" background-color=\"gray\" ");
                layout.append( "top=\"" + item.getRegion().getRect().left + "px\" left=\"" + 
                        item.getRegion().getRect().top + "px\" " );
                layout.append( "width=\"" + item.getRegion().getRect().width() + "\" height=\"" +
                        item.getRegion().getRect().height() + "\" fit=\"meet\" />\n" );
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
                
                par.append("region=\"" + item.getTag() + "\" ");
                par.append("dur=\"" + item.getEnd() + "s\" ");
                par.append("begin=\"" + item.getBegin() + "s\" ");
                
                par.append( " />\n" );
            }
            br.write( layout.toString() );
            br.write("</layout>\n</head>\n<body>\n<par>");
            br.write(par.toString());
            br.write("</par>\n</body>\n</smil>\n");
            br.close();
        } catch (Exception e) {
            Log.i("FILE", "we fucked up the file");
        }
        
        
    }
    
    private static void figureOutFile()
    {
        File pathDir = Environment.getExternalStorageDirectory();
        File appDir = new File(pathDir, "/Android/data/com.team1.composer.generator/files/");
        appDir.mkdirs();
        file = new File(appDir, "test.smil");
    }
    static final Comparator<SmilComponent> START_TIME_ORDER =
        new Comparator<SmilComponent>() {
        public int compare(SmilComponent m1, SmilComponent m2) {
            return m1.getBegin() - m2.getBegin();
        }
    };
}
