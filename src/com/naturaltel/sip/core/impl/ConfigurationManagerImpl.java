package com.naturaltel.sip.core.impl;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import com.naturaltel.sip.component.ListeningPointConfig;
import com.naturaltel.sip.core.manager.ConfigurationManager;

public class ConfigurationManagerImpl implements ConfigurationManager {

	private static ConfigurationManager configurationManager;

	private ListeningPointConfig listeningPointConfig;
	
	
	public static ConfigurationManager getInstance() {
        if (configurationManager == null) {
            synchronized (ConfigurationManagerImpl.class) {
                if (configurationManager == null) {
                	configurationManager = new ConfigurationManagerImpl();
                }
            }
        }
        return configurationManager;
    }
    
    public ConfigurationManagerImpl() {
    }
    
    
	@Override
	public ListeningPointConfig getListeningPointConfig() {
		return listeningPointConfig;
	}

	@Override
	public ListeningPointConfig loadListeningPointConfig(String filePath) {
		try {
			Gson gson = new Gson();
			JsonReader reader = new JsonReader(new FileReader(filePath));
			listeningPointConfig = gson.fromJson(reader, ListeningPointConfig.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return listeningPointConfig;
	}

	@Override
	public void setListeningPointConfig(ListeningPointConfig listeningPointConfig) {
		this.listeningPointConfig = listeningPointConfig;
	}
}
