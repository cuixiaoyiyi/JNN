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
//import sun.reflect.generics.reflectiveObjects.WildcardTypeImpl;

public abstract class JUAbstractInvokeExpr extends AbstractJUValue implements JUInvokeExpr {

	protected Executable method = null;

	protected List<JUVarable> parameters = null;

	protected VarableNameFactory nameFactory = null;

	protected JUAbstractInvokeExpr(Executable method, FullClassType fullClassType, VarableNameFactory nameFactory) {
		if (method == null) {
			throw new IllegalArgumentException("method is null");
		}
		if (fullClassType == null) {
			throw new IllegalArgumentException("type is null");
		}
		if (nameFactory == null) {
			throw new IllegalArgumentException("nameFactory is null");
		}
		this.type = fullClassType;
		this.method = method;
		this.nameFactory = nameFactory;
		createParameters();
	}
 
	private void createParameters() {
		parameters = new ArrayList<JUVarable>();
		int index = 0;
		HashMap<String, FullClassType> hashMap = new HashMap<>();
		for (TypeVariable<?> typeParameter : type.getClassType().getTypeParameters()) {
			if (type.getGeneicElements().size() > index) {
				hashMap.put(typeParameter.getTypeName(), type.getGeneicElements().get(index++));
			}
		}

		for (Parameter parameter : method.getParameters()) {
			Type parameterType = parameter.getParameterizedType();	
			FullClassType fullClassType = getActualFullClassType(parameterType, hashMap);
			List<Pair<String, Object>> paraConstraints = API.getParameterConstraints(parameter);
			Map<Field, List<Pair<String, Object>>> fieldConstraints = API.getFieldConstraints(parameter.getType());

			List<Object> paraPossibleValues = API.getParaPossibleValues(parameter.getType(), paraConstraints);
			JUVarable varable = VarableImpl.v(fullClassType, paraConstraints, fieldConstraints, paraPossibleValues, nameFactory);
			parameters.add(varable);
		}
	}

	private FullClassType getActualFullClassType(Type type, HashMap<String, FullClassType> hashMap) {
		FullClassType fullClassType = null;
		// E
		if (hashMap != null && hashMap.containsKey(type.getTypeName())) {
			fullClassType = hashMap.get(type.getTypeName());
			return fullClassType;
		}
		// Class
		if (type instanceof Class) {
			fullClassType = new FullClassType(type);
			return fullClassType;
		}
		// E[]
		if (type instanceof GenericArrayType) {
			Type baseType = type;
			int dims = 0;
			while (baseType instanceof GenericArrayType) {
				GenericArrayType genericArrayType = (GenericArrayType) baseType;
				baseType = genericArrayType.getGenericComponentType();
				dims++;
			}
			if (hashMap != null && hashMap.containsKey(baseType.getTypeName())) {
				Class<?> baseClazz = hashMap.get(baseType.getTypeName()).getClassType();
				fullClassType = new FullClassType(Array.newInstance(baseClazz, dims).getClass());
				fullClassType.getGeneicElements().addAll(hashMap.get(baseType.getTypeName()).getGeneicElements());
			} else {
				fullClassType = new FullClassType(type);
			}
			return fullClassType;
		}
		// <? extends E >
		if (type instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType) type;
			Type realType = null;
			if (wildcardType.getUpperBounds().length != 0) {
				realType = wildcardType.getUpperBounds()[0];
			}
			if (wildcardType.getLowerBounds().length != 0) {
				realType = wildcardType.getLowerBounds()[0];
			}
			return getActualFullClassType(realType, hashMap);
		}
		// ParameterizedType <K,V>
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			fullClassType = new FullClassType(type);
			fullClassType.getGeneicElements().clear();
			for (int i = 0; i < parameterizedType.getActualTypeArguments().length; i++) {
				//recursion
				FullClassType element = getActualFullClassType(parameterizedType.getActualTypeArguments()[i], hashMap);
				fullClassType.getGeneicElements().add(element);
			}
			return fullClassType;
		}
		fullClassType = new FullClassType(type);
		return fullClassType;
	}

	@Override
	public List<JUVarable> getDependentVarable() {
		return parameters;
	}

	@Override
	public Executable getMethod() {
		return method;
	}

	@Override
	public int getArgCount() {
		return parameters.size();
	}

	@Override
	public Set<Class<?>> getExceptions() {
		HashSet<Class<?>> set = new HashSet<Class<?>>();
		set.addAll(Arrays.asList(method.getExceptionTypes()));
		return set;
	}

	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		if (Modifier.isPrivate(method.getModifiers())) {
			processPrivateMethod(stringBuffer);
		} else {
			stringBuffer.append(method.getName());
			int length = parameters == null ? 0 : parameters.size();
			stringBuffer.append(StringSplicing.splicingParameterParentheses(parameters, 0, length));
		}
		return stringBuffer.toString();
	}

	protected void processPrivateMethod(StringBuffer stringBuffer) {
	}

}
