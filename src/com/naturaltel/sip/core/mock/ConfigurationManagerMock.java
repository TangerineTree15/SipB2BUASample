package com.naturaltel.sip.core.mock;

//import org.apache.log4j.Logger;

import com.naturaltel.sip.component.ListeningPointConfig;
import com.naturaltel.sip.core.manager.ConfigurationManager;

public class ConfigurationManagerMock implements ConfigurationManager {
	
//	private Logger logger;
	
	private static ConfigurationManager configurationManager;
	
	private ListeningPointConfig listeningPointConfig;

	public static ConfigurationManager getInstance(String filePath) {
        if (configurationManager == null) {
            synchronized (ConfigurationManagerMock.class) {
                if (configurationManager == null) {
                	configurationManager = new ConfigurationManagerMock(filePath);
                }
            }
        }
        return configurationManager;
    }
    
    public ConfigurationManagerMock(String filePath) {
//		logger = Logger.getLogger(ConfigurationManagerMock.class);
    		loadListeningPointConfig(filePath);
    }

	@Override
	public ListeningPointConfig getListeningPointConfig() {
		listeningPointConfig = new ListeningPointConfig("192.168.31.106", 5082, "udp", "ec01sbaporg.ims.mnc002.mcc466.3gppnetwork.org");
		return listeningPointConfig;
	}

	@Override
	public ListeningPointConfig loadListeningPointConfig(String filePath) {
//		logger.debug("loadListeningPointConfig filePath=" + filePath);
		listeningPointConfig = new ListeningPointConfig("192.168.31.106", 5082, "udp", "ec01sbaporg.ims.mnc002.mcc466.3gppnetwork.org");
		return listeningPointConfig;
	}

	@Override
	public void setListeningPointConfig(ListeningPointConfig listeningPointConfig) {
		this.listeningPointConfig = listeningPointConfig;
	}
    
    
    
}
