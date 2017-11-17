package com.naturaltel.sip.component.call;

import com.naturaltel.sip.component.type.EventType;
import com.naturaltel.sip.component.type.ModeType;

/**
 * (CEM)：一通電話，各 Leg 要回報的 Event 紀錄
 *
 */
public class CallEventMonitor {
	/** IN_Dialogue_ID 0 ~ 65535 */
	int key;
	/** 0 = CLG, 1 = SRP, 2 ~ 99 = CLD */
	int leg;
	/** */
	EventType eventtType;
	/** */
	ModeType modeType;
}
