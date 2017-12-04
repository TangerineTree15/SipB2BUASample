package com.naturaltel.sip.component;

import javax.sip.address.SipURI;
import javax.sip.address.TelURL;
import javax.sip.address.URI;

/**
 * 原始受話資料
 */
public class OriginCalleeUser {
	boolean isSipUri = true;
	SipURI calleeSipUri;
	TelURL calleeTelURL;

	public OriginCalleeUser(SipURI calleeSipUri) {
		super();
		isSipUri = true;
		this.calleeSipUri = calleeSipUri;
	}
	
	public OriginCalleeUser(TelURL calleeTelURL) {
		super();
		isSipUri = false;
		this.calleeTelURL = calleeTelURL;
	}

	public boolean isSipUri() {
		return isSipUri;
	}

	public void setSipUri(boolean isSipUri) {
		this.isSipUri = isSipUri;
	}

	public SipURI getCalleeSipUri() {
		return calleeSipUri;
	}

	public void setCalleeSipUri(SipURI calleeSipUri) {
		this.calleeSipUri = calleeSipUri;
	}
	
	public TelURL getCalleeTelURL() {
		return calleeTelURL;
	}

	public void setCalleeTelURL(TelURL calleeTelURL) {
		this.calleeTelURL = calleeTelURL;
	}
	
	public URI getCalleeUri() {
		if(isSipUri) {
			return calleeSipUri;			
		} else {
			return calleeTelURL;
		}
	}
	
	
	
}
