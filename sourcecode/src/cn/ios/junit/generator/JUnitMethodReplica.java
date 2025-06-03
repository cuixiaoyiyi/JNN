package cn.ios.junit.generator;

import cn.ios.ju.base.Log;
import cn.ios.junit.config.Config;
import cn.ios.junit.stmt.JUStmt;
import cn.ios.junit.stmt.JUStmtFactory;
import cn.ios.junit.util.StringSplicing;
import cn.ios.junit.value.FullClassType;
import cn.ios.junit.value.JUVarable;
import cn.ios.junit.value.VarableNameFactory;

import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JUnitMethodReplica {

	public static final String INNER_BLOCK_SPACE = "      ";

	private Executable method = null;
	private int methodIndex = 0;
	private int internalIndex = 0;

	private JUStmt end = null;

	private Set<String> exceptions = new HashSet<String>();

	public JUnitMethodReplica(Executable method, int methodIndex, int internalIndex) {
		this.method = method;
		this.methodIndex = ++methodIndex;
		this.internalIndex = ++internalIndex;
	}

	private void generate(List<JUVarable> variables) {
		if (variables.isEmpty()){
			Log.e("member variables should not be null");
		}else{
			JUVarable caller = variables.get(0);
			end = JUStmtFactory.createInvokeStmt(method, caller, new VarableNameFactory(method),
					new FullClassType(method.getDeclaringClass()), methodIndex);
		}
	}

	private String getStmtString(JUStmt stmt) {
		addExceptions(stmt.getExceptions());
		StringBuffer result = new StringBuffer();
		if (stmt.getPreviouStmts() != null) {
			for (JUStmt p : stmt.getPreviouStmts()) {
				result.append(getStmtString(p));
			}
		}
		result.append(INNER_BLOCK_SPACE);
		String stmtString = stmt.toString();
		result.append(stmtString);
		result.append("\n");
		if (stmt.getSuccStmts() != null) {
			for (JUStmt p : stmt.getSuccStmts()) {
				result.append(getStmtString(p));
			}
		}
		return result.toString();
	}

	private void addExceptions(Set<Class<?>> sootClasses) {
		if (sootClasses != null) {
			for (Class<?> exception : sootClasses) {
				exceptions.add(exception.getName());
			}
		}
	}

	private String getExceptions() {
		if (Modifier.isPrivate(method.getModifiers())) {
			exceptions.add("NoSuchMethodException");
			exceptions.add("SecurityException");
			exceptions.add("IllegalAccessException");
			exceptions.add("IllegalArgumentException");
			exceptions.add("java.lang.reflect.InvocationTargetException");
		}
		List<String> tmp = new ArrayList<String>(exceptions);
		return StringSplicing.splicingParameter(tmp, 0, exceptions.size()).replace("$", ".");
	}

	@Override
	public String toString() {
		StringBuilder stringBuffer = new StringBuilder();
		String stmtString = getStmtString(end);
		stringBuffer.append(Config.OFFSET_SPACE);
		stringBuffer.append("@Test\n");
		stringBuffer.append(Config.OFFSET_SPACE);
		String exception = getExceptions();
		stringBuffer.append("public void test_" + method.getName() + "_" + methodIndex + "_" + internalIndex + "()"
				+ (exception.length() == 0 ? "" : " throws " + exception) + "{\n\n");
		stringBuffer.append(stmtString);
		stringBuffer.append("\n");
		stringBuffer.append(Config.OFFSET_SPACE);
		stringBuffer.append("}\n\n");
		return stringBuffer.toString();

	}

	public static JUnitMethodReplica create(Executable method, int methodIndex, int internalIndex,List<JUVarable> juVariables) {
		JUnitMethodReplica jUnitMethodReplica = new JUnitMethodReplica(method, methodIndex, internalIndex);
		jUnitMethodReplica.generate(juVariables);
		return jUnitMethodReplica;
	}
}
