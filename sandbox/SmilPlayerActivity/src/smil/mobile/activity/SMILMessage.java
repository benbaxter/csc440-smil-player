package smil.mobile.activity;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;


import org.xmlpull.v1.XmlSerializer;


import android.util.Xml;


import smil.mobile.activity.SmilConstants;


public class SMILMessage implements Serializable
{
    private static final long serialVersionUID = 1L;
    private List<SMILComponent> componentList;
    private String subject;

    public static SMILMessage create()
    {
        SMILMessage message = new SMILMessage();
        message.setComponentList(new ArrayList<SMILComponent>());
        return message;
    }

    private SMILMessage()
    {

    }

    public List<SMILComponent> getComponentList()
    {
        return componentList;
    }

    public void setComponentList(List<SMILComponent> componentList)
    {
        this.componentList = componentList;
    }

    public void addComponent(SMILComponent component)
    {
        componentList.add(component);
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getXml() throws SMILReadException
    {
        try
        {
            XmlSerializer serializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();

            serializer.setOutput(writer);

            // set indentation option
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            serializer.startDocument("UTF-8", true);
            serializer.startTag(null, "smil");
            serializer.startTag(null, "body");
            serializer.startTag(null, "par");

            for (SMILComponent component : componentList)
            {
                switch (component.getType())
                {
                    case SmilConstants.COMPONENT_TYPE_TEXT:
                    {
                        SMILTextComponent textComponent = (SMILTextComponent) component;
                        serializer.startTag(null, "text");
                        serializer.attribute(null, "dur", "" + textComponent.getDuration());
                        serializer.attribute(null, "begin", "" + textComponent.getBegin());
                        serializer.text(textComponent.getText());
                        serializer.endTag(null, "text");
                        break;
                    }
                    case SmilConstants.COMPONENT_TYPE_IMAGE:
                    {
                        SMILImageComponent imageComponent = (SMILImageComponent) component;
                        serializer.startTag(null, "img");
                        serializer.attribute(null, "dur", "" + imageComponent.getDuration());
                        serializer.attribute(null, "begin", "" + imageComponent.getBegin());
                        serializer.attribute(null, "src", imageComponent.getSrc());
                        serializer.endTag(null, "img");
                        break;
                    }
                    case SmilConstants.COMPONENT_TYPE_AUDIO:
                    {
                        SMILAudioComponent audioComponent = (SMILAudioComponent) component;
                        serializer.startTag(null, "audio");
                        serializer.attribute(null, "dur", "" + audioComponent.getDuration());
                        serializer.attribute(null, "begin", "" + audioComponent.getBegin());
                        serializer.attribute(null, "src", audioComponent.getSrc());
                        serializer.endTag(null, "audio");
                        break;
                    }
                    case SmilConstants.COMPONENT_TYPE_VIDEO:
                    {
                        SMILVideoComponent videoComponent = (SMILVideoComponent) component;
                        serializer.startTag(null, "video");
                        serializer.attribute(null, "dur", "" + videoComponent.getDuration());
                        serializer.attribute(null, "begin", "" + videoComponent.getBegin());
                        serializer.attribute(null, "src", videoComponent.getSrc());
                        serializer.endTag(null, "video");
                        break;
                    }
                }
            }

            serializer.endTag(null, "par");
            serializer.endTag(null, "body");
            serializer.endTag(null, "smil");

            serializer.endDocument();
            serializer.flush();

            return writer.toString();
        }
        catch (Exception e)
        {
            if (e instanceof IllegalArgumentException)
                throw new SMILReadException("IllegalArgumentException occured while writing the XML message.", e);
            else if (e instanceof IllegalStateException)
                throw new SMILReadException("IllegalStateException occured while writing the XML message.", e);
            else if (e instanceof IOException)
                throw new SMILReadException("IOException occured while writing the XML message.", e);
            else
                throw new SMILReadException("An error occured while writing the XML message.", e);
        }
    }

    @Override
    public String toString()
    {
        return subject;
    }
}
