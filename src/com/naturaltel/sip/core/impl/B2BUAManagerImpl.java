package com.naturaltel.sip.core.impl;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicLong;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.Transaction;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.TelURL;
import javax.sip.address.URI;
import javax.sip.header.AcceptHeader;
import javax.sip.header.AllowHeader;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.RequireHeader;
import javax.sip.header.RouteHeader;
import javax.sip.header.SupportedHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.UserAgentHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.log4j.Logger;

import com.naturaltel.sip.Injection;
import com.naturaltel.sip.component.ConnectCalleeUsers;
import com.naturaltel.sip.component.ListeningPointConfig;
import com.naturaltel.sip.component.OriginCalleeUser;
import com.naturaltel.sip.core.manager.B2BUAManager;
import com.naturaltel.sip.core.manager.CallManager;
import com.naturaltel.sip.core.manager.ConfigurationManager;
import com.naturaltel.sip.core.manager.LegManager;
import com.naturaltel.sip.core.manager.StorageManager;

import gov.nist.javax.sip.header.HeaderFactoryImpl;
import gov.nist.javax.sip.header.SIPHeader;
import gov.nist.javax.sip.header.ims.PAccessNetworkInfoHeader;
import gov.nist.javax.sip.header.ims.PAssertedIdentityHeader;
import gov.nist.javax.sip.header.ims.PPreferredService;
import gov.nist.javax.sip.header.ims.PPreferredServiceHeader;
import gov.nist.javax.sip.header.ims.PServedUser;
import gov.nist.javax.sip.header.ims.PServedUserHeader;

public class B2BUAManagerImpl extends SipManagerImpl implements B2BUAManager {

	private Logger logger;
	
	private StorageManager storageManager = Injection.provideStorageManager();
	
	private AtomicLong counter = new AtomicLong();
	
	
    private static B2BUAManager b2BUAManager;

    public static B2BUAManager getInstance() {
        if (b2BUAManager == null) {
            synchronized (B2BUAManager.class) {
                if (b2BUAManager == null) {
                	b2BUAManager = new B2BUAManagerImpl();
                }
            }
        }
        return b2BUAManager;
    }
    
    public B2BUAManagerImpl() {
    		super();
    		logger = Logger.getLogger(B2BUAManagerImpl.class);
    		
    }
    
	@Override
	public void init(CallManager callManager, ConfigurationManager configurationManager) {
		super.init(callManager, configurationManager);
		setSipRequestListener(this);
		setSipResponsetListener(this);
	}

	
	@Override
	public void doInvite(RequestEvent requestEvent, ServerTransaction serverTransaction) {
		try {
			logger.debug("doInvite");
			SipProvider sipProvider = (SipProvider) requestEvent.getSource();
			Request request = requestEvent.getRequest();
			
			if(serverTransaction == null) {
				logger.debug("getNewServerTransaction");
				serverTransaction = sipProvider.getNewServerTransaction(request);
			}
//			displayTransaction(serverTransaction);


			FromHeader from = (FromHeader) request.getHeader(FromHeader.NAME);
			SipURI fromUri = (SipURI) from.getAddress().getURI();	
			ToHeader to = (ToHeader) request.getHeader(ToHeader.NAME);

			OriginCalleeUser originCalleeUser;
			if(to.getAddress().getURI().isSipURI()) {
				SipURI toUri = (SipURI) to.getAddress().getURI();
				originCalleeUser = new OriginCalleeUser(toUri);
				logger.debug("toUri=" + toUri.toString());
			} else {
				TelURL telUrl = (TelURL) to.getAddress().getURI();
				originCalleeUser = new OriginCalleeUser(telUrl);
				logger.debug("telUrl=" + telUrl.toString());
			}
			
			
			//呼叫 callManager 取得要 Connect Callee Users
			ConnectCalleeUsers connectCalleeUsers = callManager.getConnectCallee(originCalleeUser);
			
			ArrayList<URI> calleeUriArray = connectCalleeUsers.getCalleeUri();
			if(calleeUriArray!=null && calleeUriArray.size()>0 && connectCalleeUsers!=null) {
				logger.debug("ConnectType=" + connectCalleeUsers.getConnectType());
				switch(connectCalleeUsers.getConnectType()) {
				case ConferenceRoom:
					//TODO Tang
					break;
				case SequenceRing:
					//TODO Tang
					break;
				case ParallelRing:
					//TODO Tang
					break;
				case ConnectOne:
				default:
					logger.debug("fromUri=" + fromUri);
					logger.debug("calleeUriArray.get(0)=" + calleeUriArray.get(0));
					logger.debug("request.getRawContent() length=" + request.getRawContent().length);
					
					//TODO call IN
					
					//準備另一個 Invite 送出 //TODO Tang 要改成 array, 及交給 callManager
					ClientTransaction clientTransaction = createClientTransaction(request, fromUri, calleeUriArray.get(0), request.getRawContent());
					
					logger.debug("clientTransaction.sendRequest()");
					clientTransaction.sendRequest();
					
					//將 Transaction、Dialog 互存
					storageManager.saveInviteTransaction(serverTransaction, clientTransaction);
					displayTransaction(serverTransaction, clientTransaction);
					break;
					
				}
			} else {
				logger.debug("There is not callee." + to.toString());
				throw new RuntimeException("There is not callee." + to.toString());
			}
			
		} catch (Exception ex) {
			logger.error(ex.toString());
			ex.printStackTrace();
		}
		
	}

