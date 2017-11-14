package com.naturaltel.sip;

import com.naturaltel.sip.core.manager.B2BUAManager;

public class ServerBrokerSIP {

	public static void main(String args[]) {
//		new B2BUA().init();
		
		B2BUAManager b2BUAManager = Injection.provideB2BUAManager();
		b2BUAManager.init();
		
	}
}
