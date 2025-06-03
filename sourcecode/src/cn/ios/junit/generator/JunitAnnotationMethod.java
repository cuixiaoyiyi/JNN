package cn.ios.junit.generator;

import cn.ios.ju.base.Log;
import cn.ios.junit.config.Config;
import cn.ios.junit.stmt.JUAssignStmt;
import cn.ios.junit.stmt.JUDeclareStmt;
import cn.ios.junit.stmt.JUStmtFactory;

import java.util.ArrayList;
import java.util.List;

public class JunitAnnotationMethod {
    public String annotationName = "";
    private List<JUAssignStmt> juAssignStmts = new ArrayList<JUAssignStmt>();
    private JUAssignStmt juStmt;

    public JunitAnnotationMethod(String annotationName, List<JUDeclareStmt> juDeclareStmts){
        this.annotationName = annotationName;
        if ((annotationName.equals(Config.BEFORE_ANNOTATION) || annotationName.equals(Config.BEFORE_EACH_ANNOTATION)) &&
                !juDeclareStmts.isEmpty()){
            generate(juDeclareStmts);
        }
    }

    // create assignStmt in @Before method
    private void generate(List<JUDeclareStmt> juDeclareStmts){
        for (JUDeclareStmt juDeclareStmt : juDeclareStmts) {
            juStmt= JUStmtFactory.createAssignStmt(juDeclareStmt.getVariable());
            juAssignStmts.add(juStmt);
        }

    }

    @Override
    public String toString() {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(Config.OFFSET_SPACE);
        switch(annotationName){
            case "@Before" :
                stringBuffer.append(Config.BEFORE_ANNOTATION).append("\n");
                break;
            case "@BeforeEach" :
                stringBuffer.append(Config.BEFORE_EACH_ANNOTATION).append("\n");
                break;
            case "@After" :
                stringBuffer.append(Config.AFTER_ANNOTATION).append("\n");
                break;
            case "@AfterEach" :
                stringBuffer.append(Config.AFTER_EACH_ANNOTATION).append("\n");
                break;
            case "@BeforeClass" :
                stringBuffer.append(Config.BEFORE_CLASS_ANNOTATION).append("\n");
                break;
            case "@BeforeAll" :
                stringBuffer.append(Config.BEFORE_ALL_ANNOTATION).append("\n");
                break;
            case "@AfterClass" :
                stringBuffer.append(Config.AFTER_CLASS_ANNOTATION).append("\n");
                break;
            case "@AfterAll" :
                stringBuffer.append(Config.AFTER_ALL_ANNOTATION).append("\n");
                break;
            default:
                Log.e("annotation methods error");
                return "";
        }
        stringBuffer.append(Config.OFFSET_SPACE);

        if (annotationName.equals(Config.BEFORE_ANNOTATION) ||
                annotationName.equals(Config.AFTER_ANNOTATION) ||
                annotationName.equals(Config.BEFORE_EACH_ANNOTATION) ||
                annotationName.equals(Config.AFTER_EACH_ANNOTATION)){
            stringBuffer.append("public void ");
        }else if (annotationName.equals(Config.BEFORE_CLASS_ANNOTATION) ||
                annotationName.equals(Config.AFTER_CLASS_ANNOTATION) ||
                annotationName.equals(Config.BEFORE_ALL_ANNOTATION) ||
                annotationName.equals(Config.AFTER_ALL_ANNOTATION)){
            stringBuffer.append("public static void ");
        }
        annotationName = annotationName.substring(1);
        annotationName = annotationName.substring(0,1).toLowerCase() + annotationName.substring(1);
        stringBuffer.append(annotationName).append("()").append("{\n\n");

        // add assign stmts in @before/@BeforeEach method
        if ((annotationName.equals("before") || annotationName.equals("beforeEach")) && !juAssignStmts.isEmpty()){
            stringBuffer.append(Config.OFFSET_SPACE);
            for (JUAssignStmt juAssignStmt: juAssignStmts) {
                stringBuffer.append(juAssignStmt.toString());
            }
        }
        stringBuffer.append("\n");
        stringBuffer.append(Config.OFFSET_SPACE);
        stringBuffer.append("}\n\n");
        return stringBuffer.toString();
    }
}
