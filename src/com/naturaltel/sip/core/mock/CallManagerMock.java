package com.naturaltel.sip.core.mock;

import com.naturaltel.sip.core.impl.CallManagerImpl;
import com.naturaltel.sip.core.manager.CallManager;

public class CallManagerMock extends CallManagerImpl implements CallManager  {

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
}
