package cn.ios.junit.value.expr;

import cn.ios.junit.value.JUVarable;

public interface JUDynamicInvokeExpr extends JUInvokeExpr {
	
	public JUVarable getCaller();
}
