package smil.mobile.activity;

import java.io.Serializable;


import smil.mobile.activity.SmilConstants;


public class SMILComponent implements Serializable
{
    private static final long serialVersionUID = 1L;

    private int type;
    private int begin;
    private int duration;


    public int getType()
    {
        return type;
    }

    protected void setType(int type)
    {
        this.type = type;
    }

    public int getBegin()
    {
        return begin;
    }

    public void setBegin(int begin)
    {
        this.begin = begin;
    }

    public int getDuration()
    {
        return duration;
    }

    public void setDuration(int duration)
    {
        this.duration = duration;
    }

    @Override
    public String toString()
    {
        switch (type)
        {
            case SmilConstants.COMPONENT_TYPE_TEXT:
                return "Text Component";
            default:
                return "Component";
        }
    }
}
