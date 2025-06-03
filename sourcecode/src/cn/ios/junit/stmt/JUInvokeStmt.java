package cn.ios.junit.stmt;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

import cn.ac.ios.ann.AAPI;
import cn.ios.ju.base.Pair;
import cn.ios.junit.API;
import cn.ios.junit.config.Config;
import cn.ios.junit.generator.JUnitMethod;
import cn.ios.junit.util.StringSplicing;
import cn.ios.junit.value.FullClassType;
import cn.ios.junit.value.JUVarable;
import cn.ios.junit.value.VarableNameFactory;
import cn.ios.junit.value.expr.JUExprFactory;
import cn.ios.junit.value.expr.JUInvokeExpr;

public class JUInvokeStmt extends JUStmt {

	private Executable method = null;

	private JUInvokeExpr invokeExpr = null;

	private int methodIndex = 0;

	private StringBuilder assertStringBuilder = new StringBuilder();

	public JUInvokeStmt(Executable method, JUVarable caller, VarableNameFactory nameFactory,
			FullClassType fullClassType, int methodIndex) {
		if (method == null) {
			throw new IllegalArgumentException("method is null");
		}
		if (fullClassType == null) {
			throw new IllegalArgumentException("classGenericType is null");
		}
		this.method = method;
		this.nameFactory = nameFactory;
		this.methodIndex = methodIndex;

		invokeExpr = JUExprFactory.createInvokeExpr(method, caller, fullClassType, nameFactory);
		this.assertStringBuilder = setAssertStringBuilder();
		createDependentStmts();
	}

	private void createDependentStmts() {
		Set<JUVarable> set = new HashSet<JUVarable>(invokeExpr.getDependentVarable());
		for (JUVarable varable : set) {
			JUDefinitionStmt stmt = JUStmtFactory.createDefinitionStmt(varable, this, methodIndex, false);
			previouStmts.add(stmt);
		}
	}

	private String genAssert(Class<?> fieldType, List<Pair<String, Object>> constraints, String fieldName, String upperLevelName){
		if (constraints==null || constraints.isEmpty()) return null;
		StringBuilder stringBuilder = new StringBuilder();
		String variable = null;

		if (fieldName.isEmpty()) {
			variable = upperLevelName ;
		} else {
			if (String.class.equals(fieldType)) {
				variable = "((String)";
			} else if (boolean.class.equals(fieldType) || Boolean.class.equals(fieldType)) {
				variable = "((boolean)";
			} else if (int.class.equals(fieldType) || Integer.class.equals(fieldType)) {
				variable = "((int)";
			} else if (short.class.equals(fieldType) || Short.class.equals(fieldType)) {
				variable = "((short)";
			} else if (byte.class.equals(fieldType) || Byte.class.equals(fieldType)) {
				variable = "((byte)";
			} else if (float.class.equals(fieldType) || Float.class.equals(fieldType)) {
				variable = "((float)";
			} else if (double.class.equals(fieldType) || Double.class.equals(fieldType)) {
				variable = "((double)";
			} else if (long.class.equals(fieldType) || Long.class.equals(fieldType)) {
				variable = "((long)";
			} else if (char.class.equals(fieldType) || Character.class.equals(fieldType)) {
				variable = "((char)";
			} else {
				variable = "(";
			}
			variable = variable + fieldName + ".get(" +upperLevelName + "))";
		}
		if (fieldType == BigDecimal.class) {

		}
		for (Pair<String, Object> retConstraint : constraints) {
			Object value = retConstraint.getValue();
			String key = retConstraint.getKey();
			stringBuilder.append("      assert ");
			switch (key){
				case "assert" :
					if (value.toString().equals("false")) {
						stringBuilder.append("!").append(variable).append(";\n");
					} else {
						stringBuilder.append(variable).append(";\n");
					}
					break;
				case "match":
					stringBuilder.append(variable).append(".matches(\"").append(value).append("\")").append(";\n");
					break;
				case "SIZE_MIN":
				case "SIZE_MAX":
					if (fieldType == BigDecimal.class) {
						Config.IF_IMPORT_BIG_DECIMAL = true;
						stringBuilder.append("((BigDecimal)").append(variable).append(").compareTo(new BigDecimal(").append(value).append("))");
						if (key.equals("SIZE_MIN")){
							stringBuilder.append(">= 0; \n");
						} else{
							stringBuilder.append(">= 0; \n");
						}
					} else {
						stringBuilder.append(variable);
						if (AAPI.isStringType(fieldType)) {
							stringBuilder.append(".length()");
						} else if (fieldType.isArray()) {
							stringBuilder.append(".length");
						} else if (AAPI.isSet(fieldType)){
							stringBuilder.append(".size()");
						}
						String op = key.equals("SIZE_MIN")? ">=" : "<=";
						stringBuilder.append(" ").append(op).append(" ").append(value).append(";\n");
					}

					break;
				case "notEmpty":
					stringBuilder.append(variable);
					if (fieldType.isArray()) {
						stringBuilder.append(".length == 0;\n");
					} else if (AAPI.isSet(fieldType)) {
						stringBuilder.append(".isEmpty();\n");
					}
					break;
				default:
					if (value.toString().isEmpty()) {
						value = "\"\"";
					}
					stringBuilder.append(variable).append(" ").append(key).append(" ").append(value).append(";\n");
					break;
			}
		}
		return stringBuilder.toString();
	}

