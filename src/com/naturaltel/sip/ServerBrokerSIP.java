package com.naturaltel.sip;

import javax.sip.SipProvider;

import com.naturaltel.sip.core.impl.SipStackManagerImpl;
import com.naturaltel.sip.core.manager.B2BUAManager;
import com.naturaltel.sip.core.manager.CallManager;
import com.naturaltel.sip.core.manager.ConfigurationManager;
import com.naturaltel.sip.core.manager.SipStackManager;

public class ServerBrokerSIP {

	public static void main(String args[]) {
//		new B2BUA().init();	//for B2BUA test

		SipStackManager sipStackManager = SipStackManagerImpl.getInstance();

		String stackName;
		String configFile;
		
		
		//MTCall_1
		configFile = "./MTListeningPointConfig1.json";
		stackName = "MTCall_1";
		B2BUAManager mtB2BUAManager1 = Injection.newB2BUAManager();
		CallManager mtCallManager1 = Injection.newMoCallManager();	//TDOO 實作 mt CallManager
		addB2BUAManager(sipStackManager, stackName, mtB2BUAManager1, mtCallManager1, configFile);
		
		//MTCall_2
		configFile = "./MTListeningPointConfig2.json";
		stackName = "MTCall_2";
		B2BUAManager mtB2BUAManager2 = Injection.newB2BUAManager();
		CallManager mtCallManager2 = Injection.newMoCallManager();	//TDOO 實作 mt CallManager
		addB2BUAManager(sipStackManager, stackName, mtB2BUAManager2, mtCallManager2, configFile);
		
		//MoCall
		configFile = "./MOListeningPointConfig.json";
		stackName = "MOCall";
		B2BUAManager moB2BUAManager = Injection.newB2BUAManager();
		CallManager moCallManager = Injection.newMoCallManager();
		addB2BUAManager(sipStackManager, stackName, moB2BUAManager, moCallManager, configFile);
	}
	
	private static void addB2BUAManager(SipStackManager sipStackManager, String stackName, B2BUAManager b2BUAManager, CallManager callManager, String configFile) {
		//設定檔
		ConfigurationManager moConfigurationManager = Injection.newConfigurationManager(configFile);
		//Add B2BUAManager to SipListener
		SipProvider sipProvider = sipStackManager.addSipListener(stackName, b2BUAManager, moConfigurationManager);
		//init B2BUAManager
		b2BUAManager.init(sipProvider, callManager, moConfigurationManager);
	}
	
}
