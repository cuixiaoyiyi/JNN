package cn.ios.junit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import cn.ac.ios.ann.AAPI;
import cn.ios.ju.base.Log;
import cn.ios.ju.base.Pair;
import cn.ios.ju.base.Util;
import cn.ios.junit.config.Config;
import cn.ios.junit.config.RtJar;
import cn.ios.junit.generator.JUnitClass;
import cn.ios.junit.generator.JUnitClassReplica;
import cn.ios.junit.gui.JUAFrame;
import cn.ios.junit.output.ErrorWriter;
import cn.ios.junit.output.JUnitWriter;
import cn.ios.junit.util.FindRealClass;
import cn.ios.junit.util.JarClassLoader;
import cn.ios.junit.util.JarUtil;
import cn.ios.junit.util.StringSplicing;
import cn.ios.util.Randomness;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

import com.github.curiousoddman.rgxgen.RgxGen;

public class API {
	
	private static Map<SootMethod, Map<Integer, List<Pair<String, Object>>>> constraints = null;
	private static Map<SootMethod, List<Pair<String, Object>>> retConstraints = null;
	private static Map<SootClass, Map<SootField, List<Pair<String, Object>>>> fieldConstraints = null;
	private static Map<SootMethod, Map<Integer, List<Object>>> availableValues = null;
	
	private static boolean isCancelled = false;

	public static void generateClassList(String target) {
		generateClassList(target, null, 3);
	}
	
	private static void reset() {
		if (constraints != null) {
			constraints.clear();
			constraints = null;
		}
		if (availableValues != null) {
			availableValues.clear();
			availableValues = null;
		}
		if (fieldConstraints != null) {
			fieldConstraints.clear();
			fieldConstraints = null;
		}
		if (retConstraints != null) {
			retConstraints.clear();
			retConstraints = null;
		}
		if (allClasses != null) {
			allClasses.clear();
			allClasses = null;
		}
		if (Config.TARGET_BYTE_CODE_CLASS_PATH != null) {
			Config.TARGET_BYTE_CODE_CLASS_PATH.clear();
			Config.TARGET_BYTE_CODE_CLASS_PATH = null;
		}
		if (Config.projectClasses != null) {
			Config.projectClasses.clear();
			Config.projectClasses = null;
		}
		if (JUnitWriter.srcClasses != null) {
			JUnitWriter.srcClasses.clear();
		}

		JarClassLoader.clear();
		ErrorWriter.clear();
		FindRealClass.clear();
	}

	public static void generateClassListWithAnnotation(String target, String junitType) {
		generateClassListWithAnnotation(target, null, 1, junitType);
	}

	public static void generateClassList(String target, String dependency) {
		generateClassList(target, dependency, 1);
	}

	public static void generateClassList(String target, String dependency, int n) {
		if (n < 1) {
			n = 1;
			Log.e("Illegal n. Reset n = 1");
		}
		config(target, dependency, n);
		generateClasses(Config.projectClasses, 0);
	}

	public static void generateClassList(String target, String dependency,String outputLocation, String sourceFolder, int n){
		if (n < 1) {
			n = 1;
			Log.e("Illegal n. Reset n = 1");
		}
		config(target,dependency,outputLocation,sourceFolder,n);
		generateClasses(Config.projectClasses, 0);
	}


	public static void generateClassListWithAnnotation(String target, String dependency, int n, String junitType) {
		if (n < 1) {
			n = 1;
			Log.e("Illegal n. Reset n = 1");
		}
		config(target, dependency, n);
		generateClassesWithAnnotation(Config.projectClasses, 0, junitType);
	}

	/**
	 *
	 * @param target
	 * @param dependency
	 * @param n
	 */
	private static void config(String target, String dependency, int n) {
		config(target, dependency, null, null, n);
	}

