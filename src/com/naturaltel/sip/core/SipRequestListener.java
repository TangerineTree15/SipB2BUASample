package com.naturaltel.sip.core;

import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;

public interface SipRequestListener {
	void doInvite(RequestEvent requestEvent, ServerTransaction serverTransaction);
	void doAck(RequestEvent requestEvent, ServerTransaction serverTransaction);
	void doCancel(RequestEvent requestEvent, ServerTransaction serverTransaction);
	void doRegister(RequestEvent requestEvent, ServerTransaction serverTransaction);
	void dosSubscribe(RequestEvent requestEvent, ServerTransaction serverTransaction);
	void doMessage(RequestEvent requestEvent, ServerTransaction serverTransaction);
	void doBye(RequestEvent requestEvent, ServerTransaction serverTransaction);
	void doInfo(RequestEvent requestEvent, ServerTransaction serverTransaction);
	void doUpdate(RequestEvent requestEvent, ServerTransaction serverTransaction);
	/** Do the any in dialog request */
	void doInDialogRequest(RequestEvent requestEvent, ServerTransaction serverTransaction);
}
