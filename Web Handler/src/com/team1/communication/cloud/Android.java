package com.team1.communication.cloud;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class Android extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    public void doGet(HttpServletRequest req, HttpServletResponse res)
    	throws ServletException, IOException
    {
    	String mode = req.getParameter("mode");
    	PrintWriter out = res.getWriter();
	    if(mode.equals("upload"))
	    {
	    	String url = blobstoreService.createUploadUrl("/upload");
	    	res.setContentType("text/plain");
	    	out.println(url);
	    }
	    else if(mode.equals("download"))
	    {
	    	String filename = req.getParameter("file");
	    	Map<String, BlobKey> keys = blobstoreService.getUploadedBlobs(req);
	    	
	    	BlobKey blobKey = keys.get(filename);
	        blobstoreService.serve(blobKey, res);
	    	
	    }
    }
}