	/**
	 *
	 * @param target
	 * @param dependency
	 * @param n
	 */
	public static void config(String target, String dependency, String outputLocation, String sourceFolder, int n) {
		reset();
		HashSet<String> targetJarSet = new HashSet<String>(Arrays.asList(target.split(";")));

		Config.TARGET_BYTE_CODE_CLASS_PATH = new LinkedHashSet<String>();
		Config.TARGET_BYTE_CODE_CLASS_PATH.addAll(targetJarSet);

		Config.SRC_PATH = sourceFolder;

		Config.MAX_UNIT_METHOD = n;

		Config.TEST_SOURCE_FOLDER = outputLocation;

		HashSet<String> dependencySet = new HashSet<String>();
		if (dependency != null) {
			dependencySet.addAll(Arrays.asList(dependency.split(";")));
			Config.TARGET_BYTE_CODE_CLASS_PATH.addAll(dependencySet);
		}

		// load jars
		JarUtil.loadJars(Config.TARGET_BYTE_CODE_CLASS_PATH);

		allClasses = new HashSet<>();
		// load target jar byte code
		Config.projectClasses = new HashSet<Class<?>>();
		for (String str : targetJarSet) {
			Set<String> set = getJavaByteCodeFileByPath(str);
			for (String className : set) {
				Config.projectClasses.add(getJavaClassWithLoaderClass(className));
			}
		}
		// load rt jar byte code
		for (String className : RtJar.RT_CLASSES) {
			allClasses.add(getJavaClassForName(className));
		}
		// load dependency jar byte code
		for (String strPath : dependencySet) {
			for (String className : getJavaByteCodeFileByPath(strPath)) {
				allClasses.add(getJavaClassForName(className));
			}
		}

		constraints = AAPI.getConstraints(Config.projectClasses);
		fieldConstraints = AAPI.getFieldConstraints();
		retConstraints = AAPI.getRetConstraints();
		availableValues = getAllPossibleValues(constraints);

		ErrorWriter.clear(); 
	}

	public static Set<String> getJavaByteCodeFileByPath(String str) {
		if (str.endsWith(".jar") || str.endsWith(".Jar") || str.endsWith(".JAR")) {
			return API.getJavaByteCodeFileByJar(str);
		} else {
			return API.getJavaByteCodeFileByFolder(str);
		}
	}

	public static Set<String> getJavaSourceFileByFolder(String folderPath) {
		return getFileBySuffixInFolder(folderPath, ".java");
	}

	public static Set<String> getJavaByteCodeFileByFolder(String folderPath) {
		return getFileBySuffixInFolder(folderPath, ".class");
	}

	private static Set<String> getFileBySuffixInFolder(String folderPath, String suffix) {
		return getFileBySuffixInFolder(folderPath, suffix, "");
	}

	private static Set<String> getFileBySuffixInFolder(String folderPath, String suffix, String pre) {
		Set<String> classes = new HashSet<String>();
		File file = new File(folderPath);
		if (file.exists()) {
			File[] list = file.listFiles();
			if (list != null) {
				for (File subFile : list) {
					String absolutePath = subFile.getAbsolutePath();
					if (subFile.isDirectory()) {// folder : recursion
						String subPre = pre;
						if (subPre != "") {
							subPre += ".";
						}
						int offSet = folderPath.endsWith(File.separator) ? 0 : 1;
						subPre += absolutePath.substring(folderPath.length() + offSet);
						classes.addAll(getFileBySuffixInFolder(subFile.getAbsolutePath(), suffix, subPre));
					} else {
						if (subFile.getName().endsWith(suffix)) {// file : add
							String subPre = pre;
							if (subPre != "") {
								subPre += ".";
							}
							String className = subPre + absolutePath.substring(folderPath.length() + 1);
							className = className.substring(0, className.lastIndexOf(suffix));
							classes.add(className);
						}
					}
				}
			}
		}
		return classes;

	}

