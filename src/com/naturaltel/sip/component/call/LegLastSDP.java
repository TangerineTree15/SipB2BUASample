package com.naturaltel.sip.component.call;

/**
 * (SDP)：一通電話中，各 Leg 最新的 SDP
 */
public class LegLastSDP {
	/** IN_Dialogue_ID 0 ~ 65535 */
	int key;
	/** 0 = CLG, 1 = SRP, 2 ~ 99 = CLD */
	int leg;
	/** JAVA Message Object for SDP*/
	String SDPs;
	
}
