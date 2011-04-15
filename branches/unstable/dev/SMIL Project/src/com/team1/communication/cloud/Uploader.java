package com.team1.communication.cloud;

import java.io.*;
import java.net.*;
import android.util.Log;


public class Uploader {

    public static String uploadGetKey(File file)
    {
        String uploadURL = HTTPRequestPoster.sendGetRequest(CloudConstants.uploadURL, "");
        Log.i("UPLOADER", "got the url");
        Log.i("UPLOADER", uploadURL +"");
        String key = null;
        if( uploadURL != null) {
            try {
                Log.i("UPLOADER", "About to make a post");
                InputStream in = ClientHttpRequest.post(new URL(uploadURL), new Object[] {"myFile", file });
                Log.i("UPLOADER", "got the post");
                key = convertStreamToString(in);
                in.close();
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
            return key;
        }
        return null;
    }
	public static boolean upload(File file)
	{
		String key = uploadGetKey(file);
		return (key != null);
	}
	
	public static String convertStreamToString(InputStream is)
    throws IOException 
    {
        /*
         * To convert the InputStream to String we use the
         * Reader.read(char[] buffer) method. We iterate until the
         * Reader return -1 which means there's no more data to
         * read. We use the StringWriter class to produce the string.
         */
        if (is != null) {
            Writer writer = new StringWriter();
        
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {        
            return "";
        }
    }
}
