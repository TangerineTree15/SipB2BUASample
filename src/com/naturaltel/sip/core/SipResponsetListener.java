package com.naturaltel.sip.core;

import javax.sip.ClientTransaction;
import javax.sip.ResponseEvent;

public interface SipResponsetListener {
	void doInviteResponseWithSDP(ResponseEvent responseEvent, ClientTransaction clientTransaction);
	void do100Rel(ResponseEvent responseEvent, ClientTransaction clientTransaction);
	
}
