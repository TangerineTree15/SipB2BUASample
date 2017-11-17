package com.naturaltel.sip.component.type;

public enum EventType {

	//以下不限 MO or MT
	OrigAttemptAuthorized(1),
	CollectedInfo(2),
	AnalyzedInformation(3),
	RouteSelectFailure(4),	//指未指定下的預設 Leg ID
	//以下限 MO。
	OCalledPartyBusy(5), 	//指未指定下的預設 Leg ID
	Oalerting(-1),			//指未指定下的預設 Leg ID
	OCalledPartyNotReachable(-1),	//指未指定下的預設 Leg ID
	ONoAnswer(6),			//指未指定下的預設 Leg ID
	OAnswer(7),				//指未指定下的預設 Leg ID
	OMidCall(8),
	OSuspended(-3),
	OReAnswer(-4),
	ODisconnect(9),
	OAbandon(10),
	//以下限 MT
	TermAttemptAuthorized(12),
	TRouteSelectFailure(-5),
	TCalledPartyBusy(13),	//指未指定下的預設 Leg ID
	Talerting(-6),			//指未指定下的預設 Leg ID
	TCalledPartyNotReachable(-7),	//指未指定下的預設 Leg ID
	TNoAnswer(14),			//指未指定下的預設 Leg ID
	TAnswer(15),				//指未指定下的預設 Leg ID
	TMidCall(16),
	TSuspended(-8),
	TReAnswer(-9),
	TDisconnect(17),
	TAbandon(18),
	;
	
	int value;
	EventType(int value) {
		this.value = value;
	}

	public int getValue() {
        return value;
    }

    public static EventType lookup(final int value) {
    	EventType returnObject = null;
        for (EventType tempObject : EventType.values()) {
            if (value == tempObject.getValue() ) {
                returnObject = tempObject;
                break;
            }
        }
        return returnObject;
    }

    public static EventType lookup(final String valueString) {
        try {
            int value = Integer.parseInt(valueString);
            return lookup(value);
        } catch (Exception e) {
        	EventType returnObject = null;
            if(valueString!=null) {
                for (EventType tempObject : EventType.values()) {
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
