package com.naturaltel.sip.core.impl;


import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.log4j.Logger;

import com.naturaltel.sip.Injection;
import com.naturaltel.sip.component.ConnectCalleeUsers;
import com.naturaltel.sip.component.OriginCalleeUser;
import com.naturaltel.sip.core.manager.B2BUAManager;
import com.naturaltel.sip.core.manager.CallManager;
import com.naturaltel.sip.core.manager.StorageManager;

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
	public void init(CallManager callManager) {
		super.init(callManager);
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
			Dialog dialog = serverTransaction.getDialog();
			logger.debug("DialogId=" + dialog.getDialogId());

			FromHeader from = (FromHeader) request.getHeader(FromHeader.NAME);
			SipURI fromUri = (SipURI) from.getAddress().getURI();	
			ToHeader to = (ToHeader) request.getHeader(ToHeader.NAME);
			SipURI toUri = (SipURI) to.getAddress().getURI();
			
			//呼叫 callManager
			OriginCalleeUser originCalleeUser = new OriginCalleeUser(toUri);
			ConnectCalleeUsers connectCalleeUsers = callManager.getConnectCallee(originCalleeUser);
			
			ArrayList<SipURI> calleeUriArray = connectCalleeUsers.getCalleeUri();
			if(calleeUriArray!=null && calleeUriArray.size()>0 && connectCalleeUsers!=null) {
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
					//準備另一個 Invite 送出 //TODO Tang 要改成 array, 及交給 callManager
					ClientTransaction clientTransaction = createClientTransaction(fromUri, calleeUriArray.get(0), request.getRawContent());
					clientTransaction.sendRequest();
					
					//將 Transaction、Dialog 互存
					storageManager.saveInviteTransaction(serverTransaction, clientTransaction);
					break;
					
				}
			} else {
				logger.debug("There is not callee." + toUri);
				throw new RuntimeException("There is not callee." + toUri);
			}
			
		} catch (Exception ex) {
			logger.error(ex.getMessage());
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
		sendResponse(Response.OK, request);
	}

	@Override
	public void dosSubscribe(RequestEvent requestEvent, ServerTransaction serverTransaction) {
		Request request = requestEvent.getRequest();
		sendResponse(Response.OK, request);
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
		
	}

	@Override
	public void doInDialogRequest(RequestEvent requestEvent, ServerTransaction serverTransaction) {
		try {
			logger.debug("doInDialogRequest");
			SipProvider sipProvider = (SipProvider) requestEvent.getSource();
			Request request = requestEvent.getRequest();
			Dialog dialog = serverTransaction.getDialog();

			
			Dialog otherDialog = storageManager.loadTransaction(dialog).getOtherDialog(dialog);
			Request otherRequest = otherDialog.createRequest(request.getMethod());
			ClientTransaction clientTransaction = sipProvider.getNewClientTransaction(otherRequest);
			storageManager.saveOtherTransaction(serverTransaction, clientTransaction);
			
			//送出
			otherDialog.sendRequest(clientTransaction);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
		
	}

	@Override
	public void doInvite200Response(ResponseEvent responseEvent, ClientTransaction clientTransaction) {
		try {
			Response response = responseEvent.getResponse();
			String ipAddress = sipProvider.getListeningPoint(transport).getIPAddress();
			int port = sipProvider.getListeningPoint(transport).getPort();
			
			ServerTransaction serverTransaction;
			if(clientTransaction!=null) {
				serverTransaction = (ServerTransaction) clientTransaction.getApplicationData();
			} else {
				logger.debug("clientTransaction==null");
				logger.debug("getDialog==null: " + (responseEvent.getDialog()==null));
				return;
			}
			Response otherResponse = messageFactory.createResponse(response.getStatusCode(), serverTransaction.getRequest());
			
			//contact Header
			Address address = addressFactory.createAddress("B2BUA <sip:"+ ipAddress + ":" + port + ">");
			ContactHeader contactHeader = headerFactory.createContactHeader(address);
			response.addHeader(contactHeader);
			
			//ToHeader
			ToHeader toHeader = (ToHeader) otherResponse.getHeader(ToHeader.NAME);
			if(toHeader.getTag() == null) toHeader.setTag(new Long(counter.getAndIncrement()).toString());
			otherResponse.addHeader(contactHeader);
			
			//ContentTypeHeader
			ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application", "sdp");
			otherResponse.setContent(response.getContent(), contentTypeHeader);
			
			//send Response
			serverTransaction.sendResponse(otherResponse);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
		
		
	}

	
	
	private void sendResponse(int responseCode, Request request) {
		try {
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
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	//TODO Tang 這段 method 要客製化修改
	public ClientTransaction createClientTransaction(SipURI formSipURI, SipURI destination, byte[] rawContents) {
		try {
			String fromName = formSipURI.getUser();
//			String fromSipAddress = formSipURI.getHost();
			String fromDisplayName = "";

//			String toSipAddress = "there.com";
//			String toUser = "Target";				//TODO Tang 要修
			String toDisplayName = "Target";			//TODO Tang 要修

			String ipAddress = sipProvider.getListeningPoint(transport).getIPAddress();
			int port = sipProvider.getListeningPoint(transport).getPort();
			
			// create >From Header
			Address fromNameAddress = addressFactory.createAddress(formSipURI);
			fromNameAddress.setDisplayName(fromDisplayName);
			FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress, new Long(counter.getAndIncrement()).toString());

			// create To Header
//			SipURI toAddress = addressFactory.createSipURI(toUser, toSipAddress);
			Address toNameAddress = addressFactory.createAddress(destination);
			toNameAddress.setDisplayName(toDisplayName);
			ToHeader toHeader = headerFactory.createToHeader(toNameAddress, null);

			// create Request URI
			SipURI requestURI = destination;

			// Create ViaHeaders
			ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
			ViaHeader viaHeader = headerFactory.createViaHeader(ipAddress, port, transport, null);

			// add via headers
			viaHeaders.add(viaHeader);

			// Create ContentTypeHeader
			ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application", "sdp");

			// Create a new CallId header
			CallIdHeader callIdHeader = sipProvider.getNewCallId();

			// Create a new Cseq header
			CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1L, Request.INVITE);

			// Create a new MaxForwardsHeader
			MaxForwardsHeader maxForwards = headerFactory.createMaxForwardsHeader(70);

			// Create the request.
			Request request = messageFactory.createRequest(requestURI,
					Request.INVITE, callIdHeader, cSeqHeader, fromHeader,
					toHeader, viaHeaders, maxForwards);
			// Create contact headers
			//String host = "127.0.0.1";
			String host = ipAddress;

			SipURI contactUrl = addressFactory.createSipURI(fromName, host);
			contactUrl.setPort(port);
			contactUrl.setLrParam();

			// Create the contact name address.
			SipURI contactURI = addressFactory.createSipURI(fromName, host);
			contactURI.setPort(sipProvider.getListeningPoint(transport).getPort());

			Address contactAddress = addressFactory.createAddress(contactURI);

			// Add the contact address.
			contactAddress.setDisplayName(fromName);

			ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
			request.addHeader(contactHeader);

			// You can add extension headers of your own making
			// to the outgoing SIP request.
			// Add the extension header.
			Header extensionHeader = headerFactory.createHeader("My-Header",	"my header value");	//TODO Tang 要修
			request.addHeader(extensionHeader);

//			String sdpData = "v=0\r\n"													//TODO Tang 要修
//					+ "o=4855 13760799956958020 13760799956958020"
//					+ " IN IP4  129.6.55.78\r\n" + "s=mysession session\r\n"
//					+ "p=+46 8 52018010\r\n" + "c=IN IP4  129.6.55.78\r\n"
//					+ "t=0 0\r\n" + "m=audio 6022 RTP/AVP 0 4 18\r\n"
//					+ "a=rtpmap:0 PCMU/8000\r\n" + "a=rtpmap:4 G723/8000\r\n"
//					+ "a=rtpmap:18 G729A/8000\r\n" + "a=ptime:20\r\n";
//			byte[] contents = sdpData.getBytes();

//			request.setContent(contents, contentTypeHeader);
			request.setContent(rawContents, contentTypeHeader);
			// You can add as many extension headers as you
			// want.

			extensionHeader = headerFactory.createHeader("My-Other-Header", "my new header value ");		//TODO Tang 要修
			request.addHeader(extensionHeader);

			Header callInfoHeader = headerFactory.createHeader("Call-Info","<http://www.antd.nist.gov>");	//TODO Tang 要修
			request.addHeader(callInfoHeader);

			// Create the client transaction.
			ClientTransaction inviteTid = sipProvider.getNewClientTransaction(request);

			logger.debug("inviteTid = " + inviteTid);

			// send the request out.

//			inviteTid.sendRequest();
			
			return inviteTid;

		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
		return null;
	}
	
}