	@Override
	public void doAck(RequestEvent requestEvent, ServerTransaction serverTransaction) {
			try {
				Dialog dialog = serverTransaction.getDialog();
				Dialog otherDialog = storageManager.loadTransaction(dialog).getClientDialog();
				logger.debug("Dialog State = " + dialog.getState() + ", otherDialog State = " + otherDialog.getState());
				Request request = otherDialog.createAck(otherDialog.getLocalSeqNumber());
				otherDialog.sendAck(request);
			} catch (InvalidArgumentException ex) {
				logger.error(ex.getMessage());
				ex.printStackTrace();
			} catch (SipException ex) {
				logger.error(ex.getMessage());
				ex.printStackTrace();
			}
	}

	@Override
	public void doCancel(RequestEvent requestEvent, ServerTransaction serverTransaction) {
		try {
			Dialog dialog = serverTransaction.getDialog();
			Dialog otherDialog = storageManager.loadTransaction(dialog).getClientDialog();
			logger.debug("Dialog State = " + dialog.getState() + ", otherDialog State = " + otherDialog.getState());
			
			ArrayList<ClientTransaction> clientTransactionArray = storageManager.loadTransaction(dialog).getClientTransaction();
			for(ClientTransaction clientTransaction : clientTransactionArray) {
				logger.debug("ClientTransaction State = " + clientTransaction.getState());
				Request cancelRequest  = clientTransaction.createCancel();
				ClientTransaction ct = sipProvider.getNewClientTransaction(cancelRequest);
				storageManager.saveOtherTransaction(serverTransaction, ct);
				//送出
				ct.sendRequest();
			}
			
		} catch (SipException ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
	}

	@Override
	public void doRegister(RequestEvent requestEvent, ServerTransaction serverTransaction) {

		Request request = requestEvent.getRequest();
		ContactHeader contact = (ContactHeader) request.getHeader(ContactHeader.NAME);
		SipURI contactUri = (SipURI) contact.getAddress().getURI();
		FromHeader from = (FromHeader) request.getHeader(FromHeader.NAME);
		SipURI fromUri = (SipURI) from.getAddress().getURI();
		//儲存
		storageManager.putRegistrar(fromUri.getUser(), contactUri);
		logger.debug("User=" + fromUri.getUser() + ", contactUri=" + contactUri);
		logger.debug("registrar.size=" + storageManager.getRegistrar().size());
		//回覆 200 OK
		sendResponse(Response.OK, request, serverTransaction);
	}

	@Override
	public void dosSubscribe(RequestEvent requestEvent, ServerTransaction serverTransaction) {
		Request request = requestEvent.getRequest();
		sendResponse(Response.OK, request, serverTransaction);
	}

	@Override
	public void doMessage(RequestEvent requestEvent, ServerTransaction serverTransaction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doBye(RequestEvent requestEvent, ServerTransaction serverTransaction) {
		doInDialogRequest(requestEvent, serverTransaction);
	}

	@Override
	public void doInfo(RequestEvent requestEvent, ServerTransaction serverTransaction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doUpdate(RequestEvent requestEvent, ServerTransaction serverTransaction) {
		// TODO Auto-generated method stub
		doInDialogRequest(requestEvent, serverTransaction);
	}
	
	@Override
	public void doPrack(RequestEvent requestEvent, ServerTransaction serverTransaction) {
		//TODO 用 sipp 沒收到 PRACK
		logger.debug("doPrack");
		try {
			Request prack = requestEvent.getRequest();
			Response prackOk = messageFactory.createResponse(Response.OK, prack);
			serverTransaction.sendResponse(prackOk);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
	}

	@Override
	public void doInDialogRequest(RequestEvent requestEvent, ServerTransaction serverTransaction) {
		try {
			logger.debug("doInDialogRequest");
			SipProvider sipProvider = (SipProvider) requestEvent.getSource();
			Request request = requestEvent.getRequest();
			Dialog dialog = serverTransaction.getDialog();
//			displayTransaction(serverTransaction);

			
			Dialog otherDialog = storageManager.loadTransaction(dialog).getOtherDialog(dialog);
			Request otherRequest = otherDialog.createRequest(request.getMethod());
			ClientTransaction clientTransaction = sipProvider.getNewClientTransaction(otherRequest);
			storageManager.saveOtherTransaction(serverTransaction, clientTransaction);
			
			//送出
			otherDialog.sendRequest(clientTransaction);

			displayTransaction(serverTransaction, clientTransaction);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
		
	}

	@Override
	public void doInviteResponseWithSDP(ResponseEvent responseEvent, ClientTransaction clientTransaction) {
		try {
			logger.debug("doInviteResponseWithSDP");
			Response response = responseEvent.getResponse();
			String ipAddress = sipProvider.getListeningPoint(transport).getIPAddress();
			int port = sipProvider.getListeningPoint(transport).getPort();
			
			ServerTransaction serverTransaction;
			if(clientTransaction!=null) {
				serverTransaction = (ServerTransaction) clientTransaction.getApplicationData();
			} else {
				logger.warn("clientTransaction==null");
				logger.warn("getDialog==null: " + (responseEvent.getDialog()==null));
				return;
			}
//			displayTransaction(serverTransaction, clientTransaction);
			Response otherResponse = messageFactory.createResponse(response.getStatusCode(), serverTransaction.getRequest());
			
			//contact Header
			FromHeader from = (FromHeader) response.getHeader(FromHeader.NAME);
			SipURI formSipURI = (SipURI) from.getAddress().getURI();
			String fromName = formSipURI.getUser();
			Address address = addressFactory.createAddress("sip:"+fromName+"@"+ipAddress+":" + port);
			

			ContactHeader contactHeader = headerFactory.createContactHeader(address);
			otherResponse.addHeader(contactHeader);
			
			//ToHeader
			ToHeader otherToHeader = (ToHeader) otherResponse.getHeader(ToHeader.NAME);
			ToHeader toHeader = (ToHeader) response.getHeader(ToHeader.NAME);
			//if(toHeader.getTag() == null) toHeader.setTag(new Long(counter.getAndIncrement()).toString());
			if(otherToHeader.getTag() == null) otherToHeader.setTag(toHeader.getTag());
			logger.debug("toHeader.getTag():" + toHeader.getTag());
			logger.debug("otherToHeader.getTag():" + otherToHeader.getTag());
			
			//ContentTypeHeader
			ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application", "sdp");
			otherResponse.setContent(response.getContent(), contentTypeHeader);
			
			//copy Headers
			String[] hhh = {"Content-Length", "Via", "From", "To", "Call-ID", "CSeq", "Contact", "Content-Type"};
			Header extensionHeader;

			@SuppressWarnings("unchecked")
			ListIterator<String> headers = response.getHeaderNames();
			while(headers.hasNext()) {
				String headerName = headers.next();
				if(!Arrays.asList(hhh).contains(headerName)) {
					logger.debug("ResponseHeader: " + headerName +"="+ response.getHeader(headerName));
					extensionHeader = response.getHeader(headerName);
					otherResponse.addHeader(extensionHeader);
				}
			}

			
			
			//send Response
			logger.debug("send Response");
			serverTransaction.sendResponse(otherResponse);
			displayTransaction(serverTransaction, clientTransaction);
			
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
		
		
	}
	
	@Override
	public void do100Rel(ResponseEvent responseEvent, ClientTransaction clientTransaction) {
		logger.debug("do100Rel");
		//do100Rel, Send PRACK
		
		try {
			Response response = (Response) responseEvent.getResponse();
	        RequireHeader requireHeader = (RequireHeader) response.getHeader(RequireHeader.NAME);
	        SipProvider sipProvider = (SipProvider) responseEvent.getSource();
	        if ( requireHeader.getOptionTag().equalsIgnoreCase("100rel")) {
	            Dialog dialog = clientTransaction.getDialog();
	            Request prackRequest = dialog.createPrack(response);
	            ClientTransaction ct = sipProvider.getNewClientTransaction(prackRequest);
	            logger.debug("Send PRACK");
	            dialog.sendRequest(ct);
	        }
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
        
	}	

	
	
	private void sendResponse(int responseCode, Request request, ServerTransaction serverTransaction) {
		try {
			displayTransaction(serverTransaction);
			Response response = this.messageFactory.createResponse(responseCode, request);
			ContactHeader contactHeader = (ContactHeader) request.getHeader(ContactHeader.NAME);
			int expires = 3600;
			if(request.getExpires()!=null && request.getExpires().getExpires()>0) {
				expires =  request.getExpires().getExpires();
			}
			contactHeader.setExpires(expires);
			contactHeader.setParameter("received", request.getRequestURI().toString());
            response.addHeader(contactHeader);
            if(request.getExpires()!=null) {
            		response.addHeader(request.getExpires());
            }
            
			ServerTransaction newServerTransaction = sipProvider.getNewServerTransaction(request);
			newServerTransaction.sendResponse(response);
			displayTransaction(newServerTransaction);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	//TODO Tang 這段 method 要客製化修改
	public ClientTransaction createClientTransaction(Request oriRequest, SipURI formSipURI, URI destination, byte[] rawContents) {
		try {
			//產生另一個 leg 的 Request
			LegManager legManager = new LegManagerImpl(sipProvider, addressFactory, messageFactory, headerFactory, listeningPointConfig);
			Request request = legManager.createOtherInviteRequest(oriRequest, formSipURI, destination, rawContents);
			
			// Create the client transaction.
			ClientTransaction clientTransaction = sipProvider.getNewClientTransaction(request);
			return clientTransaction;
		} catch (Exception ex) {
			logger.error(ex.toString());
			ex.printStackTrace();
		}
		return null;
	}
	
	private void displayTransaction(Transaction transaction1, Transaction transaction2) {
		displayTransaction(transaction1);
		displayTransaction(transaction2);
	}
	private void displayTransaction(Transaction transaction) {
		try {
			String transactionFlag = "Transaction";
			if(transaction!=null) {
				if(transaction instanceof ClientTransaction) {
					transactionFlag = "ClientTransaction";
				} else if(transaction instanceof ServerTransaction) {
					transactionFlag = "ServerTransaction";
				}
				logger.debug(transactionFlag + " Request Method=" + transaction.getRequest().getMethod());
				logger.debug(transactionFlag + " Transaction RetransmitTimer=" + transaction.getRetransmitTimer());
				logger.debug(transactionFlag + " Transaction getBranchId=" + transaction.getBranchId());
				logger.debug(transactionFlag + " Transaction getState=" + transaction.getState());
				if(transaction.getDialog()!=null) {
					logger.debug(transactionFlag + " Dialog DialogId=" + transaction.getDialog().getDialogId());
					logger.debug(transactionFlag + " Dialog CallId=" + transaction.getDialog().getCallId());
					logger.debug(transactionFlag + " Dialog LocalSeqNumber=" + transaction.getDialog().getLocalSeqNumber());
					logger.debug(transactionFlag + " Dialog RemoteSeqNumber=" + transaction.getDialog().getRemoteSeqNumber());
				}
			} else {
				logger.debug(transactionFlag + " is null");
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
}
