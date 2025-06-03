/*
 */

package cn.ios.junit.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Processing class inheritance relationship
 */
public class ClassInheritanceProcess {

	private static Map<String, Boolean> map = new HashMap<>();

	public static boolean isInheritedFromGivenClass(Class<?> theClass, Class<?> classNameUnderMatch) {
		String key = getKey(theClass, classNameUnderMatch);
		if (theClass == null || classNameUnderMatch == null) {
			map.put(key, false);
			return false;
		}
		if(map.containsKey(key)) {
			return map.get(key);
		}
		if (theClass.getName().equals(classNameUnderMatch.getName())) {
			map.put(key, true);
			return true;
		}
		if (theClass.getInterfaces() != null) {
			for (Class<?> interfaceClass : theClass.getInterfaces()) {
				if (isInheritedFromGivenClass(interfaceClass, classNameUnderMatch)) {
					map.put(key, true);
					return true;
				}
			}
		}
		return isInheritedFromGivenClass(theClass.getSuperclass(), classNameUnderMatch);
	}

	private static String getKey(Class<?> theClass, Class<?> classNameUnderMatch) {
		return theClass + ";" + classNameUnderMatch;
	}

	/**
	 * Checks whether the given class name belongs to a system package
	 * 
	 * @param className The class name to check
	 * @return True if the given class name belongs to a system package, otherwise
	 *         false
	 */
	public static boolean isClassInSystemPackage(String className) {
		return className.startsWith("android.") || className.startsWith("java.") || className.startsWith("javax.")
				|| className.startsWith("sun.") || className.startsWith("org.omg.")
				|| className.startsWith("org.w3c.dom.") || className.startsWith("com.google.")
				|| className.startsWith("com.android.") || className.startsWith("com.ibm.")
				|| className.startsWith("com.sun.") || className.startsWith("com.apple.")
				|| className.startsWith("org.w3c.") || className.startsWith("soot");
	}
}
