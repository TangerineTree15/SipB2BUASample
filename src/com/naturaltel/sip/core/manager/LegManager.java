package com.naturaltel.sip.core.manager;

import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.message.Request;

public interface LegManager {
	Request createOtherInviteRequest(Request oriRequest, SipURI formSipURI, URI destination, byte[] rawContents);
}
