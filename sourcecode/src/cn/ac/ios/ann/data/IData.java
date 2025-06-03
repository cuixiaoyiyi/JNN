package cn.ac.ios.ann.data;

import java.util.List;

import cn.ios.ju.base.Pair;

public interface IData {
	
	public List<?> getAvailableObjects(List<Pair<String, Object>> constraints);
	
	public List<?> getAvailableObjects();
	
	public boolean hasConstraint();
	
}
