package com.naturaltel.sip.core.impl;

import javax.sip.address.SipURI;

import org.apache.log4j.Logger;

import com.naturaltel.sip.Injection;
import com.naturaltel.sip.component.ConnectCalleeUsers;
import com.naturaltel.sip.component.ConnectType;
import com.naturaltel.sip.component.OriginCalleeUser;
import com.naturaltel.sip.core.manager.CallManager;
import com.naturaltel.sip.core.manager.StorageManager;

public class CallManagerImpl implements CallManager {

	private Logger logger;
	
	private StorageManager storageManager = Injection.provideStorageManager();
	
	
	
	private static CallManager callManager;
    public static CallManager getInstance() {
        if (callManager == null) {
            synchronized (CallManager.class) {
                if (callManager == null) {
                	callManager = new CallManagerImpl();
                }
            }
        }
        return callManager;
    }
    
    public CallManagerImpl() {
    		logger = Logger.getLogger(CallManagerImpl.class);
    }

	@Override
	public ConnectCalleeUsers getConnectCallee(OriginCalleeUser originCalleeUser) {
		ConnectCalleeUsers connectCalleeUsers = new ConnectCalleeUsers();
		SipURI toUri = originCalleeUser.getCalleeUri();
		//找出 target (從register 清單找)
		SipURI target = storageManager.getRegistrar(toUri.getUser());
		
		String toUser = toUri.getUser();
		logger.debug("toUri.getUser() = " + toUser);
		if(toUser!=null && (toUser.contains("0903507449") || "service".equals(toUser))) {
			//For 模擬 IMS call 測試，用sipp 發話，轉給另一個 sip call (已Register)
			target = storageManager.getRegistrar("wind347");
//			target = storageManager.getRegistrar("472753");
			logger.debug("Target " + target);
			connectCalleeUsers.setConnectType(ConnectType.ConnectOne);
			connectCalleeUsers.clearCalleeUri();
			connectCalleeUsers.addCalleeUri(target);
			
		} else if(target == null) {
			logger.debug("User " + toUri + " is not registered.");
//			throw new RuntimeException("User not registered " + toUri);
			target = toUri;	//直接使用進來的 toUri
		} else {
			logger.debug("User " + toUri + " is registered.");
			logger.debug("Target " + target);
			
			connectCalleeUsers.setConnectType(ConnectType.ConnectOne);
			connectCalleeUsers.clearCalleeUri();
			connectCalleeUsers.addCalleeUri(target);
		}
		return connectCalleeUsers;
	}
    
    
}
