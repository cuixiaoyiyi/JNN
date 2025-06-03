package cn.ios.junit.util;

import java.net.URL;
import java.net.URLClassLoader;

import cn.ios.ju.base.Log;

public class JarClassLoader extends URLClassLoader {

	private static JarClassLoader sInstance = null;

	private JarClassLoader(URL[] urls) {
		super(urls);
	}

	public void addURL(URL url) {
		super.addURL(url);
	}
	
	public static void clear() {
		sInstance = null;
	}

	public static JarClassLoader getInstance() {
		if(sInstance == null ) {
			sInstance = new JarClassLoader(new URL[] {});
		}
		return sInstance;
	}

}
