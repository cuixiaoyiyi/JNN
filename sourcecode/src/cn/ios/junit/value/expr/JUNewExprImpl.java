package cn.ios.junit.value.expr;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import cn.ios.ju.base.Log;
import cn.ios.ju.base.Pair;
import cn.ios.junit.util.FindRealClass;
import cn.ios.junit.value.AbstractJUValue;
import cn.ios.junit.value.FullClassType;
import cn.ios.junit.value.JUVarable;
import cn.ios.junit.value.VarableNameFactory;

public class JUNewExprImpl extends AbstractJUValue implements JUNewExpr {

	private Executable constructorMethod = null;

	private JUInvokeExpr invokeExpr = null;

	private VarableNameFactory nameFactory = null;

	private boolean staticAssignment = false;

	private JUNewExprImpl(FullClassType fullClassType, VarableNameFactory nameFactory) {
		if (nameFactory == null) {
			throw new IllegalArgumentException("nameFactory is null");
		}
		this.type = fullClassType;
		this.nameFactory = nameFactory;
		findConstructorMethod();
	}

	private JUNewExprImpl(FullClassType fullClassType, VarableNameFactory nameFactory, Object possibleValue) {
		if (nameFactory == null) {
			throw new IllegalArgumentException("nameFactory is null");
		}
		this.type = fullClassType;
		this.nameFactory = nameFactory;
		findConstructorMethod();
	}

	private JUNewExprImpl(FullClassType fullClassType, VarableNameFactory nameFactory, Map<Field, List<Pair<String, Object>>> fieldConstraints) {
		if (nameFactory == null) {
			throw new IllegalArgumentException("nameFactory is null");
		}
		this.type = fullClassType;
		this.nameFactory = nameFactory;
		findConstructorMethod();
	}

	private void findConstructorMethod() {

		Class<?> pointClazz = FindRealClass.getPossibleClass(type.getClassType(), nameFactory);
		if(pointClazz==Class.class) {
			Log.e("");
		}
		if (pointClazz != null) {
			FullClassType fullClassType = new FullClassType(pointClazz);
			fullClassType.getGeneicElements().clear();
			fullClassType.getGeneicElements().addAll(type.getGeneicElements());
			constructorMethod = FindRealClass.getConstructMethod(pointClazz, nameFactory);
			if (constructorMethod == null) {
				for (Method method : pointClazz.getDeclaredMethods()) {
					if (Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers())
							&& method.getReturnType() == pointClazz) {
						staticAssignment = true;
						constructorMethod = method;
						invokeExpr = JUStaticInvokeExprImpl.v(method, fullClassType, nameFactory);
						break;
					}
				}
			} else {
				invokeExpr = JUStaticInvokeExprImpl.v(constructorMethod, fullClassType, nameFactory);
			}
		}

	}

	@Override
	public String toString() {

		if (invokeExpr != null) {
			String invokeExprString = invokeExpr.toString().replace(".<init>", "").replace("$", ".");
			if (!staticAssignment && constructorMethod.getDeclaringClass().getTypeParameters().length > 0) {
				int index1 = invokeExprString.indexOf('(');
				String genericInfo = invokeExpr.getGenericParametersString();
				StringBuilder stringBuilder = new StringBuilder(invokeExprString);
				stringBuilder.insert(index1, genericInfo);
				invokeExprString = stringBuilder.toString();
			}

			return (staticAssignment ? "" : "new ") + invokeExprString;
		} else {
			return "null";
		}
	}

	@Override
	public List<JUVarable> getDependentVarable() {

		return invokeExpr == null ? new ArrayList<JUVarable>() : invokeExpr.getDependentVarable();
	}

	@Override
	public Set<Class<?>> getExceptions() {
		return invokeExpr == null ? new HashSet<Class<?>>() : invokeExpr.getExceptions();
	}

	@Override
	public Executable getMethod() {
		return constructorMethod;
	}

	@Override
	public int getArgCount() {
		return constructorMethod == null ? 0 : constructorMethod.getParameterCount();
	}

	@Override
	public Object getCaller() {
		return null;
	}

	public static JUNewExprImpl v(FullClassType fullClassType, VarableNameFactory nameFactory) {
		return new JUNewExprImpl(fullClassType, nameFactory);
	}

	public static JUNewExprImpl v1(FullClassType fullClassType, VarableNameFactory nameFactory, Map<Field, List<Pair<String, Object>>> fieldConstraints) {
		return new JUNewExprImpl(fullClassType, nameFactory, fieldConstraints);
	}

	public static JUNewExprImpl v1(FullClassType fullClassType, VarableNameFactory nameFactory, Object possibleValue) {
		return new JUNewExprImpl(fullClassType, nameFactory, possibleValue);
	}

}
