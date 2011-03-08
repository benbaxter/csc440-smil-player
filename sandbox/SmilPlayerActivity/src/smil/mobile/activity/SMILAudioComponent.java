package smil.mobile.activity;

import java.io.Serializable;


import smil.mobile.activity.SmilConstants;


public class SMILAudioComponent extends SMILComponent implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String src;


    public static SMILAudioComponent create()
    {
        SMILAudioComponent comp = new SMILAudioComponent(SmilConstants.COMPONENT_TYPE_AUDIO);
        return comp;
    }


    private SMILAudioComponent(int type)
    {
        setType(type);
    }

    public String getSrc()
    {
        return src;
    }

    public void setSrc(String src)
    {
        this.src = src;
    }

    @Override
    public String toString()
    {
        return "Audio File - " + src;
    }
}
