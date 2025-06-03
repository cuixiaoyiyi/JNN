package cn.ios.junit.util;

import java.util.List;

public class StringSplicing {

	public static String splicingParameterParentheses(List<? extends Object> data) {
		return splicingParameterParentheses(data, 0, data.size());
	}

	public static String splicingParameterParentheses(List<? extends Object> data, int start, int length) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("(");
		stringBuffer.append(splicingParameter(data, start, length));
		stringBuffer.append(")");
		return stringBuffer.toString();
	}

	public static String splicingParameterParentheses(Object[] data) {
		return splicingParameterParentheses(data, 0, data.length);
	}

	public static String splicingParameterParentheses(Object[] data, int start, int length) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("(");
		stringBuffer.append(splicingParameter(data, start, length));
		stringBuffer.append(")");
		return stringBuffer.toString();
	}

	public static String splicingParameterBrace(List<? extends Object> data, int start, int length) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("{");
		stringBuffer.append(splicingParameter(data, start, length));
		stringBuffer.append("}");
		return stringBuffer.toString();
	}

	public static String splicingParameter(Object[] data) {
		if (data != null) {
			return splicingParameter(data, 0, data.length);
		}
		return "";
	}

	public static String splicingParameter(List<? extends Object> data) {
		if (data != null) {
			return splicingParameter(data, 0, data.size());
		}
		return "";
	}

	public static String splicingParameter(Object[] data, int start, int length) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < length - 1; i++) {
			stringBuffer.append(data[start + i]);
			stringBuffer.append(", ");
		}
		if (length != 0) {
			stringBuffer.append(data[start + length - 1]);
		}
		return stringBuffer.toString();
	}

	public static String splicingParameter(List<? extends Object> data, int start, int length) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < length - 1; i++) {
			stringBuffer.append(data.get(start + i));
			stringBuffer.append(", ");
		}
		if (length != 0) {
			stringBuffer.append(data.get(start + length - 1));
		}
		return stringBuffer.toString();
	}
}
