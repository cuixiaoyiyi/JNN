package cn.ios.junit.config;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

import cn.ios.ju.base.Log;
import cn.ios.junit.gui.JUAFrame;

public final class Config {

	public static final int PRIM_ARRAY_MIN_SIZE = 1;
	public static final int PRIM_ARRAY_MAX_SIZE = 5;

	public static int REF_ARRAY_MIN_SIZE = 1;
	public static int REF_ARRAY_MAX_SIZE = 5;

	public static final int STRING_MIN_LENGTH = 0;
	public static int STRING_MAX_LENGTH = 20;

	public static int MIN_NESTED_OBJECT = 0;
	public static int MAX_NESTED_OBJECT = 5;
	public static String SRC_PATH = null;

//	public static final String TARGET_BYTE_CODE_CLASS_PATH = "C:\\Users\\C\\eclipseworkspace\\code\\Sort\\bin";
	public static LinkedHashSet<String> TARGET_BYTE_CODE_CLASS_PATH ;
	public static int MAX_UNIT_METHOD = 1;

	public static String CURRENT_METHOD_NAME = "";
	public static String CURRENT_ASSERT_NAME = "";
	public static boolean IF_IMPORT_BIG_DECIMAL = false;

	public static Set<Class<?>> projectClasses = null;

	public static String TEST_SOURCE_FOLDER = null;

	public static String BEFORE_ANNOTATION = "@Before";
	public static String AFTER_ANNOTATION = "@After";
	public static String BEFORE_CLASS_ANNOTATION = "@BeforeClass";
	public static String AFTER_CLASS_ANNOTATION = "@AfterClass";
	public static String OFFSET_SPACE = "    ";
	public static String BEFORE_EACH_ANNOTATION = "@BeforeEach";
	public static String AFTER_EACH_ANNOTATION = "@AfterEach";
	public static String BEFORE_ALL_ANNOTATION = "@BeforeAll";
	public static String AFTER_ALL_ANNOTATION = "@AfterAll";
	public static String JUNIT4 = "JUNIT4";
	public static String JUNIT5 = "JUNIT5";


	public static String getOutputFolder() {n

		if (TEST_SOURCE_FOLDER == null) {
			String pathString = TARGET_BYTE_CODE_CLASS_PATH.iterator().next();
			String separator = File.separator;
			if (!pathString.endsWith(separator)) {
				pathString += separator;
			}
			boolean condtion = pathString.endsWith("bin" + separator);
			TEST_SOURCE_FOLDER = ((condtion ? pathString.substring(0, pathString.lastIndexOf("bin")) : pathString.replace(".jar", "Jar"))
					+ "monkeytest" + separator);
		}
		Log.e("getOutputFolder  ", TEST_SOURCE_FOLDER);
		if (JUAFrame.textArea != null) {
			JUAFrame.textArea.append("getOutputFolder  " + TEST_SOURCE_FOLDER + "\n");
			JUAFrame.textArea.paintImmediately(JUAFrame.textArea.getBounds());
		}
		return TEST_SOURCE_FOLDER;
	}

}
