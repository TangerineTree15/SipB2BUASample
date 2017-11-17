package com.naturaltel.sip.component.type;

public enum ModeType {
	/** Event 發給 SCP 後，等 SCP 通知。*/
	Interrupted(0),
	/** Event 只通知 SCP，但仍執行該做的事。 */
	NotifyAndContinue(1),
	/** Event 不通知 SCP。 */
	Transparent(2),
	;
	
	int value;
	ModeType(int value) {
		this.value = value;
	}

	public int getValue() {
        return value;
    }

    public static ModeType lookup(final int value) {
    	ModeType returnObject = null;
        for (ModeType tempObject : ModeType.values()) {
            if (value == tempObject.getValue() ) {
                returnObject = tempObject;
                break;
            }
        }
        return returnObject;
    }

    public static ModeType lookup(final String valueString) {
        try {
            int value = Integer.parseInt(valueString);
            return lookup(value);
        } catch (Exception e) {
        	ModeType returnObject = null;
            if(valueString!=null) {
                for (ModeType tempObject : ModeType.values()) {
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
