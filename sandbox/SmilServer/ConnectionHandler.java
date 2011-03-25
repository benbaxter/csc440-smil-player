package org.spitzig.androidMessageServer;

import java.io.IOException;
import java.net.Socket;


public class ConnectionHandler implements Runnable {

	private Socket connection;
	protected byte[] input;
	
	@Override
	public void run() 
	{
		handleConnection();
	}
	
	protected void handleConnection()
	{
		
	}
	
	protected void closeConnection ( )
	{
		try 
		{ 
	        if(!connection.isClosed())
	        {
	        	connection.close();
	        }
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
//	
//    private byte[] escapeByte ( byte value )
//    {
//    	byte[] result;
//    	if(value == 0)
//    	{
//    		result = new byte[]{1,2};
//    	}
//    	else if(value == 1)
//    	{
//    		result = new byte[]{1,1};
//    	}
//    	else
//    	{
//    		result = new byte[]{value};
//    	}
//    	return result;
//    }

	protected void sendData(String data) 
	{
		sendData(data.getBytes());
	}
//	
//    private byte[] stringToByteArray ( String string )
//    {
//    	byte[] result = new byte[string.length()];
//    	int index;
//    	
//    	for(index = 0; index < string.length(); ++index)
//    	{
//    		result[index] = (byte)string.charAt(index);
//    	}
//    	
//    	return result;
//    }
//    
    protected void sendData(byte[] data)
	{
		sendBytes(connection, data);
	}
//	
//    private void sendEscapedByte (Socket connection, byte value)
//    {
//    	try {
//			connection.getOutputStream().write(escapeByte(value));
//		} 
//    	catch (IOException e) 
//    	{
//			e.printStackTrace();
//		}
//    }
//    
//    private void sendByte (Socket connection, byte value)
//    {
//    	try {
//			connection.getOutputStream().write(value);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}    	
//    }
//    
    private void sendBytes (Socket connection, byte[] data)
    {
//    	int index;
//    	int length = data.length;
    	
        int length = data.length;
        String lengthString = Integer.toString(length) + '\n';

        try {
			connection.getOutputStream().write(lengthString.getBytes());
			connection.getOutputStream().write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
//    	for(index = 0; index < length; ++index)
//    	{
//    		sendEscapedByte(connection, data[index]);
//    	}
//    	sendByte(connection, (byte)0);    	
    }


    protected String byteArrayToString ( byte[] bytes )
    {
    	StringBuilder stringBuilder = new StringBuilder();
    	
    	for(byte currentByte : bytes)
    	{
    		stringBuilder.append((char)currentByte);
    	}
    	return stringBuilder.toString();
    }

    protected void readInput()
    {
//        byte unescapedInput = 0;
//        int input = 0;
//        boolean escapeFlag = false;
//        boolean newLine = true;
        
    	int inputSize;
    	byte inputByte;
    	StringBuilder stringBuilder = new StringBuilder();
    	
    	try {
			inputByte = (byte)connection.getInputStream().read();
			
			while(inputByte != (byte)'\n')
			{
				stringBuilder.append((char)inputByte);
				inputByte = (byte)connection.getInputStream().read();
			}
			inputSize = Integer.parseInt(stringBuilder.toString());
			input = new byte[inputSize];
			int amountRead = 0;
			int totalRead = 0;
			while(totalRead < inputSize)
			{
				amountRead = connection.getInputStream().read(input, totalRead, inputSize - totalRead);
				totalRead += amountRead;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		newline();

    }
//    
//	protected void readInput()
//	{
//        byte unescapedInput = 0;
//        int input = 0;
//        boolean escapeFlag = false;
//        boolean newLine = true;
//        
//		try 
//		{ 
//	        do
//	        {
//	        	input = connection.getInputStream().read();
//	        	if(input != -1)
//	        	{
//	        		if(input == 0 && newLine)
//	        		{
//	        			break;
//	        		}
//	        		else
//	        		{
//	        			if(input == 0)
//	        			{
//	        				newline();
//	        				newLine = true;
//	        			}
//	        			else
//	        			{
//	        				newLine = false;
//		        			if(escapeFlag)
//				        	{
//				        		unescapedInput = unescapeByte((byte)input);
//				        		escapeFlag = false;
//				        	}
//				        	else
//				        	{
//					        	if(input == 1)
//					        	{
//					        		escapeFlag = true;
//					        	}
//					        	else
//					        	{
//					        		unescapedInput = (byte)input;
//					        	}
//				        	}
//				        	if(!escapeFlag)
//				        	{
//				        		processInput(unescapedInput);
//				        	}
//	        			}
//	        		}
//	        	}
//	        }
//	        while(input != -1);
//	        
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//    private byte unescapeByte ( byte value )
//    {
//    	switch(value)
//    	{
//    		case 1:
//    			return 1;
//    		case 2:
//    			return 0;
//    	}
//    	return value;
//    }
    
    protected void newline()
    {
    	//Nothing to do
    }
    
    protected void processInput ( byte value )
    {
		printDebugMessage("Processing Input: " + (int)value);
    }

    public Socket getConnection() 
    {
		return connection;
	}

	public void setConnection(Socket connection) 
	{
		this.connection = connection;
	}
    
    protected void printDebugMessage ( String message )
    {
    	Log.message(message);
    }
}
