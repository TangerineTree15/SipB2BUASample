package com.naturaltel.sip.component;

import java.util.ArrayList;

import javax.sip.address.URI;

/**
 * Connect 受話資料 
 */
public class ConnectCalleeUsers {
	ConnectType connectType;
	ArrayList<URI> calleeUriArray = new ArrayList<URI>();

	public ConnectCalleeUsers() {
		
	}

	public ConnectType getConnectType() {
		return connectType;
	}

	public void setConnectType(ConnectType connectType) {
		this.connectType = connectType;
	}
	
	public ArrayList<URI> getCalleeUri() {
		synchronized (ConnectCalleeUsers.class) {
			return calleeUriArray;
		}
	}

	public void setCalleeUri(ArrayList<URI> calleeUriArray) {
		synchronized (ConnectCalleeUsers.class) {
			this.calleeUriArray = calleeUriArray;
		}
	}
	
	public void addCalleeUri(URI calleeUri) {
		synchronized (ConnectCalleeUsers.class) {
			this.calleeUriArray.add(calleeUri);
		}
	}
	
	public void clearCalleeUri() {
		synchronized (ConnectCalleeUsers.class) {
			this.calleeUriArray = new ArrayList<URI>();
		}
	}
	
	public void removeCalleeUri(URI calleeUri) {
		synchronized (ConnectCalleeUsers.class) {
			this.calleeUriArray.remove(calleeUri);
		}
	}


	
}
