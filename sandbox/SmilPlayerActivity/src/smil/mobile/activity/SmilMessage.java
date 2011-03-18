package smil.mobile.activity;

import java.io.Serializable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;

public class SmilMessage implements Serializable
{
    private String subject;

    private static final long serialVersionUID = 1L;
    private ArrayList<SmilComponent> resourcesByBeginTime;
    private ArrayList<SmilComponent> resourcesByEndTime;
    private Integer canvasWidth;
    private Integer canvasHeight;
    private String backgroundColorString;
    private String fileName;
    private int length = 0;
    private Paint backgroundColor;
    private static final String DEFAULT_BACKGROUND_COLOR = "#FFFFFF";
    private static final int DEFAULT_CANVAS_HEIGHT = 455;
    private static final int DEFAULT_CANVAS_WIDTH = 320;
    
    
    public SmilMessage ( ) 
    {
        resourcesByBeginTime = new ArrayList<SmilComponent>();
        resourcesByEndTime = new ArrayList<SmilComponent>();
        canvasWidth = DEFAULT_CANVAS_WIDTH;
        canvasHeight = DEFAULT_CANVAS_HEIGHT;
        backgroundColorString = DEFAULT_BACKGROUND_COLOR;
        backgroundColor = new Paint ( );
        backgroundColor.setColor ( Color.parseColor ( ( backgroundColorString ) ) );
    }
 
    public Integer getCanvasWidth ( ) 
    {
        return canvasWidth;
    }
    
    public Integer getCanvasHeight ( ) 
    {
        return canvasHeight;
    }
    
    public Paint getBackgroundColor ( ) 
    {
        return backgroundColor;
    }
    
    public String getBackgroundColorString ( ) 
    {
        return backgroundColorString;
    }
    
    public void setBackgroundColor ( String colorString ) 
    {
        backgroundColorString = colorString;
        backgroundColor.setColor ( Color.parseColor ( ( colorString ) ) );
    }

    public void addComponent ( SmilComponent component )
    {
        addResourceToBeginTimeList ( component );
        addResourceToEndTimeList ( component );
        updateLength ( );

    }
    
    private void addResourceToBeginTimeList ( SmilComponent component ) 
    {
        boolean added = false;
        if ( resourcesByBeginTime.size() == 0 ) 
        {
            resourcesByBeginTime.add ( component );
            added = true;
        }
        else
        {
            for ( int i = 0; !added && i < resourcesByBeginTime.size(); i++ )
            {
                if ( resourcesByBeginTime.get(i).getBegin ( ) > component.getBegin ( )
                    || ( resourcesByBeginTime.get(i).getBegin ( ) == component.getBegin ( )
                            && resourcesByBeginTime.get(i).getEnd() > component.getEnd()))
                { 
                    resourcesByBeginTime.add ( i, component );
                    added = true;
                }
            }
            if ( !added )
            {
                resourcesByBeginTime.add ( component );
            }
        }
    }
    
    private void addResourceToEndTimeList ( SmilComponent component ) 
    {
        boolean added = false;
        if ( resourcesByEndTime.size() == 0 ) 
        {
            resourcesByEndTime.add ( component );
            added = true;
        }
        else
        {
            for ( int i = 0; !added && i < resourcesByEndTime.size(); i++ )
            {
                if ( resourcesByEndTime.get(i).getEnd() > component.getEnd())
                {
                    resourcesByEndTime.add ( i, component );
                    added = true;
                }
            }
            if ( !added )
            {
                resourcesByEndTime.add ( component );
            }
        }
    }
    
    public void removeResource ( SmilComponent component ) 
    {
        resourcesByBeginTime.remove ( component );
        updateLength ( );
    }
    
    private void updateLength ( ) 
    {
        int maxLength = 0;
        for ( SmilComponent component : resourcesByBeginTime )
        {
            if ( component.getEnd ( ) > maxLength )
            {
                maxLength = component.getEnd ( );
            }
        }
        length = maxLength;
    }

    public int nextIndexOfType ( int type, int index )
    {
        if ( index > -1 )
        {
            for ( int i = index; i < resourcesByBeginTime.size ( ); i++ )
            {
                if ( resourcesByBeginTime.get(i).getType() == type )
                {
                    return i;
                }
            }
        }
        
        return -1;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }
    
