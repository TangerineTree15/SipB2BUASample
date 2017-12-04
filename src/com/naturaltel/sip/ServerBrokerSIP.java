package com.naturaltel.sip;

import com.naturaltel.sip.core.manager.B2BUAManager;
import com.naturaltel.sip.core.manager.CallManager;
import com.naturaltel.sip.core.manager.ConfigurationManager;

public class ServerBrokerSIP {

	public static void main(String args[]) {
//		new B2BUA().init();	//for B2BUA test
		
		//MoCall
		CallManager moCallManager = Injection.provideCallManager();
		ConfigurationManager moConfigurationManager = Injection.provideConfigurationManager();
		moConfigurationManager.loadListeningPointConfig("./ListeningPointConfig.json");
		
		B2BUAManager b2BUAManager = Injection.provideB2BUAManager();
		b2BUAManager.init(moCallManager, moConfigurationManager);
		
		//MTCall-1
//		CallManager mtCallManager1 = Injection.provideMtCallManager();
//		ConfigurationManager mtConfigurationManager1 = Injection.provideConfigurationManager();
//		mtConfigurationManager1.loadListeningPointConfig("./MTListeningPointConfig1.json");
//		
//		B2BUAManager mtB2BUAManager1 = Injection.provideB2BUAManager();
//		mtB2BUAManager1.init(mtCallManager1, mtConfigurationManager1);
		
		//MTCall-2
//		CallManager mtCallManager2 = Injection.provideMtCallManager();
//		ConfigurationManager mtConfigurationManager2 = Injection.provideConfigurationManager();
//		mtConfigurationManager2.loadListeningPointConfig("./MTListeningPointConfig2.json");
//		
//		B2BUAManager mtB2BUAManager2 = Injection.provideB2BUAManager();
//		mtB2BUAManager2.init(mtCallManager2, mtConfigurationManager2);
	}
}
