package cn.ios.junit.stmt;

import java.lang.reflect.Executable;
import cn.ios.junit.value.FullClassType;
import cn.ios.junit.value.JUVarable;
import cn.ios.junit.value.VarableNameFactory;

public class JUStmtFactory {

	public static JUInvokeStmt createInvokeStmt(Executable method, JUVarable caller,
			VarableNameFactory nameFactory, FullClassType fullClassType, int methodIndex) {
		JUInvokeStmt invokeStmt = new JUInvokeStmt(method, caller, nameFactory, fullClassType, methodIndex);
		return invokeStmt;
	}

	public static JUDefinitionStmt createDefinitionStmt(JUVarable variable, JUStmt stmt, int methodIndex, boolean random) {
		JUDefinitionStmt definitionStmt = new JUDefinitionStmt(variable, stmt, methodIndex, random);
		return definitionStmt;
	}

	public static JUDefinitionStmt createDefinitionStmt1(JUVarable variable) {
		JUDefinitionStmt definitionStmt = new JUDefinitionStmt(variable);
		return definitionStmt;
	}

	public static JUDeclareStmt createDeclareStmt(JUVarable variable){
		return new JUDeclareStmt(variable);
	}

	public static JUAssignStmt createAssignStmt(JUVarable juVarable){
		return new JUAssignStmt(juVarable);
	}

}
