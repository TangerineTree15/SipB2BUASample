package com.naturaltel.sip.component.call;

import com.naturaltel.sip.component.type.MsgType;

/**
 * (CMD)：一通電話中，主要訊息暫存區
 *
 */
public class CallMessageData {
	/** IN_Dialogue_ID 0 ~ 65535 */
	int key;
	/** */
	MsgType msgType;
	/** 0 = CLG, 1 = SRP, 2 ~ 99 = CLD */
	int leg;
	/** JAVA Message Object */
	String Data;
}
