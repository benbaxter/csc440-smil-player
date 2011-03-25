package org.spitzig.androidMessageServer;

public class Resource {
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMessageId() {
		return messageId;
	}
	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}
	public int getResourceTypeId() {
		return resourceTypeId;
	}
	public void setResourceTypeId(int resourceTypeId) {
		this.resourceTypeId = resourceTypeId;
	}
	public byte[] getResource() {
		return resource;
	}
	public void setResource(byte[] resource) {
		this.resource = resource;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	private int id;
	private int messageId;
	private int resourceTypeId;
	private byte[] resource;
	private String location;
}
