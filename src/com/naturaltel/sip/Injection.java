package com.naturaltel.sip;

import com.naturaltel.sip.core.impl.B2BUAManagerImpl;
import com.naturaltel.sip.core.impl.CallManagerImpl;
import com.naturaltel.sip.core.impl.SipManagerImpl;
import com.naturaltel.sip.core.impl.StorageManagerImpl;
import com.naturaltel.sip.core.manager.B2BUAManager;
import com.naturaltel.sip.core.manager.CallManager;
import com.naturaltel.sip.core.manager.SipManager;
import com.naturaltel.sip.core.manager.StorageManager;
import com.naturaltel.sip.core.mock.B2BUAManagerMock;
import com.naturaltel.sip.core.mock.CallManagerMock;
import com.naturaltel.sip.core.mock.SipManagerMock;
import com.naturaltel.sip.core.mock.StorageManagerMock;

public class Injection {
	enum ENV {
		MOCK, NORMAL
	}
	
	static ENV SipManagerEnv = ENV.NORMAL;
	static ENV B2BUAManagerEnv = ENV.NORMAL;
	static ENV StorageManagerEnv = ENV.NORMAL;
	static ENV CallManagerEnv = ENV.NORMAL;
	
	
    public static SipManager provideSipManager() {
        if (SipManagerEnv == ENV.MOCK) {
            return SipManagerMock.getInstance();
        } else {
            return SipManagerImpl.getInstance();
        }
    }
    
    public static B2BUAManager provideB2BUAManager() {
        if (B2BUAManagerEnv == ENV.MOCK) {
            return B2BUAManagerMock.getInstance();
        } else {
            return B2BUAManagerImpl.getInstance();
        }
    }
    
    public static StorageManager provideStorageManager() {
        if (StorageManagerEnv == ENV.MOCK) {
            return StorageManagerMock.getInstance();
        } else {
            return StorageManagerImpl.getInstance();
        }
    }
    
    public static CallManager provideCallManager() {
        if (CallManagerEnv == ENV.MOCK) {
            return CallManagerMock.getInstance();
        } else {
            return CallManagerImpl.getInstance();
        }
    }
}
