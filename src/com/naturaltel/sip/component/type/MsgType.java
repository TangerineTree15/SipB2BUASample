package com.naturaltel.sip.component.type;

public enum MsgType {

	In_INVITE(0),
	InitialDP(1),
	CTR(2),
	ETC(3),
	ICA(4),
	CON(5),
	ETC_INVITE(6),
	CA_INVITE(7),
	CON_INVITE(8),
	RINGING183(9),
	ClientFailure4XX(10), //(Only Leg 2)
	INVITE200(11),
	PendingUPDATE(12),
	;
	
	int value;
	MsgType(int value) {
		this.value = value;
	}

	public int getValue() {
        return value;
    }

    public static MsgType lookup(final int value) {
    	MsgType returnObject = null;
        for (MsgType tempObject : MsgType.values()) {
            if (value == tempObject.getValue() ) {
                returnObject = tempObject;
                break;
            }
        }
        return returnObject;
    }

    public static MsgType lookup(final String valueString) {
        try {
            int value = Integer.parseInt(valueString);
            return lookup(value);
        } catch (Exception e) {
        	MsgType returnObject = null;
            if(valueString!=null) {
                for (MsgType tempObject : MsgType.values()) {
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