    public static void saveAsXML ( SmilMessage message ) throws Exception 
    {
        saveAsXML ( message, message.getFileName ( ) );
    }
    
    public static void saveAsXML ( SmilMessage message, String fileName ) throws Exception
    {
        String SDCardDir = Environment.getExternalStorageDirectory() + "/";
        
        if ( fileName.startsWith ( SDCardDir ) )
        {
            fileName = fileName.replaceFirst ( SDCardDir, "" );
        }
        
        if ( !fileName.endsWith ( ".smil" ) )
        {
            fileName += ".smil";
        }
        
        File root = Environment.getExternalStorageDirectory ( );
        File xmlFile = new File ( root, fileName );
        xmlFile.createNewFile ( );
        
        FileWriter fileWriter = new FileWriter ( xmlFile );
        BufferedWriter out = new BufferedWriter ( fileWriter );
        
        StringBuilder regionsXmlBuilder = new StringBuilder ( );
        StringBuilder parXmlBuilder = new StringBuilder ( );
        
        ArrayList<SmilComponent> resources = new ArrayList<SmilComponent> ( );
        resources.addAll ( message.getResourcesByBeginTime ( ) );
        
        // this loop builds a par tag and all resource tags within it
        // and all of the region tags
        for ( int i = 0; i < resources.size ( ); i++ )
        {
            if ( i == 0 ) // first resource
            {
                parXmlBuilder.append ( "<par>\n" );
            }
            
            regionsXmlBuilder.append ( parseRegionXml ( resources.get(i).getRegion ( ) ) );
            parXmlBuilder.append( parseResourceXml ( resources.get ( i ) ) );
            
            if ( i == resources.size ( ) - 1 ) // last resource
            {
                parXmlBuilder.append ( "</par>\n" );
            }
        }
        
        StringBuilder overallXmlBuilder = new StringBuilder ( );
        overallXmlBuilder.append ( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
        overallXmlBuilder.append ( "<smil>\n<head>\n<layout>\n" );
        overallXmlBuilder.append ( parseRootLayoutXml ( message ) );
        overallXmlBuilder.append ( regionsXmlBuilder.toString ( ) );
        overallXmlBuilder.append ( "</layout>\n</head>\n<body>\n" );
        overallXmlBuilder.append ( parXmlBuilder.toString ( ) );
        overallXmlBuilder.append ( "</body>\n</smil>" );
        
        out.write( overallXmlBuilder.toString ( ) );
        out.close ( );

    }
    
    private static String parseResourceXml ( SmilComponent component )
    {
        String xml = "<" + component.getType ( ) 
            + " src=\"" + component.getSource ( )
            + "\" region=\"" + component.getRegion().getId()
            + "\" begin=\"" + component.getBegin ( )
            + "\" end=\"" + component.getEnd ( )
            + "\" />\n"
        ;
        return xml;
    }
    
    private static String parseRegionXml ( SmilRegion region )
    {
        String xml = "<region id=\"" + region.getId ( )
            + "\" top=\"" + region.getRect().top
            + "\" left=\"" + region.getRect().left
            + "\" width=\"" + region.getRect().width ( )
            + "\" height=\"" + region.getRect().height ( )
            + "\" />\n"
        ;
        return xml;
    }
    
    private static String parseRootLayoutXml ( SmilMessage message )
    {
        String xml = "<root-layout height=\"" + message.getCanvasHeight ( ) 
            + "\" width=\"" + message.getCanvasWidth ( )
            + "\" background-color=\"" + message.getBackgroundColorString ( ) 
            + "\" />\n"
        ;
        return xml;
    }

    @Override
    public String toString()
    {
        return subject;
    }
    
    public int getLength ( )
    {
        return length;
    }
    
    public String getFileName ( ) 
    {
        return fileName;
    }
    
    public void setFileName ( String fileName ) 
    {
        this.fileName = fileName;
    }
    
    public ArrayList<SmilComponent> getResourcesByBeginTime ( ) 
    {
        return resourcesByBeginTime;
    }
    
    public ArrayList<SmilComponent> getResourcesByEndTime ( ) 
    {
        return resourcesByEndTime;
    }
}
