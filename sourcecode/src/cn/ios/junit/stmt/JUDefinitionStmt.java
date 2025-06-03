package cn.ios.junit.stmt;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.util.*;

import cn.ios.ju.base.InheritanceUtil;
import cn.ios.junit.config.Config;
import cn.ios.junit.value.*;
import cn.ios.junit.value.expr.JUInvokeExpr;
import cn.ios.junit.value.expr.JUNewExpr;
import cn.ios.util.Randomness;

public class JUDefinitionStmt extends JUStmt {

	private JUVarable varable = null;
	private JUValue rightValue = null;
	private int methodIndex = 0;
	private boolean random = false;

	public JUDefinitionStmt(JUVarable varable, JUStmt stmt, int methodIndex, boolean random) {
		this.varable = varable;
		this.methodIndex = methodIndex;
		this.random = random;
		nextStmt = stmt;
		this.nameFactory = stmt.getNameFactory();
		if (nameFactory == null) {
			throw new IllegalArgumentException("nameFactory === null");
		}
		createRightValue();
		addElement();
	}

	public JUDefinitionStmt(JUVarable variable) {
		this.varable = variable;
		nameFactory = new VarableNameFactory(varable);
		createRightValue();
		addElement();
	}

	private void createRightValue() {
		List<Object> possibleValues = varable.getPossibleValues();
		Object possibleValue = null;
		if (possibleValues != null && !possibleValues.isEmpty()) {
			int size = possibleValues.size();
			if (random) {
				possibleValue = possibleValues.get(new Random().nextInt(size));
			} else {
				if (methodIndex < size) {
					possibleValue = possibleValues.get(methodIndex) ;
				} else {
					while (methodIndex - size >= size){
						methodIndex -= size;
					}
					possibleValue = possibleValues.get(methodIndex - size);
				}
			}
		}

		rightValue = ValueFactory.createValue(varable.getType(), nameFactory,varable.getConstraints(), possibleValue);
		if (rightValue instanceof Dependable) {
			Dependable dependable = (Dependable) rightValue;
			if (dependable.getDependentVarable() != null) {
				for (JUVarable parameter : dependable.getDependentVarable()) {
					// for new ArrayList<>(2)
					if (dependable instanceof JUNewExpr
							&& ((JUNewExpr) dependable).getType().toString().contains("java.math.BigDecimal")) {
						List<Object> lists = new ArrayList<Object>(Collections.singleton(possibleValue));
						parameter.setPossibleValues(lists);
					} else {
						parameter.setPossibleValues(possibleValues);
					}
					JUStmt stmt = JUStmtFactory.createDefinitionStmt(parameter, this, methodIndex, true);
					previouStmts.add(stmt);
				}
			}
		}
	}

	private void addElement() {
//		int min = -1;
//		int max = -1;
//		if (varable.getConstraints() != null) {
//			for (Pair<String, Object> pair : varable.getConstraints()) {
//				if (pair.isNumberType()) {
//					if (IParser.SIZE_MIN == pair.getKey()) {
//						min = ((Number) pair.getValue()).intValue();
//					} else if (IParser.SIZE_MAX == pair.getKey()) {
//						max = ((Number) pair.getValue()).intValue();
//					}
//				}
//			}
//		}
//
//		if (min < 0) {
//			min = Config.REF_ARRAY_MIN_SIZE;
//		}
//		if (max < 0) {
//			max = min + Config.REF_ARRAY_MAX_SIZE - 1;
//		}
//		if (min > max) {
//			min = Config.REF_ARRAY_MIN_SIZE;
//			max = Config.REF_ARRAY_MAX_SIZE;
//		}
//		int arraySize = Randomness.nextInt(min, max);

		int arraySize = Randomness.nextInt(Config.REF_ARRAY_MIN_SIZE, Config.REF_ARRAY_MAX_SIZE);
		try {
			if (varable.getPossibleValues() != null && varable.getPossibleValues().size() > methodIndex){
				Object o = varable.getPossibleValues().get(methodIndex);
				arraySize = Integer.parseInt(o.toString());
			}
		} catch (Exception e) {
			System.out.println("exception in addElement");
		}



		if (InheritanceUtil.isInheritedFromGivenClass(varable.getType().getClassType(), Collection.class)) {
			Method method = null;
			try {
				method = varable.getType().getClassType().getMethod("add", Object.class);
				for (int i = 0; i < arraySize; i++) {
//					Class<?> currentClass = varable.getType().getClassType();
					FullClassType elementType = varable.getType();
					JUInvokeStmt invokeStmt = JUStmtFactory.createInvokeStmt(method, varable, nameFactory, elementType, methodIndex);
					succStmts.add(invokeStmt);
				}
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}

		} else if (InheritanceUtil.isInheritedFromGivenClass(varable.getType().getClassType(), Map.class)) {
			Method method;
			try {
				method = varable.getType().getClassType().getMethod("put", Object.class, Object.class);
				for (int i = 0; i < arraySize; i++) {
					JUInvokeStmt invokeStmt = JUStmtFactory.createInvokeStmt(method, varable, nameFactory,
							varable.getType(), methodIndex);
					succStmts.add(invokeStmt);
				}
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Set<Class<?>> getExceptions() {
		if (rightValue instanceof JUNewExpr) {
			Set<Class<?>> exceptions = ((JUNewExpr) rightValue).getExceptions();
			return exceptions;
		}
		return super.getExceptions();
	}

	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
//		String classType = varable.getType().getClassType().getName();
//		stringBuffer.append(varable.getVarableTypeString());
		stringBuffer.append(varable.getType().toString().replace("$", "."));
		stringBuffer.append(" ");
		stringBuffer.append(varable);
		stringBuffer.append(" = ");
		if (varable.getType().toString().startsWith("java.lang.Class<")
				&& varable.getType().getType() instanceof GenericArrayType) {
			stringBuffer.append("(").append(varable.getType().toString()).append(")").append("new Class[]");
		}
		stringBuffer.append(rightValue);
		stringBuffer.append(";");
		return stringBuffer.toString();
	}

	@Override
	public boolean containsJUInvokeExpr() {
		return false;
	}

	@Override
	public JUInvokeExpr getJUInvokeExpr() {
		return null;
	}
}
