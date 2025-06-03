package cn.ios.junit.value.expr;

import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import cn.ios.junit.value.FullClassType;
import cn.ios.junit.value.JUVarable;
import cn.ios.junit.value.VarableNameFactory;

public class JUExprFactory {

	public static JUNewExpr createJUNewExpr(FullClassType genericType, VarableNameFactory nameFactory) {
		return JUNewExprImpl.v(genericType, nameFactory);
	}

	public static JUInvokeExpr createInvokeExpr(Executable method, JUVarable caller, FullClassType fullClassType,
			VarableNameFactory nameFactory) {
		if (Modifier.isStatic(method.getModifiers())) {
			return JUStaticInvokeExprImpl.v(method, fullClassType, nameFactory);
		} else {
			return JUDynamicInvokeExprImpl.v(caller, method, fullClassType, nameFactory);
		}
	}
}
