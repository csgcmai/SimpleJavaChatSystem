package com.chat.client;

import java.util.Calendar;

public class DateUtils {
	public static String getDate() {
		Calendar calendar = Calendar.getInstance();
		return 	"["+calendar.get(Calendar.YEAR) + "-"
				+ (calendar.get(Calendar.MONTH) + 1) + "-"
				+ calendar.get(Calendar.DAY_OF_MONTH) + "-"
				+ calendar.get(Calendar.HOUR_OF_DAY) + ":"
				+ calendar.get(Calendar.MINUTE)+ ":"
				+ calendar.get(Calendar.SECOND)+"]";
	}
}