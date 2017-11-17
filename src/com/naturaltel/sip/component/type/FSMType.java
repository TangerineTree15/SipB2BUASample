package com.naturaltel.sip.component.type;

/**
 * Leg狀態紀錄
 */
public enum FSMType {
	Idle(0), 
	// [1X: SRP Only]
	Etc(10), EtcAns(11), Ari(12), Pa(13), Srr(14),
	// [2X: CLD Only]
	IcaNew(20), IcaCue(21), IcaAns(22), IcaRec(24),
	// [3X]
	Con(30),				//(CLD Only)
	Ann(31),				//(CLG Only)
	Ans(32), Acked(33),
	// [4X]
	AbandonRlsErp(40),	//等 SCP 通知
	RlsDfcOk(41),			//尚需轉送 200 OK
	RlsDfcEnd(42),			//已結束
	;
	
	int value;
	FSMType(int value) {
		this.value = value;
	}

	public int getValue() {
        return value;
    }

    public static FSMType lookup(final int value) {
    	FSMType returnObject = null;
        for (FSMType tempObject : FSMType.values()) {
            if (value == tempObject.getValue() ) {
                returnObject = tempObject;
                break;
            }
        }
        return returnObject;
    }

    public static FSMType lookup(final String valueString) {
        try {
            int value = Integer.parseInt(valueString);
            return lookup(value);
        } catch (Exception e) {
        	FSMType returnObject = null;
            if(valueString!=null) {
                for (FSMType tempObject : FSMType.values()) {
                    if (tempObject.toString().equalsIgnoreCase(valueString) ) {
                        returnObject = tempObject;
                        break;
                    }
                }
            }
            return returnObject;
        }
    }
}
