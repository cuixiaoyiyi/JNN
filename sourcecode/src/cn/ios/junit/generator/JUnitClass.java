package cn.ios.junit.generator;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import cn.ac.ios.ann.AAPI;
import cn.ios.ju.base.Log;
import cn.ios.junit.API;
import cn.ios.junit.config.Config;
import cn.ios.junit.output.ErrorWriter;

public class JUnitClass {

	private Class<?> clazzUnderAnalysis = null;

	private String className = null;

	private String packageName = null;

	private ArrayList<JUnitMethod> methods = new ArrayList<JUnitMethod>();

	public JUnitClass(Class<?> sootClass) {
		clazzUnderAnalysis = sootClass;
		try {
			className = clazzUnderAnalysis.getSimpleName() + "_Test";
			packageName = clazzUnderAnalysis.getPackage() != null ? clazzUnderAnalysis.getPackage().getName() : null;
		} catch (Exception | Error e) {
			System.out.println("");
		}

	}

	public JUnitClass start() {
		generateJUnitMethod();
		return this;
	}

	private void generateJUnitMethod() {
//		if(!sootClassUnderAnalysis.getName().equals("cn.ios.sort.GenericTest")) {
//			return;
//		}
		try {
			if(clazzUnderAnalysis.getDeclaredMethods().length >0) {
				for (Executable method : clazzUnderAnalysis.getDeclaredMethods()) {
					if (!method.toString().endsWith(".compareTo(java.lang.Object)") && !method.toString().contains("$")) {
						generateJUnitMethod(method, methods);
					}
				}
			}
		} catch (Exception | Error e) {
			Log.e(e.getClass().getName(), " ", clazzUnderAnalysis);
		}
	}

	public void addMethod(JUnitMethod method) {
		methods.add(method);
	}

	public ArrayList<JUnitMethod> getMethods() {
		return methods;
	}

	@Override
	public String toString() {

		StringBuffer stringBuffer = new StringBuffer();

		/** package : take default package into account */
		if (packageName != null && packageName != "") {
			stringBuffer.append("package " + packageName + ";\n\n");
		}
		/** import */
		stringBuffer.append("import org.junit.Test;\n");
		stringBuffer.append("import java.lang.reflect.Field;\n");
		if (Config.IF_IMPORT_BIG_DECIMAL) {
			stringBuffer.append("import java.math.BigDecimal;\n");
		}
		/** class */
		stringBuffer.append("\npublic class " + className + " {\n\n");
		/** methods */
		for (JUnitMethod method : methods) {
			stringBuffer.append(method);
			stringBuffer.append("\n\n");
		}
		stringBuffer.append("}");
		return stringBuffer.toString();
	}

	public String getName() {
		return className;
	}

	public String getPackageName() {
		return clazzUnderAnalysis.getPackage() != null ? clazzUnderAnalysis.getPackage().getName() : null;
	}

	public static void generateJUnitMethod(Executable method, ArrayList<JUnitMethod> methods) {
		method.getModifiers();
		/**
		 * Take java bridge method into account.( Modifier.isSynthetic(method) )
		 * 20200723
		 */
		if (isIgnoredMethod(method)) {
			return;
		}
		int MAX_UNIT_METHOD = cn.ios.junit.config.Config.MAX_UNIT_METHOD;
		int maxAvailableValuesNum = API.getMaxAvailableValuesNum(method);
		MAX_UNIT_METHOD = Math.max(MAX_UNIT_METHOD, maxAvailableValuesNum);
		int max = MAX_UNIT_METHOD;

		JUnitMethod jUnitMethod = null;
		while (MAX_UNIT_METHOD > 0) {
			try {

				if (!((Method) method).getReturnType().getName().equals("void") &&
						(AAPI.getFieldConstraints().containsKey(((Method) method).getReturnType()) || AAPI.getRetConstraints().containsKey(method))) {
					Config.CURRENT_METHOD_NAME = method.getName();
				} else {
					Config.CURRENT_METHOD_NAME = "";
				}
				jUnitMethod = JUnitMethod.create(method, max - MAX_UNIT_METHOD,
						methods.size());
				MAX_UNIT_METHOD--;
				if (jUnitMethod != null) {
					methods.add(jUnitMethod);
					ErrorWriter.addMethod(method);
				}
			} catch (Exception e) {
				e.printStackTrace();
				ErrorWriter.addError(e, method);
			} catch (StackOverflowError e) {
				Log.e("generateJUnitMethod  StackOverflowError");
				e.printStackTrace();
				ErrorWriter.addError(e, method);
			} catch (Error e) {
				ErrorWriter.addError(e, method);
				e.printStackTrace();
			}

		}
	}

	public static boolean isIgnoredMethod(Executable method) {
		int mod = method.getModifiers();
		return Modifier.isAbstract(mod) || Modifier.isPrivate(mod) || Modifier.isProtected(mod)
				|| Modifier.isSynchronized(mod);
	}

}
