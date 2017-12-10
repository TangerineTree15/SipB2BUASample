package com.naturaltel.sip.core.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

import javax.sip.SipProvider;
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
import javax.sip.header.RouteHeader;
import javax.sip.header.SupportedHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.UserAgentHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;

import org.apache.log4j.Logger;

import com.naturaltel.sip.component.ListeningPointConfig;
import com.naturaltel.sip.core.manager.LegManager;

import gov.nist.javax.sip.header.HeaderFactoryImpl;
import gov.nist.javax.sip.header.SIPHeader;
import gov.nist.javax.sip.header.ims.PAccessNetworkInfoHeader;
import gov.nist.javax.sip.header.ims.PAssertedIdentityHeader;
import gov.nist.javax.sip.header.ims.PPreferredService;
import gov.nist.javax.sip.header.ims.PPreferredServiceHeader;
import gov.nist.javax.sip.header.ims.PServedUser;
import gov.nist.javax.sip.header.ims.PServedUserHeader;

public class LegManagerImpl implements LegManager {

	private Logger logger;
	
	protected SipProvider sipProvider;
	protected AddressFactory addressFactory;
	protected MessageFactory messageFactory;
	protected HeaderFactory headerFactory;
	
	protected ListeningPointConfig listeningPointConfig;
	
	public String transport = "udp";
	
