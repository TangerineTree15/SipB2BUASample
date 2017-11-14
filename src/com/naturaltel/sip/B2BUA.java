package com.naturaltel.sip;

import javax.sip.*;
import javax.sip.address.*;
import javax.sip.header.*;
import javax.sip.message.*;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

//import examples.simplecallsetup.Shootist;
import gov.nist.javax.sip.stack.NioMessageProcessorFactory;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class is a B2BUA using Websocket transport. You can use any two Websocket SIP phones
 * to register into the server and call each other by the username the advertised in the REGISTER
 * request. The registrar is just storing the contacts of the users in a HashMap locally.
 * 
 * Requiring registration is the only significant difference from the usual JAIN-SIP call flow. The
 * registrations are required because a phone must be able to receive calls on the websocket while
 * being idle otherwise.
 *
 * @author Vladimir Ralev
 */
public class B2BUA implements SipListener {

	private static AddressFactory addressFactory;

	private static MessageFactory messageFactory;

	private static HeaderFactory headerFactory;

	private static SipStack sipStack;

//	private static final String myAddress = "127.0.0.1";	
	private static final String myAddress = "192.168.31.106";
//	private static final String myAddress = "192.168.0.23";

	private static final int myPort = 5082;

	private AtomicLong counter = new AtomicLong();

	private ListeningPoint listeningPoint;
	
	private SipProvider sipProvider;

	private String transport = "udp";//"ws";

	private HashMap<String, SipURI> registrar = new HashMap<String, SipURI>();

	@Override
	public void processRequest(RequestEvent requestEvent) {
		Request request = requestEvent.getRequest();
		ServerTransaction serverTransaction = requestEvent.getServerTransaction();
		System.out.println("B2BUA log: " + request.getMethod());
				
		System.out.println("\n\nRequest " + request.getMethod()
				+ " received at " + sipStack.getStackName()
				+ " with server transaction id " + serverTransaction);

		if (request.getMethod().equals(Request.INVITE)) {
			processInvite(requestEvent, serverTransaction);
		} else if (request.getMethod().equals(Request.ACK)) {
			processAck(requestEvent, serverTransaction);
		} else if (request.getMethod().equals(Request.CANCEL)) {
			processCancel(requestEvent, serverTransaction);
		} else if (request.getMethod().equals(Request.REGISTER)) {
			processRegister(requestEvent, serverTransaction);
		} else if (request.getMethod().equals(Request.SUBSCRIBE)) {
			processSubscribe(requestEvent, serverTransaction);
		} else {
			processInDialogRequest(requestEvent, serverTransaction);
		}
	}

