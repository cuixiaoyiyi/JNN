package cn.ios.junit.stmt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.ios.junit.value.VarableNameFactory;
import cn.ios.junit.value.expr.JUInvokeExpr;

public abstract class JUStmt {
	
	protected List<JUStmt> previouStmts = new ArrayList<JUStmt>();
	protected List<JUStmt> succStmts = new ArrayList<JUStmt>();
	
	protected JUStmt nextStmt = null;
	
	protected VarableNameFactory nameFactory = null;

	public List<JUStmt> getPreviouStmts() {
		return previouStmts;
	}
	
	public List<JUStmt> getSuccStmts() {
		return succStmts;
	}

	public JUStmt getNextStmt() {
		return nextStmt;
	}

	public VarableNameFactory getNameFactory() {
		return nameFactory;
	}
	
	public Set<Class<?>> getExceptions(){
		return null;
	}
	
	public abstract boolean containsJUInvokeExpr();
	
	public abstract JUInvokeExpr getJUInvokeExpr();
	
}
