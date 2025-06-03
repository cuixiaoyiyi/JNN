package cn.ios.junit.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.ios.ju.base.InheritanceUtil;
import cn.ios.junit.API;
import cn.ios.junit.config.Config;
import cn.ios.junit.value.FullClassType;
import cn.ios.junit.value.VarableNameFactory;
import cn.ios.util.Randomness;

public class FindRealClass {

	private static Map<Class<?>, List<Class<?>>> classMap = new HashMap<Class<?>, List<Class<?>>>();

	private static Map<Class<?>, List<Constructor<?>>> construcorMap = new HashMap<Class<?>, List<Constructor<?>>>();

	public static void clear() {
		classMap.clear();
		construcorMap.clear();
	}

	public static Class<?> getPossibleClass(Class<?> key, VarableNameFactory nameFactory) {
//    	 if(key == Set.class) {
//         	return HashSet.class;
//         }
		Class<?> pointClass = key;
		if (pointClass == null) {
			return null;
		}
		if (ClassInheritanceProcess.isInheritedFromGivenClass(HashMap.class, pointClass)
				&& ClassInheritanceProcess.isInheritedFromGivenClass(pointClass, Map.class)) {
			return HashMap.class;
		} else if (ClassInheritanceProcess.isInheritedFromGivenClass(ArrayList.class, pointClass)
				&& ClassInheritanceProcess.isInheritedFromGivenClass(pointClass, List.class)) {
			return ArrayList.class;
		} else if (ClassInheritanceProcess.isInheritedFromGivenClass(HashSet.class, pointClass)
				&& ClassInheritanceProcess.isInheritedFromGivenClass(pointClass, Set.class)) {
			return HashSet.class;
		} else if (ClassInheritanceProcess.isInheritedFromGivenClass(ArrayList.class, pointClass)
				&& ClassInheritanceProcess.isInheritedFromGivenClass(pointClass, Collection.class)) {
			return ArrayList.class;
		}

		List<Class<?>> allPossible = null;
		if (classMap.containsKey(pointClass)) {
			allPossible = classMap.get(pointClass);
		} else {
			allPossible = new ArrayList<Class<?>>();
			classMap.put(pointClass, allPossible);

			if (shouldAddClass(pointClass, nameFactory)) {
				allPossible.add(pointClass);
			}
			Set<Class<?>> allClasses = Config.projectClasses;
			List<Class<?>> possibleClassesInProject = getPossible(allClasses, pointClass);
			if (possibleClassesInProject != null) {
				allPossible.addAll(possibleClassesInProject);
			} else {
				allClasses = API.getAllClassesJVMLoaded();
				List<Class<?>> possibleClassesInJDK = getPossible(allClasses, pointClass);
				if (possibleClassesInJDK != null) {
					allPossible.addAll(possibleClassesInJDK);
				}
			}

		}

		return allPossible.size() > 0 ? Randomness.choice(allPossible) : null;

	}

	private static List<Class<?>> getPossible(Set<Class<?>> allClasses, Class<?> pointClass) {
		List<Class<?>> allPossible = null;
		FullClassType fullClassType = new FullClassType(pointClass);
		for (Class<?> clazz : allClasses) {
			if (clazz == null) {
				continue;
			}
			if (clazz.getName().contains(".internal.")) {
				continue;
			}
			boolean deprecated = false;
			Annotation[] annotations = null;

			try {
				annotations = clazz.getAnnotations();
			} catch (Exception | Error e) {

			}
			if (annotations != null) {
				for (Annotation annotation : annotations) {
					if (annotation instanceof Deprecated) {
						deprecated = true;
						break;
					}
				}
			}

			if (deprecated) {
				continue;
			}
			if (InheritanceUtil.isInheritedFromGivenClass(clazz, pointClass) && !isAbstract(clazz) && isPublic(clazz)
					&& clazz.getTypeParameters().length == pointClass.getTypeParameters().length
					&& isSameList(new FullClassType(clazz).getGeneicElements(), fullClassType.getGeneicElements())) {

				TypeVariable<?>[] clazzTypeParameters = clazz.getTypeParameters();
				TypeVariable<?>[] pointClassTypeParameters = pointClass.getTypeParameters();
				boolean flag = true;
				for (int i = 0; i < pointClassTypeParameters.length; i++) {
					TypeVariable<?> type1 = pointClassTypeParameters[i];
					TypeVariable<?> type2 = clazzTypeParameters[i];
					flag &= type1.toString().equals(type2.toString());
				}
				if (flag) {
					if (allPossible == null) {
						allPossible = new ArrayList<Class<?>>();
					}
					allPossible.add(clazz);
				}
			}
		}
		return allPossible;
	}

