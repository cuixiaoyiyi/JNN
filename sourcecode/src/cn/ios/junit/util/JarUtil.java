package cn.ios.junit.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import cn.ios.ju.base.Log;
import cn.ios.junit.gui.JUAFrame;

import java.util.jar.Attributes.Name;

public class JarUtil {

	public static void loadJars(Collection<String> jars) {
		for (String jar : jars) {
			if (jar == null || jar == "") {
				continue;
			}
			processJar(jar);
		}
	}

	private static void loadClasses(URL targetUrl) {
		boolean isLoader = false;
//		for (URL url : classLoader.getURLs()) {
//			if (url.equals(targetUrl)) {
//				isLoader = true;
//				break;
//			}
//		}
		if (!isLoader) {
			Log.i("load ==> " + targetUrl);
			if (JUAFrame.textArea != null) {
				JUAFrame.textArea.append("load ==> " + targetUrl + "\n");
				JUAFrame.textArea.paintImmediately(JUAFrame.textArea.getBounds());
			}
			JarClassLoader.getInstance().addURL(targetUrl);
		} else {
			Log.i("load ==> " + targetUrl + " is already loaded");
		}
	}

	public static void processJar(String jarFilePath) {
		File file = new File(jarFilePath);
		if (!file.exists()) {
			Log.e("file does not exist!! ", file.getAbsolutePath());
			return;
		}
		if (file.isDirectory()) {
			try {
				loadClasses(file.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			return;
		}

		if (!(jarFilePath.endsWith(".jar") || jarFilePath.endsWith(".JAR") || jarFilePath.endsWith(".Jar"))) {
			return;
		}
		// .jar file
		java.util.jar.JarFile jarFile = null;
		try {
			jarFile = new JarFile(file);
			String jarClassPath = null;
			String libPath = null;
			for (Entry<Object, Object> entry : jarFile.getManifest().getMainAttributes().entrySet()) {
				if (entry.getKey() instanceof Attributes.Name) {
					Attributes.Name name = (Name) entry.getKey();
					if (name.toString().equals("Spring-Boot-Classes")) {
						jarClassPath = entry.getValue().toString();
						Log.i("getBytecodeRelativePathUnderSpringFramework jarClassPath = ", jarClassPath);
					} else if (name.toString().equals("Spring-Boot-Lib")) {
						libPath = entry.getValue().toString();
						Log.i("getBytecodeRelativePathUnderSpringFramework libPath = ", libPath);
					}
				}
			}
			if (jarClassPath != null && jarClassPath != "") {
				unzip(jarFilePath, jarFilePath + "unzip" + File.separator, jarClassPath, libPath);
			} else {
				try {
					loadClasses(file.toURI().toURL());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			Log.e(e);
		} finally {
			if (jarFile != null) {
				try {
					jarFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void unzip(String jarFile, String targetUnzipFilePath, String classes, String lib) {
		// 0. ready to unzip
		File rootDir = new File(targetUnzipFilePath);
		if (!rootDir.exists()) {
			rootDir.mkdirs();
		}

		// 1. unzip jar file
		try {
			UnZipFile.unZipFiles(jarFile, targetUnzipFilePath);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		// 2. collect all libs and classes
		File springBootDir = new File(targetUnzipFilePath + File.separator + classes);
		File libPath = new File(targetUnzipFilePath + File.separator + lib);

		List<URL> urls = new ArrayList<URL>();
		try {
			urls.add(rootDir.toURI().toURL());
			urls.add(springBootDir.toURI().toURL());
			if (libPath != null && libPath.isDirectory()) {
				String[] libs = libPath.list();

				for (int i = 0; i < libs.length; i++) {
					urls.add(new File(libs[i]).toURI().toURL());
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		String[] targetPaths = new String[urls.size()];
		for (int i = 0; i < urls.size(); i++) {
			targetPaths[i] = urls.get(i).getPath();
		}
		// 3. load all classes
		loadJars(Arrays.asList(targetPaths));
	}
}
