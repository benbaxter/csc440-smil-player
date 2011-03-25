package org.spitzig.androidMessageServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListener implements Runnable {

	private int port;
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		Socket currentConnection;
		ServerSocket server;
		ConnectionHandler connectionHandler;
		Thread handlerThread;
		
		try 
		{
			
			server = new ServerSocket(port);
			
			System.out.println("Socket created");
			
			System.out.println("Listening on port " + port);
			
			while(true)
			{
				currentConnection = server.accept();
				
				connectionHandler = new SMILMessageConnectionHandler();
				
				connectionHandler.setConnection(currentConnection);
				
				handlerThread = new Thread(connectionHandler);
				
				handlerThread.start();
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

}
