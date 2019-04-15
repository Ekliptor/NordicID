package com.nordicid.testapplication;

import java.util.Calendar;
import java.util.Date;

public class Helper {
	public static String getBetween(String input, String start, String end) {
		int startPos = input.indexOf(start);
		if (startPos == -1)
			return "";
		startPos += start.length();
		int endPos = input.indexOf(end, startPos);
		if (endPos == -1)
			return "";
		String between = input.substring(startPos, endPos);
		return between;
	}
	
	public static Date addSeconds(Date date, int sec) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, sec); //minus number would decrement the days
        return cal.getTime();
    }
}
