package cn.ios.junit.value.prim;

import cn.ios.junit.value.AbstractConstantValue;

public class JUNullConstantValue extends AbstractConstantValue {

	private JUNullConstantValue() {
		super(null, null);
	}

	@Override
	public String toString() {
		return "null";
	}

	public static JUNullConstantValue v() {
		return new JUNullConstantValue();
	}

}