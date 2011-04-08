package com.team1.communication.cloud;
import java.io.*;
import java.net.*;

import android.util.Log;

public class Downloader {
	
    static String downloadURL = CloudConstants.downloadURL;
	public static void download(String filename, String saveAs) throws MalformedURLException, IOException
	{
		InputStream in = new URL(downloadURL+"?file="+URLEncoder.encode(filename, "UTF-8")).openStream();
		Log.i("DOWNLOADER", saveAs);
		File file = new File(saveAs);
		BufferedOutputStream fOut = null;
		
		fOut = new BufferedOutputStream(new FileOutputStream(file));
		byte[] buffer = new byte[32 * 1024];
		int bytesRead = 0;
		while ((bytesRead = in.read(buffer)) != -1)
		{
		  fOut.write(buffer, 0, bytesRead);
		}
		fOut.flush();
	   fOut.close();

	}
	
	public static void download(String filename) throws MalformedURLException, IOException
	{
	    download(filename, filename);
	}
	
}
