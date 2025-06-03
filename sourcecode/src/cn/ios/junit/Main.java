package cn.ios.junit;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import cn.ios.ju.base.Log;
import cn.ios.ju.base.Util;
import cn.ios.junit.config.Config;
import cn.ios.junit.output.ErrorWriter;

public class Main {

	public static void main(String[] args) {

		Log.i("=========args begin=========");
		for (String str : args) {
			Log.i(str);
		}
		Method
		Log.i("=========args end=========");
		org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
		/***
		 * level : project | package | class | method | key
		 */
		Option levelOption = new Option("l", "level", true, "");
		options.addOption(levelOption);
		/***
		 * dependency : bin | jar
		 */
		Option dependency = new Option("d", "dependency", true, "");
		dependency.setRequired(false);
		options.addOption(dependency);
		/***
		 * rt.jar
		 */
		Option rtJarOption = new Option("r", "rtJar", true, "");
		options.addOption(rtJarOption);

		Option tOption = new Option("t", "target", true, "");
		options.addOption(tOption);

		Option packageOption = new Option("p", "packageName", true, "");
		packageOption.setRequired(false);
		options.addOption(packageOption);

		Option classOption = new Option("c", "classSignature", true, "");
		classOption.setRequired(false);
		options.addOption(classOption);

		Option methodOption = new Option("m", "methodSignature", true, "");
		methodOption.setRequired(false);
		options.addOption(methodOption);

		Option keyOption = new Option("k", "keyword", true, "");
		keyOption.setRequired(false);
		options.addOption(keyOption);

		Option numberOption = new Option("n", "number", true, "");
		numberOption.setRequired(false);
		options.addOption(numberOption);

		Option srcOption = new Option("src", "src", true, "");
		srcOption.setRequired(false);
		options.addOption(srcOption);

		CommandLineParser parser = new DefaultParser();
		CommandLine commandLine = null;
		try {
			commandLine = parser.parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (commandLine == null) {
			Log.e("commandLine == null");
			return;
		}
		int n = 1;
		if (commandLine.getOptionValue("n") != null && commandLine.getOptionValue("n").length() > 0) {
			try {
				n = Config.MAX_UNIT_METHOD = Integer.parseInt(commandLine.getOptionValue("n"));
			} catch (NumberFormatException e) {

			}
		}

		API.config(commandLine.getOptionValue("r"), commandLine.getOptionValue("t"), null,commandLine.getOptionValue("d"),
				n);

		String level = commandLine.getOptionValue("l");
		switch (level) {
		case "project":
			project();
			break;
		case "package":
			String packageT = commandLine.getOptionValue("p");
			packageT(packageT);
			break;
		case "class":
			String clazz = commandLine.getOptionValue("c");
			clazz(clazz);
			break;
		case "method":
			String method = commandLine.getOptionValue("m");
			method(method);
			break;
		case "key":
			String key = commandLine.getOptionValue("k");
			key(key);
			break;
		default:
			Log.e("level == " + level);
			return;
		}
		ErrorWriter.write();
		Log.e("## Finish All##");

	}

	private static void project() {
		API.generateClasses(Config.projectClasses,0);
	}

	private static void packageT(String packageT) {
		Set<String> classes = getSootClassesByPackage(packageT);
		API.generateClasses(classes);
	}

	private static Set<String> getSootClassesByPackage(String packageT) {
		Set<String> classes = new HashSet<String>();
		for (Class<?> clazz : Config.projectClasses) {
			if (clazz.getPackage().getName().equals(packageT) && !Util.isIgnoredClass(clazz)) {
				classes.add(clazz.getName());
			}
		}
		return classes;
	}

	private static void clazz(String clazzSignature) {

		API.generateClass(API.getJavaClassWithLoaderClass(clazzSignature));
	}

	static void method(String methodSignature) {
		Method method = null;
		
		API.generateMethod(method);
	}

	private static void key(String key) {
//		API.generateClasses(API.getClassesByKey(key));
	}

}
