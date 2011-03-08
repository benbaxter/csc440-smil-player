package smil.mobile.activity;

import java.io.Serializable;


import smil.mobile.activity.SmilConstants;


public class SMILVideoComponent extends SMILComponent implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int xPos;
    private int yPos;
    private String src;


    public static SMILVideoComponent create()
    {
        SMILVideoComponent comp = new SMILVideoComponent(SmilConstants.COMPONENT_TYPE_VIDEO);
        return comp;
    }

    private SMILVideoComponent(int type)
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

    public String getSrc()
    {
        return src;
    }

    public void setSrc(String src)
    {
        this.src = src;
    }
}
