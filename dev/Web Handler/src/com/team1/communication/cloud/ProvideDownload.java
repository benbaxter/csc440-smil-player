package com.team1.communication.cloud;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class ProvideDownload extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    public void doGet(HttpServletRequest req, HttpServletResponse res)
    	throws ServletException, IOException
    {

    	//PrintWriter out = res.getWriter();

    	String filename = req.getParameter("file");
    	BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
    	Iterator<BlobInfo> it = blobInfoFactory.queryBlobInfos();
    	BlobKey bkey = null;
    	while(it.hasNext())
    	{
    		BlobInfo binfo = it.next();
    		if(binfo.getFilename().equals(filename))
    		{
    			bkey = binfo.getBlobKey();
    		}
    	}
    	if(bkey!=null)
    		blobstoreService.serve(bkey, res);
    	
	    	
    }
}
