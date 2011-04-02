package com.team1.communication.cloud;
import java.io.*;
import java.net.*;


public class Uploader {

	public static boolean upload(String urp, File file)
	{
		String uploadURL = HTTPRequestPoster.sendGetRequest(urp, "");
		System.out.println(uploadURL);
		try {
			InputStream in = ClientHttpRequest.post(new URL(uploadURL), new Object[] {"myFile", file });
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
