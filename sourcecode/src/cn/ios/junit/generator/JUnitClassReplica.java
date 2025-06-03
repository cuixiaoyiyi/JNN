package cn.ios.junit.generator;

import cn.ios.ju.base.Log;
import cn.ios.junit.config.Config;
import cn.ios.junit.output.ErrorWriter;
import cn.ios.junit.stmt.JUDeclareStmt;
import cn.ios.junit.stmt.JUStmtFactory;
import cn.ios.junit.value.*;
import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class JUnitClassReplica {

	private Class<?> clazzUnderAnalysis = null;
	private String className = null;
	private String packageName = null;
	private String junitType = null;

	// unit test method list
	private ArrayList<JUnitMethodReplica> methods = new ArrayList<JUnitMethodReplica>();
	// annotation method list
	private ArrayList<JunitAnnotationMethod> annotationMethods = new ArrayList<JunitAnnotationMethod>();
	// declareStmt list for member variable
	private ArrayList<JUDeclareStmt> juDeclareStmts = new ArrayList<JUDeclareStmt>();
	// member variable list
	private ArrayList<JUVarable> juVariables = new ArrayList<JUVarable>();

	public JUnitClassReplica(String junitType, Class<?> sootClass) {
		this.junitType = junitType;
		clazzUnderAnalysis = sootClass;

		className = clazzUnderAnalysis.getSimpleName() + "_Test";
		packageName = clazzUnderAnalysis.getPackage() != null ? clazzUnderAnalysis.getPackage().getName() : null;
	}

	public JUnitClassReplica start() {
		generateMemberVariableStmt();
		generateAnnotationMethod(junitType);
		generateJunitMethodWithAnnotation();
		return this;
	}

	// create member variable
	private void generateMemberVariableStmt() {
		FullClassType fullClassType = new FullClassType(clazzUnderAnalysis);
		//TODO constraints may be not null
		JUVarable classVariable = VarableImpl.v(fullClassType,null, null, null, new VarableNameFactory());
		JUDeclareStmt juDeclareStmt = JUStmtFactory.createDeclareStmt(classVariable);
		juDeclareStmts.add(juDeclareStmt);
		juVariables.add(classVariable);
	}

	/* add annotation method in junit4 or junit5 */
	private void generateAnnotationMethod(String junit){
		String[] annotations;
		if (junit.equals(Config.JUNIT4)){
			annotations = new String[]{Config.BEFORE_CLASS_ANNOTATION, Config.AFTER_CLASS_ANNOTATION,
					Config.BEFORE_ANNOTATION, Config.AFTER_ANNOTATION};
		} else if (junit.equals(Config.JUNIT5)){
			annotations = new String[]{Config.BEFORE_ALL_ANNOTATION, Config.AFTER_ALL_ANNOTATION,
					Config.BEFORE_EACH_ANNOTATION, Config.AFTER_EACH_ANNOTATION};
		} else{
			return;
		}
		for (String annotation : annotations) {
			JunitAnnotationMethod junitAnnotationMethod = new JunitAnnotationMethod(annotation,juDeclareStmts);
			annotationMethods.add(junitAnnotationMethod);
		}
	}

	private void generateJunitMethodWithAnnotation(){
		try {
			for (Executable method : clazzUnderAnalysis.getDeclaredMethods()) {
				generateJUnitMethod(method, methods,juVariables);
			}
		} catch (Exception | Error e) {
			Log.e(e.getClass().getName(), " ", clazzUnderAnalysis);
		}
	}

	public void addMethod(JUnitMethodReplica method) {
		methods.add(method);
	}

	public ArrayList<JUnitMethodReplica> getMethods() {
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
		if (junitType.equals(Config.JUNIT4)){
			stringBuffer.append("import org.junit.Test;\n");
			stringBuffer.append("import org.junit.Before;\n");
			stringBuffer.append("import org.junit.After;\n");
			stringBuffer.append("import org.junit.BeforeClass;\n");
			stringBuffer.append("import org.junit.AfterClass;\n");
		}else if(junitType.equals(Config.JUNIT5)){
			stringBuffer.append("import org.junit.jupiter.api.Test;\n");
			stringBuffer.append("import org.junit.jupiter.api.BeforeEach;;\n");
			stringBuffer.append("import org.junit.jupiter.api.AfterEach;;\n");
			stringBuffer.append("import org.junit.jupiter.api.BeforeAll;;\n");
			stringBuffer.append("import org.junit.jupiter.api.AfterAll;\n");
		}


		/** class */
		stringBuffer.append("\npublic class " + className + " {\n\n");
		/** methods */
		for (JUDeclareStmt juDeclareStmt: juDeclareStmts) {
			stringBuffer.append(juDeclareStmt);
			stringBuffer.append("\n\n");
		}
		for (JunitAnnotationMethod annotationMethod : annotationMethods){
			stringBuffer.append(annotationMethod.toString());
			stringBuffer.append("\n\n");
		}
		for (JUnitMethodReplica method : methods) {
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

	public static void generateJUnitMethod(Executable method, ArrayList<JUnitMethodReplica> methods, List<JUVarable> juVariables) {
		method.getModifiers();
		/**
		 * Take java bridge method into account.( Modifier.isSynthetic(method) )
		 * 20200723
		 */
		if (isIgnoredMethod(method)) {
			return;
		}
		int MAX_UNIT_METHOD = Config.MAX_UNIT_METHOD;
		JUnitMethodReplica jUnitMethod = null;
		while (MAX_UNIT_METHOD > 0) {
			try {
				jUnitMethod = JUnitMethodReplica.create(method, Config.MAX_UNIT_METHOD - MAX_UNIT_METHOD,
						methods.size(), juVariables);

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
			MAX_UNIT_METHOD--;
			if (jUnitMethod != null) {
				methods.add(jUnitMethod);
				ErrorWriter.addMethod(method);
			}
		}
	}

	public static boolean isIgnoredMethod(Executable method) {
		int mod = method.getModifiers();
		return Modifier.isAbstract(mod) || Modifier.isPrivate(mod) || Modifier.isProtected(mod)
				|| Modifier.isSynchronized(mod);
	}

}