	public static Set<String> getJavaByteCodeFileByJar(String jarFilePath) {
		Set<String> list = new HashSet<String>();
		File file = new File(jarFilePath);
		if (!file.exists()) {
			Log.e("file does not exist!! ", file.getAbsolutePath());
			return list;
		}
		java.util.jar.JarFile jarFile;
		try {
			jarFile = new JarFile(file);
			String jarClassPath = "";
			for (Entry<Object, Object> entry : jarFile.getManifest().getMainAttributes().entrySet()) {
				if (entry.getKey() instanceof Attributes.Name) {
					Attributes.Name name = (Name) entry.getKey();
					if (name.toString().equals("Spring-Boot-Classes")) {
						jarClassPath = entry.getValue().toString().replace("\\", ".").replace("/", ".");
						Log.i("jarClassPath = ", jarClassPath);
					}
				}
			}
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry jarEntry = entries.nextElement();
				String jarEntryName = jarEntry.getName();
				jarEntry.isDirectory();
				if (jarEntryName.endsWith(".class")) {
					jarEntryName = jarEntryName.replace("\\", ".");
					jarEntryName = jarEntryName.replace("/", ".");
					jarEntryName = jarEntryName.substring(0, jarEntryName.lastIndexOf(".class"));
					if (jarClassPath == "") {
						list.add(jarEntryName);
					} else {
						if (jarEntryName.startsWith(jarClassPath)) {
							list.add(jarEntryName.substring(jarClassPath.length()));
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 *
	 * @param classSignature
	 * @param methodName
	 * @param returnType
	 * @param args
	 */
	public static void generateMethod(String classSignature, String returnType, String methodName, String... args) {
		Class<?> clazz = getJavaClassWithLoaderClass(classSignature);
		if (clazz == null) {
			Log.e("Can not find class : ", classSignature);
			return;
		}

		boolean flag = false;
		Log.i("classSignature=", classSignature);
		Log.i("returnType=", returnType);
		Log.i("methodName=", methodName);
		Log.i("classSignature=", classSignature);
		Log.i("args=", args);
		for (Method method : clazz.getMethods()) {
			if (method.getName().equals(methodName)) {
				if (method.getReturnType().toString().contains(".")) {
					flag = method.getReturnType().toString().endsWith("." + returnType);
				} else {
					flag = method.getReturnType().toString().equals(returnType);
				}
				flag = flag || method.getReturnType().toString().endsWith("$" + returnType);
				if (args != null) {
					flag = flag && args.length == method.getParameterCount();
					if (!flag) {
						continue;
					}
					for (int i = 0; i < method.getParameterCount(); i++) {
						String para = method.getParameters()[i].getParameterizedType().getTypeName().toString();
						if (para.contains(".")) {
							if(para.contains("<")) {
								int index = para.indexOf('<');
								para = para.substring(0, index);
							}
							flag = flag && para.endsWith("." + args[i]);
						} else {
							flag = flag && para.equals(args[i]);
						}
						flag = flag || para.endsWith("$" + args[i]);
					}
				}
			}
			if (flag) {
				Log.i("Find method ==> ", method.getName());
				generateMethod(method);
				break;
			}
		}
		if (!flag) {
			Log.e("Can not find method: <" + classSignature + " " + returnType + " " + methodName
					+ StringSplicing.splicingParameterParentheses(args) + ">");
		}
	}

	public static Set<Class<?>> getClassesByKey(String key) {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		for (Class<?> clazz : Config.projectClasses) {
			if (clazz.getName().contains(key) && !Util.isIgnoredClass(clazz)) {
				classes.add(clazz);
			}
		}
		return classes;
	}

	public static Class<?> getJavaClassForName(String className) {
		Class<?> clazz = Object.class;
		try {
			clazz = Class.forName(className, true, JarClassLoader.getInstance());
		} catch (ClassNotFoundException e) {
			// e.printStackTrace();
			Log.i("No class -- > " + className);
		} catch (Exception e) {
			Log.i("No class  -- > " + className);
		} catch (Error e) {
			Log.i("No class  -- > " + className);
		}
		return clazz;
	}
	
	public static Class<?> getJavaClassWithLoaderClass(String className) {
		Class<?> clazz = Object.class;
		try {
			clazz = JarClassLoader.getInstance().loadClass(className);
		} catch (ClassNotFoundException e) {
			// e.printStackTrace();
			Log.i("No class -- > " + className);
		} catch (Exception e) {
			Log.i("No class  -- > " + className);
		} catch (Error e) {
			Log.i("No class  -- > " + className);
		}
		return clazz;
	}

	public static void generateMethod(Executable method) {
		if (method == null) {
			Log.e("The formate of methodSignature is EEEEEEEEEEEEEEEEEError !!! method");
			return;
		}
		if (Util.isIgnoredClass(method.getDeclaringClass())) {
			return;
		}
		JUnitClass jUnitClass = new JUnitClass(method.getDeclaringClass());
		JUnitClass.generateJUnitMethod(method, jUnitClass.getMethods());
		JUnitWriter.write(jUnitClass);
	}
	
	public interface OnProgressListener{
		void onUpdate(String info, int total, int current);
	}

	public static void generateClasses(Set<String> classes) {
		int all = classes.size();
		int i = 1;
		Log.e("## Start  ## " + all);
		for (String className : classes) {
			Log.e("## Woking ## " + i++ + "/" + all);

			if(isCancelled()) {
				if(onProgressListener!=null) {
					onProgressListener.onUpdate(className, i, i);
				}
				break;
			}
			if(onProgressListener!=null) {
				onProgressListener.onUpdate(className, all, i);
			}
			generateClass(getJavaClassWithLoaderClass(className));
		}
		Log.e("## End  all## ");
		ErrorWriter.write();
	}

	public static void generateClasses(Set<Class<?>> classes, int j) {
		int all = classes.size();
		int i = 1;
		Log.e("## Start  ## " + all);
		if (JUAFrame.textArea != null) {
			JUAFrame.textArea.append("## Start  ## " + all + "\n");
			JUAFrame.textArea.paintImmediately(JUAFrame.textArea.getBounds());
		}
		for (Class<?> clazz : classes) {
			if(isCancelled()) {
				if(onProgressListener!=null) {
					onProgressListener.onUpdate(clazz.getName(), i, i);
				}
				break;
			}
			if(onProgressListener!=null) {
				onProgressListener.onUpdate(clazz.getName(), all, i);
			}
			Log.e("## Woking ## " + i + "/" + all + clazz);
			if (JUAFrame.textArea != null) {
				JUAFrame.textArea.append("## Woking ## " + i + "/" + all + "\n");
				JUAFrame.textArea.paintImmediately(JUAFrame.textArea.getBounds());
			}
			i++;
			Config.IF_IMPORT_BIG_DECIMAL = false;
			generateClass(clazz);
			Config.IF_IMPORT_BIG_DECIMAL = false;
		}

		ErrorWriter.write();
	}

	public static void generateClassesWithAnnotation(Set<Class<?>> classes, int j, String junitType) {
		int all = classes.size();
		int i = 1;
		Log.e("## Start  ## " + all);
		for (Class<?> clazz : classes) {
			if(isCancelled()) {
				if(onProgressListener!=null) {
					onProgressListener.onUpdate(clazz.getName(), i, i);
				}
				break;
			}
			if(onProgressListener!=null) {
				onProgressListener.onUpdate(clazz.getName(), all, i);
			}
			Log.e("## Woking ## " + i++ + "/" + all);
			generateClassWithAnnotation(junitType, clazz);
		}
		ErrorWriter.write();
	}

	public static void generateClass(Class<?> clazz) {
		if (Util.isIgnoredClass(clazz)) {
			return;
		}
		JUnitClass jUnitClass = new JUnitClass(clazz);
		jUnitClass = jUnitClass.start();
		JUnitWriter.write(jUnitClass);
	}

	public static void generateClassWithAnnotation(String junitType, Class<?> clazz) {
		if (Util.isIgnoredClass(clazz)) {
			return;
		}
		JUnitClassReplica jUnitClassReplica = new JUnitClassReplica(junitType, clazz);
		jUnitClassReplica = jUnitClassReplica.start();
		JUnitWriter.write(jUnitClassReplica);
	}
	
	public static  Map<Parameter, List<Pair<String, Object>>> getMethodConstraints(Executable executable){
		if(constraints == null) {
			return null;
		}
		return constraints.get(executable);
	}
	
	public static  List<Pair<String, Object>> getParameterConstraints(Parameter parameter){
		if(getMethodConstraints(parameter.getDeclaringExecutable()) == null) {
			return null;
		}
		return getMethodConstraints(parameter.getDeclaringExecutable()).get(parameter);
	}

	public static List<Pair<String, Object>> getRetConstraints(Method method){
		if (retConstraints.containsKey(method)) {
			return retConstraints.get(method);
		}
		return null;
	}

	public static Map<Field, List<Pair<String, Object>>> getFieldConstraints(Class<?> classType){
		if (fieldConstraints.containsKey(classType)) {
			return fieldConstraints.get(classType);
		}
		return null;
	}

	public static int getMaxAvailableValuesNum(Executable method){
		int num = 0;
		if (availableValues.containsKey(method)){
			Map<Parameter, List<Object>> paraValuesMap = availableValues.get(method);
			for (Entry<Parameter, List<Object>> parameterListEntry : paraValuesMap.entrySet()) {
				num = Math.max(parameterListEntry.getValue().size(), num);
			}
		}
		return num;
	}

	public static Map<Executable, Map<Parameter, List<Object>>> getAllPossibleValues(Map<Executable, Map<Parameter, List<Pair<String, Object>>>> constraints) {
		Map<Executable, Map<Parameter, List<Object>>> result = new HashMap<>();
		for (Entry<Executable, Map<Parameter, List<Pair<String, Object>>>> executableMapEntry : constraints.entrySet()) {
			Map<Parameter, List<Object>> paraListMap = new HashMap<>();
			for (Entry<Parameter, List<Pair<String, Object>>> parameterListEntry : executableMapEntry.getValue().entrySet()) {
				List<Pair<String, Object>> paraConstraints = parameterListEntry.getValue();
				Class<?> type = parameterListEntry.getKey().getType();
				ArrayList<Object> paraPossibleValues = getParaPossibleValues(type, paraConstraints);
				if (!paraPossibleValues.isEmpty()){
					paraListMap.put(parameterListEntry.getKey(),paraPossibleValues);
				}
			}
			if (!paraListMap.isEmpty()) {
				result.put(executableMapEntry.getKey(),paraListMap);
			}
		}
		return result;
	}


	public static ArrayList<Object> getParaPossibleValues(Class<?> type, List<Pair<String, Object>> paraConstraints) {
		ArrayList<Object> paraPossibleValues =  new ArrayList<>();
		if (paraConstraints == null || paraConstraints.isEmpty()) return paraPossibleValues;
		// for common annotation
		for (Pair<String, Object> paraConstraint : paraConstraints) {
			if (paraConstraint.getKey().equals("DEFAULT_VALUE") ||
					paraConstraint.getKey().equals("FORMAT_WITH")) {
				paraPossibleValues.add(paraConstraint.getValue());
			}
		}

		try {
			if (AAPI.isStringType(type)){
				for (Pair<String, Object> paraConstraint : paraConstraints) {
					if (paraConstraint.getKey().equals("match")){
						paraPossibleValues.add(new RgxGen(paraConstraint.getValue().toString()).generate().replaceAll("\n"," "));
					} else if (paraConstraint.getKey().equals("!=") || (paraConstraint.getKey().equals("=="))) {
						paraPossibleValues.add(new RgxGen(".+").generate().replaceAll("\n"," "));
						paraPossibleValues.add("");
					}
				}
				List<Pair<String, Object>> tempLists = new ArrayList<>();
				tempLists.addAll(paraConstraints);
				tempLists.add(new Pair<>("==", 0));
				tempLists.add(new Pair<>("==", 150));

				List<Object> lengthValueList = tempLists.stream().filter(Pair::isNumberType).map(Pair::getValue).sorted().distinct().collect(Collectors.toList());
				List<Object> possibleLength = new ArrayList<>();
				if (lengthValueList.size() > 2) {
					for (int i = 0; i < lengthValueList.size(); i++) {
						possibleLength.add(lengthValueList.get(i));
						if (Long.parseLong(lengthValueList.get(i).toString()) == 150 ) {
							break;
						}
						if (i != lengthValueList.size() -1) {
							possibleLength.add(Randomness.nextLong(Long.parseLong(lengthValueList.get(i).toString()),Long.parseLong(lengthValueList.get(i+1).toString())));
						}
					}
				}
				for (Object o : possibleLength) {
					if (Long.parseLong(o.toString()) >= 0 && Long.parseLong(o.toString()) <= 150){
						paraPossibleValues.add(new RgxGen("[\\s\\S]{" + o.toString() + "}").generate().replaceAll("\n"," ").replaceAll("\r"," "));
					}
				}

			} else if (AAPI.isNumberType(type) || type == BigDecimal.class) {
				// for number:
				//filter(Pair::isNumberType)
				List<Object> sortValueList = paraConstraints.stream().map(Pair::getValue).sorted().collect(Collectors.toList());
				if (type == int.class || type == Integer.class) {
					sortValueList.add(0, Integer.MIN_VALUE);
					sortValueList.add(Integer.MAX_VALUE);
				} else if (type == short.class || type == Short.class) {
					sortValueList.add(0, Short.MIN_VALUE);
					sortValueList.add(Short.MAX_VALUE);
				} else if (type == long.class || type == Long.class || type == BigDecimal.class ) {
					sortValueList.add(0, Long.MIN_VALUE);
					sortValueList.add(Long.MAX_VALUE);
				}


				for (int i = 0; i < sortValueList.size(); i++) {
					paraPossibleValues.add(sortValueList.get(i));
					if (i != sortValueList.size() -1) {
						if (sortValueList.get(i).toString().contains(".") || sortValueList.get(i+1).toString().contains(".")) {
							paraPossibleValues.add(Randomness.nextDouble(Double.parseDouble(sortValueList.get(i).toString()),Double.parseDouble(sortValueList.get(i+1).toString())));
						} else {
							paraPossibleValues.add(Randomness.nextLong(Long.parseLong(sortValueList.get(i).toString()),Long.parseLong(sortValueList.get(i+1).toString())));
						}
					}
				}
			} else if (AAPI.isBooleanType(type)){
				paraPossibleValues.add("true");
				paraPossibleValues.add("false");
			} else if (AAPI.isSet(type)) {
				List<Integer> list = new ArrayList<>();
				for (Pair<String, Object> paraConstraint : paraConstraints) {
					Object value = paraConstraint.getValue();
					if (Integer.parseInt(value.toString()) > Config.REF_ARRAY_MIN_SIZE &&
							Integer.parseInt(value.toString()) < Config.REF_ARRAY_MAX_SIZE)
						list.add(Integer.parseInt(value.toString()));
				}
				list.add(Config.REF_ARRAY_MIN_SIZE);
				list.add(Config.REF_ARRAY_MAX_SIZE);
				list = list.stream().distinct().sorted().collect(Collectors.toList());


				for (int i = 0; i < list.size(); i++) {
					paraPossibleValues.add(list.get(i));
					if (i != list.size() -1) {
						paraPossibleValues.add(Randomness.nextLong(Long.parseLong(list.get(i).toString()),Long.parseLong(list.get(i+1).toString())));
					}
				}

			}
		} catch (Exception | Error e) {
			System.out.println("Exception in getParaPossibleValues");
		}



		return paraPossibleValues;
	}

	private static Set<Class<?>> allClasses = null;

	public static Set<Class<?>> getAllClassesJVMLoaded() {
		return allClasses;
	}

	// unnecessary
	public static Set<Class<?>> getAllClassesJVMLoaded2() {
		if (allClasses == null) {
			allClasses = new HashSet<Class<?>>();
//			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			// APPClassLoader && ExtClassloader
//			try {
//				listClassLoaderClasses(classLoader);
//				listClassLoaderClasses(Object.class.getClassLoader());
//			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
//				e.printStackTrace();
//			}
//			String str = System.getProperty("sun.boot.class.path");

			// rt basic class
			for (String className : RtJar.RT_CLASSES) {
				allClasses.add(getJavaClassForName(className));
			}
		}
		return allClasses;
	}

	// unnecessary
	protected static void listClassLoaderClasses(ClassLoader classLoader)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Log.e("listClassLoaderClasses  ", classLoader);
		if (classLoader == null) {
			return;
		}
		if (allClasses == null) {
			return;
		}
		Class<?> cl_class = classLoader.getClass();
		while (cl_class != java.lang.ClassLoader.class) {
			cl_class = cl_class.getSuperclass();
		}
		java.lang.reflect.Field classLoader_classes_field = cl_class.getDeclaredField("classes");
		boolean accessible = classLoader_classes_field.isAccessible();
		classLoader_classes_field.setAccessible(true);
		@SuppressWarnings("unchecked")
		Vector<Class<?>> classes = (Vector<Class<?>>) classLoader_classes_field.get(classLoader);
		classLoader_classes_field.setAccessible(accessible);
		allClasses.addAll(classes);
		listClassLoaderClasses(classLoader.getParent());

	}

	public static void setOnProgressListener(OnProgressListener onProgressListener) {
		API.onProgressListener = onProgressListener;
	}

	public static boolean isCancelled() {
		return isCancelled;
	}

	public static void setCancelled(boolean isCancelled) {
		API.isCancelled = isCancelled;
	}
}