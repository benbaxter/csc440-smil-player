package org.spitzig.androidMessageServer;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
	Connection connection;
	private static final String databaseURL = "jdbc:mysql://spitzig.org:3306/android_message_server";
	
	private static final String userName = "root";
	private static final String password = "4ss3mb!3r";
	
	public DatabaseHandler ( )
	{
			try {
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection(databaseURL, userName, password);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	public List<Resource> getResourcesByMessageId ( int messageId )
	{
		PreparedStatement preparedStatement;
		ResultSet resultSet;
		List<Resource> result = new ArrayList<Resource>();
		Resource currentResource;
		
		try {
			preparedStatement = connection.prepareStatement("CALL get_resource(?)");
			preparedStatement.setInt(1, messageId);
			resultSet = preparedStatement.executeQuery();
			while(resultSet.next())
			{
				currentResource = new Resource();
				currentResource.setId(resultSet.getInt(1));
				currentResource.setMessageId(resultSet.getInt(2));
				currentResource.setResourceTypeId(resultSet.getInt(3));
				Blob resource = resultSet.getBlob(4);
				currentResource.setResource(resource.getBytes(1, (int)resource.length()));
				currentResource.setLocation(resultSet.getString(5));
				result.add(currentResource);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public int createResource (int messageId, int resourceTypeID, byte[] resource, String extension)
	{
		PreparedStatement preparedStatement;
		ResultSet resultSet;
		Blob resourceData;
		int resourceID = -1;
		
		try {
			resourceData = connection.createBlob();
			resourceData.setBytes(1, resource);
			preparedStatement = connection.prepareStatement("CALL add_resource(?,?,?,?)");
			preparedStatement.setInt(1, messageId);
			preparedStatement.setInt(2, resourceTypeID);
			preparedStatement.setBlob(3, resourceData);
			preparedStatement.setString(4, extension);
			resultSet = preparedStatement.executeQuery();
			if(resultSet.first())
			{
				resourceID = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return resourceID;
	}
	
	public int createMessage ( String message )
	{
		PreparedStatement preparedStatement;
		ResultSet resultSet;
		int messageID = -1;
		
		try {
			preparedStatement = connection.prepareStatement("CALL add_message(?)");
			preparedStatement.setString(1, message);
			resultSet = preparedStatement.executeQuery();
			if(resultSet.first())
			{
				messageID = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return messageID;
	}
	
	public Message getMessage ( int messageID )
	{
		PreparedStatement preparedStatement;
		ResultSet resultSet;
		Message result = new Message();
		
		try {
			preparedStatement = connection.prepareStatement("CALL get_message(?)");
			preparedStatement.setInt(1, messageID);
			resultSet = preparedStatement.executeQuery();
			if(resultSet.first())
			{
				result.setId(resultSet.getInt(1));
				result.setKey(resultSet.getString(2));
				result.setMessage(resultSet.getString(3));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Message getMessageByKey ( String messageKey )
	{
		PreparedStatement preparedStatement;
		ResultSet resultSet;
		Message result = new Message();
		
		try {
			preparedStatement = connection.prepareStatement("CALL get_message_by_key(?)");
			preparedStatement.setString(1, messageKey);
			resultSet = preparedStatement.executeQuery();
			if(resultSet.first())
			{
				result.setId(resultSet.getInt(1));
				result.setKey(resultSet.getString(2));
				result.setMessage(resultSet.getString(3));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void close ( )
	{
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
