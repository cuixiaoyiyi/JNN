package cn.ios.junit.value.expr;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;

import cn.ac.ios.ann.AAPI;
import cn.ios.junit.config.Config;
import cn.ios.junit.value.FullClassType;
import cn.ios.junit.value.JUVarable;
import cn.ios.junit.value.VarableImpl;
import cn.ios.junit.value.VarableNameFactory;

public class JUDynamicInvokeExprImpl extends JUAbstractInvokeExpr implements JUDynamicInvokeExpr {

	private JUVarable caller = null;
	Executable method = null;
	private StringBuilder resultStringBuilder = new StringBuilder();


	protected JUDynamicInvokeExprImpl(JUVarable caller, Executable method, FullClassType type, VarableNameFactory nameFactory) {
		super(method, type, nameFactory);
		this.caller = caller;
		this.method = method;
		this.resultStringBuilder = getStringBuilder();
	}

	private StringBuilder getStringBuilder(){
		StringBuilder sb = new StringBuilder();
		if (method instanceof Method && method.getName().equals(Config.CURRENT_METHOD_NAME) &&
				(AAPI.getFieldConstraints().containsKey(((Method) method).getReturnType()) ||
						AAPI.getRetConstraints().containsKey(method)) ) {
			Config.CURRENT_ASSERT_NAME = nameFactory.getName(((Method) method).getReturnType());
			sb.append(((Method) method).getReturnType().getName()).append(" ").append(Config.CURRENT_ASSERT_NAME).append(" = ");
		} else {
			Config.CURRENT_ASSERT_NAME = "";
		}
		return sb;
	}

	@Override
	public JUVarable getCaller() {
		return caller;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(resultStringBuilder);
		stringBuilder.append(caller).append(".").append(super.toString());
		return stringBuilder.toString();
	}

	public static JUDynamicInvokeExprImpl v(JUVarable caller, Executable method, FullClassType fullClassType,
			VarableNameFactory nameFactory) {
		if (caller == null) {
			//TODO constraints may be not null
			caller = VarableImpl.v(fullClassType,null, null, null,nameFactory);
		}
		return new JUDynamicInvokeExprImpl(caller, method, fullClassType, nameFactory);
	}

}
