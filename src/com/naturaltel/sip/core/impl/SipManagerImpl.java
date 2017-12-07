package com.naturaltel.sip.core.impl;

import java.util.Properties;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipFactory;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.address.AddressFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.header.RequireHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.naturaltel.sip.component.ListeningPointConfig;
import com.naturaltel.sip.core.SipRequestListener;
import com.naturaltel.sip.core.SipResponsetListener;
import com.naturaltel.sip.core.manager.CallManager;
import com.naturaltel.sip.core.manager.ConfigurationManager;
import com.naturaltel.sip.core.manager.SipManager;

import gov.nist.javax.sip.header.HeaderFactoryExt;
import gov.nist.javax.sip.stack.NioMessageProcessorFactory;

public class SipManagerImpl implements SipManager {

	private Logger logger;
	
	private SipRequestListener sipRequestListener;
	private SipResponsetListener sipResponsetListener;
	
	protected SipProvider sipProvider;
	protected AddressFactory addressFactory;
	protected MessageFactory messageFactory;
	protected HeaderFactory headerFactory;
	
	protected CallManager callManager;
	protected ConfigurationManager configurationManager;
	protected ListeningPointConfig listeningPointConfig;

	//TODO Tang 2017/11/06 改使用 config 取得 
//	private static final String myAddress = "192.168.31.106";	//"127.0.0.1";
//	private static final int myPort = 5082;
	public String transport = "udp";//"ws";
	
	
	public void setSipRequestListener(SipRequestListener sipRequestListener) {
		this.sipRequestListener = sipRequestListener;
	}
	
	public void setSipResponsetListener(SipResponsetListener sipResponsetListener) {
		this.sipResponsetListener = sipResponsetListener;
	}

    private static SipManager sipManager;

    public static SipManager getInstance() {
        if (sipManager == null) {
            synchronized (SipManager.class) {
                if (sipManager == null) {
                	sipManager = new SipManagerImpl();
                }
            }
        }
        return sipManager;
    }
    
    public SipManagerImpl() {
    		logger = Logger.getLogger(SipManagerImpl.class);
    }

