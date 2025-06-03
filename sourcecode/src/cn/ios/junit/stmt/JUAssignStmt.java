package cn.ios.junit.stmt;

import cn.ios.junit.config.Config;
import cn.ios.junit.value.*;
import cn.ios.junit.value.expr.JUInvokeExpr;

public class JUAssignStmt extends JUStmt {

	private JUVarable varable = null;
	private JUValue rightValue = null;

	// create AssignStmts according to declare stmt
	public JUAssignStmt(JUVarable juVariable) {
		this.varable = juVariable;
		createRightValue();
	}

	private void createRightValue() {
		// TODO
		rightValue = ValueFactory.createValue(varable.getType(), new VarableNameFactory(), varable.getConstraints(), varable.getPossibleValues());
		// if the Constructor method has parameters
		if (rightValue instanceof Dependable) { 
			Dependable dependable = (Dependable) rightValue;
			if (dependable.getDependentVarable() != null) {
				for (JUVarable parameter : dependable.getDependentVarable()) {
					JUStmt stmt = JUStmtFactory.createDefinitionStmt1(parameter);
					previouStmts.add(stmt);
				}
			}
		}
	}
 
	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		// add declare stmts for method parameters
		if (previouStmts.size() > 0) {
			stringBuffer.append("\n");
			for (JUStmt juStmt : previouStmts) {
				stringBuffer.append(Config.OFFSET_SPACE);
				stringBuffer.append(Config.OFFSET_SPACE);
				stringBuffer.append(juStmt.toString());
				stringBuffer.append("\n");
			}
		}

		stringBuffer.append(Config.OFFSET_SPACE);
		stringBuffer.append(Config.OFFSET_SPACE);
		stringBuffer.append(varable.getName());
		stringBuffer.append(" = ");
		stringBuffer.append(rightValue.toString());
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
