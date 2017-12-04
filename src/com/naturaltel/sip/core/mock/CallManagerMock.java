package com.naturaltel.sip.core.mock;

import javax.sip.address.URI;

import org.apache.log4j.Logger;

import com.naturaltel.sip.component.ConnectCalleeUsers;
import com.naturaltel.sip.component.ConnectType;
import com.naturaltel.sip.component.OriginCalleeUser;
import com.naturaltel.sip.core.impl.CallManagerImpl;
import com.naturaltel.sip.core.manager.CallManager;

public class CallManagerMock extends CallManagerImpl implements CallManager  {

	private Logger logger;
	
	private static CallManager callManager;

	public static CallManager getInstance() {
        if (callManager == null) {
            synchronized (CallManagerMock.class) {
                if (callManager == null) {
                	callManager = new CallManagerMock();
                }
            }
        }
        return callManager;
    }
    
    public CallManagerMock() {
		logger = Logger.getLogger(CallManagerImpl.class);
    }

	@Override
	public ConnectCalleeUsers getConnectCallee(OriginCalleeUser originCalleeUser) {
		ConnectCalleeUsers connectCalleeUsers = new ConnectCalleeUsers();
		URI toUri = originCalleeUser.getCalleeUri();

		logger.debug("toUri = " + toUri.toString());

		URI target = toUri;	//直接使用進來的 toUri
		//TODO 改 target ip port (使用: proxy ip:port)
		logger.debug("Target " + target);
		
		connectCalleeUsers.setConnectType(ConnectType.ConnectOne);
		connectCalleeUsers.clearCalleeUri();
		connectCalleeUsers.addCalleeUri(target);
		
		return connectCalleeUsers;
	}
}