	public LegManagerImpl(SipProvider sipProvider, AddressFactory addressFactory, 
			MessageFactory messageFactory, HeaderFactory headerFactory, 
			ListeningPointConfig listeningPointConfig) {
		logger = Logger.getLogger(LegManagerImpl.class);
		
		this.sipProvider = sipProvider;
		this.addressFactory = addressFactory;
		this.messageFactory = messageFactory;
		this.headerFactory = headerFactory;
		this.listeningPointConfig = listeningPointConfig;
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Request createOtherInviteRequest(Request oriRequest, SipURI formSipURI, URI destination,
			byte[] rawContents) {

		try {
			//Get oriRequest Data
//			String ipAddress = sipProvider.getListeningPoint(listeningPointConfig.localTransport).getIPAddress();
//			int port = sipProvider.getListeningPoint(listeningPointConfig.localTransport).getPort();
			String ipAddress = "192.168.31.106";
			int port = 5082;
			logger.debug("ipAddress=" + ipAddress + ", port=" + port);
			ViaHeader oriViaH = (ViaHeader) oriRequest.getHeader(ViaHeader.NAME);
			logger.debug("oriViaH.getHost=" + oriViaH.getHost() + ", oriViaH.getPort()=" + oriViaH.getPort());
			logger.debug(oriRequest.getHeader("P-Visited-Network-ID"));
			MaxForwardsHeader oriMaxForwardsHeader = (MaxForwardsHeader) oriRequest.getHeader(MaxForwardsHeader.NAME);
			
			// create Request URI
			URI requestURI;
			if(destination.isSipURI()) {
				requestURI = destination;
			} else {
				//TelURI
				//tel:+886944300377 SIP/2.0
				TelURL requestUrl = ((TelURL) destination);
				String callee = "tel:+" + requestUrl.getPhoneNumber();
				
				logger.debug(callee);
				requestURI = addressFactory.createURI(callee);
			}
			
			// create From Header
			String fromName = formSipURI.getUser();
			Address fromNameAddress = addressFactory.createAddress(formSipURI);
			//TODO 產生 Tag
			String tag = "p65545t1476958110m606352c32067s1_461891541-248714762";
			FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress, tag);

			// create To Header
			Address toNameAddress = addressFactory.createAddress(destination);
			ToHeader toHeader = headerFactory.createToHeader(toNameAddress, null);

			// Create ViaHeaders
			ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
			ViaHeader viaHeader = headerFactory.createViaHeader(ipAddress, port, transport, null);
			// add via headers
			viaHeaders.add(viaHeader);

			// Create a new CallId header
			CallIdHeader callIdHeader = sipProvider.getNewCallId();
			
			// Create a new Cseq header
			CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1L, Request.INVITE);
			
			// Create ContentTypeHeader
			ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application", "sdp");

			// Create a new MaxForwardsHeader
			MaxForwardsHeader maxForwards = oriMaxForwardsHeader;
			maxForwards.decrementMaxForwards();


			// Create the Request.
			Request request = messageFactory.createRequest(requestURI,
					Request.INVITE, callIdHeader, cSeqHeader, fromHeader,
					toHeader, viaHeaders, maxForwards);
			
			// Add contentTypeHeader
			request.setContent(rawContents, contentTypeHeader);
			
			
			//TODO 可以用 HeaderFactoryExt createPAssertedIdentityHeader ...
			//Customized Header
			Header extensionHeader;
			try {
				//Add ContactHeader
				SipURI contactURI = addressFactory.createSipURI(fromName, ipAddress);
				contactURI.setPort(sipProvider.getListeningPoint(transport).getPort());
				Address contactAddress = addressFactory.createAddress(contactURI);
				ContactHeader contactHeader = (ContactHeader)oriRequest.getHeader(ContactHeader.NAME);
				contactHeader.setAddress(contactAddress);
				request.addHeader(contactHeader);
				
				
				//Add P-Served-User
				PServedUserHeader pServedUserHeader = (PServedUserHeader)oriRequest.getHeader(PServedUser.NAME);
				//logger.debug(((SIPHeader) pServedUserHeader).getHeaderName() + ": " + ((SIPHeader) pServedUserHeader).getHeaderValue());
				//logger.debug(pServedUserHeader.getSessionCase() + ", " + pServedUserHeader.getRegistrationState());
				request.addHeader(headerFactory.createHeader(((SIPHeader) pServedUserHeader).getHeaderName(), ((SIPHeader) pServedUserHeader).getHeaderValue()));
				
				//P-Preferred-Service
				PPreferredServiceHeader pPreferredServiceHeader = (PPreferredServiceHeader)oriRequest.getHeader(PPreferredService.NAME);
				//request.addHeader((Header)pPreferredServiceHeader);
				HeaderFactoryImpl himpl = new HeaderFactoryImpl();
				himpl.createPPreferredServiceHeader();
			    PPreferredServiceHeader ppsh = himpl.createPPreferredServiceHeader();
			    //logger.debug(pPreferredServiceHeader.getSubserviceIdentifiers().trim());
			    ppsh.setSubserviceIdentifiers(pPreferredServiceHeader.getSubserviceIdentifiers().trim());
			    request.addHeader(ppsh);

			    //User-Agent: Ericsson MTAS -  CXP9020729/8 R6AF01
			    ArrayList<String> ua = new ArrayList<String>();
			    ua.add("SB");
			    UserAgentHeader userAgentHeader = headerFactory.createUserAgentHeader(ua);
			    request.addHeader(userAgentHeader);
			    
				ListIterator<SIPHeader> sipHeaders;
				//Add Route
				sipHeaders = oriRequest.getHeaders(RouteHeader.NAME);
				while(sipHeaders.hasNext()) {
					SIPHeader sipHeader = sipHeaders.next();
					logger.debug(sipHeader.getName() + ":" + sipHeader.getHeaderValue());
					String headerValue = sipHeader.getHeaderValue();
					if(headerValue != null && !headerValue.contains(listeningPointConfig.localDomain)) {
						RouteHeader routeHeader = (RouteHeader) sipHeader;
						request.addHeader(headerFactory.createRouteHeader(routeHeader.getAddress()));
					}
				}
				
				//copy AllowHeader
				sipHeaders = oriRequest.getHeaders(AllowHeader.NAME);
				while(sipHeaders.hasNext()) {
					SIPHeader sipHeader = sipHeaders.next();
					//logger.debug(sipHeader.getHeaderName() + ": " + sipHeader.getHeaderValue());
					request.addHeader(headerFactory.createAllowHeader(sipHeader.getHeaderValue()));
				}
				//copy SupportedHeader
				sipHeaders = oriRequest.getHeaders(SupportedHeader.NAME);
				while(sipHeaders.hasNext()) {
					SIPHeader sipHeader = sipHeaders.next();
					//logger.debug(sipHeader.getHeaderName() + ": " + sipHeader.getHeaderValue());
					request.addHeader(headerFactory.createSupportedHeader(sipHeader.getHeaderValue()));
				}
				//copy AcceptHeader
				sipHeaders = oriRequest.getHeaders(AcceptHeader.NAME);
				while(sipHeaders.hasNext()) {
					SIPHeader sipHeader = sipHeaders.next();
					//logger.debug(sipHeader.getHeaderName() + ": " + sipHeader.getHeaderValue());
					AcceptHeader acceptHeader = (AcceptHeader)sipHeader;
					request.addHeader(acceptHeader);
				}
				
				//copy P-Access-Network-Info
				sipHeaders = oriRequest.getHeaders(PAccessNetworkInfoHeader.NAME);
				while(sipHeaders.hasNext()) {
					SIPHeader sipHeader = sipHeaders.next();
					logger.debug(sipHeader.getHeaderName() + ": " + sipHeader.getHeaderValue());
					request.addHeader(headerFactory.createHeader(sipHeader.getHeaderName(), sipHeader.getHeaderValue()));
				}
				//copy P-Asserted-Identity
				sipHeaders = oriRequest.getHeaders(PAssertedIdentityHeader.NAME);
				while(sipHeaders.hasNext()) {
					SIPHeader sipHeader = sipHeaders.next();
					logger.debug(sipHeader.getHeaderName() + ": " + sipHeader.getHeaderValue());
					request.addHeader(headerFactory.createHeader(sipHeader.getHeaderName(), sipHeader.getHeaderValue()));
				}
				
			} catch(Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
			
			
		     //copy other Headers
			//TODO 修改 From -> FromHeader.NAME ... 等等
	        String[] hhh = {FromHeader.NAME, "To", "Via", "Call-ID", "CSeq", "Max-Forwards", "Content-Type", "Content-Length", "Contact", 
	        		
	        		RouteHeader.NAME, PServedUserHeader.NAME, PPreferredService.NAME, 
	        		AllowHeader.NAME, SupportedHeader.NAME, AcceptHeader.NAME, PAccessNetworkInfoHeader.NAME, PAssertedIdentityHeader.NAME, 
	        		};
			try {
				ListIterator<String> headers = oriRequest.getHeaderNames();
				while(headers.hasNext()) {
					String headerName = headers.next();
					if(!Arrays.asList(hhh).contains(headerName)) {
						logger.debug("RequestHeader: " + headerName +"="+ oriRequest.getHeader(headerName));
						extensionHeader = oriRequest.getHeader(headerName);
						request.addHeader(extensionHeader);
					}
				}
			} catch(Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
			
	        logger.debug("request=" + request.toString());

			return request;
		} catch (Exception ex) {
			logger.error(ex.toString());
			ex.printStackTrace();
		}
		return null;
	}

}
