package smil.mobile.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

import smil.mobile.activity.SmilConstants;


public class SMILTextComponent extends SMILComponent implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int xPos;
    private int yPos;
    private String text;

    public static SMILTextComponent create()
    {
        SMILTextComponent comp = new SMILTextComponent(SmilConstants.COMPONENT_TYPE_TEXT);
        return comp;
    }

    private SMILTextComponent(int type)
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

    @Override
    public String toString()
    {
        return "Text - " + text;
    }

	public void getTextFromSource ( String source )
	{
		try {
			BufferedReader br = new BufferedReader ( new FileReader ( new File ( source ) ) );
			this.text = "";
			String line = br.readLine();
			while(line != null){
				this.text += line;
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			this.text = "Source file not found.";
		} catch (IOException e) {
			e.printStackTrace();
			this.text = "Source file not found.";
		}
	}
}
