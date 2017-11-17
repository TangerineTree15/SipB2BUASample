package com.naturaltel.sip.component.call;

import java.sql.Date;

import com.naturaltel.sip.component.type.FSMType;

/**
 * (FSM)：一通電話中，各 Leg 的狀態紀錄
 */
public class LegFSM {
	/** IN_Dialogue_ID 0 ~ 65535 */
	int key;
	/** Call_ID + From_Tag + To_Tag*/
	String cid;
	/** 0 = CLG, 1 = SRP, 2 ~ 99 = CLD */
	int leg;
	/** Leg狀態紀錄 */
	FSMType fsmType;
	/** Time to report No-Answer. 0 = 表示不管控*/
	Date NOA;

	
}
