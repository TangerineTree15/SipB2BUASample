package com.naturaltel.sip.core.manager;

import com.naturaltel.sip.component.ListeningPointConfig;

public interface ConfigurationManager {

	ListeningPointConfig getListeningPointConfig();
	ListeningPointConfig loadListeningPointConfig(String filePath);
	void setListeningPointConfig(ListeningPointConfig listeningPointConfig);
}
