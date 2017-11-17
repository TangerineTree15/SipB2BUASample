package com.naturaltel.sip.component.type;

public enum CallType {
	MO(1), MT(2)
	;
	
	int value;
	CallType(int value) {
		this.value = value;
	}

	public int getValue() {
        return value;
    }

    public static CallType lookup(final int value) {
    	CallType returnObject = null;
        for (CallType tempObject : CallType.values()) {
            if (value == tempObject.getValue() ) {
                returnObject = tempObject;
                break;
            }
        }
        return returnObject;
    }

    public static CallType lookup(final String valueString) {
        try {
            int value = Integer.parseInt(valueString);
            return lookup(value);
        } catch (Exception e) {
        	CallType returnObject = null;
            if(valueString!=null) {
                for (CallType tempObject : CallType.values()) {
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
