package org.spitzig.androidMessageServer;

import java.util.List;


public class SMILMessageConnectionHandler extends ConnectionHandler{

	private int messageID;
	private String key;
	private String action;
	private Message message;
	private String command;
	private String resourceDetails;
	private int resourceTypeID;
	private String resourceExtension;
	private byte[] resource;
	private boolean finished = false;
	private ConnectionStatus currentStatus = ConnectionStatus.RECIEVING_ACTION;
//	private ArrayList<Byte> currentLine = new ArrayList<Byte>();
	
	@Override
    protected void newline()
    {
    	switch(currentStatus)
    	{
    		case RECIEVING_ACTION:
    			action = byteArrayToString(input);//byteListToString(currentLine);
    			break;
    		case RECIEVING_SMIL:
    			key = saveMessage(input);
    			break;
    		case RECIEVING_KEY:
    			key = byteArrayToString(input);
    			message = getMessageByKey(key);
    			break;
    		case RECIEVING_COMMAND:
    			command = byteArrayToString(input);
    			break;
    		case RECIEVING_RESOURCE_DETAILS:
    			String[] resourceDetailParts;
    			resourceDetails = byteArrayToString(input);
    			resourceDetailParts = resourceDetails.split("\\|");
    			resourceTypeID = Integer.parseInt(resourceDetailParts[0]);
    			resourceExtension = resourceDetailParts[1];
    			break;
    		case RECIEVING_RESOURCE_DATA:
    			Log.message("Got resource: converting");
    			resource = input;
    			Log.message("Resource converted");
    			break;
    			
    	}
    	//currentLine = new ArrayList<Byte>();
    }
	
	protected void handleConnection()
	{
		Log.message("Connection established.");
		readInput();
		if(action.equals("send"))
		{
			Log.message("Recieving message.");
			currentStatus = ConnectionStatus.RECIEVING_SMIL;
			readInput();
			Log.message("Sending key.");
			currentStatus = ConnectionStatus.SENDING_KEY;
			sendData(key);
			while(finished == false)
			{
				Log.message("Recieving command.");
				currentStatus = ConnectionStatus.RECIEVING_COMMAND;
				readInput();
				handleCommand();
			}
		}
		else if(action.equals("recieve"))
		{
			currentStatus = ConnectionStatus.RECIEVING_KEY;
			readInput();
			currentStatus = ConnectionStatus.SENDING_MESSAGE;
			sendData(message.getMessage());
			List<Resource> resources =getResourcesByMessageId(message.getId());
			
			for(Resource resource : resources)
			{
				sendData("resource");
				sendData(Integer.toString(resource.getResourceTypeId()) + "|" + resource.getLocation());
				sendData(resource.getResource());
			}
			
			sendData("end");
		}
	}
	
	private List<Resource> getResourcesByMessageId(int messageId)
	{
		List<Resource> result;
		DatabaseHandler handler = new DatabaseHandler();
		result = handler.getResourcesByMessageId(messageId);
		handler.close();
		return result;
	}
	
	private void handleCommand ()
	{
		if(command.equals("end"))
		{
			Log.message("Ending connection.");
			finished = true;
		}
		else if(command.equals("resource"))
		{
			Log.message("Recieving resource details.");
			currentStatus = ConnectionStatus.RECIEVING_RESOURCE_DETAILS;
			readInput();
			Log.message("Recieving resource.");
			currentStatus = ConnectionStatus.RECIEVING_RESOURCE_DATA;
			readInput();
			Log.message("Resource recieved");
			saveCurrentResource();
			Log.message("Resource saved");
		}
	}
	
	@Override
    protected void processInput ( byte value )
    {
		//currentLine.add(value);
    }
	
	private Message getMessageByKey ( String key )
	{
		Message message;
		DatabaseHandler handler = new DatabaseHandler();
		message = handler.getMessageByKey(key);
		System.out.println(message.getMessage());
		handler.close();
		return message;
	}
	
	private int saveCurrentResource()
	{
		DatabaseHandler handler = new DatabaseHandler();
		return handler.createResource(messageID, resourceTypeID, resource, resourceExtension);
	}

//	private String saveMessage ( List<Byte> bytes)
//	{
//		Message message;
//		DatabaseHandler handler = new DatabaseHandler();
//		this.messageID = handler.createMessage(byteListToString(bytes));
//		message = handler.getMessage(messageID);
//		System.out.println(message.getKey());
//		handler.close();
//		return message.getKey();
//	}
//	
	private String saveMessage ( byte[] bytes)
	{
		Message message;
		DatabaseHandler handler = new DatabaseHandler();
		this.messageID = handler.createMessage(byteArrayToString(bytes));
		message = handler.getMessage(messageID);
		System.out.println(message.getKey());
		handler.close();
		return message.getKey();
	}
//	
//	private String byteListToString(List<Byte> bytes)
//	{
//		String result = "";
//		
//		for(byte currentByte : bytes)
//		{
//			result += (char)currentByte;
//		}
//		
//		return result;
//	}
//	
//	private byte[] byteListToByteArray(List<Byte> bytes)
//	{
//		byte[] result = new byte[bytes.size() + 1];
//		int index = 0;
//		
//		for(byte currentByte : bytes)
//		{
//			result[index++] = currentByte;
//		}
//		
//		return result;
//	}
}