	public static Constructor<?> getConstructMethod(Class<?> pointClass, VarableNameFactory nameFactory) {

		if (pointClass == null) {
			return null;
		}
//        if(pointClass == HashSet.class) {
//        	try {
//        		Constructor<?> constructor = HashSet.class.getConstructor(Collection.class);
//				return constructor;
//			} catch (NoSuchMethodException e) {
//				e.printStackTrace();
//			} catch (SecurityException e) {
//				e.printStackTrace();
//			}
//        }

		List<Constructor<?>> allPossible = null;
		if (construcorMap.containsKey(pointClass)) {
			allPossible = construcorMap.get(pointClass);
		} else {
			allPossible = new ArrayList<Constructor<?>>();
			List<Constructor<?>> nested = new ArrayList<Constructor<?>>();
			construcorMap.put(pointClass, allPossible);
			for (Constructor<?> constructor : pointClass.getDeclaredConstructors()) {
				if (shouldAddConstructor(constructor, nameFactory)) {
					allPossible.add(constructor);
					for (Class<?> parameterType : constructor.getParameterTypes()) {
						if (InheritanceUtil.isInheritedFromGivenClass(pointClass, parameterType)) {
							nested.add(constructor);
							break;
						}
					}
				}

			}
			if (allPossible.size() > nested.size()) {
				for (Constructor<?> method : nested) {
					allPossible.remove(method);
				}
			}
			if (ClassInheritanceProcess.isInheritedFromGivenClass(pointClass, Collection.class)
					|| ClassInheritanceProcess.isInheritedFromGivenClass(pointClass, Map.class)) {
				Collections.sort(allPossible, new Comparator<Constructor<?>>() {
					@Override
					public int compare(Constructor<?> o1, Constructor<?> o2) {
						if (o1 == null) {
							return 1;
						}
						if (o2 == null) {
							return -1;
						}
						return o1.getParameterCount() - o2.getParameterCount();
					}
				});
				// choose the constructor with fewer parameters in case of set classes 20220224
				// @cbq

				return allPossible.size() > 0 ? allPossible.get(0) : null;
			}
		}
		for (Constructor<?> constructor : allPossible) {
			if (pointClass == BigDecimal.class
					&& constructor.toString().equals("public java.math.BigDecimal(java.lang.String)")) {
				return constructor;
			}
		}
		return allPossible.size() > 0 ? Randomness.choice(allPossible) : null;
	}

	public static boolean shouldAddClass(Class<?> clazz, VarableNameFactory nameFactory) {
		if (!isAbstract(clazz) && isPublic(clazz)) {
			return true;
		}
		return isDefalut(clazz) && isInSamePackage(clazz, nameFactory);
	}

	public static boolean shouldAddConstructor(Constructor<?> method, VarableNameFactory nameFactory) {
		return Modifier.isPublic(method.getModifiers()) || (isDefalut(method) && isInSamePackage(method, nameFactory));
	}

	public static boolean isInSamePackage(Class<?> clazz, VarableNameFactory nameFactory) {
		String clazzPackageName = null;
		String nameFactoryPackageName = nameFactory.getPackageName();
		if (clazz.getPackage() != null) {
			clazzPackageName = clazz.getPackage().getName();
		}
		return isInSamePackage(clazzPackageName, nameFactoryPackageName);
	}

	public static boolean isInSamePackage(Constructor<?> method, VarableNameFactory nameFactory) {
		String clazzPackageName = method.getDeclaringClass().getPackage().getName();
		String nameFactoryPackageName = nameFactory.getPackageName();
		return isInSamePackage(clazzPackageName, nameFactoryPackageName);
	}

	public static boolean isInSamePackage(String package1, String package2) {
		if (package1 == null) {
			return package2 == null;
		}
		return package1.equals(package2);
	}

	public static boolean isDefalut(Class<?> clazz) {
		return !(isPublic(clazz) || isPrivate(clazz) || isProtected(clazz));
	}

	public static boolean isDefalut(Constructor<?> method) {
		int modifiers = method.getModifiers();
		return !(Modifier.isPublic(modifiers) || Modifier.isPrivate(modifiers) || Modifier.isProtected(modifiers));
	}

	public static boolean isAbstract(Class<?> clazz) {
		return Modifier.isAbstract(clazz.getModifiers()) || clazz.isInterface();
	}

	public static boolean isPublic(Class<?> clazz) {
		return Modifier.isPublic(clazz.getModifiers());
	}

	public static boolean isPrivate(Class<?> clazz) {
		return Modifier.isPrivate(clazz.getModifiers());
	}

	public static boolean isProtected(Class<?> clazz) {
		return Modifier.isProtected(clazz.getModifiers());
	}

	public static boolean isSameList(List<FullClassType> list1, List<FullClassType> list2) {
		for (FullClassType type1 : list1) {
			boolean tag = true;
			for (FullClassType type2 : list2) {
				if (!type1.getClassType().getName().equals(type2.getClassType().getName())) {
					tag = false;
				} else {
					tag = true;
					break;
				}
			}
			if (!tag) {
				return false;
			}
		}
		return true;
	}

}
