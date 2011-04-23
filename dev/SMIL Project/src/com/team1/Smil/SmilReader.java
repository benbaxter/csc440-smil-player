package com.team1.Smil;

import java.io.FileReader;
import java.util.HashMap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


import android.os.Environment;
import android.util.Log;


public class SmilReader extends DefaultHandler
{    
    private SmilMessage parsedMessage;
    private SmilComponent parsedComponent;
    private HashMap<String, SmilRegion> parsedRegionMap = new HashMap<String, SmilRegion>();

    private static final int DEFAULT_PAR_BEGIN = 0;
    private static final int DEFAULT_PAR_END = 0;
    
    private boolean mInSmilTag = false;
    private boolean mInHeadTag = false;
    private boolean mEndHeadTag = false;
    private boolean mInLayoutTag = false;
    private boolean mInBodyTag = false;
    private boolean mInParTag = false;
    private boolean mInTextTag = false;
    private boolean mInImageTag = false;
    private boolean mInAudioTag = false;
    private boolean mInVideoTag = false;
    
    private int mParBeginTime = DEFAULT_PAR_BEGIN;
    private int mParEndTime = DEFAULT_PAR_END;
    

    public SmilReader()
    {
        super();
    }


    public static SmilMessage parseMessage ( String fileName ) throws Exception
    {
        //try
        {
    		String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath();
    		
    		if ( fileName.startsWith ( rootDir ) ) 
    		{
    			fileName = fileName.replace ( rootDir, "" );
    		}
    		
    		if ( fileName.endsWith ( ".smil" ) ) 
    		{
    			fileName = fileName.replace ( ".smil", "" );
    		}
    		
    		Log.i("FILENAME FOR PARSING", rootDir + fileName + ".smil");
    		FileReader f = new FileReader ( rootDir + fileName + ".smil" );

            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            SmilReader parser = new SmilReader();
            xr.setContentHandler(parser);
            xr.setErrorHandler(parser);

            xr.parse(new InputSource(f));

            SmilMessage message = parser.getMessage();

            return message;
        }
    }

    @Override
    public void startDocument() throws SAXException
    {
        parsedMessage = new SmilMessage ( );
    }

