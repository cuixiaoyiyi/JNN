package cn.ios.junit.value.expr;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import cn.ios.junit.value.FullClassType;
import cn.ios.junit.value.VarableNameFactory;

public class JUStaticInvokeExprImpl extends JUAbstractInvokeExpr implements JUStaticInvokeExpr {

	private Class<?> caller = null;

	protected JUStaticInvokeExprImpl(Executable constructorMethod, FullClassType fullClassType, VarableNameFactory nameFactory) {
		super(constructorMethod, fullClassType, nameFactory);
		caller = constructorMethod.getDeclaringClass();
	}

	@Override
	public String toString() {
		String typeInfo = type.getClassType().getTypeName();
		String type = "";
		if (method.getName().contains("<init>") && typeInfo.contains("<")) {
			int start = typeInfo.indexOf("<");
			type = typeInfo.substring(start);
		}

		if (!(method instanceof Constructor)) {
			type = typeInfo + ".";
		}
		return type.replace("$", ".") + super.toString();
	}

	public static JUStaticInvokeExprImpl v(Executable constructorMethod, FullClassType fullClassType, VarableNameFactory nameFactory) {
		return new JUStaticInvokeExprImpl(constructorMethod, fullClassType, nameFactory);
	}

	@Override
	public Class<?> getCaller() {
		return caller;
	}

}
