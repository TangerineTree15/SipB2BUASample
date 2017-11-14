package com.naturaltel.sip.component;

import javax.sip.address.SipURI;

/**
 * 原始受話資料
 */
public class OriginCalleeUser {
	SipURI calleeUri;

	public OriginCalleeUser(SipURI calleeUri) {
		super();
		this.calleeUri = calleeUri;
	}

	public SipURI getCalleeUri() {
		return calleeUri;
	}

	public void setCalleeUri(SipURI calleeUri) {
		this.calleeUri = calleeUri;
	}
	
	
	
}
