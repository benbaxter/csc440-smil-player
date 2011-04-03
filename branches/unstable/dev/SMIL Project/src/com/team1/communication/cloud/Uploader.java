package com.team1.communication.cloud;
import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.message.*;

import android.util.Log;


public class Uploader {

	public static boolean upload(File file)
	{
		String uploadURL = HTTPRequestPoster.sendGetRequest(CloudConstants.uploadURL, "");
		Log.i("UPLOADER", "got the url");
		Log.i("UPLOADER", uploadURL + "");
		System.out.println(uploadURL);
		try {
		    Log.i("UPLOADER", "About to make a post");
			InputStream in = ClientHttpRequest.post(new URL(uploadURL), new Object[] {"myFile", file });
			Log.i("UPLOADER", "got the post");
			in.close();
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