	private void traceField(Class<?> fieldType, StringBuilder stringBuilder, String upperLevelName){
		Config.MIN_NESTED_OBJECT++;
		if (Config.MIN_NESTED_OBJECT > Config.MAX_NESTED_OBJECT || fieldType.getName().contains("java.")) {
			Config.MIN_NESTED_OBJECT = 0;
			return;
		}
		try {
			Map<Field, List<Pair<String, Object>>> fieldConstraints = API.getFieldConstraints(fieldType);
			if (fieldConstraints != null && !fieldConstraints.isEmpty()) {
				for (Map.Entry<Field, List<Pair<String, Object>>> fieldEntry : fieldConstraints.entrySet()) {
//					String temp = upperLevelName + "_" + fieldEntry.getKey().getName();
					String temp = nameFactory.getName(fieldEntry.getKey().getType());
					List<Pair<String, Object>> constraints = fieldEntry.getValue();
					String objAssert = genAssert(fieldEntry.getKey().getType(), constraints, temp, upperLevelName);
					stringBuilder.append("      ").append("Field ").append(temp).append(" = ").append(upperLevelName).
							append(".getClass().getDeclaredField(\"").append(fieldEntry.getKey().getName()).append("\");\n");
					stringBuilder.append("      ").append(temp).append(".setAccessible(true);\n");
					if (objAssert != null && !objAssert.isEmpty()) {
						stringBuilder.append(objAssert);
					}
				}
			}
			if (AAPI.isObject(fieldType) && !fieldType.getName().contains("enum")) {
				for (Field field : fieldType.getDeclaredFields()) {
					if (AAPI.isObject(field.getType())) {
//						String tempName = fieldType.getName().replaceAll("\\\\.","_") + "_" + field.getName().replaceAll("\\\\.","_");
						String tempName = nameFactory.getName(field.getType());
						stringBuilder.append("      ").append("Field ").append(tempName).append(" = ").append(upperLevelName).
								append(".getClass().getDeclaredField(\"").append(field.getName()).append("\");\n");
						stringBuilder.append("      ").append(tempName).append(".setAccessible(true);\n");
						if (!fieldType.equals(field.getType())) {
							traceField(field.getType(), stringBuilder,tempName);
						}
					}
				}
			}
		} catch (Exception | Error e) {
			Config.MIN_NESTED_OBJECT = 0;
		}

	}

	public StringBuilder setAssertStringBuilder(){
		StringBuilder stringBuilder = new StringBuilder();
		if (method instanceof Method && method.getName().equals(Config.CURRENT_METHOD_NAME) && !Config.CURRENT_ASSERT_NAME.equals("")) {

			Class<?> returnType = ((Method) method).getReturnType();

			List<Pair<String, Object>> retConstraints = API.getRetConstraints((Method) method);

			String result = Config.CURRENT_ASSERT_NAME;
			String assertString = genAssert(returnType, retConstraints, "", result);

			if (assertString != null && !assertString.isEmpty()){
				stringBuilder.append(assertString);
			}
			// if nested
			traceField(returnType,stringBuilder,result);
			Config.MIN_NESTED_OBJECT = 0;
		}
		return stringBuilder;
	}


	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(invokeExpr.toString()).append(";").append("\n");
		stringBuilder.append(this.assertStringBuilder.toString());
		// deal assert
		return stringBuilder.toString();
	}

	private List<String> getParametersDotClass() {
		List<String> list = new ArrayList<String>();
		list.add("\"" + method.getName() + "\"");
		List<JUVarable> parameters = invokeExpr.getDependentVarable();
		if (parameters != null) {
			for (JUVarable varable : parameters) {
				list.add(varable.getType().toString().replace("$", ".") + ".class");
			}
		}
		return list;
	}

	@Override
	public Set<Class<?>> getExceptions() {
		return  invokeExpr.getExceptions();
	}

	protected void processPrivateMethod(StringBuffer stringBuffer) {
		List<JUVarable> parameters = invokeExpr.getDependentVarable();
		Object caller = invokeExpr.getCaller();
		String methodName = method.getName();
		stringBuffer.append("Method ");
		stringBuffer.append(methodName);
		stringBuffer.append(" = ");
		stringBuffer.append(caller);
		stringBuffer.append(".getClass().getDeclaredMethod");
		List<String> getParametersDotClass = getParametersDotClass();
		stringBuffer.append(
				StringSplicing.splicingParameterParentheses(getParametersDotClass, 0, getParametersDotClass.size()));
		stringBuffer.append(";\n");
		stringBuffer.append(JUnitMethod.INNER_BLOCK_SPACE);
		stringBuffer.append(methodName);
		stringBuffer.append(".setAccessible(true);\n");
		stringBuffer.append(JUnitMethod.INNER_BLOCK_SPACE);
		stringBuffer.append(methodName);
		stringBuffer.append(".invoke");
		int length = parameters == null ? 0 : parameters.size();
		stringBuffer.append(StringSplicing.splicingParameterParentheses(parameters, 0, length));

	}

	@Override
	public boolean containsJUInvokeExpr() {
		return true;
	}

	@Override
	public JUInvokeExpr getJUInvokeExpr() {
		return invokeExpr;
	}

}
