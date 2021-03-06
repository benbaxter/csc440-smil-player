package com.team1.communication.cloud;
import java.io.*;
import java.net.*;

import android.util.Log;

public class Downloader {
	
    static String downloadURL = CloudConstants.downloadURL;
    public static boolean download(String parameters, String saveAs)
    {
        try
        {
            Log.i("DOWNLOAD", parameters);
            InputStream in = new URL(downloadURL+parameters).openStream();
            File file = new File(saveAs);
            BufferedOutputStream fOut = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buffer = new byte[32 * 1024];
            int bytesRead = 0;
            while ((bytesRead = in.read(buffer)) != -1)
            {
              fOut.write(buffer, 0, bytesRead);
              //Log.i("DOWNLOAD", buffer.length+"");
            }
            fOut.flush();
            fOut.close();
            
//            BufferedReader br = new BufferedReader(new FileReader(file));
//            String line = "";
//            while((line=br.readLine()) != null)
//                Log.i("DOWNLOAD", line);
        }
        catch ( MalformedURLException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static boolean downloadKey(String key, String saveAs)
    {
        return download("?key="+key, saveAs);
    }
	public static boolean downloadFilename(String filename, String saveAs) throws MalformedURLException, IOException
	{
	    return download("?file="+URLEncoder.encode( filename, "UTF-8"), saveAs);
	}
	
	public static void downloadFilename(String filename) throws MalformedURLException, IOException
	{
	    downloadFilename(filename, filename);
	}
	
}
