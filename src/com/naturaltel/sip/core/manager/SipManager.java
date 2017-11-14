package com.naturaltel.sip.core.manager;

import javax.sip.SipListener;

public interface SipManager extends SipListener {
	void init(CallManager callManager);

}
