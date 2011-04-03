package com.team1.Smil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.util.Log;

public class SmilTextComponent extends SmilComponent
{    
    private int      xPos;
    private int      yPos;
    private String   text;
    private int      fontSize;
    private Paint    textColor;
    private String[] breakText;

    public SmilTextComponent ( ) 
    {
        super ( null, null, 0, 0 );
        this.setType ( SmilConstants.COMPONENT_TYPE_TEXT );
        this.fontSize = 25;
    }
    
    public SmilTextComponent ( String source, SmilRegion region, int begin, int end ) 
    {
        super ( source, region, begin, end );
        this.setType ( SmilConstants.COMPONENT_TYPE_TEXT );
        this.fontSize = 25;
        getTextFromSource ( );
        textColor = new Paint ( );
        updateText ( );
    }

    public void getTextFromSource ( )
    {
        try 
        {
            File sourceFile = new File ( super.getSource ( ) );
            if(sourceFile.exists())
            {
                Log.i("SOURCE FOR TEXT", "source is a file");
                BufferedReader br = new BufferedReader ( new FileReader ( sourceFile ) );
                this.text = "";
                String line = br.readLine ( );
                while ( line != null )
                {
                    this.text += line;
                    line = br.readLine ( );
                }
            }
            else
            {
                String source = super.getSource();
                Log.i("SOURCE FOR TEXT", "source is " + source);
                if(source.startsWith( "data:," ))
                {
                    Log.i("Souce for text", ""+text);
                    this.text = source.substring( 6 );
                    Log.i("Souce for text", ""+text);
                }
                else
                {
                    this.text = source;
                }
            }
        } 
        catch ( FileNotFoundException e ) 
        {
            e.printStackTrace ( );
            this.text = "Source file not found.";
        } 
        catch ( IOException e ) 
        {
            e.printStackTrace ( );
            this.text = "Source file not found.";
        }
    }

    private void prepareText ( )
    {
        Rect r = getRegion().getRect ( );
        if ( r.width ( ) > 0 && r.height ( ) > 0 )
        {
            textColor.setColor ( Color.parseColor ( "#000000" ) );
            xPos = r.centerX ( );
            int yTop = (int) ( r.top + textColor.getTextSize ( ) * textColor.getTextScaleX ( ) );
            yPos = (int) ( r.centerY ( ) - ( ( breakText.length/2) * textColor.getTextSize ( ) * textColor.getTextScaleX ( ) ) );
            if ( yPos < yTop )
            {
                yPos = yTop;
            }
        }
    }
    
    @Override 
    public void play ( Canvas canvas ) 
    {
        int y = yPos;
        
        canvas.save ( );
        canvas.clipRect ( getRegion().getRect ( ) );
        
        for ( int i = 0; i < breakText.length; i++ )
        {
            canvas.drawText ( breakText[i], xPos, y, textColor);
            y += textColor.getTextSize ( ) * textColor.getTextScaleX ( );
        }
        
        canvas.restore ( );
    }

    @Override 
    public void stop ( Canvas canvas ) 
    {
    }
    
    private String[] breakTextToRegion ( )
    {
        textColor.setTextAlign ( Align.CENTER );
        
        Rect r = super.getRegion().getRect ( );
        
        if ( r.width() > 0 && r.height() > 0 )
        {
            int end = textColor.breakText ( this.text, true, r.width ( ), null );
            int cutOff;
            String toBreak = this.text;
            String returnStr = "";
            String line;
            
            while ( end > 0 && end <= toBreak.length() )
            {
                line = toBreak.substring ( 0, end );
                cutOff = line.lastIndexOf ( ' ' );
                //if ( cutOff != -1 && end < toBreak.length() )
                //{
                //    end = cutOff;
                //    line = line.substring ( 0, cutOff );
                //    toBreak = toBreak.substring ( end + 1 );
                //}
                //else
                {
                    toBreak = toBreak.substring ( end );
                }
                
                returnStr += line + "\n";
                end = textColor.breakText ( toBreak, true, r.width(), null );
            }
            
            return returnStr.split ( "\n" );
        }
        
        return new String[]{""};
    }

    public void updateText ( )
    {
        breakText = breakTextToRegion ( );
        prepareText ( );
    }

    @Override public String getText ( )
    {
        return this.text;
    }

    @Override public void setText ( String text )
    {
        this.text = text;
    }

    @Override public String getSource ( ) 
    {
        return this.text;
    }

    @Override public String getFileName ( ) 
    {
        return this.text;
    }

    @Override public Bitmap getImage ( )
    {
        return null;
    }

    @Override public void setImage ( Bitmap image )
    {        
    }

    @Override public int getFontSize ( )
    {
        return fontSize;
    }

    @Override public void setFontSize ( int size )
    {
        this.fontSize = size;
    }
}