	@Override
	public void init(CallManager callManager, ConfigurationManager configurationManager) {
		System.out.println("init");
		
		try {
			
			createAppender();
			SipFactory sipFactory = createSipFactory();
			Properties properties = createProperties();
			logger.debug("createSipStack");
			SipStack sipStack = createSipStack(sipFactory, properties);
			createSomeFactory(sipFactory);
			listeningPointConfig = configurationManager.getListeningPointConfig();
			logger.debug(listeningPointConfig.toString());
			transport = listeningPointConfig.localTransport;
			//TODO 這邊要抽出，做多個 localAddress, localPort, addSipListener(mo, mt)
			ListeningPoint listeningPoint = sipStack.createListeningPoint(listeningPointConfig.localAddress, listeningPointConfig.localPort, listeningPointConfig.localTransport);
			sipProvider = createSipProvider(sipStack, listeningPoint);
			sipProvider.addSipListener(this);
			this.callManager = callManager;
			this.configurationManager = configurationManager;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
	}
	


	@Override
	public void processRequest(RequestEvent requestEvent) {
		try {
			Request request = requestEvent.getRequest();
			ServerTransaction serverTransaction = requestEvent.getServerTransaction();
					
			logger.debug("Request: " + request.getMethod() + " with server transaction is " + (serverTransaction==null?"null":serverTransaction)  );
			logger.debug("Dialog State: " + ((requestEvent.getDialog()!=null) ? requestEvent.getDialog().getState() : ""));
			
			if (request.getMethod().equals(Request.INVITE)) {
				if(sipRequestListener!=null) sipRequestListener.doInvite(requestEvent, serverTransaction);
			} else if (request.getMethod().equals(Request.ACK)) {
				if(sipRequestListener!=null) sipRequestListener.doAck(requestEvent, serverTransaction);
			} else if (request.getMethod().equals(Request.CANCEL)) {
				if(sipRequestListener!=null) sipRequestListener.doCancel(requestEvent, serverTransaction);
			} else if (request.getMethod().equals(Request.REGISTER)) {
				if(sipRequestListener!=null) sipRequestListener.doRegister(requestEvent, serverTransaction);
			} else if (request.getMethod().equals(Request.SUBSCRIBE)) {
				if(sipRequestListener!=null) sipRequestListener.dosSubscribe(requestEvent, serverTransaction);
			} else if (request.getMethod().equals(Request.MESSAGE)) {
				if(sipRequestListener!=null) sipRequestListener.doMessage(requestEvent, serverTransaction);
			} else if (request.getMethod().equals(Request.BYE)) {
				if(sipRequestListener!=null) sipRequestListener.doBye(requestEvent, serverTransaction);
			} else if (request.getMethod().equals(Request.INFO)) {
				if(sipRequestListener!=null) sipRequestListener.doInfo(requestEvent, serverTransaction);
			} else if (request.getMethod().equals(Request.UPDATE)) {
				if(sipRequestListener!=null) sipRequestListener.doUpdate(requestEvent, serverTransaction);
			} else if (request.getMethod().equals(Request.PRACK)) {
				if(sipRequestListener!=null) sipRequestListener.doPrack(requestEvent, serverTransaction);
			} else {
				if(sipRequestListener!=null) sipRequestListener.doInDialogRequest(requestEvent, serverTransaction);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
	}

	@Override
	public void processResponse(ResponseEvent responseEvent) {
		try {
			logger.debug("processResponse");
			Response response = responseEvent.getResponse();
			ClientTransaction clientTransaction = responseEvent.getClientTransaction();
			Dialog dialog = responseEvent.getDialog();
			if(dialog!=null) {
				logger.debug("getDialogId=" + dialog.getDialogId());
				logger.debug("getCallId=" + dialog.getCallId().getCallId());
			} else {
				logger.debug("dialog==null");
			}
			
			int statusCode = response.getStatusCode();
			logger.debug("StatusCode=" + statusCode);
			
			if(clientTransaction==null) {
				logger.debug("clientTransaction==null");
				//TODO 要檢討這裡寫法是否正確？
				return;
			} else {
				logger.debug("Response Method=" + clientTransaction.getRequest().getMethod() + ", DialogId=" + responseEvent.getDialog().getDialogId() + ", StatusCode=" + statusCode);				
			}
			
			//TODO 拆
			if(Request.INVITE.equals(clientTransaction.getRequest().getMethod())
					&& (statusCode == Response.OK) ) {
				if(sipResponsetListener!=null) sipResponsetListener.doInviteResponseWithSDP(responseEvent, clientTransaction);
			} else if(Request.INVITE.equals(clientTransaction.getRequest().getMethod())
						&& (statusCode == Response.SESSION_PROGRESS) ) {
					if(sipResponsetListener!=null) sipResponsetListener.doInviteResponseWithSDP(responseEvent, clientTransaction);
	                RequireHeader requireHeader = (RequireHeader) response.getHeader(RequireHeader.NAME);
//	                //Send PRACK for test
//	                if ( requireHeader.getOptionTag().equalsIgnoreCase("100rel")) {
//	                		if(sipResponsetListener!=null) sipResponsetListener.do100Rel(responseEvent, clientTransaction);
//	                }
			} else {
				logger.debug("else");
				ServerTransaction serverTransaction = (ServerTransaction) clientTransaction.getApplicationData();
				if(serverTransaction==null) {
					logger.warn("serverTransaction==null");
					return;
				}
				Response otherResponse = messageFactory.createResponse(statusCode, serverTransaction.getRequest());
				
//				logger.debug("serverTransaction" + " getMethod=" + serverTransaction.getRequest().getMethod());
//				logger.debug("serverTransaction" + " DialogId=" + serverTransaction.getDialog().getDialogId());				
//				logger.debug("clientTransaction" + " getMethod=" + clientTransaction.getRequest().getMethod());
//				logger.debug("clientTransaction" + " DialogId=" + clientTransaction.getDialog().getDialogId());
				
				logger.debug("sendResponse");
				serverTransaction.sendResponse(otherResponse);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	@Override
	public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
		try {
			String dialogId = (dialogTerminatedEvent.getDialog()!=null? dialogTerminatedEvent.getDialog().getDialogId(): null);
			logger.debug("processDialogTerminated:" + dialogId);
			// TODO Auto-generated method stub
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
	}

	@Override
	public void processIOException(IOExceptionEvent ioExceptionEvent) {
		try {
			logger.debug("processIOException:" + ioExceptionEvent.toString());
			// TODO Auto-generated method stub
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
	}

	@Override
	public void processTimeout(TimeoutEvent timeoutEvent) {
		try {
			String dialogId = null;
			String logMsg;
			if(timeoutEvent.isServerTransaction()) {
				if(timeoutEvent!=null 
					&& timeoutEvent.getServerTransaction() !=null 
					&& timeoutEvent.getServerTransaction().getDialog()!=null ) {
					dialogId = timeoutEvent.getServerTransaction().getDialog().getDialogId();
				}
				logMsg = "ST dialogId = " + dialogId;
			} else {
				if(timeoutEvent!=null 
					&& timeoutEvent.getClientTransaction() !=null 
					&& timeoutEvent.getClientTransaction().getDialog()!=null ) {
					dialogId = timeoutEvent.getClientTransaction().getDialog().getDialogId();
				}
				logMsg = "CT dialogId = " + dialogId;
			}
			logger.debug("processTransactionTerminated " + logMsg);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
	}

	@Override
	public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
		try {
			String dialogId = null;
			String logMsg;
			if(transactionTerminatedEvent.isServerTransaction()) {
				if(transactionTerminatedEvent!=null 
					&& transactionTerminatedEvent.getServerTransaction() !=null 
					&& transactionTerminatedEvent.getServerTransaction().getDialog()!=null ) {
					dialogId = transactionTerminatedEvent.getServerTransaction().getDialog().getDialogId();
				}
				logMsg = "ST dialogId = " + dialogId;
			} else {
				if(transactionTerminatedEvent!=null 
					&& transactionTerminatedEvent.getClientTransaction() !=null 
					&& transactionTerminatedEvent.getClientTransaction().getDialog()!=null ) {
					dialogId = transactionTerminatedEvent.getClientTransaction().getDialog().getDialogId();
				}
				logMsg = "CT dialogId = " + dialogId;
			}
			logger.debug("processTransactionTerminated " + logMsg);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
	}


	

	
	
	
	private void createAppender() {
		//create appender
		ConsoleAppender console = new ConsoleAppender(); 
		//configure the appender
		String PATTERN = "%d [%p|%c|%C{1}][%L] %m%n";
		console.setLayout(new PatternLayout(PATTERN)); 
		console.setThreshold(Level.DEBUG);
		console.activateOptions();
		//add appender to any Logger (here is root)
		Logger.getRootLogger().addAppender(console);
	}
	private SipFactory createSipFactory() {
		SipFactory sipFactory = null;
		sipFactory = SipFactory.getInstance();
		sipFactory.setPathName("gov.nist");
		return sipFactory;
	}
	private Properties createProperties() {
		Properties properties = new Properties();
		properties.setProperty("javax.sip.STACK_NAME", "shootme");
		// You need 16 for logging traces. 32 for debug + traces.
		// Your code will limp at 32 but it is best for debugging.
		properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "LOG4J");
		properties.setProperty("gov.nist.javax.sip.DEBUG_LOG", "shootmedebug.txt");
		properties.setProperty("gov.nist.javax.sip.SERVER_LOG", "shootmelog.txt");
		properties.setProperty("gov.nist.javax.sip.MESSAGE_PROCESSOR_FACTORY", NioMessageProcessorFactory.class.getName());
		return properties;
	}
	private SipStack createSipStack(SipFactory sipFactory, Properties properties) {
		SipStack sipStack = null;
		try {
			// Create SipStack object
			sipStack = sipFactory.createSipStack(properties);
			logger.debug("sipStack = " + sipStack);
		} catch (PeerUnavailableException e) {
			// could not find
			// gov.nist.jain.protocol.ip.sip.SipStackImpl
			// in the classpath
			logger.error(e.getMessage());
			if (e.getCause() != null) e.getCause().printStackTrace();
//			junit.framework.TestCase.fail("Exit JVM");
		}
		return sipStack;
	}
	private void createSomeFactory(SipFactory sipFactory) {
		try {
			headerFactory = sipFactory.createHeaderFactory();
			addressFactory = sipFactory.createAddressFactory();
			messageFactory = sipFactory.createMessageFactory();
		} catch (PeerUnavailableException ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	private SipProvider createSipProvider(SipStack sipStack, ListeningPoint listeningPoint) {
		SipProvider sipProvider = null;
		try {
			sipProvider = sipStack.createSipProvider(listeningPoint);
			logger.debug("ws provider " + sipProvider);
		} catch (ObjectInUseException ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
		return sipProvider;
		
	}
}
