package com.naturaltel.sip.core.manager;

import javax.sip.SipProvider;
import javax.sip.SipStack;

public interface SipStackManager {
	SipProvider addSipListener(String stackName, SipManager sipManager, ConfigurationManager configurationManager);
	SipStack getSipStack();
}
