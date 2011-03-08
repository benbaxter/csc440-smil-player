package smil.mobile.activity;

import java.io.FileReader;
import java.io.IOException;
//import java.io.StringReader;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.os.Environment;


public class SMILReader extends DefaultHandler
{
    private SMILMessage currentMessage;
//    private SMILComponent currentComponent;
//    private boolean readyForText;


    public SMILReader()
    {
        super();
    }


    public static SMILMessage parseMessage(String fileName) throws SMILReadException
    {
        try
        {
    		String rootDir = Environment.getExternalStorageDirectory().toString() + "/";
    		
    		if(fileName.startsWith(rootDir)) {
    			fileName = fileName.replace(rootDir, "");
    		}
    		if(fileName.endsWith(".smil")) {
    			fileName = fileName.replace(".smil", "");
    		}
    		
    		FileReader f = new FileReader(rootDir + fileName + ".smil");

            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            SMILReader parser = new SMILReader();
            xr.setContentHandler(parser);
            xr.setErrorHandler(parser);

            xr.parse(new InputSource(f));

            SMILMessage message = parser.getMessage();

            return message;
        }
        catch (Exception e)
        {
            if (e instanceof SAXException)
                throw new SMILReadException("SAXException occured while parsing the XML message.", e);
            else if (e instanceof IOException)
                throw new SMILReadException("IOException occured while parsing the XML message.", e);
            else if (e instanceof ParserConfigurationException)
                throw new SMILReadException("ParserConfigurationException occured while parsing the XML message.", e);
            else
                throw new SMILReadException("An error occured while parsing the XML message.", e);
        }
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#startDocument()
     */
    @Override
    public void startDocument() throws SAXException
    {
        currentMessage = SMILMessage.create();
    }

    @Override
    public void startElement(String uri, String name, String qName, Attributes atts)
    {
        //readyForText = false;

        if ("text".equalsIgnoreCase(name))
        {
        	SMILTextComponent component = SMILTextComponent.create();

            component.setBegin(0);
            if ( atts.getValue ( "begin" ) != null )
            {
            	component.setBegin(Integer.parseInt(atts.getValue("begin")));
            }
            
            component.setDuration(0);
            if ( atts.getValue ( "dur" ) != null )
            {
            	component.setDuration(Integer.parseInt(atts.getValue("dur")));
            }
            
            if( atts.getValue ( "src" ) != null )
            {
            	component.getTextFromSource ( SmilConstants.ROOT_PATH + atts.getValue( "src" ) );
            }
            
            //currentComponent = component;
            currentMessage.addComponent ( component ); //(currentComponent);
            //readyForText = true;
        }
        else if ("audio".equalsIgnoreCase(name))
        {
            SMILAudioComponent component = SMILAudioComponent.create();

            component.setBegin(0);
            if ( atts.getValue ( "begin" ) != null )
            {
            	component.setBegin(Integer.parseInt(atts.getValue("begin")));
            }
            
            component.setDuration(0);
            if ( atts.getValue ( "dur" ) != null )
            {
            	component.setDuration(Integer.parseInt(atts.getValue("dur")));
            }
            
            if( atts.getValue ( "src" ) != null )
            {
            	component.setSrc ( SmilConstants.ROOT_PATH + atts.getValue( "src" ) );
            }
            
            //currentComponent = component;
            currentMessage.addComponent ( component ); //(currentComponent);
        }
        else if ("img".equalsIgnoreCase(name))
        {
            SMILImageComponent component = SMILImageComponent.create();

            component.setBegin(0);
            if ( atts.getValue ( "begin" ) != null )
            {
            	component.setBegin(Integer.parseInt(atts.getValue("begin")));
            }
            
            component.setDuration(0);
            if ( atts.getValue ( "dur" ) != null )
            {
            	component.setDuration(Integer.parseInt(atts.getValue("dur")));
            }
            
            if( atts.getValue ( "src" ) != null )
            {
            	component.setSrc ( SmilConstants.ROOT_PATH + atts.getValue( "src" ) );
            }

            //currentComponent = component;
            currentMessage.addComponent ( component ); //(currentComponent);
        }
        else if ("video".equalsIgnoreCase(name))
        {
            SMILVideoComponent component = SMILVideoComponent.create();

            component.setBegin(0);
            if ( atts.getValue ( "begin" ) != null )
            {
            	component.setBegin(Integer.parseInt(atts.getValue("begin")));
            }
            
            component.setDuration(0);
            if ( atts.getValue ( "dur" ) != null )
            {
            	component.setDuration(Integer.parseInt(atts.getValue("dur")));
            }
            
            if( atts.getValue ( "src" ) != null )
            {
            	component.setSrc ( SmilConstants.ROOT_PATH + atts.getValue( "src" ) );
            }

            //currentComponent = component;
            currentMessage.addComponent ( component ); //(currentComponent);
        }
    }

/******
    @Override
    public void characters(char ch[], int start, int length)
    {
        // only build buffers and read the characters if we have to
        if (readyForText && currentComponent instanceof SMILTextComponent)
        {
            StringBuffer buffer = new StringBuffer();

            for (int i = start; i < start + length; i++)
                buffer.append(ch[i]);

            readyForText = false;
            SMILTextComponent textComponent = (SMILTextComponent) currentComponent;
            textComponent.setText(buffer.toString());
        }

    }
******/

    public SMILMessage getMessage()
    {
        return currentMessage;
    }

}
