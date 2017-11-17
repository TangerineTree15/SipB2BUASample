package com.naturaltel.sip.component.call;

import java.sql.Date;

import com.naturaltel.sip.component.type.CallType;

/**
 * (ACL)：一通電話基本資料
 *
 */
public class AliveCallList {
	
	/** 1st Inconing INVITE, Call_ID + From_Tag + To_Tag*/
	String imsCallID;
	/** (PCIDP) Peer Call ID  */
	String peerCallID;
	/** (INDID) Dialogue ID 0 ~ 65535 */
	int inDialogueID; 
	/** (ICDID) Correlation Dialogue ID of ETC or CTR,  0 ~ 65535 */
	int inCorreDID;
	/** (CT) MO(1), MT(2)  */
	CallType callType;
	/** (MLN) 1 = SRP, 2 ~ 99 = Called Party,   */
	int maxLegID;
	/** (PLID) Leg of Answered Peer 2 ~ 99*/
	int peerLegID;
	/** (SRR) 收到 SRP 發出的 BYE 時, True：需要回報, False：不要回報 */
	boolean hasToSRR;
	/** (ST) Time of receiving 1st INVITE from IMS. */
	Date startTime;
	/** (ECT) Time to cancel the call. 0 = 表示不管控*/
	Date expectedCancelTime;

}
