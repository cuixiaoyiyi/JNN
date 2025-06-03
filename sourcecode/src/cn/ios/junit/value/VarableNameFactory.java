package cn.ios.junit.value;

import cn.ios.junit.generic.type.IType;
import soot.SootMethod;

public class VarableNameFactory {

	private int index = 0;

	private SootMethod method = null;

	private JUVarable juVariable = null;

	public VarableNameFactory() {
	}

	public VarableNameFactory(JUVarable juVariable) {
		this.juVariable = juVariable;
	}

	public VarableNameFactory(SootMethod method) {
		this.method = method;
	}

	public String getPackageName() {
		return method.getDeclaringClass().getPackageName();
	}

	public String getName(IType type) {
		String varableName = getClassShortNameFirstCharLowerCase(type);
		if (type.isArray()) {
			varableName += "Array";
		}
		varableName += index++;
		return varableName;
	}

	private String getClassShortNameFirstCharLowerCase(IType type) {
		String varableName = type.getBaseElementType().toString();
		String firstChar = varableName.substring(0, 1);
		varableName = firstChar.toLowerCase() + varableName.substring(1);
		return varableName;
	}

	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(" { ");
		stringBuffer.append(method.getName());
		stringBuffer.append(" }");
		return super.toString() + stringBuffer.toString();
	}

	public JUVarable getJuVariable() {
		return juVariable;
	}

}