    @Override
    public void endDocument() throws SAXException 
    {
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException 
    {
        boolean foundProblem = false;
        String errorMessage = "<" + localName + "> tag cannot be parsed.";
        
        try 
        {
            if ( localName.equals ( "smil" ) ) 
            {
                mInSmilTag = true;
            } 
            else if ( localName.equals ( "head" ) ) 
            {
                mInHeadTag = true;
            } 
            else if ( localName.equals ( "body" ) ) 
            {
                mInBodyTag = true;
            } 
            else if ( mInSmilTag )
            {
                if ( mInHeadTag )
                {
                    if ( localName.equals ( "layout" ) ) 
                    {
                        mInLayoutTag = true;
                    }
                    else if ( mInLayoutTag )
                    {
                        if ( localName.equals ( "region" ) )
                        {
                            // region must have an id
                            String id = atts.getValue("id");
                         
                            // set default value of attributes
                            int left = 0;
                            int top = 0;
                            int height = parsedMessage.getCanvasHeight();
                            int width = parsedMessage.getCanvasWidth();
                            String backgroundColor = parsedMessage.getBackgroundColorString ( );
                         
                            // get any user-defined values for optional attributes
                            if ( atts.getValue ( "left" ) != null && !atts.getValue("left").equalsIgnoreCase("auto"))
                            {
                                left = Integer.parseInt(atts.getValue("left"));
                            }
                            
                            if ( atts.getValue ( "top" ) != null && !atts.getValue("top").equalsIgnoreCase("auto"))
                            {
                                top = Integer.parseInt(atts.getValue("top"));
                            }
                            
                            if ( atts.getValue ( "height" ) != null && !atts.getValue("height").equalsIgnoreCase("auto"))
                            {
                                height = Integer.parseInt(atts.getValue("height"));
                            }
                            
                            if ( atts.getValue ( "width" ) != null && !atts.getValue("width").equalsIgnoreCase("auto"))
                            {
                                width = Integer.parseInt(atts.getValue("width"));
                            }
                            
                            if ( atts.getValue ( "background-color" ) != null)
                            {
                                backgroundColor = atts.getValue("background-color");
                            }
                         
                            // construct SmilRegion object
                            SmilRegion smilRegion = new SmilRegion(id, backgroundColor, left, top, width, height);
                            parsedRegionMap.put(smilRegion.getId(), smilRegion);
                        } 
                        else if ( localName.equals ( "root-layout" ) )
                        {
                            if ( atts.getValue ( "background-color" ) != null )
                            {
                                parsedMessage.setBackgroundColor(atts.getValue("background-color"));
                            }
                        }
                        else
                        {
                            foundProblem = true;
                        }
                    }
                    else
                    {
                        foundProblem = true;
                    }
                }
                else if ( mInBodyTag && mEndHeadTag )
                {
                    if ( localName.equals ( "par" ) ) 
                    {
                        mInParTag = true;
                        if ( atts.getValue ( "begin" ) != null )
                        {
                            mParBeginTime = Integer.valueOf ( atts.getValue ( "begin" ) );
                        }
                     
                        if ( atts.getValue ( "end" ) != null )
                        {
                            mParEndTime = Integer.valueOf ( atts.getValue ( "end" ) );
                        }
                    }
                    else if ( mInParTag )
                    { // new resource to construct
                        String regionId = atts.getValue ( "region" );
                        String source = atts.getValue ( "src" );
                        
                        if( !localName.equals( "text" ) && source != null && !source.contains( "/" ))
                        {
                            source = Environment.getExternalStorageDirectory() + SmilConstants.MEDIA_PATH + source;
                        }
                        
                        if ( localName.equals ( "text" ) ) 
                        {
                            Log.i("SOURCE IS", source);
                            parsedComponent = new SmilTextComponent ( source, parsedRegionMap.get(regionId), mParBeginTime, mParEndTime );
                            parsedComponent.setType ( SmilConstants.COMPONENT_TYPE_TEXT );
                            mInTextTag = true;
                        }
                        
                        else if ( localName.equals ( "img" ) ) 
                        {
                            Log.i("imgSource-Reader", source);
                            parsedComponent = new SmilImageComponent ( source, parsedRegionMap.get(regionId), mParBeginTime, mParEndTime );
                            parsedComponent.setType ( SmilConstants.COMPONENT_TYPE_IMAGE );
                            mInImageTag = true;
                        } 
                        else if ( localName.equals ( "audio" ) ) 
                        {
                            parsedComponent = new SmilAudioComponent ( source, parsedRegionMap.get(regionId), mParBeginTime, mParEndTime );
                            parsedComponent.setType ( SmilConstants.COMPONENT_TYPE_AUDIO );
                            mInAudioTag = true;
                        } 
                        else if ( localName.equals ( "video" ) ) 
                        {
                            parsedComponent = new SmilVideoComponent ( source, parsedRegionMap.get(regionId), mParBeginTime, mParEndTime );
                            parsedComponent.setType ( SmilConstants.COMPONENT_TYPE_VIDEO );
                            mInVideoTag = true;
                        }
                        
                        String title = atts.getValue ( "title" );
                        if ( title != null )
                        {
                            parsedComponent.setTitle( title );
                        }
                     
                        if ( mInTextTag || mInImageTag || mInAudioTag || mInVideoTag )
                        {
                            if ( atts.getValue ( "begin" ) != null )
                            {
                                parsedComponent.setBegin ( mParBeginTime + Integer.parseInt(atts.getValue("begin")));
                            }
                            
                            if ( atts.getValue ( "end" ) != null )
                            {
                                parsedComponent.setEnd ( mParBeginTime + Integer.parseInt ( atts.getValue ( "end" ) ) );
                            }
                        }
                        else
                        {
                            foundProblem = true;
                        }
                    }
                    else
                    {
                        foundProblem = true;
                    }
                }
                else
                {
                    foundProblem = true;
                }
            }
            else
            {
                foundProblem = true;
            }
         
            if ( foundProblem )
            {
                throw new SAXParseException ( errorMessage, null );
            }
        }
        catch ( Exception e ) 
        {
            throw new SAXParseException ( errorMessage, null, e );
        }        
    }

    @Override
    public void endElement ( String namespaceURI, String localName, String qName ) throws SAXException 
    {
         if ( localName.equals ( "smil" ) ) 
         {
             mInSmilTag = false;
         } 
         else if ( localName.equals ( "head" ) ) 
         {
              mInHeadTag = false;
              mEndHeadTag = true;
         } 
         else if ( localName.equals ( "layout" ) ) 
         {
              mInLayoutTag = false;
         } 
         else if ( localName.equals ( "body" ) ) 
         {
              mInBodyTag = false;
         } 
         else if ( localName.equals ( "par" ) ) 
         {
              mInParTag = false;
              mParBeginTime = DEFAULT_PAR_BEGIN;
              mParEndTime = DEFAULT_PAR_END;
         } 
         else if ( ( localName.equals ( "text"  ) ) || 
                   ( localName.equals ( "img"   ) ) || 
                   ( localName.equals ( "audio" ) ) || 
                   ( localName.equals ( "video" ) ) ) 
         {
             parsedMessage.addComponent ( parsedComponent );
         }
    }

    public SmilMessage getMessage()
    {
        return parsedMessage;
    }

}
