package org.spitzig.androidMessageServer;

public class Main {
	public static void main(String[] args)
	{
		Thread listenerThread;
		ServerListener serverListener = new ServerListener();
		
		serverListener.setPort(23432);
		
		listenerThread = new Thread(serverListener);
		
		listenerThread.start();
		
		while(true);
		
	}
}
