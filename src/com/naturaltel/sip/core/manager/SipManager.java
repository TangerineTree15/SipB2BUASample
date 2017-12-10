package com.naturaltel.sip.core.manager;

import javax.sip.SipListener;
import javax.sip.SipProvider;

public interface SipManager extends SipListener {
	void init(SipProvider sipProvider, CallManager callManager, ConfigurationManager configurationManage);
}
