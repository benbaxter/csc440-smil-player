package org.spitzig.androidMessageServer;

public class StringConnectionHandler extends ConnectionHandler {

	private String currentLine = "";
	@Override
    protected void newline()
    {
    	printDebugMessage(currentLine);
    	currentLine = "";
    }
    
	@Override
    protected void processInput ( byte value )
    {
		currentLine += (char)value;
    }
}
