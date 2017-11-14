package com.naturaltel.sip.core.manager;

import com.naturaltel.sip.component.ConnectCalleeUsers;
import com.naturaltel.sip.component.OriginCalleeUser;

public interface CallManager {

	ConnectCalleeUsers getConnectCallee(OriginCalleeUser originCalleeUser);
}
