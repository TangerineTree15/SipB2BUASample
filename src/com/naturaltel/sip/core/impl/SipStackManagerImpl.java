package com.naturaltel.sip.core.impl;

import java.util.Properties;

import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.SipFactory;
import javax.sip.SipProvider;
import javax.sip.SipStack;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.naturaltel.sip.component.ListeningPointConfig;
import com.naturaltel.sip.core.manager.ConfigurationManager;
import com.naturaltel.sip.core.manager.SipManager;
import com.naturaltel.sip.core.manager.SipStackManager;

import gov.nist.javax.sip.stack.NioMessageProcessorFactory;

public class SipStackManagerImpl implements SipStackManager {

	private static SipStackManager sipStackManager;
	private Logger logger;
	
	private SipStack sipStack;
	
    public static SipStackManager getInstance() {
        if (sipStackManager == null) {
            synchronized (SipStackManager.class) {
                if (sipStackManager == null) {
                	sipStackManager = new SipStackManagerImpl();
                }
            }
        }
        return sipStackManager;
    }
    
	public SipStackManagerImpl() {
		logger = Logger.getLogger(SipStackManagerImpl.class);
		createAppender();
	}
	
	@Override
	public SipProvider addSipListener(String stackName, SipManager sipManager, ConfigurationManager configurationManager) {
		try {
			logger.debug("initSipStack");
			this.sipStack = createSipStack(stackName);
			
			ListeningPointConfig listeningPointConfig = configurationManager.getListeningPointConfig();
			logger.debug(listeningPointConfig.toString());
			ListeningPoint listeningPoint = sipStack.createListeningPoint(listeningPointConfig.localAddress, listeningPointConfig.localPort, listeningPointConfig.localTransport);
			SipProvider sipProvider = createSipProvider(sipStack, listeningPoint);
			sipProvider.addSipListener(sipManager);
			return sipProvider;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
		return null;
	}

	
	//--- private method ----------------------------------------------------------------------------
	private void createAppender() {
		//create appender
		ConsoleAppender console = new ConsoleAppender(); 
		//configure the appender
		String PATTERN = "%d [%p|%c|%C{1}][%L] %m%n";
		console.setLayout(new PatternLayout(PATTERN)); 
		console.setThreshold(Level.DEBUG);
		console.activateOptions();
		//add appender to any Logger (here is root)
		Logger.getRootLogger().addAppender(console);	//TODO 加寫入 file
		
	}
	private SipStack createSipStack(String stackName) {
		SipFactory sipFactory = createSipFactory();
		Properties properties = createProperties(stackName);
		logger.debug("createSipStack");
		
//		sipStack = createSipStack(sipFactory, properties);
		return createSipStack(sipFactory, properties);
	}
	private SipFactory createSipFactory() {
		SipFactory sipFactory = SipFactory.getInstance();
		sipFactory.setPathName("gov.nist");
		return sipFactory;
	}
	private Properties createProperties(String stackName) {
		Properties properties = new Properties();
		properties.setProperty("javax.sip.STACK_NAME", stackName);
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
	
	//--- get/set ----------------------------------------------------------------------------
	@Override
	public SipStack getSipStack() {
		return sipStack;
	}

	
}
