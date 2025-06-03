package cn.ios.ju.base;

import java.lang.reflect.Modifier;
import soot.SootClass;

public class Util {
	public static boolean isAnonymousInnerClass(SootClass clazz) {
		String className = clazz.getName();
		String[] splitStrings = className.split("\\$");
		for (String name : splitStrings) {
			if (name.length() > 0 && "0123456789".contains(String.valueOf(name.charAt(0)))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isIgnoredClass(SootClass clazz) {
		if (clazz.getName().equals(Object.class.getName())) {
			return true;
		}
		int modifiers = clazz.getModifiers();
		return Modifier.isProtected(modifiers) || Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers)
				|| Modifier.isPrivate(modifiers) || clazz.getName().contains("_Test") || isAnonymousInnerClass(clazz)
				|| clazz.getName().contains("$");
	}
}
