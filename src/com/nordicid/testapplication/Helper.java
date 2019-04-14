package com.nordicid.testapplication;

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
}
