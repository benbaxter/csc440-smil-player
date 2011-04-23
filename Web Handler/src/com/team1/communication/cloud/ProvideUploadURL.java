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

public class ProvideUploadURL extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    public void doGet(HttpServletRequest req, HttpServletResponse res)
    	throws ServletException, IOException
    {
    	PrintWriter out = res.getWriter();
    	String url = blobstoreService.createUploadUrl("/anupload");
    	res.setContentType("text/plain");
    	out.println(url);
    }
    
    public void doPost(HttpServletRequest req, HttpServletResponse res)
    	throws ServletException, IOException
    {
    	PrintWriter out = res.getWriter();
    	Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
        BlobKey blobKey = blobs.get("myFile");
        out.println(blobKey.getKeyString());
    	
    }
}
