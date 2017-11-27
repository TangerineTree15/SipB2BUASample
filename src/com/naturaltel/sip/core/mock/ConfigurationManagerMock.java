package com.naturaltel.sip.core.mock;

//import org.apache.log4j.Logger;

import com.naturaltel.sip.component.ListeningPointConfig;
import com.naturaltel.sip.core.manager.ConfigurationManager;

public class ConfigurationManagerMock implements ConfigurationManager {
	
//	private Logger logger;
	
	private static ConfigurationManager configurationManager;
	
	private ListeningPointConfig listeningPointConfig;

	public static ConfigurationManager getInstance() {
        if (configurationManager == null) {
            synchronized (ConfigurationManagerMock.class) {
                if (configurationManager == null) {
                	configurationManager = new ConfigurationManagerMock();
                }
            }
        }
        return configurationManager;
    }
    
    public ConfigurationManagerMock() {
//		logger = Logger.getLogger(ConfigurationManagerMock.class);
    }

	@Override
	public ListeningPointConfig getListeningPointConfig() {
		listeningPointConfig = new ListeningPointConfig("192.168.31.106", 5082, "udp");
		return listeningPointConfig;
	}

	@Override
	public ListeningPointConfig loadListeningPointConfig(String filePath) {
//		logger.debug("loadListeningPointConfig filePath=" + filePath);
		listeningPointConfig = new ListeningPointConfig("192.168.31.106", 5082, "udp");
		return listeningPointConfig;
	}

	@Override
	public void setListeningPointConfig(ListeningPointConfig listeningPointConfig) {
		this.listeningPointConfig = listeningPointConfig;
	}
    
    
    
}
