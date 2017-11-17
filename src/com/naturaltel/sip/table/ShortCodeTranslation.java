package com.naturaltel.sip.table;

import java.sql.Date;

/**
 * (SCT)：加值簡碼轉換長碼對應表
 */
public class ShortCodeTranslation {

	/** 0 ~ 99999999 Short Code */
	int key;
	/**(LC) +8862xxxxxxxx ~ +8869xxxxxxxx Long Code */
	String longCode;
	/** (ST) 效期起始時間, 0 = 表示不管控*/
	Date startTime;
	/** (ET) 效期終止時間, 0 = 表示不管控*/
	Date endTime;

}
