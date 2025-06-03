package cn.ios.junit.value.expr;

import java.lang.reflect.Executable;
import java.util.Set;

import cn.ios.junit.value.Dependable;

public interface JUInvokeExpr extends JUExpr, Dependable{
	
	public Executable getMethod();
	
	public int getArgCount();

	public Object getCaller();
	
	public Set<Class<?>> getExceptions();
	
}
