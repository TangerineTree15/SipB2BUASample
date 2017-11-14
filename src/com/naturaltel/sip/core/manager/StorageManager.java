package com.naturaltel.sip.core.manager;

import java.util.HashMap;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.ServerTransaction;
import javax.sip.address.SipURI;

import com.naturaltel.sip.component.TransactionStorage;

public interface StorageManager {

	//儲存 Register 資料
	HashMap<String, SipURI> getRegistrar();
	void setRegistrar(HashMap<String, SipURI> registrar);
	SipURI getRegistrar(String key);
	void putRegistrar(String key, SipURI sipURI);
	
	//儲存 Transaction 資料
	void saveTransaction (ServerTransaction serverTransaction);
	void saveInviteTransaction (ServerTransaction serverTransaction, ClientTransaction clientTransaction);
	void saveOtherTransaction (ServerTransaction serverTransaction, ClientTransaction clientTransaction);
	TransactionStorage loadTransaction (Dialog dialog);
}
