package cn.ios.junit.stmt;

import cn.ios.junit.config.Config;
import cn.ios.junit.value.JUVarable;
import cn.ios.junit.value.VarableNameFactory;
import cn.ios.junit.value.expr.JUInvokeExpr;

public class JUDeclareStmt extends JUStmt {

    private JUVarable variable = null;

    public JUDeclareStmt(JUVarable variable){
        this.variable = variable;
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(Config.OFFSET_SPACE);
        stringBuffer.append(variable.getType().toString().replace("$", "."));
        stringBuffer.append(" ");
        stringBuffer.append(variable);
        stringBuffer.append(";");
        return stringBuffer.toString();
    }

    public JUVarable getVariable(){
        return this.variable;
    }
    public void setNameFactory(VarableNameFactory nameFactory){
        this.nameFactory = nameFactory;
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
