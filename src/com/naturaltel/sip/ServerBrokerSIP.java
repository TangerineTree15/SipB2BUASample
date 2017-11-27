package com.naturaltel.sip;

import com.naturaltel.sip.core.manager.B2BUAManager;
import com.naturaltel.sip.core.manager.CallManager;
import com.naturaltel.sip.core.manager.ConfigurationManager;

public class ServerBrokerSIP {

	public static void main(String args[]) {
//		new B2BUA().init();
		
		//MoCall
		CallManager moCallManager = Injection.provideCallManager();
		ConfigurationManager configurationManager = Injection.provideConfigurationManager();
		configurationManager.loadListeningPointConfig("./ListeningPointConfig.json");
		
		B2BUAManager b2BUAManager = Injection.provideB2BUAManager();
		b2BUAManager.init(moCallManager, configurationManager);
		
		//MTCall
		//MTCall
	}
}