	@Override
	public void processResponse(ResponseEvent responseEvent) {
		System.out.println("B2BUA log: " + responseEvent.getResponse().getStatusCode());
		
		Response response = responseEvent.getResponse();
		System.out.println("response StatusCode=" + response.getStatusCode());
		
		ClientTransaction clientTransaction = responseEvent.getClientTransaction();
		ServerTransaction serverTransaction;
		if(clientTransaction!=null) {
			System.out.println("clientTransaction!=null");
			serverTransaction = (ServerTransaction) clientTransaction.getApplicationData();
		} else {
			System.out.println("clientTransaction==null");
			System.out.println("getDialog==null: " + (responseEvent.getDialog()==null));
			
			return;
		}
		try {
			Response otherResponse = messageFactory.createResponse(response.getStatusCode(), serverTransaction.getRequest());
			if(response.getStatusCode() == 200 && clientTransaction.getRequest().getMethod().equals("INVITE")) {
				System.out.println("getContentLength=" + response.getContentLength());
				
////				CSeqHeader cSeqHeader = (CSeqHeader) responseEvent.getResponse().getHeader(CSeqHeader.NAME); 
////				final Request ack = responseEvent.getDialog().createAck(cSeqHeader.getSeqNumber());
////				responseEvent.getDialog().sendAck(ack);
//				
//				
////				Address address = addressFactory.createAddress("B2BUA <sip:"+ myAddress + ":" + myPort + ">");
////				ContactHeader contactHeader = headerFactory.createContactHeader(address);
////				response.addHeader(contactHeader);
//				ToHeader toHeader = (ToHeader) otherResponse.getHeader(ToHeader.NAME);
//				if(toHeader.getTag() == null) toHeader.setTag(new Long(counter.getAndIncrement()).toString());
////				otherResponse.addHeader(contactHeader);
//				otherResponse.addHeader(response.getHeader(ContactHeader.NAME));
////				otherResponse.setContentDisposition(response.getContentDisposition());
////				otherResponse.setContentEncoding(response.getContentEncoding());
////				otherResponse.setContentLanguage(response.getContentLanguage());
//				
//				ContentTypeHeader contentTypeHeader = headerFactory
//						.createContentTypeHeader("application", "sdp");
//				otherResponse.setContent(response.getContent(), contentTypeHeader);
				
				Address address = addressFactory.createAddress("B2BUA <sip:"
						+ myAddress + ":" + myPort + ">");
				ContactHeader contactHeader = headerFactory.createContactHeader(address);
				response.addHeader(contactHeader);
				ToHeader toHeader = (ToHeader) otherResponse.getHeader(ToHeader.NAME);
				if(toHeader.getTag() == null) toHeader.setTag(new Long(counter.getAndIncrement()).toString());
				otherResponse.addHeader(contactHeader);
				
				ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application", "sdp");
				otherResponse.setContent(response.getContent(), contentTypeHeader);
			}
			serverTransaction.sendResponse(otherResponse);
			System.out.println("sendResponse StatusCode=" + otherResponse.getStatusCode());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Process the ACK request, forward it to the other leg.
	 */
	public void processAck(RequestEvent requestEvent, ServerTransaction serverTransaction) {
		try {
			Dialog dialog = serverTransaction.getDialog();
			System.out.println("b2bua: got an ACK! ");
			System.out.println("Dialog State = " + dialog.getState());
			Dialog otherDialog = (Dialog) dialog.getApplicationData();
			System.out.println("otherDialog getLocalSeqNumber = " + otherDialog.getLocalSeqNumber());
			Request request = otherDialog.createAck(otherDialog.getLocalSeqNumber());
			otherDialog.sendAck(request);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Process the invite request.
	 */
	public void processInvite(RequestEvent requestEvent,
			ServerTransaction serverTransaction) {
		SipProvider sipProvider = (SipProvider) requestEvent.getSource();
		Request request = requestEvent.getRequest();
		try {
			System.out.println("b2bua: got an Invite sending Trying");
			//ServerTransaction serverTransaction = requestEvent.getServerTransaction();
			if(serverTransaction == null) {
				System.out.println("getNewServerTransaction");
				serverTransaction = sipProvider.getNewServerTransaction(request);
			}
			Dialog dialog = serverTransaction.getDialog();

			FromHeader from = (FromHeader) request.getHeader(FromHeader.NAME);
			SipURI fromUri = (SipURI) from.getAddress().getURI();	
			ToHeader to = (ToHeader) request.getHeader(ToHeader.NAME);
			SipURI toUri = (SipURI) to.getAddress().getURI();
			SipURI target = registrar.get(toUri.getUser());
			
			if(target == null) {
				System.out.println("User " + toUri + " is not registered.");
				throw new RuntimeException("User not registered " + toUri);
			} else {
				System.out.println("User " + toUri + " is registered.");
				System.out.println("Target " + target);
				ClientTransaction clientTransaction = call(fromUri, target, request.getRawContent());
				clientTransaction.setApplicationData(serverTransaction);
				serverTransaction.setApplicationData(clientTransaction);
				serverTransaction.getDialog().setApplicationData(clientTransaction.getDialog());
				clientTransaction.getDialog().setApplicationData(serverTransaction.getDialog());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
//			junit.framework.TestCase.fail("Exit JVM");
		}
	}

	public void processSubscribe(RequestEvent requestEvent,
			ServerTransaction serverTransactionId) {
		Request request = requestEvent.getRequest();
		System.out.println("Method=" + request.getMethod());
		System.out.println("RequestURI Scheme=" + request.getRequestURI().getScheme());
		try {
			Response response = this.messageFactory.createResponse(200, request);
            // Both 2xx response to SUBSCRIBE and NOTIFY need a Contact
//            response.addHeader(sipPresenceAgent.getConfiguration().getContactHeader());
			ContactHeader contactHeader = (ContactHeader) request.getHeader(ContactHeader.NAME);
			contactHeader.setExpires(request.getExpires().getExpires());
			contactHeader.setParameter("received", request.getRequestURI().toString());
            response.addHeader(contactHeader);

            // Expires header is mandatory in 2xx responses to SUBSCRIBE
			response.addHeader(request.getExpires());
			ServerTransaction serverTransaction = sipProvider.getNewServerTransaction(request);
			serverTransaction.sendResponse(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * Process the any in dialog request - MESSAGE, BYE, INFO, UPDATE.
	 */
	public void processInDialogRequest(RequestEvent requestEvent,
			ServerTransaction serverTransactionId) {
		SipProvider sipProvider = (SipProvider) requestEvent.getSource();
		Request request = requestEvent.getRequest();
		Dialog dialog = requestEvent.getDialog();
		System.out.println("Method=" + request.getMethod());
		if(dialog==null) {
			System.out.println("dialog is null");
		} else {
			System.out.println("local party = " + dialog.getLocalParty());
		}
		if(serverTransactionId==null) {
			System.out.println("serverTransactionId is null");
		} else {
			System.out.println("ServerTransaction getState = " + serverTransactionId.getState());
		}
		
		try {
			//System.out.println("b2bua:  got a bye sending OK.");
			Response response = messageFactory.createResponse(200, request);
			serverTransactionId.sendResponse(response);
			System.out.println("Dialog State is "
					+ serverTransactionId.getDialog().getState());
			
			Dialog otherLeg = (Dialog) dialog.getApplicationData();
			Request otherBye = otherLeg.createRequest(request.getMethod());
			ClientTransaction clientTransaction = sipProvider.getNewClientTransaction(otherBye);
			clientTransaction.setApplicationData(serverTransactionId);
			serverTransactionId.setApplicationData(clientTransaction);
			otherLeg.sendRequest(clientTransaction);

		} catch (Exception ex) {
			ex.printStackTrace();
//			junit.framework.TestCase.fail("Exit JVM");

		}
	}
	public void processRegister(RequestEvent requestEvent,
			ServerTransaction serverTransactionId) {
		Request request = requestEvent.getRequest();
		ContactHeader contact = (ContactHeader) request.getHeader(ContactHeader.NAME);
		SipURI contactUri = (SipURI) contact.getAddress().getURI();
		FromHeader from = (FromHeader) request.getHeader(FromHeader.NAME);
		SipURI fromUri = (SipURI) from.getAddress().getURI();
		registrar.put(fromUri.getUser(), contactUri);
		System.out.println("User=" + fromUri.getUser() + ", contactUri=" + contactUri);
		System.out.println("registrar.size=" + registrar.size());
		System.out.println("RequestURI Scheme=" + request.getRequestURI().toString());
		try {
			Response response = this.messageFactory.createResponse(200, request);
			ContactHeader contactHeader = (ContactHeader) request.getHeader(ContactHeader.NAME);
			contactHeader.setExpires(3600);
			contactHeader.setParameter("received", request.getRequestURI().toString());
            response.addHeader(contactHeader);
            
			ServerTransaction serverTransaction = sipProvider.getNewServerTransaction(request);
			serverTransaction.sendResponse(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void processCancel(RequestEvent requestEvent,
			ServerTransaction serverTransactionId) {}

	@Override
	public void processTimeout(javax.sip.TimeoutEvent timeoutEvent) {
		Transaction transaction;
		if (timeoutEvent.isServerTransaction()) {
			transaction = timeoutEvent.getServerTransaction();
		} else {
			transaction = timeoutEvent.getClientTransaction();
		}
		System.out.println("state = " + transaction.getState());
		System.out.println("dialog = " + transaction.getDialog());
		System.out.println("dialogState = "
				+ transaction.getDialog().getState());
		System.out.println("Transaction Time out");
	}

	public void init() {
		System.out.println("init");
		
		ConsoleAppender console = new ConsoleAppender(); //create appender
		//configure the appender
		String PATTERN = "%d [%p|%c|%C{1}] %m%n";
		console.setLayout(new PatternLayout(PATTERN)); 
		console.setThreshold(Level.DEBUG);
		console.activateOptions();
		//add appender to any Logger (here is root)
		Logger.getRootLogger().addAppender(console);
		SipFactory sipFactory = null;
		sipStack = null;
		sipFactory = SipFactory.getInstance();
		sipFactory.setPathName("gov.nist");
		Properties properties = new Properties();
		properties.setProperty("javax.sip.STACK_NAME", "shootme");
		// You need 16 for logging traces. 32 for debug + traces.
		// Your code will limp at 32 but it is best for debugging.
		properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "LOG4J");
		properties.setProperty("gov.nist.javax.sip.DEBUG_LOG",
				"shootmedebug.txt");
		properties.setProperty("gov.nist.javax.sip.SERVER_LOG",
				"shootmelog.txt");
		properties.setProperty("gov.nist.javax.sip.MESSAGE_PROCESSOR_FACTORY", NioMessageProcessorFactory.class.getName());

		try {
			// Create SipStack object
			sipStack = sipFactory.createSipStack(properties);
			System.out.println("sipStack = " + sipStack);
		} catch (PeerUnavailableException e) {
			// could not find
			// gov.nist.jain.protocol.ip.sip.SipStackImpl
			// in the classpath
			e.printStackTrace();
			System.err.println(e.getMessage());
			if (e.getCause() != null)
				e.getCause().printStackTrace();
//			junit.framework.TestCase.fail("Exit JVM");
		}

		try {
			headerFactory = sipFactory.createHeaderFactory();
			addressFactory = sipFactory.createAddressFactory();
			messageFactory = sipFactory.createMessageFactory();
			this.listeningPoint = sipStack.createListeningPoint(myAddress,
					myPort, transport);

			B2BUA listener = this;

			sipProvider = sipStack.createSipProvider(listeningPoint);
			System.out.println("ws provider " + sipProvider);
			sipProvider.addSipListener(listener);

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}

//	public static void main(String args[]) {
//		new B2BUA().init();
//	}

	@Override
	public void processIOException(IOExceptionEvent exceptionEvent) {
		System.out.println("IOException");

	}

	@Override
	public void processTransactionTerminated(
			TransactionTerminatedEvent transactionTerminatedEvent) {
		if (transactionTerminatedEvent.isServerTransaction())
			System.out.println("Transaction terminated event recieved"
					+ transactionTerminatedEvent.getServerTransaction());
		else
			System.out.println("Transaction terminated "
					+ transactionTerminatedEvent.getClientTransaction());

	}

	@Override
	public void processDialogTerminated(
			DialogTerminatedEvent dialogTerminatedEvent) {
		System.out.println("Dialog terminated event recieved");
		Dialog d = dialogTerminatedEvent.getDialog();
		System.out.println("Local Party = " + d.getLocalParty());

	}

	public ClientTransaction call(SipURI formSipURI, SipURI destination, byte[] rawContents) {
		try {

//			String fromName = "B2BUA";
//			String fromSipAddress = "here.com";
//			String fromDisplayName = "B2BUA";
			
			String fromName = formSipURI.getUser();
			String fromSipAddress = formSipURI.getHost();
			String fromDisplayName = "";

			String toSipAddress = "there.com";
			String toUser = "Target";
			String toDisplayName = "Target";

			// create >From Header
			SipURI fromAddress = addressFactory.createSipURI(fromName, fromSipAddress);

//			Address fromNameAddress = addressFactory.createAddress(fromAddress);
			Address fromNameAddress = addressFactory.createAddress(formSipURI);
			fromNameAddress.setDisplayName(fromDisplayName);
			FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress, new Long(counter.getAndIncrement()).toString());

			// create To Header
			SipURI toAddress = addressFactory.createSipURI(toUser, toSipAddress);
//			Address toNameAddress = addressFactory.createAddress(toAddress);
			Address toNameAddress = addressFactory.createAddress(destination);
			toNameAddress.setDisplayName(toDisplayName);
			ToHeader toHeader = headerFactory.createToHeader(toNameAddress, null);

			// create Request URI
			SipURI requestURI = destination;

			// Create ViaHeaders

			ArrayList viaHeaders = new ArrayList();
			String ipAddress = listeningPoint.getIPAddress();
			ViaHeader viaHeader = headerFactory.createViaHeader(ipAddress,
					sipProvider.getListeningPoint(transport).getPort(),
					transport, null);

			// add via headers
			viaHeaders.add(viaHeader);

			// Create ContentTypeHeader
			ContentTypeHeader contentTypeHeader = headerFactory
					.createContentTypeHeader("application", "sdp");

			// Create a new CallId header
			CallIdHeader callIdHeader = sipProvider.getNewCallId();

			// Create a new Cseq header
			CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1L,
					Request.INVITE);

			// Create a new MaxForwardsHeader
			MaxForwardsHeader maxForwards = headerFactory
					.createMaxForwardsHeader(70);

			// Create the request.
			Request request = messageFactory.createRequest(requestURI,
					Request.INVITE, callIdHeader, cSeqHeader, fromHeader,
					toHeader, viaHeaders, maxForwards);
			// Create contact headers
			//String host = "127.0.0.1";
			String host = myAddress;

			SipURI contactUrl = addressFactory.createSipURI(fromName, host);
			contactUrl.setPort(listeningPoint.getPort());
			contactUrl.setLrParam();

			// Create the contact name address.
			SipURI contactURI = addressFactory.createSipURI(fromName, host);
			contactURI.setPort(sipProvider.getListeningPoint(transport)
					.getPort());

			Address contactAddress = addressFactory.createAddress(contactURI);

			// Add the contact address.
			contactAddress.setDisplayName(fromName);

			ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
			request.addHeader(contactHeader);

			// You can add extension headers of your own making
			// to the outgoing SIP request.
			// Add the extension header.
			Header extensionHeader = headerFactory.createHeader("My-Header",
					"my header value");
			request.addHeader(extensionHeader);

			String sdpData = "v=0\r\n"
					+ "o=4855 13760799956958020 13760799956958020"
					+ " IN IP4  129.6.55.78\r\n" + "s=mysession session\r\n"
					+ "p=+46 8 52018010\r\n" + "c=IN IP4  129.6.55.78\r\n"
					+ "t=0 0\r\n" + "m=audio 6022 RTP/AVP 0 4 18\r\n"
					+ "a=rtpmap:0 PCMU/8000\r\n" + "a=rtpmap:4 G723/8000\r\n"
					+ "a=rtpmap:18 G729A/8000\r\n" + "a=ptime:20\r\n";
			byte[] contents = sdpData.getBytes();

//			request.setContent(contents, contentTypeHeader);
			request.setContent(rawContents, contentTypeHeader);
			// You can add as many extension headers as you
			// want.

			extensionHeader = headerFactory.createHeader("My-Other-Header",
					"my new header value ");
			request.addHeader(extensionHeader);

			Header callInfoHeader = headerFactory.createHeader("Call-Info",
					"<http://www.antd.nist.gov>");
			request.addHeader(callInfoHeader);

			// Create the client transaction.
			ClientTransaction inviteTid = sipProvider.getNewClientTransaction(request);

			System.out.println("inviteTid = " + inviteTid);

			// send the request out.

			inviteTid.sendRequest();
			
			return inviteTid;

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
		return null;
	}

	
}
