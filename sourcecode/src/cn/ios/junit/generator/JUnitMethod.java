package cn.ios.junit.generator;

import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.ios.junit.stmt.JUStmt;
import cn.ios.junit.stmt.JUStmtFactory;
import cn.ios.junit.util.StringSplicing;
import cn.ios.junit.value.FullClassType;
import cn.ios.junit.value.JUVarable;
import cn.ios.junit.value.VarableNameFactory;

public class JUnitMethod {

	public static final String METHOD_SPACE = "  ";
	public static final String BLOCK_SPACE = "    ";
	public static final String INNER_BLOCK_SPACE = "      ";
	public static final String INNER_INNER_BLOCK_SPACE = "        ";
	public static final String INNER_INNER_INNER_BLOCK_SPACE = "          ";

	private Executable method = null;
	private int methodIndex = 0;
	private int internalIndex = 0;

	private JUStmt end = null;

	private Set<String> exceptions = new HashSet<String>();

	public JUnitMethod(Executable method, int methodIndex, int internalIndex) {
		this.method = method;
		this.methodIndex = ++methodIndex;
		this.internalIndex = ++internalIndex;
	}

	private void generate() {

		end = JUStmtFactory.createInvokeStmt(method, null, new VarableNameFactory(method),
				new FullClassType(method.getDeclaringClass()), methodIndex - 1);

		if (end.containsJUInvokeExpr() && end.getJUInvokeExpr().getCaller() instanceof JUVarable) {
			JUVarable caller = (JUVarable) end.getJUInvokeExpr().getCaller();
			JUStmt definition = JUStmtFactory.createDefinitionStmt(caller, end, methodIndex -1, false );
			end.getPreviouStmts().add(definition);
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
//		result.append("\n");
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
		stringBuffer.append(METHOD_SPACE);
		stringBuffer.append("@Test\n");
		stringBuffer.append(METHOD_SPACE);
		String exception = getExceptions();
		stringBuffer.append("public void test_" + method.getName() + "_" + methodIndex + "_" + internalIndex + "() throws "
				+ (exception.length() == 0 ? "" : exception + ", " ) + "NoSuchFieldException, IllegalAccessException {\n\n");
		stringBuffer.append(stmtString);
		stringBuffer.append("\n");
		stringBuffer.append(METHOD_SPACE);
		stringBuffer.append("}\n\n");
		return stringBuffer.toString();

	}

	public static JUnitMethod create(Executable method, int methodIndex, int internalIndex) {
		JUnitMethod jUnitMethod = new JUnitMethod(method, methodIndex, internalIndex);
		jUnitMethod.generate();
		return jUnitMethod;
	}
}
