package com.naturaltel.sip.core.mock;

import javax.sip.ClientTransaction;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;

import com.naturaltel.sip.core.manager.B2BUAManager;
import com.naturaltel.sip.core.manager.CallManager;
import com.naturaltel.sip.core.manager.ConfigurationManager;

public class B2BUAManagerMock extends SipManagerMock implements B2BUAManager {

    private static B2BUAManager b2BUAManager;

    public static B2BUAManager getInstance() {
        if (b2BUAManager == null) {
            synchronized (B2BUAManager.class) {
                if (b2BUAManager == null) {
                	b2BUAManager = new B2BUAManagerMock();
                }
            }
        }
        return b2BUAManager;
    }
    
	@Override
	public void init(CallManager callManager, ConfigurationManager configurationManager) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void doInvite(RequestEvent requestEvent, ServerTransaction serverTransaction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doAck(RequestEvent requestEvent, ServerTransaction serverTransaction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doCancel(RequestEvent requestEvent, ServerTransaction serverTransaction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doRegister(RequestEvent requestEvent, ServerTransaction serverTransaction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dosSubscribe(RequestEvent requestEvent, ServerTransaction serverTransaction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doMessage(RequestEvent requestEvent, ServerTransaction serverTransaction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doBye(RequestEvent requestEvent, ServerTransaction serverTransaction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doInfo(RequestEvent requestEvent, ServerTransaction serverTransaction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doUpdate(RequestEvent requestEvent, ServerTransaction serverTransaction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doInDialogRequest(RequestEvent requestEvent, ServerTransaction serverTransaction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doInvite200Response(ResponseEvent responseEvent, ClientTransaction clientTransaction) {
		// TODO Auto-generated method stub
		
	}
	
	

}
