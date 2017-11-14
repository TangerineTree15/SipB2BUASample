package com.naturaltel.sip.core.mock;

import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;

import com.naturaltel.sip.core.manager.SipManager;

public class SipManagerMock implements SipManager {

    private static SipManager sipManager;

    public static SipManager getInstance() {
        if (sipManager == null) {
            synchronized (SipManager.class) {
                if (sipManager == null) {
                	sipManager = new SipManagerMock();
                }
            }
        }
        return sipManager;
    }
    
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processDialogTerminated(DialogTerminatedEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processIOException(IOExceptionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processRequest(RequestEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processResponse(ResponseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processTimeout(TimeoutEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
