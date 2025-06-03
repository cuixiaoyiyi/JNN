package cn.ios.junit.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import cn.ios.junit.config.Config;
import cn.ios.junit.generator.JUnitClass;
import cn.ios.junit.generator.JUnitClassReplica;

public class JUnitWriter {

	public static final String separator = File.separator;

	public static final Set<String> srcClasses = new HashSet<String>();

	public static void write(JUnitClass class1) {
		String TEST_SOURCE_FOLDER = Config.getOutputFolder();
		String packageName = class1.getPackageName();
		if (packageName == null) {
			packageName = "";
		}
//		File folder = new File((TEST_SOURCE_FOLDER + packageName).replace(".", separator));
		File folder = new File(TEST_SOURCE_FOLDER + packageName.replace(".", separator));
		if (!folder.exists()) {
			folder.mkdirs();
		}
		File file = new File(folder, class1.getName() + ".java");

		try {
			file.createNewFile();
			PrintWriter printWriter = new PrintWriter(file);
			printWriter.write(class1.toString());
			printWriter.close();
			srcClasses.add(file.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void write(JUnitClassReplica class1) {

		String TEST_SOURCE_FOLDER = Config.getOutputFolder();
		
		String packageName = class1.getPackageName();
		if (packageName == null) {
			packageName = "";
		}
		File folder = new File(TEST_SOURCE_FOLDER + packageName.replace(".", separator));
		if (!folder.exists()) {
			folder.mkdirs();
		}
		File file = new File(folder, class1.getName() + ".java");

		try {
			file.createNewFile();
			PrintWriter printWriter = new PrintWriter(file);
			printWriter.write(class1.toString());
			printWriter.close();
			srcClasses.add(file.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
