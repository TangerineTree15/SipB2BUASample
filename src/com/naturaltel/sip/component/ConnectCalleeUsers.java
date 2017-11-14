package com.naturaltel.sip.component;

import java.util.ArrayList;

import javax.sip.address.SipURI;

/**
 * Connect 受話資料 
 */
public class ConnectCalleeUsers {
	ConnectType connectType;
	ArrayList<SipURI> calleeUriArray = new ArrayList<SipURI>();

	public ConnectCalleeUsers() {
		
	}

	public ConnectType getConnectType() {
		return connectType;
	}

	public void setConnectType(ConnectType connectType) {
		this.connectType = connectType;
	}
	
	public ArrayList<SipURI> getCalleeUri() {
		synchronized (ConnectCalleeUsers.class) {
			return calleeUriArray;
		}
	}

	public void setCalleeUri(ArrayList<SipURI> calleeUriArray) {
		synchronized (ConnectCalleeUsers.class) {
			this.calleeUriArray = calleeUriArray;
		}
	}
	
	public void addCalleeUri(SipURI calleeUri) {
		synchronized (ConnectCalleeUsers.class) {
			this.calleeUriArray.add(calleeUri);
		}
	}
	
	public void clearCalleeUri() {
		synchronized (ConnectCalleeUsers.class) {
			this.calleeUriArray = new ArrayList<SipURI>();
		}
	}
	
	public void removeCalleeUri(SipURI calleeUri) {
		synchronized (ConnectCalleeUsers.class) {
			this.calleeUriArray.remove(calleeUri);
		}
	}


	
}
