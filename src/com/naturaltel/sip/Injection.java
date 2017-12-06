package com.naturaltel.sip;

import com.naturaltel.sip.core.impl.B2BUAManagerImpl;
import com.naturaltel.sip.core.impl.CallManagerImpl;
import com.naturaltel.sip.core.impl.ConfigurationManagerImpl;
import com.naturaltel.sip.core.impl.SipManagerImpl;
import com.naturaltel.sip.core.impl.StorageManagerImpl;
import com.naturaltel.sip.core.manager.B2BUAManager;
import com.naturaltel.sip.core.manager.CallManager;
import com.naturaltel.sip.core.manager.ConfigurationManager;
import com.naturaltel.sip.core.manager.SipManager;
import com.naturaltel.sip.core.manager.StorageManager;
import com.naturaltel.sip.core.mock.B2BUAManagerMock;
import com.naturaltel.sip.core.mock.CallManagerMock;
import com.naturaltel.sip.core.mock.ConfigurationManagerMock;
import com.naturaltel.sip.core.mock.SipManagerMock;
import com.naturaltel.sip.core.mock.StorageManagerMock;

public class Injection {
	enum ENV {
		MOCK, NORMAL
	}
	
	static ENV B2BUAManagerEnv = ENV.NORMAL;
	static ENV StorageManagerEnv = ENV.NORMAL;
	static ENV CallMoManagerEnv = ENV.MOCK;
	static ENV CallMtManagerEnv = ENV.MOCK;
	static ENV ConfigurationManagerEnv = ENV.NORMAL;
	

    public static B2BUAManager newB2BUAManager() {
        if (B2BUAManagerEnv == ENV.MOCK) {
            return new B2BUAManagerMock();
        } else {
            return new B2BUAManagerImpl();
        }
    }
    
    public static StorageManager provideStorageManager() {
        if (StorageManagerEnv == ENV.MOCK) {
            return StorageManagerMock.getInstance();
        } else {
            return StorageManagerImpl.getInstance();
        }
    }
    
    public static CallManager newMoCallManager() {
        if (CallMoManagerEnv == ENV.MOCK) {
            return new CallManagerMock();
        } else {
            return new CallManagerImpl();	//TODO 分mo/mt
        }
    }
    
    public static CallManager newMtCallManager() {
        if (CallMtManagerEnv == ENV.MOCK) {
            return new CallManagerMock();
        } else {
            return new CallManagerImpl();	//TODO 分mo/mt
        }
    }
    
    public static ConfigurationManager newConfigurationManager() {
        if (ConfigurationManagerEnv == ENV.MOCK) {
            return new ConfigurationManagerMock();
        } else {
            return new ConfigurationManagerImpl();
        }
    }
}
