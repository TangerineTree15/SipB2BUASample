package com.naturaltel.sip.core.impl;

import java.util.HashMap;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.ServerTransaction;
import javax.sip.address.SipURI;

import com.naturaltel.sip.component.TransactionStorage;
import com.naturaltel.sip.core.manager.StorageManager;

public class StorageManagerImpl implements StorageManager {

	private HashMap<String, SipURI> registrar = new HashMap<String, SipURI>();

	
    private static StorageManager storageManager;

    public static StorageManager getInstance() {
        if (storageManager == null) {
            synchronized (StorageManager.class) {
                if (storageManager == null) {
                	storageManager = new StorageManagerImpl();
                }
            }
        }
        return storageManager;
    }
    
    
	@Override
	public HashMap<String, SipURI> getRegistrar() {
		return registrar;
	}

	@Override
	public void setRegistrar(HashMap<String, SipURI> registrar) {
		this.registrar = registrar;
	}

	@Override
	public SipURI getRegistrar(String key) {
		return registrar.get(key);
	}

	@Override
	public void putRegistrar(String key, SipURI sipURI) {
		registrar.put(key, sipURI);
	}
	
	@Override
	public void saveInviteTransaction(ServerTransaction serverTransaction, ClientTransaction clientTransaction) {
		if(serverTransaction==null || clientTransaction==null) return;
		Dialog serverDialog = serverTransaction.getDialog();
		Dialog clientDialog = clientTransaction.getDialog();
		
		TransactionStorage transactionStorage = new TransactionStorage(serverDialog, clientDialog, serverTransaction, clientTransaction);
	
		clientTransaction.setApplicationData(serverTransaction);
		serverTransaction.setApplicationData(clientTransaction);
		serverTransaction.getDialog().setApplicationData(transactionStorage);
		clientTransaction.getDialog().setApplicationData(transactionStorage);
	}
	@Override
	public void saveOtherTransaction(ServerTransaction serverTransaction, ClientTransaction clientTransaction) {
		if(serverTransaction==null || clientTransaction==null) return;
		clientTransaction.setApplicationData(serverTransaction);
		serverTransaction.setApplicationData(clientTransaction);

	}
	@Override
	public void saveTransaction (ServerTransaction serverTransaction) {
		if(serverTransaction==null) return;
		Dialog serverDialog = serverTransaction.getDialog();
		TransactionStorage transactionStorage = new TransactionStorage(serverDialog, serverTransaction);
		serverTransaction.getDialog().setApplicationData(transactionStorage);
	}
	@Override
	public TransactionStorage loadTransaction (Dialog dialog) {
		if(dialog!=null) {
			return (TransactionStorage)dialog.getApplicationData();
		}
		return null;
	}
	
}
