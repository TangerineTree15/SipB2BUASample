package com.naturaltel.sip.core;

import javax.sip.ClientTransaction;
import javax.sip.ResponseEvent;

public interface SipResponsetListener {
	void doInvite200Response(ResponseEvent responseEvent, ClientTransaction clientTransaction);
}
