package cn.ios.junit.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Executable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ErrorWriter {

	public static HashMap<String, HashMap<Class<?>, Set<Executable>>> errorHashMap = new HashMap<String, HashMap<Class<?>, Set<Executable>>>();

	private static Set<Executable> reportSet = new HashSet<Executable>();

	public static void addMethod(Executable method) {
		reportSet.add(method);
	}

	public static void clear() {
		errorHashMap.clear();
		reportSet.clear();
	}

	public static void write() {
//		Calendar calendar = new Calendar.Builder().setInstant(System.currentTimeMillis()).build();
//		calendar.get
		File file = new File("./Error.txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		PrintWriter printWriter = null;
		try {
			printWriter = new PrintWriter(file);
			String result = getResultInfo(null);
			printWriter.write(result);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (printWriter != null) {
				printWriter.close();
			}
		}
	}

	public static String getResultInfo(Collection<String> projectClasses) {
		StringBuffer sb = new StringBuffer();
		Set<Class<?>> set = new HashSet<>();
		for (Executable executable : reportSet) {
			set.add(executable.getDeclaringClass());
		}
		sb.append("==============Success Classes==============\n");
		sb.append(set.size() + "\n");

		if (projectClasses != null) {
			sb.append("==============Unprocessed Classes==========\n");
			

			int unprocessedSize = 0;
			String unprocessedClassInfo = "";
			for (String className : projectClasses) {
				boolean flag = false;
				for (Class<?> clazz : set) {
					if (clazz.getName().equals(className)) {
						flag = true;
						break;
					}
				}
				if (!flag) {
					unprocessedSize++;
					unprocessedClassInfo += (className + "\n");
				}
			}
			sb.append(unprocessedSize + "\n");
			if (unprocessedSize > 0) {
				sb.append(
						"These unprocessed classes may be inner classes or private classes or do not contain any public methods(Constructors are ignored). \n");
			}
			sb.append(unprocessedClassInfo);
		}
		sb.append("==============Success Methods==============\n");
		sb.append(reportSet.size() + "\n");
		sb.append("==============Error Methods================\n");

		String str = "";
		int i = 0;
		for (String exceptionType : errorHashMap.keySet()) {
			str += "---------------" + exceptionType + "-----------------------\n";

			String exceptionString = "";
			int j = 0;
			HashMap<Class<?>, Set<Executable>> value = errorHashMap.get(exceptionType);
			for (Class<?> clazz : value.keySet()) {
				exceptionString += "    " + clazz + "\n";
				for (Executable method : value.get(clazz)) {
					exceptionString += "        " + method + "\n";
					i++;
					j++;
				}
			}
			str += j + "\n";
			str += exceptionString;
		}
		sb.append(i + "\n");
		sb.append(str + "\n");
		sb.append("=======================================");
		return sb.toString();
	}

	public static void addError(Throwable throwable, Executable method) {
		String exceptionType = throwable.getClass().getName();
		HashMap<Class<?>, Set<Executable>> value = null;
		if (errorHashMap.containsKey(exceptionType)) {
			value = errorHashMap.get(exceptionType);
		} else {
			value = new HashMap<Class<?>, Set<Executable>>();
			errorHashMap.put(exceptionType, value);
		}
		Class<?> clazz = method.getDeclaringClass();
		Set<Executable> methods = null;
		if (value.containsKey(clazz)) {
			methods = value.get(clazz);
		} else {
			methods = new HashSet<Executable>();
			value.put(clazz, methods);
		}
		methods.add(method);
	}
}
