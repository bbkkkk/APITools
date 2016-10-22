package com.itlaborer.apitools.utils;

public class JsonFormatUtils {

	public static String Format(String jsonStr) {
		int level = 0;
		StringBuffer jsonForMatStr = new StringBuffer();
		char pre1 = 0;
		char next = 0;
		for (int i = 0; i < jsonStr.length(); i++) {
			if (i > 0) {
				pre1 = jsonStr.charAt(i - 1);
			}
			if (i < jsonStr.length() - 1) {
				next = jsonStr.charAt(i + 1);
			}
			char c = jsonStr.charAt(i);
			if (level > 0 && '\n' == jsonForMatStr.charAt(jsonForMatStr.length() - 1)) {
				jsonForMatStr.append(GetLevelStr(level));
			}
			switch (c) {
			case '{':
			case '[':
				jsonForMatStr.append(c + "\n");
				level++;
				break;
			case ',':
				if ((pre1 == '"' && next == '"') || (Character.isDigit(pre1) && next == '"')
						|| ((jsonForMatStr.length() > 3)
								&& (jsonForMatStr.substring(jsonForMatStr.length() - 4) .equals("null")) && next == '"')) {
					jsonForMatStr.append(c + "\n");
				} else {
					jsonForMatStr.append(c);
				}
				break;
			case '}':
			case ']':
				jsonForMatStr.append("\n");
				level--;
				jsonForMatStr.append(GetLevelStr(level));
				jsonForMatStr.append(c);
				break;
			default:
				jsonForMatStr.append(c);
				break;
			}
		}
		return jsonForMatStr.toString();
	}

	private static String GetLevelStr(int level) {
		StringBuffer levelStr = new StringBuffer();
		for (int levelI = 0; levelI < level; levelI++) {
			levelStr.append("\t");
		}
		return levelStr.toString();
	}
}
