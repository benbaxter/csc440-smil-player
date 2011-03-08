package smil.mobile.activity;

import java.io.Serializable;


import smil.mobile.activity.SmilConstants;


public class SMILImageComponent extends SMILComponent implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int xPos;
    private int yPos;
    private String text;
    private String src;

    public static SMILImageComponent create()
    {
        SMILImageComponent comp = new SMILImageComponent(SmilConstants.COMPONENT_TYPE_IMAGE);
        return comp;
    }

    private SMILImageComponent(int type)
    {
        setType(type);
    }

    public int getxPos()
    {
        return xPos;
    }

    public void setxPos(int xPos)
    {
        this.xPos = xPos;
    }

    public int getyPos()
    {
        return yPos;
    }

    public void setyPos(int yPos)
    {
        this.yPos = yPos;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getSrc()
    {
        return src;
    }

    public void setSrc(String src)
    {
        this.src = src;
    }
}
