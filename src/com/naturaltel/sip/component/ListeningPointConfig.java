package com.naturaltel.sip.component;

import java.io.Serializable;

public class ListeningPointConfig implements Serializable {

	private static final long serialVersionUID = 7678207149924306732L;
	public String localAddress;
	public int localPort;
	public String localTransport;
	public String localDomain;
	
	
	public ListeningPointConfig(String localAddress, int localPort, String localTransport, String localDomain) {
		super();
		this.localAddress = localAddress;
		this.localPort = localPort;
		this.localTransport = localTransport;
		this.localDomain = localDomain;
	}


	@Override
	public String toString() {
		return "ListeningPointConfig [localAddress=" + localAddress + ", localPort=" + localPort + ", localTransport="
				+ localTransport + ", localDomain=" + localDomain + "]";
	}
	
}
