package cn.ac.ios.ann.data;

import java.util.List;

import cn.ios.ju.base.Pair;

public abstract class AbstractData implements IData{

	protected List<Pair<String, Object>> constraints = null;

	protected AbstractData(List<Pair<String, Object>> constraints) {
		this.constraints = constraints;
	}
	
	@Override
	public boolean hasConstraint() {
		return constraints != null && !constraints.isEmpty();
	}
	
	@Override
	public List<?> getAvailableObjects() {
		return getAvailableObjects(constraints);
	}
}
