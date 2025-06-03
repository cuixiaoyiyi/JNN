package cn.ios.junit.value.expr;

import java.lang.reflect.*;
import java.util.*;

import cn.ios.ju.base.Pair;
import cn.ios.junit.API;
import cn.ios.junit.util.StringSplicing;
import cn.ios.junit.value.AbstractJUValue;
import cn.ios.junit.value.FullClassType;
import cn.ios.junit.value.JUVarable;
import cn.ios.junit.value.VarableImpl;
import cn.ios.junit.value.VarableNameFactory;

public class JUStaticInvokeJavaConstructorExprImpl extends AbstractJUValue implements JUInvokeExpr {

	protected List<JUVarable> parameters = null;

	private VarableNameFactory nameFactory = null;

	private Constructor<?> constructor = null;

	private JUStaticInvokeJavaConstructorExprImpl(FullClassType clazz, Constructor<?> constructor,
			VarableNameFactory nameFactory) {
		if (clazz == null) {
			throw new IllegalArgumentException("clazz is null");
		}
		this.type = clazz;
		this.nameFactory = nameFactory;
		this.constructor = constructor;
		createParameters();

	}

	private void createParameters() {
		parameters = new ArrayList<JUVarable>();
		if (constructor.getParameterTypes() != null) {
			for (Parameter parameter : constructor.getParameters()) {
				Type parameterType = parameter.getParameterizedType();
				List<Pair<String, Object>> paraConstraints = API.getParameterConstraints(parameter);
				Map<Field, List<Pair<String, Object>>> fieldConstraints = API.getFieldConstraints(parameter.getType());
				List<Object> paraPossibleValues = API.getParaPossibleValues(parameter.getType(), paraConstraints);
				JUVarable varable = VarableImpl.v(new FullClassType(parameterType), paraConstraints, fieldConstraints, paraPossibleValues, nameFactory);
				parameters.add(varable);
			}
		}
	}

	@Override
	public List<JUVarable> getDependentVarable() {
		return parameters;
	}

	@Override
	public int getArgCount() {
		return parameters.size();
	}

	@Override
	public Object getCaller() {
		return type.getClassType();
	}

	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(getCaller().toString().replace("$", "."));
		int length = parameters == null ? 0 : parameters.size();
		stringBuffer.append(StringSplicing.splicingParameterParentheses(parameters, 0, length));
		return stringBuffer.toString();
	}

	public static JUStaticInvokeJavaConstructorExprImpl v(FullClassType clazz, Constructor<?> constructor,
			VarableNameFactory nameFactory) {
		return new JUStaticInvokeJavaConstructorExprImpl(clazz, constructor, nameFactory);
	}

	@Override
	public Set<Class<?>> getExceptions() {
		Set<Class<?>> exceptions = new HashSet<Class<?>>();
		if (constructor.getExceptionTypes() != null) {
			for (Class<?> type : constructor.getExceptionTypes()) {
				exceptions.add(type);
			}
		}
		return exceptions;
	}

	@Override
	public Executable getMethod() {
		return constructor;
	}

}
