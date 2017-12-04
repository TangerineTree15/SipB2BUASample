package com.naturaltel.sip.component;

import java.util.ArrayList;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.ServerTransaction;

/**
 * 儲存 Dialog, ServerTransaction clientTransactionArray 互存內容
 * get/set/clear/add/remove
 */
public class TransactionStorage {
	Dialog serverDialog;
	Dialog clientDialog;
	ServerTransaction serverTransaction;
	ArrayList<ClientTransaction> clientTransactionArray = new ArrayList<ClientTransaction>();
	
	
	public TransactionStorage(Dialog serverDialog, Dialog clientDialog, ServerTransaction serverTransaction,
			ArrayList<ClientTransaction> clientTransactionArray) {
		super();
		this.serverDialog = serverDialog;
		this.clientDialog = clientDialog;
		this.serverTransaction = serverTransaction;
		this.clientTransactionArray = clientTransactionArray;
	}
	public TransactionStorage(Dialog serverDialog, Dialog clientDialog, ServerTransaction serverTransaction,
			ClientTransaction clientTransaction) {
		super();
		this.serverDialog = serverDialog;
		this.clientDialog = clientDialog;			//TODO clientDialog 應該會有好幾個，要修改
		this.serverTransaction = serverTransaction;
		addClientTransaction(clientTransaction);
	}
	public TransactionStorage(Dialog serverDialog, ServerTransaction serverTransaction) {
		super();
		this.serverDialog = serverDialog;
		this.serverTransaction = serverTransaction;
	}
	
	public Dialog getServerDialog() {
		return serverDialog;
	}
	public void setServerDialog(Dialog serverDialog) {
		this.serverDialog = serverDialog;
	}
	public Dialog getClientDialog() {
		return clientDialog;
	}
	public void setClientDialog(Dialog clientDialog) {
		this.clientDialog = clientDialog;
	}
	public Dialog getOtherDialog(Dialog dialog) {
		if(dialog==null) return null;
		if(dialog.equals(clientDialog)) return serverDialog;
		return clientDialog;
	}
	public ServerTransaction getServerTransaction() {
		return serverTransaction;
	}
	public void setServerTransaction(ServerTransaction serverTransaction) {
		this.serverTransaction = serverTransaction;
	}
	public ArrayList<ClientTransaction> getClientTransaction() {
		return clientTransactionArray;
	}
	public void setClientTransaction(ArrayList<ClientTransaction> clientTransactionArray) {
		this.clientTransactionArray = clientTransactionArray;
	};
	public void clearClientTransaction() {
		this.clientTransactionArray = new ArrayList<ClientTransaction>();
	};
	public void addClientTransaction(ClientTransaction clientTransaction) {
		if(!clientTransactionArray.contains(clientTransaction)) {
			clientTransactionArray.add(clientTransaction);
		}
	};
	public void removeClientTransaction(ClientTransaction clientTransaction) {
		if(clientTransactionArray.contains(clientTransaction)) {
			clientTransactionArray.remove(clientTransaction);
		}
	};
	
	
}
